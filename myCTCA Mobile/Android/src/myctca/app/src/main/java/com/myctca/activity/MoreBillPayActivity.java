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
import com.myctca.fragment.MoreBillPayFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

public class MoreBillPayActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-MOREBILLPAY";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreBillPayActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("MoreBillPayActivity:onCreate", CTCAAnalyticsConstants.PAGE_BILL_PAY_VIEW, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_bill_pay);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.more_bill_pay_fragment_container);

        if (fragment == null) {
            fragment = new MoreBillPayFragment();
            fm.beginTransaction()
                    .add(R.id.more_bill_pay_fragment_container, fragment)
                    .commit();
        }

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_bill_pay, menu);

        String toolbarTitle = getString(R.string.more_bill_pay_title);
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
