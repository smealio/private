package com.myctca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MoreMedicalDocFragment;

public class MoreMedicalDocActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-MOREMEDDOC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_medical_doc);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.more_med_doc_fragment_container);

        if (fragment == null) {
            fragment = new MoreMedicalDocFragment();
            fm.beginTransaction()
                    .add(R.id.more_med_doc_fragment_container, fragment)
                    .commit();
        }

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_med_doc, menu);

        String toolbarTitle = getString(R.string.more_med_doc_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public void showCarePlan() {

        Intent intent = new Intent(this, MoreMedDocCarePlanActivity.class);
        startActivity(intent);
    }

    public void showClinicalSummary() {
        Intent intent = MoreMedDocClinicalSummaryActivity.newIntent(this);
        startActivity(intent);
    }

    public void showMedDocList(String medDocType) {
        Intent intent = MoreMedDocListActivity.newIntent(this, medDocType);
        startActivity(intent);
    }

    public void showImagingDocList() {
        Log.d(TAG, "showImagingDocList");
        Intent intent = MoreMedDocImagingListActivity.newIntent(this);
        startActivity(intent);
    }
}
