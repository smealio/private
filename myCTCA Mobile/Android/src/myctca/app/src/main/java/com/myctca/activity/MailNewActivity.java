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
import com.myctca.common.view.CustomDialogTopBottom;
import com.myctca.common.view.CustomRequestDialog;
import com.myctca.fragment.CareTeamsDialogFragment;
import com.myctca.fragment.MailNewFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.CareTeam;
import com.myctca.model.Mail;
import com.myctca.model.MailBoxTask;
import com.myctca.service.MailService;
import com.myctca.service.SessionFacade;

import java.util.List;
import java.util.Map;

public class MailNewActivity extends MyCTCAActivity
        implements MailService.MailServicePostListener, CareTeamsDialogFragment.CareTeamsDialogListener,
        CustomDialogTopBottom.CustomDialogListener {

    private static final String TAG = "myCTCA-NEWMAIL";
    private Mail respondingMail;
    private SessionFacade sessionFacade;

    public static Intent newMailIntent(Context packageContext) {
        return new Intent(packageContext, MailNewActivity.class);
    }

    public static Intent newReplyIntent(Context packageContext, Mail respondingMail) {
        Intent intent = new Intent(packageContext, MailNewActivity.class);
        intent.putExtra("respondingMail", respondingMail);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_new);
        sessionFacade = new SessionFacade();

        Intent i = getIntent();
        this.respondingMail = (Mail) i.getSerializableExtra("respondingMail");

        FragmentManager fm = getSupportFragmentManager();
        MailNewFragment fragment = new MailNewFragment();
        fm.beginTransaction()
                .add(R.id.new_mail_fragment_container, fragment)
                .commit();
        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mail_new, menu);

        // Tint the Send Button in the Menu
        Drawable drawable = menu.findItem(R.id.toolbar_new_mail_send).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
        menu.findItem(R.id.toolbar_new_mail_send).setIcon(drawable);

        String toolbarTitle = getString(R.string.new_mail_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.toolbar_new_mail_send:
                Log.d(TAG, "SEND MAIL PRESSED");
                sendMail();
                break;
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                onBackPressed();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }
        return true;
    }

    private void showLeaveAppointmentDialog() {
        AlertDialog dialog = new CustomDialogTopBottom().getDialog(this, this, "", getString(R.string.appt_request_leave_message), getString(R.string.appt_stay_on_page), getString(R.string.appt_leave_page));
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void onBackPressed() {
        showLeaveAppointmentDialog();
    }

    public Mail getRespondingMail() {
        return respondingMail;
    }

    private void sendMail() {
        Log.d(TAG, "sendMail");
        FragmentManager fm = getSupportFragmentManager();
        MailNewFragment fragment = (MailNewFragment) fm.findFragmentById(R.id.new_mail_fragment_container);

        assert fragment != null;
        if (fragment.formIsValid()) {
            submitForm(fragment);
        } else {
            Log.d(TAG, "form is not valid");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.new_mail_invalid_form_title))
                    .setMessage(getString(R.string.new_mail_invalid_form_message))
                    .setPositiveButton(getString(R.string.nav_alert_ok), (dialog, which) -> dialog.cancel());
            if (!isFinishing())
                builder.show();
        }
    }

    private void submitForm(final MailNewFragment fragment) {
        Log.d(TAG, "submitForm: " + fragment.getNewMailSendData());
        showActivityIndicator(getString(R.string.send_new_mail_indicator));
        sessionFacade.setOnServer(this, this, new Gson().toJson(fragment.getNewMailSendData()), MailBoxTask.SEND_NEW);
    }

    private void showNewMailRequestSuccess() {
        CTCAAnalyticsManager.createEvent("MailNewActivity:showNewMailRequestSuccess", CTCAAnalyticsConstants.ALERT_NEW_MAIL_SUCCESS, null, null);
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, true, getString(R.string.new_mail_sent_success_title), getString(R.string.new_mail_sent_success_message));
        if (!isFinishing())
            dialog.show();
    }

    private void showNewMailRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MailNewActivity:showNewMailRequestFailure", CTCAAnalyticsConstants.ALERT_NEW_MAIL_FAIL, null, null);
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, false, getString(R.string.failure_title), message);
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void notifyPostSuccess(boolean success, String response, int task) {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        if (success)
            showNewMailRequestSuccess();
        else
            showNewMailRequestFailure(response);
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_new_mail);
        CTCAAnalyticsManager.createEvent("MailNewActivity:notifyPostError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        hideActivityIndicator();
        // Error handling
        Log.d(TAG, "Something went wrong while trying to send new mail request! Error: " + error.toString() + "::" + error.getLocalizedMessage());
        if (message.isEmpty())
            message = getString(R.string.error_400);
        showNewMailRequestFailure(message);
    }

    @Override
    public void onCareTeamsSelectionDone(Map<String, Boolean> selectedCareTeams, List<CareTeam> careTeamsDetails) {
        Log.d(TAG, "onCareTeamsSelectionDone: " + selectedCareTeams);
        MailNewFragment fragment = (MailNewFragment) getSupportFragmentManager().findFragmentById(R.id.new_mail_fragment_container);
        fragment.setCareTeamsInputText(selectedCareTeams, careTeamsDetails);
    }

    @Override
    public void negativeButtonAction() {
        super.onBackPressed();
    }
}
