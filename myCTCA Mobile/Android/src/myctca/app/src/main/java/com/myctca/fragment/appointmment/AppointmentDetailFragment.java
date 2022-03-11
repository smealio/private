package com.myctca.fragment.appointmment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.myctca.R;
import com.myctca.activity.AppointmentDetailActivity;
import com.myctca.activity.AppointmentRequestActivity;
import com.myctca.activity.AppointmentUpcomingTelehealthUrlActivity;
import com.myctca.activity.TelehealthCommunicationActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.fragment.DownloadPdfFragment;
import com.myctca.model.Appointment;
import com.myctca.model.AppointmentRequest;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MedicalCenter;
import com.myctca.model.MeetingAccessTokenJson;
import com.myctca.model.MyCTCATask;
import com.myctca.model.MyCTCAUserProfile;
import com.myctca.model.UserPermissions;
import com.myctca.model.UserType;
import com.myctca.service.AppointmentService;
import com.myctca.service.SessionFacade;
import com.myctca.util.Constants;
import com.myctca.util.GeneralUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class AppointmentDetailFragment extends Fragment implements AppointmentService.AppointmentServicePostListener {
    private static String[] allPermissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private static final String TAG = "myCTCA-ApptDetail";
    private static final int TELEHEALTH_MEETING = 1;
    private static final int CALL_PHONE = 2;
    private TextView tvFacilityMainPhone;
    private TextView tvFacilitySchedulingPhone;
    private TextView tvFacilityAccommodationsPhone;
    private TextView tvFacilityTransportationPhone;
    private TextView tvFacilityAddress;
    private TextView tvFacilityCityStateZip;

    private String phoneNumberToCall;
    private Button btnTelehealth;

    private RelativeLayout rlSchedPhone;
    private RelativeLayout rlMainPhone;
    private RelativeLayout rlAccomPhone;
    private RelativeLayout rlTransPhone;

    private TextView tvApptDetailDesc;
    private TextView tvApptDetailDateTime;
    private TextView tvApptDetailLoc;
    private TextView tvApptDetailPatientInstructions;
    private TextView tvApptDetailSchedulerNotes;
    private TextView tvFacilityName;

    private Appointment appointment;
    private Context context;
    private SessionFacade sessionFacade;
    private AlertDialog dialog;
    private String meetingTime = "";
    private TextView tvApptDetailAdditionalInfo;
    private TextView tvApptDetailProviderInfo;
    private String token;
    private LinearLayout llMapAddress;
    private Button btnGetDirections;
    private TextView tvShareAppointment;
    private TextView tvRescheduleAppointment;
    private TextView tvCancelAppointment;
    private TextView tvRescheduleCancelMessage;
    private CardView cvShareReschCancel;
    private String permissionMessage;
    private String permissionsTitle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appt_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            permissionMessage = getString(R.string.telehealth_permission_bt_message);
            permissionsTitle = getString(R.string.telehealth_permission_bt_title);
            allPermissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_CONNECT};
        } else {
            permissionsTitle = getString(R.string.telehealth_permission_title);
            permissionMessage = getString(R.string.telehealth_permission_message);
        }
        sessionFacade = new SessionFacade();
        tvApptDetailDesc = view.findViewById(R.id.appt_detail_desc);
        tvApptDetailDateTime = view.findViewById(R.id.appt_detail_date_time);
        tvApptDetailLoc = view.findViewById(R.id.appt_detail_loc);
        tvApptDetailPatientInstructions = view.findViewById(R.id.appt_detail_patient_instr);
        tvApptDetailSchedulerNotes = view.findViewById(R.id.appt_detail_sched_notes);
        tvApptDetailAdditionalInfo = view.findViewById(R.id.appt_additional_info);
        tvApptDetailProviderInfo = view.findViewById(R.id.appt_provider_info);
        llMapAddress = view.findViewById(R.id.llMapAddress);
        btnTelehealth = view.findViewById(R.id.telehealth_btn);
        btnGetDirections = view.findViewById(R.id.btnGetDirections);
        tvFacilityName = view.findViewById(R.id.appt_detail_treatment_facility);
        tvFacilityMainPhone = view.findViewById(R.id.appt_detail_call_main_phone);
        tvFacilitySchedulingPhone = view.findViewById(R.id.appt_detail_call_sched_phone);
        tvFacilityAccommodationsPhone = view.findViewById(R.id.appt_detail_call_accom_phone);
        tvFacilityTransportationPhone = view.findViewById(R.id.appt_detail_call_trans_phone);
        tvFacilityAddress = view.findViewById(R.id.appt_detail_map_address);
        tvFacilityCityStateZip = view.findViewById(R.id.appt_detail_map_city_state_zip);
        tvRescheduleCancelMessage = view.findViewById(R.id.tv_reschedule_cancel_message);
        cvShareReschCancel = view.findViewById(R.id.cvShareReschCancel);

        tvShareAppointment = view.findViewById(R.id.tvShareAppointment);
        tvRescheduleAppointment = view.findViewById(R.id.tvRescheduleAppointment);
        tvCancelAppointment = view.findViewById(R.id.tvCancelAppointment);

        rlMainPhone = view.findViewById(R.id.appt_detail_main_phone_layout);
        rlSchedPhone = view.findViewById(R.id.appt_detail_sched_phone_layout);
        rlAccomPhone = view.findViewById(R.id.appt_detail_accom_phone_layout);
        rlTransPhone = view.findViewById(R.id.appt_detail_trans_phone_layout);
        appointment = ((AppointmentDetailActivity) context).getAppointmentData();

        setTelehealthButton();
        prepareView();
    }

    private void setTelehealthButton() {
        if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER) {
            btnTelehealth.setVisibility(View.GONE);
        } else if (appointment.getTelehealth() && !TextUtils.isEmpty(appointment.getTelehealthMeetingJoinUrl())) {
            btnTelehealth.setText(context.getString(R.string.telehealth_join_telehealth_meeting));
            btnTelehealth.setBackground(ContextCompat.getDrawable(context, R.drawable.ctca_round_button_green));
            btnTelehealth.setTextColor(ContextCompat.getColor(context, R.color.white));
            openTelehealthCommunicationScreen(btnTelehealth, appointment);
        } else if (appointment.getTelehealth() && !TextUtils.isEmpty(appointment.getTeleHealthUrl())) {
            btnTelehealth.setText(context.getString(R.string.telehealth_join_telehealth_meeting));
            btnTelehealth.setBackground(ContextCompat.getDrawable(context, R.drawable.ctca_round_button_green));
            btnTelehealth.setTextColor(ContextCompat.getColor(context, R.color.white));
            openApptUpcomingTelehealthUrl(btnTelehealth, appointment.getTeleHealthUrl());
        } else if (appointment.getTelehealth() && !TextUtils.isEmpty(appointment.getTelehealthInfoUrl())) {
            btnTelehealth.setText(context.getString(R.string.telehealth_setup_guide));
            openApptUpcomingTelehealthUrl(btnTelehealth, appointment.getTelehealthInfoUrl());
        } else {
            btnTelehealth.setVisibility(View.GONE);
        }
    }

    private void openTelehealthCommunicationScreen(Button btnTelehealth, Appointment appointment) {
        btnTelehealth.setOnClickListener(view -> {
                    if (isNetworkAvailable()) {
                        ((AppointmentDetailActivity) context).showActivityIndicator(context.getString(R.string.login_options_loading));
                        sessionFacade.downloadMeetingAccessToken(context, appointment.getMeetingId(), this);
                    } else {
                        showErrorDialog(context.getString(R.string.telehealth_internet_error_title), context.getString(R.string.telehealth_internet_error_message), true);
                    }
                }
        );
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    private void prepareView() {
        tvApptDetailDesc.setText(appointment.getDescription());
        String dateTime = appointment.getStartDateString() + " @ " + appointment.getStartTimeString() + " " + appointment.getFacilityTimeZone();
        tvApptDetailDateTime.setText(dateTime);
        tvApptDetailLoc.setText(appointment.getLocation());

        String provider = appointment.getResourceList();
        if (TextUtils.isEmpty(provider)) {
            provider = context.getString(R.string.appt_provider_null_title);
        }
        tvApptDetailProviderInfo.setText(provider);

        String patientInstr = appointment.getPatientInstructions();
        if (TextUtils.isEmpty(patientInstr)) {
            patientInstr = context.getString(R.string.appt_detail_null_text);
        }
        tvApptDetailPatientInstructions.setText(patientInstr);

        String schedulerNotes = appointment.getSchedulerNotes();
        if (schedulerNotes == null || schedulerNotes.equals("")) {
            schedulerNotes = context.getString(R.string.appt_detail_null_text);
        }
        tvApptDetailSchedulerNotes.setText(schedulerNotes);

        String additonalInfo = appointment.getAdditionalInfo();
        if (additonalInfo == null || additonalInfo.equals("")) {
            additonalInfo = context.getString(R.string.appt_detail_null_text);
        }
        tvApptDetailAdditionalInfo.setText(additonalInfo);

        if (appointment.getTelehealth() || TextUtils.isEmpty(appointment.getFacilityAddress1())) {
            llMapAddress.setVisibility(View.GONE);
            btnGetDirections.setVisibility(View.GONE);
        } else {
            btnGetDirections.setVisibility(View.VISIBLE);
        }
        tvFacilityAddress.setText(appointment.getFacilityAddress1());
        String cityStateZip = appointment.getFacilityCity() + ", " + appointment.getFacilityState() + " " + appointment.getFacilityPostalCode();
        tvFacilityCityStateZip.setText(cityStateZip);
        if (appointment.getTelehealth()) {
            tvFacilityName.setText("Telehealth");
        } else {
            String facilityName = appointment.getFacilityName();
            List<MedicalCenter> facilityAll = AppSessionManager.getInstance().getFacilityAll();
            for (MedicalCenter medicalCenter : facilityAll) {
                if (medicalCenter.key.equals(facilityName)) {
                    tvFacilityName.setText(medicalCenter.value);
                    break;
                }
            }
        }
        tvFacilityMainPhone.setText(appointment.getFacilityMainPhone());
        tvFacilitySchedulingPhone.setText(appointment.getFacilitySchedulingPhone());
        tvFacilityAccommodationsPhone.setText(appointment.getFacilityAccommodationsPhone());
        tvFacilityTransportationPhone.setText(appointment.getFacilityTransportationPhone());

        setVisibilities();
        setOnClickListeners();
    }

    private void setVisibilities() {
        Date myDate = new Date();
        Date appointmentDate = appointment.getStartDateLocal();
        long difference = appointmentDate.getTime() - myDate.getTime();
        if (appointment.getUpcoming()) {
            //hide share and cancel buttons and show a message if appointment is within 24 hours.
            if (difference <= DateUtils.DAY_IN_MILLIS) {
                Spanned message = HtmlCompat.fromHtml(context.getString(R.string.reschedule_cancel_message, appointment.getFacilitySchedulingPhone()), HtmlCompat.FROM_HTML_MODE_LEGACY);
                SpannableString ss = new SpannableString(message);

                //make the text inside the message clickable
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        makePhoneCall(appointment.getFacilitySchedulingPhone());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        ds.setUnderlineText(true);
                    }
                };
                //message is fixed except the contact number at the end. hardcoded the starting index of number.
                ss.setSpan(clickableSpan, 66, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvRescheduleCancelMessage.setText(ss);
                tvRescheduleCancelMessage.setMovementMethod(LinkMovementMethod.getInstance());
                tvRescheduleCancelMessage.setHighlightColor(Color.TRANSPARENT);

                tvRescheduleAppointment.setVisibility(View.GONE);
                tvCancelAppointment.setVisibility(View.GONE);
                tvRescheduleCancelMessage.setVisibility(View.VISIBLE);
            }
            MyCTCAUserProfile user = AppSessionManager.getInstance().getUserProfile();
            if (user != null && !user.userCan(UserPermissions.RESCHEDULE_APPOINTMENT)) {
                tvRescheduleAppointment.setVisibility(View.GONE);
            }
            if (user != null && !user.userCan(UserPermissions.CANCEL_APPOINTMENT)) {
                tvCancelAppointment.setVisibility(View.GONE);
            }
        } else {
            //if appointment is past, don't allow user to cancel, share or reschedule the appointment
            cvShareReschCancel.setVisibility(View.GONE);
            tvRescheduleCancelMessage.setVisibility(View.GONE);
        }
    }

    private void setOnClickListeners() {
        tvShareAppointment.setOnClickListener(view -> {
            ((AppointmentDetailActivity) context).addFragment(DownloadPdfFragment.newInstance(), appointment.getAppointmentId());
        });

        tvRescheduleAppointment.setOnClickListener(view -> {
            CTCAAnalyticsManager.createEvent("AppointmentDetailFragment:setOnClickListeners", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_RESCHEDULE_TAP, null, null);
            Log.d(TAG, "RESCHEDULE BUTTON PRESSED");
            Intent intent = new Intent(context, AppointmentRequestActivity.class);
            intent.putExtra(AppointmentRequest.APPT_REQUEST_TYPE, AppointmentRequest.APPT_RESCHEDULE);
            intent.putExtra("APPOINTMENT_DETAILS", appointment);
            startActivity(intent);
        });

        tvCancelAppointment.setOnClickListener(view -> {
            CTCAAnalyticsManager.createEvent("AppointmentDetailFragment:setOnClickListeners", CTCAAnalyticsConstants.ACTION_APPOINTMENTS_CANCEL_TAP, null, null);
            Log.d(TAG, "CANCEL BUTTON PRESSED");
            Intent cancelIntent = new Intent(context, AppointmentRequestActivity.class);
            cancelIntent.putExtra(AppointmentRequest.APPT_REQUEST_TYPE, AppointmentRequest.APPT_CANCEL);
            cancelIntent.putExtra("APPOINTMENT_DETAILS", appointment);
            startActivity(cancelIntent);
        });

        rlMainPhone.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Main Phone");
            makePhoneCall(tvFacilityMainPhone.getText().toString());
        });

        rlSchedPhone.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Scheduling Phone");
            makePhoneCall(tvFacilitySchedulingPhone.getText().toString());
        });

        rlAccomPhone.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Accommodations Phone");
            makePhoneCall(tvFacilityAccommodationsPhone.getText().toString());
        });

        rlTransPhone.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Transportation Phone");
            makePhoneCall(tvFacilityTransportationPhone.getText().toString());
        });

        btnGetDirections.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Map");
            showMap();
        });
    }

    private void openApptUpcomingTelehealthUrl(final Button btnTelehealth, final String url) {
        btnTelehealth.setOnClickListener(view -> {
            Intent apptDetailIntent = AppointmentUpcomingTelehealthUrlActivity.newIntent(context, url, btnTelehealth.getText().toString());
            context.startActivity(apptDetailIntent);
        });
    }

    private void makePhoneCall(String phoneNumber) {
        phoneNumberToCall = phoneNumber;
        if (capableOfCalling()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> permissionsToAskFor = new ArrayList<>();
                permissionsToAskFor.add(Manifest.permission.CALL_PHONE);
                showPermissionError(CALL_PHONE, HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative), permissionsToAskFor);
            } else {
                // Permission has already been granted or Android earlier than Marshmallow
                callPhoneNumber(phoneNumberToCall);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.appt_detail_incapable_of_calling_title));
            builder.setMessage(context.getString(R.string.appt_detail_incapable_of_calling_message));
            builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void callPhoneNumber(String phoneNumber) {
        if (phoneNumber != null) {
            Log.d(TAG, "incoming phone number: " + phoneNumber);
            String phoneForURL = formatPhoneNumberForURL(phoneNumber);
            phoneForURL = Constants.countryCode + phoneForURL;
            Log.d(TAG, "URL phone number: " + phoneForURL);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneForURL));
            startActivity(callIntent);
        }
    }

    @Override
    @NonNull
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callPhoneNumber(phoneNumberToCall);
            } else {
                Toast.makeText(context, "Call Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == TELEHEALTH_MEETING) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                allPermissionsGranted &= (result == PackageManager.PERMISSION_GRANTED);
            }
            if (allPermissionsGranted) {
                openTelehealthScreen();
            } else {
                ArrayList<String> permissionsToAskFor = new ArrayList<>();
                boolean somePermissionsForeverDenied = false;
                boolean denied = false;
                for (String permission : permissions) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        //denied
                        denied = true;
                        permissionsToAskFor.add(permission);
                        Log.e("denied", permission);
                    } else {
                        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            //allowed
                            Log.e("allowed", permission);
                        } else {
                            //set to never ask again
                            Log.e("set to never ask again", permission);
                            somePermissionsForeverDenied = true;
                        }
                    }
                }
                if (permissions.length == 1 && permissions[0].equals(Manifest.permission.CALL_PHONE)) {
                    if (somePermissionsForeverDenied) {
                        showPermissionError(CALL_PHONE, HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_positive), getString(R.string.telehealth_permission_negative), new ArrayList<>());
                    } else if (denied) {
                        showPermissionError(CALL_PHONE, HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative), permissionsToAskFor);
                    }
                } else {
                    if (somePermissionsForeverDenied) {
                        showPermissionError(TELEHEALTH_MEETING, HtmlCompat.toHtml(HtmlCompat.fromHtml(permissionsTitle, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(permissionMessage, HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_positive), getString(R.string.telehealth_permission_negative), new ArrayList<>());
                    } else if (denied) {
                        showPermissionError(TELEHEALTH_MEETING, HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_bt_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative), permissionsToAskFor);
                    }
                }
            }
        }
    }

    private boolean capableOfCalling() {

        final PackageManager mgr = context.getPackageManager();

        Uri callToUri = Uri.parse("tel:");
        Intent intent = new Intent(Intent.ACTION_CALL, callToUri);
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Log.d(TAG, "list: " + list);
        return (!list.isEmpty());
    }

    private String formatPhoneNumberForURL(String phoneNum) {
        // Replaces any non-digit with an empty string
        return phoneNum.replaceAll("\\D", "");
    }

    private void showMap() {

        String streetAddress = tvFacilityAddress.getText().toString();
        String cityStateZip = tvFacilityCityStateZip.getText().toString();
        String addressForURI = formatAddressForURI(streetAddress, cityStateZip);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + addressForURI);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private String formatAddressForURI(String streetAddress, String cityStateZip) {
        return streetAddress + ", " + cityStateZip;
    }

    public void showPostErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }


    @Override
    public void notifyPostSuccess(boolean ifError, String error) {
        //do nothing
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        ((AppointmentDetailActivity) context).hideActivityIndicator();
        if (task == MyCTCATask.MEETING_ACCESS_TOKEN) {
            showErrorDialog(context.getString(R.string.telehealth_error_title), context.getString(R.string.telehealth_error_message), false);
        } else {
            if (message.isEmpty())
                message = context.getString(R.string.error_400);
            showPostErrorMessage(message);
        }
    }

    @Override
    public void notifyMeetingAccessToken(MeetingAccessTokenJson json) {
        ((AppointmentDetailActivity) context).hideActivityIndicator();
        token = json.getMeetingAccessToken().getToken();
        getAllPermissions();
    }

    private void getAllPermissions() {
        ArrayList<String> permissionsToAskFor = new ArrayList<>();
        for (String permission : allPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToAskFor.add(permission);
            }
        }
        if (!permissionsToAskFor.isEmpty()) {
            showPermissionError(TELEHEALTH_MEETING, HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_bt_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.telehealth_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative), permissionsToAskFor);
        } else {
            openTelehealthScreen();
        }
    }

    private void showPermissionError(int permissionType, String title, String message, String positiveBtn, String negativeBtn, ArrayList<String> permissionsToAskFor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(60,30,55,0);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_COMPACT));
        AlertDialog dialog = builder.setCustomTitle(textView)
                .setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(positiveBtn, (dialogInterface, i) -> {
                    if (positiveBtn.equals(getString(R.string.telehealth_permission_positive))) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        requestPermissions(permissionsToAskFor.toArray(new String[0]), permissionType);
                    }
                })
                .setNegativeButton(negativeBtn, (dialogInterface, i) -> {
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }


    private void openTelehealthScreen() {
        Intent intent = new Intent(context, TelehealthCommunicationActivity.class);
        intent.putExtra("TOKEN", token);
        intent.putExtra("APPOINTMENT", appointment);
        startActivity(intent);
        AppSessionManager.getInstance().setIdleTimeout(36000000);
    }

    private void showErrorDialog(String title, String message, boolean isReasonInternet) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!isReasonInternet) {
            dialog = builder.setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Not Now", (dialog1, which) -> {
                    })
                    .setPositiveButton("Continue", (dialog1, which) -> {
                        if (isNetworkAvailable()) {
                            if (!appointment.getTeleHealthUrl().isEmpty()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appointment.getTeleHealthUrl()));
                                startActivityForResult(intent, TELEHEALTH_MEETING);
                            }
                        } else {
                            dialog.dismiss();
                            showErrorDialog(context.getString(R.string.telehealth_internet_error_title), context.getString(R.string.telehealth_internet_error_message), true);
                        }
                    }).create();
        } else {
            dialog = builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", ((dialogInterface, i) -> {
                    })).create();
        }
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TELEHEALTH_MEETING)
            GeneralUtil.logoutApplication();
    }
}
