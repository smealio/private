package com.myctca.fragment.appointmment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.activity.ApptRequestCalenderActivity;
import com.myctca.fragment.common.CalendarFragment;
import com.myctca.model.AppointmentCalendarData;

import java.util.Calendar;
import java.util.List;

public class ApptRequestCalenderFragment extends Fragment {
    //slot timings
    private static final String MORNING_SLOT = "8 am - 12 pm";
    private static final String NOON_SLOT = "1 pm - 4 pm";
    private static final String FULL_DAY_SLOT = "8 am - 4 pm";
    //slot names
    private static final String MORNING = "MORNING";
    private static final String NOON = "AFTERNOON";
    private static final String FULL_DAY = "ALL_DAY";

    private Context context;
    private CardView morningSlot;
    private CardView noonSlot;
    private CheckBox cbMorning;
    private CheckBox cbNoon;
    private TextView tvMorningTitle;
    private TextView tvMorningTime;
    private TextView tvNoonTitle;
    private TextView tvNoonTime;
    private Button btnAddDateTime;
    private AppointmentCalendarData apptCalendarData;
    private boolean isForEdit;
    private ImageView closeCalendar;
    private List<AppointmentCalendarData> allSelectedDates;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appt_request_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        morningSlot = view.findViewById(R.id.cvMorningSlot);
        noonSlot = view.findViewById(R.id.cvNoonSlot);
        cbMorning = view.findViewById(R.id.cbMorning);
        cbNoon = view.findViewById(R.id.cbNoon);
        tvMorningTitle = view.findViewById(R.id.tvMorningTitle);
        tvMorningTime = view.findViewById(R.id.tvMorningTime);
        tvNoonTitle = view.findViewById(R.id.tvNoonTitle);
        tvNoonTime = view.findViewById(R.id.tvNoonTime);
        btnAddDateTime = view.findViewById(R.id.btnAddDateTime);
        closeCalendar = view.findViewById(R.id.close_calendar);

        AppointmentCalendarData data = (AppointmentCalendarData) getArguments().getSerializable("PREFERRED_DATE_TIME");
        if (data != null) {
            apptCalendarData = data;
        } else {
            apptCalendarData = new AppointmentCalendarData();
        }
        isForEdit = getArguments().getBoolean("FOR_EDIT", false);
        allSelectedDates = (List<AppointmentCalendarData>) getArguments().getSerializable("ALL_SELECTED_DATE");

        handleClickListeners();
        setDefaultValues();
        enableAddButton();
    }

    private void setDefaultValues() {
        morningSlot.setCardElevation(5);
        noonSlot.setCardElevation(5);

        if (apptCalendarData != null) {
            switch (apptCalendarData.getSlot()) {
                case MORNING_SLOT:
                    cbMorning.performClick();
                    break;
                case NOON_SLOT:
                    cbNoon.performClick();
                    break;
                case FULL_DAY_SLOT:
                    cbMorning.performClick();
                    cbNoon.performClick();
                    break;
                default:
                    //do nothing
            }
        }

        //set part of fragment with calendar.
        Fragment childFragment = new CalendarFragment(getSelectedDate(), allSelectedDates);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.calender_fragment_container, childFragment).commit();
    }

    private Calendar getSelectedDate() {
        if (apptCalendarData != null) {
            return apptCalendarData.getDate();
        }
        return null;
    }

    private void handleClickListeners() {
        //both checkboxes can be selected
        cbMorning.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //highlight is selected
                tvMorningTitle.setTextColor(ContextCompat.getColor(context, R.color.purple_bright));
                tvMorningTime.setTextColor(ContextCompat.getColor(context, R.color.purple_bright));
                morningSlot.setCardElevation(30);
            } else {
                tvMorningTitle.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
                tvMorningTime.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
                morningSlot.setCardElevation(5);
            }
            enableAddButton();
        });

        cbNoon.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //highlight is selected
                tvNoonTitle.setTextColor(ContextCompat.getColor(context, R.color.purple_bright));
                tvNoonTime.setTextColor(ContextCompat.getColor(context, R.color.purple_bright));
                noonSlot.setCardElevation(30);
            } else {
                tvNoonTitle.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
                tvNoonTime.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
                noonSlot.setCardElevation(5);
            }
            enableAddButton();
        });

        btnAddDateTime.setOnClickListener(view -> {
            //return to previous fragment from which it was called
            Intent returnIntent = new Intent();
            returnIntent.putExtra("PREFERRED_DATE_TIME", apptCalendarData);
            returnIntent.putExtra("FOR_EDIT", isForEdit);
            ((ApptRequestCalenderActivity) context).setResult(Activity.RESULT_OK, returnIntent);
            ((ApptRequestCalenderActivity) context).finish();
        });

        //do not save anything when closed
        closeCalendar.setOnClickListener(view -> ((ApptRequestCalenderActivity) context).finish());
    }

    private void enableAddButton() {
        if (cbMorning.isChecked() && cbNoon.isChecked()) {
            apptCalendarData.setSlot(FULL_DAY_SLOT);
            apptCalendarData.setTimePreference(FULL_DAY);
        } else if (cbMorning.isChecked()) {
            apptCalendarData.setSlot(MORNING_SLOT);
            apptCalendarData.setTimePreference(MORNING);
        } else if (cbNoon.isChecked()) {
            apptCalendarData.setSlot(NOON_SLOT);
            apptCalendarData.setTimePreference(NOON);
        }

        if ((apptCalendarData != null) && (cbMorning.isChecked() || cbNoon.isChecked()) && apptCalendarData.getDate() != null) {
            btnAddDateTime.setAlpha(1);
            btnAddDateTime.setEnabled(true);
        } else {
            btnAddDateTime.setAlpha(0.4f);
            btnAddDateTime.setEnabled(false);
        }
    }

    public void selectedDateAndDay(AppointmentCalendarData appointmentCalendarData) {
        apptCalendarData.setDateStr(appointmentCalendarData.getDateStr());
        apptCalendarData.setDay(appointmentCalendarData.getDay());
        apptCalendarData.setDate(appointmentCalendarData.getDate());
        enableAddButton();
    }
}