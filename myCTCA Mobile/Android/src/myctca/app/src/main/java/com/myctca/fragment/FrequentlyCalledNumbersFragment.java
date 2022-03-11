package com.myctca.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Facility;
import com.myctca.service.SessionFacade;
import com.myctca.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class FrequentlyCalledNumbersFragment extends Fragment {
    private static final String TAG = FrequentlyCalledNumbersFragment.class.getSimpleName();
    private Context context;
    private TextView facilityTitle;
    private TextView facilityLocation;
    private TextView facilityLocationZip;
    private TextView generalEnquiriesContact;
    private TextView technicalSupportContact;
    private TextView careManagementContact;
    private TextView schedulingContact;
    private TextView secondarySchedulingContact;
    private TextView travelAccomodationsContact;
    private TextView medicalRecordsContact;
    private TextView billingContact;
    private TextView pharmacyContact;
    private SessionFacade sessionFacade;
    private String phoneNumberToCall;
    private LinearLayout llMap;
    private LinearLayout llFacilityLocationZip;
    private LinearLayout atlantaFacilityMap;
    private TextView atlantaFacilityLocation;
    private TextView atlantaFacilityZip;
    private LinearLayout chicagoFacilityMap;
    private TextView chicagoFacilityLocation;
    private TextView chicagoFacilityZip;
    private LinearLayout phoenixFacilityMap;
    private TextView phoenixFacilityLocation;
    private TextView phoenixFacilityZip;
    private TextView atlantaFacilityContact;
    private TextView chicagoFacilityContact;
    private TextView phoenixFacilityContact;
    private TextView downtownChicagoHospitalContact;
    private TextView downtownGurneeHospitalContact;
    private TextView northPhoenixHospitalContact;
    private TextView scottdaleHospitalContact;
    private LinearLayout occPatientAllContacts;
    private LinearLayout patientFacilityContacts;
    private LinearLayout llGeneralEnquiries;
    private LinearLayout llTechnicalSupport;
    private LinearLayout llCareManagement;
    private LinearLayout llScheduling;
    private LinearLayout llTravelAndAccomodations;
    private LinearLayout llMedicalRecords;
    private LinearLayout llBilling;
    private LinearLayout llPharmacy;
    private LinearLayout llFinancialCounseling;
    private TextView financialCounselingContact;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frequently_called_numbers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();

        facilityTitle = view.findViewById(R.id.facility_title);
        facilityLocation = view.findViewById(R.id.facility_location);
        facilityLocationZip = view.findViewById(R.id.facility_location_zip);
        llFacilityLocationZip = view.findViewById(R.id.ll_secondary_scheduling_contact);
        generalEnquiriesContact = view.findViewById(R.id.general_enquiries_contact);
        llGeneralEnquiries = view.findViewById(R.id.llGeneralInquiry);
        technicalSupportContact = view.findViewById(R.id.technical_support_contact);
        llTechnicalSupport = view.findViewById(R.id.llTechnicalSupport);
        careManagementContact = view.findViewById(R.id.care_management_contact);
        llCareManagement = view.findViewById(R.id.llCareManagement);
        schedulingContact = view.findViewById(R.id.scheduling_contact);
        llScheduling = view.findViewById(R.id.llScheduling);
        secondarySchedulingContact = view.findViewById(R.id.secondary_scheduling_contact);
        travelAccomodationsContact = view.findViewById(R.id.travel_accomodations_contact);
        llTravelAndAccomodations = view.findViewById(R.id.llTravelAndAccomodations);
        medicalRecordsContact = view.findViewById(R.id.medical_records_contact);
        llMedicalRecords = view.findViewById(R.id.llMedicalRecords);
        billingContact = view.findViewById(R.id.billing_contact);
        llFinancialCounseling = view.findViewById(R.id.llFinancialCounseling);
        financialCounselingContact = view.findViewById(R.id.financial_counseling_contact);
        llBilling = view.findViewById(R.id.llBilling);
        pharmacyContact = view.findViewById(R.id.pharmacy_contact);
        llPharmacy = view.findViewById(R.id.llPharmacy);
        llMap = view.findViewById(R.id.ll_map);

        atlantaFacilityMap = view.findViewById(R.id.atlanta_facility_map);
        atlantaFacilityLocation = view.findViewById(R.id.atlanta_facility_location);
        atlantaFacilityZip = view.findViewById(R.id.atlanta_facility_location_zip);
        atlantaFacilityContact = view.findViewById(R.id.atlanta_facility_contact);

        chicagoFacilityMap = view.findViewById(R.id.chicago_facility_map);
        chicagoFacilityLocation = view.findViewById(R.id.chicago_facility_location);
        chicagoFacilityZip = view.findViewById(R.id.chicago_facility_location_zip);
        chicagoFacilityContact = view.findViewById(R.id.chicago_facility_contact);

        phoenixFacilityMap = view.findViewById(R.id.phoenix_facility_map);
        phoenixFacilityLocation = view.findViewById(R.id.phoenix_facility_location);
        phoenixFacilityZip = view.findViewById(R.id.phoenix_facility_location_zip);
        phoenixFacilityContact = view.findViewById(R.id.phoenix_facility_contact);

        downtownChicagoHospitalContact = view.findViewById(R.id.downtown_chicago_hospital_contact);
        downtownGurneeHospitalContact = view.findViewById(R.id.downtown_gurnee_hospital_contact);
        northPhoenixHospitalContact = view.findViewById(R.id.north_phoenix_hospital_contact);
        scottdaleHospitalContact = view.findViewById(R.id.scottdale_hospital_contact);

        patientFacilityContacts = view.findViewById(R.id.patient_facility_contacts);
        occPatientAllContacts = view.findViewById(R.id.occ_patient_all_contacts);

        setVisibilities();
        initializeContacts();
        handleOnClickListeners();
    }

    private void setVisibilities() {
        if (sessionFacade.getMyCtcaUserProfile().getPrimaryFacility().equals("OCC")) {
            occPatientAllContacts.setVisibility(View.VISIBLE);
            patientFacilityContacts.setVisibility(View.GONE);
            llMap.setVisibility(View.GONE);
        } else {
            occPatientAllContacts.setVisibility(View.GONE);
            patientFacilityContacts.setVisibility(View.VISIBLE);
            llMap.setVisibility(View.VISIBLE);
        }

        Facility facility = sessionFacade.getPreferredFacility();
        if (AppSessionManager.getInstance().getTechnicalSupport().isEmpty()) {
            llTechnicalSupport.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(facility.getCareManagementPhone())) {
            llCareManagement.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(facility.getSchedulingPhone())) {
            llScheduling.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(facility.getTravelAndAccommodationsPhone())) {
            llTravelAndAccomodations.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(facility.getHimroiPhone())) {
            llMedicalRecords.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(facility.getFinancialCounselingPhone())) {
            llFinancialCounseling.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(facility.getBillingPhone())) {
            llBilling.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(facility.getPharmacyPhone())) {
            llPharmacy.setVisibility(View.GONE);
        }
        if (!sessionFacade.getMyCtcaUserProfile().getPrimaryFacility().equals("OCC")
                && facility.getMainPhone().isEmpty()) {
            llGeneralEnquiries.setVisibility(View.GONE);
        }
    }

    private void initializeContacts() {
        Facility facility = sessionFacade.getPreferredFacility();

        facilityLocation.setText(facility.getStreetAddress());
        facilityLocationZip.setText(facility.getCityStateZip());

        facilityLocation.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        facilityLocationZip.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        atlantaFacilityLocation.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        atlantaFacilityZip.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        chicagoFacilityLocation.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        chicagoFacilityZip.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        phoenixFacilityLocation.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        phoenixFacilityZip.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        facilityTitle.setText(facility.getShortDisplayName());
        if (!sessionFacade.getMyCtcaUserProfile().getPrimaryFacility().equals("OCC"))
            generalEnquiriesContact.setText(facility.getMainPhone());
        technicalSupportContact.setText(AppSessionManager.getInstance().getTechnicalSupport());
        careManagementContact.setText(facility.getCareManagementPhone());
        schedulingContact.setText(facility.getSchedulingPhone());
        financialCounselingContact.setText(facility.getFinancialCounselingPhone());
        if (!TextUtils.isEmpty(facility.getSchedulingSecondaryPhone())) {
            llFacilityLocationZip.setVisibility(View.VISIBLE);
            secondarySchedulingContact.setText(facility.getSchedulingSecondaryPhone());
        }
        travelAccomodationsContact.setText(facility.getTravelAndAccommodationsPhone());
        medicalRecordsContact.setText(facility.getHimroiPhone());
        billingContact.setText(facility.getBillingPhone());
        pharmacyContact.setText(facility.getPharmacyPhone());
    }

    private void handleOnClickListeners() {
        generalEnquiriesContact.setOnClickListener(view -> makePhoneCall(generalEnquiriesContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_GENERAL_ENQ_TAP));
        technicalSupportContact.setOnClickListener(view -> makePhoneCall(technicalSupportContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_TECH_SUP_TAP));
        careManagementContact.setOnClickListener(view -> makePhoneCall(careManagementContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_CARE_MNG_TAP));
        schedulingContact.setOnClickListener(view -> makePhoneCall(schedulingContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_SCHEDULING_TAP));
        secondarySchedulingContact.setOnClickListener(view -> makePhoneCall(secondarySchedulingContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_SCHEDULING_TAP));
        travelAccomodationsContact.setOnClickListener(view -> makePhoneCall(travelAccomodationsContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_TRAVEL_TAP));
        medicalRecordsContact.setOnClickListener(view -> makePhoneCall(medicalRecordsContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_MEDICAL_REC_TAP));
        financialCounselingContact.setOnClickListener(view -> makePhoneCall(financialCounselingContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FINANCIAL_TAP));
        billingContact.setOnClickListener(view -> makePhoneCall(billingContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_BILLING_TAP));
        pharmacyContact.setOnClickListener(view -> makePhoneCall(pharmacyContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_PHARMACY_TAP));

        atlantaFacilityContact.setOnClickListener(view -> makePhoneCall(atlantaFacilityContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FAC_ATLANTA_TAP));
        chicagoFacilityContact.setOnClickListener(view -> makePhoneCall(chicagoFacilityContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FAC_CHICAGO_TAP));
        phoenixFacilityContact.setOnClickListener(view -> makePhoneCall(phoenixFacilityContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FAC_PHOENIX_TAP));

        downtownChicagoHospitalContact.setOnClickListener(view -> makePhoneCall(downtownChicagoHospitalContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FAC_CHI_DOT_TAP));
        downtownGurneeHospitalContact.setOnClickListener(view -> makePhoneCall(downtownGurneeHospitalContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FAC_GURNEE_TAP));
        northPhoenixHospitalContact.setOnClickListener(view -> makePhoneCall(northPhoenixHospitalContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FAC_PH_NRTH_TAP));
        scottdaleHospitalContact.setOnClickListener(view -> makePhoneCall(scottdaleHospitalContact.getText().toString(), CTCAAnalyticsConstants.ACTION_IMP_NUM_CALL_FAC_SCOTTSD_TAP));

        llMap.setOnClickListener(view -> showMap(facilityLocation.getText().toString(), facilityLocationZip.getText().toString()));
        chicagoFacilityMap.setOnClickListener(view -> showMap(chicagoFacilityLocation.getText().toString(), chicagoFacilityZip.getText().toString()));
        atlantaFacilityMap.setOnClickListener(view -> showMap(atlantaFacilityLocation.getText().toString(), atlantaFacilityZip.getText().toString()));
        phoenixFacilityMap.setOnClickListener(view -> showMap(phoenixFacilityLocation.getText().toString(), phoenixFacilityZip.getText().toString()));
    }

    private void getAllPermissions(String phoneNumber) {
        phoneNumberToCall = phoneNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            showPermissionError(HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_title), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), HtmlCompat.toHtml(HtmlCompat.fromHtml(getString(R.string.phone_permission_initial_message), HtmlCompat.FROM_HTML_MODE_LEGACY), HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE), getString(R.string.telehealth_permission_initial_positive), getString(R.string.telehealth_permission_initial_negative));
        } else {
            // Permission has already been granted or Android earlier than Marshmallow
            callPhoneNumber();
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

    private void makePhoneCall(String phoneNumber, String contactType) {
        CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("FrequentlyCalledNumbersFragment::handleOnClickListeners", contactType, sessionFacade.getPreferredFacility()));
        if (capableOfCalling()) {
            getAllPermissions(phoneNumber);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.appt_detail_incapable_of_calling_title));
            builder.setMessage(context.getString(R.string.appt_detail_incapable_of_calling_message));
            builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
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

    private void showMap(String streetAddress, String cityStateZip) {
        String addressForURI = formatAddressForURI(streetAddress, cityStateZip);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + addressForURI);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        startActivity(mapIntent);
    }

    private String formatAddressForURI(String streetAddress, String cityStateZip) {
        return streetAddress + ", " + cityStateZip;
    }
}