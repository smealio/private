package com.myctca.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tomackb on 2/26/18.
 */

public class UserPermissions {

    //todo
    public static final String ADD_ALTERNATE_CONTACT = "Add Alternate Contact";
    public static final String CANCEL_APPOINTMENT = "Cancel Appointment";
    public static final String DOWNLOAD_CCDA_DOCUMENTS = "Download CCDA Documents";
    //todo
    public static final String EDIT_ALTERNATE_CONTACT = "Edit Alternate Contact";
    //todo
    public static final String EDIT_CONTACT = "Edit Contact";
    //todo
    public static final String MANAGE_NOTIFICATIONS = "Manage Notifications";
    //TODO
    public static final String MANAGE_PREFERENCES = "Manage Preferences";
    public static final String REQUEST_APPOINTMENT = "Request Appointment";
    public static final String REQUEST_PRESCRIPTION_REFILL = "Request Prescription Refill";
    public static final String RESCHEDULE_APPOINTMENT = "Reschedule Appointment";
    public static final String SEND_SECURE_MESSAGES = "Send Secure Messages";
    public static final String SUBMIT_ROI_FORM = "Submit ROI Form";
    public static final String SUBMIT_ANNC_FORM = "Submit ANNC Form";
    public static final String TRANSMIT_CCDA_DOCUMENTS = "Transmit CCDA Documents";
    public static final String VIEW_ALLERGIES = "View Allergies";
    public static final String VIEW_APPOINTMENTS = "View Appointments";
    public static final String VIEW_BILLPAY = "View Bill Pay";
    public static final String VIEW_CARE_PLAN = "View Care Plan";
    public static final String VIEW_CCDA_DOCUMENTS = "View CCDA Documents";
    public static final String VIEW_CLINICAL_DOCUMENTS = "View Clinical Documents";
    public static final String VIEW_EXTERNAL_LINKS = "View External Links";
    public static final String VIEW_FORMS_LIBRARY = "View Forms Library";
    public static final String VIEW_HEALTH_HISTORY = "View Health History";
    public static final String VIEW_HEALTH_ISSUES = "View Health Issues";
    public static final String VIEW_IMAGING_DOCUMENTS = "View Imaging Documents";
    public static final String VIEW_IMMUNIZATIONS = "View Immunizations";
    public static final String VIEW_INTEGRATIVE_DOCUMENTS = "View Integrative Documents";
    public static final String VIEW_INTERACTIVE = "View Interactive";
    public static final String VIEW_LAB_RESULTS = "View Lab Results";
    //TODO
    public static final String VIEW_MEDICAL_CONTACTS = "View Medical Contacts";
    public static final String VIEW_MEDICAL_DOCUMENTS = "View Medical Documents";
    public static final String VIEW_MY_ACCOUNT = "View My Account";
    public static final String VIEW_PATIENT_REPORTED_DOCUMENTS = "View Patient Reported Documents";
    public static final String VIEW_PRESCRIPTIONS = "View Prescriptions";
    //TODO
    public static final String VIEW_QUICK_LINKS = "View Quick Links";
    public static final String VIEW_RADIATION_LINKS = "View Radiation Documents";
    public static final String VIEW_SECURE_MESSAGES = "View Secure Messages";
    public static final String VIEW_SYSTEM_ALERT_MESSAGES = "View System Alert Messages";
    public static final String VIEW_USER_LOGS = "View User Logs";
    public static final String VIEW_VITAL_SIGNS = "View Vital Signs";
    public static final String SUBMIT_PATIENT_REPORTED_DOCUMENTS = "Submit Patient Reported Documents";
    public final String userPermission;

    public UserPermissions(@UserPermissions.UserPermissionsDef String userPermission) {
        this.userPermission = userPermission;
    }

    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @StringDef({ADD_ALTERNATE_CONTACT, CANCEL_APPOINTMENT, DOWNLOAD_CCDA_DOCUMENTS, EDIT_ALTERNATE_CONTACT, EDIT_CONTACT, MANAGE_NOTIFICATIONS, MANAGE_PREFERENCES, REQUEST_APPOINTMENT, REQUEST_PRESCRIPTION_REFILL, RESCHEDULE_APPOINTMENT, SEND_SECURE_MESSAGES, SUBMIT_ROI_FORM, TRANSMIT_CCDA_DOCUMENTS, VIEW_ALLERGIES, VIEW_APPOINTMENTS, VIEW_BILLPAY, VIEW_CARE_PLAN, VIEW_CCDA_DOCUMENTS, VIEW_CLINICAL_DOCUMENTS, VIEW_EXTERNAL_LINKS, VIEW_FORMS_LIBRARY, SUBMIT_PATIENT_REPORTED_DOCUMENTS, VIEW_HEALTH_HISTORY, VIEW_HEALTH_ISSUES, VIEW_IMAGING_DOCUMENTS, VIEW_IMMUNIZATIONS, VIEW_INTEGRATIVE_DOCUMENTS, VIEW_INTERACTIVE, VIEW_LAB_RESULTS, VIEW_MEDICAL_CONTACTS, VIEW_MEDICAL_DOCUMENTS, VIEW_MY_ACCOUNT, VIEW_PATIENT_REPORTED_DOCUMENTS, VIEW_PRESCRIPTIONS, VIEW_QUICK_LINKS, VIEW_RADIATION_LINKS, VIEW_SECURE_MESSAGES, VIEW_SYSTEM_ALERT_MESSAGES, VIEW_USER_LOGS, VIEW_VITAL_SIGNS})
    // Create an interface for validating int types
    public @interface UserPermissionsDef {
    }

}
