package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

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
import com.myctca.fragment.CareTeamsDialogFragment;
import com.myctca.fragment.PrescriptionRefillFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.CareTeam;
import com.myctca.model.Prescription;
import com.myctca.service.HealthHistoryService;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrescriptionRefillActivity extends MyCTCAActivity implements HealthHistoryService.HealthHistoryServicePostListener, CareTeamsDialogFragment.CareTeamsDialogListener, CustomDialogTopBottom.CustomDialogListener {

    private static final String TAG = "myCTCA-PRESCRIPTREFILL";
    private static final String PRESCRIPTION_LIST_KEY = "prescriptionsList";
    public List<Prescription> prescriptions;
    private SessionFacade sessionFacade;

    public static Intent refillRequestIntent(Context packageContext, List<Prescription> prescriptions) {
        Intent intent = new Intent(packageContext, PrescriptionRefillActivity.class);
        intent.putExtra(PRESCRIPTION_LIST_KEY, (ArrayList<Prescription>) prescriptions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CTCAAnalyticsManager.createEvent("PrescriptionRefillActivity:onCreate", CTCAAnalyticsConstants.PAGE_PRESCRIPTION_RENEWAL_VIEW, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_refill);

        sessionFacade = new SessionFacade();
        Intent i = getIntent();
        if (i != null) {
            this.prescriptions = (ArrayList<Prescription>) i.getSerializableExtra(PRESCRIPTION_LIST_KEY);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.prescription_refill_fragment_container);

        if (fragment == null) {
            fragment = new PrescriptionRefillFragment();
            fm.beginTransaction()
                    .add(R.id.prescription_refill_fragment_container, fragment)
                    .commit();
        }

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_prescription_refill, menu);

        // Tint the Send Button in the Menu
        Drawable drawable = menu.findItem(R.id.toolbar_prescription_refill_send).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
        menu.findItem(R.id.toolbar_prescription_refill_send).setIcon(drawable);

        String toolbarTitle = getString(R.string.prescription_refill_title);
        setToolBar(toolbarTitle);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.toolbar_prescription_refill_send:
                CTCAAnalyticsManager.createEvent("PrescriptionRefillActivity:onOptionsItemSelected", CTCAAnalyticsConstants.ACTION_PRESCRIPTION_RENEWAL_SEND_TAP, null, null);
                Log.d(TAG, "SEND REQUEST RENEWAL PRESSED");
                sendRefillRequest();
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

    @Override
    public void onBackPressed() {
        showLeaveAppointmentDialog();
    }

    private void showLeaveAppointmentDialog() {
        AlertDialog dialog = new CustomDialogTopBottom().getDialog(this, this, "", getString(R.string.appt_request_leave_message), getString(R.string.appt_stay_on_page), getString(R.string.appt_leave_page));
        if (!isFinishing())
            dialog.show();
    }

    private void sendRefillRequest() {
        Log.d(TAG, "sendMail");
        dismissKeyboard();
        FragmentManager fm = getSupportFragmentManager();
        PrescriptionRefillFragment fragment = (PrescriptionRefillFragment) fm.findFragmentById(R.id.prescription_refill_fragment_container);


        if (!formIsValid(fragment) && !phoneNoIsValid(fragment)) {
            incompleteFormDialog(getString(R.string.phone_no_invalid_error_title), getString(R.string.phone_no_invalid_error_message));
        } else if (!formIsValid(fragment) && !pharmacyPhoneNoIsValid(fragment)) {
            incompleteFormDialog(getString(R.string.phone_no_invalid_error_title), getString(R.string.phone_no_invalid_error_message));
        } else if (formIsValid(fragment)) {
            submitForm(fragment);
        } else {
            Log.d(TAG, "form is not valid");
            incompleteFormDialog(getString(R.string.new_appt_invalid_form_title), getString(R.string.new_appt_invalid_form_message));
        }
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
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

    private boolean formIsValid(PrescriptionRefillFragment fragment) {

        return fragment.formIsValid();
    }

    private boolean phoneNoIsValid(PrescriptionRefillFragment fragment) {

        return fragment.isPhoneNoValid();
    }

    private boolean pharmacyPhoneNoIsValid(PrescriptionRefillFragment fragment) {

        return fragment.isPharmacyPhoneNoValid();
    }

    private void submitForm(final PrescriptionRefillFragment fragment) {
        Gson gson = new Gson();
        String json = gson.toJson(fragment.getRefillRequest());
        Log.d(TAG, "submitForm: " + json);
        showActivityIndicator(getString(R.string.submit_request_renewal_indicator));
        sessionFacade.submitRequestRenewalForm(this, this, json);
    }

    private void showRefillRequestSuccess() {
        CTCAAnalyticsManager.createEvent("PrescriptionRefillActivity:showRefillRequestSuccess", CTCAAnalyticsConstants.ALERT_PRESCRIPTION_RENEWAL_SUCCESS, null, null);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.renew_prescription);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorPrimary));
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, true, getString(R.string.prescription_refill_sent_success_title), getString(R.string.prescription_refill_sent_success_message));
        if (!isFinishing())
            dialog.show();
    }

    private void showRefillRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("PrescriptionRefillActivity:showRefillRequestFailure", CTCAAnalyticsConstants.ALERT_PRESCRIPTION_RENEWAL_FAIL, null, null);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.renew_prescription);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.redWarningContent));
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, false, getString(R.string.prescription_refill_sent_failure_title), message);
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void onCareTeamsSelectionDone(Map<String, Boolean> selectedCareTeams, List<CareTeam> careTeamsDetails) {
        Log.d(TAG, "onCareTeamsSelectionDone: " + selectedCareTeams);
        PrescriptionRefillFragment fragment = (PrescriptionRefillFragment) getSupportFragmentManager().findFragmentById(R.id.prescription_refill_fragment_container);

        assert fragment != null;
        fragment.setCareTeamsInputText(selectedCareTeams, careTeamsDetails);
    }

    @Override
    public void notifyPostSuccess() {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        showRefillRequestSuccess();
    }

    @Override
    public void notifyPostError(String message) {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        showRefillRequestFailure(message);
    }

    @Override
    public void negativeButtonAction() {
        super.onBackPressed();
    }
}
