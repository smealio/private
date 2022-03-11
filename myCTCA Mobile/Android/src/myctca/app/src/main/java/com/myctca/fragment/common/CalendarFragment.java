package com.myctca.fragment.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.myctca.R;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.AppointmentCalendarData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment {
    private final Calendar selectedDate;
    private final List<AppointmentCalendarData> allSelectedDates;
    private Context context;
    private OnFragmentInteractionListener mListener;
    private CalendarView calendarView;
    private List<Calendar> selectedDatesCalendarList = new ArrayList<>();

    public CalendarFragment(Calendar calendar, List<AppointmentCalendarData> allSelectedDates) {
        this.selectedDate = calendar;
        this.allSelectedDates = allSelectedDates;
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
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = view.findViewById(R.id.calendarView);

        //disable weekends
        disableWeekendDays();

        //highlight previous selected dates if any
        setPreviousSelectedDates();

        //set min date
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_YEAR, 2);
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        minDate.set(Calendar.MINUTE, 0);
        minDate.set(Calendar.SECOND, 0);
        minDate.set(Calendar.MILLISECOND, 0);
        calendarView.setMinimumDate(minDate);

        //set max date
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 3);
        calendarView.setMaximumDate(maxDate);

        //set calendar date
        try {
            if (selectedDate != null) {
                calendarView.setDate(selectedDate);
            }
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        for (AppointmentCalendarData calendarData : allSelectedDates) {
            selectedDatesCalendarList.add(calendarData.getDate());
        }

        //handle date on click listener.
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar checkCalendar = eventDay.getCalendar();
            if (selectedDatesCalendarList.isEmpty() || !selectedDatesCalendarList.contains(checkCalendar)) {
                if (!isWeekendDay(eventDay.getCalendar())
                        && checkCalendar.get(Calendar.MONTH) == calendarView.getCurrentPageDate().get(Calendar.MONTH)
                        && checkCalendar.before(maxDate)) {
                    setAppointmentCalendarData(checkCalendar);
                }
            } else {
                mListener.selectedDateAndDay(new AppointmentCalendarData());
            }
        });
    }

    private void setPreviousSelectedDates() {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (AppointmentCalendarData data : allSelectedDates) {
            CalendarDay calendarDay = new CalendarDay(data.getDate());
            calendarDayList.add(calendarDay);
            calendarDay.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.blank_white_circle_elevated));
            calendarDay.setLabelColor(R.color.purple_bright);
            if (!data.getDate().equals(selectedDate)) {
                calendarDay.setSelectedLabelColor(R.color.purple_bright);
                calendarDay.setSelectedBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.blank_white_circle_elevated));
            }
        }
        calendarView.setCalendarDays(calendarDayList);
    }

    public void setAppointmentCalendarData(Calendar calendar) {
        AppointmentCalendarData appointmentCalendarData = new AppointmentCalendarData();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String timestamp = getFormattedDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        appointmentCalendarData.setDate(calendar);
        appointmentCalendarData.setDateStr(timestamp);
        appointmentCalendarData.setDay(getDayOfWeek(dayOfWeek));
        mListener.selectedDateAndDay(appointmentCalendarData);
    }

    private String getDayOfWeek(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            default:
                return "";
        }
    }

    public void disableWeekendDays() {
        List<Calendar> calendarList = new ArrayList<>();
        //disable all weekends between 3 years from today
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(c1.getTime());
        c2.add(Calendar.YEAR, 3);

        while (!c1.after(c2)) {
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                Calendar saturday = Calendar.getInstance();
                saturday.setTime(c1.getTime());
                calendarList.add(saturday);
            }
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                Calendar sunday = Calendar.getInstance();
                sunday.setTime(c1.getTime());
                calendarList.add(sunday);
            }
            c1.add(Calendar.DATE, 1);
        }
        calendarView.setDisabledDays(calendarList);
    }

    private boolean isWeekendDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private String getFormattedDate(int year, int month, int day) {
        String date = month + 1 + "/" + year + "/" + day;
        SimpleDateFormat oldFormat = new SimpleDateFormat("MM/yyyy/dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date mTimestamp;
        String timestamp = "";
        try {
            mTimestamp = oldFormat.parse(date);
            assert mTimestamp != null;
            timestamp = newFormat.format(mTimestamp);
        } catch (ParseException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertStringToLocalDate", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
        }
        return timestamp;
    }

    public interface OnFragmentInteractionListener {
        void selectedDateAndDay(AppointmentCalendarData appointmentCalendarData);
    }
}