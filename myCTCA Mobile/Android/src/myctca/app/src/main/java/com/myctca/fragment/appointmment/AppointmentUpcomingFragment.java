package com.myctca.fragment.appointmment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.myctca.R;
import com.myctca.activity.AppointmentRequestActivity;
import com.myctca.activity.NavActivity;
import com.myctca.activity.TelehealthCommunicationActivity;
import com.myctca.adapter.AppointmentSectionAdapter;
import com.myctca.common.AppSessionManager;
import com.myctca.common.CTCARecyclerView;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MeetingAccessTokenJson;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.MyCTCATask;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.UserPermissions;
import com.myctca.service.AppointmentService;
import com.myctca.service.SessionFacade;
import com.myctca.util.GeneralUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentUpcomingFragment extends Fragment implements AppointmentService.AppointmentServiceGetListener, AppointmentSectionAdapter.AppointmentSectionListener, AppointmentService.AppointmentServicePostListener {

    private static final String TAG = "myCTCA-Appt";
    private static final String APPT_PAST = "PAST";
    private static final String APPT_UPCOMING = "UPCOMING";
    private static String[] allPermissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private static String appointmentType = "";
    private final int TELEHEALTH_MEETING = 1;
    private SwipeRefreshLayout mApptRefreshLayout;
    private CTCARecyclerView mApptRecyclerView;
    private SectionedRecyclerViewAdapter mApptAdapter;
    private SessionFacade sessionFacade;
    private MyCTCAUserProfile userProfile;
    private Button mEmptyApptRequestButton;
    private Context context;
    private String meetingTime = "";
    private AlertDialog dialog;
    private String token;
    private Appointment appointment;
    private String permissionMessage;
    private String permissionsTitle;

    public AppointmentUpcomingFragment() {
        //nothing
    }

    public static void setAppointmentType(String appointmentType) {
        AppointmentUpcomingFragment.appointmentType = appointmentType;
    }

    public static AppointmentUpcomingFragment newInstance(int position) {
        Bundle args = new Bundle();
        if (position == 0)
            args.putString("appointmentType", APPT_UPCOMING);
        else
            args.putString("appointmentType", APPT_PAST);
        AppointmentUpcomingFragment f = new AppointmentUpcomingFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        userProfile = sessionFacade.getMyCtcaUserProfile();
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_upcoming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            permissionMessage = getString(R.string.telehealth_permission_bt_message);
            permissionsTitle = getString(R.string.telehealth_permission_bt_title);
            allPermissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_CONNECT};
        } else {
            permissionsTitle = getString(R.string.telehealth_permission_title);
            permissionMessage = getString(R.string.telehealth_permission_message);
        }
        // Section Adapter
        mApptAdapter = new SectionedRecyclerViewAdapter();

        // Find Views
        mApptRecyclerView = view.findViewById(R.id.apptRecyclerView);
        mApptRefreshLayout = view.findViewById(R.id.apptRefreshLayout);
        View mEmptyView = view.findViewById(R.id.appt_empty_view);
        mEmptyApptRequestButton = view.findViewById(R.id.appt_request_appointment);

        // Pull To Refresh
        mApptRefreshLayout.setOnRefreshListener(this::refreshItems);
        if (getArguments() != null)
            appointmentType = getArguments().getString("appointmentType");
        setEmptyListView();
        setApptRecyclerView(mEmptyView);
        downloadAppointments(context.getString(R.string.get_appointments_indicator));
    }

    private void setEmptyListView() {
        if (isProxyUser()) {
            mEmptyApptRequestButton.setVisibility(View.GONE);
        } else {
            if (userProfile != null && userProfile.userCan(UserPermissions.VIEW_APPOINTMENTS)) {
                setTextClickable();
            } else {
                mEmptyApptRequestButton.setText(context.getString(R.string.appt_empty_no_permissions));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.toolbar_appts_new) {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentUpcomingFragment::onOptionsItemSelected", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_REQUEST_TAP));
            Log.d(TAG, "New Appointment Request selected");
            Intent intent = new Intent(context, AppointmentRequestActivity.class);
            intent.putExtra(AppointmentRequest.APPT_REQUEST_TYPE, AppointmentRequest.APPT_NEW);
            startActivity(intent);
        }
        return true;
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

    private void setTextClickable() {
        mEmptyApptRequestButton.setOnClickListener(view -> {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentUpcomingFragment::setTextClickable", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_REQUEST_TAP));
            Intent intent = new Intent(context, AppointmentRequestActivity.class);
            intent.putExtra(AppointmentRequest.APPT_REQUEST_TYPE, AppointmentRequest.APPT_NEW);
            startActivity(intent);
        });
    }


    private void setApptRecyclerView(View mEmptyView) {
        // RecyclerView
        mApptRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mApptRecyclerView.setLayoutManager(layoutManager);
        mApptRecyclerView.setAdapter(mApptAdapter);
        mApptRecyclerView.setEmptyView(mEmptyView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        mApptRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void downloadAppointments(String indicationStr) {
        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_APPOINTMENTS)) {
            if (context != null)
                ((NavActivity) context).showActivityIndicator(indicationStr);
            if (appointmentType.equals(APPT_UPCOMING))
                sessionFacade.getAppointments(context, APPT_UPCOMING, this);
            else
                sessionFacade.getAppointments(context, APPT_PAST, this);

        }
    }

    public void updateUI(Map<String, List<Appointment>> listMap, List<String> apptSections) {

        int sectionCount = 0;

        List<String> mApptSections = apptSections;
        Map<String, List<Appointment>> mAppointments = listMap;
        mApptAdapter.removeAllSections();

        for (String dateStr : mApptSections) {
            mApptAdapter.addSection(new AppointmentSectionAdapter(this, context, dateStr, mAppointments.get(dateStr), sectionCount, appointmentType));
            sectionCount++;
        }
        for (int i = 0; i < mApptRecyclerView.getItemDecorationCount(); i++) {
            if (mApptRecyclerView.getItemDecorationAt(i) instanceof DividerItemDecoration)
                mApptRecyclerView.removeItemDecorationAt(i);
        }
        mApptRecyclerView.getRecycledViewPool().clear();
        mApptAdapter.notifyDataSetChanged();

        // If the page is refreshing, we want to stay at the top of the view/
        // Otherwise, we want to scroll to the newest message.
        if (mApptRefreshLayout.isRefreshing()) {
            onRefreshItemsLoadComplete();
        } else {
            mApptRecyclerView.scrollToPosition(0);
        }
    }

    // Swipe Refresh Layout
    private void refreshItems() {
        if (userProfile.userCan(UserPermissions.VIEW_APPOINTMENTS)) {
            sessionFacade.clearAppointments();
            downloadAppointments(context.getString(R.string.refresh_appointments_indicator));
        }
        onRefreshItemsLoadComplete();
    }

    private void onRefreshItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        Log.d(TAG, "ApptFragment onRefreshItemsLoadComplete() {\n");
        // Stop refresh animation
        mApptRefreshLayout.setRefreshing(false);
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("ApptFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        alertDialog.setOnShowListener(arg0 -> alertDialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        alertDialog.show();
    }

    @Override
    public void notifyFetchSuccess(Map<String, List<Appointment>> apptsListMap, List<String> apptSections, String purpose) {
        updateUI(apptsListMap, apptSections);
        if (context != null) {
            ((NavActivity) context).hideActivityIndicator();
            Fragment appointmentFragment = ((NavActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (appointmentFragment instanceof AppointmentFragment)
                ((AppointmentFragment) appointmentFragment).setMenuButtons();
        }
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        updateUI(Collections.emptyMap(), Collections.emptyList());
        if (context != null)
            ((NavActivity) context).hideActivityIndicator();
        showRequestFailure(error);
    }

    @Override
    public void notifyMeetingAccessToken(MeetingAccessTokenJson json) {
        ((NavActivity) context).hideActivityIndicator();
        token = json.getMeetingAccessToken().getToken();
        getAllPermissions();
    }

    private void getAllPermissions() {
        ArrayList<String> permissionsToAskFor = new ArrayList<>();
        for (String permission : allPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToAskFor.add(permission);
            }
        }
        if (!permissionsToAskFor.isEmpty()) {
            showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_bt_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative), permissionsToAskFor);
        } else {
            openTelehealthScreen();
        }
    }

    private void openTelehealthScreen() {
        Intent intent = new Intent(context, TelehealthCommunicationActivity.class);
        intent.putExtra("TOKEN", token);
        intent.putExtra("APPOINTMENT", appointment);
        startActivity(intent);
        AppSessionManager.getInstance().setIdleTimeout(36000000);
    }

    @Override
    public void openTelehealthCommunicationScreen(Appointment appointment) {
        if (isNetworkAvailable()) {
            this.appointment = appointment;
            ((NavActivity) context).showActivityIndicator(context.getString(R.string.login_options_loading));
            sessionFacade.downloadMeetingAccessToken(context, appointment.getMeetingId(), this);
        } else {
            showErrorDialog(context.getString(R.string.telehealth_internet_error_title), context.getString(R.string.telehealth_internet_error_message), true);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    public void showPostErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyPostSuccess(boolean ifError, String error) {
        //do nothing
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        ((NavActivity) context).hideActivityIndicator();
        if (task == MyCTCATask.MEETING_ACCESS_TOKEN) {
            showErrorDialog(context.getString(R.string.telehealth_error_title), context.getString(R.string.telehealth_error_message), false);
        } else {
            if (message.isEmpty())
                message = context.getString(R.string.error_400);
            showPostErrorMessage(message);
        }
    }

    private void showErrorDialog(String title, String message, boolean isReasonInternet) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!isReasonInternet) {
            dialog = builder.setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Not Now", (dialog1, which) -> {
                    })
                    .setPositiveButton("Continue", (dialog1, which) -> {
                        if (isNetworkAvailable()) {
                            if (!appointment.getTeleHealthUrl().isEmpty()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appointment.getTeleHealthUrl()));
                                startActivityForResult(intent, TELEHEALTH_MEETING);
                            }
                        } else {
                            dialog.dismiss();
                            showErrorDialog(context.getString(R.string.telehealth_internet_error_title), context.getString(R.string.telehealth_internet_error_message), true);
                        }
                    }).create();
        } else {
            dialog = builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", ((dialogInterface, i) -> {
                    })).create();
        }

        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TELEHEALTH_MEETING)
            GeneralUtil.logoutApplication();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           int[] grantResults) {
        if (requestCode == TELEHEALTH_MEETING) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                allPermissionsGranted &= (result == PackageManager.PERMISSION_GRANTED);
            }
            if (allPermissionsGranted) {
                openTelehealthScreen();
            } else {
                ArrayList<String> permissionsToAskFor = new ArrayList<>();
                boolean somePermissionsForeverDenied = false;
                boolean denied = false;
                for (String permission : permissions) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        //denied
                        denied = true;
                        permissionsToAskFor.add(permission);
                        Log.e("denied", permission);
                    } else {
                        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            //allowed
                            Log.e("allowed", permission);
                        } else {
                            //set to never ask again
                            Log.e("set to never ask again", permission);
                            somePermissionsForeverDenied = true;
                        }
                    }
                }
                if (somePermissionsForeverDenied) {
                    showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(permissionsTitle, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(permissionMessage, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_positive), getString(R.string.telehealth_permission_negative), new ArrayList<>());
                } else if (denied) {
                    showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_bt_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative), permissionsToAskFor);
                }
            }
        }
    }

    private void showPermissionError(String title, String message, String positiveBtn, String negativeBtn, ArrayList<String> permissionsToAskFor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(60, 30, 55, 0);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_COMPACT));
        AlertDialog dialog = builder.setCustomTitle(textView)
                .setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(positiveBtn, (dialogInterface, i) -> {
                    if (positiveBtn.equals(getString(R.string.telehealth_permission_positive))) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        requestPermissions(permissionsToAskFor.toArray(new String[0]), TELEHEALTH_MEETING);
                    }
                })
                .setNegativeButton(negativeBtn, (dialogInterface, i) -> {
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }
}
