package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.view.CustomRequestDialog;
import com.myctca.fragment.TimePickerFragment;
import com.myctca.fragment.appointmment.AppointmentChangeFragment;
import com.myctca.fragment.appointmment.AppointmentDatePickerFragment;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MeetingAccessTokenJson;
import com.myctca.model.MyCTCATask;
import com.myctca.service.AppointmentService;
import com.myctca.service.SessionFacade;

import java.util.Date;

public class AppointmentChangeActivity extends MyCTCAActivity
        implements AppointmentDatePickerFragment.DatePickerFragmentListener,
        TimePickerFragment.TimePickerFragmentListener,
        AppointmentService.AppointmentServicePostListener {

    public static final String APPT_KEY = "Appointment";
    public static final String CHANGE_KEY = "ChangeType";
    private static final String TAG = "myCTCA-CHANGEAPPT";
    private ChangeType changeType = ChangeType.CANCEL;
    private SessionFacade sessionFacade;

    public static Intent newIntent(Context packageContext, Appointment appt, ChangeType changeType) {
        Log.d(TAG, "newIntent: " + changeType);
        Intent intent = new Intent(packageContext, AppointmentChangeActivity.class);
        intent.putExtra(CHANGE_KEY, changeType);
        intent.putExtra(APPT_KEY, appt);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_change);
        sessionFacade = new SessionFacade();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.changeType = (ChangeType) extras.getSerializable(CHANGE_KEY);
            Appointment appointment = (Appointment) extras.getSerializable(APPT_KEY);
            Log.d(TAG, "ApptChangeActivity changeType: " + this.changeType);

            FragmentManager fm = getSupportFragmentManager();
            AppointmentChangeFragment fragment = (AppointmentChangeFragment) fm.findFragmentById(R.id.change_appt_fragment_container);

            if (fragment == null) {
                fragment = new AppointmentChangeFragment();
                fm.beginTransaction()
                        .add(R.id.change_appt_fragment_container, fragment)
                        .commit();
            }
            fragment.setChangeType(this.changeType);
            fragment.setAppointment(appointment);

            //Internet banner
            llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
            llInternetConnected = findViewById(R.id.ll_internet_connected);
            selectedFragment = fragment;
            fragmentName = selectedFragment.getClass().getSimpleName();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appt_change, menu);

        // Tint the Send Button in the Menu
        Drawable drawable = menu.findItem(R.id.toolbar_change_appt_send).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
        menu.findItem(R.id.toolbar_change_appt_send).setIcon(drawable);

        if (this.changeType == ChangeType.CANCEL) {
            setToolBar(getString(R.string.cancel_appt_title));
        } else {
            setToolBar(getString(R.string.reschedule_appt_title));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.toolbar_change_appt_send:
                Log.d(TAG, "SUBMIT REQUEST PRESSED");
                submitRequest();
                break;
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                this.onBackPressed();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }

        return true;
    }

    private void submitRequest() {
        Log.d(TAG, "submitRequest");
        FragmentManager fm = getSupportFragmentManager();
        AppointmentChangeFragment fragment = (AppointmentChangeFragment) fm.findFragmentById(R.id.change_appt_fragment_container);

        if (!isFormValid(fragment) && !isPhoneNoValid(fragment)) {
            incompleteFormDialog(getString(R.string.phone_no_invalid_error_title), getString(R.string.phone_no_invalid_error_message));
        } else if (isFormValid(fragment)) {
            submitForm(fragment);
        } else {
            Log.d(TAG, "form is not valid");
            incompleteFormDialog(getString(R.string.new_appt_invalid_form_title), getString(R.string.new_appt_invalid_form_message));
        }
    }

    private void incompleteFormDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog, which) -> dialog.cancel());
        if (!isFinishing())
            builder.show();
    }

    private boolean isFormValid(AppointmentChangeFragment fragment) {

        return fragment.isFormValid();
    }

    private boolean isPhoneNoValid(AppointmentChangeFragment fragment) {

        return fragment.isPhoneNoValid();
    }

    private void submitForm(final AppointmentChangeFragment fragment) {
        Log.d(TAG, "submitForm: " + fragment.getFormData());

        showActivityIndicator(getString(R.string.send_appt_form_request_indicator));
        sessionFacade.changeAppointmentsRequest(AppointmentRequest.APPT_RESCHEDULE, new Gson().toJson(fragment.getFormData()), this, this);
    }

    private void showChangeApptRequestSuccess() {

        String msg;
        if (changeType == ChangeType.CANCEL) {
            CTCAAnalyticsManager.createEvent("ApptChangeActivity:showChangeApptRequestSuccess", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_CANCEL_SUCCESS, null, null);
            msg = getString(R.string.change_appt_request_success_message, getString(R.string.change_appt_cancellation));
        } else {
            CTCAAnalyticsManager.createEvent("ApptChangeActivity:showChangeApptRequestSuccess", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_RESCHEDULE_SUCCESS, null, null);
            msg = getString(R.string.change_appt_request_success_message, getString(R.string.change_appt_reschedule));
        }
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, true, getString(R.string.change_appt_request_success_title), msg);
        if (!isFinishing())
            dialog.show();
    }

    private void showChangeApptRequestFailure(String message) {
        if (changeType == ChangeType.CANCEL) {
            CTCAAnalyticsManager.createEvent("ApptChangeActivity:showChangeApptRequestFailure", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_CANCEL_FAIL, null, null);
        } else {
            CTCAAnalyticsManager.createEvent("ApptChangeActivity:showChangeApptRequestFailure", CTCAAnalyticsConstants.ALERT_APPOINTMENTS_RESCHEDULE_FAIL, null, null);
        }
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, false, getString(R.string.change_appt_request_failure_title), message);
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void onDateSet(Date date) {
        Log.d(TAG, "ChangeAppointment Actviity dateSet: " + date);

        FragmentManager fm = getSupportFragmentManager();
        AppointmentChangeFragment fragment = (AppointmentChangeFragment) fm.findFragmentById(R.id.change_appt_fragment_container);

        fragment.setChangeAppointmentDate(date);
    }

    @Override
    public void onTimeSet(Date date) {
        Log.d(TAG, "changeAppointment Actviity timeSet: " + date);

        FragmentManager fm = getSupportFragmentManager();
        AppointmentChangeFragment fragment = (AppointmentChangeFragment) fm.findFragmentById(R.id.change_appt_fragment_container);

        fragment.setChangeAppointmentTime(date);
    }

    @Override
    public void notifyPostSuccess(boolean isError, String error) {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        if (isError) {
            showChangeApptRequestFailure(error);
        } else {
            showChangeApptRequestSuccess();
        }
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        String url = BuildConfig.myctca_server;
        if (task == MyCTCATask.APPT_CANCEL) {
            url += getString(R.string.myctca_cancel_appointments);
            CTCAAnalyticsManager.createEvent("ApptChangeActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        } else {
            url += getString(R.string.myctca_reschedule_appointments);
            CTCAAnalyticsManager.createEvent("ApptChangeActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        }
        hideActivityIndicator();
        // Error handling
        if (message.isEmpty())
            message = getString(R.string.error_400);
        showChangeApptRequestFailure(message);
    }

    @Override
    public void notifyMeetingAccessToken(MeetingAccessTokenJson json) {
        //do nothing
    }

    public enum ChangeType {
        RESCHEDULE,
        CANCEL
    }
}
