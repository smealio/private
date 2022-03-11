package com.myctca.service;

import android.content.Context;
import android.util.Base64;
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
import com.myctca.interfaces.PostPdfClientListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.CarePlan;
import com.myctca.model.ClinicalSummary;
import com.myctca.model.ClinicalSummaryDetail;
import com.myctca.model.ImagingDoc;
import com.myctca.model.MedDoc;
import com.myctca.model.MedDocType;
import com.myctca.model.MyCTCATask;
import com.myctca.network.GetClient;
import com.myctca.network.PostClient;
import com.myctca.network.PostPDFClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.myctca.util.GeneralUtil.noNull;

public class MoreMedicalDocumentsService implements GetListener, PostListener, PostPdfClientListener {

    private static final String TAG = MoreMedicalDocumentsService.class.getSimpleName();
    private static final String CLINICAL_SUMMARY_PURPOSE = "CLINICAL_SUMMARY";
    private static final String CARE_PLAN_PURPOSE = "CARE_PLAN";
    private static final String CLINICAL_PURPOSE = MedDocType.CLINICAL;
    private static final String RADIATION_PURPOSE = MedDocType.RADIATION;
    private static final String INTEGRATIVE_PURPOSE = MedDocType.INTEGRATIVE;
    private static final String IMAGING_PURPOSE = "IMAGING";
    private static MoreMedicalDocumentsService clinicalSummaryService;
    private Context context;
    private MoreMedDocListenerGet listenerGet;
    private MoreMedDocClinicalSummaryListenerPost listenerPost;
    private MoreMedDocClinicalSummaryListenerPdf listenerPdf;

    public static MoreMedicalDocumentsService getInstance() {
        if (clinicalSummaryService == null) {
            return new MoreMedicalDocumentsService();
        }
        return clinicalSummaryService;
    }

