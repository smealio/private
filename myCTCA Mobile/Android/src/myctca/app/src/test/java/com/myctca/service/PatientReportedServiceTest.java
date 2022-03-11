package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.PatientReportedSymptomInventory;
import com.myctca.model.SymptomInventory;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class PatientReportedServiceTest {

    private static final String PURPOSE = "SYMPTOM_INVENTORY";
    private static final String JSON_PATIENT_INVENTORY = "symptomInventory.json";
    @InjectMocks
    private PatientReportedService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private PatientReportedService.PatientReportedServiceListener getListener;
    @Mock
    private Context context;
    @Mock
    private VolleyError volleyError;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        PatientReportedService patientReportedService = service.getInstance();
        assertThat(patientReportedService).isNotNull();
        assertThat(patientReportedService).isInstanceOf(PatientReportedService.class);
    }

    @Test
    public void downloadPatientSymptomInventory() {
        when(appSessionManager.getSymptomInventories()).thenReturn(getMockedSymptomInventories());
        final String url = "https://v3testservice.myctca.com/api/v1/medicaldocuments/getpatientreporteddocuments";
        service.downloadPatientSymptomInventory(getListener, context, url, PURPOSE);
    }

    private List<SymptomInventory> getMockedSymptomInventories() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        PatientReportedSymptomInventory[] symptomInventories = new PatientReportedSymptomInventory[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_PATIENT_INVENTORY).
                    toURI())).parallel().collect(Collectors.joining());
            symptomInventories = new Gson().fromJson(json, PatientReportedSymptomInventory[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Map<String, List<PatientReportedSymptomInventory>> inventoryMap = new HashMap<>();
        for (PatientReportedSymptomInventory inventory : symptomInventories) {
            if (inventoryMap.containsKey(inventory.getPerformedDisplayDate())) {
                inventoryMap.get(inventory.getPerformedDisplayDate()).add(inventory);
            } else {
                List<PatientReportedSymptomInventory> newList = new ArrayList<>();
                newList.add(inventory);
                inventoryMap.put(inventory.getPerformedDisplayDate(), newList);
            }
        }

        List<SymptomInventory> list = new ArrayList<>();
        for (String key :
                inventoryMap.keySet()) {
            SymptomInventory inventory = new SymptomInventory();
            inventory.setDate(key);
            inventory.setSymptomInventories(inventoryMap.get(key));
            list.add(inventory);
        }

        return list;
    }

    @Test
    public void notifyFetchSuccess() {
        when(appSessionManager.getSymptomInventories()).thenReturn(getMockedSymptomInventories());
        service.notifyFetchSuccess(new Gson().toJson(getMockedSymptomInventories()), PURPOSE);
        assertThat(appSessionManager.getSymptomInventories()).isNotEmpty();
        verify(getListener).notifyFetchSuccess(appSessionManager.getSymptomInventories());
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, PURPOSE);
        verify(getListener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), PURPOSE);
    }
}