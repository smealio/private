package com.myctca.fragment.appointmment;


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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.AppointmentChangeActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.fragment.TimePickerFragment;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentForm;
import com.myctca.service.CommonService;
import com.myctca.service.SessionFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentChangeFragment extends Fragment implements CommonService.CommonServiceListener {

    private static final String TAG = "myCTCA-NEWAPPT";
    private static final String CONTACT_INFO_PURPOSE = "CONTACT_INFO";
    private static final int ANIMATION_MIN_WIDTH = 0;
    private AppointmentForm formData;
    private AppointmentChangeActivity.ChangeType changeType;
    private Appointment appointment;
    // Layouts
    private LinearLayout llChangeApptDate;
    private LinearLayout llChangeApptTime;
    // Input Fields
    private TextView tvChangeApptFromInput;
    private TextView tvChangeApptSubjectInput;
    private TextView tvChangeApptDescInput;
    private TextView tvChangeApptDateInput;
    private TextView tvChangeApptTimeInput;
    private EditText etChangeApptPhoneInput;
    private EditText etChangeApptReasonInput;
    // Labels
    private TextView tvChangeApptDateLabel;
    private TextView tvChangeApptTimeLabel;
    private TextView tvChangeApptPhoneLabel;
    private TextView tvChangeApptReasonLabel;
    private TextView invalidDateTimeError;

    // Disclosures
    private View viewChangeAppDateDisclosure;
    private View viewChangeAppTimeDisclosure;
    // Separators and Highlights
    private View viewChangeApptDateSeparator;
    private View viewChangeApptDateHighlight;
    private View viewChangeApptTimeSeparator;
    private View viewChangeApptTimeHighlight;
    private View viewChangeApptPhoneSeparator;
    private View viewChangeApptPhoneHighlight;

    private String apptTime;
    private String apptDate;
    // Date
    private Date date;
    private Date time;

    private boolean phoneNoInvalid;
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appt_change, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        watcher = new PhoneNumberFormattingTextWatcher("US");
        // Get View Components
        // Layouts
        llChangeApptDate = view.findViewById(R.id.change_appt_date_layout);
        llChangeApptTime = view.findViewById(R.id.change_appt_time_layout);
        // Input Fields
        tvChangeApptFromInput = view.findViewById(R.id.change_appt_from_input);
        tvChangeApptSubjectInput = view.findViewById(R.id.change_appt_subject_input);
        tvChangeApptDescInput = view.findViewById(R.id.change_appt_desc_input);
        tvChangeApptDateInput = view.findViewById(R.id.change_appt_date_input);
        tvChangeApptTimeInput = view.findViewById(R.id.change_appt_time_input);
        etChangeApptPhoneInput = view.findViewById(R.id.change_appt_phone_input);
        etChangeApptReasonInput = view.findViewById(R.id.change_appt_reason_input);
        // Labels
        tvChangeApptDateLabel = view.findViewById(R.id.change_appt_date_label);
        tvChangeApptTimeLabel = view.findViewById(R.id.change_appt_time_label);
        tvChangeApptPhoneLabel = view.findViewById(R.id.change_appt_phone_label);
        tvChangeApptReasonLabel = view.findViewById(R.id.change_appt_reason_label);
        invalidDateTimeError = view.findViewById(R.id.invalid_date_time_error);
        // Disclosures
        viewChangeAppDateDisclosure = view.findViewById(R.id.change_appt_date_disclosure);
        viewChangeAppTimeDisclosure = view.findViewById(R.id.change_appt_time_disclosure);
        // Separators and Highlights
        viewChangeApptDateSeparator = view.findViewById(R.id.change_appt_date_separator);
        viewChangeApptDateHighlight = view.findViewById(R.id.change_appt_date_highlight);
        viewChangeApptTimeSeparator = view.findViewById(R.id.change_appt_time_separator);
        viewChangeApptTimeHighlight = view.findViewById(R.id.change_appt_time_highlight);
        viewChangeApptPhoneSeparator = view.findViewById(R.id.change_appt_phone_separator);
        viewChangeApptPhoneHighlight = view.findViewById(R.id.change_appt_phone_highlight);

        prepareView();
        sessionFacade.downloadContactInfo(this, context, CONTACT_INFO_PURPOSE);
        view.findViewById(R.id.changeApptScrollView).requestFocus();
    }

    private void prepareView() {
        // Add preset input
        tvChangeApptFromInput.setText(AppSessionManager.getInstance().getUserProfile().getFullName());

        tvChangeApptDescInput.setText(this.appointment.getDescription());

        if (changeType == AppointmentChangeActivity.ChangeType.RESCHEDULE) {
            prepareViewforRescheduleAppt();
        } else {
            prepareViewforCancelAppt();
        }

        // Phone
        tvChangeApptPhoneLabel.setOnClickListener(v -> {
            etChangeApptPhoneInput.setFocusableInTouchMode(true);
            etChangeApptPhoneInput.requestFocus();
        });
        etChangeApptPhoneInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                etChangeApptPhoneInput.post(() -> etChangeApptPhoneInput.setSelection(etChangeApptPhoneInput.getText().length()));
                animateHighlightIn(viewChangeApptPhoneHighlight, viewChangeApptPhoneSeparator.getWidth());
            } else {
                changePriorityOfField(tvChangeApptPhoneLabel, etChangeApptPhoneInput.getText().toString());
                animateHighlightOut(viewChangeApptPhoneHighlight, viewChangeApptPhoneSeparator.getWidth());
            }
        });

        etChangeApptPhoneInput.setOnClickListener(v -> etChangeApptPhoneInput.setSelection(etChangeApptPhoneInput.getText().length()));
        etChangeApptPhoneInput.setLongClickable(false);
        etChangeApptPhoneInput.addTextChangedListener(watcher);
        etChangeApptPhoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1 && Character.isDigit(charSequence.charAt(0))) {
                    setPhoneEditTextLength(14);
                } else if (charSequence.length()>0 && charSequence.toString().charAt(0) == '+') {
                    int spaces = charSequence.toString().replaceAll("[^ ]", "").length();
                    setPhoneEditTextLength(16 + spaces);
                }
                PhoneNumberUtils.formatNumber(charSequence.toString(), "US");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Reason
        tvChangeApptReasonLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click tvMessageLabel");
            etChangeApptReasonInput.requestFocus();
            etChangeApptReasonInput.performClick();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etChangeApptReasonInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        etChangeApptReasonInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                changePriorityOfField(tvChangeApptReasonLabel, etChangeApptReasonInput.getText().toString());
            }
        });
    }

    private void prepareViewforCancelAppt() {
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("ApptChangeFragment:prepareView", CTCAAnalyticsConstants.PAGE_APPOINTMENTS_CANCEL_VIEW));
        tvChangeApptSubjectInput.setText(context.getString(R.string.change_appt_subject_cancel_input));
        tvChangeApptDateInput.setText(appointment.getStartDateString());
        tvChangeApptTimeInput.setText(appointment.getStartTimeString());
        viewChangeAppDateDisclosure.setVisibility(View.GONE);
        viewChangeAppTimeDisclosure.setVisibility(View.GONE);
        tvChangeApptDateLabel.setText(context.getString(R.string.change_appt_date));
        tvChangeApptTimeLabel.setText(context.getString(R.string.change_appt_time));
    }

    private void prepareViewforRescheduleAppt() {
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("ApptChangeFragment:prepareView", CTCAAnalyticsConstants.PAGE_APPOINTMENTS_RESCHEDULE_VIEW));

        tvChangeApptSubjectInput.setText(context.getString(R.string.change_appt_subject_reschedule_input));
        // Date Input
        llChangeApptDate.setFocusableInTouchMode(true);
        llChangeApptDate.setFocusable(true);
        llChangeApptDate.setOnClickListener(v -> {
            showDatePickerDialog();
            dismissKeyboard();
        });
        llChangeApptDate.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewChangeApptDateHighlight, viewChangeApptDateSeparator.getWidth());
                llChangeApptDate.performClick();
            } else {
                apptDate = (String) tvChangeApptDateInput.getText();
                updateDateTimeUi(apptDate, apptTime);
                animateHighlightOut(viewChangeApptDateHighlight, viewChangeApptDateSeparator.getWidth());
            }
        });
        // Time Input
        llChangeApptTime.setFocusableInTouchMode(true);
        llChangeApptTime.setFocusable(true);
        llChangeApptTime.setOnClickListener(v -> {
            showTimePickerDialog();
            dismissKeyboard();
        });
        llChangeApptTime.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewChangeApptTimeHighlight, viewChangeApptTimeSeparator.getWidth());
                llChangeApptTime.performClick();
            } else {
                apptTime = (String) tvChangeApptTimeInput.getText();
                updateDateTimeUi(apptDate, apptTime);
                animateHighlightOut(viewChangeApptTimeHighlight, viewChangeApptTimeSeparator.getWidth());
            }
        });

        viewChangeAppDateDisclosure.setVisibility(View.VISIBLE);
        viewChangeAppTimeDisclosure.setVisibility(View.VISIBLE);

        tvChangeApptDateLabel.setText(context.getString(R.string.change_appt_new_date));
        tvChangeApptTimeLabel.setText(context.getString(R.string.change_appt_new_time));
    }

    private void updateDateTimeUi(String apptDate, String apptTime) {
        if (!TextUtils.isEmpty(apptDate) && !TextUtils.isEmpty(apptTime)) {
            if (dateTimeIsValid()) {
                invalidDateTimeError.setVisibility(View.GONE);
                tvChangeApptTimeLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                tvChangeApptDateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                invalidDateTimeError.setVisibility(View.VISIBLE);
                tvChangeApptTimeLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                tvChangeApptDateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        }
    }

    private void changePriorityOfField(TextView inputFieldLabel, String inputFieldText) {
        if (inputFieldText.isEmpty() || inputFieldText.equals("")) {
            inputFieldLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            inputFieldLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
    }

    private boolean dateTimeIsValid() {
        Calendar cTime = Calendar.getInstance();
        Calendar cDate = Calendar.getInstance();
        cDate.setTime(this.date);
        cTime.setTime(this.time);

        Log.d(TAG, "dateIsValid date    : " + date);
        return sessionFacade.dateTimeIsValid(cTime, cDate);
    }


    public void showDatePickerDialog() {
        AppointmentChangeActivity activity = (AppointmentChangeActivity) context;
        AppointmentDatePickerFragment datePickerFragment = AppointmentDatePickerFragment.newInstance(activity);
        datePickerFragment.show(activity.getSupportFragmentManager(), "datePicker");
        datePickerFragment.setPreviouslySelected(this.date);
    }

    public void showTimePickerDialog() {
        AppointmentChangeActivity activity = (AppointmentChangeActivity) context;
        TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(activity);
        timePickerFragment.show(activity.getSupportFragmentManager(), "timePicker");
        timePickerFragment.setPreviouslySelected(this.time);
    }

    public void setChangeType(AppointmentChangeActivity.ChangeType changeType) {
        this.changeType = changeType;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setChangeAppointmentDate(Date date) {
        this.date = date;
        Log.d(TAG, "ApptChangeFragment setChangeAppointmentDate: " + date);
        String changeApptDateString;

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        changeApptDateString = sdf.format(date);
        Log.d(TAG, "ApptChangeFragment setChangeAppointmentDate: " + changeApptDateString);
        tvChangeApptDateInput.setText(changeApptDateString);
    }

    public void setChangeAppointmentTime(Date date) {
        this.time = date;
        Log.d(TAG, "ApptChangeFragment setChangeAppointmentTime: " + date);
        String changeApptTimeString;

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        changeApptTimeString = sdf.format(date);

        tvChangeApptTimeInput.setText(changeApptTimeString);
    }

    public boolean isFormValid() {

        boolean validForm = true;

        formData = new AppointmentForm();

        String apptDate = (String) tvChangeApptDateInput.getText();
        if (TextUtils.isEmpty(apptDate)) {
            validForm = false;
            changePriorityOfField(tvChangeApptDateLabel, apptDate);
        } else {
            changePriorityOfField(tvChangeApptDateLabel, apptDate);
        }
        String apptTime = (String) tvChangeApptTimeInput.getText();
        if (TextUtils.isEmpty(apptTime)) {
            validForm = false;
            changePriorityOfField(tvChangeApptTimeLabel, apptTime);
        } else {
            changePriorityOfField(tvChangeApptTimeLabel, apptTime);
        }
        String apptPhone = etChangeApptPhoneInput.getText().toString();
        if (TextUtils.isEmpty(apptPhone)) {
            validForm = false;
            phoneNoInvalid = true;
            changePriorityOfField(tvChangeApptPhoneLabel, apptPhone);
        } else {
            String phone = apptPhone.replaceAll("[^0-9]", "");
            if (phone.length() >= 10) {
                phoneNoInvalid = true;
                tvChangeApptPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                validForm = false;
                phoneNoInvalid = false;
                tvChangeApptPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        }
        String apptReason = etChangeApptReasonInput.getText().toString();
        if (TextUtils.isEmpty(apptReason)) {
            validForm = false;
            changePriorityOfField(tvChangeApptReasonLabel, apptReason);
        } else {
            changePriorityOfField(tvChangeApptReasonLabel, apptReason);
        }

        if (changeType == AppointmentChangeActivity.ChangeType.RESCHEDULE && !TextUtils.isEmpty(apptTime) && !TextUtils.isEmpty(apptDate)) {
            if (dateTimeIsValid()) {
                invalidDateTimeError.setVisibility(View.GONE);
                tvChangeApptTimeLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                tvChangeApptDateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            } else {
                validForm = false;
                invalidDateTimeError.setVisibility(View.VISIBLE);
                tvChangeApptTimeLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                tvChangeApptDateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        }


        if (validForm) {
            formData.setAppointmentId(this.appointment.getAppointmentId());
            formData.setFrom((String) tvChangeApptFromInput.getText());
            if (changeType == AppointmentChangeActivity.ChangeType.RESCHEDULE) {
                formData.setSubject("Appointment reschedule request");
            } else {
                formData.setSubject("Appointment cancellation request");
            }
            String apptDateTime = apptDate + ", " + apptTime;
            formData.setAppointmentDate(apptDateTime);
            formData.setPhoneNumber(apptPhone);
            formData.setComments(apptReason);
            formData.setFacilityTimeZone(getTimeZoneShortName());
        }

        return validForm;
    }

    private String getTimeZoneShortName() {
        String tz = TimeZone.getDefault().getDisplayName(TimeZone.getDefault().inDaylightTime(new Date()), TimeZone.LONG);
        String[] stz = tz.split(" ");
        StringBuilder sName = new StringBuilder();
        for (String s : stz) {
            sName.append(s.charAt(0));
        }
        return sName.toString();
    }

    public boolean isPhoneNoValid() {
        return phoneNoInvalid;
    }

    public AppointmentForm getFormData() {
        return formData;
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && ((AppointmentChangeActivity) context).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((AppointmentChangeActivity) context).getCurrentFocus().getWindowToken(), 0);
        }

    }

    private void animateHighlightIn(final View view, final int maxWidth) {
        int viewWidth = view.getWidth();
        view.getLayoutParams().width = ANIMATION_MIN_WIDTH;
        Log.d(TAG, "animateHighlightIn() view: " + view + "::::minWidth: " + ANIMATION_MIN_WIDTH + ":::: maxWidth: " + maxWidth + ":::: viewWidth: " + viewWidth);
        view.setVisibility(View.VISIBLE);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(ANIMATION_MIN_WIDTH, maxWidth);
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
        ValueAnimator widthAnimator = ValueAnimator.ofInt(maxWidth, ANIMATION_MIN_WIDTH);
        int mDuration = 100; //in millis
        Log.d(TAG, "animateHighlightIn() view: " + view + "::::minWidth: " + ANIMATION_MIN_WIDTH + ":::: maxWidth: " + maxWidth + ":::: viewWidth: " + viewWidth);
        widthAnimator.setDuration(mDuration);
        widthAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().width = (int) animation.getAnimatedValue();
            view.requestLayout();
            if (view.getWidth() == ANIMATION_MIN_WIDTH) {
                view.setVisibility(View.GONE);
            }
        });
        widthAnimator.start();
    }

    private void setPhoneEditTextLength(int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        etChangeApptPhoneInput.setFilters(filterArray);
    }

    @Override
    public void notifyFetchSuccess(String purpose) {
        if (context != null)
            ((AppointmentChangeActivity) context).hideActivityIndicator();
        etChangeApptPhoneInput.setText(sessionFacade.getUserContactNumber());
        if(sessionFacade.getUserContactNumber().length() == 10)
            setPhoneEditTextLength(14);
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        if (context != null)
            ((AppointmentChangeActivity) context).hideActivityIndicator();
    }
}
