package com.myctca.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.FacilityInfoAll;
import com.myctca.model.MedicalCenter;
import com.myctca.network.GetClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.myctca.util.GeneralUtil.noNull;

public class FacilityInfoAllService implements GetListener {
    private static final String TAG = FacilityInfoAllService.class.getSimpleName();
    private static FacilityInfoAllService facilityInfoAllService;
    protected FacilityInfoAllListener listener;
    private Context context;

    public static FacilityInfoAllService getInstance() {
        if (facilityInfoAllService == null) {
            facilityInfoAllService = new FacilityInfoAllService();
        }
        return facilityInfoAllService;
    }

    public void downloadFacilityInfoAll(Context context, FacilityInfoAllListener listener, String purpose) {
        this.listener = listener;
        this.context = context;
        if (AppSessionManager.getInstance().getFacilityInfoAllList().isEmpty()) {
            final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_facility_all);
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        } else
            listener.notifyFetchSuccess(AppSessionManager.getInstance().getFacilityInfoAllList(), AppSessionManager.getInstance().getFacilityAll());
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            FacilityInfoAll[] facilityInfoAlls = new Gson().fromJson(parseSuccess, FacilityInfoAll[].class);
            AppSessionManager.getInstance().setFacilityInfoAllList(new LinkedList<>(Arrays.asList(facilityInfoAlls)));
            List<MedicalCenter> facilityList = new ArrayList<>();
            for (FacilityInfoAll facility : facilityInfoAlls) {
                facilityList.add(new MedicalCenter(facility.name, facility.displayName));
            }
            AppSessionManager.getInstance().setFacilityAll(facilityList);
            listener.notifyFetchSuccess(AppSessionManager.getInstance().getFacilityInfoAllList(), AppSessionManager.getInstance().getFacilityAll());
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception: " + exception);
            listener.notifyFetchError(context.getString(R.string.error_400));
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.d("Error.Response", noNull(error.getMessage()));
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_facility_all);
        CTCAAnalyticsManager.createEvent("MailService:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        listener.notifyFetchError(VolleyErrorHandler.handleError(error, context));
    }

    public interface FacilityInfoAllListener {
        void notifyFetchSuccess(List<FacilityInfoAll> facilityInfoAlls, List<MedicalCenter> facilityAll);

        void notifyFetchError(String message);
    }
}
