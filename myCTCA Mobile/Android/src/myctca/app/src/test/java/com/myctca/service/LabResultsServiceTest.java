package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.LabResult;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class LabResultsServiceTest {

    private static final String JSON_LABS = "labResults.json";
    private static final String LAB_RESULTS = "LAB_RESULTS";
    private List<LabResult> labResults = getMockedLabResults();

    @InjectMocks
    private LabResultsService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private LabResultsService.LabResultsServiceListener listener;
    @Mock
    private Context context;
    @Mock
    private VolleyError volleyError;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
        when(appSessionManager.getLabResults()).thenReturn(labResults);
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        LabResultsService labResultsService = service.getInstance();
        assertThat(labResultsService).isNotNull();
        assertThat(labResultsService).isInstanceOf(LabResultsService.class);
    }

    @Test
    public void getLabResults() {
        service.getLabResults(context, LAB_RESULTS, listener);
        verify(listener, times(1)).notifyFetchSuccess(labResults);
    }

    @Test
    public void clearLabResults() {
        assertThat(appSessionManager.getLabResults()).isNotEmpty();
        service.clearLabResults();
        assertThat(appSessionManager.getLabResults()).isEmpty();
    }

    @Test
    public void isLabResultLessThan24HoursAgo() {
        boolean result = service.isLabResultLessThan24HoursAgo(new Date());
        assertThat(result).isFalse();

        result = service.isLabResultLessThan24HoursAgo(new Date(new Date().getTime() - 86400000*2));
        assertThat(result).isTrue();
    }

    @Test
    public void notifyFetchSuccess() {
        assertThat(getMockedLabResults()).isNotEmpty();
        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getAppointments()), LAB_RESULTS);
        assertThat(appSessionManager.getLabResults()).isNotEmpty();
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, LAB_RESULTS);
        verify(listener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), LAB_RESULTS);
    }

    private List<LabResult> getMockedLabResults() {
        List<LabResult> labResults;
        ClassLoader classLoader = this.getClass().getClassLoader();
        LabResult[] labResultArray = new LabResult[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_LABS).
                    toURI())).parallel().collect(Collectors.joining());
            labResultArray = new Gson().fromJson(json, LabResult[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        labResults = new LinkedList<>(Arrays.asList(labResultArray));

        return labResults;
    }
}