package com.myctca.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.view.CustomDialogTopBottom;
import com.myctca.common.view.CustomRequestDialog;
import com.myctca.fragment.appointmment.AppointmentPreferredDateTimeFragment;
import com.myctca.fragment.appointmment.AppointmentRequestCommonFragment;
import com.myctca.fragment.appointmment.ApptRequestContactPreferenceFragment;
import com.myctca.fragment.appointmment.ApptRequestSummaryFragment;
import com.myctca.fragment.appointmment.BaseAppointmentFragment;
import com.myctca.model.AppointmentCalendarData;
import com.myctca.model.AppointmentDateTime;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.AppointmentRequestData;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MeetingAccessTokenJson;
import com.myctca.service.AppointmentService;
import com.myctca.service.CommonService;
import com.myctca.service.SessionFacade;
import com.myctca.util.MyCTCADateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AppointmentRequestActivity extends MyCTCAActivity
        implements AppointmentService.AppointmentServicePostListener, CommonService.CommonServiceListener,
        CustomDialogTopBottom.CustomDialogListener {
    private static final String CONTACT_INFO_PURPOSE = "CONTACT_INFO";
    private static final String MORNING = "MORNING";
    private static final String NOON = "AFTERNOON";
    private static final String FULL_DAY = "ALL_DAY";
    private AppointmentRequestData appointmentRequestData;
    private ImageView screenProgressBar;
    private LinearLayout llPrevious;
    private LinearLayout llNext;
    private Button btnSaveChanges;
    private int apptRequestType = 0;
    private List<Integer> currentArray;
    private List<AppointmentCalendarData> appointmentCalendarDataList;
    private SessionFacade sessionFacade;
    private LinearLayout backNextLayout;
    private Button btnApptSubmitRequest;
    private String appointmentRequestName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_request);
        sessionFacade = new SessionFacade();
        setAppointmentRequestData();

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        screenProgressBar = findViewById(R.id.screenProgressBar);

        //bottom layout buttons
        backNextLayout = findViewById(R.id.backNextLayout);
        llPrevious = findViewById(R.id.llPrevious);
        llNext = findViewById(R.id.llNext);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnApptSubmitRequest = findViewById(R.id.btnApptSubmitRequest);

        setProgressBar();
        sessionFacade.downloadContactInfo(this, this, CONTACT_INFO_PURPOSE);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    private void setAppointmentRequestData() {
        if (getIntent() != null && getIntent().getExtras() != null)
            this.apptRequestType = getIntent().getExtras().getInt(AppointmentRequest.APPT_REQUEST_TYPE);
        appointmentRequestData = new AppointmentRequestData();
        appointmentRequestData.setAppointmentDate(MyCTCADateUtils.convertDateToLocalString(new Date()));
        appointmentRequestData.setFrom(sessionFacade.getMyCtcaUserProfile().getFullName());
        switch (apptRequestType) {
            case AppointmentRequest.APPT_CANCEL:
                appointmentRequestName = "Cancellation Request";
                appointmentRequestData.setSubject("Appointment " + appointmentRequestName);
                break;
            case AppointmentRequest.APPT_RESCHEDULE:
                appointmentRequestName = "Reschedule Request";
                appointmentRequestData.setSubject("Appointment " + appointmentRequestName);
                break;
            case AppointmentRequest.APPT_NEW:
                appointmentRequestName = "Request Appointment";
                appointmentRequestData.setSubject("Appointment New Request");
                break;
            default:
                //nothing
        }
    }

    public AppointmentRequestData getAppointmentRequestData() {
        return appointmentRequestData;
    }

    public int getApptRequestType() {
        return apptRequestType;
    }

    public void addFragment(Fragment fragment, boolean forEditing) {
        if (forEditing) {
            backNextLayout.setVisibility(View.GONE);
            btnSaveChanges.setVisibility(View.VISIBLE);
            btnApptSubmitRequest.setVisibility(View.GONE);
            screenProgressBar.setVisibility(View.GONE);
            btnSaveChanges.setOnClickListener(view -> {
                saveAppointmentData();
                openPreviousScreen();
            });
        } else {
            backNextLayout.setVisibility(View.VISIBLE);
            btnApptSubmitRequest.setVisibility(View.GONE);
            btnSaveChanges.setVisibility(View.GONE);
            screenProgressBar.setVisibility(View.VISIBLE);
        }
        enableNextButton(false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_from_right,  // enter
                        R.anim.slide_out_to_left,  // exit
                        R.anim.slide_in_from_left,   // popEnter
                        R.anim.slide_out_to_right  // popExit
                );
        selectedFragment = fragment;
        if (selectedFragment instanceof AppointmentRequestCommonFragment && getSupportFragmentManager().getBackStackEntryCount() > 1) {
            fragmentName = selectedFragment.getClass().getSimpleName() + getString(R.string.appt_additional_info_title);
        } else {
            fragmentName = selectedFragment.getClass().getSimpleName();
        }
        fragment.setArguments(getIntent().getExtras());
        transaction.replace(R.id.appt_request_container, fragment);
        transaction.addToBackStack(fragmentName);
        transaction.commit();
        prepareView(fragmentName);
    }

    private void setProgressBar() {
        if (apptRequestType == AppointmentRequest.APPT_NEW) {
            //progress bar with 5 dots
            List<Integer> apptNewProgressBar = new ArrayList<>();
            Collections.addAll(apptNewProgressBar, R.drawable.line_circle5_1,
                    R.drawable.line_circle5_2,
                    R.drawable.line_circle5_3,
                    R.drawable.line_circle5_4,
                    R.drawable.line_circle5_5);
            this.currentArray = apptNewProgressBar;
            //add 1st page
            addFragment(new AppointmentRequestCommonFragment(getString(R.string.appt_reason)), false);
        } else if (apptRequestType == AppointmentRequest.APPT_CANCEL || apptRequestType == AppointmentRequest.APPT_RESCHEDULE) {
            //progress bar with 4 dots
            List<Integer> apptChangeProgressBar = new ArrayList<>();
            Collections.addAll(apptChangeProgressBar, 0,
                    R.drawable.line_circle4_1,
                    R.drawable.line_circle4_2,
                    R.drawable.line_circle4_3,
                    R.drawable.line_circle4_4);
            this.currentArray = apptChangeProgressBar;
            //directly open 2nd page
            addFragment(new AppointmentPreferredDateTimeFragment(), false);
        }
    }

    public String getViewPageType() {
        switch (getApptRequestType()) {
            case AppointmentRequest.APPT_NEW:
                return CTCAAnalyticsConstants.PAGE_APPT_NEW_REQ;
            case AppointmentRequest.APPT_RESCHEDULE:
                return CTCAAnalyticsConstants.PAGE_APPT_RESCHED_REQ;
            case AppointmentRequest.APPT_CANCEL:
                return CTCAAnalyticsConstants.PAGE_APPT_CANCEL_REQ;
            default:
                return "";
        }
    }

    public String getActionPageType() {
        switch (getApptRequestType()) {
            case AppointmentRequest.APPT_NEW:
                return CTCAAnalyticsConstants.ACTION_APPT_NEW_REQ;
            case AppointmentRequest.APPT_RESCHEDULE:
                return CTCAAnalyticsConstants.ACTION_APPT_RESCHED_REQ;
            case AppointmentRequest.APPT_CANCEL:
                return CTCAAnalyticsConstants.ACTION_APPT_CANCEL_REQ;
            default:
                return "";
        }
    }

    private void prepareView(String fragmentName) {
        //set progress bar according to fragment opened
        if (fragmentName.equals(AppointmentRequestCommonFragment.class.getSimpleName())) {
            screenProgressBar.setImageDrawable(ContextCompat.getDrawable(this, currentArray.get(0)));
            llPrevious.setVisibility(View.GONE);
            llNext.setOnClickListener(view -> {
                addFragment(new AppointmentPreferredDateTimeFragment(), false);
                saveAppointmentData();
            });
        } else if (fragmentName.equals(AppointmentPreferredDateTimeFragment.class.getSimpleName())) {
            if (apptRequestType == AppointmentRequest.APPT_NEW)
                llPrevious.setVisibility(View.VISIBLE);
            else
                llPrevious.setVisibility(View.GONE);
            screenProgressBar.setImageDrawable(ContextCompat.getDrawable(this, currentArray.get(1)));
            llNext.setOnClickListener(view -> {
                addFragment(new ApptRequestContactPreferenceFragment(), false);
                saveAppointmentData();
            });
        } else if (fragmentName.equals(ApptRequestContactPreferenceFragment.class.getSimpleName())) {
            llPrevious.setVisibility(View.VISIBLE);
            screenProgressBar.setImageDrawable(ContextCompat.getDrawable(this, currentArray.get(2)));
            llNext.setOnClickListener(view -> {
                addFragment(new AppointmentRequestCommonFragment(getString(R.string.appt_additional_comments)), false);
                saveAppointmentData();
            });
        } else if (fragmentName.equals(AppointmentRequestCommonFragment.class.getSimpleName() + getString(R.string.appt_additional_info_title))) {
            llPrevious.setVisibility(View.VISIBLE);
            enableNextButton(true);
            screenProgressBar.setImageDrawable(ContextCompat.getDrawable(this, currentArray.get(3)));
            llNext.setOnClickListener(view -> {
                addFragment(new ApptRequestSummaryFragment(), false);
                saveAppointmentData();
            });
        } else {
            screenProgressBar.setImageDrawable(ContextCompat.getDrawable(this, currentArray.get(4)));
            backNextLayout.setVisibility(View.GONE);
            btnSaveChanges.setVisibility(View.GONE);
            btnApptSubmitRequest.setVisibility(View.VISIBLE);
            btnApptSubmitRequest.setOnClickListener(view -> {
                CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentRequestActivity:prepareView", getActionPageType() + CTCAAnalyticsConstants.ACTION_APPT_REQ_SUBMIT_TAP));
                submitAppointmentRequest();
            });
        }
        llPrevious.setOnClickListener(view -> openPreviousScreen());
    }

    private void saveAppointmentData() {
        //BaseAppointmentFragment is a common fragment with saveAppointmentData() function overridden in all Appt fragments
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.appt_request_container);
        if (fragment instanceof BaseAppointmentFragment) {
            ((BaseAppointmentFragment) fragment).saveAppointmentData();
        }
    }

    public void openPreviousScreen() {
        super.onBackPressed();
        screenProgressBar.setVisibility(View.VISIBLE);
        btnSaveChanges.setVisibility(View.GONE);
        btnApptSubmitRequest.setVisibility(View.GONE);
        backNextLayout.setVisibility(View.VISIBLE);

        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (index >= 0) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            if (tag != null) {
                fragmentName = tag;
                prepareView(tag);
            }
        } else {
            finish();
        }
        selectedFragment = getSupportFragmentManager().findFragmentById(R.id.more_forms_library_fragment_container);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() - 1 == 0)
            showLeaveAppointmentDialog();
        else
            openPreviousScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appt_request_calender, menu);

        String toolbarTitle = appointmentRequestName;
        setToolBar(toolbarTitle);

        //hide default back arrow in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_0) {
            showLeaveAppointmentDialog();
        } else {
            Log.d(TAG, "DEFAULT");
        }

        return true;
    }

    private void showLeaveAppointmentDialog() {
        AlertDialog dialog = new CustomDialogTopBottom().getDialog(this, this, "", getString(R.string.appt_request_leave_message), getString(R.string.appt_stay_on_page), getString(R.string.appt_leave_page));
        if (!isFinishing())
            dialog.show();
    }

    public void enableNextButton(boolean enable) {
        llNext.setEnabled(enable);
        llNext.setAlpha(enable ? 1 : 0.4f);

        btnSaveChanges.setEnabled(enable);
        btnSaveChanges.setAlpha(enable ? 1 : 0.4f);
    }

    public List<AppointmentCalendarData> getAppointmentCalendarDataList() {
        return appointmentCalendarDataList;
    }

    public void setAppointmentCalendarDataList(List<AppointmentCalendarData> appointmentCalendarDataList) {
        this.appointmentCalendarDataList = appointmentCalendarDataList;
    }

    private void showNewApptRequestSuccess() {
        CTCAAnalyticsManager.createEvent("ApptNewActivity:showNewApptRequestSuccess", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_REQUEST_SUCCESS, null, null);
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, true, getString(R.string.new_appt_request_success_title), getString(R.string.new_appt_request_success_message));
        if (!isFinishing())
            dialog.show();
    }

    private void showNewApptRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("ApptNewActivity:showNewApptRequestFailure", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_REQUEST_FAIL, null, null);
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, false, getString(R.string.new_appt_request_failure_title), message);
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void notifyPostSuccess(boolean isError, String error) {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        if (isError) {
            // We got an error
            showNewApptRequestFailure(error);
        } else {
            showNewApptRequestSuccess();
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_new_appointments);
        CTCAAnalyticsManager.createEvent("ApptNewActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        hideActivityIndicator();
        // Error handling
        Log.d(TAG, "Something went wrong while trying to submit new appointment request! Error: " + error.toString() + "::" + error.getLocalizedMessage());
        if (message.isEmpty())
            message = getString(R.string.error_400);
        showNewApptRequestFailure(message);
    }

    @Override
    public void notifyMeetingAccessToken(MeetingAccessTokenJson json) {
        //do nothing
    }

    public void submitAppointmentRequest() {
        trackCountEvents();
        showActivityIndicator(getString(R.string.send_appt_form_request_indicator));
        sessionFacade.changeAppointmentsRequest(apptRequestType, new Gson().toJson(appointmentRequestData), this, this);
    }

    private void trackCountEvents() {
        String pageType = "";
        switch (getApptRequestType()) {
            case AppointmentRequest.APPT_NEW:
                pageType = CTCAAnalyticsConstants.APPT_NEW_REQ;
                break;
            case AppointmentRequest.APPT_RESCHEDULE:
                pageType = CTCAAnalyticsConstants.APPT_RESCHED_REQ;
                break;
            case AppointmentRequest.APPT_CANCEL:
                pageType = CTCAAnalyticsConstants.APPT_CANCEL_REQ;
                break;
        }

        if (appointmentRequestData.getCommunicationPreference().equals(AppointmentRequest.CONTACT_PREFERENCE_CALL)) {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentRequestActivity:prepareView", pageType + CTCAAnalyticsConstants.CALL_ME_SELECTION));
        } else {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentRequestActivity:prepareView", pageType + CTCAAnalyticsConstants.EMAIL_ME_SELECTION));
        }

        for (AppointmentDateTime appointmentDateTime : appointmentRequestData.getAppointmentDateTimes()) {
            switch (appointmentDateTime.getTimePreference()) {
                case MORNING:
                    CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentRequestActivity:prepareView", pageType + CTCAAnalyticsConstants.MORNING_SLOT_SELECTION, appointmentDateTime.getDate()));
                    break;
                case NOON:
                    CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentRequestActivity:prepareView", pageType + CTCAAnalyticsConstants.NOON_SLOT_SELECTION, appointmentDateTime.getDate()));
                    break;
                case FULL_DAY:
                    CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentRequestActivity:prepareView", pageType + CTCAAnalyticsConstants.ALL_DAY_SLOT_SELECTION, appointmentDateTime.getDate()));
                    break;
            }
        }
    }

    @Override
    public void notifyFetchSuccess(String purpose) {
        appointmentRequestData.setPhoneNumber(sessionFacade.getUserContactNumber());
        appointmentRequestData.setEmail(sessionFacade.getUserEmail());
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        //do nothing
    }

    @Override
    public void negativeButtonAction() {
        CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("AppointmentRequestActivity:showLeaveAppointmentDialog", getActionPageType() + CTCAAnalyticsConstants.ACTION_APPT_REQ_LEAVE_TAP));
        finish();
    }
}