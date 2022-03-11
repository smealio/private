package com.myctca.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.activity.NavActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.ClinicalSummary;
import com.myctca.model.ImagingDoc;
import com.myctca.model.ImpersonatedUserProfile;
import com.myctca.model.MedDoc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PowerMockIgnore("jdk.internal.reflect.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class MoreMedicalDocumentsServiceTest {
    private static final String CLINICAL_SUMMARY = "CLINICAL_SUMMARY";
    private static final String JSON_CLINICAL_SUMMARY = "clinicalSummary.json";
    private static final String JSON_CLINICAL_SUMMARY_DETAIL = "clinicalSummaryDetail.json";
    private static final String CARE_PLAN_PURPOSE = "CARE_PLAN";
    @InjectMocks
    private MoreMedicalDocumentsService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private VolleyError volleyError;
    @Mock
    private Context context;
    @Mock
    private NavActivity activity;
    @Mock
    private PackageManager packageManager;
    @Mock
    private PackageInfo packageInfo;
    @Mock
    private ImpersonatedUserProfile userProfile;
    @Mock
    private Boolean show;
    @Mock
    private MoreMedicalDocumentsService.MoreMedDocListenerGet clinicalSummaryListenerGet;
    @Mock
    private MoreMedicalDocumentsService.MoreMedDocClinicalSummaryListenerPost clinicalSummaryListenerPost;
    @Mock
    private MoreMedicalDocumentsService.MoreMedDocClinicalSummaryListenerPdf clinicalSummaryListenerPdf;
    private String JSON_MED_DOC_LIST = "radiation.json";

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
        when(appSessionManager.getmClinicalSummaries()).thenReturn(getMockedClinicalSummaries());
        when(appSessionManager.getmClinicalDocs()).thenReturn(getMockedDocs());
        when(appSessionManager.getmRadiationDocs()).thenReturn(getMockedDocs());
        when(appSessionManager.getmIntegrativeDocs()).thenReturn(getMockedDocs());
        when(appSessionManager.getmImagingDocs()).thenReturn(getMockedImagingDocs());
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        MoreMedicalDocumentsService clinicalSummaryService = service.getInstance();
        assertThat(clinicalSummaryService).isNotNull();
        assertThat(clinicalSummaryService).isInstanceOf(MoreMedicalDocumentsService.class);
    }

    @Test
    public void notifyFetchSuccess() {
        assertThat(getMockedClinicalSummaries()).isNotEmpty();
        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getmClinicalSummaries()), CLINICAL_SUMMARY);
        assertThat(appSessionManager.getmClinicalSummaries()).isNotEmpty();

        assertThat(getMockedCarePlan()).isNotEmpty();
        service.notifyFetchSuccess(getMockedCarePlan(), CARE_PLAN_PURPOSE);

        List<MedDoc> medDocs = getMockedDocs();
        when(appSessionManager.getmRadiationDocs()).thenReturn(getMockedDocs());
        service.notifyFetchSuccess(new Gson().toJson(medDocs), "Radiation");
        assertThat(appSessionManager.getmRadiationDocs()).isNotEmpty();
        verify(clinicalSummaryListenerGet).fetchMedicalDocSuccess(appSessionManager.getmRadiationDocs());

        List<MedDoc> medDocs1 = getMockedDocs();
        when(appSessionManager.getmClinicalDocs()).thenReturn(getMockedDocs());
        service.notifyFetchSuccess(new Gson().toJson(medDocs1), "Clinical");
        assertThat(appSessionManager.getmClinicalDocs()).isNotEmpty();
        verify(clinicalSummaryListenerGet).fetchMedicalDocSuccess(appSessionManager.getmClinicalDocs());

        List<MedDoc> medDocs2 = getMockedDocs();
        when(appSessionManager.getmIntegrativeDocs()).thenReturn(getMockedDocs());
        service.notifyFetchSuccess(new Gson().toJson(medDocs2), "Integrative");
        assertThat(appSessionManager.getmIntegrativeDocs()).isNotEmpty();
        verify(clinicalSummaryListenerGet).fetchMedicalDocSuccess(appSessionManager.getmIntegrativeDocs());

        when(appSessionManager.getmImagingDocs()).thenReturn(getMockedImagingDocs());
        service.notifyFetchSuccess(new Gson().toJson(medDocs2), "IMAGING");
        assertThat(appSessionManager.getmImagingDocs()).isNotEmpty();
        verify(clinicalSummaryListenerGet).fetchMedicalDocSuccess(appSessionManager.getmImagingDocs());
    }

    private List<ImagingDoc> getMockedImagingDocs() {
        List<ImagingDoc> medDocList;
        ClassLoader classLoader = this.getClass().getClassLoader();
        ImagingDoc[] imagingDocs = new ImagingDoc[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_MED_DOC_LIST).
                    toURI())).parallel().collect(Collectors.joining());
            imagingDocs = new Gson().fromJson(json, ImagingDoc[].class);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        medDocList = new LinkedList<>(Arrays.asList(imagingDocs));
        return medDocList;
    }

    private List<MedDoc> getMockedDocs() {
        List<MedDoc> medDocList;
        ClassLoader classLoader = this.getClass().getClassLoader();
        MedDoc[] medDocs = new MedDoc[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_MED_DOC_LIST).
                    toURI())).parallel().collect(Collectors.joining());
            medDocs = new Gson().fromJson(json, MedDoc[].class);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        medDocList = new LinkedList<>(Arrays.asList(medDocs));

        return medDocList;
    }

    private String getMockedCarePlan() {
        return "<div style=\"text-align: Left; font-style: Normal; font-weight: normal; color: #000000;\">\n" +
                "    <h3>CONTENTS</h3>\n" +
                "    <p></p>\n" +
                "    <div>\n" +
                "        <a href=\"#Patient\">Patient</a>\n" +
                "    </div>\n" +
                "    <div>\n" +
                "        <a href=\"#Patient Status\">Patient Status</a>\n" +
                "    </div>\n" +
                "    <div>\n" +
                "        <a href=\"#Care Providers\">Care Providers</a>\n" +
                "    </div>\n" +
                "    <div>\n" +
                "        <a href=\"#Height, Weight, Vital Signs\">Height, Weight, Vital Signs</a>\n" +
                "    </div>\n" +
                "    <div>\n" +
                "        <a href=\"#Medications\">Medications</a>\n" +
                "    </div>\n" +
                "    <div>\n" +
                "        <a href=\"#Allergies\">Allergies</a>\n" +
                "    </div>\n" +
                "    <div>\n" +
                "        <a href=\"#Oncologist Assessment and Plan\">Oncologist Assessment and Plan</a>\n" +
                "    </div>\n" +
                "    <div>\n" +
                "        <a href=\"#Rehab\">Rehab</a>\n" +
                "    </div>\n" +
                "</div>\n";
    }

    private List<ClinicalSummary> getMockedClinicalSummaries() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        ClinicalSummary[] clinicalSummaries = new ClinicalSummary[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_CLINICAL_SUMMARY).
                    toURI())).parallel().collect(Collectors.joining());
            clinicalSummaries = new Gson().fromJson(json, ClinicalSummary[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new LinkedList<>(Arrays.asList(clinicalSummaries));
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, CLINICAL_SUMMARY);
        verify(clinicalSummaryListenerGet).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, CARE_PLAN_PURPOSE);
        verify(clinicalSummaryListenerGet, times(2)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, "Clinical");
        verify(clinicalSummaryListenerGet, times(3)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, "Radiation");
        verify(clinicalSummaryListenerGet, times(4)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, "Integrative");
        verify(clinicalSummaryListenerGet, times(5)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, "Imaging");
        verify(clinicalSummaryListenerGet, times(6)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
    }

    @Test
    public void clearClinicalSummaries() {
        assertThat(appSessionManager.getmClinicalSummaries()).isNotEmpty();
        service.clearClinicalSummaries();
        assertThat(appSessionManager.getmClinicalSummaries()).isEmpty();
    }

    @Test
    public void notifyPostSuccess() {
        service.notifyPostSuccess("Success", 2);
        verify(clinicalSummaryListenerPost, times(1))
                .notifyPostSuccess("Success");
    }

    @Test
    public void notifyPostError() {
        service.notifyPostError(volleyError, "message", 1);
        verify(clinicalSummaryListenerPost, times(1))
                .notifyPostError("message");
    }

    @Test
    public void notifyPostPdfSuccess() {
        String str = "hello";
        service.notifyPostPdfSuccess(str.getBytes(), 1);
        verify(clinicalSummaryListenerPdf, times(1))
                .notifyPostPdfSuccess(str.getBytes());
    }

    @Test
    public void notifyPostPdfError() {
        service.notifyPostPdfError(volleyError, 1);
        verify(clinicalSummaryListenerPdf, times(1))
                .notifyPostPdfError();
    }

    @Test
    public void clearDocsArray() {
        assertThat(appSessionManager.getmClinicalDocs()).isNotEmpty();
        service.clearDocsArray("Clinical");
        assertThat(appSessionManager.getmClinicalDocs()).isEmpty();

        assertThat(appSessionManager.getmRadiationDocs()).isNotEmpty();
        service.clearDocsArray("Radiation");
        assertThat(appSessionManager.getmRadiationDocs()).isEmpty();

        assertThat(appSessionManager.getmIntegrativeDocs()).isNotEmpty();
        service.clearDocsArray("Integrative");
        assertThat(appSessionManager.getmIntegrativeDocs()).isEmpty();

        assertThat(appSessionManager.getmImagingDocs()).isNotEmpty();
        service.clearDocsArray("Imaging");
        assertThat(appSessionManager.getmImagingDocs()).isEmpty();
    }
}