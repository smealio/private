package com.myctca.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.model.AccessToken;
import com.myctca.model.ActivityLogItem;
import com.myctca.model.Allergy;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.ApplicationVersion;
import com.myctca.model.Appointment;
import com.myctca.model.CareTeam;
import com.myctca.model.ClinicalSummary;
import com.myctca.model.ContactInfo;
import com.myctca.model.ExternalLink;
import com.myctca.model.Facility;
import com.myctca.model.FacilityInfoAll;
import com.myctca.model.HealthIssue;
import com.myctca.model.IdentityUser;
import com.myctca.model.ImagingDoc;
import com.myctca.model.Immunization;
import com.myctca.model.Impersonated;
import com.myctca.model.ImpersonatedUserProfile;
import com.myctca.model.LabResult;
import com.myctca.model.Mail;
import com.myctca.model.MedDoc;
import com.myctca.model.MedicalCenter;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.MyCTCATask;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.Prescription;
import com.myctca.model.RoiDetails;
import com.myctca.model.SymptomInventory;
import com.myctca.model.UserPreference;
import com.myctca.model.VitalsGroup;
import com.myctca.network.VolleyService;
import com.myctca.service.HomeService;
import com.myctca.service.SessionFacade;
import com.myctca.util.Constants;
import com.myctca.util.GeneralUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by tomackb on 11/2/17.
 */

public class AppSessionManager implements HomeService.HomeServiceInterface {

    private static final String TAG = "MyCTCA-Session";
    private static final String CLIENT_ID = "CTCA.Portal.UI.Android";
    private static final String CLIENT_SECRET = BuildConfig.client_secret;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 555;
    public static Impersonated user2 = new Impersonated();
    // Volatile prevents another thread from seeing a half-initialized state of mInstance
    private static volatile AppSessionManager mInstance;
    //user preferences
    private UserPreference appointmentsDays;
    // Current user
    private Impersonated user1 = new Impersonated();
    private Impersonated currentUser = new Impersonated();
    private IdentityUser mIdentityUser = new IdentityUser();
    private SessionFacade sessionFacade = new SessionFacade();
    private AppSessionTimerHandler mTimeTracker = new AppSessionTimerHandler();
    private String mFCMToken = "";

    private boolean successfullyLoggedIn;
    private boolean showDiagnosticResportingDialog;

    private boolean deepLinking;
    private boolean sessionExpired;

    //application info
    private String technicalSupport = "1-800-234-0482";
    private List<ApplicationVersion> applicationVersions = new ArrayList<>();


    //Private constructor
    private AppSessionManager() {
        //Protect from the reflection api.
        if (mInstance != null) {
            Log.e(TAG, "Exception: Use getInstance() method to get the single instance of this class.");
        }
    }

    public static AppSessionManager getInstance() {
        synchronized (AppSessionManager.class) {
            if (mInstance == null) mInstance = new AppSessionManager();
        }
        return mInstance;
    }

    //Protect singleton from serialize and deserialize operation.
    protected AppSessionManager readResolve() {
        return getInstance();
    }

    public void beginCurrentSession() {
        // Mark Session Created Time
        Date mSessionCreated = new Date();

        // User Defaults <-> Shared Preferences
        Context mContext = MyCTCA.getAppContext();
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // Latest and Previous Sign In
        long latestMillis = sharedPref.getLong(mContext.getString(R.string.pref_latest_sign_in), 0L);
        if (latestMillis > 0) {
            editor.putLong(mContext.getString(R.string.pref_previous_sign_in), latestMillis);
        }
        editor.putLong(mContext.getString(R.string.pref_latest_sign_in), mSessionCreated.getTime()).apply();

        mTimeTracker.startIdleHandler();
        mTimeTracker.startTokenRefreshHandler();
    }

