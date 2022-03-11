package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.LabsDetailFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.LabResult;
import com.myctca.util.MyCTCADateUtils;

public class LabsDetailActivity extends MyCTCAActivity implements LabsDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = "myCTCA-LABSDETAIL";

    private LabResult labResult;

    public static Intent newIntent(Context packageContext, LabResult labResult) {
        Intent intent = new Intent(packageContext, LabsDetailActivity.class);
        intent.putExtra("labResult", labResult);
        Log.d(TAG, "LabsDetail intent: " + labResult.getSummaryNames(", "));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("LabsDetailActivity:onCreate", CTCAAnalyticsConstants.PAGE_LAB_RESULT_DETAIL_VIEW, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labs_detail);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.labs_detail_fragment_container);

        if (fragment == null) {
            fragment = new LabsDetailFragment();
            fm.beginTransaction()
                    .add(R.id.labs_detail_fragment_container, fragment)
                    .commit();
        }

        Intent i = getIntent();
        this.labResult = (LabResult) i.getSerializableExtra("labResult");
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.labs_detail_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.labs_detail_fragment_container);
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
                CTCAAnalyticsManager.createEvent("LabsDetailActivity:onOptionsItemSelected", CTCAAnalyticsConstants.ACTION_LAB_RESULTS_PDF_SHARE_TAP, null, null);
                sharePdf();
                break;
            case R.id.item_print_pdf:
                CTCAAnalyticsManager.createEvent("LabsDetailActivity:onOptionsItemSelected", CTCAAnalyticsConstants.ACTION_LAB_RESULTS_PDF_PRINT_TAP, null, null);
                openMorePdfOptions();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }

        return true;
    }

    public LabResult getLabResult() {
        return labResult;
    }

    @Override
    public void addFragment(Fragment fragment) {
        CTCAAnalyticsManager.createEvent("LabsDetailActivity:addFragment", CTCAAnalyticsConstants.PAGE_LAB_RESULT_PDF_VIEW, null, null);
        Bundle bundle = new Bundle();
        bundle.putString("TOOLBAR_NAME", getString(R.string.labs_detail_title) + " " + MyCTCADateUtils.getSlashedDateStr(this.labResult.getPerformedDate()));

        ((DownloadPdfFragment) fragment).setFileName(getString(R.string.labs_results_pdf) + ".pdf");
        ((DownloadPdfFragment) fragment).setPdfFor(getString(R.string.labs_results_pdf));
        ((DownloadPdfFragment) fragment).params.clear();
        ((DownloadPdfFragment) fragment).params.put("performeddate", labResult.getPerformedDateStr());
        ((DownloadPdfFragment) fragment).params.put("collectedby", labResult.getCollectedBy());
        ((DownloadPdfFragment) fragment).setPdfCheck(false);
        ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_download_lab_results));

        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.labs_detail_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }
}
