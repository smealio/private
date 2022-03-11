package com.myctca.model;

import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.microsoft.appcenter.analytics.Analytics;
import com.myctca.MyCTCA;
import com.myctca.common.AppSessionManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CTCAAnalyticsManager {
    private static final List<String> eventList = new ArrayList<>(
            Arrays.asList(
                    "LOGIN_TIMEOUT_ERROR",
                    "LOGIN_INCORRECT_CREDENTIALS",
                    "LOGIN_LOCKED_ACCOUNT",
                    "LOGIN_REST_API_FAILURE",
                    "EXCEPTION_REST_API",
                    "EXCEPTION_SYSTEM_THROWN",
                    "ALERT_SIGNIN_FAIL",
                    "ALERT_SEND_MESSAGE_FAIL",
                    "ALERT_SEND_MESSAGE_SUCCESS",
                    "ALERT_APPOINTMENTS_RESCHEDULE_SUCCESS",
                    "ALERT_APPOINTMENTS_RESCHEDULE_FAIL",
                    "ALERT_APPOINTMENTS_CANCEL_SUCCESS",
                    "ALERT_APPOINTMENTS_CANCEL_FAIL",
                    "ALERT_APPOINTMENTS_REQUEST_SUCCESS",
                    "ALERT_APPOINTMENTS_REQUEST_FAIL",
                    "ALERT_NEW_MAIL_SUCCESS",
                    "ALERT_NEW_MAIL_FAIL",
                    "ALERT_CS_DOWNLOAD_SUCCESS",
                    "ALERT_CS_DOWNLOAD_FAIL",
                    "ALERT_CS_TRANSMIT_SUCCESS",
                    "ALERT_CS_TRANSMIT_FAIL",
                    "ALERT_PRESCRIPTION_RENEWAL_SUCCESS",
                    "ALERT_PRESCRIPTION_RENEWAL_FAIL",
                    "ALERT_ROI_SUBMIT_SUCCESS",
                    "ALERT_ROI_SUBMIT_FAIL",
                    "ALERT_ROI_DOWNLOAD_FAIL",
                    "ALERT_ANNC_SUBMIT_SUCCESS",
                    "ALERT_ANNC_SUBMIT_FAIL",
                    "ALERT_ANNC_DOWNLOAD_FAIL",
                    "ACTION_TELEHEALTH_JON_NOW_TAP",
                    "PAGE_TELEHEALTH_MEETING",
                    "DURATION_TELEHEALTH_JOIN_MEETING",
                    "DURATION_TELEHEALTH_PATIENT_IN_LOBBY",
                    "DURATION_TELEHEALTH_TOTAL_MEETING",
                    "EXCEPTION_TELEHEALTH_MEETING_INTERRUPTED",
                    "ACTION_TELEHEALTH_VIDEO_ON",
                    "ACTION_TELEHEALTH_JON_ON_WEB",
                    "ACTION_APPOINTMENTS_PDF_SHARE_TAP",
                    "ACTION_APPOINTMENTS_PDF_CLOSE_SHARE_TAP",
                    "ACTION_APPTS_PDF_SHARE_SELECTED_APP_TAP",
                    "ACTION_APPOINTMENTS_PDF_PRINT_TAP",

                    "ACTION_APPOINTMENTS_REQUEST_TAP",
                    "PAGE_APPT_NEW_REQ_REASON_VIEW",
                    "PAGE_APPT_NEW_REQ_PREF_DATETIME_VIEW",
                    "PAGE_APPT_NEW_REQ_COMM_PREF_VIEW",
                    "PAGE_APPT_NEW_REQ_ADDI_COMMENTS_VIEW",
                    "PAGE_APPT_NEW_REQ_SUMMARY_VIEW",
                    "ACTION_APPT_NEW_REQ_SUBMIT_TAP",
                    "APPT_NEW_REQ_CALL_ME_SELECTION",
                    "APPT_NEW_REQ_EMAIL_ME_SELECTION",
                    "APPT_NEW_REQ_MORNING_SLOT_SELECTION",
                    "APPT_NEW_REQ_NOON_SLOT_SELECTION",
                    "APPT_NEW_REQ_ALL_DAY_SLOT_SELECTION",
                    "ACTION_APPT_NEW_REQ_LEAVE_TAP",

                    "ACTION_APPOINTMENTS_RESCHEDULE_TAP",
                    "PAGE_APPT_RESCHED_REQ_PREF_DATETIME_VIEW",
                    "PAGE_APPT_RESCHED_REQ_COMM_PREF_VIEW",
                    "PAGE_APPT_RESCHED_REQ_ADDI_COMMENTS_VIEW",
                    "PAGE_APPT_RESCHED_REQ_SUMMARY_VIEW",
                    "ACTION_APPT_RESCHED_REQ_SUBMIT_TAP",//
                    "APPT_RESCHED_REQ_CALL_ME_SELECTION",
                    "APPT_RESCHED_REQ_EMAIL_ME_SELECTION",
                    "APPT_RESCHED_REQ_MORNING_SLOT_SELECTION",
                    "APPT_RESCHED_REQ_NOON_SLOT_SELECTION", //
                    "APPT_RESCHED_REQ_ALL_DAY_SLOT_SELECTION",
                    "ACTION_APPT_RESCHED_REQ_LEAVE_TAP",

                    "ACTION_APPOINTMENTS_CANCEL_TAP",
                    "PAGE_APPT_CANCEL_REQ_REASON_VIEW",
                    "PAGE_APPT_CANCEL_REQ_COMM_PREF_VIEW",
                    "PAGE_APPT_CANCEL_REQ_ADDI_COMMENTS_VIEW",
                    "PAGE_APPT_CANCEL_REQ_SUMMARY_VIEW",
                    "ACTION_APPT_CANCEL_REQ_SUBMIT_TAP",
                    "APPT_CANCEL_REQ_CALL_ME_SELECTION",
                    "APPT_CANCEL_REQ_EMAIL_ME_SELECTION",
                    "ACTION_APPT_CANCEL_REQ_LEAVE_TAP",

                    "ACTION_IMP_NUM_CALL_GENERAL_ENQ_TAP",
                    "ACTION_IMP_NUM_CALL_TECH_SUP_TAP",
                    "ACTION_IMP_NUM_CALL_CARE_MNG_TAP",
                    "ACTION_IMP_NUM_CALL_SCHEDULING_TAP",
                    "ACTION_IMP_NUM_CALL_TRAVEL_TAP",
                    "ACTION_IMP_NUM_CALL_MEDICAL_REC_TAP",
                    "ACTION_IMP_NUM_CALL_FINANCIAL_TAP",
                    "ACTION_IMP_NUM_CALL_BILLING_TAP",
                    "ACTION_IMP_NUM_CALL_PHARMACY_TAP",

                    "ACTION_IMP_NUM_CALL_FAC_ATLANTA_TAP",
                    "ACTION_IMP_NUM_CALL_FAC_CHICAGO_TAP",
                    "ACTION_IMP_NUM_CALL_FAC_PHOENIX_TAP",
                    "ACTION_IMP_NUM_CALL_FAC_CHI_DOT_TAP",
                    "ACTION_IMP_NUM_CALL_FAC_PH_NRTH_TAP",
                    "ACTION_IMP_NUM_CALL_FAC_SCOTTSD_TAP",
                    "ACTION_IMP_NUM_CALL_FAC_GURNEE_TAP"));


    private static FirebaseAnalytics firebaseAnalytics;
    private static Bundle bundle;

    public static void createEvent(String methodName, String eventName, VolleyError error, String apiUrl) {
        bundle = new Bundle();
        firebaseAnalytics = FirebaseAnalytics.getInstance(MyCTCA.getAppContext());
        if (eventList.contains(eventName)) {
            Map<String, String> map = new HashMap<>();
            Integer statusCode;

            //error-code
            String errorMessage = "";
            if (error != null) {
                if (error.networkResponse != null && error.networkResponse.statusCode > 0)
                    statusCode = error.networkResponse.statusCode;
                else
                    statusCode = 999;
                map.put("statusCode", statusCode.toString());
                bundle.putString("statusCode", statusCode.toString());
            }

            //error-exception
            if (error != null) {
                errorMessage = error.getLocalizedMessage();
                map.put("exception", errorMessage);
                bundle.putString("exception", errorMessage);
            }

            //user-type
            if (eventName.equals(CTCAAnalyticsConstants.LOGIN_SUCCESSFUL)) {
                String userType = "";
                if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER) {
                    userType = "CARE_GIVER";
                } else if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.PATIENT) {
                    userType = "PATIENT";
                }
                map.put("userType", userType);
                bundle.putString("userType", userType);
            }

            IdentityUser identityUser = AppSessionManager.getInstance().getIdentityUser();
            if (identityUser != null && identityUser.getUserId() != 0) {
                Integer userId = identityUser.getUserId();
                map.put("identityUserID", userId.toString());
                bundle.putString("identityUserID", userId.toString());
            }
            if (!TextUtils.isEmpty(apiUrl)) {
                if (eventName.equals(CTCAAnalyticsConstants.ACTION_APPTS_PDF_SHARE_SELECTED_APP_TAP)) {
                    map.put("appPackageName", apiUrl);
                    bundle.putString("appPackageName", apiUrl);
                } else {
                    map.put("api", apiUrl);
                    bundle.putString("api", apiUrl);
                }
            }
            if (!TextUtils.isEmpty(methodName)) {
                map.put("methodName", methodName);
                bundle.putString("methodName", methodName);
            }
            Analytics.trackEvent(eventName, map);
            firebaseAnalytics.logEvent(eventName, bundle);
        }
    }

    public static void createEventForSystemExceptions(String methodName, String eventName, Exception error) {
        bundle = new Bundle();
        Map<String, String> map = new HashMap<>();
        Integer statusCode = error instanceof ParseException ? 998 : 999;
        map.put("statusCode", statusCode.toString());
        bundle.putString("statusCode", statusCode.toString());

        if (!TextUtils.isEmpty(methodName)) {
            map.put("methodName", methodName);
            bundle.putString("methodName", methodName);
        }
        IdentityUser identityUser = AppSessionManager.getInstance().getIdentityUser();
        if (identityUser != null) {
            Integer userId = identityUser.getUserId();
            map.put("identityUserID", userId.toString());
            bundle.putString("identityUserID", userId.toString());
        }
        //error-exception
        if (error != null) {
            String errorMessage = error.getLocalizedMessage();
            map.put("exception", errorMessage);
            bundle.putString("exception", errorMessage);
        }
        Analytics.trackEvent(eventName, map);
        firebaseAnalytics.logEvent(eventName, bundle);
    }

    public static void createScreenViewEvent(CTCAAnalytics ctcaAnalytics) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(MyCTCA.getAppContext());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, ctcaAnalytics.getEventName());
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, ctcaAnalytics.getMethodName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    public static void createCommonEvent(CTCAAnalytics ctcaAnalytics) {
        bundle = new Bundle();
        firebaseAnalytics = FirebaseAnalytics.getInstance(MyCTCA.getAppContext());
        if (eventList.contains(ctcaAnalytics.getEventName())) {
            Map<String, String> map = new HashMap<>();
            Integer statusCode;

            //error-code
            if (ctcaAnalytics.getTelehealthErrorCode() != 0) {
                map.put("statusCode", String.valueOf(ctcaAnalytics.getTelehealthErrorCode()));
                bundle.putString("statusCode", String.valueOf(ctcaAnalytics.getTelehealthErrorCode()));
            }

            //error-code
            String errorMessage = "";
            if (ctcaAnalytics.getError() != null) {
                if (ctcaAnalytics.getError().networkResponse != null && ctcaAnalytics.getError().networkResponse.statusCode > 0)
                    statusCode = ctcaAnalytics.getError().networkResponse.statusCode;
                else
                    statusCode = 999;
                map.put("statusCode", statusCode.toString());
                bundle.putString("statusCode", statusCode.toString());
            }

            //error-exception
            if (ctcaAnalytics.getError() != null) {
                errorMessage = ctcaAnalytics.getError().getLocalizedMessage();
                map.put("exception", errorMessage);
                bundle.putString("exception", errorMessage);
            }

            //user-type
            if (ctcaAnalytics.getEventName().equals(CTCAAnalyticsConstants.LOGIN_SUCCESSFUL)) {
                String userType = "";
                if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER) {
                    userType = "CARE_GIVER";
                } else if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.PATIENT) {
                    userType = "PATIENT";
                }
                map.put("userType", userType);
                bundle.putString("userType", userType);
            }

            IdentityUser identityUser = AppSessionManager.getInstance().getIdentityUser();
            if (identityUser != null && identityUser.getUserId() != 0) {
                Integer userId = identityUser.getUserId();
                map.put("identityUserID", userId.toString());
                bundle.putString("identityUserID", userId.toString());
            }
            if (ctcaAnalytics.getComponentName() != null) {
                map.put("appPackageName", ctcaAnalytics.getComponentName().getPackageName());
                bundle.putString("appPackageName", ctcaAnalytics.getComponentName().getPackageName());
            }
            if (!TextUtils.isEmpty(ctcaAnalytics.getApiUrl())) {
                map.put("api", ctcaAnalytics.getApiUrl());
                bundle.putString("api", ctcaAnalytics.getApiUrl());
            }
            if (!TextUtils.isEmpty(ctcaAnalytics.getMethodName())) {
                map.put("methodName", ctcaAnalytics.getMethodName());
                bundle.putString("methodName", ctcaAnalytics.getMethodName());
            }

            if (!TextUtils.isEmpty(ctcaAnalytics.getMeetingID())) {
                map.put("meetingID", ctcaAnalytics.getMeetingID());
                bundle.putString("meetingID", ctcaAnalytics.getMeetingID());
            }
            if (ctcaAnalytics.getDuration() > 0) {
                map.put("durationInSec", String.valueOf(ctcaAnalytics.getDuration()));
                bundle.putString("durationInSec", String.valueOf(ctcaAnalytics.getDuration()));
            }
            if (ctcaAnalytics.getException() != null) {
                String message = ctcaAnalytics.getException().getLocalizedMessage();
                map.put("exception", message);
                bundle.putString("exception", message);
            }
            if (ctcaAnalytics.getAppointmentDate() != null) {
                map.put("appointmentDate", ctcaAnalytics.getAppointmentDate());
                bundle.putString("appointmentDate", ctcaAnalytics.getAppointmentDate());
            }

            if (ctcaAnalytics.getFacility() != null) {
                map.put("Facility", ctcaAnalytics.getFacility().getShortDisplayName());
                bundle.putString("Facility", ctcaAnalytics.getFacility().getShortDisplayName());
            }

            Analytics.trackEvent(ctcaAnalytics.getEventName(), map);
            firebaseAnalytics.logEvent(ctcaAnalytics.getEventName(), bundle);
        }
    }
}
