package com.myctca.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.activity.MoreContactUsActivity;
import com.myctca.activity.SendMessageActivity;
import com.myctca.adapter.CaregiverFacilityAllAdapter;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Facility;
import com.myctca.model.FacilityInfoAll;
import com.myctca.model.MedicalCenter;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.UserType;
import com.myctca.service.FacilityInfoAllService;
import com.myctca.service.SessionFacade;
import com.myctca.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreContactUsFragment extends Fragment implements FacilityInfoAllService.FacilityInfoAllListener, CaregiverFacilityAllAdapter.CaregiverFacilityAllInterface {

    private static final String TAG = "myCTCA-CONTACTUS";

    private TextView tvFacilityName;
    private TextView tvCallMain;
    private TextView tvCallSched;
    private TextView tvCallAccomm;
    private TextView tvCallTransport;
    private TextView tvCallROIHelp;
    private TextView tvMapAddress;
    private TextView tvMapCityStateZip;
    private TextView tvCallTech;

    private RelativeLayout rlMainPhone;
    private RelativeLayout rlSchedPhone;
    private RelativeLayout rlAccomPhone;
    private RelativeLayout rlTransPhone;
    private RelativeLayout rlROIHelpPhone;
    private RelativeLayout rlMap;

    private RelativeLayout rlCallTech;
    private RelativeLayout rlSendMsgTech;

    private String phoneNumberToCall;
    private RecyclerView rvFacilityAll;
    private SessionFacade sessionFacade;
    private CaregiverFacilityAllAdapter adapter;
    private RelativeLayout rl_contact_us;
    private LinearLayout llCaregiverFacilityAll;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more_contact_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        tvFacilityName = view.findViewById(R.id.more_contact_us_treatment_facility);

        tvCallMain = view.findViewById(R.id.more_contact_us_call_main_phone);
        tvCallSched = view.findViewById(R.id.more_contact_us_call_sched_phone);
        tvCallAccomm = view.findViewById(R.id.more_contact_us_call_accom_phone);
        tvCallTransport = view.findViewById(R.id.more_contact_us_call_trans_phone);
        tvCallROIHelp = view.findViewById(R.id.more_contact_us_roi_help_phone);
        tvMapAddress = view.findViewById(R.id.more_contact_us_map_address);
        tvMapCityStateZip = view.findViewById(R.id.more_contact_us_map_city_state_zip);

        tvCallTech = view.findViewById(R.id.more_contact_us_call_tech_phone);
        tvCallTech.setText(AppSessionManager.getInstance().getTechnicalSupport());
        rl_contact_us = view.findViewById(R.id.rl_contact_us);

        // Layouts
        llCaregiverFacilityAll = view.findViewById(R.id.ll_care_giver_facility_all);
        rlMainPhone = view.findViewById(R.id.more_contact_us_main_phone_layout);
        rlSchedPhone = view.findViewById(R.id.more_contact_us_sched_phone_layout);
        rlAccomPhone = view.findViewById(R.id.more_contact_us_accom_phone_layout);
        rlTransPhone = view.findViewById(R.id.more_contact_us_trans_phone_layout);
        rlROIHelpPhone = view.findViewById(R.id.more_contact_us_roi_help_layout);
        rlMap = view.findViewById(R.id.more_contact_us_map_layout);

        rlCallTech = view.findViewById(R.id.more_contact_us_call_tech_layout);
        rlSendMsgTech = view.findViewById(R.id.more_contact_us_send_message_layout);

        rvFacilityAll = view.findViewById(R.id.caregiver_facilityInfoAll);
        updateUI();
        if (sessionFacade.getUserType() == UserType.CAREGIVER && isCaregiverImpersonating()) {
            llCaregiverFacilityAll.setVisibility(View.VISIBLE);
            downloadAllFacilityInfo();
            rl_contact_us.setVisibility(View.GONE);
        }
    }

    public boolean isCaregiverImpersonating() {
        List<MyCTCAProxy> proxies = sessionFacade.getProxies();
        boolean isImpersonating = false;
        for (MyCTCAProxy proxy : proxies) {
            if (sessionFacade.getMyCtcaUserProfile().getCtcaId().equals(proxy.getToCtcaUniqueId())) {
                isImpersonating = proxy.isImpersonating();
            }
        }
        return isImpersonating;
    }

    private void downloadAllFacilityInfo() {
        ((MoreContactUsActivity) context).showActivityIndicator(context.getString(R.string.login_options_loading));
        String PURPOSE = "Caregiver_Facility_All";
        sessionFacade.downloadAllFacilityInfo(context, this, PURPOSE);
    }

    private void updateUI() {

        Facility facility = AppSessionManager.getInstance().getPreferredFacility();
        if (facility != null) {
            tvFacilityName.setText(facility.getDisplayName());
            tvCallMain.setText(facility.getMainPhone());
            tvCallSched.setText(facility.getSchedulingPhone());
            tvCallAccomm.setText(facility.getAccommodationsPhone());
            tvCallTransport.setText(facility.getTransportationPhone());
            tvCallROIHelp.setText(facility.getHimroiPhone());
            tvMapAddress.setText(facility.getStreetAddress());
            tvMapCityStateZip.setText(facility.getCityStateZip());
        }
        rlMainPhone.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_CALL_MAIN_PHONE_TAP, null, null);
            Log.d(TAG, "Got A Click Main Phone");
            makePhoneCall((String) tvCallMain.getText());
        });

        rlSchedPhone.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_CALL_SCHEDULING_TAP, null, null);
            Log.d(TAG, "Got A Click Scheduling Phone");
            makePhoneCall((String) tvCallSched.getText());
        });

        rlAccomPhone.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_CALL_ACCOMODATIONS_TAP, null, null);
            Log.d(TAG, "Got A Click Accommodations Phone");
            makePhoneCall((String) tvCallAccomm.getText());
        });

        rlROIHelpPhone.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_CALL_ROI_TAP, null, null);
            Log.d(TAG, "Got A Click ROI Help Phone");
            makePhoneCall((String) tvCallTransport.getText());
        });

        rlTransPhone.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_CALL_TRANSPORTATIONS_TAP, null, null);
            Log.d(TAG, "Got A Click Transportation Phone");
            makePhoneCall((String) tvCallROIHelp.getText());
        });

        rlMap.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_MAP_ADDR_TAP, null, null);
            Log.d(TAG, "Got A Click Map");
            showMap();
        });

        rlCallTech.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_CALL_TECH_SUPPORT_TAP, null, null);
            Log.d(TAG, "Got A Click Tech Help Phone");
            makePhoneCall((String) tvCallTech.getText());
        });

        rlSendMsgTech.setOnClickListener(v -> {
            CTCAAnalyticsManager.createEvent("MoreContactUsFragment:updateUI", CTCAAnalyticsConstants.ACTION_CONTACT_US_SEND_MESSAGE_TAP, null, null);
            Log.d(TAG, "Got A Click Tech Send Message");
            onSendMessage();
        });

        adapter = new CaregiverFacilityAllAdapter(new CaregiverFacilityAllAdapter.CaregiverFacilityInfoDiffUtil(), this);
        rvFacilityAll.setAdapter(adapter);
        rvFacilityAll.setLayoutManager(new LinearLayoutManager(context));
    }

    private void makePhoneCall(String phoneNumber) {

        if (capableOfCalling()) {
            phoneNumberToCall = phoneNumber;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative));
            } else {
                // Permission has already been granted or Android earlier than Marshmallow
                callPhoneNumber();
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

    private void showPermissionError(String title, String message, String positiveBtn, String negativeBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_COMPACT))
                .setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(positiveBtn, (dialogInterface, i) -> {
                    if (positiveBtn.equals(getString(R.string.telehealth_permission_positive))) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, AppSessionManager.getInstance().getpermissionsRequestCallPhone());
                    }
                })
                .setNegativeButton(negativeBtn, (dialogInterface, i) -> {
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppSessionManager.getInstance().getpermissionsRequestCallPhone()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callPhoneNumber();
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
                if (somePermissionsForeverDenied) {
                    showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_positive), getString(R.string.telehealth_permission_negative));
                } else if (denied) {
                    showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative));
                }
            }
        }
    }

    public void callPhoneNumber() {
        if (phoneNumberToCall != null) {
            Log.d(TAG, "incoming phone number: " + phoneNumberToCall);
            String phoneForURL = formatPhoneNumberForURL(phoneNumberToCall);
            phoneForURL = Constants.countryCode + phoneForURL;
            Log.d(TAG, "URL phone number: " + phoneForURL);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneForURL));
            startActivity(callIntent);
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

        String streetAddress = (String) tvMapAddress.getText();
        String cityStateZip = (String) tvMapCityStateZip.getText();
        String addressForURI = formatAddressForURI(streetAddress, cityStateZip);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + addressForURI);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private String formatAddressForURI(String streetAddress, String cityStateZip) {
        return streetAddress + ", " + cityStateZip;
    }

    public void onSendMessage() {
        Intent sendMsgIntent = SendMessageActivity.newIntent(this.context);
        startActivity(sendMsgIntent);
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MailInboxFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_MAIL_REQUEST_FAIL, null, null);
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
        ((MoreContactUsActivity) context).hideActivityIndicator();
        adapter.submitList(facilityInfoAlls);
    }

    @Override
    public void notifyFetchError(String message) {
        ((MoreContactUsActivity) context).hideActivityIndicator();
        showRequestFailure(message);
    }

    @Override
    public void callFacility(String phoneNo) {
        makePhoneCall(phoneNo);
    }
}
