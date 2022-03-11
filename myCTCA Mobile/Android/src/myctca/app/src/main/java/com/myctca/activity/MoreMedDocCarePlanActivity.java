package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.MoreMedDocCarePlanFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

public class MoreMedDocCarePlanActivity extends MyCTCAActivity implements MoreMedDocCarePlanFragment.OnFragmentInteractionListener {

    private static final String TAG = "myCTCA-MORECAREPLAN";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreMedDocCarePlanActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("MoreMedDocCarePlanActivity:onCreate", CTCAAnalyticsConstants.PAGE_CARE_PLAN_VIEW, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_med_doc_care_plan);

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);


        FragmentManager fm = getSupportFragmentManager();
        MoreMedDocCarePlanFragment fragment = new MoreMedDocCarePlanFragment();
        fm.beginTransaction()
                .add(R.id.more_med_doc_care_plan_fragment_container, fragment)
                .commit();
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_med_doc, menu);

        String toolbarTitle = getString(R.string.more_med_doc_care_plan_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setToolBar(String title) {
        if (toolbar != null) {
            TextView titleTV = findViewById(R.id.toolbar_tvTitle);
            titleTV.setText(title);
        }
    }

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_med_doc_care_plan_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_med_doc_care_plan_fragment_container);
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
    public void addFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("TOOLBAR_NAME", getString(R.string.care_plan_pdf));

        ((DownloadPdfFragment) fragment).setFileName(getString(R.string.care_plan_pdf) + ".pdf");
        ((DownloadPdfFragment) fragment).setPdfFor(getString(R.string.care_plan_pdf));
        ((DownloadPdfFragment) fragment).params.clear();
        ((DownloadPdfFragment) fragment).setPdfCheck(false);
        ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_download_care_plan_report));

        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.more_med_doc_care_plan_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }
}
