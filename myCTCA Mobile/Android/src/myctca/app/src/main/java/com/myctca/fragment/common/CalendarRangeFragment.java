package com.myctca.fragment.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.myctca.R;
import com.myctca.activity.AppointmentDownloadScheduleActivity;
import com.myctca.common.view.CustomDialogSideBySide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarRangeFragment extends Fragment implements CustomDialogSideBySide.CustomDialogSideBySideListener {
    private static final String START_DATE = "START";
    private CalendarView calendarView;
    private Context context;
    private OnFragmentInteractionListener mListener;
    private String dateType;

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_range, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = view.findViewById(R.id.calendarView);
        //handle date on click listener.
        List<Calendar> calendarList = new ArrayList<>();
        calendarView.setOnDayClickListener(eventDay -> {
            if (!calendarView.getSelectedDates().isEmpty()) {
                if (calendarView.getSelectedDates().size() > 1) {
                    if (dateType.equals(START_DATE)) {
                        Calendar lastDate = calendarView.getSelectedDates().get(calendarView.getSelectedDates().size() - 1);
                        if (eventDay.getCalendar().before(lastDate)) {
                            calendarList.add(lastDate);
                            calendarView.setSelectedDates(calendarList);
                            mListener.setRange(eventDay.getCalendar().getTime(), lastDate.getTime());
                        } else {
                            mListener.setRange(eventDay.getCalendar().getTime(), null);
                            new CustomDialogSideBySide().getButtonsSideBySideDialog(this, (AppointmentDownloadScheduleActivity) context, context.getString(R.string.appt_download_schedule_calendar_invalid_title), context.getString(R.string.appt_download_schedule_calendar_invalid_message), "Ok", "").show();
                        }
                    } else {
                        Calendar startDate = calendarView.getSelectedDates().get(0);
                        if (eventDay.getCalendar().after(startDate)) {
                            calendarList.add(startDate);
                            calendarView.setSelectedDates(calendarList);
                            mListener.setRange(startDate.getTime(), eventDay.getCalendar().getTime());
                        } else {
                            mListener.setRange(eventDay.getCalendar().getTime(), null);
                            new CustomDialogSideBySide().getButtonsSideBySideDialog(this, (AppointmentDownloadScheduleActivity) context, context.getString(R.string.appt_download_schedule_calendar_invalid_title), context.getString(R.string.appt_download_schedule_calendar_invalid_message), "Ok", "").show();
                        }
                    }
                    calendarList.clear();
                } else {
                    if (calendarView.getFirstSelectedDate().getTime().after(eventDay.getCalendar().getTime()))
                        mListener.setRange(eventDay.getCalendar().getTime(), calendarView.getFirstSelectedDate().getTime());
                    else
                        mListener.setRange(calendarView.getFirstSelectedDate().getTime(), eventDay.getCalendar().getTime());
                }
            } else
                mListener.setRange(eventDay.getCalendar().getTime(), null);
        });
    }

    @Override
    public void negativeButtonAction() {
        //do nothing
    }

    @Override
    public void positiveButtonAction() {
        //do nothing
    }

    public interface OnFragmentInteractionListener {
        void setRange(Date start, Date end);
    }
}