package com.myctca.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.myctca.BuildConfig;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.activity.DisplayWebViewActivity;
import com.myctca.activity.LoginActivity;
import com.myctca.activity.SendMessageActivity;
import com.myctca.activity.SplashActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.HomeFragment;
import com.myctca.fragment.LabsFragment;
import com.myctca.fragment.LoadingDialogFragment;
import com.myctca.fragment.MailArchivedFragment;
import com.myctca.fragment.MailInboxFragment;
import com.myctca.fragment.MailSentFragment;
import com.myctca.fragment.MoreActivityLogsFragment;
import com.myctca.fragment.MoreMedDocClinicalSummaryFragment;
import com.myctca.fragment.MoreMedDocImagingListFragment;
import com.myctca.fragment.MoreMedDocListFragment;
import com.myctca.fragment.MoreReleaseOfInfoFormFragment;
import com.myctca.fragment.TeleHealthCommunicationFragment;
import com.myctca.fragment.appointmment.AppointmentUpcomingFragment;
import com.myctca.util.GeneralUtil;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

public class MyCTCAActivity extends AppCompatActivity {

    protected static final String TAG = "MyCTCA-Activity";
    private static final String RELEASE = "release";
    public Toolbar toolbar;
    protected MyCTCA mMyCTCA;
    protected LinearLayout llNoInternetConnection;
    protected LinearLayout llInternetConnected;
    protected Fragment selectedFragment = null;
    protected String fragmentName = "";
    protected LoadingDialogFragment activityIndicator;
    private int wasDisconnected = 0;
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                if (llNoInternetConnection != null && llInternetConnected != null) {
                    llNoInternetConnection.setVisibility(View.GONE);
                    if (wasDisconnected == 1) {
                        wasDisconnected = 0;
                        llInternetConnected.setVisibility(View.VISIBLE);
                        new Handler(Looper.myLooper()).postDelayed(() -> llInternetConnected.setVisibility(View.GONE), 3000);
                        if (MyCTCA.isIsInForeground())
                            reloadData();
                    }
                }
            } else {
                if (llNoInternetConnection != null && llInternetConnected != null) {
                    wasDisconnected = 1;
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llInternetConnected.setVisibility(View.GONE);
                    if (MyCTCA.isIsInForeground() && fragmentName.equals(TeleHealthCommunicationFragment.class.getSimpleName())) {
                        ((TeleHealthCommunicationFragment) selectedFragment).showErrorDialog(getString(R.string.telehealth_internet_error_title), getString(R.string.telehealth_internet_error_message), true);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSession();

        // Removes screenshot when app goes into background (Recent Apps)
        if (BuildConfig.BUILD_TYPE.equals(RELEASE))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    private void checkSession() {
        Log.d(TAG, "checkSession");
        MyCTCAActivity activity = (MyCTCAActivity) MyCTCA.getCurrentActivity();
        Log.d(TAG, "activity" + activity);
        if (!activity.getClass().isAssignableFrom(LoginActivity.class) &&
                !activity.getClass().isAssignableFrom(SplashActivity.class) &&
                !activity.getClass().isAssignableFrom(DisplayWebViewActivity.class) &&
                !activity.getClass().isAssignableFrom(SendMessageActivity.class)) {
            Log.d(TAG, "userProfile" + AppSessionManager.getInstance().getUserProfile());
            if (AppSessionManager.getInstance().getUserProfile() == null) {
                Log.d(TAG, "Application killed by OS");
                GeneralUtil.logoutApplication();
            }

        }
    }

    private void reloadData() {
        if (fragmentName.equals(HomeFragment.class.getSimpleName())) {
            ((HomeFragment) selectedFragment).downloadAlertMessages("RETRIEVE_DATA");
        } else if (fragmentName.equals(AppointmentUpcomingFragment.class.getSimpleName())) {
            ((AppointmentUpcomingFragment) selectedFragment).downloadAppointments(getString(R.string.get_appointments_indicator));
        } else if (fragmentName.equals(LabsFragment.class.getSimpleName())) {
            ((LabsFragment) selectedFragment).downloadLabResults(getString(R.string.get_lab_results_indicator));
        } else if (fragmentName.equals(MailInboxFragment.class.getSimpleName())) {
            ((MailInboxFragment) selectedFragment).downloadMail(getString(R.string.get_inbox_mail_indicator));
        } else if (fragmentName.equals(MailSentFragment.class.getSimpleName())) {
            ((MailSentFragment) selectedFragment).downloadSentMail(getString(R.string.get_sent_mail_indicator));
        } else if (fragmentName.equals(MailArchivedFragment.class.getSimpleName())) {
            ((MailArchivedFragment) selectedFragment).downloadArchivedMail(getString(R.string.get_archived_mail_indicator));
        } else if (fragmentName.equals(MoreMedDocClinicalSummaryFragment.class.getSimpleName())) {
            ((MoreMedDocClinicalSummaryFragment) selectedFragment).downloadClinicalSummaryList(getString(R.string.get_clinical_summary_indicator));
        } else if (fragmentName.equals(MoreMedDocImagingListFragment.class.getSimpleName())) {
            ((MoreMedDocImagingListFragment) selectedFragment).downloadImagingDocs(getString(R.string.get_imaging_indicator));
        } else if (fragmentName.equals(MoreMedDocListFragment.class.getSimpleName())) {
            ((MoreMedDocListFragment) selectedFragment).downloadMedDocs("Retrieve");
        } else if (fragmentName.equals(MoreActivityLogsFragment.class.getSimpleName())) {
            ((MoreActivityLogsFragment) selectedFragment).downloadActivityLogs("Retrieve Logs");
        } else if (fragmentName.equals(MoreReleaseOfInfoFormFragment.class.getSimpleName())) {
            ((MoreReleaseOfInfoFormFragment) selectedFragment).downloadROIFormDetails();
            ((MoreReleaseOfInfoFormFragment) selectedFragment).downloadUserDetails();
        } else if (fragmentName.equals(DownloadPdfFragment.class.getSimpleName())) {
            ((DownloadPdfFragment) selectedFragment).checkPdf();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSession();
        registerReceiver(networkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        // Test if App was in background
        if (AppSessionManager.getInstance().wasInBackground()) {
            //Do specific came-here-from-background code
            Log.d(TAG, "App WAS IN BACKGROUND");
        }
        AppSessionManager.getInstance().resetIdleHandler();
        AppSessionManager.getInstance().stopActivityBackgroundTimer();

        // Set toolbar
        toolbar = findViewById(R.id.toolbar_nav);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_back));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearReferences();
        if (networkReceiver != null)
            unregisterReceiver(networkReceiver);
        // Background Test stuff
        AppSessionManager.getInstance().startActivityBackgroundTimer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReferences();
        Log.d(TAG, "onDestroy");
    }

    private void clearReferences() {
        Activity currActivity = mMyCTCA.getCurrentActivity();
        if (this.equals(currActivity)) {
            if (mMyCTCA.getCurrentActivity() instanceof LoginActivity && ((LoginActivity) mMyCTCA.getCurrentActivity()).getDialog() != null)
                ((LoginActivity) mMyCTCA.getCurrentActivity()).getDialog().dismiss();

        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Log.d(TAG, "onUserInteraction");
        AppSessionManager.getInstance().resetIdleHandler();
    }

    public void showActivityIndicator(final String message) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        Handler mainHandler = new Handler(this.getMainLooper());
        mainHandler.post(() -> {
            FragmentManager manager = getSupportFragmentManager();
            activityIndicator = LoadingDialogFragment.newInstance(message);
            activityIndicator.show(manager, "");
        });
    }

    public void hideActivityIndicator() {

        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Handler mainHandler = new Handler(this.getMainLooper());
        if (activityIndicator != null) {
            activityIndicator.dismissAllowingStateLoss();
        }
        mainHandler.post(() -> {
            if (activityIndicator != null) {
                activityIndicator.dismissAllowingStateLoss();
            }
        });
    }

    public void returnToRoot(boolean deepLinking) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("DEEP_LINKING", deepLinking);

        startActivity(intent);
        finish();
    }

    public void showSnack(String message) {
        Snackbar mySnackbar = Snackbar.make(this.findViewById(android.R.id.content), message, LENGTH_LONG);
        mySnackbar.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            wasDisconnected = 1;
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    public void setToolBar(String title) {
        if (toolbar != null) {
            TextView titleTV = findViewById(R.id.toolbar_tvTitle);
            titleTV.setText(title);
        }
    }

}
