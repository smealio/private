package com.myctca.fragment;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.SendMessageActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Facility;
import com.myctca.model.FacilityInfoAll;
import com.myctca.model.MedicalCenter;
import com.myctca.model.SendMessage;
import com.myctca.service.CommonService;
import com.myctca.service.FacilityInfoAllService;
import com.myctca.service.SessionFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class SendMessageFragment extends Fragment implements FacilityInfoAllService.FacilityInfoAllListener, CommonService.CommonServiceListener {
    private static final String TAG = SendMessageFragment.class.getSimpleName();
    private static final int ANIMATE_MIN_WIDTH = 0;
    private static final String PURPOSE = "SEND_MESSAGE";
    private static final String CONTACT_INFO_PURPOSE = "CONTACT_INFO";

    // Input Fields
    private EditText etFromNameInput;
    private EditText etFromEmailInput;
    private EditText etSubjectInput;
    private EditText etPhoneInput;
    private Spinner spConcernInput;
    private Spinner spFacilityInput;
    private EditText etMessageInput;
    // Labels
    private TextView tvFromNameLabel;
    private TextView tvFromEmailLabel;
    private TextView tvSubjectLabel;
    private TextView tvPhoneLabel;
    private TextView tvConcernLabel;
    private TextView tvFacilityLabel;
    private TextView tvMessageLabel;
    // Separators and Highlights
    private View viewFromNameSeparator;
    private View viewFromNameHighlight;
    private View viewFromEmailSeparator;
    private View viewFromEmailHighlight;
    private View viewSubjectSeparator;
    private View viewSubjectHighlight;
    private View viewPhoneSeparator;
    private View viewPhoneHighlight;
    private View viewAreaOfConcernSeparator;
    private View viewAreaOfConcernHighlight;
    private View viewTreatmentFacilitySeparator;
    private View viewTreatmentFacilityHighlight;
    private int selection = 0;
    private int mSelectedFacilityIndex = -1;
    private int mSelectedOtherIndex = -1;
    private boolean showPhoneNo;
    private boolean showEmailId;
    private ArrayAdapter<String> concernsAdapter;
    private ArrayAdapter<String> facilityAdapter;
    private SendMessage sendMessage;
    private SessionFacade sessionFacade;
    private Context context;
    private PhoneNumberFormattingTextWatcher watcher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Make it scrollable when keyboard is out
        ((SendMessageActivity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        sendMessage = new SendMessage();
        sessionFacade = new SessionFacade();
        watcher = new PhoneNumberFormattingTextWatcher("US");

        // Get View Components
        // Input Fields
        etFromNameInput = view.findViewById(R.id.fromNameET);
        etFromEmailInput = view.findViewById(R.id.fromEmailET);
        etSubjectInput = view.findViewById(R.id.subjectET);
        etPhoneInput = view.findViewById(R.id.phoneET);
        spConcernInput = view.findViewById(R.id.areaOfConcernSpinner);
        spFacilityInput = view.findViewById(R.id.treatmentFacilitySpinner);
        etMessageInput = view.findViewById(R.id.messageET);
        // Labels
        tvFromNameLabel = view.findViewById(R.id.fromNameLabel);
        tvFromEmailLabel = view.findViewById(R.id.fromEmailLabel);
        tvSubjectLabel = view.findViewById(R.id.subjectLabel);
        tvPhoneLabel = view.findViewById(R.id.phoneLabel);
        tvConcernLabel = view.findViewById(R.id.concernLabel);
        tvFacilityLabel = view.findViewById(R.id.facilityLabel);
        tvMessageLabel = view.findViewById(R.id.message_label);
        // Separators and Highlights
        viewFromNameSeparator = view.findViewById(R.id.from_name_separator);
        viewFromNameHighlight = view.findViewById(R.id.from_name_highlight);
        viewFromEmailSeparator = view.findViewById(R.id.from_email_separator);
        viewFromEmailHighlight = view.findViewById(R.id.from_email_highlight);
        viewSubjectSeparator = view.findViewById(R.id.subject_separator);
        viewSubjectHighlight = view.findViewById(R.id.subject_highlight);
        viewPhoneSeparator = view.findViewById(R.id.phone_separator);
        viewPhoneHighlight = view.findViewById(R.id.phone_highlight);
        viewAreaOfConcernSeparator = view.findViewById(R.id.area_of_concern_separator);
        viewAreaOfConcernHighlight = view.findViewById(R.id.area_of_concern_highlight);
        viewTreatmentFacilitySeparator = view.findViewById(R.id.treatment_facility_separator);
        viewTreatmentFacilityHighlight = view.findViewById(R.id.treatment_facility_highlight);
        view.findViewById(R.id.sendMessageScrollView).requestFocus();
        prepareView();
        downloadFacilityDetails();
        if (AppSessionManager.getInstance().isSuccessfullyLoggedIn())
            sessionFacade.downloadContactInfo(this, context, CONTACT_INFO_PURPOSE);
    }

    private void downloadFacilityDetails() {
        ((SendMessageActivity) context).showActivityIndicator(context.getString(R.string.please_wait_loading));
        sessionFacade.downloadAllFacilityInfo(context, this, PURPOSE);
    }

    public SendMessage getSendMessageData() {
        return sendMessage;
    }

    private void prepareView() {
        tvFromNameLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click DOB");
            etFromNameInput.setFocusableInTouchMode(true);
            etFromNameInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etFromNameInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        etFromNameInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etFromNameInput onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewFromNameHighlight, viewFromNameSeparator.getWidth());
            } else {
                changePriorityOfField(tvFromNameLabel, etFromNameInput.getText().toString());
                animateHighlightOut(viewFromNameHighlight, viewFromNameSeparator.getWidth());
            }
        });
        tvFromEmailLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click DOB");
            etFromEmailInput.setFocusableInTouchMode(true);
            etFromEmailInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etFromEmailInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        etFromEmailInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etFromNameInput onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewFromEmailHighlight, viewFromEmailSeparator.getWidth());
            } else {
                String fromEmail = etFromEmailInput.getText().toString();
                if (TextUtils.isEmpty(fromEmail) || !isValidEmail(fromEmail)) {
                    tvFromEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    changePriorityOfField(tvFromEmailLabel, fromEmail);
                }
                animateHighlightOut(viewFromEmailHighlight, viewFromEmailSeparator.getWidth());
            }
        });
        tvSubjectLabel.setOnClickListener(v -> {
            etSubjectInput.setFocusableInTouchMode(true);
            etSubjectInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSubjectInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        etSubjectInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewSubjectHighlight, viewSubjectSeparator.getWidth());
            } else {
                changePriorityOfField(tvSubjectLabel, etSubjectInput.getText().toString());
                animateHighlightOut(viewSubjectHighlight, viewSubjectSeparator.getWidth());
            }
        });
        tvPhoneLabel.setOnClickListener(v -> {
            etPhoneInput.setFocusableInTouchMode(true);
            etPhoneInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etPhoneInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        etPhoneInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                etPhoneInput.post(() -> etPhoneInput.setSelection(etPhoneInput.getText().length()));
                animateHighlightIn(viewPhoneHighlight, viewPhoneSeparator.getWidth());
            } else {
                animateHighlightOut(viewPhoneHighlight, viewPhoneSeparator.getWidth());
            }
        });

        etPhoneInput.setOnClickListener(v -> etPhoneInput.setSelection(etPhoneInput.getText().length()));
        etPhoneInput.setLongClickable(false);
        etPhoneInput.addTextChangedListener(watcher);
        etPhoneInput.addTextChangedListener(new TextWatcher() {
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
                PhoneNumberUtils.formatNumber(charSequence.toString(), "US");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //set adapter for concern dropdown spinner
        List<String> concerns = new ArrayList<>(Arrays.asList("Billing",
                "Care Management",
                "Medical Records",
                "Registration",
                "Scheduling",
                "Technical Issue",
                "Other"));


        concernsAdapter = new ArrayAdapter<String>(context, R.layout.spinner_view_layout) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.spinner_dialog_root)).setText("");
                    ((TextView) v.findViewById(R.id.spinner_dialog_root)).setHint(getItem(getCount()));
                    v.findViewById(R.id.spinner_dialog_root).setPadding(0, 8, 8, 8);
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = super.getDropDownView(position, convertView, parent);

                if (position == mSelectedOtherIndex) {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.viewLightGrey));
                } else {
                    itemView.setBackgroundColor(Color.TRANSPARENT);
                }

                return itemView;
            }

        };

        concernsAdapter.setDropDownViewResource(R.layout.spinner_view_layout);
        for (String concern : concerns) {
            concernsAdapter.add(concern);
        }

        concernsAdapter.add(context.getString(R.string.input_text_placeholder));

        spConcernInput.setAdapter(concernsAdapter);
        spConcernInput.setSelection(concernsAdapter.getCount());
        spConcernInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedOtherIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        // Hide Keyboard if Spinners are tapped
        tvConcernLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click tvMedicalCenterLabel");
            spConcernInput.requestFocus();
            spConcernInput.performClick();
            dismissKeyboard();
        });
        spConcernInput.setFocusableInTouchMode(true);
        spConcernInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "spMedicalCenter onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewAreaOfConcernHighlight, viewAreaOfConcernSeparator.getWidth());
                spConcernInput.performClick();
            } else {
                changePriorityOfField(tvConcernLabel, spConcernInput.getSelectedItem().toString());
                animateHighlightOut(viewAreaOfConcernHighlight, viewAreaOfConcernSeparator.getWidth());
            }
        });
        spConcernInput.setOnTouchListener((v, event) -> {
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return false;
        });
        tvFacilityLabel.setOnClickListener(v -> {
            spFacilityInput.requestFocus();
            spFacilityInput.performClick();
            dismissKeyboard();
        });
        spFacilityInput.setFocusableInTouchMode(true);
        spFacilityInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "spMedicalCenter onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewTreatmentFacilityHighlight, viewTreatmentFacilitySeparator.getWidth());
                spFacilityInput.performClick();
            } else {
                changePriorityOfField(tvFacilityLabel, spFacilityInput.getSelectedItem().toString());
                animateHighlightOut(viewTreatmentFacilityHighlight, viewTreatmentFacilitySeparator.getWidth());
            }
        });
        spFacilityInput.setOnTouchListener((v, event) -> {
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return false;
        });

        //message
        tvMessageLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click tvMessageLabel");
            etMessageInput.requestFocus();
            etMessageInput.performClick();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etMessageInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        etMessageInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                changePriorityOfField(tvMessageLabel, etMessageInput.getText().toString());
            }
        });

        if (sessionFacade.getMyCtcaUserProfile() != null) {
            etFromNameInput.setText(sessionFacade.getMyCtcaUserProfile().getFullName());
            etFromEmailInput.setText(sessionFacade.getMyCtcaUserProfile().getEmailAddress());
        }
    }

    private void changePriorityOfField(TextView inputFieldLabel, String inputFieldText) {
        if (TextUtils.isEmpty(inputFieldText)) {
            inputFieldLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            inputFieldLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
    }

    public boolean formIsValid() {

        boolean validForm = true;

        String fromName = etFromNameInput.getText().toString();
        if (TextUtils.isEmpty(fromName)) {
            validForm = false;
            changePriorityOfField(tvFromNameLabel, fromName);
        } else {
            changePriorityOfField(tvFromNameLabel, fromName);

        }
        String fromEmail = etFromEmailInput.getText().toString();
        if (TextUtils.isEmpty(fromEmail)) {
            validForm = false;
            showEmailId = true;
            tvFromEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (!isValidEmail(fromEmail)) {
            validForm = false;
            showEmailId = false;
            tvFromEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            showEmailId = true;
            changePriorityOfField(tvFromEmailLabel, fromEmail);
        }
        String subject = etSubjectInput.getText().toString();
        if (TextUtils.isEmpty(subject)) {
            validForm = false;
            changePriorityOfField(tvSubjectLabel, subject);
        } else {
            changePriorityOfField(tvSubjectLabel, subject);
        }
        // Phone is not required
        String phone = etPhoneInput.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            phone = phone.replaceAll("[^0-9]", "");
            if (phone.length() >= 10) {
                showPhoneNo = true;
                tvPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                validForm = false;
                showPhoneNo = false;
                tvPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            showPhoneNo = true;
            tvPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }

        String concern = "";
        Log.d(TAG, "Area of Concern: " + concern + "::: itemId: " + spConcernInput.getSelectedItemId());
        if (spConcernInput.getSelectedItemId() == concernsAdapter.getCount()) {
            validForm = false;
            changePriorityOfField(tvConcernLabel, concern);
        } else {
            concern = spConcernInput.getSelectedItem().toString();
            changePriorityOfField(tvConcernLabel, concern);
        }
        String facility = "";
        if (spFacilityInput.getSelectedItemId() == facilityAdapter.getCount()) {
            validForm = false;
            changePriorityOfField(tvFacilityLabel, facility);
        } else {
            if (spFacilityInput.getSelectedItem() != null) {
                facility = spFacilityInput.getSelectedItem().toString();
            }
            changePriorityOfField(tvFacilityLabel, facility);
        }
        String message = etMessageInput.getText().toString();
        if (TextUtils.isEmpty(message)) {
            validForm = false;
            changePriorityOfField(tvMessageLabel, message);
        } else {
            changePriorityOfField(tvMessageLabel, message);
        }
        if (validForm) {
            sendMessage.setUserName(fromName);
            sendMessage.setEmailAddress(fromEmail);
            sendMessage.setSubject(subject);
            sendMessage.setPhoneNumber(phone);
            sendMessage.setAreaOfConcern(concern);
            sendMessage.setFacility(facility);
            sendMessage.setComments(message);
        }

        return validForm;
    }

    public boolean showPhoneNoInvalidDialog() {
        return showPhoneNo;
    }

    public boolean showEmailIdInvalidDialog() {
        return showEmailId;
    }

    private void dismissKeyboard() {
        Log.d(TAG, "DISMISS KEYBOARD");
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        // verify if the soft keyboard is open
        if (imm != null && ((SendMessageActivity) context).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((SendMessageActivity) context).getCurrentFocus().getWindowToken(), 0);
        }

    }

    private void animateHighlightIn(final View view, final int maxWidth) {
        int viewWidth = view.getWidth();
        view.getLayoutParams().width = ANIMATE_MIN_WIDTH;
        Log.d(TAG, "animateHighlightIn() view: " + view + "::::minWidth: " + ANIMATE_MIN_WIDTH + ":::: maxWidth: " + maxWidth + ":::: viewWidth: " + viewWidth);
        view.setVisibility(View.VISIBLE);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(ANIMATE_MIN_WIDTH, maxWidth);
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
        int viewWidth = view.getWidth();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(maxWidth, ANIMATE_MIN_WIDTH);
        int mDuration = 100; //in millis
        Log.d(TAG, "animateHighlightIn() view: " + view + "::::minWidth: " + ANIMATE_MIN_WIDTH + ":::: maxWidth: " + maxWidth + ":::: viewWidth: " + viewWidth);
        widthAnimator.setDuration(mDuration);
        widthAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().width = (int) animation.getAnimatedValue();
            view.requestLayout();
            if (view.getWidth() == ANIMATE_MIN_WIDTH) {
                view.setVisibility(View.GONE);
            }
        });
        widthAnimator.start();
    }

    private void setFacilityList(List<MedicalCenter> medCenters) {
        //set adapter for medical center dropdown spinner
        Facility facility = sessionFacade.getPreferredFacility();
        facilityAdapter = new ArrayAdapter<String>(context, R.layout.spinner_view_layout) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.spinner_dialog_root)).setText("");
                    ((TextView) v.findViewById(R.id.spinner_dialog_root)).setHint(getItem(getCount()));
                    v.findViewById(R.id.spinner_dialog_root).setPadding(0, 8, 8, 8);
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // dont display last item. It is used as hint.
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = super.getDropDownView(position, convertView, parent);

                if (position == mSelectedFacilityIndex) {
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.viewLightGrey));
                } else {
                    itemView.setBackgroundColor(Color.TRANSPARENT);
                }

                return itemView;
            }
        };

        facilityAdapter.setDropDownViewResource(R.layout.spinner_view_layout);
        int flag = 0;
        for (int medCenter = 0; medCenter < medCenters.size(); medCenter++) {
            facilityAdapter.add(medCenters.get(medCenter).value);
            if (facility != null && medCenters.get(medCenter).key.equals(facility.getName())) {
                flag = 1;
                this.selection = medCenter;
            }
        }
        if (flag == 0) {
            this.selection = facilityAdapter.getCount() + 1;
        }
        facilityAdapter.add(context.getString(R.string.input_text_placeholder));

        spFacilityInput.setAdapter(facilityAdapter);
        spFacilityInput.setSelection(this.selection);
        spFacilityInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedFacilityIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("SendMessageFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_SEND_MESSAGE_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchSuccess(List<FacilityInfoAll> facilityInfoAlls, List<MedicalCenter> facilityAll) {
        ((SendMessageActivity) context).hideActivityIndicator();
        setFacilityList(facilityAll);
    }

    @Override
    public void notifyFetchError(String message) {
        ((SendMessageActivity) context).hideActivityIndicator();
        showRequestFailure(message);
    }

    private void setPhoneEditTextLength(int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        etPhoneInput.setFilters(filterArray);
    }

    @Override
    public void notifyFetchSuccess(String purpose) {
        ((SendMessageActivity) context).hideActivityIndicator();
        if (purpose.equals(CONTACT_INFO_PURPOSE)) {
            etPhoneInput.setText(sessionFacade.getUserContactNumber());
            if (sessionFacade.getUserContactNumber().length() == 10)
                setPhoneEditTextLength(14);
        }
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        ((SendMessageActivity) context).hideActivityIndicator();
    }
}
