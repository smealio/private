package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MoreMedDocImagingListFragment;
import com.myctca.model.MedDocType;

public class MoreMedDocImagingListActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-MOREIMGDOCLIST";

    private String mDocType = MedDocType.IMAGING;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreMedDocImagingListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_med_doc_imaging_list);

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);

        FragmentManager fm = getSupportFragmentManager();
        MoreMedDocImagingListFragment fragment = new MoreMedDocImagingListFragment();
        fm.beginTransaction()
                .add(R.id.more_med_doc_imaging_list_fragment_container, fragment)
                .commit();
        fragment.medDocType = mDocType;
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_med_doc_list, menu);

        String toolbarTitle = this.mDocType;
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
}