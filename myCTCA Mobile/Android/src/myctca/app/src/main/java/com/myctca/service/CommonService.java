package com.myctca.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.CareTeam;
import com.myctca.model.ContactInfo;
import com.myctca.model.PhoneNumber;
import com.myctca.network.GetClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.myctca.util.GeneralUtil.noNull;

public class CommonService implements GetListener {
    private static final String TAG = CommonService.class.getSimpleName();
    private static final String CONTACT_INFO_PURPOSE = "CONTACT_INFO";
    private static final String CARE_PLAN_PURPOSE = "CARE_PLAN_NEW";
    private static CommonService commonService;
    protected CommonServiceListener listener;
    private Context context;

    public static CommonService getInstance() {
        if (commonService == null) {
            commonService = new CommonService();
        }
        return commonService;
    }

    public void downloadCareTeams(CommonServiceListener listener, Context context, String purpose) {
        this.listener = listener;
        this.context = context;
        if (AppSessionManager.getInstance().getmCareTeam().isEmpty()) {
            final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_care_team);
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        } else
            listener.notifyFetchSuccess(purpose);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        if (CARE_PLAN_PURPOSE.equals(purpose)) {
            try {
                CareTeam[] careTeams = new Gson().fromJson(parseSuccess, CareTeam[].class);
                Map<String, CareTeam> careTeamsDetails = new HashMap<>();
                List<String> careTeamNames = new ArrayList<>();
                for (CareTeam careTeam : careTeams) {
                    if (!TextUtils.isEmpty(careTeam.getName())) {
                        careTeamNames.add(careTeam.getName());
                        careTeamsDetails.put(careTeam.getName(), careTeam);
                    }
                }
                AppSessionManager.getInstance().setmCareTeam(careTeamNames);
                AppSessionManager.getInstance().setmCareTeamDetails(careTeamsDetails);
                Log.d(TAG, "CareTeam retrieved: " + AppSessionManager.getInstance().getmCareTeam());
                listener.notifyFetchSuccess(purpose);
            } catch (JsonParseException exception) {
                Log.e(TAG, "exception: " + exception);
                listener.notifyFetchError(context.getString(R.string.error_400), purpose);
            }
        } else if (CONTACT_INFO_PURPOSE.equals(purpose)) {
            try {
                ContactInfo contactInfo = new Gson().fromJson(parseSuccess, ContactInfo.class);
                AppSessionManager.getInstance().setContactInfo(contactInfo);
                Log.d(TAG, "ContactInfo retrieved: " + AppSessionManager.getInstance().getContactInfo());
                listener.notifyFetchSuccess(purpose);
            } catch (JsonParseException exception) {
                Log.e(TAG, "exception: " + exception);
                listener.notifyFetchError(context.getString(R.string.error_400), purpose);
            }
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        String url = "";
        switch (purpose) {
            case CARE_PLAN_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_care_team);
                break;
            case CONTACT_INFO_PURPOSE:
                url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_roi_contact_info);
                break;
        }
        CTCAAnalyticsManager.createEvent("Common:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);

        Log.d("Error.Response", noNull(error.getMessage()));
        listener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    public void downloadContactInfo(CommonServiceListener listener, Context context, String purpose) {
        this.listener = listener;
        this.context = context;
        if (AppSessionManager.getInstance().getContactInfo() == null) {
            final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_roi_contact_info);
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        } else
            listener.notifyFetchSuccess(purpose);
    }

    public String getUserEmail() {
        return AppSessionManager.getInstance().getContactInfo().getEmailAddress();
    }

    public String getUserContactNumber() {
        ContactInfo contactInfo = AppSessionManager.getInstance().getContactInfo();
        if (contactInfo != null) {
            List<PhoneNumber> phoneNumbers = contactInfo.getPhoneNumbers();
            for (PhoneNumber number : phoneNumbers) {
                switch (number.getPhoneType()) {
                    case "home":
                    case "cellular":
                    case "alt":
                    case "business":
                    case "international":
                    case "office":
                    case "temporary":
                    case "other":
                    case "work": {
                        String phone = number.getPhone();
                        if (phone != null && !phone.isEmpty()) {
                            if (phone.charAt(0) == '+')
                                return phone;
                            else
                                return phone.replaceAll("[^0-9]", "");
                        }
                    }
                }
            }
        }
        return "";
    }

    public Date getUserDob() {
        return AppSessionManager.getInstance().getContactInfo().getUserDob();
    }

    public interface CommonServiceListener {
        void notifyFetchSuccess(String purpose);

        void notifyFetchError(String error, String purpose);
    }
}
