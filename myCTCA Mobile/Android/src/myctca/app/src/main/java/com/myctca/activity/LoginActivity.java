package com.myctca.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.fingerprintauth.FingerprintHandler;
import com.myctca.common.fingerprintauth.KeystoreHandler;
import com.myctca.fragment.CallTechSupportFragment;
import com.myctca.fragment.ContactSupportDialogFragment;
import com.myctca.fragment.CreateAccountDialogFragment;
import com.myctca.fragment.TermsAndConditionsFragment;
import com.myctca.interfaces.FingerprintAuthListener;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MyCTCATask;
import com.myctca.model.StoredPreferences;
import com.myctca.service.LoginService;
import com.myctca.service.SessionFacade;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * LoginActivity is the one activity that has no corresponding fragment because it will never be in a Master/Detail situation
 */

public class LoginActivity extends MyCTCAActivity
        implements CreateAccountDialogFragment.CreateAccountDialogListener,
        ContactSupportDialogFragment.ContactSupportDialogListener, FingerprintAuthListener,
        LoginService.LoginServicePostListener, LoginService.LoginServiceGetListener {

    private static final String TAG = "myCTCA-Login";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //Purposes
    private static final String USER_PREFERENCES_PURPOSE = "USER_PREFERENCES_PURPOSE";
    private static final String DEEP_LINKING = "DEEP_LINKING";
    private static final String PURPOSE_USER_DATA = "PURPOSE_USER_DATA";
    private static final String PURPOSE_USER_PROFILE = "PURPOSE_USER_PROFILE";
    private static final String PURPOSE_FACILITY_DATA = "PURPOSE_FACILITY_DATA";
    private static final String PURPOSE_FACILITY_ALL = "PURPOSE_FACILITY_ALL";

    private TextView mUsernameLabel;
    private TextView mPasswordLabel;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private ImageButton mTogglePasswordButton;
    private ImageButton mFingerprintAuthButton;
    private boolean passwordIsSecure = false;
    private AlertDialog fingerprintDialog;
    private boolean deepLinking;

    private Button mForgotPasswordButton;
    private Button mCreateAccountButton;
    private Button mContactSupportButton;
    private Button mSignInButton;

    private SessionFacade sessionFacade;
    private int count = 0;
    private boolean isTermsOfUseAccepted;
    private String username;
    private String password;
    private StoredPreferences storedPreferences;
    private boolean blockLogin;
    private CallTechSupportFragment ctDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("LoginActivity:onCreate", CTCAAnalyticsConstants.PAGE_SIGNIN_VIEW));
        getStoredPreferences();
        prepareView();

        if (checkPlayServices()) {
            AppSessionManager.getInstance().setFCMToken(FirebaseInstanceId.getInstance().getToken());
            Log.d(TAG, "Got Play Services: Token: " + AppSessionManager.getInstance().getFCMToken());
        } else {
            Log.d(TAG, "Ain't got no Play Services");
        }
        deepLinking = getIntent().getBooleanExtra(DEEP_LINKING, deepLinking);
        Log.d(TAG, "Deep Linking:" + deepLinking);
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
//        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAppDown();
    }

    private void checkAppDown() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
        calendar.set(2021, 3, 8, 0, 0, 0);
        Date initialDate = calendar.getTime();
        calendar.set(2021, 3, 10, 8, 0, 0);
        Date startDate = calendar.getTime();
        calendar.set(2021, 3, 10, 16, 0, 0);
        Date endDate = calendar.getTime();
        if (today.after(initialDate) && today.before(startDate)) {
            blockLogin = false;
            showAppDownWarningAlert(getString(R.string.login_app_maintenance_warning_title), getString(R.string.login_app_maintenance_warning_message), true);
        } else if (today.before(initialDate) || today.after(endDate)) {
            //do nothing
        } else {
            blockLogin = true;
            stopAuthentication();
            showAppDownWarningAlert(getString(R.string.login_app_maintenance_down_title), getString(R.string.login_app_maintenance_down_message), false);
        }
    }

    private void showAppDownWarningAlert(String title, String message, boolean showPosBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(title)
                .setMessage(message);
        if (showPosBtn) {
            builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
        }
        builder.setCancelable(false);
        if (!isFinishing())
            builder.show();
    }

    private void getStoredPreferences() {
        storedPreferences = sessionFacade.getStoredPreferences(this);
    }

    private void prepareView() {
        mForgotPasswordButton = findViewById(R.id.forgot_password_button);
        mCreateAccountButton = findViewById(R.id.create_account_button);
        mContactSupportButton = findViewById(R.id.contact_support_button);
        mSignInButton = findViewById(R.id.signin_button);

        mUsernameLabel = findViewById(R.id.ctca_id_label);
        mPasswordLabel = findViewById(R.id.password_label);

        mUsernameEditText = findViewById(R.id.ctca_id_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);

        mTogglePasswordButton = findViewById(R.id.toggle_password_button);

        mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mFingerprintAuthButton = findViewById(R.id.finger_auth_button);

        handleButtonClickListeners();
        prepareTextFields();
    }

    private void handleButtonClickListeners() {

        mForgotPasswordButton.setOnClickListener(v -> {
            Log.d(TAG, "HELLO...Forgot Password");
            doForgotPassword();
        });

        mCreateAccountButton.setOnClickListener(v -> {
            Log.d(TAG, "HELLO...CTCA Create Account");
            doCreateAccount();
        });

        mContactSupportButton.setOnClickListener(v -> {
            Log.d(TAG, "HELLO...Contact Support");
            doContactSupport();
        });

        mSignInButton.setOnClickListener(v -> {
            Log.d(TAG, "SignInClicked");
            doSignIn();
        });

        mTogglePasswordButton.setOnClickListener(v -> {
            Log.d(TAG, "HELLO...Toggle Password");
            doTogglePassword();
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (FingerprintHandler.getInstance() != null && FingerprintHandler.getInstance().isCapable()) {
                // Fingerprint Authentication button
                if (storedPreferences.getHasPreviousUsername() && storedPreferences.isFingerAuthEnabled()
                        && storedPreferences.isFingerAuthPrefSet() && storedPreferences.isFingerAuthSupported()) {
                    Log.d(TAG, "mFingerprintAuthButton everything is true");
                    mFingerprintAuthButton.setVisibility(View.VISIBLE);
                    mFingerprintAuthButton.setOnClickListener(v -> doFingerAuth());
                } else {
                    Log.d(TAG, "mFingerprintAuthButton should be gone");
                    mFingerprintAuthButton.setVisibility(View.GONE);
                }
                if (storedPreferences.getHasPreviousUsername() && storedPreferences.isFingerAuthEnabled()
                        && storedPreferences.isFingerAuthPrefSet() && storedPreferences.isFingerAuthSupported()) {
                    doFingerAuth();
                }
            } else {
                Log.d(TAG, "mFingerprintAuthButton !FingerprintHandler.getInstance().isCapable()");
                mFingerprintAuthButton.setVisibility(View.INVISIBLE);
                mFingerprintAuthButton.setImageAlpha(0);
            }
        } else {
            Log.d(TAG, "mFingerprintAuthButton Build.VERSION.SDK_INT < 23");
            mFingerprintAuthButton.setVisibility(View.GONE);
        }
    }

    private void prepareTextFields() {
        // Username (CTCA ID)
        mUsernameLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Username");
            mUsernameEditText.setFocusableInTouchMode(true);
            mUsernameEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(mUsernameEditText, InputMethodManager.SHOW_IMPLICIT);
        });

        // User Name Field
        mUsernameEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                doSignIn();
                handled = true;
            }
            return handled;
        });
        mUsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not required
            }

            @Override
            public void afterTextChanged(Editable s) {
                //not required
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String currentUsername = mUsernameEditText.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mFingerprintAuthButton != null) {
                    if (currentUsername.equals(storedPreferences.getPreviousUsername()) && storedPreferences.isFingerAuthEnabled()
                            && storedPreferences.isFingerAuthPrefSet() && storedPreferences.isFingerAuthSupported()) {
                        mFingerprintAuthButton.setVisibility(View.VISIBLE);
                    } else {
                        mFingerprintAuthButton.setVisibility(View.GONE);
                    }
                }
            }
        });

        if (!storedPreferences.getPreviousUsername().equals("")) {
            mUsernameEditText.setText(storedPreferences.getPreviousUsername());
        }

        // Password Field
        mPasswordLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Username");
            mPasswordEditText.setFocusableInTouchMode(true);
            mPasswordEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(mPasswordEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                doSignIn();
                handled = true;
            }
            return handled;
        });
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                stopAuthentication();
            }
        });
    }

    private void doTogglePassword() {
        if (this.passwordIsSecure) {
            this.passwordIsSecure = false;
            mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mTogglePasswordButton.setImageResource(R.drawable.eye_slash);
        } else {
            this.passwordIsSecure = true;
            mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mTogglePasswordButton.setImageResource(R.drawable.eye);
        }
        mPasswordEditText.setSelection(mPasswordEditText.getText().length());
    }

    private void doSignIn() {
        stopAuthentication();
        // Validate Form
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            doSignInIncompleteError();
        } else {
            Log.d(TAG, "Sign in clicked by user: " + mUsernameEditText.getText());
            this.username = username;
            this.password = password;
            showActivityIndicator("Authenticating...");
            loginWithParameters(username, password);
        }
    }

    private void stopAuthentication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintHandler.getInstance().stopAuth();
        }
    }

    private void doForgotPassword() {
        Intent myIntent = new Intent(LoginActivity.this, DisplayWebViewActivity.class);
        myIntent.putExtra("type", getString(R.string.login_forgot_password_title_text));
        myIntent.putExtra("url", BuildConfig.server_ctca_host + getString(R.string.endpoint_reset_password));
        startActivity(myIntent);
    }

    private void doCreateAccount() {
        FragmentManager manager = getSupportFragmentManager();
        CreateAccountDialogFragment dialog = new CreateAccountDialogFragment();
        dialog.show(manager, "");
    }

    private void doContactSupport() {
        FragmentManager manager = getSupportFragmentManager();
        ContactSupportDialogFragment dialog = ContactSupportDialogFragment.newInstance(this);
        dialog.show(manager, "");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void doFingerAuth() {

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_fingerprint_black_48dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorPrimary));


        String username = mUsernameEditText.getText().toString();

        if (fingerprintDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this).
                    setTitle("Fingerprint Authentication").
                    setMessage("Please place your finger on the scanner to verify your identity, " + username).
                    setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss()).
                    setIcon(drawable);
            fingerprintDialog = builder.create();
        }
        if (!isFinishing())
            fingerprintDialog.show();
        FingerprintHandler.getInstance().beginAuthorization(this);
    }

    // CreateAccountDialogListener
    public void onTermsClick(DialogFragment dialog) {
        Intent myIntent = new Intent(LoginActivity.this, DisplayWebViewActivity.class);
        myIntent.putExtra("fragment", TermsAndConditionsFragment.class.getSimpleName()); //Optional parameters
        myIntent.putExtra("showTermsOfUseBottomView", false);
        startActivity(myIntent);
    }

    public void onPrivacyClick(DialogFragment dialog) {
        Intent myIntent = new Intent(LoginActivity.this, DisplayWebViewActivity.class);
        String privacyUrlStr = BuildConfig.myctca_server + getString(R.string.link_privacy_policy);
        myIntent.putExtra("type", getString(R.string.create_account_privacy));
        myIntent.putExtra("url", privacyUrlStr);
        startActivity(myIntent);
    }

    public void onCreateAccount(DialogFragment dialog) {
        Intent myIntent = new Intent(LoginActivity.this, DisplayWebViewActivity.class);
        myIntent.putExtra("type", getString(R.string.login_create_account_button));
        String ctcaHost = BuildConfig.server_ctca_host;
        String registrationEndpoint = getString(R.string.endpoint_registration);
        String registrationdURL = ctcaHost + registrationEndpoint;
        myIntent.putExtra("url", registrationdURL);
        startActivity(myIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppSessionManager.getInstance().getpermissionsRequestCallPhone() && ctDialog != null)
            ctDialog.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // ContactSupportDialogListener
    public void onCall(DialogFragment dialog) {
        FragmentManager manager = getSupportFragmentManager();
        ctDialog = CallTechSupportFragment.newInstance(getApplicationContext());
        ctDialog.show(manager, "");
    }

    public void onSendMessage(DialogFragment dialog) {
        Intent sendMsgIntent = SendMessageActivity.newIntent(this);
        startActivity(sendMsgIntent);
    }

    public void loginWithParameters(String username, String password) {
        // Show ProgressBar and Disable Activity
        if (!username.equals(storedPreferences.getPreviousUsername()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintHandler.getInstance().resetFingerprintAuthorization();
            KeystoreHandler.getInstance().resetKeystorePreference();
            Context context = this;
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            sharedPref.edit().putBoolean(getString(R.string.pref_successfully_logged_in), false).apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintDialog != null) {
            fingerprintDialog.dismiss();
        }
        sessionFacade.loginWithParameters(this, MyCTCATask.TOKEN, this, username, password);
    }

    private void retrieveUserData() {
        String url = BuildConfig.server_ctca_host + getString(R.string.endpoint_user_info);
        Map<String, String> params = new HashMap<>();
        params.put("schema", "openid");
        sessionFacade.getUserInfo(url, params, this, this, PURPOSE_USER_DATA);
    }

    private void retrieveUserProfile() {
        String url = BuildConfig.myctca_server + getString(R.string.endpoint_user_profile);
        sessionFacade.getUserInfo(url, null, this, this, PURPOSE_USER_PROFILE);
    }

    private void retrieveAllFacilities() {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_get_facility_all);
        sessionFacade.getAllFacilities(url, this, this, PURPOSE_FACILITY_ALL);
    }

    private void retrieveFacilityData() {
        // ActivityIndicator
        Map<String, String> params = new HashMap<>();
        params.put("facility", sessionFacade.getPrimaryFacility());
        String url = BuildConfig.myctca_server + getString(R.string.myctca_get_facility);
        sessionFacade.getUserInfo(url, params, this, this, PURPOSE_FACILITY_DATA);
    }

    private void getUserPreferences() {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_get_user_preferences);
        sessionFacade.getUserInfo(url, null, this, this, USER_PREFERENCES_PURPOSE);
    }

    private void successfulLogin() {
        sessionFacade.successfulLogin(this);
        sessionFacade.storePassword(mUsernameEditText.getText().toString(), password, storedPreferences.isFingerAuthEnabled());

        // Dismiss keyboard
        dismissKeyboard();

        if (isTermsOfUseAccepted) {
            Intent navIntent = NavActivity.newIntent(this, username, password, deepLinking);
            startActivity(navIntent);
            finish();
        } else {
            //Accept Terms of Use
            Intent myIntent = new Intent(LoginActivity.this, DisplayWebViewActivity.class);
            myIntent.putExtra("fragment", TermsAndConditionsFragment.class.getSimpleName()); //Optional parameters
            myIntent.putExtra("showTermsOfUseBottomView", true); //Optional parameters
            startActivity(myIntent);
        }
    }

    public void callTechSupport() {
        String techPhoneNumber = "tel:" + AppSessionManager.getInstance().getTechnicalSupport();
        Log.d(TAG, "techPhoneNumber: " + techPhoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(techPhoneNumber));
        Log.d(TAG, "OK TO CALL: " + techPhoneNumber);
        startActivity(callIntent);
    }

    private void showTokenErrorDialog(String title, String errorMsg) {
        CTCAAnalyticsManager.createEvent("LoginActivity:showTokenErrorDialog", CTCAAnalyticsConstants.ALERT_SIGNIN_FAIL, null, null);
        Log.d(TAG, "VolleyErrorHandler: " + errorMsg);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(title)
                .setMessage(errorMsg)
                .setNegativeButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    View view = findViewById(R.id.login_form_scroll);
                    if (view != null) {
                        view.refreshDrawableState();
                    }
                });
        if (!isFinishing())
            builder.show();
    }

    private void doSignInIncompleteError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.error_sign_in_incomplete_title)
                .setMessage(R.string.error_sign_in_incomplete_msg)
                .setNegativeButton("OK", (dialog, which) -> dialog.cancel());
        if (!isFinishing())
            builder.show();
    }

    //get user preferences
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        Log.d("RegisterDevice", "checkPlayServices resultCode: " + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d("RegisterDevice", "This device is not supported by Google Play Services.");
                toastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void toastNotify(final String notificationMessage) {
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, notificationMessage, Toast.LENGTH_LONG).show());
    }

    public AlertDialog getDialog() {
        return fingerprintDialog;
    }

    public void dismissKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        View focusedView = getCurrentFocus();
        if (focusedView != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    // FingerprintAuthListeners
    @TargetApi(Build.VERSION_CODES.M)
    public void fingerprintAuthSucceeded() {
        String uName = mUsernameEditText.getText().toString();
        Log.d(TAG, "LoginActivity fingerprintAuthSucceeded uName: " + uName);
        String pWord = null;
        try {
            pWord = KeystoreHandler.getInstance().decryptPassword(uName);
        } catch (NullPointerException np) {
            pWord = null;
        }

        if (!uName.equals("") && pWord != null) {
            this.username = uName;
            this.password = pWord;
            showActivityIndicator("Authenticating...");
            loginWithParameters(uName, pWord);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);

            builder.setTitle(getString(R.string.fingerprint_auth_error_title))
                    .setMessage(getString(R.string.fingerprint_auth_error))
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(R.drawable.ic_warning_white_24dp);
            if (!isFinishing())
                builder.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void fingerprintAuthError(int errMsgId, CharSequence errString) {
        if (!blockLogin) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);

            builder.setTitle(getString(R.string.fingerprint_auth_error_title))
                    .setMessage(getString(R.string.fingerprint_auth_error))
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(R.drawable.ic_warning_white_24dp);
            if (!isFinishing())
                builder.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void fingerprintAuthFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle("Fingerprint Authentication Error")
                .setMessage("There was an error while performing fingerprint authorization. The fingerprint did not match with any of the fingerprints registered on the device.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setIcon(R.drawable.ic_warning_white_24dp);
        if (!isFinishing())
            builder.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void fingerprintAuthHelp(int helpMsgId, String helpString) {
        Toast.makeText(this, "Fingerprint help\n" + helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void notifyPostSuccess() {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        retrieveUserData();
        retrieveUserProfile();
        getUserPreferences();
    }

    @Override
    public void notifyPostError(String message) {
        hideActivityIndicator();
        if (message.isEmpty()) {
            showTokenErrorDialog(getString(R.string.login_lock_account_title), getString(R.string.login_lock_account_message));
        } else {
            showTokenErrorDialog(getString(R.string.error_sign_in_incomplete_title), message);
        }
    }

    @Override
    public void notifyGetSuccess(String purpose, boolean isTermsOfUseAccepted) {
        switch (purpose) {
            case PURPOSE_USER_DATA:
            case PURPOSE_FACILITY_ALL:
            case PURPOSE_FACILITY_DATA:
                count++;
                break;
            case PURPOSE_USER_PROFILE:
                count++;
                retrieveAllFacilities();
                retrieveFacilityData();
                break;
            case USER_PREFERENCES_PURPOSE:
                this.isTermsOfUseAccepted = isTermsOfUseAccepted;
                count++;
                break;
            default:
                //do nothing
        }
        if (count == 5) {
            hideActivityIndicator();
            successfulLogin();
        }
    }

    @Override
    public void notifyError(String message) {
        hideActivityIndicator();
        showTokenErrorDialog(getString(R.string.error_sign_in_incomplete_title), message);
    }
}
