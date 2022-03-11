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
import com.myctca.model.LabResult;
import com.myctca.network.GetClient;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.myctca.util.GeneralUtil.noNull;

public class LabResultsService implements GetListener {
    private static final String TAG = LabResultsService.class.getSimpleName();
    private static LabResultsService labResultsService;
    private LabResultsServiceListener getListener;
    private Context context;

    public static LabResultsService getInstance() {
        if (labResultsService == null) {
            return new LabResultsService();
        }
        return labResultsService;
    }

    public void getLabResults(Context context, String purpose, LabResultsServiceListener listener) {
        this.getListener = listener;
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_lab_results);
        this.context = context;
        if (AppSessionManager.getInstance().getLabResults().isEmpty()) {
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        } else {
            listener.notifyFetchSuccess(AppSessionManager.getInstance().getLabResults());
        }
    }

    public void clearLabResults() {
        AppSessionManager.getInstance().getLabResults().clear();
    }

    public boolean isLabResultLessThan24HoursAgo(Date labResultDate) {
        Calendar perfDate = Calendar.getInstance();
        perfDate.setTime(labResultDate);

        Calendar twentyFourAgo = Calendar.getInstance();
        twentyFourAgo.add(Calendar.DATE, -1);

        long perfDateMillis = perfDate.getTimeInMillis();
        long twentyFourAgoMillis = twentyFourAgo.getTimeInMillis();

        return perfDateMillis < twentyFourAgoMillis;
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            LabResult[] labResults = new Gson().fromJson(parseSuccess, LabResult[].class);
            AppSessionManager.getInstance().setLabResults(new LinkedList<>(Arrays.asList(labResults)));
            Log.d(TAG, "Lab Results retrieved: " + AppSessionManager.getInstance().getLabResults().size());
            getListener.notifyFetchSuccess(new LinkedList<>(Arrays.asList(labResults)));
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception:" + exception);
            getListener.notifyFetchError(context.getString(R.string.error_400), purpose);
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_lab_results);
        CTCAAnalyticsManager.createEvent("LabsResultsService:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, null, url);

        Log.d("Error.Response", noNull(error.getMessage()));
        getListener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    public interface LabResultsServiceListener {
        void notifyFetchSuccess(List<LabResult> labResults);

        void notifyFetchError(String error, String purpose);
    }
}
