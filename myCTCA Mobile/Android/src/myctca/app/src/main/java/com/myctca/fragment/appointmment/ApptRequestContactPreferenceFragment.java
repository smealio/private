package com.myctca.fragment.appointmment;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.myctca.R;
import com.myctca.activity.AppointmentRequestActivity;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.AppointmentRequestData;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ApptRequestContactPreferenceFragment extends BaseAppointmentFragment {

    private static final int CALL = 1;
    private static final int EMAIL = 2;
    private ToggleButton tbCallMe;
    private LinearLayout llEditContactNumber;
    private EditText etCallMeNumber;
    private TextView tvCallMeNumber;
    private Button btnSaveContact;
    private Context context;
    private CheckBox cbCallMe;
    private Button btnSaveEmailId;
    private EditText etEmailMeId;
    private ToggleButton tbEmailMe;
    private LinearLayout llEditEmailId;
    private TextView tvEmailMeId;
    private CheckBox cbEmailMe;
    private TextView tvCallMePhoneError;
    private TextView tvEmailMeIdError;
    private TextView tvCallMeTitle;
    private TextView tvEmailMeTitle;
    private AppointmentRequestData appointmentRequestData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appt_request_contact_preference, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("ApptRequestContactPreferenceFragment:onViewCreated", ((AppointmentRequestActivity) context).getViewPageType() + CTCAAnalyticsConstants.PAGE_APPT_COMM_PREF_VIEW));
        appointmentRequestData = ((AppointmentRequestActivity) context).getAppointmentRequestData();

        cbCallMe = view.findViewById(R.id.cbCallMe);
        tbCallMe = view.findViewById(R.id.tbCallMe);
        llEditContactNumber = view.findViewById(R.id.llEditContactNumber);
        tvCallMeNumber = view.findViewById(R.id.tvCallMeNumber);
        etCallMeNumber = view.findViewById(R.id.etCallMeNumber);
        btnSaveContact = view.findViewById(R.id.btnSaveContact);
        tvCallMePhoneError = view.findViewById(R.id.tvCallMePhoneError);
        tvCallMeTitle = view.findViewById(R.id.tvCallMeTitle);


        cbEmailMe = view.findViewById(R.id.cbEmailMe);
        tbEmailMe = view.findViewById(R.id.tbEmailMe);
        llEditEmailId = view.findViewById(R.id.llEditEmailId);
        tvEmailMeId = view.findViewById(R.id.tvEmailMeId);
        etEmailMeId = view.findViewById(R.id.etEmailMeId);
        btnSaveEmailId = view.findViewById(R.id.btnSaveEmailId);
        tvEmailMeIdError = view.findViewById(R.id.tvEmailMeIdError);
        tvEmailMeTitle = view.findViewById(R.id.tvEmailMeTitle);

        setOnClickListeners();
        prepareView();
        setDefaultValues();
    }

    private void setDefaultValues() {
        //set the values if stored on object in activity
        etCallMeNumber.setText(appointmentRequestData.getPhoneNumber());
        tvCallMeNumber.setText(etCallMeNumber.getText());

        if (appointmentRequestData.getPhoneNumber().length() == 10)
            setPhoneEditTextLength(14);
        else if (appointmentRequestData.getPhoneNumber().isEmpty()) {
            etCallMeNumber.setHint("(___) ___-____");
            tbCallMe.performClick();
        }

        etEmailMeId.setText(appointmentRequestData.getEmail());
        tvEmailMeId.setText(etEmailMeId.getText());

        if (appointmentRequestData.getEmail().isEmpty()) {
            tbEmailMe.performClick();
        }

        //if any option was selected by user previously, check the button as soon as screen opens
        if (!TextUtils.isEmpty(appointmentRequestData.getCommunicationPreference())) {
            if (appointmentRequestData.getCommunicationPreference().equals(AppointmentRequest.CONTACT_PREFERENCE_CALL)) {
                cbCallMe.setChecked(true);
            } else {
                cbEmailMe.setChecked(true);
            }
        }
    }

    private void prepareView() {
        //call section
        etCallMeNumber.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                //do not allow user to edit the number from between.
                etCallMeNumber.post(() -> etCallMeNumber.setSelection(etCallMeNumber.getText().length()));
            }
        });
        etCallMeNumber.setOnClickListener(v -> etCallMeNumber.setSelection(etCallMeNumber.getText().length()));
        etCallMeNumber.setLongClickable(false);
        //text watcher to format the number to US number
        etCallMeNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher("US"));
        //other text watcher for validations -
        etCallMeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1 && Character.isDigit(charSequence.charAt(0))) {
                    setPhoneEditTextLength(14);
                } else if (charSequence.length() > 0 && charSequence.toString().charAt(0) == '+') {
                    int spaces = charSequence.toString().replaceAll("[^ ]", "").length();
                    setPhoneEditTextLength(16 + spaces);
                }

                String str = charSequence.toString().replaceAll("[^0-9]", "");
                if (str.length() < 10)
                    cbCallMe.setChecked(false);
                //format everytime phone number is edited
                PhoneNumberUtils.formatNumber(charSequence.toString(), "US");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        etEmailMeId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isValidEmail())
                    cbEmailMe.setChecked(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setPhoneEditTextLength(int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        etCallMeNumber.setFilters(filterArray);
    }

    private void setOnClickListeners() {
        //handles the edit section for email and call
        handleEditSection(tbCallMe, llEditContactNumber, tvCallMeNumber, etCallMeNumber, tvCallMePhoneError);
        handleEditSection(tbEmailMe, llEditEmailId, tvEmailMeId, etEmailMeId, tvEmailMeIdError);

        //handles the save button clicks for email and call
        handleSaveButtonClickListener(btnSaveContact, tvCallMeNumber, etCallMeNumber, tbCallMe, tvCallMePhoneError, CALL);
        handleSaveButtonClickListener(btnSaveEmailId, tvEmailMeId, etEmailMeId, tbEmailMe, tvEmailMeIdError, EMAIL);

        //handles the check box clicks for email and call
        handleCheckboxCheckListener(cbCallMe, tvCallMeNumber, tvCallMeTitle, cbEmailMe, llEditContactNumber, tbCallMe, tvCallMePhoneError, etCallMeNumber);
        handleCheckboxCheckListener(cbEmailMe, tvEmailMeId, tvEmailMeTitle, cbCallMe, llEditEmailId, tbEmailMe, tvEmailMeIdError, etEmailMeId);
    }

    private void handleCheckboxCheckListener(CheckBox cb1, TextView tv, TextView tvTitle, CheckBox cb2, LinearLayout ll, ToggleButton tb, TextView error, EditText et) {
        cb1.setOnCheckedChangeListener((compoundButton, b) -> {
            if (tv.getText().toString().isEmpty()) {
                //do not allow user to check if value(phone or email) is empty
                cb1.setChecked(false);
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
                tv.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
            } else if (b) {
                // uncheck if other checkbox is enabled. as only one can be checked at a time
                if (cb2.isChecked()) {
                    cb2.setChecked(false);
                }
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.purple_bright));
                tv.setTextColor(ContextCompat.getColor(context, R.color.purple_bright));
                if (ll.getVisibility() == View.VISIBLE) {
                    tb.performClick();
                    error.setVisibility(View.GONE);
                    et.setBackground(ContextCompat.getDrawable(context, R.drawable.select_caregiver_access_patient_border));
                }
            } else {
                //when unchecked
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
                tv.setTextColor(ContextCompat.getColor(context, R.color.gray_150));
            }
            enableNextButton();
        });

    }

    private void handleSaveButtonClickListener(Button btn, TextView tv, EditText et, ToggleButton tb, TextView error, int type) {
        //handle validations
        btn.setOnClickListener(view -> {
            if (type == CALL ? isPhoneValid() : isValidEmail()) {
                tv.setText(et.getText());
                tb.performClick();
                et.setBackground(ContextCompat.getDrawable(context, R.drawable.select_caregiver_access_patient_border));
                error.setVisibility(View.GONE);
                enableNextButton();
            } else {
                String phone = et.getText().toString();
                if (phone.isEmpty()) {
                    error.setText(type == CALL ? context.getString(R.string.appt_contact_pref_empty_phone) : context.getString(R.string.appt_contact_pref_empty_email));
                } else {
                    error.setText(type == CALL ? context.getString(R.string.appt_contact_pref_incorrect_phone) : context.getString(R.string.appt_contact_pref_incorrect_email));
                }
                et.setBackground(ContextCompat.getDrawable(context, R.drawable.red_corner_stroke));
                error.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleEditSection(ToggleButton tb, LinearLayout ll, TextView tv, EditText et, TextView error) {
        //set visibilities when edit view is expanded or collapsed using toggle button
        tb.setOnClickListener(view -> {
            if (ll.getVisibility() == View.GONE) {
                et.setText(tv.getText());
                et.requestFocus();
                tv.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
            } else {
                dismissKeyboard();
                ll.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                et.setBackground(ContextCompat.getDrawable(context, R.drawable.select_caregiver_access_patient_border));
            }
        });
    }

    public void dismissKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

        View focusedView = ((AppointmentRequestActivity) context).getCurrentFocus();
        if (focusedView != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    private boolean isPhoneValid() {
        String phone = etCallMeNumber.getText().toString();
        int digits = phone.replaceAll("[^0-9]", "").length();
        return digits >= 10;
    }

    public boolean isValidEmail() {
        return (!TextUtils.isEmpty(etEmailMeId.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(etEmailMeId.getText().toString()).matches());
    }


    public void enableNextButton() {
        ((AppointmentRequestActivity) context).enableNextButton(cbEmailMe.isChecked() || cbCallMe.isChecked());
    }

    //called when next button is clicked from activity
    @Override
    public void saveAppointmentData() {
        super.saveAppointmentData();
        if (cbEmailMe.isChecked()) {
            appointmentRequestData.setCommunicationPreference(AppointmentRequest.CONTACT_PREFERENCE_EMAIL);
        } else if (cbCallMe.isChecked()) {
            appointmentRequestData.setCommunicationPreference(AppointmentRequest.CONTACT_PREFERENCE_CALL);
        }
        if(llEditContactNumber.getVisibility() == View.VISIBLE){
            tbCallMe.performClick();
        }
        if(llEditEmailId.getVisibility() == View.VISIBLE){
            tbEmailMe.performClick();
        }
        appointmentRequestData.setEmail(tvEmailMeId.getText().toString());
        appointmentRequestData.setPhoneNumber(tvCallMeNumber.getText().toString());
    }
}