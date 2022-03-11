package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.MoreHealthHistoryAllergiesListFragment;
import com.myctca.fragment.MoreHealthHistoryHealthIssueListFragment;
import com.myctca.fragment.MoreHealthHistoryImmunizationListFragment;
import com.myctca.fragment.MoreHealthHistoryPrescriptionsListFragment;
import com.myctca.fragment.MoreHealthHistoryVitalsListFragment;
import com.myctca.model.HealthHistoryType;
import com.myctca.model.Prescription;

import java.util.ArrayList;
import java.util.Map;

public class MoreHealthHistoryListActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-MOREHHLIST";

    private String healthHistoryType;

    public static Intent newIntent(Context packageContext, String healthHistoryType) {
        Intent intent = new Intent(packageContext, MoreHealthHistoryListActivity.class);
        intent.putExtra("healthHistoryType", healthHistoryType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_health_history_list);

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);

        Intent i = getIntent();
        this.healthHistoryType = (String) i.getSerializableExtra("healthHistoryType");
        Log.d(TAG, "MoreHealthHistoryListActivity healthHistoryType: " + this.healthHistoryType);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.more_health_history_list_fragment_container);

        if (fragment == null) {
            if (this.healthHistoryType.equals(HealthHistoryType.VITALS)) {
                fragment = new MoreHealthHistoryVitalsListFragment();
            }
            if (this.healthHistoryType.equals(HealthHistoryType.PRESCRIPTIONS)) {
                fragment = new MoreHealthHistoryPrescriptionsListFragment();
            }
            if (this.healthHistoryType.equals(HealthHistoryType.ALLERGIES)) {
                fragment = new MoreHealthHistoryAllergiesListFragment();
            }
            if (this.healthHistoryType.equals(HealthHistoryType.IMMUNIZATIONS)) {
                fragment = new MoreHealthHistoryImmunizationListFragment();
            }
            if (this.healthHistoryType.equals(HealthHistoryType.HEALTH_ISSUES)) {
                fragment = new MoreHealthHistoryHealthIssueListFragment();
            }
            fm.beginTransaction()
                    .add(R.id.more_health_history_list_fragment_container, fragment)
                    .commit();
            selectedFragment = fragment;
            fragmentName = selectedFragment.getClass().getSimpleName();
        }

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        for (int i = 0; i < AppSessionManager.getInstance().getmVitalsGroups().size(); i++) {
            AppSessionManager.getInstance().getmVitalsGroups().get(i).setExpanded(true);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_health_history_list, menu);

        String toolbarTitle = this.healthHistoryType;
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    private void sharePdf() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_health_history_list_fragment_container);
        downloadPdfFragment.sharePdf();
    }

    private void openMorePdfOptions() {
        DownloadPdfFragment downloadPdfFragment = (DownloadPdfFragment) getSupportFragmentManager().findFragmentById(R.id.more_health_history_list_fragment_container);
        downloadPdfFragment.printSavePdf();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            case R.id.toolbar_vitals_download:
                addFragment(DownloadPdfFragment.newInstance(), getString(R.string.myctca_download_vitals_report));
                break;
            case R.id.toolbar_allergies_download:
                addFragment(DownloadPdfFragment.newInstance(), getString(R.string.myctca_download_allergies_report));
                break;
            case R.id.toolbar_prescription_download:
                addFragment(DownloadPdfFragment.newInstance(), getString(R.string.myctca_download_prescriptions_report));
                break;
            case R.id.toolbar_immunizations_download:
                addFragment(DownloadPdfFragment.newInstance(), getString(R.string.myctca_download_immunizations_report));
                break;
            case R.id.toolbar_health_issues_download:
                addFragment(DownloadPdfFragment.newInstance(), getString(R.string.myctca_download_health_issues_report));
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

    private void addFragment(Fragment fragment, String endpoint) {
        Bundle bundle = new Bundle();
        bundle.putString("TOOLBAR_NAME", healthHistoryType);

        ((DownloadPdfFragment) fragment).setFileName(healthHistoryType + ".pdf");
        ((DownloadPdfFragment) fragment).setPdfFor(healthHistoryType);
        ((DownloadPdfFragment) fragment).params.clear();
        ((DownloadPdfFragment) fragment).setPdfCheck(false);
        ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + endpoint);

        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.more_health_history_list_fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    private void onRefillRequestPressed() {
        Log.d(TAG, "onRefillRequestPressed");
        FragmentManager fm = getSupportFragmentManager();
        MoreHealthHistoryPrescriptionsListFragment fragment = (MoreHealthHistoryPrescriptionsListFragment) fm.findFragmentById(R.id.more_health_history_list_fragment_container);

        Map<String, Prescription> refillRequest = fragment.getRefillRequest();
        Log.d(TAG, "onRefillRequestPressed count: " + refillRequest.size());

        if (AppSessionManager.getInstance().getmPrescriptions().isEmpty()) {
            showAlertMessage(getString(R.string.prescription_refill_no_ctca_prescribed_title), getString(R.string.no_records_found_message));
        } else if (!AppSessionManager.getInstance().isCtcaPrescribed()) {
            Log.d(TAG, "none of the prescription is CTCA Prescribed");
            showAlertMessage(getString(R.string.prescription_refill_no_ctca_prescribed_title), getString(R.string.prescription_refill_no_ctca_prescribed_message));
        } else if (refillRequest.size() == 0) {
            Log.d(TAG, "form is not valid");
            showAlertMessage(getString(R.string.prescription_refill_invalid_form_title), getString(R.string.prescription_refill_no_prescriptions_selected));
        } else {
            Intent intent = PrescriptionRefillActivity.refillRequestIntent(this, new ArrayList<>(refillRequest.values()));
            startActivity(intent);
        }
    }

    private void showAlertMessage(String title, String message) {
        Log.d(TAG, "form is not valid");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog, which) -> dialog.cancel());
        if (!isFinishing())
            builder.show();
    }

    public Spannable highlightSearchedText(String fullText, String searchText) {
        int startPos = fullText.toLowerCase().indexOf(searchText.toLowerCase());
        int endPos = startPos + searchText.length();
        Spannable spannable = new SpannableString(fullText);
        if (startPos != -1) {
            spannable.setSpan(new BackgroundColorSpan(ContextCompat.getColor(this,
                    R.color.highlight_text_color)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,
                    R.color.white)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
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