    public void endCurrentSession() {
        Log.d(TAG, "endCurrentSession");
        // Revoke the token before clearing session parameters
        // Service not working
//        revokeToken();
        clearSessionParameters();
        mTimeTracker.stopIdleHandler();
        mTimeTracker.killIdleHandler();
        mTimeTracker.stopTokenRefreshHandler();
        mTimeTracker.killTokenRefreshHandler();
    }

    public void refreshToken() {

        if (currentUser == user1) {
            final String username = getIdentityUser().getEmail();
            final String password = getIdentityUser().getPword();

            // Get URL
            String url = BuildConfig.server_ctca_host + MyCTCA.getAppContext().getString(R.string.endpoint_authorize);
            Log.d(TAG, "refreshToken url: " + url);
            Log.d(TAG, "refreshToken: " + username + "::" + password);

            // Request a string response
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                try {
                    AccessToken accessToken = new Gson().fromJson(response, AccessToken.class);
                    AppSessionManager.getInstance().setAccessToken(accessToken);
                    Log.d(TAG, "AccessToken: " + accessToken.prettyPrint());
                } catch (JsonParseException e) {
                    Log.e(TAG, "Something went wrong while trying to get Access Token! Error");
                }
            }, error -> {
                // Error handling
                Log.e(TAG, "Something went wrong while trying to get Access Token! Error: " + error.toString() + "::" + error.getLocalizedMessage());
                doRefreshTokenError(error);
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    params.put("client_id", getclientId());
                    params.put("client_secret", getclientSecret());
                    params.put("grant_type", "password");
                    params.put("scope", "openid profile email api external.identity.readwrite impersonation");

                    return params;
                }
            };
            MyCTCAActivity activity = (MyCTCAActivity) MyCTCA.getCurrentActivity();

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyService volleyService = VolleyService.getVolleyService(activity);
            volleyService.addToRequestQueue(stringRequest);
        } else {
            ImpersonatedUserProfile impersonatedUserProfile = new ImpersonatedUserProfile();
            impersonatedUserProfile.setToCtcaUniqueId(getCurrentProxyUser());
            impersonatedUserProfile.setImpersonating(true);
            sessionFacade.getImpersonatedAccessToken(MyCTCA.getAppContext(), this, impersonatedUserProfile, MyCTCATask.PURPOSE_ACCESS_PATIENT_PROFILE);
        }
    }

    private String getCurrentProxyUser() {
        if (currentUser.getmUserProfile() != null) {
            List<MyCTCAProxy> proxies = currentUser.getmUserProfile().getProxies();
            for (MyCTCAProxy proxy : proxies) {
                if (proxy.isImpersonating()) {
                    return proxy.getToCtcaUniqueId();
                }
            }
        } else {
            Log.d(TAG, "Application killed by OS");
            GeneralUtil.logoutApplication();
        }
        return "";
    }

    // GETS and SETS
    public List<ExternalLink> getExternalLinksList() {
        return currentUser.getExternalLinkList();
    }

    public void setExternalLinksList(List<ExternalLink> externalLinkList) {
        currentUser.setExternalLinkList(externalLinkList);
    }

    public String getTechnicalSupport() {
        return PhoneNumberUtils.formatNumber(technicalSupport.substring(2), "US");
    }

    public void setTechnicalSupport(String technicalSupport) {
        this.technicalSupport = technicalSupport;
    }

    public List<ApplicationVersion> getApplicationVersions() {
        return applicationVersions;
    }

    public void setApplicationVersions(List<ApplicationVersion> applicationVersions) {
        this.applicationVersions = applicationVersions;
    }

    public boolean isDeepLinking() {
        return deepLinking;
    }

    public void setDeepLinking(boolean deepLinking) {
        this.deepLinking = deepLinking;
    }

    public boolean isSessionExpired() {
        return sessionExpired;
    }

    public void setSessionExpired(boolean sessionExpired) {
        this.sessionExpired = sessionExpired;
    }

    public UserPreference getAppointmentsDays() {
        return appointmentsDays;
    }

    public void setAppointmentsDays(UserPreference appointmentsDays) {
        this.appointmentsDays = appointmentsDays;
    }

    public String getSitSurveyUrl() {
        return getCurrentUser().getSitSurveyUrl();
    }

    public void setSitSurveyUrl(String sitSurveyUrl) {
        getCurrentUser().setSitSurveyUrl(sitSurveyUrl);
    }

    public void setmUserProfile(MyCTCAUserProfile mUserProfile) {
        getCurrentUser().setmUserProfile(mUserProfile);
    }

    public void setmPreferredFacility(Facility mPreferredFacility) {
        getCurrentUser().setmPreferredFacility(mPreferredFacility);
    }

    public boolean getROIPdfInstalled() {
        return getCurrentUser().isROIPdfInstalled();
    }

    public void setROIPdfInstalled(boolean roipdfinstalled) {
        getCurrentUser().setROIPdfInstalled(roipdfinstalled);
    }

    public boolean getANNCPdfInstalled() {
        return getCurrentUser().isANNCPdfInstalled();
    }

    public void setANNCPdfInstalled(boolean anncpdfinstalled) {
        getCurrentUser().setANNCPdfInstalled(anncpdfinstalled);
    }

    public List<String> getHomeAlerts() {
        return getCurrentUser().getHomeAlerts();
    }

    public void setHomeAlerts(List<String> homeAlerts) {
        getCurrentUser().setHomeAlerts(homeAlerts);
    }

    public List<Appointment> getAppointments() {
        return getCurrentUser().getAppointments();
    }

    public void setAppointments(List<Appointment> appointments) {
        getCurrentUser().setAppointments(appointments);
    }

    public Map<String, List<Appointment>> getUpcomingAppointments() {
        return getCurrentUser().getUpcomingAppointments();
    }

    public void setUpcomingAppointments(Map<String, List<Appointment>> upcomingAppointments) {
        getCurrentUser().setUpcomingAppointments(upcomingAppointments);
    }

    public List<String> getUpcomingSections() {
        return getCurrentUser().getUpcomingSections();
    }

    public void setUpcomingSections(List<String> upcomingSections) {
        getCurrentUser().setUpcomingSections(upcomingSections);
    }

    public Map<String, List<Appointment>> getPastAppointments() {
        return getCurrentUser().getPastAppointments();
    }

    public void setPastAppointments(Map<String, List<Appointment>> pastAppointments) {
        getCurrentUser().setPastAppointments(pastAppointments);
    }

    public boolean isSuccessfullyLoggedIn() {
        return successfullyLoggedIn;
    }

    public void setSuccessfullyLoggedIn(boolean successfullyLoggedIn) {
        this.successfullyLoggedIn = successfullyLoggedIn;
    }

    public boolean showDiagnosticResportingDialog() {
        return showDiagnosticResportingDialog;
    }

    public void setShowDiagnosticResportingDialog(boolean showDiagnosticResportingDialog) {
        this.showDiagnosticResportingDialog = showDiagnosticResportingDialog;
    }

    public List<String> getPastSections() {
        return getCurrentUser().getPastSections();
    }

    public List<LabResult> getLabResults() {
        return getCurrentUser().getLabResults();
    }

    public void setLabResults(List<LabResult> labResults) {
        getCurrentUser().setLabResults(labResults);
    }

    public List<SymptomInventory> getSymptomInventories() {
        return getCurrentUser().getSymptomInventories();
    }

    public void setSymptomInventories(List<SymptomInventory> symptomInventoryList) {
        getCurrentUser().setSymptomInventories(symptomInventoryList);
    }

    public List<Mail> getmInbox() {
        return getCurrentUser().getmInbox();
    }

    public void setmInbox(List<Mail> mails) {
        getCurrentUser().setmInbox(mails);
    }

    public List<Mail> getmSentbox() {
        return getCurrentUser().getmSentbox();
    }

    public void setmSentbox(List<Mail> sentbox) {
        getCurrentUser().setmSentbox(sentbox);
    }

    public List<Mail> getmArchivebox() {
        return getCurrentUser().getmArchivebox();
    }

    public void setmArchivebox(List<Mail> archivebox) {
        getCurrentUser().setmArchivebox(archivebox);
    }

    public List<ClinicalSummary> getmClinicalSummaries() {
        return getCurrentUser().getmClinicalSummaries();
    }

    public void setmClinicalSummaries(List<ClinicalSummary> mClinicalSummaries) {
        getCurrentUser().setmClinicalSummaries(mClinicalSummaries);
    }

    public List<MedDoc> getmClinicalDocs() {
        return getCurrentUser().getmClinicalDocs();
    }

    public void setmClinicalDocs(List<MedDoc> mClinicalDocs) {
        getCurrentUser().setmClinicalDocs(mClinicalDocs);
    }

    public List<MedDoc> getmRadiationDocs() {
        return getCurrentUser().getmRadiationDocs();
    }

    public void setmRadiationDocs(List<MedDoc> mRadiationDocs) {
        getCurrentUser().setmRadiationDocs(mRadiationDocs);
    }

    public List<MedDoc> getmIntegrativeDocs() {
        return getCurrentUser().getmIntegrativeDocs();
    }

    public void setmIntegrativeDocs(List<MedDoc> mIntegrativeDocs) {
        getCurrentUser().setmIntegrativeDocs(mIntegrativeDocs);
    }

    public List<ImagingDoc> getmImagingDocs() {
        return getCurrentUser().getmImagingDocs();
    }

    public void setmImagingDocs(List<ImagingDoc> mImagingDocs) {
        getCurrentUser().setmImagingDocs(mImagingDocs);
    }


    public List<VitalsGroup> getmVitalsGroups() {
        return getCurrentUser().getmVitalsGroups();
    }

    public void setmVitalsGroups(List<VitalsGroup> mVitalsGroups) {
        getCurrentUser().setmVitalsGroups(mVitalsGroups);
    }

    public List<Prescription> getmPrescriptions() {
        return getCurrentUser().getmPrescriptions();
    }

    public void setmPrescriptions(List<Prescription> mPrescriptions) {
        getCurrentUser().setmPrescriptions(mPrescriptions);
    }

    public boolean isCtcaPrescribed() {
        return getCurrentUser().isCtcaPrescribed();
    }

    public void setCtcaPrescribed(boolean ctcaPrescribed) {
        getCurrentUser().setCtcaPrescribed(ctcaPrescribed);
    }

    public List<Allergy> getmAllergies() {
        return getCurrentUser().getmAllergies();
    }

    public void setmAllergies(List<Allergy> allergies) {
        getCurrentUser().setmAllergies(allergies);
    }

    public List<Immunization> getmImmunizations() {
        return getCurrentUser().getmImmunizations();
    }

    public void setmImmunizations(List<Immunization> mImmunizations) {
        getCurrentUser().setmImmunizations(mImmunizations);
    }

    public List<HealthIssue> getmHealthIssues() {
        return getCurrentUser().getmHealthIssues();
    }

    public void setmHealthIssues(List<HealthIssue> healthIssues) {
        getCurrentUser().setmHealthIssues(healthIssues);
    }

    public Map<Date, List<ActivityLogItem>> getActivityLogs() {
        return getCurrentUser().getActivityLogs();
    }

    public List<Date> getmActivityLogsDates() {
        return getCurrentUser().getmActivityLogsDates();
    }

    public void setmActivityLogsDates(ArrayList<Date> dates) {
        getCurrentUser().setmActivityLogsDates(dates);
    }

    public List<String> getmCareTeam() {
        return getCurrentUser().getmCareTeam();
    }

    public void setmCareTeam(List<String> mCareTeam) {
        getCurrentUser().setmCareTeam(mCareTeam);
    }

    public ContactInfo getContactInfo() {
        return getCurrentUser().getContactInfo();
    }

    public void setContactInfo(ContactInfo contactInfo) {
        getCurrentUser().setContactInfo(contactInfo);
    }

    public Map<String, CareTeam> getmCareTeamDetails() {
        return getCurrentUser().getmCareTeamDetails();
    }

    public void setmCareTeamDetails(Map<String, CareTeam> mCareTeamDetails) {
        getCurrentUser().setmCareTeamDetails(mCareTeamDetails);
    }

    public String getclientId() {
        return CLIENT_ID;
    }

    public String getclientSecret() {
        return CLIENT_SECRET;
    }

    public int getpermissionsRequestCallPhone() {
        return PERMISSIONS_REQUEST_CALL_PHONE;
    }

    public AppSessionTimerHandler getmTimeTracker() {
        return mTimeTracker;
    }

    public void setmTimeTracker(AppSessionTimerHandler mTimeTracker) {
        this.mTimeTracker = mTimeTracker;
    }

    public RoiDetails getRoiDetails() {
        return getCurrentUser().getRoiDetails();
    }

    public void setRoiDetails(RoiDetails roiDetails) {
        getCurrentUser().setRoiDetails(roiDetails);
    }

    public boolean getAnncFormExistsCheck() {
        return getCurrentUser().isAnncFormExistsCheck();
    }

    public void setAnncFormExistsCheck(boolean anncFormExistsCheck) {
        getCurrentUser().setAnncFormExistsCheck(anncFormExistsCheck);
    }

    public boolean getIfAnncFormExists() {
        return getCurrentUser().isIfAnncFormExists();
    }

    public void setIfAnncFormExists(boolean ifAnncFormExists) {
        getCurrentUser().setIfAnncFormExists(ifAnncFormExists);
    }

    public String getMrn() {
        return getCurrentUser().getMrn();
    }

    public void setMrn(String mrn) {
        getCurrentUser().setMrn(mrn);
    }

    public List<MedicalCenter> getFacilityAll() {
        return getCurrentUser().getFacilityAll();
    }

    public void setFacilityAll(List<MedicalCenter> facilityAll) {
        getCurrentUser().setFacilityAll(facilityAll);
    }

    public List<FacilityInfoAll> getFacilityInfoAllList() {
        return getCurrentUser().getFacilityInfoAllList();
    }

    public void setFacilityInfoAllList(List<FacilityInfoAll> facilityInfoAllList) {
        getCurrentUser().setFacilityInfoAllList(facilityInfoAllList);
    }

    public AccessToken getAccessToken() {
        if (getCurrentUser() != null)
            return getCurrentUser().getmAccessToken();
        else return null;
    }

    public void setAccessToken(AccessToken accessToken) {
        getCurrentUser().setmAccessToken(accessToken);
        if (mTimeTracker != null) {
            mTimeTracker.resetTokenRefreshHandler();
        }
    }

    public String getFCMToken() {
        return mFCMToken;
    }

    public void setFCMToken(String fcmToken) {
        mFCMToken = fcmToken;
    }

    public String getAuthTokenString() {
        if (getCurrentUser().getmAccessToken() == null) {
            GeneralUtil.logoutApplication();
            return null;
        } else
            return getCurrentUser().getmAccessToken().getAuthTokenString();
    }

    public String getOriginalTokenString() {
        if (this.user1.getmAccessToken() == null) {
            GeneralUtil.logoutApplication();
            return null;
        } else
            return this.user1.getmAccessToken().getAuthTokenString();
    }

    public Facility getPreferredFacility() {
        return getCurrentUser().getmPreferredFacility();
    }

    public void setmIdentityUser(IdentityUser mIdentityUser) {
        this.mIdentityUser = mIdentityUser;
    }

    public IdentityUser getIdentityUser() {
        if (mIdentityUser == null) {
            mIdentityUser = new IdentityUser();
        }
        return mIdentityUser;
    }

    private void clearSessionParameters() {
        Log.d(TAG, "clearSessionParameters clearSessionParameters clearSessionParameters clearSessionParameters");
        mIdentityUser = null;
        getCurrentUser().setmPreferredFacility(null);
        successfullyLoggedIn = false;
        showDiagnosticResportingDialog = false;
        deepLinking = false;
        sessionExpired = false;
        applicationVersions.clear();

        clearUserPreferences();
        clearAllAppointments();
        clearLabResults();
        clearMail();
        clearMore();
        currentUser = null;
        user1 = null;
        user2 = null;
    }

    private void clearUserPreferences() {
        appointmentsDays = null;
    }

    private void clearAllAppointments() {
        getCurrentUser().getAppointments().clear();
        clearPastAndUpcomingAppointments();
    }

    public void clearPastAndUpcomingAppointments() {
        getCurrentUser().getUpcomingSections().clear();
        getCurrentUser().getUpcomingAppointments().clear();
        getCurrentUser().getPastAppointments().clear();
        getCurrentUser().getPastSections().clear();
    }

    private void clearMail() {
        clearNew();
        clearSentbox();
        clearInbox();
        clearArchivebox();
    }

    private void clearMore() {
        clearClinicalSummaries();
        clearClinicalDocs();
        clearRadiationDocs();
        clearIntegrativeDocs();
        clearImagingDocs();

        clearVitals();
        clearPrescriptions();
        clearAllergies();
        clearImmunizations();
        clearHealthIssues();

        clearActivityLogs();
        clearReleaseOfInformation();
        clearAnnc();
        clearPdf();

        clearFacilityAll();
    }

    private void clearFacilityAll() {
        getCurrentUser().getFacilityInfoAllList().clear();
    }

    private void clearAnnc() {
        getCurrentUser().setAnncFormExistsCheck(false);
        getCurrentUser().setIfAnncFormExists(false);
        getCurrentUser().setMrn(null);
        getCurrentUser().getFacilityAll().clear();
    }

    public void clearInbox() {
        getCurrentUser().getmInbox().clear();
    }

    public void clearNew() {
        getCurrentUser().getmCareTeam().clear();
        getCurrentUser().getmCareTeamDetails().clear();
        getCurrentUser().setContactInfo(null);
    }

    public void clearSentbox() {
        getCurrentUser().getmSentbox().clear();
    }

    public void clearArchivebox() {
        getCurrentUser().getmArchivebox().clear();
    }

    public void clearLabResults() {
        getCurrentUser().getLabResults().clear();
    }

    public void clearClinicalSummaries() {
        getCurrentUser().getmClinicalSummaries().clear();
    }

    public void clearClinicalDocs() {
        getCurrentUser().getmClinicalDocs().clear();
    }

    public void clearRadiationDocs() {
        getCurrentUser().getmRadiationDocs().clear();
    }

    public void clearIntegrativeDocs() {
        getCurrentUser().getmIntegrativeDocs().clear();
    }

    public void clearImagingDocs() {
        getCurrentUser().getmImagingDocs().clear();
    }

    public void clearVitals() {
        getCurrentUser().getmVitalsGroups().clear();
    }

    public void clearPrescriptions() {
        getCurrentUser().getmPrescriptions().clear();
        getCurrentUser().setCtcaPrescribed(false);
    }

    public void clearAllergies() {
        getCurrentUser().getmAllergies().clear();
    }

    public void clearImmunizations() {
        getCurrentUser().getmImmunizations().clear();
    }

    public void clearHealthIssues() {
        getCurrentUser().getmHealthIssues().clear();
    }

    public void clearActivityLogs() {
        getCurrentUser().getmActivityLogsDates().clear();
        getCurrentUser().getActivityLogs().clear();
    }

    public void clearReleaseOfInformation() {
        getCurrentUser().setRoiDetails(null);
    }

    private void clearPdf() {
        getCurrentUser().setROIPdfInstalled(false);
        getCurrentUser().setANNCPdfInstalled(false);
    }

    public Impersonated getCurrentUser() {
        if (currentUser == null) {
            return new Impersonated();
        }
        return currentUser;
    }

