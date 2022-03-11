package com.myctca.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.AppointmentDetailActivity;
import com.myctca.activity.AppointmentUpcomingTelehealthUrlActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.Appointment;
import com.myctca.model.UserType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class AppointmentSectionAdapter extends Section {

    private static final String TAG = AppointmentSectionAdapter.class.getSimpleName();
    private static final String APPT_UPCOMING = "UPCOMING";
    private static CountDownTimer timer;
    private final Context context;
    private final AppointmentSectionListener listener;
    private final String appointmentType;
    String title;
    List<Appointment> appointments = new ArrayList<>();
    int sectionNumber;

    public AppointmentSectionAdapter(AppointmentSectionListener listener, Context context, String title, List<Appointment> appointments, int sectionNumber, String appointmentType) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.list_item_appt)
                .headerResourceId(R.layout.section_appt)
                .build());
        this.context = context;
        this.listener = listener;
        this.title = title;
        this.appointmentType = appointmentType;
        if (appointments != null)
            this.appointments = appointments;
        this.sectionNumber = sectionNumber;
    }

    public static void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    @Override
    public int getContentItemsTotal() {
        return appointments.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new ApptListItemHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ApptListItemHolder apptListItemHolder = (ApptListItemHolder) holder;
        apptListItemHolder.bind(appointments.get(position), sectionNumber, position);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new ApptHeader(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        ApptHeader headerHolder = (ApptHeader) holder;
        headerHolder.bind(title, sectionNumber);
    }

    public void showAppointmentDetail(Appointment appointment) {
        Intent apptDetailIntent = AppointmentDetailActivity.newIntent(context, appointment);
        context.startActivity(apptDetailIntent);
    }

    public interface AppointmentSectionListener {
        void openTelehealthCommunicationScreen(Appointment appointment);
    }

    private class ApptHeader extends RecyclerView.ViewHolder {

        private final TextView tvSectionTitle;
        private final TextView tvNextApptText;

        private ApptHeader(View view) {
            super(view);
            tvSectionTitle = view.findViewById(R.id.section_title);
            tvNextApptText = view.findViewById(R.id.nextApptText);
        }

        public void bind(String title, int sectionNumber) {
            tvSectionTitle.setText(title);
            if (appointmentType.equals(APPT_UPCOMING) && sectionNumber == 0) {
                //show "Next Appointment" title 1 week before appointment
                Date myDate = new Date();
                Date appointmentDate = appointments.get(0).getStartDateLocal();
                long difference = appointmentDate.getTime() - myDate.getTime();
                float days = (float) difference / (1000 * 60 * 60 * 24);
                if (days <= 3.0)
                    tvNextApptText.setVisibility(View.VISIBLE);
                else
                    tvNextApptText.setVisibility(View.GONE);
            } else {
                tvNextApptText.setVisibility(View.GONE);
            }
        }
    }

    private class ApptListItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView appointmentName;
        private final TextView appointmentTimeAndLocation;
        private final TextView appointmentProvider;
        private final Button btnTelehealth;

        private Appointment appointment;

        private ApptListItemHolder(View view) {
            super(view);

            appointmentName = itemView.findViewById(R.id.appt_desc);
            appointmentTimeAndLocation = itemView.findViewById(R.id.appt_date_time);
            appointmentProvider = itemView.findViewById(R.id.appt_provider);
            btnTelehealth = view.findViewById(R.id.telehealth_btn);
        }

        private void clearListItems() {
            // Clear the main body of the message item
            appointmentName.setText("");
            appointmentTimeAndLocation.setText("");
            appointmentProvider.setText("");
        }

        public void bind(Appointment appointment, int sectionNumber, int position) {
            this.appointment = appointment;
            itemView.setOnClickListener(this);

            // Clear  list item properties
            clearListItems();
            // Set Text
            appointmentName.setText(appointment.getDescription());
            String dateTime = appointment.getStartTimeString() + " " + appointment.getFacilityTimeZone() + ", " + appointment.getLocation();
            appointmentTimeAndLocation.setText(dateTime);
            if (TextUtils.isEmpty(appointment.getResources()))
                appointmentProvider.setVisibility(View.GONE);
            else
                appointmentProvider.setVisibility(View.VISIBLE);
            appointmentProvider.setText(appointment.getResources());

            btnTelehealth.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button_grey));
            btnTelehealth.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            if (appointmentType.equals(APPT_UPCOMING) && sectionNumber == 0 && position == 0) {
                btnTelehealth.setVisibility(View.VISIBLE);
                getTimeDifference();
            } else {
                btnTelehealth.setVisibility(View.GONE);
            }
        }

        private void setButtonUI() {
            if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER) {
                btnTelehealth.setVisibility(View.GONE);
            } else if (!appointment.getTelehealth()) {
                btnTelehealth.setVisibility(View.VISIBLE);
                btnTelehealth.setText("Now");
            } else if (appointment.getTelehealth() && !TextUtils.isEmpty(appointment.getTelehealthMeetingJoinUrl())) {
                btnTelehealth.setText(context.getString(R.string.telehealth_join_telehealth_meeting));
                btnTelehealth.setBackground(ContextCompat.getDrawable(context, R.drawable.ctca_round_button_green));
                btnTelehealth.setTextColor(ContextCompat.getColor(context, R.color.white));
                openTelehealthCommunicationScreen(appointment);
            } else if (appointment.getTelehealth() && !TextUtils.isEmpty(appointment.getTeleHealthUrl())) {
                btnTelehealth.setText(context.getString(R.string.telehealth_join_telehealth_meeting));
                btnTelehealth.setBackground(ContextCompat.getDrawable(context, R.drawable.ctca_round_button_green));
                btnTelehealth.setTextColor(ContextCompat.getColor(context, R.color.white));
                openApptUpcomingTelehealthUrl(appointment.getTeleHealthUrl());
            } else if (appointment.getTelehealth() && !TextUtils.isEmpty(appointment.getTelehealthInfoUrl())) {
                btnTelehealth.setText(context.getString(R.string.telehealth_setup_guide));
                openApptUpcomingTelehealthUrl(appointment.getTelehealthInfoUrl());
            } else {
                btnTelehealth.setVisibility(View.GONE);
            }
        }

        private void openTelehealthCommunicationScreen(Appointment nextAppt) {
            btnTelehealth.setOnClickListener(view -> listener.openTelehealthCommunicationScreen(nextAppt));
        }

        private void openApptUpcomingTelehealthUrl(final String url) {
            btnTelehealth.setOnClickListener(view -> {
                Intent apptDetailIntent = AppointmentUpcomingTelehealthUrlActivity.newIntent(context, url, btnTelehealth.getText().toString());
                context.startActivity(apptDetailIntent);
            });
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "ApptListItemHolder onCLick: " + getLayoutPosition());
            showAppointmentDetail(this.appointment);
        }

        private void getTimeDifference() {
            Date myDate = new Date();
            Date appointmentDate = appointment.getStartDateLocal();
            long difference = appointmentDate.getTime() - myDate.getTime();
            //start timer starting 2 days prior to appt time if time is left before appointment
            if (difference > 0) {
                timer = new CountDownTimer(difference, 1000) {
                    public void onTick(long millisUntilFinished) {
                        Calendar previousDateFromAppt = Calendar.getInstance();
                        previousDateFromAppt.setTime(appointmentDate);
                        previousDateFromAppt.add(Calendar.DATE, -1);

                        //hide button if more than 3 days are left
                        if (millisUntilFinished <= 3 * DateUtils.DAY_IN_MILLIS) {
                            if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == previousDateFromAppt.get(Calendar.DAY_OF_MONTH)) {
                                //show tomorrow if 1 day is left for the appointment
                                btnTelehealth.setText("Tomorrow");
                            } else if (DateUtils.isToday(appointmentDate.getTime())) {
                                //show hrs and mins if appt is today
                                int days = (int) ((int) millisUntilFinished / DateUtils.DAY_IN_MILLIS);
                                int hours = (int) ((millisUntilFinished - (DateUtils.DAY_IN_MILLIS * days)) / (DateUtils.HOUR_IN_MILLIS));
                                int min = (int) ((int) (millisUntilFinished - (DateUtils.DAY_IN_MILLIS * days) - ((DateUtils.HOUR_IN_MILLIS) * hours)) / (DateUtils.MINUTE_IN_MILLIS));
                                if (min + 1 == 60) {
                                    hours++;
                                    min = 0;
                                } else {
                                    min++;
                                }
                                //timer starting from 2 days prior to appt time
                                if (millisUntilFinished <= 30 * 60000 && appointment.getTelehealth()) {
                                    //display telehealth button 30 mins ahead of meeting time
                                    setButtonUI();
                                    timer.cancel();
                                } else if (DateUtils.isToday(appointmentDate.getTime())) {
                                    if (hours > 0) {
                                        if (min > 0)
                                            btnTelehealth.setText(String.format("Starts in %s Hour and %s Min", hours, min));
                                        else
                                            btnTelehealth.setText(String.format("Starts in %s Hour", hours));
                                    } else if (min > 0) {
                                        btnTelehealth.setText(String.format("Starts in %s Min", min));
                                    }
                                }
                            } else {
                                //show set up guide for remaining days
                                if (appointment.getTelehealth())
                                    //show setup button only if telehealth appointment
                                    setButtonUI();
                                else
                                    //hide in case of in-person appointment
                                    btnTelehealth.setVisibility(View.GONE);
                            }
                        } else {
                            btnTelehealth.setVisibility(View.GONE);
                        }
                    }

                    public void onFinish() {
                        setButtonUI();
                    }
                }.start();
            } else {
                setButtonUI();
            }
        }
    }
}

