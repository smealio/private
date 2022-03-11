package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MoreMedDocListFragment;

public class MoreMedDocListActivity extends MyCTCAActivity {

    public static final String MED_DOC_TYPE = "MedDocType";
    private static final String TAG = "myCTCA-MOREMEDDOCLIST";
    private String mDocType = "";

    public static Intent newIntent(Context packageContext, String docType) {
        Intent intent = new Intent(packageContext, MoreMedDocListActivity.class);
        intent.putExtra(MED_DOC_TYPE, docType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_med_doc_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mDocType = (String) extras.getSerializable(MED_DOC_TYPE);
            Log.d(TAG, "DOC TYPE: " + this.mDocType);
        }

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);

        FragmentManager fm = getSupportFragmentManager();
        MoreMedDocListFragment fragment = new MoreMedDocListFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("MED_DOC_TYPE", mDocType);
        fragment.setArguments(mBundle);

        fm.beginTransaction()
                .add(R.id.more_med_doc_list_fragment_container, fragment)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "BACK BUTTON PRESSED");
            this.onBackPressed();
        } else {
            Log.d(TAG, "DEFAULT");
        }
        return true;
    }
}
