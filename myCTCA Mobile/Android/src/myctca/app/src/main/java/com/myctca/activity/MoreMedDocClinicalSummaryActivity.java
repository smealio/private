package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MoreDatePickerFragment;
import com.myctca.fragment.MoreMedDocClinicalSummaryDetailFragment;
import com.myctca.fragment.MoreMedDocClinicalSummaryFragment;
import com.myctca.fragment.StartEndDateDialogFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoreMedDocClinicalSummaryActivity extends MyCTCAActivity implements
        MoreDatePickerFragment.MoreDatePickerFragmentListener,
        StartEndDateDialogFragment.StartEndDateDialogListener,
        MoreMedDocClinicalSummaryFragment.MoreMedDocClinicalSummaryListener,
        MoreMedDocClinicalSummaryDetailFragment.MoreMedDocClinicalSummaryDetailListener {

    private static final String TAG = "myCTCA-MORECLINSUMM";
    private static final String LOGS_START_DATE = "LOGSSTARTDATE";
    StartEndDateDialogFragment dialogFragment;
    FragmentManager fm;
    MoreMedDocClinicalSummaryFragment moreMedDocClinicalSummaryFragment;
    private Fragment fragment;
    private boolean isFilterApplied;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreMedDocClinicalSummaryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_med_doc_clinical_summary);

        fm = getSupportFragmentManager();
        moreMedDocClinicalSummaryFragment = new MoreMedDocClinicalSummaryFragment();
        dialogFragment = StartEndDateDialogFragment.newInstance(this);
        dialogFragment.setDateMinMax(true, false);
        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);

        fragment = new MoreMedDocClinicalSummaryFragment();
        fm.beginTransaction()
                .add(R.id.more_med_doc_clinical_summary_fragment_container, fragment)
                .commit();
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
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

    private void filterClinicalSummaries() {
        if (!isFilterApplied) {
            dialogFragment.show(getSupportFragmentManager(), MoreMedDocClinicalSummaryActivity.class.getSimpleName());
        } else {
            ((MoreMedDocClinicalSummaryFragment) fragment).removeFilters();
            isFilterApplied = false;
        }
    }

    private void selectAndDownloadClinicalSummary() {
        ((MoreMedDocClinicalSummaryFragment) fragment).selectClinicalSummary();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            case R.id.toolbar_clinical_summaries_filter:
                if (!isFilterApplied && AppSessionManager.getInstance().getmClinicalSummaries().isEmpty()) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(this).
                                    setTitle(getString(R.string.clinical_summary_filter_title)).
                                    setMessage(getString(R.string.no_records_found_message)).
                                    setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                    if (!isFinishing())
                        builder.create().show();
                } else
                    this.filterClinicalSummaries();
                break;
            case R.id.toolbar_clinical_summaries_download:
                if (AppSessionManager.getInstance().getmClinicalSummaries().isEmpty()) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(this).
                                    setTitle(getString(R.string.clinical_summary_download_title)).
                                    setMessage(getString(R.string.no_records_found_message)).
                                    setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                    if (!isFinishing())
                        builder.create().show();
                } else
                    this.selectAndDownloadClinicalSummary();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }

        return true;
    }

    @Override
    public void onMoreDateSet(Date date, String purpose) {
        if (purpose.equals(LOGS_START_DATE)) {
            dialogFragment.setStartDate(date);
        } else {
            dialogFragment.setEndDate(date);
        }
    }

    @Override
    public void applyResultUsingDates(String startDate, String endDate) {
        isFilterApplied = true;
        ((MoreMedDocClinicalSummaryFragment) fragment).applyFilterOnClinicalSummary(startDate, endDate);
    }

    @Override
    public void addFragment(Fragment fragment, List<String> selectedClinicalSummaryIDs, String selectedItemName) {
        Bundle bundle = new Bundle();
        //send selected clinical summary ids
        String key = "SELECTED_CLINICAL_SUMMARY_ID";
        bundle.putStringArrayList(key, (ArrayList<String>) selectedClinicalSummaryIDs);

        String name = "SELECTED_CLINICAL_SUMMARY_NAME";
        bundle.putString(name, selectedItemName);

        fragment.setArguments(bundle);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.add(R.id.more_med_doc_clinical_summary_fragment_container, fragment);
        transaction.commit();
    }
}
