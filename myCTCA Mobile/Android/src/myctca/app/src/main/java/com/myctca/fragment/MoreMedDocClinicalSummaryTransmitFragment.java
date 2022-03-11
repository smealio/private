package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreMedDocClinicalSummaryActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MyCTCATask;
import com.myctca.model.TransmitClinicalSummary;
import com.myctca.service.MoreMedicalDocumentsService;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreMedDocClinicalSummaryTransmitFragment extends Fragment implements MoreMedicalDocumentsService.MoreMedDocClinicalSummaryListenerPost {
    private static final String TAG = MoreMedDocClinicalSummaryTransmitFragment.class.getSimpleName();
    ArrayList<String> selectedClinicalSummaryIds;
    private TextView transmitClinicalSummaryBody;
    private TextView transmitPasswordProtectionClinicalSummaryBody;
    private CheckBox cbPasswordProtect;
    private CheckBox cbSendSecure;
    private CheckBox cbSendNonSecure;
    private EditText etTransmitFilePassword;
    private EditText etTransmitEmail;
    private LinearLayout llTransmitFilePassword;
    private Button btnCloseTransmitClinicalSummary;
    private Button btnTransmitClinicalSummary;
    private LinearLayout llTransmitEmail;
    private TextView tvTransmitFilePassword;
    private TextView tvTransmitEmailLabel;
    private TextView transmitSecurityOptionError;
    private boolean isValid;
    private boolean barracuda;
    private boolean flag;
    private ImageButton mTogglePasswordButton;
    private boolean passwordIsSecure = false;
    private Context context;
    private SessionFacade sessionFacade;

    public static MoreMedDocClinicalSummaryTransmitFragment newInstance() {
        return new MoreMedDocClinicalSummaryTransmitFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get args
        String key = "SELECTED_CLINICAL_SUMMARY_ID";
        selectedClinicalSummaryIds = getArguments().getStringArrayList(key);

        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_med_doc_clinical_summary_transmit, container, false);
    }

    private void doTogglePassword() {
        if (this.passwordIsSecure) {
            this.passwordIsSecure = false;
            etTransmitFilePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mTogglePasswordButton.setImageResource(R.drawable.eye_slash);
        } else {
            this.passwordIsSecure = true;
            etTransmitFilePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mTogglePasswordButton.setImageResource(R.drawable.eye);
        }
        etTransmitFilePassword.setSelection(etTransmitFilePassword.getText().length());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MoreMedDocClinicalSummaryActivity) context).setToolBar(context.getString(R.string.transmit_clinical_summary_toolbar_title));
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        transmitClinicalSummaryBody = view.findViewById(R.id.transmit_clinical_summary_body);
        transmitPasswordProtectionClinicalSummaryBody = view.findViewById(R.id.transmit_password_protection_clinical_summary_body);
        cbPasswordProtect = view.findViewById(R.id.cb_transmit_pass_prot);
        cbSendSecure = view.findViewById(R.id.cb_send_secure);
        cbSendNonSecure = view.findViewById(R.id.send_non_secure);
        etTransmitFilePassword = view.findViewById(R.id.transmit_file_password_et);
        etTransmitEmail = view.findViewById(R.id.transmit_email_et);
        llTransmitFilePassword = view.findViewById(R.id.ll_transmit_file_password);

        tvTransmitFilePassword = view.findViewById(R.id.transmit_file_password_tv);
        tvTransmitEmailLabel = view.findViewById(R.id.transmit_email_label);
        llTransmitEmail = view.findViewById(R.id.ll_transmit_email);
        transmitSecurityOptionError = view.findViewById(R.id.transmit_security_option_error);

        LinearLayout bottomLayoutTransmitClinicalSummary = view.findViewById(R.id.botton_layout_transmit_clinical_summary);
        btnCloseTransmitClinicalSummary = bottomLayoutTransmitClinicalSummary.findViewById(R.id.btn_close_clinical_summary);
        btnTransmitClinicalSummary = bottomLayoutTransmitClinicalSummary.findViewById(R.id.btn_transmit_clinical_summary);

        mTogglePasswordButton = view.findViewById(R.id.toggle_transmit_clinical_summary_password_button);
        mTogglePasswordButton.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("LoginActivity:prepareView", CTCAAnalyticsConstants.ACTION_SHOW_PASSWORD_TAP, null, null);
            Log.d(TAG, "HELLO...Toggle Password");
            doTogglePassword();
        });

        barracuda = false;
        flag = false;
        transmitClinicalSummaryBody.setText(HtmlCompat.fromHtml(context.getString(R.string.transmit_clinical_summary_body), HtmlCompat.FROM_HTML_MODE_LEGACY));

        prepareView();
        handleCheckBoxes();
    }

    private void prepareView() {
        btnTransmitClinicalSummary.setVisibility(View.VISIBLE);

        llTransmitEmail.setOnClickListener(v -> {
            etTransmitEmail.setFocusableInTouchMode(true);
            etTransmitEmail.requestFocus();
        });
        etTransmitEmail.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientFirstNameInput onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus && etTransmitEmail.getText().toString().matches("")) {
                tvTransmitEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                tvTransmitEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }

        });

        llTransmitFilePassword.setOnClickListener(v -> {
            etTransmitFilePassword.setFocusableInTouchMode(true);
            etTransmitFilePassword.requestFocus();
        });
        etTransmitFilePassword.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientFirstNameInput onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                if (etTransmitFilePassword.getText().toString().matches("")) {
                    tvTransmitFilePassword.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvTransmitFilePassword.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
            }
        });

        btnTransmitClinicalSummary.setOnClickListener(view -> {
            boolean isCheckBoxSelected = isCheckBoxSelected();
            boolean isValidFilePassword = isValidFilePassword();
            boolean isValidEmail = isValidEmail();

            if (!isValidEmail) {
                incompleteFormDialog(context.getString(R.string.email_id_invalid_error_title), context.getString(R.string.email_id_invalid_error_message));
            } else if (isCheckBoxSelected && isValidFilePassword && isValidEmail) {
                transmitClinicalSummary();
            } else if (!isCheckBoxSelected && isValidFilePassword && isValidEmail) {
                if (flag)
                    transmitClinicalSummary();
                else {
                    Log.d(TAG, "transmit form is not valid");
                    flag = true;
                }
            } else {
                Log.d(TAG, "transmit form is not valid");
            }
        });

        btnCloseTransmitClinicalSummary.setOnClickListener(view -> {
            ((MoreMedDocClinicalSummaryActivity) context).onBackPressed();
        });
    }

    private void transmitClinicalSummary() {
        // Get URL
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).showActivityIndicator(context.getString(R.string.transmit_clinical_summary_indicator));

        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_clinical_summary_transmit);

        TransmitClinicalSummary transmitClinicalSummary = new TransmitClinicalSummary(selectedClinicalSummaryIds, etTransmitEmail.getText().toString(), etTransmitFilePassword.getText().toString());
        Log.d(TAG, "clinicalsummary download zip url: " + url);
        Map<String, String> params = new HashMap<>();
        params.put("barracuda", String.valueOf(barracuda));

        sessionFacade.postClinicalSummaryData(context, this, url, MyCTCATask.TRANSMIT_CLINICAL_SUMMARY, new Gson().toJson(transmitClinicalSummary), params);
    }


    private void handleCheckBoxes() {
        //handle checkboxes
        cbPasswordProtect.setOnClickListener(view -> {
            if (cbPasswordProtect.isChecked()) {
                cbSendNonSecure.setChecked(false);
                transmitPasswordProtectionClinicalSummaryBody.setVisibility(View.VISIBLE);
                llTransmitFilePassword.setVisibility(View.VISIBLE);
            } else {
                transmitPasswordProtectionClinicalSummaryBody.setVisibility(View.GONE);
                llTransmitFilePassword.setVisibility(View.GONE);
            }
        });

        cbSendSecure.setOnClickListener(view -> {
            if (cbSendSecure.isChecked()) {
                cbPasswordProtect.setChecked(true);
                cbPasswordProtect.setClickable(false);

                cbSendNonSecure.setChecked(false);
                transmitPasswordProtectionClinicalSummaryBody.setVisibility(View.VISIBLE);
                llTransmitFilePassword.setVisibility(View.VISIBLE);
                barracuda = true;
            } else {
                barracuda = false;
                cbPasswordProtect.setClickable(true);
            }
        });

        cbSendNonSecure.setOnClickListener(view -> {
            if (cbSendNonSecure.isChecked()) {
                cbPasswordProtect.setChecked(false);
                cbPasswordProtect.setClickable(true);

                cbSendSecure.setChecked(false);
                barracuda = false;

                transmitPasswordProtectionClinicalSummaryBody.setVisibility(View.GONE);
                llTransmitFilePassword.setVisibility(View.GONE);
            }
        });
    }

    private boolean isCheckBoxSelected() {
        if (cbPasswordProtect.isChecked() || cbSendNonSecure.isChecked() || cbSendSecure.isChecked()) {
            transmitSecurityOptionError.setVisibility(View.GONE);
            return true;
        } else {
            if (flag)
                return true;
            transmitSecurityOptionError.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean isValidFilePassword() {
        if (cbPasswordProtect.isChecked()) {
            if (!TextUtils.isEmpty(etTransmitFilePassword.getText().toString())) {
                isValid = true;
                tvTransmitFilePassword.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else {
                isValid = false;
                tvTransmitFilePassword.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            isValid = true;
        }
        return isValid;
    }

    private boolean isValidEmail() {
        String transmitEmail = etTransmitEmail.getText().toString();
        if (!TextUtils.isEmpty(transmitEmail) && Patterns.EMAIL_ADDRESS.matcher(transmitEmail).matches()) {
            isValid = true;
            tvTransmitEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            isValid = false;
            tvTransmitEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
        return isValid;
    }

    private void incompleteFormDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void showTransmitClinicalSummaryError(String message) {
        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryTransmitFragment:showTransmitClinicalSummaryError", CTCAAnalyticsConstants.ALERT_CLINICAL_SUMMARIES_TRANSMIT_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.transmit_clinical_summary_error_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyPostError(String message) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).hideActivityIndicator();
        showTransmitClinicalSummaryError(message);
    }

    @Override
    public void notifyPostSuccess(String text) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).hideActivityIndicator();
        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryTransmitFragment:notifyPostSuccess", CTCAAnalyticsConstants.ALERT_CLINICAL_SUMMARIES_TRANSMIT_SUCCESS, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.transmit_clinical_summary_success_title))
                .setMessage(context.getString(R.string.transmit_clinical_summary_success))
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog, which) -> {
                    dialog.cancel();
                    getFragmentManager().popBackStack();
                })
                .show();
    }
}