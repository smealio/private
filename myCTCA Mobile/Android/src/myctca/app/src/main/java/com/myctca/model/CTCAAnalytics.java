package com.myctca.model;

import android.content.ComponentName;

import com.android.volley.VolleyError;

public class CTCAAnalytics {
    private String methodName;
    private String eventName;
    private VolleyError error;
    private String apiUrl;
    private String appointmentDate;
    private Exception exception;
    private String meetingID;
    private long duration;
    private int telehealthErrorCode;
    private ComponentName componentName;
    private Facility facility;

    public CTCAAnalytics(String methodName, String eventName, ComponentName componentName) {
        this.methodName = methodName;
        this.eventName = eventName;
        this.componentName = componentName;
    }

    public CTCAAnalytics(String methodName, String eventName, Facility facility) {
        this.methodName = methodName;
        this.eventName = eventName;
        this.facility = facility;
    }

    public Facility getFacility() {
        return facility;
    }

    public int getTelehealthErrorCode() {
        return telehealthErrorCode;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public CTCAAnalytics(String methodName, String eventName, VolleyError error, String apiUrl) {
        this.methodName = methodName;
        this.eventName = eventName;
        this.error = error;
        this.apiUrl = apiUrl;
    }

    public CTCAAnalytics(String methodName, String eventName, int errorCode, String apiUrl, String meetingID) {
        this.methodName = methodName;
        this.eventName = eventName;
        this.telehealthErrorCode = errorCode;
        this.apiUrl = apiUrl;
        this.meetingID = meetingID;
    }

    public CTCAAnalytics(String methodName, String eventName) {
        this.methodName = methodName;
        this.eventName = eventName;
    }

    public CTCAAnalytics(String methodName, String eventName, String appointmentDate) {
        this.methodName = methodName;
        this.eventName = eventName;
        this.appointmentDate = appointmentDate;
    }

    public CTCAAnalytics(String methodName, String eventName, String meetingID, int errorCode, long duration) {
        this.methodName = methodName;
        this.eventName = eventName;
        this.meetingID = meetingID;
        this.telehealthErrorCode = errorCode;
        this.duration = duration;
    }

    public CTCAAnalytics(String methodName, String eventName, String meetingID, long duration) {
        this.methodName = methodName;
        this.eventName = eventName;
        this.meetingID = meetingID;
        this.duration = duration;
    }


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public VolleyError getError() {
        return error;
    }

    public void setError(VolleyError error) {
        this.error = error;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
