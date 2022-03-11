package com.myctca.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.interfaces.PostListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MyCTCATask;
import com.myctca.model.RoiDetails;
import com.myctca.network.GetClient;
import com.myctca.network.PostClient;

import java.util.Map;

public class MoreFormsLibraryService implements GetListener, PostListener {

    private static final String ANNC_PURPOSE = "ANNC_FORM_EXISTS";
    private static final String TAG = MoreFormsLibraryService.class.getSimpleName();
    private static final String PURPOSE_MRN = "MRN";
    private static final String ROIDETAILS = "ROI DETAILS";
    private static MoreFormsLibraryService formsLibraryService;
    private MoreFormsLibraryANNCListenerGet listenerANNCGet;
    private MoreFormsLibraryROIListenerGet listenerROIGet;
    private MoreFormsLibraryListenerPost listenerPost;
    private Context context;

    public static MoreFormsLibraryService getInstance() {
        if (formsLibraryService == null) {
            return new MoreFormsLibraryService();
        }
        return formsLibraryService;
    }

    public void fetchANNCData(MoreFormsLibraryANNCListenerGet listener, String url, Context context, String purpose) {
        this.listenerANNCGet = listener;
        this.context = context;
        switch (purpose) {
            case ANNC_PURPOSE:
                if (!AppSessionManager.getInstance().getAnncFormExistsCheck()) {
                    GetClient getClient = new GetClient(this, context);
                    getClient.fetch(url, null, purpose);
                } else {
                    listener.ifANNCExists(AppSessionManager.getInstance().getIfAnncFormExists());
                }
                break;
            case PURPOSE_MRN:
                if (TextUtils.isEmpty(AppSessionManager.getInstance().getMrn())) {
                    GetClient getClient = new GetClient(this, context);
                    getClient.fetch(url, null, purpose);
                } else {
                    listener.notifyMrn(AppSessionManager.getInstance().getMrn());
                }
                break;
        }
    }

    public void fetchROIData(MoreFormsLibraryROIListenerGet listener, String url, Map<String, String> params, Context context, String purpose) {
        this.listenerROIGet = listener;
        this.context = context;
        switch (purpose) {
            case ROIDETAILS:
                if (AppSessionManager.getInstance().getRoiDetails() == null) {
                    GetClient getClient = new GetClient(this, context);
                    getClient.fetch(url, params, purpose);
                } else {
                    listenerROIGet.notifyFetchDetails(AppSessionManager.getInstance().getRoiDetails());
                }
                break;
        }
    }

    @Override
    public void notifyFetchSuccess(String response, String purpose) {
        Log.d(TAG, "response:" + response);
        switch (purpose) {
            case ANNC_PURPOSE:
                AppSessionManager.getInstance().setAnncFormExistsCheck(true);
                listenerANNCGet.ifANNCExists(response.equals("true"));
                AppSessionManager.getInstance().setIfAnncFormExists(response.equals("true"));
                break;
            case PURPOSE_MRN:
                response = response.substring(1, response.length() - 1);
                AppSessionManager.getInstance().setMrn(response);
                listenerANNCGet.notifyMrn(response);
                break;
            case ROIDETAILS:
                try {
                    RoiDetails roiDetails = new Gson().fromJson(response, RoiDetails.class);
                    AppSessionManager.getInstance().setRoiDetails(roiDetails);
                    listenerROIGet.notifyFetchDetails(roiDetails);
                } catch (JsonParseException e) {
                    listenerROIGet.notifyFetchError(context.getString(R.string.error_400));
                }
                break;
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        String url = "";
        Log.d(TAG, "error:" + error);
        switch (purpose) {
            case ANNC_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_annc_form_exists);
                Log.d(TAG, "error:" + error);
                listenerANNCGet.notifyFetchError(VolleyErrorHandler.handleError(error, context));
                break;
            case PURPOSE_MRN:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_mrn);
                listenerANNCGet.notifyFetchError(VolleyErrorHandler.handleError(error, context));
                break;
            case ROIDETAILS:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_roi_form_info);
                listenerROIGet.notifyFetchError(VolleyErrorHandler.handleError(error, context));
                break;
        }
        CTCAAnalyticsManager.createEvent("MoreFormsLibraryService:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, null, url);
    }

    public void submitForm(int formType, String url, String body, Context context, MoreFormsLibraryListenerPost listenerPost) {
        this.context = context;
        this.listenerPost = listenerPost;
        PostClient postClient = new PostClient(this, context, formType);
        postClient.sendData(url, body, null);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        Log.d(TAG, "notifyMorePostClientSuccess SUCCESS");
        AppSessionManager.getInstance().setAnncFormExistsCheck(false);
        listenerPost.notifyPostSuccess();
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        if ((task) == MyCTCATask.RELEASE_OF_INFORMATION) {
            String url = BuildConfig.myctca_server + context.getString(R.string.myctca_post_roi);
            CTCAAnalyticsManager.createEvent("MoreFormsLibraryActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        } else {
            String url = BuildConfig.myctca_server + context.getString(R.string.myctca_annc_form_submission);
            CTCAAnalyticsManager.createEvent("MoreFormsLibraryActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        }
        Log.d(TAG, "notifyMorePostClientSuccess FAILURE: " + error);
        if (message.isEmpty())
            message = context.getString(R.string.error_400);
        listenerPost.notifyPostError(message);
    }

    public interface MoreFormsLibraryANNCListenerGet {
        void ifANNCExists(boolean anncExists);

        void notifyFetchError(String errMessage);

        void notifyMrn(String response);
    }

    public interface MoreFormsLibraryListenerPost {

        void notifyPostError(String message);

        void notifyPostSuccess();
    }

    public interface MoreFormsLibraryROIListenerGet {
        void notifyFetchError(String errMessage);

        void notifyFetchDetails(RoiDetails roiDetails);
    }
}