//    private void revokeToken() {
//        if (mAccessToken != null) {
//            Context mContext = MyCTCA.getAppContext();
//            String revokeEndpoint = mContext.getString(R.string.server_ctca_host) + mContext.getString(R.string.endpoint_token_revocation);
//            //        Log.d(TAG, "revokeEndpoint: " + revokeEndpoint);
//            final String authString = AppSessionManager.getInstance().CLIENT_ID + ":" + AppSessionManager.getInstance().CLIENT_SECRET;
//            //        Log.d(TAG, "authString: " + authString);
//            final String base64Encoded = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
//            //        Log.d(TAG, "base64Encoded: " + base64Encoded);
//
//            // Request a string response
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, revokeEndpoint, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d(TAG, "revokeToken onResponse: " + response);
//                    mAccessToken = null;
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    // Error handling
//                    //                Log.d(TAG, "revokeToken onErrorResponse - Something went wrong while trying to revoke Access Token! Error: " + error.toString());
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/x-www-form-urlencoded; charset=UTF-8";
//                }
//
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<String, String>();
//                    headers.put("Authorization", base64Encoded);
//                    //                Log.d(TAG, "headers: " + headers);
//                    return headers;
//                }
//
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    if (mAccessToken != null) {
//                        params.put("token", mAccessToken.getToken());
//                    }
//                    params.put("token_type_hint", "access_token");
//
//                    return params;
//                }
//            };
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            // Get Volley Request Queue
//            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//            // Add the request to the queue
//            requestQueue.add(stringRequest);
//        }
//    }

    public void setCurrentUser(int type) {
        if (type == 1) {
            if (user1 == null) {
                user1 = new Impersonated();
            }
            currentUser = user1;
        } else {
            if (user2 == null) {
                user2 = new Impersonated();
            }
            currentUser = user2;
        }
    }

    public MyCTCAUserProfile getUserProfile() {
        return getCurrentUser().getmUserProfile();
    }

    // TIMERS
    public void resetIdleHandler() {
        Log.d(TAG, "AppSessionManger resetIdleHandler");
        mTimeTracker.resetIdleHandler();
    }

//    public void stopIdleHandler() {
//        mTimeTracker.stopIdleHandler();
//    }
//
//    public void killIdleHandler() { mTimeTracker.killIdleHandler();}

    public long getIdleTimeout() {
        return mTimeTracker.getIdleTimeout();
    }

    public void setIdleTimeout(long idleTimeout) {
        mTimeTracker.setIdleTimeout(idleTimeout);
    }

    public void setTokenRefreshInterval(long tokenRefreshBeforeExpired) {
        mTimeTracker.setTokenRefreshInterval(tokenRefreshBeforeExpired);
    }

    public void startActivityBackgroundTimer() {
        mTimeTracker.startActivityBackgroundTimer();
    }

    public void stopActivityBackgroundTimer() {
        mTimeTracker.stopActivityBackgroundTimer();
    }

    public boolean wasInBackground() {
        return mTimeTracker.isWasInBackground();
    }


    private void doRefreshTokenError(VolleyError error) {

        String errorMsg = VolleyErrorHandler.handleError(error, MyCTCA.getAppContext());
        Log.d(TAG, "VolleyErrorHandler: " + errorMsg);
    }

    @Override
    public void notifyFetchSuccess(List<String> list, String purpose) {
        //do nothing
    }

    @Override
    public void notifyError(String message) {
        showRequestFailure(message);
    }

    @Override
    public void notifyImpersonatedAccessToken(AccessToken impersonatedAccessToken) {
        //do nothing
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("HomeFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_HOME_ALERT_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MyCTCA.getCurrentActivity());
        final AlertDialog dialog = builder.setTitle(MyCTCA.getAppContext().getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(MyCTCA.getAppContext().getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.colorPrimary)));
        dialog.show();
    }
}
