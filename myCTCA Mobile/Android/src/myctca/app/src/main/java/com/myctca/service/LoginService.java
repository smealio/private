package com.myctca.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.microsoft.appcenter.AppCenter;
import com.myctca.BuildConfig;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.fingerprintauth.FingerprintHandler;
import com.myctca.common.fingerprintauth.KeystoreHandler;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.interfaces.PostListener;
import com.myctca.model.AccessToken;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Facility;
import com.myctca.model.FacilityInfoAll;
import com.myctca.model.IdentityUser;
import com.myctca.model.LoginError;
import com.myctca.model.MedicalCenter;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.StoredPreferences;
import com.myctca.model.UserPreference;
import com.myctca.network.GetClient;
import com.myctca.network.PostClient;
import com.myctca.notification.RegistrationIntent;
import com.myctca.util.Constants;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LoginService implements GetListener, PostListener {

    private static final String TAG = LoginService.class.getSimpleName();
    private static final String USER_PREFERENCES_PURPOSE = "USER_PREFERENCES_PURPOSE";
    private static final String PURPOSE_USER_DATA = "PURPOSE_USER_DATA";
    private static final String PURPOSE_USER_PROFILE = "PURPOSE_USER_PROFILE";
    private static final String PURPOSE_FACILITY_DATA = "PURPOSE_FACILITY_DATA";
    private static final String PURPOSE_FACILITY_ALL = "PURPOSE_FACILITY_ALL";
    private static final String SIT_SURVEY_PURPOSE = "SIT_SURVEY_PURPOSE";
    private static LoginService loginService;
    private Context context;
    private LoginServicePostListener postListener;
    private LoginServiceGetListener getListener;

    public static LoginService getInstance() {
        if (loginService == null) {
            return new LoginService();
        }
        return loginService;
    }

    public StoredPreferences getStoredPreferences(Context context) {
        this.context = context;
        // Get Shared Preferences
        StoredPreferences storedPreferences = new StoredPreferences();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        // Get preferences
        boolean hasSuccessfullyLoggedIn = sharedPref.getBoolean(context.getString(R.string.pref_successfully_logged_in), false);
        storedPreferences.setPreviousUsername(sharedPref.getString(context.getString(R.string.pref_last_ctca_id), ""));
        storedPreferences.setHasPreviousUsername(!(storedPreferences.getPreviousUsername().equals("")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            storedPreferences.setFingerAuthEnabled(sharedPref.getBoolean(context.getString(R.string.pref_finger_auth_enabled), false));
            storedPreferences.setFingerAuthPrefSet(sharedPref.getBoolean(context.getString(R.string.pref_finger_auth_pref_set), false));
            storedPreferences.setFingerAuthSupported(osSupportsFingerAuthentication());
            Log.d(TAG, "isFingerAuthEnabled: " + storedPreferences.isFingerAuthEnabled());
            Log.d(TAG, "isFingerAuthPrefSet: " + storedPreferences.isFingerAuthPrefSet());
            Log.d(TAG, "isFingerAuthSupported: " + storedPreferences.isFingerAuthSupported());
        } else {
            storedPreferences.setFingerAuthEnabled(false);
            storedPreferences.setFingerAuthPrefSet(false);
            storedPreferences.setFingerAuthSupported(false);
        }
        Log.d(TAG, "hasSuccessfullyLoggedIn: " + hasSuccessfullyLoggedIn);
        Log.d(TAG, "previousUsername: " + storedPreferences.getPreviousUsername());
        Log.d(TAG, "hasPreviousUsername: " + storedPreferences.getHasPreviousUsername());
        return storedPreferences;
    }

    /**
     * Check if Android supports Fingerprint authentication (>= Marshmallow)
     */
    protected boolean osSupportsFingerAuthentication() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "SDK_INT: " + Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "Fingerprint Capable: " + FingerprintHandler.getInstance().isCapable());
                return FingerprintHandler.getInstance().isCapable();
            }
        }
        return false;
    }

    public void loginWithParameters(LoginServicePostListener listener, int task, Context context, String sUsername, String sPassword) {
        this.postListener = listener;
        this.context = context;

        final String url = BuildConfig.server_ctca_host + context.getString(R.string.endpoint_authorize);
        Log.d(TAG, "loginWithParameters url: " + url);
        Log.d(TAG, "loginWithParameters: " + sUsername + "::" + sPassword);

        Map<String, String> params = new HashMap<>();
        params.put("username", sUsername);
        params.put("password", sPassword);
        params.put("client_id", AppSessionManager.getInstance().getclientId());
        params.put("client_secret", AppSessionManager.getInstance().getclientSecret());
        params.put("grant_type", "password");
        params.put("scope", "openid profile email api external.identity.readwrite impersonation");

        PostClient postClient = new PostClient(this, context, task);
        postClient.getAuthToken(url, params);
    }

    public void getUserInfo(String url, Map<String, String> params, LoginServiceGetListener listener, Context context, String purpose) {
        this.getListener = listener;
        this.context = context;
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, params, purpose);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        try {
            AccessToken accessToken = new Gson().fromJson(response, AccessToken.class);
            AppSessionManager.getInstance().setCurrentUser(1);
            AppSessionManager.getInstance().setAccessToken(accessToken);
            Log.d(TAG, "AccessToken: " + accessToken.prettyPrint());
            postListener.notifyPostSuccess();
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception:" + exception);
            postListener.notifyPostError(context.getString(R.string.error_400));
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        final String url = BuildConfig.server_ctca_host + context.getString(R.string.endpoint_authorize);
        CTCAAnalyticsManager.createEvent("LoginService:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        CTCAAnalyticsManager.createEvent("LoginService:notifyPostError", CTCAAnalyticsConstants.LOGIN_REST_API_FAILURE, error, url);

        if (error instanceof TimeoutError) {
            CTCAAnalyticsManager.createEvent("LoginService:notifyPostError", CTCAAnalyticsConstants.LOGIN_TIMEOUT_ERROR, error, url);
        }

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        int wrongLoginAttempts = sharedPref.getInt(context.getString(R.string.pref_incorrect_login_attempts), 0);

        // Error handling
        String responseBody = "";
        if (error.networkResponse != null)
            responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
        try {
            LoginError loginError = new Gson().fromJson(responseBody, LoginError.class);
            if (loginError != null && error.networkResponse != null && !TextUtils.isEmpty(responseBody) && loginError.error != null && loginError.error.equals(context.getString(R.string.invalid_grant))) {
                if (wrongLoginAttempts == 2)
                    postListener.notifyPostError("");
                else
                    postListener.notifyPostError(loginError.error_description + " " + context.getString(R.string.error_append, AppSessionManager.getInstance().getTechnicalSupport()));
                wrongLoginAttempts++;
                sharedPref.edit().putInt(context.getString(R.string.pref_incorrect_login_attempts), wrongLoginAttempts).apply();

                if (loginError.error_description.contains(context.getString(R.string.login_incorrect_creds_message))) {
                    CTCAAnalyticsManager.createEvent("LoginService:notifyPostError", CTCAAnalyticsConstants.LOGIN_INCORRECT_CREDENTIALS, error, url);
                } else if (loginError.error_description.contains(context.getString(R.string.login_locked_account_message))) {
                    CTCAAnalyticsManager.createEvent("LoginService:notifyPostError", CTCAAnalyticsConstants.LOGIN_LOCKED_ACCOUNT, error, url);
                }

            } else {
                postListener.notifyPostError(VolleyErrorHandler.handleError(error, context));
            }
        } catch (JsonParseException e) {
            postListener.notifyPostError(VolleyErrorHandler.handleError(error, context));
        }
        Log.d(TAG, "Something went wrong while trying to get Access Token! Error: " + error.toString() + "::" + error.getLocalizedMessage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintHandler.getInstance().resetFingerprintAuthorization();
            KeystoreHandler.getInstance().resetKeystorePreference();
        }
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        Log.d(TAG, parseSuccess);
        switch (purpose) {
            case PURPOSE_USER_DATA:
                IdentityUser identityUser;
                try {
                    identityUser = new Gson().fromJson(parseSuccess, IdentityUser.class);
                    AppSessionManager.getInstance().setmIdentityUser(identityUser);
                    Log.d(TAG, "LoginActivity retrieveUserData DONE");
                    getListener.notifyGetSuccess(purpose, false);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception: " + exception);
                    getListener.notifyError(context.getString(R.string.error_400));
                }
                break;
            case PURPOSE_USER_PROFILE:
                MyCTCAUserProfile userProfile;
                try {
                    userProfile = new Gson().fromJson(parseSuccess, MyCTCAUserProfile.class);
                    AppSessionManager.getInstance().setmUserProfile(userProfile);
                    Log.d(TAG, "LoginService retrieveUserProfile DONE");
                    getListener.notifyGetSuccess(purpose, false);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception: " + exception);
                    getListener.notifyError(context.getString(R.string.error_400));
                }
                break;
            case USER_PREFERENCES_PURPOSE:
                acceptedTermsOfUse(parseSuccess, purpose);
                Log.d(TAG, "LoginService retrieveUserPreferences DONE");
                break;
            case PURPOSE_FACILITY_DATA:
                Facility[] facilities;
                try {
                    facilities = new Gson().fromJson(parseSuccess, Facility[].class);
                    if (facilities.length > 0)
                        AppSessionManager.getInstance().setmPreferredFacility(facilities[0]);
                    getListener.notifyGetSuccess(purpose, false);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception: " + exception);
                    getListener.notifyError(context.getString(R.string.error_400));
                }
                break;
            case PURPOSE_FACILITY_ALL:
                try {
                    FacilityInfoAll[] facilityInfoAlls = new Gson().fromJson(parseSuccess, FacilityInfoAll[].class);
                    AppSessionManager.getInstance().setFacilityInfoAllList(new LinkedList<>(Arrays.asList(facilityInfoAlls)));
                    List<MedicalCenter> facilityList = new ArrayList<>();
                    for (FacilityInfoAll facility : facilityInfoAlls) {
                        facilityList.add(new MedicalCenter(facility.name, facility.displayName));
                    }
                    AppSessionManager.getInstance().setFacilityAll(facilityList);
                    getListener.notifyGetSuccess(purpose, false);
                } catch (JsonParseException exception) {
                    Log.e(TAG, "exception: " + exception);
                    getListener.notifyError(context.getString(R.string.error_400));
                }
                break;
            default:
                //do nothing
        }
    }

    protected void acceptedTermsOfUse(String parseSuccess, String purpose) {
        boolean acceptedTermsOfUse = false;
        try {
            UserPreference[] userPreferences = new Gson().fromJson(parseSuccess, UserPreference[].class);
            for (UserPreference userPreference : userPreferences) {
                switch (userPreference.getUserPreferenceType()) {
                    case "AcceptedTermsOfUse":
                        acceptedTermsOfUse = userPreference.getUserPreferenceValue().equals(context.getString(R.string.val_true));
                        break;
                    case "AppointmentsDays":
                        AppSessionManager.getInstance().setAppointmentsDays(userPreference);
                        break;
                    case "AcceptedDiagnosticReporting":
                        if (userPreference.getUserPreferenceValue().equals(context.getString(R.string.val_true))) {
                            AppCenter.setEnabled(true);
                            MyCTCA.enableDiagnosticReporting(true);
                            Log.d(TAG, "Analytics Enabled");
                        } else if (userPreference.getUserPreferenceValue().equals(context.getString(R.string.val_false))) {
                            AppCenter.setEnabled(true);
                            MyCTCA.enableDiagnosticReporting(false);
                            Log.d(TAG, "Analytics Disabled");
                        } else {
                            AppSessionManager.getInstance().setShowDiagnosticResportingDialog(true);
                            Log.d(TAG, "Analytics Unspecified");
                        }
                        break;
                }
            }
            getListener.notifyGetSuccess(purpose, acceptedTermsOfUse);
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception: " + exception);
            getListener.notifyError(context.getString(R.string.error_400));
        }
    }


    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        String url = "";
        switch (purpose) {
            case PURPOSE_USER_DATA:
                url = BuildConfig.server_ctca_host + context.getString(R.string.endpoint_user_info);
                break;
            case USER_PREFERENCES_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_user_preferences);
                break;
            case PURPOSE_USER_PROFILE:
                url = BuildConfig.myctca_server + context.getString(R.string.endpoint_user_profile);
                break;
            case PURPOSE_FACILITY_DATA:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_facility);
                break;
            default:
                url = "";
        }
        CTCAAnalyticsManager.createEvent("LoginService:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        CTCAAnalyticsManager.createEvent("LoginService:notifyFetchError", CTCAAnalyticsConstants.LOGIN_REST_API_FAILURE, error, url);
        Log.e(TAG, "error:" + error);
        getListener.notifyError(VolleyErrorHandler.handleError(error, context));
    }

    public void resetLoginAttempts(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putInt(context.getString(R.string.pref_incorrect_login_attempts), 0).apply();
    }

    public void successfulLogin(Context context) {
        // Get Application Context from Application
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        // Store successful Username in Preferences and that user successfully logged in
        sharedPref.edit().putString(context.getString(R.string.pref_last_ctca_id), AppSessionManager.getInstance().getIdentityUser().getEmail()).apply();
        Log.d(TAG, "successfulLogin key: " + context.getString(R.string.pref_last_ctca_id) + " ::: value: " + AppSessionManager.getInstance().getIdentityUser().getEmail());
        sharedPref.edit().putBoolean(context.getString(R.string.pref_successfully_logged_in), true).apply();
        // Begin the current Session
        AppSessionManager.getInstance().setIdleTimeout(Constants.SESSION_EXPIRY);
        AppSessionManager.getInstance().setTokenRefreshInterval(35700);
        AppSessionManager.getInstance().beginCurrentSession();
        // Register device for notifications
        Intent intent = new Intent(context, RegistrationIntent.class);
        context.startService(intent);
        resetLoginAttempts(context);
    }

    public void storePassword(String username, String password, boolean isFingerAuthEnabled) {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            // Store Password in IdentityUser object
            AppSessionManager.getInstance().getIdentityUser().setPword(password);
            if (isFingerAuthEnabled) {
                KeystoreHandler.getInstance().encryptPassword(username, password);
            }
        }
    }

    public Facility getPreferredFacility() {
        return AppSessionManager.getInstance().getPreferredFacility();
    }

    public String getPrimaryService() {
        return AppSessionManager.getInstance().getUserProfile().getPrimaryFacility();
    }

    public int getUserType() {
        return AppSessionManager.getInstance().getIdentityUser().getUserType();
    }

    public void getAllFacilities(String url, LoginServiceGetListener listener, Context context, String purpose) {
        this.getListener = listener;
        this.context = context;
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, null, purpose);
    }

    public interface LoginServicePostListener {
        void notifyPostSuccess();

        void notifyPostError(String message);
    }

    public interface LoginServiceGetListener {
        void notifyGetSuccess(String purpose, boolean isTermsOfUseAccepted);

        void notifyError(String message);
    }
}
