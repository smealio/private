package com.myctca.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;
import com.myctca.common.view.CustomDialogTopBottom;
import com.myctca.common.view.CustomRequestDialog;
import com.myctca.fragment.ConfidentialInfoDiscloseDialogFragment;
import com.myctca.fragment.DeliveryMethodDiscloseDialogFragment;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.fragment.GeneralInfoDiscloseDialogFragment;
import com.myctca.fragment.MoreAnncFormFragment;
import com.myctca.fragment.MoreAnncFragment;
import com.myctca.fragment.MoreDatePickerFragment;
import com.myctca.fragment.MoreFormsLibraryFragment;
import com.myctca.fragment.MoreReleaseOfInfoFormFragment;
import com.myctca.fragment.MoreReleaseOfInfoFragment;
import com.myctca.fragment.PurposeDiscloseDialogFragment;
import com.myctca.fragment.RoiAuthorizationDialogFragment;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MyCTCATask;
import com.myctca.service.MoreFormsLibraryService;
import com.myctca.service.SessionFacade;

import java.util.Date;
import java.util.Map;

public class MoreFormsLibraryActivity extends MyCTCAActivity
        implements MoreDatePickerFragment.MoreDatePickerFragmentListener,
        GeneralInfoDiscloseDialogFragment.GeneralInfoDiscloseDialogListener,
        ConfidentialInfoDiscloseDialogFragment.ConfidentialInfoDiscloseDialogListener,
        DeliveryMethodDiscloseDialogFragment.DeliveryMethodDiscloseDialogListener,
        PurposeDiscloseDialogFragment.PurposeDiscloseDialogListener,
        MoreFormsLibraryService.MoreFormsLibraryListenerPost,
        MoreFormsLibraryFragment.OnFragmentInteractionListener,
        MoreReleaseOfInfoFragment.OnFragmentInteractionListener,
        MoreAnncFragment.OnFragmentInteractionListener,
        RoiAuthorizationDialogFragment.RoiAuthDialogListener,
        CustomDialogTopBottom.CustomDialogListener {

    private static final String TAG = "myCTCA-MOREROI";
    private FragmentManager fm;
    private Fragment fragment;
    private SessionFacade sessionFacade;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MoreFormsLibraryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_forms_library);
        sessionFacade = new SessionFacade();
        fm = getSupportFragmentManager();
        fragment = new MoreFormsLibraryFragment();
        addFragment(fragment, fragment.getClass().getSimpleName());
        if (AppSessionManager.getInstance().isDeepLinking()) {
            fragment = new MoreAnncFormFragment();
            addFragment(fragment, fragment.getClass().getSimpleName());
            AppSessionManager.getInstance().setDeepLinking(false);
        }

        //Internet banner
        llNoInternetConnection = findViewById(R.id.ll_no_internet_connection);
        llInternetConnected = findViewById(R.id.ll_internet_connected);
        selectedFragment = fragment;
        fragmentName = selectedFragment.getClass().getSimpleName();

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void addFragment(Fragment fragment, String tag) {
        if (fragment.getClass().isAssignableFrom(DownloadPdfFragment.class)) {
            Bundle bundle = new Bundle();
            if (tag.equals(MoreReleaseOfInfoFragment.class.getSimpleName())) {
                bundle.putString("TOOLBAR_NAME", getString(R.string.more_roi_title));
                ((DownloadPdfFragment) fragment).setFileName("ROI.pdf");
                ((DownloadPdfFragment) fragment).setPdfFor("ROI");
                ((DownloadPdfFragment) fragment).params = null;
                ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_getroi_pdf));
                ((DownloadPdfFragment) fragment).setPdfCheck(AppSessionManager.getInstance().getROIPdfInstalled());
            } else {
                bundle.putString("TOOLBAR_NAME", getString(R.string.adv_notice_title));
                ((DownloadPdfFragment) fragment).setFileName(getString(R.string.annc_pdf) + ".pdf");
                ((DownloadPdfFragment) fragment).setPdfFor(getString(R.string.annc_pdf));
                ((DownloadPdfFragment) fragment).params = null;
                ((DownloadPdfFragment) fragment).setmUrl(BuildConfig.myctca_server + getString(R.string.myctca_get_annc_pdf));
                ((DownloadPdfFragment) fragment).setPdfCheck(AppSessionManager.getInstance().getANNCPdfInstalled());
            }
            fragment.setArguments(bundle);
        }

        this.fragment = fragment;
        selectedFragment = fragment;
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_from_right,  // enter
                R.anim.slide_out_to_left,  // exit
                R.anim.slide_in_from_left,   // popEnter
                R.anim.slide_out_to_right  // popExit
        );
        transaction.add(R.id.more_forms_library_fragment_container, fragment);
        if (!fragment.getClass().isAssignableFrom(MoreFormsLibraryFragment.class)) {
            transaction.addToBackStack(MoreReleaseOfInfoFragment.class.getSimpleName());
        }
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.toolbar_roi_send:
                Log.d(TAG, "SUBMIT ROI PRESSED");
                submitROI();
                break;
            case R.id.toolbar_annc_send:
                Log.d(TAG, "SUBMIT ANNC PRESSED");
                submitANNC();
                break;
            case android.R.id.home:
                Log.d(TAG, "BACK BUTTON PRESSED");
                onBackPressed();
                break;
            case R.id.item_share_pdf:
                shareRoiPdf();
                break;
            case R.id.item_print_pdf:
                openMorePdfOptions();
                break;
            default:
                Log.d(TAG, "DEFAULT");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment instanceof MoreReleaseOfInfoFormFragment
                || selectedFragment instanceof MoreAnncFormFragment) {
            showLeaveAppointmentDialog();
        } else
            super.onBackPressed();
        selectedFragment = getSupportFragmentManager().findFragmentById(R.id.more_forms_library_fragment_container);
    }

    private void showLeaveAppointmentDialog() {
        AlertDialog dialog = new CustomDialogTopBottom().getDialog(this, this, "", getString(R.string.appt_request_leave_message), getString(R.string.appt_stay_on_page), getString(R.string.appt_leave_page));
        if (!isFinishing())
            dialog.show();
    }

    private void shareRoiPdf() {
        if (selectedFragment instanceof DownloadPdfFragment)
            ((DownloadPdfFragment) selectedFragment).sharePdf();
    }

    private void openMorePdfOptions() {
        if (selectedFragment instanceof DownloadPdfFragment)
            ((DownloadPdfFragment) selectedFragment).printSavePdf();
    }

    private void submitANNC() {
        if (selectedFragment instanceof MoreAnncFormFragment) {
            MoreAnncFormFragment moreAnncFormFragment = (MoreAnncFormFragment) selectedFragment;
            if (moreAnncFormFragment.formIsValid()) {
                Log.d(TAG, "MoreReleaseOfInfoFragment formIsValid: " + moreAnncFormFragment.formIsValid());
                submitAnncForm(moreAnncFormFragment);
            } else {
                Log.d(TAG, "form is not valid");
                incompleteFormDialog(getString(R.string.new_appt_invalid_form_title), getString(R.string.new_appt_invalid_form_message));
            }
        }
    }

    private boolean phoneNoIsValid(MoreReleaseOfInfoFormFragment fragment) {

        return fragment.isPhoneNoValid();
    }

    private boolean faxNoIsValid(MoreReleaseOfInfoFormFragment fragment) {

        return fragment.isFaxNoValid();
    }

    private boolean emailIdIsValid(MoreReleaseOfInfoFormFragment fragment) {

        return fragment.isEmailIdValid();
    }

    private void submitROI() {
        if (selectedFragment instanceof MoreReleaseOfInfoFormFragment) {

            MoreReleaseOfInfoFormFragment moreReleaseOfInfoFormFragment = (MoreReleaseOfInfoFormFragment) selectedFragment;
            if (!moreReleaseOfInfoFormFragment.formIsValid() && !phoneNoIsValid(moreReleaseOfInfoFormFragment)) {
                incompleteFormDialog(getString(R.string.phone_no_invalid_error_title), getString(R.string.phone_no_invalid_error_message));
            } else if (!moreReleaseOfInfoFormFragment.formIsValid() && !faxNoIsValid(moreReleaseOfInfoFormFragment)) {
                incompleteFormDialog(getString(R.string.fax_no_invalid_error_title), getString(R.string.fax_no_invalid_error_message));
            } else if (!moreReleaseOfInfoFormFragment.formIsValid() && !emailIdIsValid(moreReleaseOfInfoFormFragment)) {
                incompleteFormDialog(getString(R.string.email_id_invalid_error_title), getString(R.string.email_id_invalid_error_message));
            } else if (moreReleaseOfInfoFormFragment.formIsValid()) {
                Log.d(TAG, "MoreReleaseOfInfoFragment formIsValid: " + moreReleaseOfInfoFormFragment.formIsValid());
                submitRoiForm(moreReleaseOfInfoFormFragment);
            } else {
                Log.d(TAG, "form is not valid");
                incompleteFormDialog(getString(R.string.new_appt_invalid_form_title), getString(R.string.new_appt_invalid_form_message));
            }
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


    private void submitRoiForm(final MoreReleaseOfInfoFormFragment fragment) {
        Log.d(TAG, "submitForm: " + fragment.getRoiObj());
        showActivityIndicator("Submitting request…");
        String url = BuildConfig.myctca_server + getString(R.string.myctca_post_roi);
        sessionFacade.submitForm(MyCTCATask.RELEASE_OF_INFORMATION, url, new Gson().toJson(fragment.getRoiObj()), this, this);
    }

    private void submitAnncForm(final MoreAnncFormFragment fragment) {
        Log.d(TAG, "submitForm: " + fragment.getAnncFormObject());
        showActivityIndicator("Submitting request…");
        String url = BuildConfig.myctca_server + getString(R.string.myctca_annc_form_submission);
        sessionFacade.submitForm(MyCTCATask.ANNC, url, new Gson().toJson(fragment.getAnncFormObject()), this, this);
    }

    // DatePickerListener
    public void onMoreDateSet(Date date, String purpose) {
        Log.d(TAG, "MoreReleaseOfInfoActivity dateSet: " + date);
        if (fragment.getClass().isAssignableFrom(MoreReleaseOfInfoFormFragment.class)) {
            MoreReleaseOfInfoFormFragment moreReleaseOfInfoFormFragment = (MoreReleaseOfInfoFormFragment) fragment;
            moreReleaseOfInfoFormFragment.setDateFromDatePicker(date, purpose);
        } else {
            MoreAnncFormFragment moreAnncFormFragment = (MoreAnncFormFragment) this.fragment;
            moreAnncFormFragment.setDateFromDatePicker(date, purpose);
        }
    }

    // Delivery Method Listener
    public void onDeliveryMethodDiscloseDone(Map<String, Boolean> deliveryMethod) {
        Log.d(TAG, "onDeliveryMethodDiscloseDone: " + deliveryMethod);

        if (selectedFragment instanceof MoreReleaseOfInfoFormFragment)
            ((MoreReleaseOfInfoFormFragment) selectedFragment).setDeliveryMethod(deliveryMethod);
    }

    public void onPurposeDiscloseDone(Map<String, Boolean> purpose) {
        Log.d(TAG, "onPurposeDiscloseDone: " + purpose);

        if (selectedFragment instanceof MoreReleaseOfInfoFormFragment)
            ((MoreReleaseOfInfoFormFragment) selectedFragment).setPurpose(purpose);
    }

    public void onGeneralDiscloseInfoDone(Map<String, Boolean> generalInfo, String generalInfoOther) {
        Log.d(TAG, "onGeneralDiscloseInfoDone: " + generalInfo);

        if (selectedFragment instanceof MoreReleaseOfInfoFormFragment)
            ((MoreReleaseOfInfoFormFragment) selectedFragment).setGeneralInfoInput(generalInfo, generalInfoOther);
    }

    public void onConfidentialDiscloseInfoDone(Map<String, Boolean> confidentialInfo) {
        Log.d(TAG, "onConfidentialDiscloseInfoDone: " + confidentialInfo);
        if (selectedFragment instanceof MoreReleaseOfInfoFormFragment)
            ((MoreReleaseOfInfoFormFragment) selectedFragment).setConfidentialInfoInputText(confidentialInfo);
    }

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    private void showRequestSuccess() {
        String title = "";
        String body = "";
        if (fragment.getClass().isAssignableFrom(MoreReleaseOfInfoFormFragment.class)) {
            title = getString(R.string.roi_request_sent_success_title);
            body = getString(R.string.roi_request_sent_success_message);
            CTCAAnalyticsManager.createEvent("MoreFormsLibraryActivity:showRequestSuccess", CTCAAnalyticsConstants.ALERT_ROI_SUBMIT_SUCCESS, null, null);
        } else {
            title = getString(R.string.annc_request_sent_success_title);
            body = getString(R.string.annc_request_sent_success_body);
            CTCAAnalyticsManager.createEvent("MoreFormsLibraryActivity:showRequestSuccess", CTCAAnalyticsConstants.ALERT_ANNC_SUBMIT_SUCCESS, null, null);
        }
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, true, title, body);
        if (!isFinishing())
            dialog.show();
    }

    private void showRequestError(String message) {
        String title = "";
        if (fragment.getClass().isAssignableFrom(MoreReleaseOfInfoFormFragment.class)) {
            title = getString(R.string.roi_request_sent_error_title);
            CTCAAnalyticsManager.createEvent("MoreFormsLibraryActivity:showRequestError", CTCAAnalyticsConstants.ALERT_ROI_SUBMIT_FAIL, null, null);
        } else {
            title = getString(R.string.annc_request_sent_error_title);
            CTCAAnalyticsManager.createEvent("MoreFormsLibraryActivity:showRequestError", CTCAAnalyticsConstants.ALERT_ANNC_SUBMIT_FAIL, null, null);
        }
        AlertDialog dialog = new CustomRequestDialog().getSuccessFailureDialog(this, false, title, message);
        if (!isFinishing())
            dialog.show();
    }

    @Override
    public void onAuthActionDone(Map<String, Boolean> authAction) {
        Log.d(TAG, "onAuthActionDone: " + authAction);
        if (selectedFragment instanceof MoreReleaseOfInfoFormFragment) {
            ((MoreReleaseOfInfoFormFragment) selectedFragment).setAuthAction(authAction);
        }
    }

    @Override
    public void notifyPostSuccess() {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        showRequestSuccess();
    }

    @Override
    public void notifyPostError(String message) {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        hideActivityIndicator();
        showRequestError(message);
    }

    @Override
    public void negativeButtonAction() {
        super.onBackPressed();
        selectedFragment = getSupportFragmentManager().findFragmentById(R.id.more_forms_library_fragment_container);
    }
}
