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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.view.CustomDialogTopBottom;
import com.myctca.common.view.CustomRequestDialog;
import com.myctca.fragment.SendMessageFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.SendMessage;
import com.myctca.service.SendMessageService;
import com.myctca.service.SessionFacade;

public class SendMessageActivity extends MyCTCAActivity
        implements SendMessageService.SendMessageInterface,
        CustomDialogTopBottom.CustomDialogListener {

    private static final String TAG = "myCTCA-SendMsg";
    private SessionFacade sessionFacade;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, SendMessageActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionFacade = new SessionFacade();
        setContentView(R.layout.activity_send_message);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new SendMessageFragment();
        fm.beginTransaction()
                .add(R.id.send_message_fragment_container, fragment)
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
        inflater.inflate(R.menu.menu_send_message, menu);

        // Tint the Send Button in the Menu
        Drawable drawable = menu.findItem(R.id.toolbar_send_message_send).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
        menu.findItem(R.id.toolbar_send_message_send).setIcon(drawable);

        String toolbarTitle = getString(R.string.send_message_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.toolbar_send_message_send:
                Log.d(TAG, "SEND MESSAGE PRESSED");
                sendMessage();
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

    @Override
    public void onBackPressed() {
        showLeaveAppointmentDialog();
    }

    private void showLeaveAppointmentDialog() {
        AlertDialog dialog = new CustomDialogTopBottom().getDialog(this, this, "", getString(R.string.appt_request_leave_message), getString(R.string.appt_stay_on_page), getString(R.string.appt_leave_page));
        if (!isFinishing())
            dialog.show();
    }

    private void sendMessage() {
        FragmentManager fm = getSupportFragmentManager();
        SendMessageFragment fragment = (SendMessageFragment) fm.findFragmentById(R.id.send_message_fragment_container);
        assert fragment != null;
        if (!formIsValid(fragment) && !showEmailIdInvalidDialog(fragment)) {
            incompleteFormDialog(getString(R.string.email_id_invalid_error_title), getString(R.string.email_id_invalid_error_message));
        } else if (!formIsValid(fragment) && !showPhoneNoInvalidDialog(fragment)) {
            incompleteFormDialog(getString(R.string.phone_no_invalid_error_title), getString(R.string.phone_no_invalid_error_message));
        } else if (formIsValid(fragment)) {
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

    private boolean formIsValid(SendMessageFragment fragment) {
        Log.d(TAG, "formIsValid");
        return fragment.formIsValid();
    }

    private boolean showPhoneNoInvalidDialog(SendMessageFragment fragment) {
        return fragment.showPhoneNoInvalidDialog();
    }

    private boolean showEmailIdInvalidDialog(SendMessageFragment fragment) {
        return fragment.showEmailIdInvalidDialog();
    }

    private void submitForm(final SendMessageFragment fragment) {
        Log.d(TAG, "submitForm");
        showActivityIndicator("Submitting request…");

        SendMessage sendMessage = fragment.getSendMessageData();
        sessionFacade.sendMessage(this, this, new Gson().toJson(sendMessage));
    }

    private void showShowMessageSuccess() {
        hideActivityIndicator();
        CTCAAnalyticsManager.createEvent("SendMessageActivity:showRequestSuccess", CTCAAnalyticsConstants.ALERT_SEND_MESSAGE_SUCCESS, null, null);
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, true, getString(R.string.send_message_success_title), getString(R.string.send_message_success_message));
        if (!isFinishing())
            dialog.show();
    }

    private void showSendMessageFailure(String message) {
        hideActivityIndicator();
        CTCAAnalyticsManager.createEvent("SendMessageActivity:showRequestFailure", CTCAAnalyticsConstants.ALERT_SEND_MESSAGE_FAIL, null, null);
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, false, getString(R.string.send_message_failure_title), message);
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void notifyPostSuccess(String response) {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        Log.d(TAG, "SEND MESSAGE Request Response: " + response);

        if (response.equals("\"Success\"")) {
            showShowMessageSuccess();
        } else {
            showSendMessageFailure(response);
        }
    }

    @Override
    public void notifyPostError(String message) {
        showSendMessageFailure(message);
    }

    @Override
    public void negativeButtonAction() {
        super.onBackPressed();
    }
}
