package com.myctca.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.microsoft.appcenter.AppCenter;
import com.myctca.BuildConfig;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.fingerprintauth.FingerprintHandler;
import com.myctca.common.fingerprintauth.KeystoreHandler;
import com.myctca.fragment.HomeFragment;
import com.myctca.fragment.LabsFragment;
import com.myctca.fragment.MailInboxFragment;
import com.myctca.fragment.MoreFragment;
import com.myctca.fragment.appointmment.AppointmentFragment;
import com.myctca.interfaces.PostListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Mail;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.MyCTCATask;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.StoredPreferences;
import com.myctca.model.UserPermissions;
import com.myctca.model.UserPreference;
import com.myctca.model.UserType;
import com.myctca.network.PostClient;
import com.myctca.service.SessionFacade;
import com.myctca.util.Constants;
import com.myctca.util.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class NavActivity extends MyCTCAActivity implements
        PostListener, MoreFragment.MoreFragmentInteractionListener {

    public static final String USER_NAME = "username";
    public static final String PASS_WORD = "password";
    private static final String TAG = "myCTCA-NAV";
    private static final String USER_DIAGNOSTIC_REPORTING = "AcceptedDiagnosticReporting";
    private static final String USER_ACCEPT_TERMS_OF_USE = "AcceptedTermsOfUse";
    private static final String USER_APPT_DAYS = "AppointmentsDays";
    private static final String DEEP_LINKING = "DEEP_LINKING";
    //Purposes
    private String username;
    private String password;
    private String userPreferenceValue;
    private boolean deepLinking;
    private BottomNavigationView bottomNavigationView;
    private SessionFacade sessionFacade;
    private String caregiverAccessingPatientName = "";

    public static Intent newIntent(Context packageContext, String username, String password, boolean deepLinking) {
        Log.d(TAG, "newIntent: " + username + ":" + password);
        Intent intent = new Intent(packageContext, NavActivity.class);
        intent.putExtra(USER_NAME, username);
        intent.putExtra(PASS_WORD, password);
        intent.putExtra(DEEP_LINKING, deepLinking);
        return intent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        deepLinking = intent.getData() != null && intent.getData().getPathSegments().get(1).equals(Constants.FORMS_LIBRARY_DEEP_LINK);
        AppSessionManager.getInstance().setDeepLinking(deepLinking);
        if (deepLinking && !AppSessionManager.getInstance().isSessionExpired() && sessionFacade.getUserType() == UserType.PATIENT) {
            // Go to Forms Library activity
            intent = MoreFormsLibraryActivity.newIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionFacade = new SessionFacade();
        deepLinking = getIntent().getData() != null &&
                getIntent().getData().getPathSegments().get(1).equals(Constants.FORMS_LIBRARY_DEEP_LINK);
        AppSessionManager.getInstance().setDeepLinking(deepLinking);
        setContentView(R.layout.activity_nav);
        //login successful
        AppSessionManager.getInstance().setSuccessfullyLoggedIn(true);
        //enable/disable analytics reporting
        if (BuildConfig.enable_diagnostic_reporting && AppSessionManager.getInstance().showDiagnosticResportingDialog()) {
            promptForDiagnosticReporting();
        }
        getIntentExtras();
        prepareView();
        Log.d(TAG, "NAV onCreate: " + savedInstanceState);
        if (null == savedInstanceState) {
            //Manually displaying the first fragment - one time only
            selectedFragment = HomeFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, selectedFragment, selectedFragment.getTag());
            transaction.commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (MyCTCA.getCurrentActivity() instanceof NavActivity) {
            sessionFacade.cancelRequest();
            hideActivityIndicator();
        }
    }

    public String getCaregiverAccessingPatientName() {
        return caregiverAccessingPatientName;
    }

    private void getIntentExtras() {
        // Get Extras
        username = getIntent().getStringExtra(USER_NAME);
        password = getIntent().getStringExtra(PASS_WORD);
        deepLinking = getIntent().getBooleanExtra(DEEP_LINKING, false);
        AppSessionManager.getInstance().setDeepLinking(deepLinking);
        if (deepLinking && sessionFacade.getUserType() == UserType.PATIENT) {
            // Go to Forms Library activity
            Intent intent = MoreFormsLibraryActivity.newIntent(this);
            startActivity(intent);
        }
    }

    private void prepareView() {
        bottomNavigationView = findViewById(R.id.bottomNavBar);
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        if (sessionFacade.getUserType() == UserType.CAREGIVER && isCaregiverImpersonating())
            bottomNavigationView.setVisibility(View.GONE);
        handleBottomNavigation();
    }

    public boolean isCaregiverImpersonating() {
        List<MyCTCAProxy> proxies = sessionFacade.getProxies();
        boolean isImpersonating = false;
        for (MyCTCAProxy proxy : proxies) {
            if (sessionFacade.getMyCtcaUserProfile().getCtcaId().equals(proxy.getToCtcaUniqueId())) {
                isImpersonating = proxy.isImpersonating();
                if (isImpersonating)
                    caregiverAccessingPatientName = proxy.getFullName();
            } else {
                if (proxy.isImpersonating()) {
                    caregiverAccessingPatientName = proxy.getFullName();
                }
            }
        }
        return isImpersonating;
    }

    public void showPermissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(getString(R.string.permisson_not_granted_title))
                .setMessage(getString(R.string.permisson_not_granted_message_proxy))
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    //do nothing
                }).create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary)));
        if (!isFinishing())
            dialog.show();
    }

    private void handleBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener
                (item -> {
                    int flag = 0;
                    MyCTCAUserProfile user = AppSessionManager.getInstance().getUserProfile();
                    switch (item.getItemId()) {
                        case R.id.menu_item_1:
                            if (user.userCan(UserPermissions.VIEW_APPOINTMENTS)) {
                                flag = 1;
                                selectedFragment = new AppointmentFragment();
                            } else {
                                showPermissionAlert();
                                bottomNavigationView.getMenu().getItem(1).setCheckable(false);
                                return false;
                            }
                            break;
                        case R.id.menu_item_2:
                            if (user.userCan(UserPermissions.VIEW_LAB_RESULTS)) {
                                flag = 1;
                                selectedFragment = LabsFragment.newInstance();
                            } else {
                                showPermissionAlert();
                                bottomNavigationView.getMenu().getItem(2).setCheckable(false);
                                return false;
                            }
                            break;
                        case R.id.menu_item_3:
                            if (user.userCan(UserPermissions.VIEW_SECURE_MESSAGES)) {
                                flag = 1;
                                selectedFragment = MailInboxFragment.newInstance();
                            } else {
                                showPermissionAlert();
                                bottomNavigationView.getMenu().getItem(3).setCheckable(false);
                                return false;
                            }
                            break;
                        case R.id.menu_item_4:
                            flag = 1;
                            selectedFragment = MoreFragment.newInstance();
                            break;
                        default:
                            flag = 1;
                            selectedFragment = HomeFragment.newInstance();
                            break;
                    }
                    if (flag == 1) {
                        fragmentName = selectedFragment.getClass().getSimpleName();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, selectedFragment, selectedFragment.getTag());
                        transaction.commit();
                    }
                    return true;
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set toolbar
        toolbar = findViewById(R.id.toolbar_nav);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.toolbar_appts_download) {
            if (AppSessionManager.getInstance().getAppointments().isEmpty()) {
                showAlertDialog(getString(R.string.appt_download_schedule_title), getString(R.string.no_records_found_message));
            } else {
                Intent intent = new Intent(this, AppointmentDownloadScheduleActivity.class);
                startActivity(intent);
            }
            return true;
        }
        if (id == R.id.item_archived_mail) {
            Log.d(TAG, "Archived Mail selected");
            Intent archiveMailIntent = new Intent(MailArchivedActivity.newIntent(this));
            startActivity(archiveMailIntent);
            return true;
        }
        if (id == R.id.item_sent_mail) {
            Log.d(TAG, "Sent Mail selected");
            Intent sentMailIntent = new Intent(MailSentActivity.newIntent(this));
            startActivity(sentMailIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setTitle(title).
                        setMessage(message).
                        setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
        if (!isFinishing())
            builder.create().show();
    }

    public void presentationReady() {
        hideActivityIndicator();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            promptForFingerAuth();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void promptForFingerAuth() {
        StoredPreferences storedPreferences = sessionFacade.getStoredPreferences(this);
        boolean fingerAuthCapable = storedPreferences.isFingerAuthSupported();
        boolean hasSuccessfullyLoggedIn = AppSessionManager.getInstance().isSuccessfullyLoggedIn();
        boolean isFingerAuthPrefSet = storedPreferences.isFingerAuthPrefSet();
        Log.d(TAG, "promptForFingerAuth fingerAuthCapable: " + fingerAuthCapable + " ::: hasSuccessfullyLoggedIn: " + hasSuccessfullyLoggedIn);
        if (fingerAuthCapable && hasSuccessfullyLoggedIn && !isFingerAuthPrefSet) {
            Log.d(TAG, "Show Finger ALERT");
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_fingerprint_black_48dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorPrimary));
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this).
                            setTitle(getString(R.string.fingerprint_auth_alert_title)).
                            setMessage(getString(R.string.fingerprint_auth_alert_message) + ", " + AppSessionManager.getInstance().getUserProfile().getEmailAddress() + "?").
                            setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                enableFingerprintAuthorization();
                            }).
                            setNeutralButton("Learn More", (dialog, which) -> learnAboutFingerprintAuthorization()).
                            setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                                disableFingerprintAuthorization();
                            }).
                            setIcon(drawable);
            builder.create().show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void enableFingerprintAuthorization() {
        FingerprintHandler.getInstance().enableFingerprintAuthorization();
        Log.d(TAG, "enableFingerprintAuthorization username: " + username);
        KeystoreHandler.getInstance().encryptPassword(username, password);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_fingerprint_black_48dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorPrimary));
        showAlertDialog(getString(R.string.fingerprint_auth_alert_enabled_title), getString(R.string.fingerprint_auth_alert_enabled_message));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void disableFingerprintAuthorization() {
        FingerprintHandler.getInstance().disableFingerprintAuthorization();

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_fingerprint_black_48dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorPrimary));
        showAlertDialog(getString(R.string.fingerprint_auth_alert_disabled_title), getString(R.string.fingerprint_auth_alert_disabled_message));
    }

    @Override
    @NonNull
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppSessionManager.getInstance().getpermissionsRequestCallPhone()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callTechSupport();
            } else {
                Toast.makeText(this, "Call Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void callTechSupport() {
        String techPhoneNumber = "tel:" + AppSessionManager.getInstance().getTechnicalSupport();
        Log.d(TAG, "techPhoneNumber: " + techPhoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(techPhoneNumber));
        Log.d(TAG, "OK TO CALL: " + techPhoneNumber);
        startActivity(callIntent);
    }

    private void promptForDiagnosticReporting() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setTitle(getString(R.string.diagnostic_reporting_title)).
                        setMessage(getString(R.string.diagnostic_reporting_message)).
                        setPositiveButton("YES", (dialog, which) -> {
                            AppCenter.setEnabled(true);
                            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
                            MyCTCA.enableDiagnosticReporting(true);
                            userPreferenceValue = getString(R.string.val_true);
                            postUserPreferences();
                        })
                        .setNegativeButton("NO", (dialogInterface, i) ->
                                new Handler(Looper.myLooper()).postDelayed(() -> {
                                    AppCenter.setEnabled(true);
                                    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
                                    MyCTCA.enableDiagnosticReporting(false);
                                    userPreferenceValue = getString(R.string.val_false);
                                    postUserPreferences();
                                }, 5000));
        builder.setCancelable(false);
        builder.create().setOnShowListener(arg0 -> builder.create().getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary)));
        if (!isFinishing())
            builder.create().show();
    }

    private void postUserPreferences() {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_set_user_preferences);
        List<UserPreference> arrayList = new ArrayList<>();

        UserPreference diagnosticReporting = new UserPreference();
        diagnosticReporting.setUserId(AppSessionManager.getInstance().getIdentityUser().getUserId());
        diagnosticReporting.setUserPreferenceType(USER_DIAGNOSTIC_REPORTING);
        diagnosticReporting.setUserPreferenceValue(userPreferenceValue);
        arrayList.add(diagnosticReporting);

        UserPreference acceptTermsOfUse = new UserPreference();
        acceptTermsOfUse.setUserId(AppSessionManager.getInstance().getIdentityUser().getUserId());
        acceptTermsOfUse.setUserPreferenceType(USER_ACCEPT_TERMS_OF_USE);
        acceptTermsOfUse.setUserPreferenceValue(getString(R.string.val_true));
        arrayList.add(acceptTermsOfUse);

        UserPreference appointmentDays = new UserPreference();
        appointmentDays.setUserId(AppSessionManager.getInstance().getIdentityUser().getUserId());
        appointmentDays.setUserPreferenceType(USER_APPT_DAYS);
        UserPreference appointmentDaysObj = AppSessionManager.getInstance().getAppointmentsDays();
        appointmentDays.setUserPreferenceValue(appointmentDaysObj != null ? appointmentDaysObj.getUserPreferenceValue() : null);
        arrayList.add(appointmentDays);

        Gson gson = new Gson();
        PostClient postClient = new PostClient(this, this, MyCTCATask.SAVE_USER_PREFERENCES);
        postClient.sendData(url, gson.toJson(arrayList), null);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        Log.d(TAG, response);
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_set_user_preferences);
        CTCAAnalyticsManager.createEvent("NavActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        Log.d(TAG, "error" + error);
        if (message.isEmpty())
            message = getString(R.string.error_400);
        showAlertDialog(getString(R.string.failure_title), message);
    }

    public void showMedDocs() {
        Log.d(TAG, "showMedDocs");
        Intent intent = new Intent(this, MoreMedicalDocActivity.class);
        startActivity(intent);
    }

    public void showHealthHistory() {
        Log.d(TAG, "showHealthHistory");
        Intent intent = new Intent(this, MoreHealthHistoryActivity.class);
        startActivity(intent);
    }

    public void showFormsLibrary() {
        Log.d(TAG, "showForms");
        Intent intent = MoreFormsLibraryActivity.newIntent(this);
        startActivity(intent);
    }

    public void showMyResources() {
        Log.d(TAG, "showMyResources");
        Intent intent = new Intent(this, MyResourcesActivity.class);
        startActivity(intent);
    }

    public void showBillPay() {
        Log.d(TAG, "showBillPay");
        Intent intent = MoreBillPayActivity.newIntent(this);
        startActivity(intent);
    }

    @Override
    public void showPatientReported() {
        Log.d(TAG, "showPatientReported");
        Intent intent = PatientReportedActivity.newIntent(this);
        startActivity(intent);
    }

    public void showActivityLogs() {
        Log.d(TAG, "showActivityLogs");
        Intent intent = MoreActivityLogsActivity.newIntent(this);
        startActivity(intent);
    }

    public void showContactUs() {
        Log.d(TAG, "showContactUs");
        Intent intent = MoreContactUsActivity.newIntent(this);
        startActivity(intent);
    }

    public void showAboutMyCTCA() {
        Log.d(TAG, "showBillPay");
        Intent intent = MoreAboutMyCTCAActivity.newIntent(this);
        startActivity(intent);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void learnAboutFingerprintAuthorization() {
        Intent intent = new Intent(this, AboutFingerprintAuthActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Pressed Back Button");
        // Alert that device can't send email
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle(getString(R.string.nav_back_tapped_title))
                .setMessage(getString(R.string.nav_back_tapped_text))
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.cancel();
                    GeneralUtil.logoutApplication();
                })
                .setNegativeButton(getString(R.string.nav_alert_cancel), (dialog12, which) -> {
                    dialog12.cancel();
                    View view = findViewById(R.id.fragment_container);
                    view.refreshDrawableState();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary)));
        if (!isFinishing())
            dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode: " + requestCode);
        if (requestCode == MailDetailActivity.MAIL_DETAIL_REQUEST) {

            if (resultCode == Mail.DO_REFRESH) {
                //Update List
                Log.d(TAG, "DO REFRESH");
                FragmentManager fm = getSupportFragmentManager();
                MailInboxFragment fragment = (MailInboxFragment) fm.findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    fragment.refreshItems();
                }
            }
            if (resultCode == Mail.CANCEL_REFRESH) {
                //Do nothing
                Log.d(TAG, "DON'T DO REFRESH");
            }
        }
    }

    public void reloadFragment() {
        if (sessionFacade.getUserType() == UserType.CAREGIVER && isCaregiverImpersonating())
            bottomNavigationView.setVisibility(View.GONE);
        else
            bottomNavigationView.setVisibility(View.VISIBLE);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (selectedFragment != null && selectedFragment.isVisible()) {
            ft.detach(selectedFragment);
            ft.attach(selectedFragment);
            ft.commit();
        }

    }
}