    public void getMedicalDocumentsData(Context context, MoreMedDocListenerGet listener, String purpose, String url, Map<String, String> params) {
        this.context = context;
        this.listenerGet = listener;
        switch (purpose) {
            case CLINICAL_SUMMARY_PURPOSE:
                if (!AppSessionManager.getInstance().getmClinicalSummaries().isEmpty()) {
                    listener.fetchMedicalDocSuccess(AppSessionManager.getInstance().getmClinicalSummaries());
                }
                break;
            case RADIATION_PURPOSE:
                if (getMedDocSize(RADIATION_PURPOSE) > 0) {
                    listener.fetchMedicalDocSuccess(AppSessionManager.getInstance().getmRadiationDocs());
                }
                break;
            case CLINICAL_PURPOSE:
                if (getMedDocSize(CLINICAL_PURPOSE) > 0) {
                    listener.fetchMedicalDocSuccess(AppSessionManager.getInstance().getmClinicalDocs());
                }
                break;
            case INTEGRATIVE_PURPOSE:
                if (getMedDocSize(INTEGRATIVE_PURPOSE) > 0) {
                    listener.fetchMedicalDocSuccess(AppSessionManager.getInstance().getmIntegrativeDocs());
                }
                break;
            case IMAGING_PURPOSE:
                if (!AppSessionManager.getInstance().getmImagingDocs().isEmpty()) {
                    listener.fetchMedicalDocSuccess(AppSessionManager.getInstance().getmImagingDocs());
                }
        }
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, params, purpose);
    }

    private int getMedDocSize(String medDocType) {
        if (medDocType.equals(CLINICAL_PURPOSE)) {
            return AppSessionManager.getInstance().getmClinicalDocs().size();
        }
        if (medDocType.equals(RADIATION_PURPOSE)) {
            return AppSessionManager.getInstance().getmRadiationDocs().size();
        }
        if (medDocType.equals(INTEGRATIVE_PURPOSE)) {
            return AppSessionManager.getInstance().getmIntegrativeDocs().size();
        }
        return 0;
    }

    void clearDocsArray(String medDocType) {
        switch (medDocType) {
            case CLINICAL_PURPOSE:
                AppSessionManager.getInstance().clearClinicalDocs();
                break;
            case RADIATION_PURPOSE:
                AppSessionManager.getInstance().clearRadiationDocs();
                break;
            case INTEGRATIVE_PURPOSE:
                AppSessionManager.getInstance().clearIntegrativeDocs();
                break;
            case IMAGING_PURPOSE:
                AppSessionManager.getInstance().clearImagingDocs();
                break;
        }
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        switch (purpose) {
            case CLINICAL_SUMMARY_PURPOSE:
                try {
                    ClinicalSummary[] clinicalSummaries = new Gson().fromJson(parseSuccess, ClinicalSummary[].class);
                    List<ClinicalSummary> clinicalSummariesList = new LinkedList<>(Arrays.asList(clinicalSummaries));
                    sortClinicalSummary(clinicalSummariesList);
                    AppSessionManager.getInstance().setmClinicalSummaries(clinicalSummariesList);
                    listenerGet.fetchMedicalDocSuccess(AppSessionManager.getInstance().getmClinicalSummaries());
                } catch (JsonParseException e) {
                    listenerGet.notifyFetchError(context.getString(R.string.error_400));
                }
                break;
            case CARE_PLAN_PURPOSE:
                CarePlan carePlan;
                try {
                    carePlan = new Gson().fromJson(parseSuccess, CarePlan.class);
                    String documentText = carePlan.getDocumentText();
                    listenerGet.fetchCarePlan(documentText);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception: " + exception);
                    if (parseSuccess.length() > 2)
                        listenerGet.fetchCarePlan("");
                    else listenerGet.notifyFetchError(context.getString(R.string.error_400));
                }
                break;
            case RADIATION_PURPOSE:
            case INTEGRATIVE_PURPOSE:
            case CLINICAL_PURPOSE:
                try {
                    MedDoc[] medDocs = new Gson().fromJson(parseSuccess, MedDoc[].class);
                    List<MedDoc> medDocList = Arrays.asList(medDocs);
                    sortMedDocs(medDocList);
                    addToDocArray(medDocList, purpose);
                    listenerGet.fetchMedicalDocSuccess(getMedDocs(purpose));
                } catch (JsonParseException e) {
                    listenerGet.notifyFetchError(context.getString(R.string.error_400));
                }
                break;
            case IMAGING_PURPOSE:
                try {
                    ImagingDoc[] imagingDocs = new Gson().fromJson(parseSuccess, ImagingDoc[].class);
                    List<ImagingDoc> imagingDocList = new LinkedList<>(Arrays.asList(imagingDocs));
                    sortImagingDocs(imagingDocList);
                    AppSessionManager.getInstance().setmImagingDocs(imagingDocList);
                    listenerGet.fetchMedicalDocSuccess(imagingDocList);
                } catch (JsonParseException e) {
                    listenerGet.notifyFetchError(context.getString(R.string.error_400));
                }
                break;
        }
    }

    List<MedDoc> getMedDocs(String medDocType) {
        if (medDocType.equals(CLINICAL_PURPOSE)) {
            return AppSessionManager.getInstance().getmClinicalDocs();
        }
        if (medDocType.equals(RADIATION_PURPOSE)) {
            return AppSessionManager.getInstance().getmRadiationDocs();
        }
        if (medDocType.equals(INTEGRATIVE_PURPOSE)) {
            return AppSessionManager.getInstance().getmIntegrativeDocs();
        }
        return Collections.emptyList();
    }

    private void sortMedDocs(List<MedDoc> medDocs) {
        Collections.sort(medDocs, (medDoc, t1) -> medDoc.getDocumentAuthoredDate().compareTo(t1.getDocumentAuthoredDate()));
        Collections.reverse(medDocs);
    }

    private void sortImagingDocs(List<ImagingDoc> imagingDocs) {
        Collections.sort(imagingDocs, (imagingDocs1, t1) -> imagingDocs1.getDocumentDate().compareTo(t1.getDocumentDate()));
        Collections.reverse(imagingDocs);
    }

    private void addToDocArray(List<MedDoc> medDocs, String purpose) {

        if (purpose.equals(CLINICAL_PURPOSE)) {
            AppSessionManager.getInstance().setmClinicalDocs(new LinkedList<>(medDocs));
        }
        if (purpose.equals(RADIATION_PURPOSE)) {
            AppSessionManager.getInstance().setmRadiationDocs(new LinkedList<>(medDocs));
        }
        if (purpose.equals(INTEGRATIVE_PURPOSE)) {
            AppSessionManager.getInstance().setmIntegrativeDocs(new LinkedList<>(medDocs));
        }
    }

    private void sortClinicalSummary(List<ClinicalSummary> clinicalSummariesList) {
        Collections.sort(clinicalSummariesList, (clinicalSummary, t1) -> clinicalSummary.getCreationTime().compareTo(t1.getCreationTime()));
        Collections.reverse(clinicalSummariesList);
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.d("Error.Response", noNull(error.getMessage()));
        String url = "";
        switch (purpose) {
            case CLINICAL_SUMMARY_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_clinical_summary);
                break;
            case CARE_PLAN_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_care_plan);
                break;
            case CLINICAL_PURPOSE:
            case RADIATION_PURPOSE:
            case INTEGRATIVE_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_med_docs, purpose);
                break;
            case IMAGING_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_imaging_docs);

        }
        CTCAAnalyticsManager.createEvent("MoreMedDocCarePlanFragment:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        listenerGet.notifyFetchError(VolleyErrorHandler.handleError(error, context));
    }

    public void clearClinicalSummaries() {
        AppSessionManager.getInstance().getmClinicalSummaries().clear();
    }

    public void postClinicalSummaryData(Context context, MoreMedDocClinicalSummaryListenerPost listenerPost, String url, int purpose, String body, Map<String, String> params) {
        this.context = context;
        this.listenerPost = listenerPost;
        PostClient postClient = new PostClient(this, context, purpose);
        postClient.sendData(url, body, params);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        if (task == MyCTCATask.CLINICAL_SUMMARY_DETAIL) {
            try {
                ClinicalSummaryDetail clinicalSummaryDetail = new Gson().fromJson(response, ClinicalSummaryDetail.class);
                byte[] data = Base64.decode(clinicalSummaryDetail.getContent(), Base64.DEFAULT);
                String text = new String(data, StandardCharsets.UTF_8);
                listenerPost.notifyPostSuccess(text);
            } catch (JsonParseException e) {
                listenerPost.notifyPostError(context.getString(R.string.error_400));
            }
        } else {
            listenerPost.notifyPostSuccess(response);
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_clinical_summary_detail);
        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryDetailFragment:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        Log.d("Error.Response", noNull(error.getMessage()));
        if (message.isEmpty())
            message = context.getString(R.string.error_400);
        listenerPost.notifyPostError(message);
    }

    public void downloadClinicalSummary(MoreMedDocClinicalSummaryListenerPdf listenerPdf, Context context, String body) {
        this.context = context;
        this.listenerPdf = listenerPdf;
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_clinical_summary_download);
        PostPDFClient postPDFClient = new PostPDFClient(this, context, MyCTCATask.DOWNLOAD_CLINICAL_SUMMARY);
        postPDFClient.downloadPdf(url, body);
    }

    @Override
    public void notifyPostPdfSuccess(byte[] response, int task) {
        listenerPdf.notifyPostPdfSuccess(response);
    }

    @Override
    public void notifyPostPdfError(VolleyError error, int task) {
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_clinical_summary_download);
        CTCAAnalyticsManager.createEvent("MoreMedDocService:notifyPostPdfError", CTCAAnalyticsConstants.ALERT_CLINICAL_SUMMARIES_DOWNLOAD_FAIL, error, url);
        Log.d(TAG, "Error: " + error);
        listenerPdf.notifyPostPdfError();
    }

    public List<ImagingDoc> getImagingDocs() {
        return AppSessionManager.getInstance().getmImagingDocs();
    }

    public interface MoreMedDocListenerGet {
        void fetchCarePlan(String document);

        <T> void fetchMedicalDocSuccess(List<T> medicalDocs);

        void notifyFetchError(String errorMessage);
    }

    public interface MoreMedDocClinicalSummaryListenerPost {

        void notifyPostSuccess(String text);

        void notifyPostError(String message);
    }

    public interface MoreMedDocClinicalSummaryListenerPdf {

        void notifyPostPdfSuccess(byte[] response);

        void notifyPostPdfError();
    }
}
