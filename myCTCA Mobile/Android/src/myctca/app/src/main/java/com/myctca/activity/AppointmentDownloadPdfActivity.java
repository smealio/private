package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.FragmentTransaction;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

public class AppointmentDownloadPdfActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-DOWNLOADAPPT";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, AppointmentDownloadPdfActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("DownloadApptPdfActivity:onCreate", CTCAAnalyticsConstants.PAGE_APPOINTMENTS_PDF_VIEW));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_pdf);

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);

        Bundle bundle = new Bundle();
        bundle.putString("TOOLBAR_NAME", getString(R.string.appt_download_schedule_title));

        DownloadPdfFragment fragment = DownloadPdfFragment.newInstance();
        fragment.setFileName(getString(R.string.appt_pdf) + ".pdf");
        fragment.setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_get_appointments_report));
        fragment.setPdfCheck(false);
        fragment.setPdfFor(getString(R.string.appt_pdf));
        fragment.params.put("startDate", getIntent().getStringExtra("startDate"));
        fragment.params.put("endDate", getIntent().getStringExtra("endDate"));

        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.appt_download_fragment_container, fragment);
        transaction.commit();

        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.appt_download_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.appt_download_fragment_container);
        downloadPdfFragment.printSavePdf();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            case R.id.item_share_pdf:
                sharePdf();
                break;
            case R.id.item_print_pdf:
                openMorePdfOptions();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
