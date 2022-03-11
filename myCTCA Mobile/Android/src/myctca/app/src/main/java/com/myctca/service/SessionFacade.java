package com.myctca.service;

import android.content.Context;

import com.myctca.activity.NavActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.Facility;
import com.myctca.model.ImagingDoc;
import com.myctca.model.ImpersonatedUserProfile;
import com.myctca.model.MedDoc;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.StoredPreferences;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SessionFacade {

    //    Alerts
    public void getAlertMessages(Context context, HomeService.HomeServiceInterface serviceInterface, String purpose, String url) {
        HomeService.getInstance().getAlertMessages(context, serviceInterface, purpose, url);
    }

    public void getImpersonatedAccessToken(Context context, HomeService.HomeServiceInterface serviceInterface, ImpersonatedUserProfile userProfile, int purpose) {
        HomeService.getInstance().getImpersonatedAccessToken(context, serviceInterface, userProfile, purpose);
    }

    public void checkVersions(NavActivity activity) {
        HomeService.getInstance().checkVersions(activity);
    }

    public boolean isMessageUpdateDialogMandatory() {
        return HomeService.getInstance().isMessageUpdateDialogMandatory();
    }

    public boolean showMessageUpdateDialog() {
        return HomeService.getInstance().showMessageUpdateDialog();
    }

    //Appointments
    public void getAppointments(Context context, String purpose, AppointmentService.AppointmentServiceGetListener listener) {
        AppointmentService.getInstance().getAppointments(context, purpose, listener);
    }

    public void clearAppointments() {
        AppointmentService.getInstance().clearAppointments();
    }

    public boolean isAppointmentsEmpty() {
        return AppointmentService.getInstance().isAppointmentsEmpty();
    }

    public void changeAppointmentsRequest(int requestType, String appointmentFormObj, Context context, AppointmentService.AppointmentServicePostListener listener) {
        AppointmentService.getInstance().changeAppointmentsRequest(requestType, appointmentFormObj, context, listener);
    }

    public MyCTCAUserProfile getMyCtcaUserProfile() {
        return UserProfileService.getInstance().getMyCtcaUserProfile();
    }

    public boolean dateTimeIsValid(Calendar cTime, Calendar cDate) {
        return AppointmentService.getInstance().dateTimeIsValid(cTime, cDate);
    }

    public void downloadPdfFile(PdfService.PdfServiceListener listener, Context context, String mUrl, Map<String, String> params) {
        PdfService.getInstance().downloadPdfFile(listener, context, mUrl, params);
    }

    public void downloadAppointmentSchedule(PdfService.PdfServiceListener listener, Context context, String mUrl, String body) {
        PdfService.getInstance().downloadAppointmentSchedule(listener, context, mUrl, body);
    }

    public void downloadMeetingAccessToken(Context context, String meetingId, AppointmentService.AppointmentServicePostListener listener) {
        AppointmentService.getInstance().downloadMeetingAccessToken(context, meetingId, listener);
    }

    //Lab Results
    public void getLabResults(Context context, String purpose, LabResultsService.LabResultsServiceListener listener) {
        LabResultsService.getInstance().getLabResults(context, purpose, listener);
    }

    public void clearLabResults() {
        LabResultsService.getInstance().clearLabResults();
    }

    public boolean isLabResultLessThan24HoursAgo(Date labResultDate) {
        return LabResultsService.getInstance().isLabResultLessThan24HoursAgo(labResultDate);
    }

    //Authentication
    public StoredPreferences getStoredPreferences(Context context) {
        return LoginService.getInstance().getStoredPreferences(context);
    }

    public void loginWithParameters(LoginService.LoginServicePostListener listener, int task, Context context, String username, String password) {
        LoginService.getInstance().loginWithParameters(listener, task, context, username, password);
    }

    public void getUserInfo(String url, Map<String, String> params, LoginService.LoginServiceGetListener listener, Context context, String purpose) {
        LoginService.getInstance().getUserInfo(url, params, listener, context, purpose);
    }

    public void getAllFacilities(String url, LoginService.LoginServiceGetListener listener, Context context, String purpose) {
        LoginService.getInstance().getAllFacilities(url, listener, context, purpose);
    }

    public void successfulLogin(Context context) {
        LoginService.getInstance().successfulLogin(context);
    }

    public void storePassword(String username, String password, boolean isFingerAuthEnabled) {
        LoginService.getInstance().storePassword(username, password, isFingerAuthEnabled);
    }

    public Facility getPreferredFacility() {
        return AppSessionManager.getInstance().getPreferredFacility();
    }

    public String getPrimaryFacility() {
        return LoginService.getInstance().getPrimaryService();
    }

    public int getUserType() {
        return LoginService.getInstance().getUserType();
    }

    //MAILS
    public void downloadMail(Context context, Map<String, String> params, String purpose, MailService.MailServiceGetListener listener) {
        MailService.getInstance().downloadMail(context, params, purpose, listener);
    }

    public void clearMails(String purpose) {
        MailService.getInstance().clearMails(purpose);
    }

    public void setOnServer(Context context, MailService.MailServicePostListener listener, String body, int task) {
        MailService.getInstance().setOnServer(context, listener, task, body);
    }

    //common
    public void downloadCareTeams(CommonService.CommonServiceListener listener, Context context, String purpose) {
        CommonService.getInstance().downloadCareTeams(listener, context, purpose);
    }

    public void downloadAllFacilityInfo(Context context, FacilityInfoAllService.FacilityInfoAllListener listener, String purpose) {
        FacilityInfoAllService.getInstance().downloadFacilityInfoAll(context, listener, purpose);
    }

    //health history
    public void downloadHealthHistoryTypeList(HealthHistoryService.HealthHistoryServiceListener listener, Context context, String url, String purpose) {
        HealthHistoryService.getInstance().downloadHealthHistoryTypeList(listener, context, url, purpose);
    }

    public void clearHealthHistoryType(String purpose) {
        HealthHistoryService.getInstance().clearHealthHistoryType(purpose);
    }

    public <T> List<T> getHealthHistoryType(String purpose) {
        return HealthHistoryService.getInstance().getHealthHistoryType(purpose);
    }

    public void submitRequestRenewalForm(HealthHistoryService.HealthHistoryServicePostListener listener, Context context, String json) {
        HealthHistoryService.getInstance().submitRequestRenewalForm(listener, context, json);
    }

    public void cancelRequest() {
        AppointmentService.getInstance().cancelRequest();
    }

    public List<MyCTCAProxy> getProxies() {
        return HomeService.getInstance().getProxies();
    }

    //activity logs
    public void downloadActivityLogs(Context context, ActivityLogsService.ActivityLogsListener listener, int skip, int take, String purpose) {
        ActivityLogsService.getInstance().downloadActivityLogs(context, listener, skip, take, purpose);
    }

    public void applyFilterOnActivityLogs(String etFilterUsername, String etFilterMessage, String selectedDate, String nextDate) {
        ActivityLogsService.getInstance().applyFilterOnActivityLogs(etFilterUsername, etFilterMessage, selectedDate, nextDate);
    }

    public void getSelectedAndNextDate(ActivityLogsService.ActivityLogsDatesListener listener, Date date) {
        ActivityLogsService.getInstance().getSelectedAndNextDate(listener, date);
    }

    //send message
    public void sendMessage(Context context, SendMessageService.SendMessageInterface sendMessageInterface, String body) {
        SendMessageService.getInstance().sendMessage(context, sendMessageInterface, body);
    }

    //patient reported
    public void downloadPatientSymptomInventory(PatientReportedService.PatientReportedServiceListener listener, Context context, String url, String purpose) {
        PatientReportedService.getInstance().downloadPatientSymptomInventory(listener, context, url, purpose);
    }

    //external links
    public void downloadAllExternalLinks(Context context, MyResourcesService.MyResourceServiceListener listener, String purpose) {
        MyResourcesService.getInstance().downloadAllExternalLinks(context, listener, purpose);
    }

    public String getSurveyUrl() {
        return HomeService.getInstance().getSurveyUrl();
    }

    //forms library
    public void getANNCFormInfo(MoreFormsLibraryService.MoreFormsLibraryANNCListenerGet listener, String url, Context context, String purpose) {
        MoreFormsLibraryService.getInstance().fetchANNCData(listener, url, context, purpose);
    }

    public void getROIFormInfo(MoreFormsLibraryService.MoreFormsLibraryROIListenerGet listener, String url, Map<String, String> params, Context context, String purpose) {
        MoreFormsLibraryService.getInstance().fetchROIData(listener, url, params, context, purpose);
    }

    public void submitForm(int formType, String url, String body, Context context, MoreFormsLibraryService.MoreFormsLibraryListenerPost listenerPost) {
        MoreFormsLibraryService.getInstance().submitForm(formType, url, body, context, listenerPost);
    }

    public void getMedicalDocumentsData(Context context, MoreMedicalDocumentsService.MoreMedDocListenerGet listener, String purpose, String url, Map<String, String> params) {
        MoreMedicalDocumentsService.getInstance().getMedicalDocumentsData(context, listener, purpose, url, params);
    }

    public void clearClinicalSummaries() {
        MoreMedicalDocumentsService.getInstance().clearClinicalSummaries();
    }

    public void postClinicalSummaryData(Context context, MoreMedicalDocumentsService.MoreMedDocClinicalSummaryListenerPost listenerPost, String url, int purpose, String body, Map<String, String> params) {
        MoreMedicalDocumentsService.getInstance().postClinicalSummaryData(context, listenerPost, url, purpose, body, params);
    }

    public void downloadClinicalSummary(MoreMedicalDocumentsService.MoreMedDocClinicalSummaryListenerPdf listenerPdf, Context context, String body) {
        MoreMedicalDocumentsService.getInstance().downloadClinicalSummary(listenerPdf, context, body);
    }

    public List<MedDoc> getMedDocs(String medDocType) {
        return MoreMedicalDocumentsService.getInstance().getMedDocs(medDocType);
    }

    public void clearMedDocs(String medDocType) {
        MoreMedicalDocumentsService.getInstance().clearDocsArray(medDocType);
    }

    public List<ImagingDoc> getmImagingDocs() {
        return MoreMedicalDocumentsService.getInstance().getImagingDocs();
    }

    public void downloadContactInfo(CommonService.CommonServiceListener listener, Context context, String purpose) {
        CommonService.getInstance().downloadContactInfo(listener, context, purpose);
    }

    public String getUserContactNumber() {
        return CommonService.getInstance().getUserContactNumber();
    }

    public String getUserEmail() {
        return CommonService.getInstance().getUserEmail();
    }

    public Date getUserDob() {
        return CommonService.getInstance().getUserDob();
    }
}