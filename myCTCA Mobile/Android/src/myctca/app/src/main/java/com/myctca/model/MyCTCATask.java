package com.myctca.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tomackb on 2/9/18.
 */

public class MyCTCATask {

    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({RELEASE_OF_INFORMATION, CLINICAL_SUMMARY_DETAIL, TRANSMIT_CLINICAL_SUMMARY, PRESCRIPTION_REFILL_REQUEST})
    // Create an interface for validating int types
    public @interface MoreTypeDef {}

    public static final int RELEASE_OF_INFORMATION = 0;
    public static final int CLINICAL_SUMMARY_DETAIL = 1;
    public static final int TRANSMIT_CLINICAL_SUMMARY = 2;
    public static final int PRESCRIPTION_REFILL_REQUEST = 3;
    public static final int ANNC = 4;
    public static final int DOWNLOAD_CLINICAL_SUMMARY = 5;
    public static final int SAVE_USER_PREFERENCES = 6;
    public static final int APPT_RESCHEDULE = 7;
    public static final int APPT_CANCEL = 8;
    public static final int APPT_NEW = 9;
    public static final int TOKEN = 10;
    public static final int IMPERSONATION_USER_PROFILE = 11;
    public static final int SEND_MESSAGE = 12;
    public static final int PURPOSE_REVERT_PATIENT_PROFILE = 13;
    public static final int PURPOSE_ACCESS_PATIENT_PROFILE = 14;
    public static final int MEETING_ACCESS_TOKEN = 15;
    public static final int APPOINTMENT_SCHEDULE = 16;


    public final int taskType;

    public MyCTCATask(@MoreTypeDef int taskType) {
        this.taskType = taskType;
    }

}
