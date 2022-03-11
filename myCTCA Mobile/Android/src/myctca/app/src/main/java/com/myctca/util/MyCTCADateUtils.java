package com.myctca.util;

import android.text.format.DateUtils;
import android.util.Log;

import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by tomackb on 8/1/17.
 */

public class MyCTCADateUtils {

    private static final String TAG = "CTCA-DateUtils";

    public static Date convertStringToLocalDate(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date localDate = new Date();
        try {
            localDate = sdf.parse(timestamp);
        } catch (ParseException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertStringToLocalDate", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "Parse Exception String To Date: " + e.getMessage());
        }
        //Log.d(TAG,"ConnectDateUtil convertStringToLocal converted data: " + localDate);
        return localDate;
    }

    public static Date convertAppointmentStringToLocalDate(String timestamp, String timezone) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneId(timezone)));
        try {
            date = simpleDateFormat.parse(timestamp);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getTimeZoneId(String key) {
        Map<String, String> map = new HashMap<>();
        map.put("HST", "US/Hawaii");
        map.put("HDT", "US/Hawaii");
        map.put("HAST", "America/Adak");
        map.put("HADT", "America/Adak");
        map.put("AKST", "America/Anchorage");
        map.put("AKDT", "America/Anchorage");
        map.put("PST", "America/Los_Angeles");
        map.put("PDT", "America/Los_Angeles");
        map.put("MST", "America/Phoenix");
        map.put("MDT", "America/Phoenix");
        map.put("CST", "America/Chicago");
        map.put("CDT", "America/Chicago");
        map.put("EST", "America/Detroit");
        map.put("EDT", "America/Detroit");

        if (map.containsKey(key))
            return map.get(key);
        else return TimeZone.getDefault().getID();
    }

    public static Date convertTimestampToLocalDate(String timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date localDate = new Date();
        try {
            localDate = sdf.parse(timestamp);
        } catch (ParseException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertTimestampToLocalDate", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "Parse Exception String To Date: " + e.getMessage());
        }
        return localDate;
    }

    public static Date convertShortStringToLocalDate(String timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date localDate = new Date();
        if (!timestamp.isEmpty()) {
            try {
                localDate = sdf.parse(timestamp);
            } catch (ParseException e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertShortStringToLocalDate", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG, "Parse Exception String To Date: " + e.getMessage());
            }
        }
        return localDate;
    }

    public static Date convertStringToLocalDate(String timestamp, String dateFormat) {

        //Log.d(TAG,"ConnectDateUtil convertStringToLocal: " + timestamp);
        //TimeZone tz = cal.getTimeZone();
        //timestamp = timestamp.replace("T"," ");
        String dFormat = dateFormat;
        if (dFormat == null) {
            dFormat = "yyyy-MM-dd'T'HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dFormat, Locale.getDefault());
        //Log.e(TAG,"ConnectDateUtil convertStringToLocalDate SimpleDateFormat: " + dFormat);
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date mTimestamp = new Date();
        try {
            mTimestamp = sdf.parse(timestamp);
        } catch (ParseException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertStringToLocalDate", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "String To Date ERROR: " + e.getMessage());
            Log.e(TAG, "Full Error: " + e);
        }
        //Log.d(TAG,"ConnectDateUtil convertStringToLocal converted data: " + mTimestamp);
        return mTimestamp;
    }

    public static String convertDateToLocalStringUTC(Date timestamp) {
        //Log.d(TAG,timestamp.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        String mtimestamp = null;
        try {
            mtimestamp = sdf.format(timestamp);
            Log.d(TAG, mtimestamp);

        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertDateToLocalStringUTC", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, "convertDateToLocalStringUTC: " + mtimestamp);

        return mtimestamp;
    }

    public static String convertDateToLocalString(Date timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        String mtimestamp = null;
        try {
            mtimestamp = sdf.format(timestamp);

        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertDateToLocalString", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, e.getMessage());
        }
        return mtimestamp;
    }

    public static Date convertISO8601StrToDate(String dateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        Date isoDate = new Date();
        try {
            isoDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MyCTCADateUtils:convertISO8601StrToDate", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "String To Date ERROR: " + e.getMessage());
            Log.e(TAG, "Full Error: " + e);
        }
        return isoDate;
    }

    public static boolean isYesterday(Date d) {
        return DateUtils.isToday(d.getTime() + DateUtils.DAY_IN_MILLIS);
    }

    public static String getDayDateStr(Date d) {
        String dayDateString;

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        dayDateString = sdf.format(d);
        Log.d(TAG, "dayDateString: " + dayDateString);
        return dayDateString;
    }

    public static String getDayDateTimeStr(Date d) {
        String dayDateTimeString;

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy, h:mm", Locale.getDefault());
        dayDateTimeString = sdf.format(d);
        Log.d(TAG, "dayDateTimeString: " + dayDateTimeString);
        return dayDateTimeString;
    }

    public static String getSlashedDateStr(Date d) {
        String dayDateString;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
        dayDateString = sdf.format(d);
        Log.d(TAG, "dayDateString: " + dayDateString);
        return dayDateString;
    }

    public static String getSlashedDateFullYearStr(Date d) {
        String dayDateString;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        dayDateString = sdf.format(d);
        Log.d(TAG, "dayDateString: " + dayDateString);
        return dayDateString;
    }

    public static String getMonthDateStr(Date d) {
        String monthDateString;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        monthDateString = sdf.format(d);
        Log.d(TAG, "monthDateString: " + monthDateString);
        return monthDateString;
    }

    public static String getMonthDateYearAtTimeStr(Date d) {

        String monthDateYearAtTimeString;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy @ hh:mm aa", Locale.getDefault());
        monthDateYearAtTimeString = sdf.format(d);
        Log.d(TAG, "monthDateYearAtTimeString: " + monthDateYearAtTimeString);
        return monthDateYearAtTimeString;
    }

    public static String getFullMonthString(Date d) {

        String monthDateYearAtTimeString;

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        monthDateYearAtTimeString = sdf.format(d);
        Log.d(TAG, "monthDateYearAtTimeString: " + monthDateYearAtTimeString);
        return monthDateYearAtTimeString;
    }


    public static String getTime(Date d) {

        String time;

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        time = sdf.format(d);
        Log.d(TAG, "monthDateYearAtTimeString: " + time);
        return time;
    }

    public static Date setTimeToMidnight(Date d) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
}
