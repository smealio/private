package com.myctca.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.BuildConfig;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.Allergy;
import com.myctca.model.HealthIssue;
import com.myctca.model.Immunization;
import com.myctca.model.MyCTCATask;
import com.myctca.model.Prescription;
import com.myctca.model.VitalsGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class HealthHistoryServiceTest {
    private static final String TAG = "HealthHistoryServiceTest";
    private static final String HEALTH_ISSUE_PURPOSE = "HEALTH_ISSUE";
    private static final String ALLERGIES_PURPOSE = "ALLERGIES";
    private static final String IMMUNIZATION_PURPOSE = "IMMUNIZATION";
    private static final String VITALS_PURPOSE = "VITALS";
    private static final String PRESCRIPTIONS_PURPOSE = "PRESCRIPTIONS";
    private static final String JSON_ALLERGIES = "allergies.json";
    private static final String JSON_PRESCRIPTIONS = "prescriptions.json";
    private static final String JSON_IMMUNIZATIONS = "immunizations.json";
    private static final String JSON_HEALTH_ISSUES = "healthIssues.json";
    private static final String JSON_VITALS = "vitals.json";
    @InjectMocks
    private HealthHistoryService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private HealthHistoryService.HealthHistoryServiceListener listener;
    @Mock
    private HealthHistoryService.HealthHistoryServicePostListener postListener;
    @Mock
    private VolleyError volleyError;
    @Mock
    private Context context;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
        when(appSessionManager.getmAllergies()).thenReturn(getMockedAllergies());
        when(appSessionManager.getmImmunizations()).thenReturn(getMockedImmunizations());
        when(appSessionManager.getmVitalsGroups()).thenReturn(getMockedVitals());
        when(appSessionManager.getmHealthIssues()).thenReturn(getMockedHealthIssues());
        when(appSessionManager.getmPrescriptions()).thenReturn(getMockedPrescriptions());
    }

    private List<Prescription> getMockedPrescriptions() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Prescription[] prescriptions = new Prescription[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_PRESCRIPTIONS).
                    toURI())).parallel().collect(Collectors.joining());
            prescriptions = new Gson().fromJson(json, Prescription[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new LinkedList<>(Arrays.asList(prescriptions));

    }

    private List<HealthIssue> getMockedHealthIssues() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        HealthIssue[] healthIssues = new HealthIssue[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_HEALTH_ISSUES).
                    toURI())).parallel().collect(Collectors.joining());
            healthIssues = new Gson().fromJson(json, HealthIssue[].class);
        } catch (IOException | URISyntaxException e) {
            Log.e(TAG, "exception:" + e);
        }
        return new LinkedList<>(Arrays.asList(healthIssues));
    }

    private List<VitalsGroup> getMockedVitals() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        VitalsGroup[] vitalsGroups = new VitalsGroup[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_VITALS).
                    toURI())).parallel().collect(Collectors.joining());
            vitalsGroups = new Gson().fromJson(json, VitalsGroup[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new LinkedList<>(Arrays.asList(vitalsGroups));

    }

    private List<Immunization> getMockedImmunizations() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Immunization[] immunizations = new Immunization[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_IMMUNIZATIONS).
                    toURI())).parallel().collect(Collectors.joining());
            immunizations = new Gson().fromJson(json, Immunization[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new LinkedList<>(Arrays.asList(immunizations));
    }

    private List<Allergy> getMockedAllergies() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Allergy[] allergies = new Allergy[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_ALLERGIES).
                    toURI())).parallel().collect(Collectors.joining());
            allergies = new Gson().fromJson(json, Allergy[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new LinkedList<>(Arrays.asList(allergies));
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        HealthHistoryService healthHistoryService = service.getInstance();
        assertThat(healthHistoryService).isNotNull();
        assertThat(healthHistoryService).isInstanceOf(HealthHistoryService.class);
    }

    @Test
    public void downloadHealthHistoryTypeList() {
        service.downloadHealthHistoryTypeList(listener, context,
                BuildConfig.myctca_server + context.getString(R.string.myctca_get_allergies),
                ALLERGIES_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmAllergies(), ALLERGIES_PURPOSE);

        service.downloadHealthHistoryTypeList(listener, context,
                BuildConfig.myctca_server + context.getString(R.string.myctca_get_immunizations),
                IMMUNIZATION_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmImmunizations(), IMMUNIZATION_PURPOSE);

        service.downloadHealthHistoryTypeList(listener, context,
                BuildConfig.myctca_server + context.getString(R.string.myctca_get_vitals),
                VITALS_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmVitalsGroups(), VITALS_PURPOSE);

        service.downloadHealthHistoryTypeList(listener, context,
                BuildConfig.myctca_server + context.getString(R.string.myctca_get_health_issues),
                HEALTH_ISSUE_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmHealthIssues(), HEALTH_ISSUE_PURPOSE);

        service.downloadHealthHistoryTypeList(listener, context,
                BuildConfig.myctca_server + context.getString(R.string.myctca_get_prescriptions),
                PRESCRIPTIONS_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmPrescriptions(), PRESCRIPTIONS_PURPOSE);
    }

    @Test
    public void getHealthHistoryType() {
        List<Allergy> allergies = service.getHealthHistoryType(ALLERGIES_PURPOSE);
        assertThat(allergies).isEqualTo(appSessionManager.getmAllergies());

        List<Immunization> immunizations = service.getHealthHistoryType(IMMUNIZATION_PURPOSE);
        assertThat(immunizations).isEqualTo(appSessionManager.getmImmunizations());

        List<HealthIssue> healthIssues = service.getHealthHistoryType(HEALTH_ISSUE_PURPOSE);
        assertThat(healthIssues).isEqualTo(appSessionManager.getmHealthIssues());

        List<VitalsGroup> vitals = service.getHealthHistoryType(VITALS_PURPOSE);
        assertThat(vitals).isEqualTo(appSessionManager.getmVitalsGroups());

        List<Prescription> prescriptions = service.getHealthHistoryType(PRESCRIPTIONS_PURPOSE);
        assertThat(prescriptions).isEqualTo(appSessionManager.getmPrescriptions());
    }

    @Test
    public void notifyFetchSuccess() {
        //correct json
        service.notifyFetchSuccess(new Gson().toJson(getMockedAllergies()), ALLERGIES_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmAllergies(), ALLERGIES_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedHealthIssues()), HEALTH_ISSUE_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmHealthIssues(), HEALTH_ISSUE_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedImmunizations()), IMMUNIZATION_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmImmunizations(), IMMUNIZATION_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedPrescriptions()), PRESCRIPTIONS_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmPrescriptions(), PRESCRIPTIONS_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedVitals()), VITALS_PURPOSE);
        verify(listener).notifyFetchSuccess(appSessionManager.getmVitalsGroups(), VITALS_PURPOSE);

        //bad json
        service.notifyFetchSuccess(new Gson().toJson(getMockedAllergies() + "{bad json}"), ALLERGIES_PURPOSE);
        verify(listener).notifyFetchError(context.getString(R.string.error_400), ALLERGIES_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedImmunizations() + "{bad json}"), IMMUNIZATION_PURPOSE);
        verify(listener).notifyFetchError(context.getString(R.string.error_400), IMMUNIZATION_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedHealthIssues() + "{bad json}"), HEALTH_ISSUE_PURPOSE);
        verify(listener).notifyFetchError(context.getString(R.string.error_400), HEALTH_ISSUE_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedVitals() + "{bad json}"), VITALS_PURPOSE);
        verify(listener).notifyFetchError(context.getString(R.string.error_400), VITALS_PURPOSE);

        service.notifyFetchSuccess(new Gson().toJson(getMockedPrescriptions() + "{bad json}"), PRESCRIPTIONS_PURPOSE);
        verify(listener).notifyFetchError(context.getString(R.string.error_400), PRESCRIPTIONS_PURPOSE);
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, ALLERGIES_PURPOSE);
        verify(listener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), ALLERGIES_PURPOSE);

        service.notifyFetchError(volleyError, PRESCRIPTIONS_PURPOSE);
        verify(listener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), PRESCRIPTIONS_PURPOSE);

        service.notifyFetchError(volleyError, IMMUNIZATION_PURPOSE);
        verify(listener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), IMMUNIZATION_PURPOSE);

        service.notifyFetchError(volleyError, HEALTH_ISSUE_PURPOSE);
        verify(listener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), HEALTH_ISSUE_PURPOSE);

        service.notifyFetchError(volleyError, VITALS_PURPOSE);
        verify(listener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), VITALS_PURPOSE);
    }

    @Test
    public void clearHealthHistoryType() {
        service.clearHealthHistoryType(ALLERGIES_PURPOSE);
        assertThat(appSessionManager.getmAllergies()).isEmpty();

        service.clearHealthHistoryType(HEALTH_ISSUE_PURPOSE);
        assertThat(appSessionManager.getmHealthIssues()).isEmpty();

        service.clearHealthHistoryType(VITALS_PURPOSE);
        assertThat(appSessionManager.getmVitalsGroups()).isEmpty();

        service.clearHealthHistoryType(PRESCRIPTIONS_PURPOSE);
        assertThat(appSessionManager.getmPrescriptions()).isEmpty();

        service.clearHealthHistoryType(IMMUNIZATION_PURPOSE);
        assertThat(appSessionManager.getmImmunizations()).isEmpty();
    }

    @Test
    public void notifyPostSuccess() {
        service.notifyPostSuccess("success", MyCTCATask.PRESCRIPTION_REFILL_REQUEST);
        verify(postListener).notifyPostSuccess();
    }

    @Test
    public void notifyPostError() {
        service.notifyPostError(volleyError, "Error", MyCTCATask.PRESCRIPTION_REFILL_REQUEST);
        verify(postListener).notifyPostError("Error");
    }
}