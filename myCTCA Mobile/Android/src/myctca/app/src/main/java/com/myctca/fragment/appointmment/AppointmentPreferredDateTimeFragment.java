package com.myctca.fragment.appointmment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;

import com.myctca.R;
import com.myctca.activity.AppointmentRequestActivity;
import com.myctca.activity.ApptRequestCalenderActivity;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentCalendarData;
import com.myctca.model.AppointmentDateTime;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.AppointmentRequestData;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.util.MyCTCADateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppointmentPreferredDateTimeFragment extends BaseAppointmentFragment {

    private static final int PREFERRED_DATE_TIME = 1;
    CopyOnWriteArrayList<AppointmentCalendarData> appointmentCalendarDataList = new CopyOnWriteArrayList<>();
    private TextView addDateTime;
    private Context context;
    private LinearLayout selectedCalendarDateTime;
    private View selectedView;
    private AppointmentRequestData appointmentRequestData;
    private LinearLayout llAppointmentDetails;
    private CardView cvAppointmentRequestDetailView;
    private TextView apptDate;
    private TextView apptName;
    private TextView apptPhysician;
    private TextView apptTimeLocation;
    private Appointment appointment;
    private RelativeLayout llCancelReason;
    private TextView cancelApptTitle;
    private EditText cancelApptValue;
    private LinearLayout llPreferredDateTime;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_preferred_date_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appointmentRequestData = ((AppointmentRequestActivity) context).getAppointmentRequestData();

        llAppointmentDetails = view.findViewById(R.id.llAppointmentDetails);
        cvAppointmentRequestDetailView = view.findViewById(R.id.appointment_request_detail_view);
        apptDate = cvAppointmentRequestDetailView.findViewById(R.id.apptDate);
        apptName = cvAppointmentRequestDetailView.findViewById(R.id.apptName);
        apptPhysician = cvAppointmentRequestDetailView.findViewById(R.id.apptPhysician);
        apptTimeLocation = cvAppointmentRequestDetailView.findViewById(R.id.apptTimeLocation);

        addDateTime = view.findViewById(R.id.addDateTime);
        selectedCalendarDateTime = view.findViewById(R.id.selectedCalendarDateTime);
        llPreferredDateTime = view.findViewById(R.id.llPreferredDateTime);
        llCancelReason = view.findViewById(R.id.llCancelReason);
        cancelApptTitle = llCancelReason.findViewById(R.id.apptTitle);
        cancelApptValue = llCancelReason.findViewById(R.id.apptValue);

        if (getArguments() != null) {
            appointment = (Appointment) getArguments().getParcelable("APPOINTMENT_DETAILS");
        }
        prepareView();
        setClickListeners();
        setDefaultValue();
        enableNextButton();
    }

    private void prepareView() {
        cancelApptValue.setHint(context.getString(R.string.appt_cancel_reason_hint));
        //show appointment detail if request is to reschedule or cancel the appointment
        if (((AppointmentRequestActivity) context).getApptRequestType() == AppointmentRequest.APPT_NEW) {
            CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("AppointmentPreferredDateTimeFragment:prepareView", ((AppointmentRequestActivity) context).getViewPageType() + CTCAAnalyticsConstants.PAGE_APPT_PREF_DATETIME_VIEW));
            llAppointmentDetails.setVisibility(View.GONE);
            llPreferredDateTime.setVisibility(View.VISIBLE);
        } else if (((AppointmentRequestActivity) context).getApptRequestType() == AppointmentRequest.APPT_RESCHEDULE) {
            CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("AppointmentPreferredDateTimeFragment:prepareView", ((AppointmentRequestActivity) context).getViewPageType() + CTCAAnalyticsConstants.PAGE_APPT_PREF_DATETIME_VIEW));
            llAppointmentDetails.setVisibility(View.VISIBLE);
            llPreferredDateTime.setVisibility(View.VISIBLE);
        } else {
            CTCAAnalyticsManager.createScreenViewEvent(new CTCAAnalytics("AppointmentPreferredDateTimeFragment:prepareView", ((AppointmentRequestActivity) context).getViewPageType() + CTCAAnalyticsConstants.PAGE_APPT_REASON_VIEW));
            llPreferredDateTime.setVisibility(View.GONE);
            llAppointmentDetails.setVisibility(View.VISIBLE);
            llCancelReason.setVisibility(View.VISIBLE);
            //enable/disable button when text changes and only in case of reason screen.
            cancelApptValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //nothing
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    enableNextButton();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    //nothing
                }
            });
            cancelApptTitle.setText(HtmlCompat.fromHtml(context.getString(R.string.appointment_cancel_title), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
        if (appointment != null) {
            appointmentRequestData.setAppointmentId(appointment.getAppointmentId());
            apptDate.setText(appointment.getStartDateString());
            apptName.setText(appointment.getDescription());
            apptPhysician.setText(appointment.getResourceList());
            apptTimeLocation.setText(String.format("%s %s, %s", appointment.getStartTimeString(), appointment.getFacilityTimeZone(), appointment.getLocation()));
        }
    }

    private void setDefaultValue() {
        //add card views to view if appointments were already added by user and are saved to obj in activity
        if (!appointmentRequestData.getAppointmentDateTimes().isEmpty()) {
            for (AppointmentCalendarData appointmentCalendarData : ((AppointmentRequestActivity) context).getAppointmentCalendarDataList()) {
                setApptCalendarData(appointmentCalendarData, false);
            }
        }
        cancelApptValue.setText(appointmentRequestData.getReason());
    }

    private void setClickListeners() {
        addDateTime.setOnClickListener(view -> openCalendarView(null, false));
    }

    private void openCalendarView(AppointmentCalendarData appointmentCalendarData, boolean forEdit) {
        //open calendar view
        Intent intent = new Intent(context, ApptRequestCalenderActivity.class);
        intent.putExtra("PREFERRED_DATE_TIME", appointmentCalendarData);
        intent.putExtra("FOR_EDIT", forEdit);
        intent.putExtra("ALL_SELECTED_DATE", appointmentCalendarDataList);
        startActivityForResult(intent, PREFERRED_DATE_TIME);
    }

    public void setApptCalendarData(AppointmentCalendarData appointmentCalendarData, boolean forEdit) {
        View viewToAdd;
        if (forEdit) {
            viewToAdd = selectedView;
        } else {
            viewToAdd = getLayoutInflater().inflate(R.layout.cardview_calendar_date_time_edit_delete, selectedCalendarDateTime, false);
            selectedCalendarDateTime.addView(viewToAdd);
            appointmentCalendarData.setId(viewToAdd.toString());
        }
        TextView appointmentDate = viewToAdd.findViewById(R.id.appointmentDate);
        TextView appointmentTime = viewToAdd.findViewById(R.id.appointmentTime);
        ImageView ivEditDateTime = viewToAdd.findViewById(R.id.ivEditDateTime);
        ImageView ivDeleteDateTime = viewToAdd.findViewById(R.id.ivDeleteDateTime);

        appointmentDate.setText(appointmentCalendarData.getDateStr());
        appointmentTime.setText(new StringBuilder().append(appointmentCalendarData.getDay()).append(", ").append(appointmentCalendarData.getSlot()).toString());
        ivEditDateTime.setOnClickListener(view -> {
            selectedView = viewToAdd;
            openCalendarView(appointmentCalendarData, true);
        });
        ivDeleteDateTime.setOnClickListener(view -> {
            AlertDialog dialog = getButtonsSideBySideDialog(viewToAdd, appointmentCalendarData, (AppointmentRequestActivity) context, "", getString(R.string.appt_preferred_date_time_delete_message), getString(R.string.appt_detail_cancel_icon_title), getString(R.string.delete_text));
            dialog.show();
        });
        //do now allow user to add more options after count reach 3
        if (selectedCalendarDateTime.getChildCount() == 3) {
            addDateTime.setVisibility(View.GONE);
        } else {
            addDateTime.setVisibility(View.VISIBLE);
        }
        //disable the next button if no appointment date/time is added.
        if (selectedCalendarDateTime.getChildCount() == 0) {
            ((AppointmentRequestActivity) context).enableNextButton(false);
        }
        setAppointmentDateTimeList(appointmentCalendarData);
    }

    private AlertDialog getButtonsSideBySideDialog(View viewToAdd, AppointmentCalendarData appointmentCalendarData, Activity activity, String title, String message, String positiveBtn, String negativeBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_yes_no_sidebyside_dialog_box, null);
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        TextView dialogMessage = view.findViewById(R.id.dialogMessage);
        TextView dialogPositiveButton = view.findViewById(R.id.dialogPositiveButton);
        TextView dialogNegativeButton = view.findViewById(R.id.dialogNegativeButton);

        if (title.isEmpty()) {
            dialogTitle.setVisibility(View.GONE);
        }
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        dialogPositiveButton.setText(positiveBtn);
        dialogNegativeButton.setText(negativeBtn);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPositiveButton.setOnClickListener(view1 -> dialog.dismiss());

        dialogNegativeButton.setOnClickListener(view1 -> {
            dialog.dismiss();
            selectedCalendarDateTime.removeView(viewToAdd);
            //do now allow user to add more options after count reach 3
            if (selectedCalendarDateTime.getChildCount() == 3) {
                addDateTime.setVisibility(View.GONE);
            } else {
                addDateTime.setVisibility(View.VISIBLE);
            }
            enableNextButton();
            deletePreferredDateTime(appointmentCalendarData);
        });
        return dialog;
    }

    private void deletePreferredDateTime(AppointmentCalendarData appointmentCalendarData) {
        //delete item from locally saved data
        for (AppointmentCalendarData appt : appointmentCalendarDataList) {
            if (appt.getId().equals(appointmentCalendarData.getId())) {
                appointmentCalendarDataList.remove(appt);
                break;
            }
        }
    }

    public void setAppointmentDateTimeList(AppointmentCalendarData appointmentCalendarData) {
        //save the data locally to list
        for (AppointmentCalendarData appt : appointmentCalendarDataList) {
            if (appt.getId().equals(appointmentCalendarData.getId())) {
                appointmentCalendarDataList.remove(appt);
                appointmentCalendarDataList.add(appointmentCalendarData);
                return;
            }
        }
        appointmentCalendarDataList.add(appointmentCalendarData);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREFERRED_DATE_TIME && resultCode == Activity.RESULT_OK && data != null) {
            AppointmentCalendarData appointmentCalendarData = (AppointmentCalendarData) data.getSerializableExtra("PREFERRED_DATE_TIME");
            boolean forEdit = data.getBooleanExtra("FOR_EDIT", false);
            setApptCalendarData(appointmentCalendarData, forEdit);
        }
        enableNextButton();
    }

    public void enableNextButton() {
        if (((AppointmentRequestActivity) context).getApptRequestType() == AppointmentRequest.APPT_CANCEL) {
            ((AppointmentRequestActivity) context).enableNextButton(!cancelApptValue.getText().toString().isEmpty());
        } else {
            ((AppointmentRequestActivity) context).enableNextButton(selectedCalendarDateTime.getChildCount() > 0);
        }
        saveAppointmentData();
    }

    //called when next button is called from activity
    @Override
    public void saveAppointmentData() {
        super.saveAppointmentData();
        List<AppointmentDateTime> appointmentDateTimeList = new ArrayList<>();
        for (AppointmentCalendarData appt : appointmentCalendarDataList) {
            AppointmentDateTime appointmentDateTime = new AppointmentDateTime();
            appointmentDateTime.setDate(MyCTCADateUtils.convertDateToLocalString(appt.getDate().getTime()));
            appointmentDateTime.setTimePreference(appt.getTimePreference());
            appointmentDateTimeList.add(appointmentDateTime);
        }
        ((AppointmentRequestActivity) context).setAppointmentCalendarDataList(appointmentCalendarDataList);
        if (((AppointmentRequestActivity) context).getApptRequestType() == AppointmentRequest.APPT_CANCEL)
            ((AppointmentRequestActivity) context).getAppointmentRequestData().setReason(cancelApptValue.getText().toString());
        ((AppointmentRequestActivity) context).getAppointmentRequestData().setAppointmentDateTimes(appointmentDateTimeList);
    }
}