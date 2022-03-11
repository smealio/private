package com.myctca.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tomackb on 2/2/18.
 */

public class MoreDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = MoreDatePickerFragment.class.getSimpleName();
    private MoreDatePickerFragmentListener datePickerListener;
    private String purpose;
    private boolean needsMax = true;
    private boolean needsMin = false;
    private boolean isDateSet = false;
    private Date previouslySelected;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static MoreDatePickerFragment newInstance(MoreDatePickerFragmentListener listener, String purpose, boolean withMax, boolean withMin) {
        MoreDatePickerFragment fragment = new MoreDatePickerFragment();
        fragment.setDatePickerListener(listener);
        fragment.setPurpose(purpose);
        fragment.setMax(withMax);
        fragment.setMin(withMin);
        return fragment;
    }

    public void setDatePickerListener(MoreDatePickerFragmentListener listener) {
        this.datePickerListener = listener;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setMax(boolean hasMax) {
        this.needsMax = hasMax;
    }

    public void setMin(boolean hasMin) {
        this.needsMin = hasMin;
    }

    protected void notifyDatePickerListener(Date date) {
        if (this.datePickerListener != null) {
            if (date != null) {
                this.datePickerListener.onMoreDateSet(date, purpose);
            }
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("MoreDatePickerFragment:onCreateDialog", CTCAAnalyticsConstants.PAGE_MORE_DATE_PICKER_VIEW, null, null);
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
            datePickerDialog = new DatePickerDialog(context, R.style.DatePickerDialogCustom, this, year, month, day);
        }
        // Set the max day to today
        if (needsMax && !needsMin) {
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        }

        if (needsMin && !needsMax) {
            // Set the minimum day to tomorrow
            c.add(Calendar.DAY_OF_YEAR, 1);

            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        }

        return datePickerDialog;


    }

    public void setPreviouslySelected(Date previouslySelected) {
        this.previouslySelected = previouslySelected;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!isDateSet) {
            notifyDatePickerListener(null);
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        Log.d("DATEPICKER", "onDateSet: " + year + "." + month + "." + day);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0, 0);
        Date date = c.getTime();

        isDateSet = true;
        // Do something with the date chosen by the user
        notifyDatePickerListener(date);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreDatePickerFragment:show", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.d(TAG, "error : " + e);
        }
    }

    public interface MoreDatePickerFragmentListener {
        void onMoreDateSet(Date date, String purpose);
    }
}
