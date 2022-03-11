package com.myctca.fragment.appointmment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.myctca.R;
import com.myctca.activity.AppointmentRequestActivity;
import com.myctca.model.AppointmentRequestData;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

//reusable screen for Appointment reason and appointment additional comments.
public class AppointmentRequestCommonFragment extends BaseAppointmentFragment {

    private String type = "";
    private Context context;
    private EditText apptValue;
    private TextView apptTitle;
    private AppointmentRequestData appointmentRequestData;

    //saves the type when screen is for appt Reason or additional comments
    public AppointmentRequestCommonFragment(String type) {
        this.type = type;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_reason_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appointmentRequestData = ((AppointmentRequestActivity) context).getAppointmentRequestData();
        apptTitle = view.findViewById(R.id.apptTitle);
        apptValue = view.findViewById(R.id.apptValue);
        prepareView();
        enableNextButton();
    }

    private void prepareView() {
        //set default texts and hints
        if (!TextUtils.isEmpty(type)) {
            if (type.equals(context.getString(R.string.appt_reason))) {
                CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("AppointmentRequestCommonFragment:prepareView", ((AppointmentRequestActivity) context).getViewPageType() + CTCAAnalyticsConstants.PAGE_APPT_REASON_VIEW));
                apptTitle.setText(HtmlCompat.fromHtml(context.getString(R.string.appointment_reason_title), HtmlCompat.FROM_HTML_MODE_LEGACY));
                apptValue.setText(appointmentRequestData.getReason());
            } else {
                CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("AppointmentRequestCommonFragment:prepareView", ((AppointmentRequestActivity) context).getViewPageType() + CTCAAnalyticsConstants.PAGE_APPT_ADDI_COMMENTS_VIEW));
                apptValue.setHint(context.getString(R.string.appt_additional_comments_hint));
                apptValue.setText(appointmentRequestData.getAdditionalNotes());
                apptTitle.setText(HtmlCompat.fromHtml(context.getString(R.string.appointment_additional_comments_title), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
        }

        //enable/disable button when text changes and only in case of reason screen.
        apptValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (type.equals(context.getString(R.string.appt_reason))) {
                    ((AppointmentRequestActivity) context).enableNextButton(!charSequence.toString().isEmpty());
                }
                saveAppointmentData();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //nothing
            }
        });
    }

    public void enableNextButton() {
        if (type.equals(context.getString(R.string.appt_reason)))
            ((AppointmentRequestActivity) context).enableNextButton(!apptValue.getText().toString().isEmpty());
    }

    //called when next button is clicked from activity
    @Override
    public void saveAppointmentData() {
        super.saveAppointmentData();
        if (type.equals(context.getString(R.string.appt_reason))) {
            appointmentRequestData.setReason(apptValue.getText().toString());
        } else {
            appointmentRequestData.setAdditionalNotes(apptValue.getText().toString());
        }
    }
}