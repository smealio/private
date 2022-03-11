package com.myctca.model;

import android.util.Log;

import com.myctca.common.AppSessionManager;
import com.myctca.util.GeneralUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Impersonated {
    private AccessToken mAccessToken;
    private MyCTCAUserProfile mUserProfile;
    private String sitSurveyUrl;

    // Medical Docs
    private List<ClinicalSummary> mClinicalSummaries = new ArrayList<>();
    private List<MedDoc> mClinicalDocs = new ArrayList<>();
    private List<MedDoc> mRadiationDocs = new ArrayList<>();
    private List<MedDoc> mIntegrativeDocs = new ArrayList<>();
    private List<ImagingDoc> mImagingDocs = new ArrayList<>();

    //Release of information
    private boolean isROIPdfInstalled = false;
    private boolean isANNCPdfInstalled = false;
    // ActivityLogs
    private Map<Date, List<ActivityLogItem>> activityLogs = new HashMap<>();
    private List<Date> mActivityLogsDates = new ArrayList<>();
    private List<String> mCareTeam = new ArrayList<>();
    private Map<String, CareTeam> mCareTeamDetails = new HashMap<>();
    // Health History Docs
    private List<VitalsGroup> mVitalsGroups = new ArrayList<>();
    private List<Prescription> mPrescriptions = new ArrayList<>();
    private boolean ctcaPrescribed = false;
    private List<Allergy> mAllergies = new ArrayList<>();
    private List<Immunization> mImmunizations = new ArrayList<>();
    private List<HealthIssue> mHealthIssues = new ArrayList<>();
    private Facility mPreferredFacility;
    //Advanced notice of non coverage
    private boolean anncFormExistsCheck = false;
    private boolean ifAnncFormExists = false;
    private String mrn;
    private List<MedicalCenter> facilityAll = new ArrayList<>();
    private List<FacilityInfoAll> facilityInfoAllList = new ArrayList<>();
    //Alert messages - home screen
    private List<String> homeAlerts = new ArrayList<>();
    // All Appointments
    private List<Appointment> appointments = new ArrayList<>();
    // Upcoming Appointemnts
    private Map<String, List<Appointment>> upcomingAppointments = new HashMap<>();
    private List<String> upcomingSections = new ArrayList<>();
    // Past Appointments
    private Map<String, List<Appointment>> pastAppointments = new HashMap<>();
    private List<String> pastSections = new ArrayList<>();
    // Lab Results
    private List<LabResult> labResults = new ArrayList<>();
    // Mail
    private List<Mail> mInbox = new ArrayList<>();
    private List<Mail> mSentbox = new ArrayList<>();
    private List<Mail> mArchivebox = new ArrayList<>();
    //patient reported
    private List<SymptomInventory> symptomInventoryList = new ArrayList<>();
    //external links
    private List<ExternalLink> externalLinkList = new ArrayList<>();
    private RoiDetails roiDetails;
    //common
    private ContactInfo contactInfo;

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<ExternalLink> getExternalLinkList() {
        return externalLinkList;
    }

    public void setExternalLinkList(List<ExternalLink> externalLinkList) {
        this.externalLinkList = externalLinkList;
    }

    public List<String> getHomeAlerts() {
        return homeAlerts;
    }

    public void setHomeAlerts(List<String> homeAlerts) {
        this.homeAlerts = homeAlerts;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Map<String, List<Appointment>> getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(Map<String, List<Appointment>> upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }

    public List<String> getUpcomingSections() {
        return upcomingSections;
    }

    public void setUpcomingSections(List<String> upcomingSections) {
        this.upcomingSections = upcomingSections;
    }

    public Map<String, List<Appointment>> getPastAppointments() {
        return pastAppointments;
    }

    public void setPastAppointments(Map<String, List<Appointment>> pastAppointments) {
        this.pastAppointments = pastAppointments;
    }

    public List<String> getPastSections() {
        return pastSections;
    }

    public void setPastSections(List<String> pastSections) {
        this.pastSections = pastSections;
    }

    public List<LabResult> getLabResults() {
        return labResults;
    }

    public void setLabResults(List<LabResult> labResults) {
        this.labResults = labResults;
    }

    public List<Mail> getmInbox() {
        return mInbox;
    }

    public void setmInbox(List<Mail> mInbox) {
        this.mInbox = mInbox;
    }

    public List<Mail> getmSentbox() {
        return mSentbox;
    }

    public void setmSentbox(List<Mail> mSentbox) {
        this.mSentbox = mSentbox;
    }

    public List<Mail> getmArchivebox() {
        return mArchivebox;
    }

    public void setmArchivebox(List<Mail> mArchivebox) {
        this.mArchivebox = mArchivebox;
    }

    public boolean isAnncFormExistsCheck() {
        return anncFormExistsCheck;
    }

    public void setAnncFormExistsCheck(boolean anncFormExistsCheck) {
        this.anncFormExistsCheck = anncFormExistsCheck;
    }

    public boolean isIfAnncFormExists() {
        return ifAnncFormExists;
    }

    public void setIfAnncFormExists(boolean ifAnncFormExists) {
        this.ifAnncFormExists = ifAnncFormExists;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public List<MedicalCenter> getFacilityAll() {
        return facilityAll;
    }

    public void setFacilityAll(List<MedicalCenter> facilityAll) {
        this.facilityAll = facilityAll;
    }

    public List<FacilityInfoAll> getFacilityInfoAllList() {
        return facilityInfoAllList;
    }

    public void setFacilityInfoAllList(List<FacilityInfoAll> facilityInfoAllList) {
        this.facilityInfoAllList = facilityInfoAllList;
    }

    public Facility getmPreferredFacility() {
        return mPreferredFacility;
    }

    public void setmPreferredFacility(Facility mPreferredFacility) {
        this.mPreferredFacility = mPreferredFacility;
    }

    public Map<Date, List<ActivityLogItem>> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(Map<Date, List<ActivityLogItem>> activityLogs) {
        this.activityLogs = activityLogs;
    }

    public List<Date> getmActivityLogsDates() {
        return mActivityLogsDates;
    }

    public void setmActivityLogsDates(List<Date> mActivityLogsDates) {
        this.mActivityLogsDates = mActivityLogsDates;
    }

    public List<String> getmCareTeam() {
        return mCareTeam;
    }

    public void setmCareTeam(List<String> mCareTeam) {
        this.mCareTeam = mCareTeam;
    }

    public Map<String, CareTeam> getmCareTeamDetails() {
        return mCareTeamDetails;
    }

    public void setmCareTeamDetails(Map<String, CareTeam> mCareTeamDetails) {
        this.mCareTeamDetails = mCareTeamDetails;
    }

    public boolean isROIPdfInstalled() {
        return isROIPdfInstalled;
    }

    public void setROIPdfInstalled(boolean ROIPdfInstalled) {
        isROIPdfInstalled = ROIPdfInstalled;
    }

    public boolean isANNCPdfInstalled() {
        return isANNCPdfInstalled;
    }

    public void setANNCPdfInstalled(boolean ANNCPdfInstalled) {
        isANNCPdfInstalled = ANNCPdfInstalled;
    }

    public List<VitalsGroup> getmVitalsGroups() {
        return mVitalsGroups;
    }

    public void setmVitalsGroups(List<VitalsGroup> mVitalsGroups) {
        this.mVitalsGroups = mVitalsGroups;
    }

    public List<Prescription> getmPrescriptions() {
        return mPrescriptions;
    }

    public void setmPrescriptions(List<Prescription> mPrescriptions) {
        this.mPrescriptions = mPrescriptions;
    }

    public boolean isCtcaPrescribed() {
        return ctcaPrescribed;
    }

    public void setCtcaPrescribed(boolean ctcaPrescribed) {
        this.ctcaPrescribed = ctcaPrescribed;
    }

    public List<Allergy> getmAllergies() {
        return mAllergies;
    }

    public void setmAllergies(List<Allergy> mAllergies) {
        this.mAllergies = mAllergies;
    }

    public List<Immunization> getmImmunizations() {
        return mImmunizations;
    }

    public void setmImmunizations(List<Immunization> mImmunizations) {
        this.mImmunizations = mImmunizations;
    }

    public List<HealthIssue> getmHealthIssues() {
        return mHealthIssues;
    }

    public void setmHealthIssues(List<HealthIssue> mHealthIssues) {
        this.mHealthIssues = mHealthIssues;
    }

    public AccessToken getmAccessToken() {
        return mAccessToken;
    }

    public void setmAccessToken(AccessToken mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public MyCTCAUserProfile getmUserProfile() {
        if (mUserProfile == null) {
            if (AppSessionManager.getInstance().isSuccessfullyLoggedIn()) {
                Log.d("Impersonated: ", "Application killed by OS");
                GeneralUtil.logoutApplication();
                return new MyCTCAUserProfile();
            } else
                return null;
        }
        return mUserProfile;
    }

    public void setmUserProfile(MyCTCAUserProfile mUserProfile) {
        this.mUserProfile = mUserProfile;
    }

    public List<ClinicalSummary> getmClinicalSummaries() {
        return mClinicalSummaries;
    }

    public void setmClinicalSummaries(List<ClinicalSummary> mClinicalSummaries) {
        this.mClinicalSummaries = mClinicalSummaries;
    }

    public List<MedDoc> getmClinicalDocs() {
        return mClinicalDocs;
    }

    public void setmClinicalDocs(List<MedDoc> mClinicalDocs) {
        this.mClinicalDocs = mClinicalDocs;
    }

    public List<MedDoc> getmRadiationDocs() {
        return mRadiationDocs;
    }

    public void setmRadiationDocs(List<MedDoc> mRadiationDocs) {
        this.mRadiationDocs = mRadiationDocs;
    }

    public List<MedDoc> getmIntegrativeDocs() {
        return mIntegrativeDocs;
    }

    public void setmIntegrativeDocs(List<MedDoc> mIntegrativeDocs) {
        this.mIntegrativeDocs = mIntegrativeDocs;
    }

    public List<ImagingDoc> getmImagingDocs() {
        return mImagingDocs;
    }

    public void setmImagingDocs(List<ImagingDoc> mImagingDocs) {
        this.mImagingDocs = mImagingDocs;
    }

    public List<SymptomInventory> getSymptomInventories() {
        return symptomInventoryList;
    }

    public void setSymptomInventories(List<SymptomInventory> symptomInventoryList) {
        this.symptomInventoryList = symptomInventoryList;
    }

    public String getSitSurveyUrl() {
        return sitSurveyUrl;
    }

    public void setSitSurveyUrl(String sitSurveyUrl) {
        this.sitSurveyUrl = sitSurveyUrl;
    }

    public RoiDetails getRoiDetails() {
        return roiDetails;
    }

    public void setRoiDetails(RoiDetails roiDetails) {
        this.roiDetails = roiDetails;
    }
}
