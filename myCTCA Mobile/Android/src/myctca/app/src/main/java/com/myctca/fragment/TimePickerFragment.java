package com.myctca.fragment;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragment.TimePickerFragmentListener timePickerListener;
    private Date previouslySelected;
    private Context context;

    public static TimePickerFragment newInstance(TimePickerFragment.TimePickerFragmentListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setTimePickerListener(listener);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void setTimePickerListener(TimePickerFragment.TimePickerFragmentListener listener) {
        this.timePickerListener = listener;
    }

    protected void notifyTimePickerListener(Date date) {
        if (this.timePickerListener != null) {
            this.timePickerListener.onTimeSet(date);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog;
        if (previouslySelected != null) {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(previouslySelected);
            int previousSelectedHour = c1.get(Calendar.HOUR_OF_DAY);
            int previousSelectedMinute = c1.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(context, R.style.DatePickerDialogCustom, this, previousSelectedHour, previousSelectedMinute, DateFormat.is24HourFormat(context));
        } else {
            timePickerDialog = new TimePickerDialog(context, R.style.DatePickerDialogCustom, this, hour, minute, DateFormat.is24HourFormat(context));
        }
        return timePickerDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        Date date = c.getTime();

        // Do something with the date chosen by the user
        notifyTimePickerListener(date);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("TimePickerFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
        }
    }

    public void setPreviouslySelected(Date previouslySelected) {
        this.previouslySelected = previouslySelected;
    }

    public interface TimePickerFragmentListener {
        void onTimeSet(Date date);
    }
}
