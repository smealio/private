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

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MoreContactUsFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

public class MoreContactUsActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-MORECONTACTUS";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreContactUsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("MoreContactUsActivity:onCreate", CTCAAnalyticsConstants.PAGE_CONTACT_US_VIEW, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_contact_us);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.more_contact_us_fragment_container);

        if (fragment == null) {
            fragment = new MoreContactUsFragment();
            fm.beginTransaction()
                    .add(R.id.more_contact_us_fragment_container, fragment)
                    .commit();
        }
        selectedFragment = fragment;
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_contact_us, menu);

        String toolbarTitle = getString(R.string.more_contact_us_title);
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
}
