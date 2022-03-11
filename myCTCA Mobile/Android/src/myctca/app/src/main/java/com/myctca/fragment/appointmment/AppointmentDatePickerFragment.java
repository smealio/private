package com.myctca.fragment.appointmment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private DatePickerFragmentListener datePickerListener;
    private Date previouslySelected;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static AppointmentDatePickerFragment newInstance(DatePickerFragmentListener listener) {
        AppointmentDatePickerFragment fragment = new AppointmentDatePickerFragment();
        fragment.setDatePickerListener(listener);
        return fragment;
    }

    public void setDatePickerListener(DatePickerFragmentListener listener) {
        this.datePickerListener = listener;
    }

    protected void notifyDatePickerListener(Date date) {
        if (this.datePickerListener != null) {
            this.datePickerListener.onDateSet(date);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("ApptDatePickerFragment:onCreateDialog", CTCAAnalyticsConstants.PAGE_APPT_DATE_PICKER_VIEW));

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog;
        if (previouslySelected != null) {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(previouslySelected);
            int previousSelectedYear = c1.get(Calendar.YEAR);
            int previousSelectedMonth = c1.get(Calendar.MONTH);
            int previousSelectedDay = c1.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialogCustom, this, previousSelectedYear, previousSelectedMonth, previousSelectedDay);
        } else {
            // Create a new instance of DatePickerDialog and return it
            datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialogCustom, this, year, month, day);
        }
        // Set the minimum day to tomorrow
        c.add(Calendar.DAY_OF_YEAR, 1);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        Date date = c.getTime();
        // Do something with the date chosen by the user
        notifyDatePickerListener(date);
    }

    public void setPreviouslySelected(Date previouslySelected) {
        this.previouslySelected = previouslySelected;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("ApptDatePickerFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
        }
    }

    public interface DatePickerFragmentListener {
        void onDateSet(Date date);
    }
}
