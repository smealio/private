package com.myctca.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.microsoft.appcenter.AppCenter;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.fingerprintauth.FingerprintHandler;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.AccessToken;
import com.myctca.model.Facility;
import com.myctca.model.IdentityUser;
import com.myctca.model.LoginError;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.StoredPreferences;
import com.myctca.model.UserPreference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static com.myctca.MyCTCA.getAppContext;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppSessionManager.class)
public class LoginServiceTest {

    private static final String JSON_TOKEN = "loginToken.json";
    private static final String JSON_IDENTITY_TOKEN = "loginIdentityUser.json";
    private static final String JSON_FACILITY_INFO = "loginFacilityInfo.json";
    private static final String JSON_USER_PROFILE = "loginUserProfile.json";
    private static final String JSON_USER_PREFERENCES = "loginUserPreferences.json";
    @InjectMocks
    private LoginService service;
    @Mock
    private AppSessionManager appSessionManager;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private LoginService.LoginServiceGetListener loginServiceGetListener;
    @Mock
    private LoginService.LoginServicePostListener loginServicePostListener;
    @Mock
    private VolleyError volleyError;
    @Mock
    private NetworkResponse networkResponse;
    @Mock
    private Context context;
    @Mock
    private FingerprintHandler fingerprintHandler;
    @Mock
    private VolleyErrorHandler volleyErrorHandler;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private AppCenter appCenter;
    @Mock
    private IdentityUser identityUser;
    @Mock
    private MyCTCAUserProfile userProfile;
    @Mock
    private FingerprintManager fingerprintManager;

    private void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    private void setStatic(Object newValue) throws Exception {
        Field field = MyCTCA.class.getDeclaredField("mContext");
        field.setAccessible(true);
        field.set(null, newValue);
    }

    @Before
    public void setup() {
        PowerMockito.mockStatic(AppSessionManager.class);
        when(AppSessionManager.getInstance()).thenReturn(appSessionManager);

        try {
            setStatic(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getInstance() {
        assertThat(service).isNotNull();
        LoginService loginService = service.getInstance();
        assertThat(loginService).isNotNull();
        assertThat(loginService).isInstanceOf(LoginService.class);
    }

    @Test
    public void getStoredPreferences() {
        when(context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(context.getString(R.string.pref_last_ctca_id), "")).thenReturn("philly.scm@testctca.com");
        StoredPreferences storedPreferences = service.getStoredPreferences(context);
        assertThat(storedPreferences.getHasPreviousUsername()).isTrue();
        assertThat(storedPreferences.getPreviousUsername()).isNotEmpty();
        assertThat(storedPreferences.isFingerAuthEnabled()).isFalse();
        assertThat(storedPreferences.isFingerAuthPrefSet()).isFalse();
        assertThat(storedPreferences.isFingerAuthSupported()).isFalse();

        //change the SDK number so that it goes inside if condition
        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 123);
        } catch (Exception e) {
            e.printStackTrace();
        }

        when(sharedPreferences.getBoolean(context.getString(R.string.pref_finger_auth_enabled), false)).thenReturn(true);
        when(sharedPreferences.getBoolean(context.getString(R.string.pref_finger_auth_pref_set), false)).thenReturn(true);
        storedPreferences = service.getStoredPreferences(context);
        assertThat(storedPreferences.getHasPreviousUsername()).isTrue();
        assertThat(storedPreferences.getPreviousUsername()).isNotEmpty();
        assertThat(storedPreferences.isFingerAuthEnabled()).isTrue();
        assertThat(storedPreferences.isFingerAuthPrefSet()).isTrue();
        assertThat(storedPreferences.isFingerAuthSupported()).isFalse();

        when(sharedPreferences.getString(context.getString(R.string.pref_last_ctca_id), "")).thenReturn("");
        storedPreferences = service.getStoredPreferences(context);
        assertThat(storedPreferences.getHasPreviousUsername()).isFalse();
        assertThat(storedPreferences.getPreviousUsername()).isEmpty();
    }

    @Test
    public void osSupportsFingerAuthentication() {
        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 123);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(fingerprintManager.isHardwareDetected()).thenReturn(true);
        boolean ifFingerprintSupported = service.osSupportsFingerAuthentication();
        assertThat(ifFingerprintSupported).isFalse();
    }

