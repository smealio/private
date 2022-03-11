package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.appointmment.AppointmentDetailFragment;
import com.myctca.model.Appointment;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

public class AppointmentDetailActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-APPTDETAIL";

    private Appointment appointment;

    public static Intent newIntent(Context packageContext, Appointment appt) {
        Intent intent = new Intent(packageContext, AppointmentDetailActivity.class);
        intent.putExtra("appt", appt);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("ApptDetailActivity:onCreate", CTCAAnalyticsConstants.PAGE_APPOINTMENTS_DETAILS_VIEW));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_detail);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.appt_detail_fragment_container);

        if (fragment == null) {
            fragment = new AppointmentDetailFragment();
            fm.beginTransaction()
                    .add(R.id.appt_detail_fragment_container, fragment)
                    .commit();
        }

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();

        Intent i = getIntent();
        this.appointment = i.getParcelableExtra("appt");
    }

    public void addFragment(Fragment fragment, String appointmentId) {
        Bundle bundle = new Bundle();
        String type = "Appointment Schedule";
        bundle.putString("TOOLBAR_NAME", type);
        ((DownloadPdfFragment) fragment).setFileName("Schedule" + ".pdf");
        ((DownloadPdfFragment) fragment).setPdfFor("Single " + type);
        ((DownloadPdfFragment) fragment).setPdfCheck(false);
        ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_download_appt_detail));
        ((DownloadPdfFragment) fragment).setAppointmentId(appointmentId);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.appt_detail_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appt_detail_upcoming, menu);

        String toolbarTitle = "Appointment Details";
        setToolBar(toolbarTitle);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.appt_detail_fragment_container);
        if (fragment instanceof DownloadPdfFragment) {
            CTCAAnalyticsManager.createEvent("AppointmentDetailActivity:onBackPressed", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_PDF_CLOSE_SHARE_TAP, null, null);
        }
        //STATEMENT should be here only. First apply above logic and then open previous screen.
        super.onBackPressed();
    }

    public Appointment getAppointmentData() {
        return this.appointment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            case R.id.item_share_pdf:
                CTCAAnalyticsManager.createEvent("AppointmentDetailActivity:onOptionsItemSelected", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_PDF_SHARE_TAP, null, null);
                sharePdf();
                break;
            case R.id.item_print_pdf:
                CTCAAnalyticsManager.createEvent("AppointmentDetailActivity:onOptionsItemSelected", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_PDF_PRINT_TAP, null, null);
                openMorePdfOptions();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }

        return true;
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.appt_detail_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.appt_detail_fragment_container);
        downloadPdfFragment.printSavePdf();
    }
}
