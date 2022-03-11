package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.CareTeam;

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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class CommonServiceTest {
    private static final String JSON_CARE_TEAMS = "careTeams.json";
    private static final String CARE_PLAN_PURPOSE = "CARE_PLAN_NEW";
    @InjectMocks
    private CommonService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private CommonService.CommonServiceListener listener;
    @Mock
    private VolleyError volleyError;
    @Mock
    private Context context;

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        CommonService commonService = service.getInstance();
        assertThat(commonService).isNotNull();
        assertThat(commonService).isInstanceOf(CommonService.class);
    }

    @Test
    public void notifyFetchSuccess() {
        when(appSessionManager.getmCareTeam()).thenReturn(getCareTeamsNames());
        service.notifyFetchSuccess(new Gson().toJson(getMockedCareTeams()), "ANY");
        assertThat(appSessionManager.getmCareTeam()).isNotEmpty();
        verify(listener).notifyFetchSuccess(CARE_PLAN_PURPOSE);
    }

    private List<CareTeam> getMockedCareTeams() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        CareTeam[] careTeams = new CareTeam[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_CARE_TEAMS).
                    toURI())).parallel().collect(Collectors.joining());
            careTeams = new Gson().fromJson(json, CareTeam[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new LinkedList<>(Arrays.asList(careTeams));
    }

    private List<String> getCareTeamsNames() {
        List<String> careTeamNames = new ArrayList<>();
        for (CareTeam team : getMockedCareTeams()) {
            careTeamNames.add(team.getName());
        }
        return careTeamNames;
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, "ANY");
        verify(listener).notifyFetchError(VolleyErrorHandler.handleError(volleyError, context), "ANY");
    }
}