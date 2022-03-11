package com.myctca.fragment.appointmment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.myctca.R;
import com.myctca.activity.AppointmentRequestActivity;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentCalendarData;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.AppointmentRequestData;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.service.SessionFacade;

import java.util.List;

public class ApptRequestSummaryFragment extends BaseAppointmentFragment {
    private ImageView ivEditReason;
    private ImageView ivEditPreferredDateTime;
    private ImageView ivContactPref;
    private ImageView ivAdditionalComments;
    private Context context;
    private TextView tvApptReasonValue;
    private TextView tvContactPrefValue;
    private TextView tvAdditionalComments;
    private CardView cvApptReason;
    private TextView tvReasonTitle;
    private TextView tvContactPrefTitle;
    private TextView tvContactPrefSubTitle;
    private CardView cvAdditionalComments;
    private TextView tvAdditionalCommentsTitle;
    private CardView cvPreferredDateTime;
    private CardView cvContactPreference;
    private TextView tvPreferredTimeTitle;
    private LinearLayout llCellView;
    private CardView cvApptRequestDetailView;
    private TextView apptDate;
    private TextView apptName;
    private TextView apptPhysician;
    private TextView apptTimeLocation;
    private AppointmentRequestData appointmentRequestData;
    private TextView tvPreferredTimeSubTitle;
    private TextView prefDateTimeValue;
    private SessionFacade sessionFacade;
    private Appointment appointment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appt_request_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("ApptRequestSummaryFragment:onViewCreated", ((AppointmentRequestActivity) context).getViewPageType() + CTCAAnalyticsConstants.PAGE_APPT_SUMMARY_VIEW));
        sessionFacade = new SessionFacade();
        cvApptRequestDetailView = view.findViewById(R.id.appointment_request_detail_view);
        apptDate = cvApptRequestDetailView.findViewById(R.id.apptDate);
        apptName = cvApptRequestDetailView.findViewById(R.id.apptName);
        apptPhysician = cvApptRequestDetailView.findViewById(R.id.apptPhysician);
        apptTimeLocation = cvApptRequestDetailView.findViewById(R.id.apptTimeLocation);

        cvApptReason = view.findViewById(R.id.cvApptReason);
        ivEditReason = cvApptReason.findViewById(R.id.ivEdit);
        tvReasonTitle = cvApptReason.findViewById(R.id.tvTitle);
        LinearLayout llApptReasonDetail = cvApptReason.findViewById(R.id.llDetail);
        tvApptReasonValue = llApptReasonDetail.findViewById(R.id.tvMessage);

        cvPreferredDateTime = view.findViewById(R.id.cvPreferredDateTime);
        llCellView = cvPreferredDateTime.findViewById(R.id.llCell);
        ivEditPreferredDateTime = cvPreferredDateTime.findViewById(R.id.ivEdit);
        tvPreferredTimeTitle = cvPreferredDateTime.findViewById(R.id.tvTitle);
        LinearLayout llPreferredDateTime = cvPreferredDateTime.findViewById(R.id.llDetail);
        tvPreferredTimeSubTitle = llPreferredDateTime.findViewById(R.id.tvSubtitle);
        prefDateTimeValue = llPreferredDateTime.findViewById(R.id.tvMessage);

        cvContactPreference = view.findViewById(R.id.cvContactPreference);
        ivContactPref = cvContactPreference.findViewById(R.id.ivEdit);
        tvContactPrefTitle = cvContactPreference.findViewById(R.id.tvTitle);
        LinearLayout llContactPreferenceDetail = cvContactPreference.findViewById(R.id.llDetail);
        tvContactPrefSubTitle = llContactPreferenceDetail.findViewById(R.id.tvSubtitle);
        tvContactPrefValue = llContactPreferenceDetail.findViewById(R.id.tvMessage);

        cvAdditionalComments = view.findViewById(R.id.cvAdditionalComments);
        ivAdditionalComments = cvAdditionalComments.findViewById(R.id.ivEdit);
        tvAdditionalCommentsTitle = cvAdditionalComments.findViewById(R.id.tvTitle);
        LinearLayout llAdditionalCommentsDetail = cvAdditionalComments.findViewById(R.id.llDetail);
        tvAdditionalComments = llAdditionalCommentsDetail.findViewById(R.id.tvMessage);

        appointmentRequestData = ((AppointmentRequestActivity) context).getAppointmentRequestData();
        if (getArguments() != null) {
            appointment = (Appointment) getArguments().getParcelable("APPOINTMENT_DETAILS");
        }
        prepareView();
        setOnClickListeners();
    }

    private void prepareView() {
        String reasonTitle = context.getString(R.string.new_appt_reason);
        //show appointment detail if request is to reschedule or cancel the appointment
        if (((AppointmentRequestActivity) context).getApptRequestType() == AppointmentRequest.APPT_NEW) {
            cvApptRequestDetailView.setVisibility(View.GONE);
            cvApptReason.setVisibility(View.VISIBLE);
        } else if (((AppointmentRequestActivity) context).getApptRequestType() == AppointmentRequest.APPT_RESCHEDULE) {
            cvApptRequestDetailView.setVisibility(View.VISIBLE);
            cvApptReason.setVisibility(View.GONE);
        } else {
            cvApptRequestDetailView.setVisibility(View.VISIBLE);
            cvApptReason.setVisibility(View.VISIBLE);
            cvPreferredDateTime.setVisibility(View.GONE);
            reasonTitle = context.getString(R.string.cancel_appt_reason);
        }
        if (appointment != null) {
            apptDate.setText(appointment.getStartDateString());
            apptName.setText(appointment.getDescription());
            apptPhysician.setText(appointment.getResourceList());
            apptTimeLocation.setText(String.format("%s %s, %s", appointment.getStartTimeString(), appointment.getFacilityTimeZone(), appointment.getLocation()));
        }
        //Reason
        tvReasonTitle.setText(reasonTitle);
        tvApptReasonValue.setText(appointmentRequestData.getReason());

        //Contact Preference
        tvContactPrefTitle.setText(context.getString(R.string.appt_request_summary_contact_pref_title));
        tvContactPrefSubTitle.setVisibility(View.VISIBLE);
        tvContactPrefSubTitle.setText(appointmentRequestData.getCommunicationPreference());
        if (appointmentRequestData.getCommunicationPreference().equals(AppointmentRequest.CONTACT_PREFERENCE_CALL))
            tvContactPrefValue.setText(appointmentRequestData.getPhoneNumber());
        else
            tvContactPrefValue.setText(appointmentRequestData.getEmail());


        //Additional Comments
        tvAdditionalCommentsTitle.setText(context.getString(R.string.appt_request_summary_additional_comments_title));
        if (!TextUtils.isEmpty(appointmentRequestData.getAdditionalNotes()))
            tvAdditionalComments.setText(appointmentRequestData.getAdditionalNotes());
        else
            tvAdditionalComments.setText("No Comments");

        //Preferred Date and Time
        tvPreferredTimeTitle.setText(context.getString(R.string.appt_request_summary_preferred_date_title));
        tvPreferredTimeSubTitle.setVisibility(View.VISIBLE);
        //set first item
        List<AppointmentCalendarData> appointmentDateTimes = ((AppointmentRequestActivity) context).getAppointmentCalendarDataList();
        if (!appointmentDateTimes.isEmpty()) {
            tvPreferredTimeSubTitle.setText(appointmentDateTimes.get(0).getDateStr());
            prefDateTimeValue.setText(String.format("%s, %s", appointmentDateTimes.get(0).getDay(), appointmentDateTimes.get(0).getSlot()));
            //set rest items if any
            for (int i = 1; i < appointmentDateTimes.size(); i++) {
                View lineToAdd = getLayoutInflater().inflate(R.layout.purple_dotted_view, llCellView, false);
                llCellView.addView(lineToAdd);
                View viewToAdd = getLayoutInflater().inflate(R.layout.subtitle_message_layout, llCellView, false);
                TextView date = viewToAdd.findViewById(R.id.tvSubtitle);
                date.setVisibility(View.VISIBLE);
                TextView time = viewToAdd.findViewById(R.id.tvMessage);
                date.setText(appointmentDateTimes.get(i).getDateStr());
                time.setText(String.format("%s, %s", appointmentDateTimes.get(i).getDay(), appointmentDateTimes.get(i).getSlot()));
                llCellView.addView(viewToAdd);
            }
        }
    }

    private void setOnClickListeners() {
        ivEditReason.setOnClickListener(view -> {
            if (((AppointmentRequestActivity) context).getApptRequestType() == AppointmentRequest.APPT_CANCEL)
                ((AppointmentRequestActivity) context).addFragment(new AppointmentPreferredDateTimeFragment(), true);
            else
                ((AppointmentRequestActivity) context).addFragment(new AppointmentRequestCommonFragment(context.getString(R.string.appt_reason)), true);
        });

        ivEditPreferredDateTime.setOnClickListener(view -> ((AppointmentRequestActivity) context).addFragment(new AppointmentPreferredDateTimeFragment(), true));

        ivContactPref.setOnClickListener(view -> ((AppointmentRequestActivity) context).addFragment(new ApptRequestContactPreferenceFragment(), true));

        ivAdditionalComments.setOnClickListener(view -> ((AppointmentRequestActivity) context).addFragment(new AppointmentRequestCommonFragment(context.getString(R.string.appt_additional_comments)), true));
    }
}