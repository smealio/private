package com.myctca.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.interfaces.PostListener;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MeetingAccessTokenJson;
import com.myctca.model.MyCTCATask;
import com.myctca.network.GetClient;
import com.myctca.network.PostClient;
import com.myctca.network.VolleyService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.myctca.util.GeneralUtil.noNull;

public class AppointmentService implements GetListener, PostListener {
    private static final String TAG = AppointmentService.class.getSimpleName();
    private static final String ERROR = "error";
    private static final String APPT_UPCOMING = "UPCOMING";
    private static final String APPT_PAST = "PAST";

    private static AppointmentService appointmentService;
    protected AppointmentServiceGetListener getListener;
    private AppointmentServicePostListener postListener;
    private Context context;
    private String meetingId = "";

    public static AppointmentService getInstance() {
        if (appointmentService == null) {
            return new AppointmentService();
        }
        return appointmentService;
    }

    public void getAppointments(Context context, String purpose, AppointmentServiceGetListener listener) {
        this.getListener = listener;
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_appointments);
        this.context = context;
        if (AppSessionManager.getInstance().getAppointments().isEmpty()) {
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        } else {
            if (purpose.equals(APPT_UPCOMING))
                listener.notifyFetchSuccess(AppSessionManager.getInstance().getUpcomingAppointments(), AppSessionManager.getInstance().getUpcomingSections(), purpose);
            else if (purpose.equals(APPT_PAST))
                listener.notifyFetchSuccess(AppSessionManager.getInstance().getPastAppointments(), AppSessionManager.getInstance().getPastSections(), purpose);
        }
    }

    public void clearAppointments() {
        AppSessionManager.getInstance().getAppointments().clear();
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            Appointment[] tempAppts = new Gson().fromJson(parseSuccess, Appointment[].class);
            AppSessionManager.getInstance().setAppointments(new LinkedList<>(Arrays.asList(tempAppts)));
            Log.d(TAG, "Appointments retrieved: " + AppSessionManager.getInstance().getAppointments().size());
            buildUpcomingAndPastData();
            returnAppointmentsData(purpose);
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception:" + exception);
            getListener.notifyFetchError(context.getString(R.string.error_400), purpose);
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.d("Error.Response", noNull(error.getMessage()));
        getListener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    protected void returnAppointmentsData(String purpose) {
        if (purpose.equals(APPT_UPCOMING))
            getListener.notifyFetchSuccess(AppSessionManager.getInstance().getUpcomingAppointments(), AppSessionManager.getInstance().getUpcomingSections(), purpose);
        else if (purpose.equals(APPT_PAST))
            getListener.notifyFetchSuccess(AppSessionManager.getInstance().getPastAppointments(), AppSessionManager.getInstance().getPastSections(), purpose);
    }

    protected void buildUpcomingAndPastData() {
        Log.d(TAG, "buildUpcomingAndPastData");
        List<Appointment> appointments = AppSessionManager.getInstance().getAppointments();
        if (!appointments.isEmpty()) {
            AppSessionManager.getInstance().clearPastAndUpcomingAppointments();

            // Separate Appointments by date
            List<Appointment> allUpcomingAppt = new ArrayList<>();
            List<Appointment> allPastAppt = new ArrayList<>();
            for (Appointment appt : appointments) {
                if (appt != null && appt.getUpcoming()) {
                    allUpcomingAppt.add(appt);
                } else {
                    allPastAppt.add(appt);
                }
            }
            if (!allUpcomingAppt.isEmpty()) {
                buildUpcomingApptData(allUpcomingAppt);
            }
            if (!allPastAppt.isEmpty()) {
                buildPastApptData(allPastAppt);
            }
        }
    }

    protected void buildUpcomingApptData(List<Appointment> upcomingAppts) {
        String currDate = "";
        String apptDate;

        Map<String, List<Appointment>> upcomingAppointments = AppSessionManager.getInstance().getUpcomingAppointments();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        for (Appointment appt : upcomingAppts) {
            Date startDate = appt.getStartDate();
            apptDate = sdf.format(startDate);

            if (!apptDate.equals(currDate)) {
                currDate = apptDate;
                AppSessionManager.getInstance().getUpcomingSections().add(currDate);
            }
            List<Appointment> currAppts = new ArrayList<>();
            boolean flag = upcomingAppointments.containsKey(currDate);
            if (flag) {
                currAppts = upcomingAppointments.get(currDate);
            }
            currAppts.add(appt);
            upcomingAppointments.put(currDate, currAppts);
        }
        AppSessionManager.getInstance().setUpcomingAppointments(upcomingAppointments);
        Log.d(TAG, "mUpcomingSections: " + AppSessionManager.getInstance().getUpcomingSections().size());
        Log.d(TAG, "mUpcomingAppts: " + AppSessionManager.getInstance().getUpcomingAppointments().size());
    }

    protected void buildPastApptData(List<Appointment> pastAppts) {
        String currDate = "";
        String apptDate;
        Log.d(TAG, "buildPastApptData");

        // Reverse Sort the pastAppts
        reverseSortPastAppointments(pastAppts);

        Map<String, List<Appointment>> pastAppointments = AppSessionManager.getInstance().getPastAppointments();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        for (Appointment appt : pastAppts) {
            Date startDate = appt.getStartDate();
            apptDate = sdf.format(startDate);

            if (!apptDate.equals(currDate)) {
                currDate = apptDate;
                AppSessionManager.getInstance().getPastSections().add(currDate);
            }
            List<Appointment> currAppts = new ArrayList<>();
            if (pastAppointments.containsKey(currDate)) {
                currAppts = pastAppointments.get(currDate);
            }
            currAppts.add(appt);
            pastAppointments.put(currDate, currAppts);
        }
        AppSessionManager.getInstance().setPastAppointments(pastAppointments);
        Log.d(TAG, "mPastSections: " + AppSessionManager.getInstance().getPastSections().size());
        Log.d(TAG, "mPastAppts: " + AppSessionManager.getInstance().getPastAppointments().size());
    }

    protected void reverseSortPastAppointments(List<Appointment> pastAppts) {
        Collections.sort(pastAppts, (a1, a2) -> a1.getStartDateLocal().compareTo(a2.getStartDateLocal()));
        Collections.reverse(pastAppts);
    }

    public boolean isAppointmentsEmpty() {
        return AppSessionManager.getInstance().getAppointments().isEmpty();
    }

    public void changeAppointmentsRequest(int requestType, String appointmentFormObj, Context context, AppointmentServicePostListener listener) {
        this.postListener = listener;
        this.context = context;
        // Get URL
        String url = BuildConfig.myctca_server;
        int task;
        if (requestType == AppointmentRequest.APPT_RESCHEDULE) {
            url += context.getString(R.string.myctca_reschedule_appointmentsv2);
            task = MyCTCATask.APPT_RESCHEDULE;
        } else if (requestType == AppointmentRequest.APPT_CANCEL) {
            url += context.getString(R.string.myctca_cancel_appointmentsv2);
            task = MyCTCATask.APPT_CANCEL;
        } else {
            url += context.getString(R.string.myctca_new_appointmentsv2);
            task = MyCTCATask.APPT_NEW;
        }
        Log.d(TAG, "submitForm url: " + url);

        // Request a string response
        PostClient postClient = new PostClient(this, context, task);
        postClient.sendData(url, appointmentFormObj, null);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
        Log.d(TAG, task + " Appointment Request Response: " + response);
        Log.d(TAG, task + " Appointment JSON: " + responseJson);
        if (task == MyCTCATask.MEETING_ACCESS_TOKEN) {
            try {
                MeetingAccessTokenJson json = new Gson().fromJson(responseJson, MeetingAccessTokenJson.class);
                postListener.notifyMeetingAccessToken(json);
            } catch (JsonParseException exception) {
                postListener.notifyPostError(new VolleyError(), context.getString(R.string.error_400), task);
            }
        } else if (responseJson.has(ERROR)) {
            postListener.notifyPostSuccess(responseJson.has(ERROR), responseJson.get(ERROR).toString());
        } else {
            postListener.notifyPostSuccess(responseJson.has(ERROR), "");
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        if (task == MyCTCATask.MEETING_ACCESS_TOKEN) {
            String url = context.getString(R.string.myctca_get_teams_access_token);
            int statusCode;
            if (error.networkResponse != null && error.networkResponse.statusCode > 0)
                statusCode = error.networkResponse.statusCode;
            else
                statusCode = 999;
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentService:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, statusCode, url, meetingId));
        }
        if(TextUtils.isEmpty(message))
            message = context.getString(R.string.error_400);
        // Error handling
        Log.d(TAG, "Something went wrong while trying to submit change appointment request! Error: " + error.toString() + "::" + error.getLocalizedMessage());
        postListener.notifyPostError(error, message, task);
    }

    public boolean dateTimeIsValid(Calendar cTime, Calendar cDate) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.SECOND, cTime.get(Calendar.SECOND));
        selectedDate.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));
        selectedDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
        selectedDate.set(Calendar.MONTH, cDate.get(Calendar.MONTH));
        selectedDate.set(Calendar.DAY_OF_MONTH, cDate.get(Calendar.DAY_OF_MONTH));
        selectedDate.set(Calendar.YEAR, cDate.get(Calendar.YEAR));

        // Valid date 24 hours from now
        // Get tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        Log.d(TAG, "dateIsValid tomorrow: " + tomorrow);

        return selectedDate.getTime().after(tomorrow);
    }

    public void cancelRequest() {
        VolleyService service = VolleyService.getVolleyService(context);
        service.cancelRequest();
    }

    public void downloadMeetingAccessToken(Context context, String meetingId, AppointmentServicePostListener listener) {
        String url = context.getString(R.string.myctca_get_teams_access_token);
        this.postListener = listener;
        this.context = context;
        this.meetingId = meetingId;
        PostClient postClient = new PostClient(this, context, MyCTCATask.MEETING_ACCESS_TOKEN);
        postClient.sendData(url, null, null);
    }

    public interface AppointmentServiceGetListener {
        void notifyFetchSuccess(Map<String, List<Appointment>> listMap, List<String> apptSections, String purpose);

        void notifyFetchError(String error, String purpose);
    }

    public interface AppointmentServicePostListener {
        void notifyPostSuccess(boolean ifError, String error);

        void notifyPostError(VolleyError error, String message, int task);

        void notifyMeetingAccessToken(MeetingAccessTokenJson json);
    }
}
