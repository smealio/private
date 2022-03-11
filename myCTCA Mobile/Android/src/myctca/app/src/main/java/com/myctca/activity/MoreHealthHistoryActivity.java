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
import com.myctca.fragment.MoreHealthHistoryFragment;
import com.myctca.model.HealthHistoryType;

public class MoreHealthHistoryActivity extends MyCTCAActivity {

    private static final String TAG = "myCTCA-MOREHEALTHHIST";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreHealthHistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_health_history);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.more_health_history_fragment_container);

        if (fragment == null) {
            fragment = new MoreHealthHistoryFragment();
            fm.beginTransaction()
                    .add(R.id.more_health_history_fragment_container, fragment)
                    .commit();
        }

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_health_history, menu);

        String toolbarTitle = getString(R.string.more_health_history_title);
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

    public void showVitals() {
        Log.d(TAG, "showVitals");
        Intent intent = MoreHealthHistoryListActivity.newIntent(this, HealthHistoryType.VITALS);
        startActivity(intent);
    }

    public void showPrescriptions() {
        Log.d(TAG, "showPrescriptions");
        Intent intent = MoreHealthHistoryListActivity.newIntent(this, HealthHistoryType.PRESCRIPTIONS);
        startActivity(intent);
    }

    public void showAllergies() {
        Log.d(TAG, "showAllergies");
        Intent intent = MoreHealthHistoryListActivity.newIntent(this, HealthHistoryType.ALLERGIES);
        startActivity(intent);
    }

    public void showImmunizations() {
        Log.d(TAG, "showImmunizations");
        Intent intent = MoreHealthHistoryListActivity.newIntent(this, HealthHistoryType.IMMUNIZATIONS);
        startActivity(intent);
    }

    public void showHealthIssues() {
        Log.d(TAG, "showHealthIssues");
        Intent intent = MoreHealthHistoryListActivity.newIntent(this, HealthHistoryType.HEALTH_ISSUES);
        startActivity(intent);
    }
}
