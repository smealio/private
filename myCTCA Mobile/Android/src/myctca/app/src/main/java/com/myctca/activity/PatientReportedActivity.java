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

import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.PatientReportedFragment;

public class PatientReportedActivity extends MyCTCAActivity {

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, PatientReportedActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reported);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new PatientReportedFragment();
        fm.beginTransaction()
                .add(R.id.more_patient_reported_fragment_container, fragment)
                .commit();


        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_patient_reported, menu);

        String toolbarTitle = getString(R.string.more_patient_reported);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_patient_reported_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_patient_reported_fragment_container);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        for (int i = 0; i < AppSessionManager.getInstance().getSymptomInventories().size(); i++) {
            AppSessionManager.getInstance().getSymptomInventories().get(i).setExpanded(true);
        }
    }

    public void addFragment(Fragment fragment, String url, String title) {
        if (fragment instanceof DownloadPdfFragment) {
            Bundle bundle = new Bundle();
            bundle.putString("TOOLBAR_NAME", title);
            ((DownloadPdfFragment) fragment).setFileName(title + ".pdf");
            ((DownloadPdfFragment) fragment).setPdfFor(title);
            ((DownloadPdfFragment) fragment).params.clear();
            ((DownloadPdfFragment) fragment).setPdfCheck(false);
            ((DownloadPdfFragment) fragment).setmUrl(url);
            fragment.setArguments(bundle);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.more_patient_reported_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

}