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
import com.myctca.interfaces.PostListener;
import com.myctca.model.Allergy;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.HealthIssue;
import com.myctca.model.Immunization;
import com.myctca.model.MyCTCATask;
import com.myctca.model.Prescription;
import com.myctca.model.VitalsGroup;
import com.myctca.network.GetClient;
import com.myctca.network.PostClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.myctca.util.GeneralUtil.noNull;

public class HealthHistoryService implements GetListener, PostListener {
    private static final String TAG = HealthHistoryService.class.getSimpleName();
    private static final String HEALTH_ISSUE_PURPOSE = "HEALTH_ISSUE";
    private static final String ALLERGIES_PURPOSE = "ALLERGIES";
    private static final String IMMUNIZATION_PURPOSE = "IMMUNIZATION";
    private static final String VITALS_PURPOSE = "VITALS";
    private static final String PRESCRIPTIONS_PURPOSE = "PRESCRIPTIONS";

    private static HealthHistoryService healthHistoryService;
    private HealthHistoryServiceListener listener;
    private HealthHistoryServicePostListener postListener;
    private Context context;

    public static HealthHistoryService getInstance() {
        if (healthHistoryService == null) {
            healthHistoryService = new HealthHistoryService();
        }
        return healthHistoryService;
    }

    public void downloadHealthHistoryTypeList(HealthHistoryServiceListener listener, Context context, String url, String purpose) {
        this.listener = listener;
        this.context = context;
        int flag = 0;
        switch (purpose) {
            case ALLERGIES_PURPOSE:
                if (!AppSessionManager.getInstance().getmAllergies().isEmpty()) {
                    flag = 1;
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmAllergies(), purpose);
                }
                break;
            case HEALTH_ISSUE_PURPOSE:
                if (!AppSessionManager.getInstance().getmHealthIssues().isEmpty()) {
                    flag = 1;
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmHealthIssues(), purpose);
                }
                break;
            case IMMUNIZATION_PURPOSE:
                if (!AppSessionManager.getInstance().getmImmunizations().isEmpty()) {
                    flag = 1;
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmImmunizations(), purpose);
                }
                break;
            case VITALS_PURPOSE:
                if (!AppSessionManager.getInstance().getmVitalsGroups().isEmpty()) {
                    flag = 1;
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmVitalsGroups(), purpose);
                }
                break;
            case PRESCRIPTIONS_PURPOSE:
                if (!AppSessionManager.getInstance().getmPrescriptions().isEmpty()) {
                    flag = 1;
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmPrescriptions(), purpose);
                }
                break;
            default:
                //do nothing
        }
        if (flag == 0) {
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getHealthHistoryType(String purpose) {
        switch (purpose) {
            case ALLERGIES_PURPOSE:
                return (List<T>) AppSessionManager.getInstance().getmAllergies();
            case HEALTH_ISSUE_PURPOSE:
                return (List<T>) AppSessionManager.getInstance().getmHealthIssues();
            case PRESCRIPTIONS_PURPOSE:
                return (List<T>) AppSessionManager.getInstance().getmPrescriptions();
            case IMMUNIZATION_PURPOSE:
                return (List<T>) AppSessionManager.getInstance().getmImmunizations();
            case VITALS_PURPOSE:
                return (List<T>) AppSessionManager.getInstance().getmVitalsGroups();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        switch (purpose) {
            case ALLERGIES_PURPOSE:
                try {
                    Allergy[] allergies = new Gson().fromJson(parseSuccess, Allergy[].class);
                    AppSessionManager.getInstance().setmAllergies(new LinkedList<>(Arrays.asList(allergies)));
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmAllergies(), purpose);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception: " + exception);
                    listener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            case HEALTH_ISSUE_PURPOSE:
                try {
                    HealthIssue[] healthIssues = new Gson().fromJson(parseSuccess, HealthIssue[].class);
                    AppSessionManager.getInstance().setmHealthIssues(new LinkedList<>(Arrays.asList(healthIssues)));
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmHealthIssues(), purpose);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception: " + exception);
                    listener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            case IMMUNIZATION_PURPOSE:
                try {
                    Immunization[] immunizations = new Gson().fromJson(parseSuccess, Immunization[].class);
                    AppSessionManager.getInstance().setmImmunizations(new LinkedList<>(Arrays.asList(immunizations)));
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmImmunizations(), purpose);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception:" + exception);
                    listener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            case VITALS_PURPOSE:
                try {
                    VitalsGroup[] vitalsGroups = new Gson().fromJson(parseSuccess, VitalsGroup[].class);
                    for (VitalsGroup vitalsGroup : vitalsGroups) {
                        vitalsGroup.setFilteredDetails(vitalsGroup.getDetails());
                    }
                    AppSessionManager.getInstance().setmVitalsGroups(new LinkedList<>(Arrays.asList(vitalsGroups)));
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmVitalsGroups(), purpose);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception:" + exception);
                    listener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            case PRESCRIPTIONS_PURPOSE:
                try {
                    Prescription[] prescriptions = new Gson().fromJson(parseSuccess, Prescription[].class);
                    List<Prescription> prescriptionList = Arrays.asList(prescriptions);
                    Collections.sort(prescriptionList, (prescription, t1) -> Integer.parseInt(t1.getPrescriptionId()) - Integer.parseInt(prescription.getPrescriptionId()));
                    AppSessionManager.getInstance().setmPrescriptions(new LinkedList<>(prescriptionList));
                    listener.notifyFetchSuccess(AppSessionManager.getInstance().getmPrescriptions(), purpose);
                    for (Prescription prescription : prescriptions) {
                        if (prescription.getAllowRenewal()) {
                            AppSessionManager.getInstance().setCtcaPrescribed(true);
                            break;
                        }
                    }
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception:" + exception);
                    listener.notifyFetchError(context.getString(R.string.error_400), purpose);
                }
                break;
            default:
                //do nothing
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        String url = "";
        switch (purpose) {
            case ALLERGIES_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_allergies);
                break;
            case HEALTH_ISSUE_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_health_issues);
                break;
            case IMMUNIZATION_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_immunizations);
                break;
            case VITALS_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_vitals);
                break;
            case PRESCRIPTIONS_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_prescriptions);
                break;
            default:
                //do nothing
        }
        CTCAAnalyticsManager.createEvent("HealthHistoryService:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        Log.d("Error.Response", noNull(error.getMessage()));
        listener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    public void clearHealthHistoryType(String purpose) {
        switch (purpose) {
            case ALLERGIES_PURPOSE:
                AppSessionManager.getInstance().getmAllergies().clear();
                break;
            case HEALTH_ISSUE_PURPOSE:
                AppSessionManager.getInstance().getmHealthIssues().clear();
                break;
            case IMMUNIZATION_PURPOSE:
                AppSessionManager.getInstance().getmImmunizations().clear();
                break;
            case VITALS_PURPOSE:
                AppSessionManager.getInstance().getmVitalsGroups().clear();
                break;
            case PRESCRIPTIONS_PURPOSE:
                AppSessionManager.getInstance().getmPrescriptions().clear();
                break;
            default:
                //do nothing
        }
    }

    public void submitRequestRenewalForm(HealthHistoryServicePostListener listener, Context context, String json) {
        this.postListener = listener;
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_prescription_refill_request);
        Log.d(TAG, "submitForm url: " + url);
        PostClient postClient = new PostClient(this, context, MyCTCATask.PRESCRIPTION_REFILL_REQUEST);
        postClient.sendData(url, json, null);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        Log.d(TAG, "notifyMorePostClientSuccess SUCCESS: " + response);
        postListener.notifyPostSuccess();
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        if(message.isEmpty())
            message = context.getString(R.string.error_400);
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_prescription_refill_request);
        CTCAAnalyticsManager.createEvent("PrescriptionRefillActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        Log.d(TAG, "notifyMorePostClientSuccess FAILURE: " + error);
        postListener.notifyPostError(message);
    }

    public interface HealthHistoryServiceListener {
        <T> void notifyFetchSuccess(List<T> list, String purpose);

        void notifyFetchError(String message, String purpose);
    }

    public interface HealthHistoryServicePostListener {
        void notifyPostSuccess();

        void notifyPostError(String message);
    }
}