    private AccessToken getMockedTokenResponse() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        AccessToken token = new AccessToken();
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_TOKEN).
                    toURI())).parallel().collect(Collectors.joining());
            token = new Gson().fromJson(json, AccessToken.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return token;
    }

    @Test
    public void notifyPostSuccess() {
        service.notifyPostSuccess(new Gson().toJson(getMockedTokenResponse()), 1);
        verify(loginServicePostListener, times(1))
                .notifyPostSuccess();
    }

    @Test
    public void notifyPostError() {
        when(context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(sharedPreferences.getInt(context.getString(R.string.pref_incorrect_login_attempts), 1)).thenReturn(2);
        when(context.getString(R.string.error_400)).thenReturn("Error found");
        service.notifyPostError(volleyError, "error", 1);
        verify(loginServicePostListener, times(1)).notifyPostError("Error found");

        when(context.getString(R.string.invalid_grant)).thenReturn("invalid_grant");
        when(sharedPreferences.edit()).thenReturn(editor);
        when(context.getString(R.string.pref_incorrect_login_attempts)).thenReturn("com.myctca.myctca.INCORRECT_LOGIN_ATTEMPTS");
        when(sharedPreferences.edit().putInt(context.getString(R.string.pref_incorrect_login_attempts), 1)).thenReturn(editor);
        try {
            Field field = VolleyError.class.getField("networkResponse");
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(volleyError, networkResponse);

            field = NetworkResponse.class.getField("data");
            field.setAccessible(true);
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            LoginError loginError = new LoginError();
            loginError.error = "invalid_grant";
            field.set(networkResponse, new Gson().toJson(loginError).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        service.notifyPostError(volleyError, "error", 1);
        verify(loginServicePostListener, times(1)).notifyPostError("Error found");

        when(sharedPreferences.getInt(context.getString(R.string.pref_incorrect_login_attempts), 0)).thenReturn(2);
        when(sharedPreferences.edit().putInt(context.getString(R.string.pref_incorrect_login_attempts), 3)).thenReturn(editor);
        service.notifyPostError(volleyError, "error", 1);
        verify(loginServicePostListener, times(1)).notifyPostError("");

        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 123);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(sharedPreferences.getBoolean(context.getString(R.string.pref_finger_auth_enabled), false)).thenReturn(true);
        when(sharedPreferences.edit().putBoolean(getAppContext().getString(R.string.pref_finger_auth_enabled), false)).thenReturn(editor);
        when(sharedPreferences.getString(context.getString(R.string.pref_last_ctca_id), "")).thenReturn("philly.scm@testctca.com");
        when(FingerprintHandler.getInstance()).thenReturn(fingerprintHandler);
        service.notifyPostError(volleyError, "error", 1);

        assertThat(sharedPreferences.getBoolean(context.getString(R.string.pref_finger_auth_enabled), false)).isTrue();
        assertThat(sharedPreferences.getBoolean(context.getString(R.string.pref_finger_auth_pref_set), false)).isTrue();
    }

    private IdentityUser getMockedIdentityUser() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        IdentityUser identityUser = new IdentityUser();
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_IDENTITY_TOKEN).
                    toURI())).parallel().collect(Collectors.joining());
            identityUser = new Gson().fromJson(json, IdentityUser.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return identityUser;
    }

    private MyCTCAUserProfile getMockedUserProfile() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        MyCTCAUserProfile userProfile = new MyCTCAUserProfile();
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_USER_PROFILE).
                    toURI())).parallel().collect(Collectors.joining());
            userProfile = new Gson().fromJson(json, MyCTCAUserProfile.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return userProfile;
    }

    private Facility[] getMockedFacilityInfo() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Facility[] facilities = new Facility[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_FACILITY_INFO).
                    toURI())).parallel().collect(Collectors.joining());
            facilities = new Gson().fromJson(json, Facility[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return facilities;
    }

    private UserPreference[] getMockedFUserPreferences() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        UserPreference[] userProfile = new UserPreference[0];
        try {
            String json = Files.lines(Paths.get(classLoader.getResource(JSON_USER_PREFERENCES).
                    toURI())).parallel().collect(Collectors.joining());
            userProfile = new Gson().fromJson(json, UserPreference[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return userProfile;
    }

    @Test
    public void notifyFetchSuccess() {
        service.notifyFetchSuccess(new Gson().toJson(getMockedIdentityUser()), "PURPOSE_USER_DATA");
        verify(loginServiceGetListener).notifyGetSuccess("PURPOSE_USER_DATA", false);

        service.notifyFetchSuccess(new Gson().toJson(getMockedUserProfile()), "PURPOSE_USER_PROFILE");
        verify(loginServiceGetListener).notifyGetSuccess("PURPOSE_USER_PROFILE", false);

        service.notifyFetchSuccess(new Gson().toJson(getMockedFacilityInfo()), "PURPOSE_FACILITY_DATA");
        verify(loginServiceGetListener).notifyGetSuccess("PURPOSE_FACILITY_DATA", false);

        service.notifyFetchSuccess(new Gson().toJson(getMockedFUserPreferences()), "USER_PREFERENCES_PURPOSE");
    }

    @Test
    public void acceptedTermsOfUse() {
        when(context.getString(R.string.val_true)).thenReturn("True");
        service.acceptedTermsOfUse(new Gson().toJson(getMockedFUserPreferences()), "USER_PREFERENCES_PURPOSE");
        assertThat(appCenter.isEnabled().get()).isFalse();
        verify(loginServiceGetListener).notifyGetSuccess("USER_PREFERENCES_PURPOSE", true);

        when(context.getString(R.string.val_true)).thenReturn("False");
        service.acceptedTermsOfUse(new Gson().toJson(getMockedFUserPreferences()), "USER_PREFERENCES_PURPOSE");
        verify(loginServiceGetListener).notifyGetSuccess("USER_PREFERENCES_PURPOSE", false);

        when(context.getString(R.string.val_false)).thenReturn("True");
        service.acceptedTermsOfUse(new Gson().toJson(getMockedFUserPreferences()), "USER_PREFERENCES_PURPOSE");
    }

    @Test
    public void notifyFetchError() {
        service.notifyFetchError(volleyError, "PURPOSE_USER_DATA");
        verify(loginServiceGetListener).notifyError(volleyErrorHandler.handleError(volleyError, context));
        service.notifyFetchError(volleyError, "USER_PREFERENCES_PURPOSE");
        service.notifyFetchError(volleyError, "PURPOSE_USER_PROFILE");
        service.notifyFetchError(volleyError, "PURPOSE_FACILITY_DATA");
        service.notifyFetchError(volleyError, "");
        verify(loginServiceGetListener, times(5)).notifyError(volleyErrorHandler.handleError(volleyError, context));
    }

    @Test
    public void resetLoginAttempts() {
        when(context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(context.getString(R.string.pref_incorrect_login_attempts)).thenReturn("com.myctca.myctca.INCORRECT_LOGIN_ATTEMPTS");
        when(sharedPreferences.edit().putInt(context.getString(R.string.pref_incorrect_login_attempts), 0)).thenReturn(editor);
        service.resetLoginAttempts(context);
        assertThat(sharedPreferences.getInt(context.getString(R.string.pref_incorrect_login_attempts), 0)).isEqualTo(0);
    }

    @Test
    public void successfulLogin() {
        when(context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(appSessionManager.getIdentityUser()).thenReturn(identityUser);
        when(appSessionManager.getIdentityUser().getEmail()).thenReturn("philly.scm@testctca.com");
        when(context.getString(R.string.pref_last_ctca_id)).thenReturn("com.myctca.myctca.LAST_CTCA_ID");
        when(sharedPreferences.edit().putString(context.getString(R.string.pref_last_ctca_id), appSessionManager.getIdentityUser().getEmail())).thenReturn(editor);
        when(context.getString(R.string.pref_successfully_logged_in)).thenReturn("com.myctca.myctca.SUCCESSFULLY_LOGGED_IN");
        when(sharedPreferences.edit().putBoolean(context.getString(R.string.pref_successfully_logged_in), true)).thenReturn(editor);
        when(appSessionManager.getIdleTimeout()).thenReturn(Long.valueOf(300000));
        resetLoginAttempts();
        service.successfulLogin(context);
        assertThat(appSessionManager.getIdleTimeout()).isEqualTo(300000);
    }

    @Test
    public void storePassword() {
        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 123);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(appSessionManager.getIdentityUser()).thenReturn(identityUser);
        when(appSessionManager.getIdentityUser().getPword()).thenReturn("abcde");
        service.storePassword("philly.scm@testctca.com", "password", false);
        assertThat(appSessionManager.getIdentityUser().getPword()).isEqualTo("abcde");

        service.storePassword("philly.scm@testctca.com", "password", true);
    }

    @Test
    public void getPrimaryService() {
        when(appSessionManager.getUserProfile()).thenReturn(userProfile);
        when(appSessionManager.getUserProfile().getPrimaryFacility()).thenReturn("facility");
        String primaryFacility = service.getPrimaryService();
        assertThat(primaryFacility).isEqualTo("facility");
    }

    @Test
    public void getUserType() {
        when(appSessionManager.getIdentityUser()).thenReturn(identityUser);
        when(appSessionManager.getIdentityUser().getUserType()).thenReturn(3);
        int userType = service.getUserType();
        assertThat(userType).isEqualTo(3);
    }
}