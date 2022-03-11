package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.MoreActivityLogsFilterDialogFragment;
import com.myctca.fragment.MoreActivityLogsFragment;
import com.myctca.fragment.MoreDatePickerFragment;
import com.myctca.service.ActivityLogsService;
import com.myctca.service.SessionFacade;

import java.util.Date;

public class MoreActivityLogsActivity extends MyCTCAActivity implements MoreActivityLogsFilterDialogFragment.OnFilterApplyListener, MoreDatePickerFragment.MoreDatePickerFragmentListener, ActivityLogsService.ActivityLogsDatesListener {

    private static final String TAG = "myCTCA-MOREACTIVITYLOG";
    private boolean isFilterApplied;
    private Fragment fragment;
    private MoreActivityLogsFilterDialogFragment dialog;
    private SessionFacade sessionFacade;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreActivityLogsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_activity_logs);

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);

        //filter activity logs
        ImageView ivFilterActivityLogs = findViewById(R.id.iv_filter_activity_logs);
        ivFilterActivityLogs.setOnClickListener(view -> {
            if (!isFilterApplied) {
                FragmentManager manager = getSupportFragmentManager();
                dialog = MoreActivityLogsFilterDialogFragment.newInstance(this);
                dialog.show(manager, "");
            } else {
                ((MoreActivityLogsFragment) fragment).removeFilters();
                isFilterApplied = false;
                ((MoreActivityLogsFragment) fragment).setRefreshState();
                ivFilterActivityLogs.setImageResource(R.drawable.filter_icon);
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.more_activity_logs_fragment_container);

        if (fragment == null) {
            fragment = new MoreActivityLogsFragment();
            fm.beginTransaction()
                    .add(R.id.more_activity_logs_fragment_container, fragment)
                    .commit();
        }

        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_activity_logs, menu);

        String toolbarTitle = getString(R.string.more_activity_logs_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public void applyFilterOnActivityLogs(String etFilterUsername, String etFilterMessage) {
        isFilterApplied = true;
        ImageView filterIcon = findViewById(R.id.iv_filter_activity_logs);
        filterIcon.setImageResource(R.drawable.filter_off_icon);
        ((MoreActivityLogsFragment) fragment).applyFilterOnActivityLogs(etFilterUsername, etFilterMessage);
        ((MoreActivityLogsFragment) fragment).setRefreshState();
    }

    @Override
    public void onMoreDateSet(Date date, String purpose) {
        sessionFacade.getSelectedAndNextDate(this, date);
        dialog.setDate(date);
    }

    @Override
    public void notifyDateSelected(String selectedDate, String nextDate) {
        ((MoreActivityLogsFragment) fragment).setSelectedAndNextDate(selectedDate, nextDate);
    }
}
