package com.myctca.fragment;


import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.myctca.R;
import com.myctca.activity.PrescriptionRefillActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.CareTeam;
import com.myctca.model.Prescription;
import com.myctca.model.PrescriptionRefillRequest;
import com.myctca.service.CommonService;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class PrescriptionRefillFragment extends Fragment implements CommonService.CommonServiceListener {

    private static final String TAG = "myCTCA-REFILLREQUEST";
    private static final String PURPOSE = "PRESCRIPTION_REFILL";
    private static final String CONTACT_INFO_PURPOSE = "CONTACT_INFO";
    private static final String CARE_PLAN_PURPOSE = "CARE_PLAN_NEW";
    private int mHighlightMinWidth = 0;
    private PrescriptionRefillRequest refillRequest;

    // Input Fields
    private TextView tvFromInput;
    private TextView tvToInput;
    private TextView tvSubjectInput;
    private TextView tvPrescriptionsInput;
    private EditText etPhoneInput;
    private EditText etPharmacyInput;
    private EditText etPharmacyPhoneInput;
    private EditText etCommentsInput;
    // Labels
    private TextView tvPhoneLabel;
    private TextView tvPharmacyLabel;
    private TextView tvPharmacyPhoneLabel;
    private TextView tvCommentsLabel;
    private TextView tvprescriptionRefillToLabel;
    // Separators and Highlights
    private View viewPhoneSeparator;
    private View viewPhoneHighlight;
    private View viewPharmacySeparator;
    private View viewPharmacyHighlight;
    private View viewPharmacyPhoneSeparator;
    private View viewPharmacyPhoneHighlight;
    private LinearLayout llCareTeams;
    private List<String> prescriptionCareTeamsSelected = new ArrayList<>();
    private boolean phoneNoValid;
    private boolean pharmacyPhoneNoValid;
    private SessionFacade sessionFacade;
    private Context context;
    private PhoneNumberFormattingTextWatcher watcher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prescription_refill, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        watcher = new PhoneNumberFormattingTextWatcher("US");
        sessionFacade = new SessionFacade();
        refillRequest = new PrescriptionRefillRequest();
        view.findViewById(R.id.prescription_refill_scroll_view).requestFocus();
        tvFromInput = view.findViewById(R.id.prescription_refill_from_input);
        tvToInput = view.findViewById(R.id.prescription_refill_to_input);
        tvSubjectInput = view.findViewById(R.id.prescription_refill_subject_input);
        tvPrescriptionsInput = view.findViewById(R.id.prescription_refill_prescriptions_input);
        etPhoneInput = view.findViewById(R.id.prescription_refill_phone_input);
        etPharmacyInput = view.findViewById(R.id.prescription_refill_pharmacy_input);
        etPharmacyPhoneInput = view.findViewById(R.id.prescription_refill_pharmacy_phone_input);
        etCommentsInput = view.findViewById(R.id.prescription_refill_comments_input);

        tvprescriptionRefillToLabel = view.findViewById(R.id.prescription_refill_to_label);
        tvPhoneLabel = view.findViewById(R.id.prescription_refill_phone_label);
        tvPharmacyLabel = view.findViewById(R.id.prescription_refill_pharmacy_label);
        tvPharmacyPhoneLabel = view.findViewById(R.id.prescription_refill_pharmacy_phone_label);
        tvCommentsLabel = view.findViewById(R.id.prescription_refill_comments_label);

        viewPhoneSeparator = view.findViewById(R.id.prescription_refill_phone_separator);
        viewPhoneHighlight = view.findViewById(R.id.prescription_refill_phone_highlight);
        viewPharmacySeparator = view.findViewById(R.id.prescription_refill_pharmacy_separator);
        viewPharmacyHighlight = view.findViewById(R.id.prescription_refill_pharmacy_highlight);
        viewPharmacyPhoneSeparator = view.findViewById(R.id.prescription_refill_pharmacy_phone_separator);
        viewPharmacyPhoneHighlight = view.findViewById(R.id.prescription_refill_pharmacy_phone_highlight);

        llCareTeams = view.findViewById(R.id.ll_care_teams);

        sessionFacade.downloadContactInfo(this, context, CONTACT_INFO_PURPOSE);
        prepareView();
        downloadCareTeam();
    }

    public void prepareView() {
        tvFromInput.setText(sessionFacade.getMyCtcaUserProfile().getFullName());

        llCareTeams.setFocusableInTouchMode(true);
        llCareTeams.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click To Care Teams");
            showCareTeams();
        });
        llCareTeams.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });

        tvSubjectInput.setText(R.string.prescription_refill_subject_input);

        PrescriptionRefillActivity activity = (PrescriptionRefillActivity) context;
        List<Prescription> prescriptions = activity.prescriptions;
        String prescriptionNames = "";
        for (Prescription prescription : prescriptions) {
            if (prescriptionNames.equals("")) {
                prescriptionNames = (new StringBuilder(prescriptionNames)).append(prescription.getDrugName()).toString();
            } else {
                String commaConcat = ", " + prescription.getDrugName();
                prescriptionNames = (new StringBuilder(prescriptionNames)).append(commaConcat).toString();
            }
        }
        tvPrescriptionsInput.setText(prescriptionNames);
        // Input Fields
        tvPhoneLabel.setOnClickListener(v -> etPhoneInput.setFocusableInTouchMode(true));
        etPhoneInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                etPhoneInput.post(() -> etPhoneInput.setSelection(etPhoneInput.getText().length()));
                animateHighlightIn(viewPhoneHighlight, viewPhoneSeparator.getWidth());
            } else {
                changePriorityOfField(etPhoneInput.getText().toString(), tvPhoneLabel);
                animateHighlightOut(viewPhoneHighlight, viewPhoneSeparator.getWidth());
            }
        });
        etPhoneInput.setOnClickListener(v -> etPhoneInput.setSelection(etPhoneInput.getText().length()));
        etPhoneInput.setLongClickable(false);
        etPhoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1 && Character.isDigit(charSequence.charAt(0))) {
                    setPhoneEditTextLength(14, etPhoneInput);
                } else if (charSequence.length() > 0 && charSequence.toString().charAt(0) == '+') {
                    int spaces = charSequence.toString().replaceAll("[^ ]", "").length();
                    setPhoneEditTextLength(16 + spaces, etPhoneInput);
                }
                PhoneNumberUtils.formatNumber(charSequence.toString(), "US");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        tvPharmacyLabel.setOnClickListener(v -> etPharmacyInput.setFocusableInTouchMode(true));
        etPharmacyInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewPharmacyHighlight, viewPharmacyPhoneSeparator.getWidth());
            } else {
                changePriorityOfField(etPharmacyInput.getText().toString(), tvPharmacyLabel);
                animateHighlightOut(viewPharmacyHighlight, viewPharmacyPhoneSeparator.getWidth());
            }
        });

        tvPharmacyPhoneLabel.setOnClickListener(v -> etPharmacyPhoneInput.setFocusableInTouchMode(true));

        etPharmacyPhoneInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewPharmacyPhoneHighlight, viewPharmacySeparator.getWidth());
            } else {
                changePriorityOfField(etPharmacyPhoneInput.getText().toString(), tvPharmacyPhoneLabel);
                animateHighlightOut(viewPharmacyPhoneHighlight, viewPharmacySeparator.getWidth());
            }
        });
        etPharmacyPhoneInput.setOnClickListener(v -> etPharmacyPhoneInput.setSelection(etPharmacyPhoneInput.getText().length()));
        etPharmacyPhoneInput.setLongClickable(false);
        etPharmacyPhoneInput.addTextChangedListener(watcher);
        etPharmacyPhoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1 && Character.isDigit(charSequence.charAt(0))) {
                    setPhoneEditTextLength(14, etPharmacyPhoneInput);
                } else if (charSequence.length() > 0 && charSequence.toString().charAt(0) == '+') {
                    int spaces = charSequence.toString().replaceAll("[^ ]", "").length();
                    setPhoneEditTextLength(16 + spaces, etPharmacyPhoneInput);
                }
                PhoneNumberUtils.formatNumber(charSequence.toString(), "US");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        tvCommentsLabel.setOnClickListener(v -> {
            etCommentsInput.setFocusableInTouchMode(true);
            etCommentsInput.requestFocus();
        });
    }

    public PrescriptionRefillRequest getRefillRequest() {
        return refillRequest;
    }

    public void showCareTeams() {
        dismissKeyboard();
        PrescriptionRefillActivity activity = (PrescriptionRefillActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        CareTeamsDialogFragment dialog = CareTeamsDialogFragment.newInstance(activity);
        dialog.show(manager, "");
        dialog.setPreviouslySelected(prescriptionCareTeamsSelected);
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && ((PrescriptionRefillActivity) context).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((PrescriptionRefillActivity) context).getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void setCareTeamsInputText(Map<String, Boolean> selectedCareTeams, List<CareTeam> careTeamsDetails) {
        String careTeamsText = "";
        prescriptionCareTeamsSelected.clear();
        for (Map.Entry<String, Boolean> entry : selectedCareTeams.entrySet()) {
            if (entry.getValue()) {
                // Build String for display
                if (careTeamsText.equals("")) {
                    careTeamsText = entry.getKey();
                } else {
                    careTeamsText = (new StringBuilder(careTeamsText)).append(", ").append(entry.getKey()).toString();
                }
                // Build array for ROI
                prescriptionCareTeamsSelected.add(entry.getKey());
            }
        }
        refillRequest.to = careTeamsDetails;
        tvToInput.setText(careTeamsText);
    }

    private void changePriorityOfField(String inputFieldText, TextView label) {
        if (inputFieldText.isEmpty() || inputFieldText.equals("")) {
            label.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            label.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
    }

    private void downloadCareTeam() {
        if (context != null)
            ((PrescriptionRefillActivity) context).showActivityIndicator("Retrieving Care Team data…");
        sessionFacade.downloadCareTeams(this, context, CARE_PLAN_PURPOSE);
    }

    public boolean formIsValid() {

        boolean validForm = true;

        String toInputText = tvToInput.getText().toString();
        if (TextUtils.isEmpty(toInputText)) {
            validForm = false;
            changePriorityOfField(toInputText, tvprescriptionRefillToLabel);
        } else {
            changePriorityOfField(toInputText, tvprescriptionRefillToLabel);
        }

        String prescriptionPhone = etPhoneInput.getText().toString();
        if (!TextUtils.isEmpty(prescriptionPhone)) {
            String phone = prescriptionPhone.replaceAll("[^0-9]", "");
            if (phone.length() >= 10) {
                phoneNoValid = true;
                tvPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                validForm = false;
                phoneNoValid = false;
                tvPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            validForm = false;
            phoneNoValid = true;
            tvPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        String pharmacy = etPharmacyInput.getText().toString();
        if (TextUtils.isEmpty(pharmacy)) {
            validForm = false;
            changePriorityOfField(pharmacy, tvPharmacyLabel);
        } else {
            changePriorityOfField(pharmacy, tvPharmacyLabel);
        }

        String pharmacyPhone = etPharmacyPhoneInput.getText().toString();
        if (!TextUtils.isEmpty(pharmacyPhone)) {
            String phone = pharmacyPhone.replaceAll("[^0-9]", "");
            if (phone.length() >= 10) {
                pharmacyPhoneNoValid = true;
                tvPharmacyPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                validForm = false;
                pharmacyPhoneNoValid = false;
                tvPharmacyPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            validForm = false;
            pharmacyPhoneNoValid = true;
            tvPharmacyPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        if (validForm) {

            // selectedPrescriptions
            PrescriptionRefillActivity activity = (PrescriptionRefillActivity) context;
            List<Prescription> prescriptions = activity.prescriptions;
            List<String> prescriptionIds = new ArrayList<>();
            for (Prescription prescription : prescriptions) {
                prescriptionIds.add(prescription.getPrescriptionId());
            }
            refillRequest.selectedPrescriptions = prescriptionIds;
            refillRequest.patientPhone = prescriptionPhone;
            refillRequest.pharmacyName = pharmacy;
            refillRequest.pharmacyPhone = pharmacyPhone;

            String refillComments = etCommentsInput.getText().toString();
            if (!refillComments.isEmpty() || !refillComments.equals("")) {
                refillRequest.comments = refillComments;
            }
        }

        return validForm;
    }

    public boolean isPhoneNoValid() {
        return phoneNoValid;
    }

    public boolean isPharmacyPhoneNoValid() {
        return pharmacyPhoneNoValid;
    }

    private void animateHighlightIn(final View view, final int maxWidth) {
        view.getLayoutParams().width = mHighlightMinWidth;
        view.setVisibility(View.VISIBLE);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(mHighlightMinWidth, maxWidth);
        int mDuration = 300; //in millis
        widthAnimator.setDuration(mDuration);
        widthAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().width = (int) animation.getAnimatedValue();
            view.requestLayout();
            if (view.getWidth() == maxWidth) {
                view.setVisibility(View.VISIBLE);
            }
        });
        widthAnimator.start();
    }

    private void animateHighlightOut(final View view, int maxWidth) {
        ValueAnimator widthAnimator = ValueAnimator.ofInt(maxWidth, mHighlightMinWidth);
        int mDuration = 100; //in millis
        widthAnimator.setDuration(mDuration);
        widthAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().width = (int) animation.getAnimatedValue();
            if (view.getWidth() == mHighlightMinWidth) {
                view.setVisibility(View.GONE);
            }
        });
        widthAnimator.start();
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("PrescriptionRefillFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_PRESCRIPTION_RENEWAL_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    private void setPhoneEditTextLength(int length, EditText editText) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(filterArray);
    }

    @Override
    public void notifyFetchSuccess(String purpose) {
        if (context != null)
            ((PrescriptionRefillActivity) context).hideActivityIndicator();
        if (purpose.equals(CONTACT_INFO_PURPOSE)) {
            etPhoneInput.addTextChangedListener(watcher);
            etPhoneInput.setText(sessionFacade.getUserContactNumber());
            if (sessionFacade.getUserContactNumber().length() == 10)
                setPhoneEditTextLength(14, etPhoneInput);
        }
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        if (context != null)
            ((PrescriptionRefillActivity) context).hideActivityIndicator();
        showRequestFailure(error);
    }
}
