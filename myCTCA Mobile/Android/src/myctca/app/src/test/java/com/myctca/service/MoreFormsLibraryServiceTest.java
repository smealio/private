package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.Facility;
import com.myctca.model.FacilityInfoAll;
import com.myctca.model.MedicalCenter;
import com.myctca.model.RoiDetails;

import junit.framework.TestCase;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PowerMockIgnore("jdk.internal.reflect.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class MoreFormsLibraryServiceTest extends TestCase {
    private static final String JSON_ROI_DETAILS = "roiDetails.json";
    private static final String JSON_CONTACT_INFO = "contact.json";
    private static final String JSON_FACILITIES = "facilities.json";
    private static final String JSON_ROI_DOB = "roiDateOfBirth.json";
    private static final String ANNC_PURPOSE = "ANNC_FORM_EXISTS";
    private static final String PURPOSE_FACILITY_LIST = "FACILITY_LIST";
    private static final String PURPOSE_MRN = "MRN";
    private static final String ROIDETAILS = "ROI DETAILS";
    private static final String ROICONTACTINFO = "ROI CONTACT INFO";


    @InjectMocks
    private MoreFormsLibraryService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private MoreFormsLibraryService.MoreFormsLibraryROIListenerGet roiListenerGet;
    @Mock
    private MoreFormsLibraryService.MoreFormsLibraryANNCListenerGet anncListenerGet;
    @Mock
    private MoreFormsLibraryService.MoreFormsLibraryListenerPost listenerPost;
    @Mock
    private VolleyError volleyError;
    @Mock
    private Context context;
    @Mock
    private Date date;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
        when(appSessionManager.getRoiDetails()).thenReturn(getMockedRoiDetails());
        when(appSessionManager.getDob()).thenReturn(date);
        when(appSessionManager.getAnncFormExistsCheck()).thenReturn(true);
        when(appSessionManager.getMrn()).thenReturn("12345");
        when(appSessionManager.getFacilityAll()).thenReturn(getMockedFacilities());
        when(appSessionManager.getPreferredFacility()).thenReturn(new Facility());
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        MoreFormsLibraryService formsLibraryService = service.getInstance();
        assertThat(formsLibraryService).isNotNull();
        assertThat(formsLibraryService).isInstanceOf(MoreFormsLibraryService.class);
    }

    @Test
    public void fetchANNCData() {
        service.fetchANNCData(anncListenerGet, "", context, ANNC_PURPOSE);
        verify(anncListenerGet).ifANNCExists(appSessionManager.getIfAnncFormExists());

        service.fetchANNCData(anncListenerGet, "", context, PURPOSE_MRN);
        verify(anncListenerGet).notifyMrn(appSessionManager.getMrn());

        service.fetchANNCData(anncListenerGet, "", context, PURPOSE_FACILITY_LIST);
        verify(anncListenerGet).notifyFacilityInfo(appSessionManager.getFacilityAll(), appSessionManager.getPreferredFacility());
    }

    @Test
    public void fetchROIData() {
        service.fetchROIData(roiListenerGet, "", null, context, ROIDETAILS);
        verify(roiListenerGet).notifyFetchDetails(appSessionManager.getRoiDetails());

        service.fetchROIData(roiListenerGet, "", null, context, ROICONTACTINFO);
        verify(roiListenerGet).notifyFetchDob(appSessionManager.getDob());
    }

    private RoiDetails getMockedRoiDetails() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        RoiDetails roiDetails = new RoiDetails();
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_ROI_DETAILS).
                    toURI())).parallel().collect(Collectors.joining());
            roiDetails = new Gson().fromJson(json, RoiDetails.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return roiDetails;
    }

    @Test
    public void notifyFetchSuccess() {
        service.notifyFetchSuccess("true", ANNC_PURPOSE);
        assertThat(appSessionManager.getAnncFormExistsCheck()).isTrue();

        service.notifyFetchSuccess("\"12345\"", PURPOSE_MRN);
        assertThat(appSessionManager.getMrn()).isEqualTo("12345");

        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getFacilityAll()), PURPOSE_FACILITY_LIST);
        assertThat(appSessionManager.getFacilityAll()).isNotEmpty();
        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getFacilityAll() + "hello"), PURPOSE_FACILITY_LIST);
        assertThat(appSessionManager.getFacilityAll()).isNotEmpty();

        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getRoiDetails()), ROIDETAILS);
        assertThat(appSessionManager.getRoiDetails()).isNotNull();
        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getRoiDetails() + "hello"), ROIDETAILS);
        assertThat(appSessionManager.getRoiDetails()).isNotNull();

        service.notifyFetchSuccess("{\n" +
                "            \"dateOfBirth\": \"1977-02-18T00:00:00\"\n" +
                "        }", ROICONTACTINFO);
        assertThat(appSessionManager.getDob()).isNotNull();

        service.notifyFetchSuccess("{\n" +
                "            \"dateOfBirthhee\": \"1977-02-18T00:00:00\"\n" +
                "        }", ROICONTACTINFO);
        assertThat(appSessionManager.getDob()).isNotNull();
    }

    private List<MedicalCenter> getMockedFacilities() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        FacilityInfoAll[] facilityInfoAlls;
        List<MedicalCenter> facilityList = null;
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_FACILITIES).
                    toURI())).parallel().collect(Collectors.joining());
            facilityInfoAlls = new Gson().fromJson(json, FacilityInfoAll[].class);
            facilityList = new ArrayList<>();
            for (FacilityInfoAll facilityInfoAll : facilityInfoAlls) {
                facilityList.add(new MedicalCenter(facilityInfoAll.name, facilityInfoAll.displayName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return facilityList;
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, ANNC_PURPOSE);
        verify(anncListenerGet).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, PURPOSE_FACILITY_LIST);
        verify(anncListenerGet, times(2)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, PURPOSE_MRN);
        verify(anncListenerGet, times(3)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, ROICONTACTINFO);
        verify(roiListenerGet).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, ROIDETAILS);
        verify(roiListenerGet, times(2)).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context));
    }

    @Test
    public void notifyPostError() {
        service.notifyPostError(volleyError, "message", 0);
        verify(listenerPost, times(1))
                .notifyPostError("message");
        service.notifyPostError(volleyError, "message", 1);
        verify(listenerPost, times(2))
                .notifyPostError("message");
    }
}