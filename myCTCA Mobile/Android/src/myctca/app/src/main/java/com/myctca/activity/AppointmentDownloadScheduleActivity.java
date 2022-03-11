package com.myctca.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.appointmment.AppointmentDownloadScheduleFragment;
import com.myctca.fragment.common.CalendarRangeFragment;

import java.util.Date;

public class AppointmentDownloadScheduleActivity extends MyCTCAActivity implements CalendarRangeFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new AppointmentDownloadScheduleFragment();
        fm.beginTransaction()
                .add(R.id.calendarContainer, fragment)
                .commit();

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
    }

    public void setToolBar(String title) {
        if (toolbar != null) {
            TextView titleTV = findViewById(R.id.toolbar_tvTitle);
            titleTV.setText(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appt_request_calender, menu);

        String toolbarTitle = "Download Schedule";
        setToolBar(toolbarTitle);

        //hide default back arrow in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_0) {
            onBackPressed();
        } else {
            Log.d(TAG, "DEFAULT");
        }
        return true;
    }

    @Override
    public void setRange(Date start, Date end) {
        AppointmentDownloadScheduleFragment fragment = (AppointmentDownloadScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.calendarContainer);
        assert fragment != null;
        fragment.setRange(start, end);
    }
}