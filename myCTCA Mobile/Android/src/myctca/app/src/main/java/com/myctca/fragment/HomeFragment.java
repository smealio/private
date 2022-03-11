package com.myctca.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.AppointmentRequestActivity;
import com.myctca.activity.CaregiverMoreActivity;
import com.myctca.activity.DisplayWebViewActivity;
import com.myctca.activity.FrequentlyCalledNumbersActivity;
import com.myctca.activity.MailNewActivity;
import com.myctca.activity.NavActivity;
import com.myctca.adapter.HomeAlertMessagesAdapter;
import com.myctca.common.AppSessionManager;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.AccessToken;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.ImpersonatedUserProfile;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.MyCTCATask;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.UserPermissions;
import com.myctca.model.UserType;
import com.myctca.service.HomeService;
import com.myctca.service.LoginService;
import com.myctca.service.SessionFacade;
import com.myctca.util.GeneralUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class HomeFragment extends Fragment implements HomeService.HomeServiceInterface,
        HomeScreenPatientDialogFragment.HomeScreenPatientDialogListener,
        LoginService.LoginServiceGetListener {
    private static final int CONVERT_ACCOUNT = 1;
    private static final int SIT_SURVEY = 2;
    private static final String TAG = "myctca_home";
    private static final String PURPOSE_ALERTS = "HOME_ALERTS";
    private static final String REFRESH_DATA = "REFRESH_DATA";
    private static final String RETRIEVE_DATA = "RETRIEVE_DATA";
    private static final String PURPOSE_USER_PROFILE = "PURPOSE_USER_PROFILE";
    private static final String PURPOSE_FACILITY_DATA = "PURPOSE_FACILITY_DATA";
    private static final String SIT_SURVEY_PURPOSE = "SIT_SURVEY_PURPOSE";
    private TextView homePatientName;
    private TextView alertMessagesHeader;
    private CTCARecyclerView rvHomeAlertMessage;
    private SwipeRefreshLayout alertsRefreshLayout;
    private SessionFacade sessionFacade;
    private TextView homeQuickLinksTitle;
    private ImageButton homeVertElipses;
    private TextView careGiverAccessTitle;
    private ImageView homeRequestAppt;
    private ImageView homeCallTechSupport;
    private ImageView homeMessageCareteam;
    private ImageView homeShareRecords;
    private TextView homeAlertLoadMore;
    private List<String> homeAlerts;
    private LinearLayout llCareGiverAccess;
    private LinearLayout llHomeQuickLinks;
    private LinearLayout llCaregiverAccessPatients;
    private TextView tvSelectedPatientName;
    private TextView accessPatientsEmptyString;
    private Button btnLogoutCaregiver;
    private Button btnViewPatientRecords;
    private int count = 0;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPrefs;
    private Context context;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        homePatientName = view.findViewById(R.id.home_patient_name);

        //alert section
        alertMessagesHeader = view.findViewById(R.id.home_alerts_top_green_banner);
        rvHomeAlertMessage = view.findViewById(R.id.rv_home_alert_message);
        alertsRefreshLayout = view.findViewById(R.id.alertsRefreshLayout);
        homeVertElipses = view.findViewById(R.id.home_vert_ellipses);

        //quick links sections
        homeQuickLinksTitle = view.findViewById(R.id.home_quick_links);
        llHomeQuickLinks = view.findViewById(R.id.ll_home_quick_links);
        homeRequestAppt = view.findViewById(R.id.home_request_appt);
        homeCallTechSupport = view.findViewById(R.id.home_call_tech_support);
        homeMessageCareteam = view.findViewById(R.id.home_message_careteam);
        homeShareRecords = view.findViewById(R.id.home_share_records);
        homeAlertLoadMore = view.findViewById(R.id.home_alerts_load_more);

        //caregiver access section
        careGiverAccessTitle = view.findViewById(R.id.care_giver_access_title);
        llCareGiverAccess = view.findViewById(R.id.ll_care_giver_access);
        llCaregiverAccessPatients = view.findViewById(R.id.caregiver_access_patients_layout);
        btnViewPatientRecords = view.findViewById(R.id.btn_view_patient_records);
        tvSelectedPatientName = view.findViewById(R.id.selected_patient_name);
        btnLogoutCaregiver = view.findViewById(R.id.btn_logout_caregiver);

        // Empty View
        View mEmptyView = view.findViewById(R.id.alert_empty_view);
        TextView mEmptyTextView = view.findViewById(R.id.alerts_empty_text_view);
        mEmptyTextView.setText(context.getString(R.string.empty_list_message, context.getString(R.string.home_title), ""));
        accessPatientsEmptyString = view.findViewById(R.id.access_patients_empty_string);

        // Pull To Refresh
        alertsRefreshLayout.setOnRefreshListener(this::refreshItems);
        rvHomeAlertMessage.setEmptyView(mEmptyView);
        rvHomeAlertMessage.setNestedScrollingEnabled(false);

        setSectionVisibilities();
        prepareView();
        setMenuVisibility(true);
        setAlertsRecyclerView();
        downloadAlertMessages(RETRIEVE_DATA);
        retrieveSitSurvey();
    }

    private void retrieveSitSurvey() {
        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.SUBMIT_PATIENT_REPORTED_DOCUMENTS)) {
            String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_sit_survey);
            sessionFacade.getAlertMessages(context, this, SIT_SURVEY_PURPOSE, url);
        }
    }

    private void showSitSurvey() {
        String url = sessionFacade.getSurveyUrl();
        if (!TextUtils.isEmpty(url)) {
            boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);
            if (tabletSize) {
                long time = sharedPrefs.getLong(context.getString(R.string.pref_sit_survey_time), 0);
                if (time < System.currentTimeMillis() - 86400000) {
                    showSitSurveyDialog(url);
                    editor.putBoolean(context.getString(R.string.pref_sit_survey_not_now), true);
                    editor.putLong(context.getString(R.string.pref_sit_survey_time), System.currentTimeMillis()).apply();
                }
            }
        }
    }

    private void showSitSurveyDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.inventory_survey_title))
                .setMessage(context.getString(R.string.inventory_survey_message));
        builder.setNegativeButton(context.getString(R.string.new_update_available_negative_btn_text), ((dialogInterface, i) -> {
            //do nothing
        }));
        builder.setPositiveButton(context.getString(R.string.yes_btn), (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivityForResult(intent, SIT_SURVEY);
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.setCancelable(false);
        if (!((NavActivity) context).isFinishing())
            dialog.show();
    }

    private void setSectionVisibilities() {
        if (!sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_QUICK_LINKS)) {
            llHomeQuickLinks.setVisibility(View.GONE);
        }
        if (sessionFacade.getUserType() == UserType.CAREGIVER) {
            if (((NavActivity) context).isCaregiverImpersonating()) {
                btnLogoutCaregiver.setVisibility(View.VISIBLE);
                llHomeQuickLinks.setVisibility(View.GONE);
            } else {
                llCareGiverAccess.setVisibility(View.GONE);
            }
        } else if (sessionFacade.getUserType() == UserType.PATIENT) {
            if (((NavActivity) context).isCaregiverImpersonating()) {
                if (sessionFacade.getProxies().size() > 1)
                    llCareGiverAccess.setVisibility(View.VISIBLE);
                else
                    llCareGiverAccess.setVisibility(View.GONE);
                homeVertElipses.setVisibility(View.GONE);
            } else {
                llCareGiverAccess.setVisibility(View.GONE);
            }
        }

        if (sessionFacade.getProxies().size() > 1) {
            llCaregiverAccessPatients.setVisibility(View.VISIBLE);
        } else {
            accessPatientsEmptyString.setVisibility(View.VISIBLE);
        }
    }

    private void setAlertsRecyclerView() {
        rvHomeAlertMessage.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvHomeAlertMessage.setLayoutManager(layoutManager);
    }

    private void showAlert() {
        sessionFacade.checkVersions((NavActivity) context);
        boolean show = sessionFacade.showMessageUpdateDialog();
        boolean mandatory = sessionFacade.isMessageUpdateDialogMandatory();
        if (show) {
            if (!mandatory) {
                if (sharedPrefs.getBoolean(context.getString(R.string.pref_message_update_not_now), false)) {
                    //YOUR CODE TO SHOW DIALOG
                    long time = sharedPrefs.getLong(context.getString(R.string.pref_message_update_time), 0);
                    if (time < System.currentTimeMillis() - 86400000) {
                        showDialog(false);
                    }
                } else {
                    showDialog(false);
                }
            } else {
                showDialog(true);
            }
        } else {
            editor.putBoolean(context.getString(R.string.pref_message_update_not_now), false);
            editor.putLong(context.getString(R.string.pref_message_update_time), 0).apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showAlert();
    }

    private void showDialog(boolean mandatory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String positiveBtnText = context.getString(R.string.new_update_available_positive_btn_text);
        builder.setTitle(context.getString(R.string.new_update_available_title))
                .setMessage(context.getString(R.string.new_update_available_message));
        if (!mandatory) {
            builder.setNegativeButton(context.getString(R.string.new_update_available_negative_btn_text), ((dialogInterface, i) -> {
                editor.putBoolean(context.getString(R.string.pref_message_update_not_now), true);
                editor.putLong(context.getString(R.string.pref_message_update_time), System.currentTimeMillis()).apply();
            }));
            positiveBtnText = "Yes";
        }
        builder.setPositiveButton(positiveBtnText, (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.new_update_playstore_url)));
            startActivity(intent);
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.setCancelable(false);
        if (!((NavActivity) context).isFinishing())
            dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ((NavActivity) context).setToolBar(context.getString(R.string.home_title));

        inflater.inflate(R.menu.menu_home, menu);
    }

    private void refreshItems() {
        downloadAlertMessages(REFRESH_DATA);
        onRefreshItemsLoadComplete();
        homeAlertLoadMore.setVisibility(View.VISIBLE);
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        Log.d(TAG, "HomeFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        alertsRefreshLayout.setRefreshing(false);
    }

    public void downloadAlertMessages(String type) {
        if (context != null) {
            if (type.equals(RETRIEVE_DATA)) {
                ((NavActivity) context).showActivityIndicator(context.getString(R.string.retrieve_home_alert_messages));
            } else {
                ((NavActivity) context).showActivityIndicator(context.getString(R.string.refresh_home_alert_messages));
            }
        }
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_alert_messages);
        Log.d(TAG, "url: " + url);
        sessionFacade.getAlertMessages(context, this, PURPOSE_ALERTS, url);
    }

    private void prepareView() {
        MyCTCAUserProfile user = AppSessionManager.getInstance().getUserProfile();
        if (!((NavActivity) context).isCaregiverImpersonating()) {
            homePatientName.setText(context.getString(R.string.home_screen_user_name, user.getFullName(), ((NavActivity) context).getCaregiverAccessingPatientName()));
        } else {
            homePatientName.setText(user.getFullName());
        }
        alertMessagesHeader.setText(context.getString(R.string.home_alert_messages_header));
        alertMessagesHeader.setGravity(Gravity.CENTER);

        homeVertElipses.setOnClickListener(this::showMenu);
        homeQuickLinksTitle.setText(context.getString(R.string.home_quick_links_header));
        homeQuickLinksTitle.setGravity(Gravity.CENTER);

        careGiverAccessTitle.setText(context.getString(R.string.home_caregiver_access_header));
        careGiverAccessTitle.setGravity(Gravity.CENTER);
        homeRequestAppt.setOnClickListener(view -> {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("HomeFragment::prepareView", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_REQUEST_TAP));
            if (user.userCan(UserPermissions.REQUEST_APPOINTMENT)) {
                Intent intent = new Intent(context, AppointmentRequestActivity.class);
                intent.putExtra(AppointmentRequest.APPT_REQUEST_TYPE, AppointmentRequest.APPT_NEW);
                startActivity(intent);
            } else
                showPermissionAlert();
        });
        homeMessageCareteam.setOnClickListener(view -> {
            if (user.userCan(UserPermissions.SEND_SECURE_MESSAGES))
                startActivity(new Intent(context, MailNewActivity.class));
            else
                showPermissionAlert();

        });
        homeCallTechSupport.setOnClickListener(view -> {
            Intent myIntent = new Intent(context, FrequentlyCalledNumbersActivity.class);
            startActivity(myIntent);
        });
        homeShareRecords.setOnClickListener(view ->
        {
            Intent myIntent = new Intent(context, DisplayWebViewActivity.class);
            myIntent.putExtra("type", context.getString(R.string.login_share_records_text));
            myIntent.putExtra("url", BuildConfig.server_ctca_host + context.getString(R.string.link_share_my_records));
            startActivity(myIntent);
        });

        homeAlertLoadMore.setOnClickListener(view1 -> {
            if (homeAlertLoadMore.getText().equals(context.getString(R.string.alert_message_see_more))) {
                homeAlertLoadMore.setText(context.getString(R.string.alert_message_see_less));
                updateUI(homeAlerts, 0, homeAlerts.size());
            } else {
                homeAlertLoadMore.setText(context.getString(R.string.alert_message_see_more));
                updateUI(homeAlerts, 0, 1);
            }
        });

        llCaregiverAccessPatients.setOnClickListener(view ->
        {
            HomeScreenPatientDialogFragment dialog = new HomeScreenPatientDialogFragment();
            dialog.setTargetFragment(this, 0);
            dialog.show(getFragmentManager(), "");
        });

        btnLogoutCaregiver.setOnClickListener(view -> signOut());
    }

    private void showPermissionAlert() {
        String message = "";
        if (isProxyUser()) {
            message = context.getString(R.string.permisson_not_granted_message_proxy);
        } else {
            message = context.getString(R.string.permisson_not_granted_message_patient_mail);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(context.getString(R.string.permisson_not_granted_title))
                .setMessage(message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    //do nothing
                }).create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        if (!((NavActivity) context).isFinishing())
            dialog.show();
    }

    public boolean isProxyUser() {
        List<MyCTCAProxy> proxies = sessionFacade.getProxies();
        boolean isImpersonating = false;
        for (MyCTCAProxy proxy : proxies) {
            if (sessionFacade.getMyCtcaUserProfile().getCtcaId().equals(proxy.getToCtcaUniqueId())) {
                isImpersonating = proxy.isImpersonating();
            }
        }
        return !isImpersonating;
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.nav_back_tapped_title))
                .setMessage(context.getString(R.string.nav_back_tapped_text))
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.cancel();
                    GeneralUtil.logoutApplication();
                })
                .setNegativeButton(context.getString(R.string.nav_alert_cancel), (dialog12, which) -> dialog12.cancel())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        if (!((NavActivity) context).isFinishing())
            dialog.show();
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        if (((NavActivity) context).isCaregiverImpersonating()) {
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.home_more) {
                    Intent intent = new Intent(context, CaregiverMoreActivity.class);
                    startActivity(intent);
                } else {
                    showLogoutAlert();
                }
                return false;
            });
            popup.inflate(R.menu.menu_home_caregiver);
        } else {
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.home_return_back_caregiver_account) {
                    count = 0;
                    if (context != null)
                        ((NavActivity) context).showActivityIndicator(context.getString(R.string.please_wait_indicator_text));
                    ImpersonatedUserProfile userProfile = new ImpersonatedUserProfile();
                    userProfile.setToCtcaUniqueId(sessionFacade.getMyCtcaUserProfile().getCtcaId());
                    userProfile.setImpersonating(false);
                    sessionFacade.getImpersonatedAccessToken(context, this, userProfile, MyCTCATask.PURPOSE_REVERT_PATIENT_PROFILE);
                }
                return false;
            });
            popup.inflate(R.menu.menu_home_caregiver_viewing_patient);
        }
        popup.show();
    }

    private void updateUI(List<String> alertList, int start, int end) {
        if (!alertList.isEmpty()) {
            HomeAlertMessagesAdapter adapter = new HomeAlertMessagesAdapter(alertList.subList(start, end));
            rvHomeAlertMessage.setAdapter(adapter);
        }
        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (alertsRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            rvHomeAlertMessage.scrollToPosition(0);
        }
    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.convert_account_logout_alert_title))
                .setMessage(context.getString(R.string.convert_account_logout_alert_message))
                .setPositiveButton("Yes", (dialog1, which) ->
                {
                    String ctcaId = sessionFacade.getMyCtcaUserProfile().getCtcaId();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(BuildConfig.server_ctca_host + context.getString(R.string.myctca_home_caregiver_convert_account, ctcaId)));
                    startActivityForResult(i, CONVERT_ACCOUNT);
                })
                .setNegativeButton("Not Now", (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        if (!((NavActivity) context).isFinishing())
            dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONVERT_ACCOUNT || requestCode == SIT_SURVEY)
            GeneralUtil.logoutApplication();
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("HomeFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_HOME_ALERT_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        if (!((NavActivity) context).isFinishing())
            dialog.show();
    }

    @Override
    public void notifyFetchSuccess(List<String> alertsList, String purpose) {
        if (purpose.equals(PURPOSE_ALERTS)) {
            if (context != null)
                ((NavActivity) context).presentationReady();
            this.homeAlerts = alertsList;
            updateUI(alertsList, 0, 1);
        } else {
            showSitSurvey();
        }
    }

    @Override
    public void notifyGetSuccess(String purpose, boolean isTermsOfUseAccepted) {
        if (purpose.equals(PURPOSE_USER_PROFILE)) {
            count++;
            getImpersonatedFacilityInfo();
        } else {
            count++;
        }
        if (context != null && count == 2) {
            ((NavActivity) context).hideActivityIndicator();
            ((NavActivity) context).reloadFragment();
        }
    }

    @Override
    public void notifyError(String message) {
        if (context != null)
            ((NavActivity) context).hideActivityIndicator();
        showRequestFailure(message);
    }

    @Override
    public void notifyImpersonatedAccessToken(AccessToken impersonatedAccessToken) {
        getImpersonatedUserProfile();
    }

    private void getImpersonatedFacilityInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("facility", sessionFacade.getPrimaryFacility());
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_facility);
        sessionFacade.getUserInfo(url, params, this, context, PURPOSE_FACILITY_DATA);
    }

    private void getImpersonatedUserProfile() {
        String url = BuildConfig.myctca_server + context.getString(R.string.endpoint_user_profile);
        sessionFacade.getUserInfo(url, null, this, context, PURPOSE_USER_PROFILE);
    }

    @Override
    public void onPatientSelected(MyCTCAProxy proxy) {
        tvSelectedPatientName.setText(proxy.getFullName());
        btnViewPatientRecords.setVisibility(View.VISIBLE);
        btnViewPatientRecords.setOnClickListener(view -> {
            count = 0;
            if (context != null)
                ((NavActivity) context).showActivityIndicator(context.getString(R.string.please_wait_indicator_text));
            ImpersonatedUserProfile impersonatedUserProfile = new ImpersonatedUserProfile();
            impersonatedUserProfile.setToCtcaUniqueId(proxy.getToCtcaUniqueId());
            impersonatedUserProfile.setImpersonating(true);
            sessionFacade.getImpersonatedAccessToken(context, this, impersonatedUserProfile, MyCTCATask.PURPOSE_ACCESS_PATIENT_PROFILE);
        });
    }

}