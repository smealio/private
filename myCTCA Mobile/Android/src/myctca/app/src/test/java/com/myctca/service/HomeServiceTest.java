package com.myctca.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.activity.NavActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.ApplicationVersion;
import com.myctca.model.HomeAlert;
import com.myctca.model.ImpersonatedUserProfile;
import com.myctca.model.MyCTCAUserProfile;

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
public class HomeServiceTest {

    private static final String JSON_ALERTS = "homeAlerts.json";
    private static final String JSON_USER_PROFILE = "userProfile.json";
    private static final String JSON_VERSIONS = "applicationVersions.json";
    private static final String HOME_ALERTS = "HOME_ALERTS";
    @InjectMocks
    private HomeService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private HomeService.HomeServiceInterface homeServiceInterface;
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

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);
        when(appSessionManager.getHomeAlerts()).thenReturn(getMockedAlerts());
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        HomeService homeService = service.getInstance();
        assertThat(homeService).isNotNull();
        assertThat(homeService).isInstanceOf(HomeService.class);
    }

    @Test
    public void isMessageUpdateDialogMandatory() {

    }

    @Test
    public void notifyFetchSuccess() {
        assertThat(getMockedAlerts()).isNotEmpty();
        service.notifyFetchSuccess(new Gson().toJson(appSessionManager.getAppointments()), HOME_ALERTS);
        assertThat(appSessionManager.getHomeAlerts()).isNotEmpty();
    }

    private List<String> getMockedAlerts() {
        List<String> alerts = new ArrayList<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        HomeAlert[] homeAlerts = new HomeAlert[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_ALERTS).
                    toURI())).parallel().collect(Collectors.joining());
            homeAlerts = new Gson().fromJson(json, HomeAlert[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        for (HomeAlert alert : homeAlerts) {
            alerts.add(alert.messageText);
        }
        return alerts;
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, HOME_ALERTS);
        verify(homeServiceInterface).notifyError(VolleyErrorHandler.handleError(volleyError, context));
    }

    @Test
    public void checkVersions() {
        when(appSessionManager.getApplicationVersions()).thenReturn(getMockedApplicationVersions());
        when(activity.getPackageManager()).thenReturn(packageManager);
        try {
            when(packageManager.getPackageInfo(activity.getPackageName(), 0)).thenReturn(packageInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        packageInfo.versionName = "2.0.5";
        service.checkVersions(activity);
        show = service.showMessageUpdateDialog();
        assertThat(show).isFalse();

        packageInfo.versionName = "2.0";
        service.checkVersions(activity);
        show = service.showMessageUpdateDialog();
        assertThat(show).isTrue();

        packageInfo.versionName = "2.0.3";
        service.checkVersions(activity);
        show = service.showMessageUpdateDialog();
        assertThat(show).isTrue();
    }

    private List<ApplicationVersion> getMockedApplicationVersions() {
        List<ApplicationVersion> applicationVersions = new ArrayList<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ApplicationVersion[] versions = new ApplicationVersion[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_VERSIONS).
                    toURI())).parallel().collect(Collectors.joining());
            versions = new Gson().fromJson(json, ApplicationVersion[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        applicationVersions = new LinkedList<>(Arrays.asList(versions));
        return applicationVersions;
    }

    @Test
    public void notifyPostSuccess() {
        String userProfileStr = getMockedUserProfile();
        MyCTCAUserProfile userProfile = new Gson().fromJson(userProfileStr, MyCTCAUserProfile.class);
        service.notifyPostSuccess(userProfileStr, 12);
        when(appSessionManager.getUserProfile()).thenReturn(userProfile);
        assertThat(appSessionManager.getUserProfile()).isEqualTo(userProfile);
    }

    private String getMockedUserProfile() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_USER_PROFILE).
                    toURI())).parallel().collect(Collectors.joining());
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Test
    public void notifyPostError() {
        service.notifyPostError(volleyError,"error",12);
        verify(homeServiceInterface).notifyError(VolleyErrorHandler.handleError(volleyError, context));

    }
}