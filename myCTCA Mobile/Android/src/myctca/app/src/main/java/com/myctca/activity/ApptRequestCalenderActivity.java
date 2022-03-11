package com.myctca.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.appointmment.ApptRequestCalenderFragment;
import com.myctca.fragment.common.CalendarFragment;
import com.myctca.model.AppointmentCalendarData;

public class ApptRequestCalenderActivity extends MyCTCAActivity implements CalendarFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_request_calender);

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new ApptRequestCalenderFragment();
        fm.beginTransaction()
                .add(R.id.appt_request_calender_container, fragment)
                .commit();
        fragment.setArguments(getIntent().getExtras());
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void selectedDateAndDay(AppointmentCalendarData appointmentCalendarData) {
        ((ApptRequestCalenderFragment) selectedFragment).selectedDateAndDay(appointmentCalendarData);
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