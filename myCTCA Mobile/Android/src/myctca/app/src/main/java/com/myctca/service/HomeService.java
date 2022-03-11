package com.myctca.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.NavActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.interfaces.PostListener;
import com.myctca.model.AccessToken;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.ApplicationVersion;
import com.myctca.model.HomeAlert;
import com.myctca.model.ImpersonatedUserProfile;
import com.myctca.model.ImpersonationUserToken;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.MyCTCATask;
import com.myctca.network.GetClient;
import com.myctca.network.PostClient;

import java.util.ArrayList;
import java.util.List;

public class HomeService implements GetListener, PostListener {

    private static final String TAG = HomeService.class.getSimpleName();
    private static final String PURPOSE_ALERTS = "HOME_ALERTS";
    private static HomeService homeService;
    protected boolean show = false;
    private HomeServiceInterface listener;
    private Context context;
    private boolean mandatory = false;

    public static HomeService getInstance() {
        if (homeService == null) {
            homeService = new HomeService();
        }
        return homeService;
    }

    public boolean isMessageUpdateDialogMandatory() {
        return mandatory;
    }

    public void getAlertMessages(Context context, HomeServiceInterface listener, String purpose, String url) {
        this.listener = listener;
        this.context = context;
        GetClient getClient = new GetClient(this, context);
        getClient.fetch(url, null, purpose);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        if (purpose.equals(PURPOSE_ALERTS)) {
            try {
                HomeAlert[] alerts = new Gson().fromJson(parseSuccess, HomeAlert[].class);
                List<String> alertsArray = new ArrayList<>();
                for (HomeAlert alert : alerts) {
                    alertsArray.add(alert.messageText);
                }
                AppSessionManager.getInstance().setHomeAlerts(alertsArray);
                listener.notifyFetchSuccess(alertsArray, purpose);
            } catch (JsonParseException exception) {
                listener.notifyError(context.getString(R.string.error_400));
            }
        } else {
            String sitUrl = "";
            if (!parseSuccess.isEmpty())
                sitUrl = parseSuccess.substring(1, parseSuccess.length() - 1);
            AppSessionManager.getInstance().setSitSurveyUrl(sitUrl);
            Log.d(TAG, "HomeService retrieveSITUrl DONE");
            listener.notifyFetchSuccess(null, purpose);
        }
    }

    public String getSurveyUrl() {
        return AppSessionManager.getInstance().getSitSurveyUrl();
    }


    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        String url = "";
        if (purpose.equals(PURPOSE_ALERTS)) {
            url = BuildConfig.myctca_server + context.getString(R.string.myctca_alert_messages);
        } else {
            url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_sit_survey);
        }
        CTCAAnalyticsManager.createEvent("HomeFragment:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        listener.notifyError(VolleyErrorHandler.handleError(error, context));
    }

    public void checkVersions(NavActivity activity) {
        List<ApplicationVersion> applicationVersions = AppSessionManager.getInstance().getApplicationVersions();
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            String myVersionName = packageInfo.versionName;
            for (ApplicationVersion version : applicationVersions) {
                String newVersionName = version.getVersionNumber();

                if (version.getPlatform().equals("Android")) {
                    int flag = 0;
                    String[] myVersionNameSplits = myVersionName.split("\\.");
                    String[] newVersionNameSplits = newVersionName.split("\\.");
                    int maxIndex = Math.min(myVersionNameSplits.length, newVersionNameSplits.length);
                    for (int i = 0; i < maxIndex; i++) {
                        int myPackageNamePart = Integer.parseInt(myVersionNameSplits[i]);
                        int newPackageNamePart = Integer.parseInt(newVersionNameSplits[i]);
                        if (myPackageNamePart < newPackageNamePart) {
                            show = true;
                            flag = 1;
                            mandatory = (mandatory) ? mandatory : version.getMandatory();
                            break;
                        }
                        if (myPackageNamePart > newPackageNamePart) {
                            show = (show) ? show : false;
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0 && newVersionNameSplits.length > myVersionNameSplits.length) {
                        show = true;
                        mandatory = (mandatory) ? mandatory : version.getMandatory();
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "exception: " + e);
        }
    }

    public boolean showMessageUpdateDialog() {
        return show;
    }

    public List<MyCTCAProxy> getProxies() {
        if (AppSessionManager.getInstance().getUserProfile() != null)
            return AppSessionManager.getInstance().getUserProfile().getProxies();
        else return new ArrayList<>();
    }

    public void getImpersonatedAccessToken(Context context, HomeServiceInterface listener, ImpersonatedUserProfile userProfile, int purpose) {
        this.listener = listener;
        this.context = context;
        String url = BuildConfig.myctca_server + context.getString(R.string.endpoint_impersonated_token);
        Log.d(TAG, "url: " + url);
        PostClient postClient = new PostClient(this, context, purpose);
        postClient.sendData(url, new Gson().toJson(userProfile), null);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        ImpersonationUserToken impersonationUserToken = new Gson().fromJson(response, ImpersonationUserToken.class);
        AccessToken tokenImpersonated = new AccessToken();
        tokenImpersonated.setAccess_token(impersonationUserToken.getAccess_token());
        tokenImpersonated.setExpires_in(impersonationUserToken.getExpires_in());
        tokenImpersonated.setId_token(impersonationUserToken.getId_token());
        tokenImpersonated.setToken_type(impersonationUserToken.getToken_type());
        if (impersonationUserToken.getAccess_token() == null) {
            listener.notifyError(context.getString(R.string.error_400));
        } else {
            if (task == MyCTCATask.PURPOSE_REVERT_PATIENT_PROFILE) {
                AppSessionManager.user2 = null;
                AppSessionManager.getInstance().setCurrentUser(1);
            } else {
                AppSessionManager.getInstance().setCurrentUser(2);
                AppSessionManager.getInstance().setAccessToken(tokenImpersonated);
            }

            listener.notifyImpersonatedAccessToken(AppSessionManager.getInstance().getAccessToken());
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        String url = BuildConfig.myctca_server + context.getString(R.string.endpoint_impersonated_token);
        CTCAAnalyticsManager.createEvent("HomeFragment:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        listener.notifyError(VolleyErrorHandler.handleError(error, context));
    }

    public interface HomeServiceInterface {
        void notifyFetchSuccess(List<String> list, String purpose);

        void notifyError(String message);

        void notifyImpersonatedAccessToken(AccessToken impersonatedAccessToken);
    }
}
