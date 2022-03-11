package com.myctca.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MoreFragment;

public class CaregiverMoreActivity extends MyCTCAActivity implements MoreFragment.MoreFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_more);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.caregiver_more_container);

        if (fragment == null) {
            fragment = new MoreFragment();
            fm.beginTransaction()
                    .add(R.id.caregiver_more_container, fragment)
                    .commit();
        }

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }
        return true;
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

}