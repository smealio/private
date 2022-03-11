package com.myctca.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreFormsLibraryActivity;
import com.myctca.adapter.SpinnerAdapter;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Facility;
import com.myctca.model.MedicalCenter;
import com.myctca.model.ROI;
import com.myctca.model.RoiDetails;
import com.myctca.service.CommonService;
import com.myctca.service.MoreFormsLibraryService;
import com.myctca.service.SessionFacade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;


public class MoreReleaseOfInfoFormFragment extends Fragment implements MoreFormsLibraryService.MoreFormsLibraryROIListenerGet, CommonService.CommonServiceListener {

    private static final String TAG = "myCTCA-MOREROI";
    private static final int ANIMATE_MIN_WIDTH = 0;
    private static final String ROIDETAILS = "ROI DETAILS";
    private static final String CONTACT_INFO_PURPOSE = "CONTACT_INFO";
    private static final String DATE_OF_BIRTH = "DOB";
    private static final String PICKUP_DATE = "PickupDate";
    private static final String BEGINNING_DATE_OF_SERVICE = "BeginningDateOfService";
    private static final String ENDING_DATE_OF_SERVICE = "EndingDateOfService";
    private ROI roiObj;
    // Main View
    private View mView;
    // ScrollView
    private ScrollView mScrollView;
    // Main Section
    private TextView tvMedicalCenterLabel;
    private Spinner spMedicalCenter;
    private View viewMedicalCenterSeparator;
    private View viewMedicalCenterHighlight;
    private TextView tvPatientFirstNameLabel;
    private EditText etPatientFirstNameInput;
    private View viewPatientFirstNameSeparator;
    private View viewPatientFirstNameHighlight;
    private TextView tvPatientLastNameLabel;
    private EditText etPatientLastNameInput;
    private View viewPatientLastNameSeparator;
    private View viewPatientLastNameHighlight;
    private LinearLayout llDOBLayout;
    private TextView tvDOBLabel;
    private TextView tvDOBInput;
    private View viewDOBSeparator;
    private View viewDOBHighlight;
    private LinearLayout llDeliveryMethod;
    private TextView tvDeliveryMethodLabel;
    private TextView tvDeliveryMethodInput;
    private View viewDeliveryMethodSeparator;
    private View viewDeliveryMethodHighlight;
    private LinearLayout llPickupDate;
    private TextView tvPickupDateLabel;
    private TextView tvPickupDateInput;
    private TextView tvAuthReleaseObtainLabel;
    private LinearLayout llAuthReleaseObtain;
    private TextView tvRoiRequestTitle;
    private TextView tvAuthActionInput;
    private View viewAuthReleaseObtainSeparator;
    private View viewAuthReleaseObtainHighlight;
    private TextView tvAuthFacilityLabel;
    private EditText etAuthFacilityInput;
    private View viewAuthFacilitySeparator;
    private View viewAuthFacilityHighlight;
    private TextView tvAuthAddressLabel;
    private EditText etAuthAddressInput;
    private View viewAuthAddressSeparator;
    private View viewAuthAddressHighlight;
    private TextView tvAuthCityLabel;
    private EditText etAuthCityInput;
    private View viewAuthCitySeparator;
    private View viewAuthCityHighlight;
    private TextView tvAuthStateLabel;
    private EditText etAuthStateInput;
    private View viewAuthStateSeparator;
    private View viewAuthStateHighlight;
    private TextView tvAuthZipLabel;
    private EditText etAuthZipInput;
    private View viewAuthZipSeparator;
    private View viewAuthZipHighlight;
    private TextView tvAuthPhoneLabel;
    private EditText etAuthPhoneInput;
    private View viewAuthPhoneSeparator;
    private View viewAuthPhoneHighlight;
    private TextView tvAuthFaxLabel;
    private EditText etAuthFaxInput;
    private View viewAuthFaxSeparator;
    private View viewAuthFaxHighlight;
    private TextView tvAuthEmailLabel;
    private EditText etAuthEmailInput;
    private View viewAuthEmailSeparator;
    private View viewAuthEmailHighlight;
    private LinearLayout llPurpose;
    private TextView tvPurposeLabel;
    private TextView tvPurposeInput;
    private View viewPurposeSeparator;
    private View viewPurposeHighlight;
    // Dates of Service
    private TextView tvDatesOfServiceFrom;
    private View viewDatesOfServiceFromSeparator;
    private View viewDatesOfServiceFromHighlight;
    private LinearLayout llDatesOfServiceTo;
    private LinearLayout llDatesOfServiceFrom;
    private TextView tvDateOfServiceTo;
    private TextView tvDateOfServiceFrom;
    private TextView tvDatesOfServiceTo;
    private View viewDatesOfServiceToSeparator;
    private View viewDatesOfServiceToHighlight;
    private TextView tvDatesOfServiceRestrictionsLabel;
    private EditText etDatesOfServiceRestrictionsInput;
    private View viewDatesOfServiceRestrictionsSeparator;
    private View viewDatesOfServiceRestrictionsHighlight;
    // Information to be Disclosed
    private LinearLayout llGeneralInfoLayout;
    private TextView tvGeneralInfoLabel;
    private TextView tvGeneralInfoInput;
    private View viewGeneralInfoSeparator;
    private View viewGeneralInfoHighlight;
    private LinearLayout llConfidentialInfoLayout;
    private TextView tvConfidentialInfoLabel;
    private TextView tvConfidentialInfoInput;
    private View viewConfidentialInfoSeparator;
    private View viewConfidentialInfoHighlight;
    //Heading include layouts
    private TextView roiPatientInfoTitle;
    private TextView roiDateOfServiceTitle;
    private TextView roiInfoDsiclosedTitle;
    private TextView roiAuthorizationTitle;
    // Authorize
    private EditText etAuthPatientPartyInput;
    private TextView tvAuthPatientPartyLabel;
    private int shortAnimationDuration;
    private boolean faxRequired = false;
    private boolean emailRequired = false;

    private int selection = 0;

    private List<String> roiSelectedDeliveryMethod = new ArrayList<>();
    private List<String> roiSelectedPurposes = new ArrayList<>();
    private List<String> roiSelectedAuthActions = new ArrayList<>();
    private List<String> roiSelectedInfo = new ArrayList<>();
    private List<String> roiSelectedConfidentialInfo = new ArrayList<>();

    private String generalInfoOther = "";
    private Date previousDob;
    private Date previousPickupDate;
    private Date previousBeginDateOfService;
    private Date previousEndDateOfService;
    private boolean phoneNoValid;
    private boolean faxNoValid;
    private boolean emailIdValid;
    private Context context;
    private SessionFacade sessionFacade;
    private RoiDetails roiDetails;
    private PhoneNumberFormattingTextWatcher watcher;

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View focusedView = ((MoreFormsLibraryActivity) context).getCurrentFocus();
        Log.d(TAG, "onDestroy: focusedView: " + focusedView);
        mScrollView.requestFocus();
    }

    // Options Menu in Action Bar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_more_release_of_info_form, container, false);
        mView = view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        watcher = new PhoneNumberFormattingTextWatcher("US");
        initializeViews(view);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        //initialize roi object
        roiObj = new ROI();
        downloadROIFormDetails();
        handleScrollView();
    }

    private void handleScrollView() {
        // Take away focus so that keyboard doesn't pop up
        mScrollView.requestFocus();
        // Scroll to top…not sure whichis more effective so I use both
        mScrollView.smoothScrollTo(0, 0);
        mScrollView.pageScroll(View.FOCUS_UP);
        setRetainInstance(true);
    }

    private void initializeViews(View view) {
        // WIRE IT UP!
        //retrieve titles
        roiPatientInfoTitle = view.findViewById(R.id.roi_patient_info_title);
        roiDateOfServiceTitle = view.findViewById(R.id.roi_date_of_service_title);
        roiInfoDsiclosedTitle = view.findViewById(R.id.roi_info_diclosed_title);
        roiAuthorizationTitle = view.findViewById(R.id.roi_authorization_title);
        // Main Section
        tvMedicalCenterLabel = view.findViewById(R.id.medical_center_label);
        spMedicalCenter = view.findViewById(R.id.medical_center_spinner);
        viewMedicalCenterSeparator = view.findViewById(R.id.medical_center_separator);
        viewMedicalCenterHighlight = view.findViewById(R.id.medical_center_highlight);
        tvPatientFirstNameLabel = view.findViewById(R.id.patient_firstname_tv);
        etPatientFirstNameInput = view.findViewById(R.id.patient_firstname_et);
        viewPatientFirstNameSeparator = view.findViewById(R.id.patient_firstname_separator);
        viewPatientFirstNameHighlight = view.findViewById(R.id.patient_firstname_highlight);
        tvPatientLastNameLabel = view.findViewById(R.id.patient_lastname_tv);
        etPatientLastNameInput = view.findViewById(R.id.patient_lastname_et);
        viewPatientLastNameSeparator = view.findViewById(R.id.patient_lastname_separator);
        viewPatientLastNameHighlight = view.findViewById(R.id.patient_lastname_highlight);
        llDOBLayout = view.findViewById(R.id.dob_layout);
        tvDOBLabel = view.findViewById(R.id.dob_label);
        tvDOBInput = view.findViewById(R.id.dob_input);
        viewDOBSeparator = view.findViewById(R.id.dob_separator);
        viewDOBHighlight = view.findViewById(R.id.dob_highlight);
        llDeliveryMethod = view.findViewById(R.id.delivery_method_layout);
        tvDeliveryMethodLabel = view.findViewById(R.id.delivery_method_label);
        tvDeliveryMethodInput = view.findViewById(R.id.delivery_method_input);
        viewDeliveryMethodSeparator = view.findViewById(R.id.delivery_method_separator);
        viewDeliveryMethodHighlight = view.findViewById(R.id.delivery_method_highlight);
        llPickupDate = view.findViewById(R.id.pickup_layout);
        tvPickupDateLabel = view.findViewById(R.id.pickup_label);
        tvPickupDateInput = view.findViewById(R.id.pickup_input);
        tvAuthReleaseObtainLabel = view.findViewById(R.id.auth_release_obtain_label);
        llAuthReleaseObtain = view.findViewById(R.id.auth_release_obtain_layout);
        tvRoiRequestTitle = view.findViewById(R.id.roi_request_title);
        tvAuthActionInput = view.findViewById(R.id.tv_auth_action_input);
        viewAuthReleaseObtainSeparator = view.findViewById(R.id.auth_release_obtain_separator);
        viewAuthReleaseObtainHighlight = view.findViewById(R.id.auth_release_obtain_highlight);
        tvAuthFacilityLabel = view.findViewById(R.id.auth_facility_label);
        etAuthFacilityInput = view.findViewById(R.id.auth_facility_input);
        viewAuthFacilitySeparator = view.findViewById(R.id.auth_facility_separator);
        viewAuthFacilityHighlight = view.findViewById(R.id.auth_facility_highlight);
        tvAuthAddressLabel = view.findViewById(R.id.auth_address_label);
        etAuthAddressInput = view.findViewById(R.id.auth_address_input);
        viewAuthAddressSeparator = view.findViewById(R.id.auth_address_separator);
        viewAuthAddressHighlight = view.findViewById(R.id.auth_address_highlight);
        tvAuthCityLabel = view.findViewById(R.id.auth_city_label);
        etAuthCityInput = view.findViewById(R.id.auth_city_input);
        viewAuthCitySeparator = view.findViewById(R.id.auth_city_separator);
        viewAuthCityHighlight = view.findViewById(R.id.auth_city_highlight);
        tvAuthStateLabel = view.findViewById(R.id.auth_state_label);
        etAuthStateInput = view.findViewById(R.id.auth_state_input);
        viewAuthStateSeparator = view.findViewById(R.id.auth_state_separator);
        viewAuthStateHighlight = view.findViewById(R.id.auth_state_highlight);
        tvAuthZipLabel = view.findViewById(R.id.auth_zip_label);
        etAuthZipInput = view.findViewById(R.id.auth_zip_input);
        viewAuthZipSeparator = view.findViewById(R.id.auth_zip_separator);
        viewAuthZipHighlight = view.findViewById(R.id.auth_zip_highlight);
        tvAuthPhoneLabel = view.findViewById(R.id.auth_phone_label);
        etAuthPhoneInput = view.findViewById(R.id.auth_phone_input);
        viewAuthPhoneSeparator = view.findViewById(R.id.auth_phone_separator);
        viewAuthPhoneHighlight = view.findViewById(R.id.auth_phone_highlight);
        tvAuthFaxLabel = view.findViewById(R.id.auth_fax_label);
        etAuthFaxInput = view.findViewById(R.id.auth_fax_input);
        viewAuthFaxSeparator = view.findViewById(R.id.auth_fax_separator);
        viewAuthFaxHighlight = view.findViewById(R.id.auth_fax_highlight);
        tvAuthEmailLabel = view.findViewById(R.id.auth_email_label);
        etAuthEmailInput = view.findViewById(R.id.auth_email_input);
        viewAuthEmailSeparator = view.findViewById(R.id.auth_email_separator);
        viewAuthEmailHighlight = view.findViewById(R.id.auth_email_highlight);
        llPurpose = view.findViewById(R.id.purpose_layout);
        tvPurposeLabel = view.findViewById(R.id.purpose_label);
        tvPurposeInput = view.findViewById(R.id.purpose_input);
        viewPurposeSeparator = view.findViewById(R.id.purpose_separator);
        viewPurposeHighlight = view.findViewById(R.id.purpose_highlight);
        // Dates Of Service
        tvDatesOfServiceFrom = view.findViewById(R.id.dates_of_service_from);
        viewDatesOfServiceFromSeparator = view.findViewById(R.id.dates_of_service_from_separator);
        viewDatesOfServiceFromHighlight = view.findViewById(R.id.dates_of_service_from_highlight);
        llDatesOfServiceTo = view.findViewById(R.id.ll_dates_of_service_to);
        llDatesOfServiceFrom = view.findViewById(R.id.ll_dates_of_service_from);
        tvDateOfServiceTo = view.findViewById(R.id.ending_specific_date_tv);
        tvDateOfServiceFrom = view.findViewById(R.id.beginning_specific_date_tv);

        tvDatesOfServiceTo = view.findViewById(R.id.dates_of_service_to);
        viewDatesOfServiceToSeparator = view.findViewById(R.id.dates_of_service_to_separator);
        viewDatesOfServiceToHighlight = view.findViewById(R.id.dates_of_service_to_highlight);
        tvDatesOfServiceRestrictionsLabel = view.findViewById(R.id.dates_of_service_restrictions_label);
        etDatesOfServiceRestrictionsInput = view.findViewById(R.id.dates_of_service_restrictions_input);
        viewDatesOfServiceRestrictionsSeparator = view.findViewById(R.id.dates_of_service_restrictions_separator);
        viewDatesOfServiceRestrictionsHighlight = view.findViewById(R.id.dates_of_service_restrictions_highlight);
        // Information to be Disclosed
        llGeneralInfoLayout = view.findViewById(R.id.info_disclosed_general_layout);
        tvGeneralInfoLabel = view.findViewById(R.id.info_disclosed_general_label);
        tvGeneralInfoInput = view.findViewById(R.id.info_disclosed_general_input);
        viewGeneralInfoSeparator = view.findViewById(R.id.info_disclosed_general_separator);
        viewGeneralInfoHighlight = view.findViewById(R.id.info_disclosed_general_highlight);
        llConfidentialInfoLayout = view.findViewById(R.id.info_disclosed_confidential_layout);
        tvConfidentialInfoLabel = view.findViewById(R.id.info_disclosed_confidential_label);
        tvConfidentialInfoInput = view.findViewById(R.id.info_disclosed_confidential_input);
        viewConfidentialInfoSeparator = view.findViewById(R.id.info_disclosed_confidential_separator);
        viewConfidentialInfoHighlight = view.findViewById(R.id.info_disclosed_confidential_highlight);
        // Authorize
        etAuthPatientPartyInput = view.findViewById(R.id.auth_patient_party_input);
        tvAuthPatientPartyLabel = view.findViewById(R.id.auth_patient_party_label);
        mScrollView = view.findViewById(R.id.more_roi_scroll_view);
    }

    public void downloadROIFormDetails() {
        ((MoreFormsLibraryActivity) context).showActivityIndicator(context.getString(R.string.more_roi_retrive_roi_records));
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_roi_form_info);
        Log.d(TAG, "ROI URL: " + url);
        Map<String, String> params = new HashMap<>();
        params.put("listOptions", "true");
        sessionFacade.getROIFormInfo(this, url, params, context, ROIDETAILS);
    }

    public void downloadUserDetails() {
        sessionFacade.downloadContactInfo(this, context, CONTACT_INFO_PURPOSE );
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_roi, menu);
        ((MoreFormsLibraryActivity) context).setToolBar(context.getString(R.string.more_roi_title));

        // Tint the Send Button in the Menu
        Drawable drawable = menu.findItem(R.id.toolbar_roi_send).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.colorAccent));
        menu.findItem(R.id.toolbar_roi_send).setIcon(drawable);

    }

    private void prepareView() {
        this.roiDetails = AppSessionManager.getInstance().getRoiDetails();
        //set titles
        roiPatientInfoTitle.setText(context.getString(R.string.roi_patient_info_title));
        roiInfoDsiclosedTitle.setText(context.getString(R.string.roi_info_disclosed_title));
        roiDateOfServiceTitle.setText(context.getString(R.string.roi_date_of_service_title));
        roiAuthorizationTitle.setText(context.getString(R.string.roi_auth_title));
        // Main Section
        tvMedicalCenterLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click tvMedicalCenterLabel");
            spMedicalCenter.requestFocus();
            spMedicalCenter.performClick();
        });
        handleSpinner();
        handlePatientInformation();
        handleConfidentialInfo();
        handleGeneralInfo();
        handleDateOfService();
        handleDeliveryMethod();
        handleReleaseInfo();
        handleReleaseAuthFacilities();
        handleAuthorization();
    }

    private void handleAuthorization() {
        tvAuthPatientPartyLabel.setOnClickListener(v -> {
            etAuthPatientPartyInput.setFocusableInTouchMode(true);
            etAuthPatientPartyInput.requestFocus();
        });
        etAuthPatientPartyInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                if (etAuthPatientPartyInput.getText().toString().matches("")) {
                    tvAuthPatientPartyLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthPatientPartyLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
            }
        });

        tvDatesOfServiceRestrictionsLabel.setOnClickListener(v -> {
            etDatesOfServiceRestrictionsInput.setFocusableInTouchMode(true);
            etDatesOfServiceRestrictionsInput.requestFocus();
        });
        etDatesOfServiceRestrictionsInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewDatesOfServiceRestrictionsHighlight, viewDatesOfServiceRestrictionsSeparator.getWidth());
            } else {
                animateHighlightOut(viewDatesOfServiceRestrictionsHighlight, viewDatesOfServiceRestrictionsSeparator.getWidth());
            }
        });
        List<String> highlyConfidentialInformationList = roiDetails.getHighlyConfidentialInformationList();
        Map<String, Boolean> highlyConfidentialInformationMap = new HashMap<>();
        for (String item : highlyConfidentialInformationList) {
            highlyConfidentialInformationMap.put(item, true);
        }
        setConfidentialInfoInputText(highlyConfidentialInformationMap);
    }

    private void handleReleaseAuthFacilities() {
        tvRoiRequestTitle.setText(context.getString(R.string.more_roi_request_title));
        llAuthReleaseObtain.setFocusableInTouchMode(true);
        llAuthReleaseObtain.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Purpose");
            showAuthActionDialog();

        });
        llAuthReleaseObtain.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "llAuthReleaseObtain onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewAuthReleaseObtainHighlight, viewAuthReleaseObtainSeparator.getWidth());
                if (llAuthReleaseObtain.canResolveLayoutDirection()) {
                    llAuthReleaseObtain.performClick();
                }
            } else {
                if (tvAuthActionInput.getText().toString().matches("")) {
                    tvAuthReleaseObtainLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthReleaseObtainLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthReleaseObtainHighlight, viewAuthReleaseObtainSeparator.getWidth());
            }
        });
        llAuthReleaseObtain.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
    }

    private void handleReleaseInfo() {
        // Hide Pickup Date and only show if Pick-up is selected Delivery Method
        llPickupDate.setAlpha(0);
        llPickupDate.setVisibility(View.GONE);
        llPickupDate.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click PickupDate");
            showDatePickerDialog(PICKUP_DATE, false, true);
        });

        tvAuthFacilityLabel.setOnClickListener(v -> {
            etAuthFacilityInput.setFocusableInTouchMode(true);
            etAuthFacilityInput.requestFocus();
        });
        etAuthFacilityInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewAuthFacilityHighlight, viewAuthFacilitySeparator.getWidth());
            } else {
                if (etAuthFacilityInput.getText().toString().matches("")) {
                    tvAuthFacilityLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthFacilityLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthFacilityHighlight, viewAuthFacilitySeparator.getWidth());
            }
        });
        tvAuthAddressLabel.setOnClickListener(v -> {
            etAuthAddressInput.setFocusableInTouchMode(true);
            etAuthAddressInput.requestFocus();
        });
        etAuthAddressInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewAuthAddressHighlight, viewAuthAddressSeparator.getWidth());
            } else {
                if (etAuthAddressInput.getText().toString().matches("")) {
                    tvAuthAddressLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthAddressLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthAddressHighlight, viewAuthAddressSeparator.getWidth());
            }
        });
        tvAuthCityLabel.setOnClickListener(v -> {
            etAuthCityInput.setFocusableInTouchMode(true);
            etAuthCityInput.requestFocus();
        });
        etAuthCityInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewAuthCityHighlight, viewAuthCitySeparator.getWidth());
            } else {
                if (etAuthCityInput.getText().toString().matches("")) {
                    tvAuthCityLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthCityLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthCityHighlight, viewAuthCitySeparator.getWidth());
            }
        });
        tvAuthStateLabel.setOnClickListener(v -> {
            etAuthStateInput.setFocusableInTouchMode(true);
            etAuthStateInput.requestFocus();
        });
        etAuthStateInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewAuthStateHighlight, viewAuthStateSeparator.getWidth());
            } else {
                if (etAuthStateInput.getText().toString().matches("")) {
                    tvAuthStateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthStateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthStateHighlight, viewAuthStateSeparator.getWidth());
            }
        });
        tvAuthZipLabel.setOnClickListener(v -> {
            etAuthZipInput.setFocusableInTouchMode(true);
            etAuthZipInput.requestFocus();
        });
        etAuthZipInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewAuthZipHighlight, viewAuthZipSeparator.getWidth());
            } else {
                if (etAuthZipInput.getText().toString().matches("")) {
                    tvAuthZipLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthZipLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthZipHighlight, viewAuthZipSeparator.getWidth());
            }
        });
        tvAuthPhoneLabel.setOnClickListener(v -> {
            etAuthPhoneInput.setFocusableInTouchMode(true);
            etAuthPhoneInput.requestFocus();
        });
        etAuthPhoneInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                etAuthPhoneInput.post(() -> etAuthPhoneInput.setSelection(etAuthPhoneInput.getText().length()));
                animateHighlightIn(viewAuthPhoneHighlight, viewAuthPhoneSeparator.getWidth());
            } else {
                if (etAuthPhoneInput.getText().toString().matches("")) {
                    tvAuthPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthPhoneHighlight, viewAuthPhoneSeparator.getWidth());
            }
        });

        etAuthPhoneInput.setOnClickListener(v -> etAuthPhoneInput.setSelection(etAuthPhoneInput.getText().length()));
        etAuthPhoneInput.setLongClickable(false);
        etAuthPhoneInput.addTextChangedListener(watcher);
        etAuthPhoneInput.addTextChangedListener(new TextWatcher() {
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


        tvAuthFaxLabel.setOnClickListener(v -> {
            etAuthFaxInput.setFocusableInTouchMode(true);
            etAuthFaxInput.requestFocus();
        });
        etAuthFaxInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewAuthFaxHighlight, viewAuthFaxSeparator.getWidth());
            } else {
                if (faxRequired && etAuthFaxInput.getText().toString().matches("")) {
                    tvAuthFaxLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthFaxLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthFaxHighlight, viewAuthFaxSeparator.getWidth());
            }
        });
        tvAuthEmailLabel.setOnClickListener(v -> {
            etAuthEmailInput.setFocusableInTouchMode(true);
            etAuthEmailInput.requestFocus();
        });
        etAuthEmailInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                animateHighlightIn(viewAuthEmailHighlight, viewAuthEmailSeparator.getWidth());
            } else {
                if (!isValidEmail(etAuthEmailInput.getText().toString()) && emailRequired && etAuthEmailInput.getText().toString().matches("")) {
                    tvAuthEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvAuthEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewAuthEmailHighlight, viewAuthEmailSeparator.getWidth());
            }
        });

        // Purpose
        llPurpose.setFocusableInTouchMode(true);
        llPurpose.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Purpose");
            showPurposeDialog();
        });
        llPurpose.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewPurposeHighlight, viewPurposeSeparator.getWidth());
                if (llPurpose.canResolveLayoutDirection()) {
                    llPurpose.performClick();
                }
            } else {
                if (tvPurposeInput.getText().toString().matches("")) {
                    tvPurposeLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvPurposeLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewPurposeHighlight, viewPurposeSeparator.getWidth());
            }
        });
        llPurpose.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });

    }

    private void handleDeliveryMethod() {
        llDeliveryMethod.setFocusableInTouchMode(true);
        llDeliveryMethod.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click DeliveryMethod");
            showDeliveryMethodDialog();

        });
        llDeliveryMethod.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewDeliveryMethodHighlight, viewDeliveryMethodSeparator.getWidth());
                if (llDeliveryMethod.canResolveLayoutDirection()) {
                    llDeliveryMethod.performClick();
                }
            } else {
                if (tvDeliveryMethodInput.getText().equals("")) {
                    tvDeliveryMethodLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvDeliveryMethodLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewDeliveryMethodHighlight, viewDeliveryMethodSeparator.getWidth());
            }
        });
        llDeliveryMethod.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
    }

    private void handleDateOfService() {
        // Dates Of Service
        // From
        llDatesOfServiceFrom.setFocusable(true);
        llDatesOfServiceFrom.setFocusableInTouchMode(true);
        llDatesOfServiceFrom.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "llDatesOfServiceFrom onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewDatesOfServiceFromHighlight, viewDatesOfServiceFromSeparator.getWidth());
                if (llDatesOfServiceFrom.canResolveLayoutDirection()) {
                    llDatesOfServiceFrom.performClick();
                }
            } else {
                if (TextUtils.isEmpty(tvDateOfServiceFrom.getText().toString())) {
                    tvDatesOfServiceFrom.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvDatesOfServiceFrom.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewDatesOfServiceFromHighlight, viewDatesOfServiceFromSeparator.getWidth());
            }
        });

        View.OnClickListener llDatesOfServiceFromListener = view -> {
            llDatesOfServiceFrom.requestFocus();
            showDatePickerDialog(BEGINNING_DATE_OF_SERVICE, true, false);
        };
        llDatesOfServiceFrom.setOnClickListener(llDatesOfServiceFromListener);

        // To
        llDatesOfServiceTo.setFocusable(true);
        llDatesOfServiceTo.setFocusableInTouchMode(true);
        llDatesOfServiceTo.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "llDatesOfServiceTo onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                showDatePickerDialog(ENDING_DATE_OF_SERVICE, false, false);
                animateHighlightIn(viewDatesOfServiceToHighlight, viewDatesOfServiceToSeparator.getWidth());
            } else {
                if (TextUtils.isEmpty(tvDateOfServiceTo.getText().toString())) {
                    tvDatesOfServiceTo.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvDatesOfServiceTo.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewDatesOfServiceToHighlight, viewDatesOfServiceToSeparator.getWidth());
            }
        });

        View.OnClickListener rgDatesOfServiceToListener = view -> {
            llDatesOfServiceTo.requestFocus();
            showDatePickerDialog(ENDING_DATE_OF_SERVICE, false, false);
        };
        llDatesOfServiceTo.setOnClickListener(rgDatesOfServiceToListener);

    }

    private void handlePatientInformation() {
        etPatientFirstNameInput.setText(sessionFacade.getMyCtcaUserProfile().getFirstName());
        tvPatientFirstNameLabel.setOnClickListener(v -> {
            etPatientFirstNameInput.setFocusableInTouchMode(true);
            etPatientFirstNameInput.requestFocus();
        });
        etPatientFirstNameInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientFirstNameInput onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewPatientFirstNameHighlight, viewPatientFirstNameSeparator.getWidth());
            } else {
                if (etPatientFirstNameInput.getText().toString().matches("")) {
                    tvPatientFirstNameLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvPatientFirstNameLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewPatientFirstNameHighlight, viewPatientFirstNameSeparator.getWidth());
            }
        });

        etPatientLastNameInput.setText(sessionFacade.getMyCtcaUserProfile().getLastName());
        tvPatientLastNameLabel.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click last name");
            etPatientLastNameInput.setFocusableInTouchMode(true);
            etPatientLastNameInput.requestFocus();
        });
        etPatientLastNameInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewPatientLastNameHighlight, viewPatientLastNameSeparator.getWidth());
            } else {
                if (etPatientLastNameInput.getText().toString().matches("")) {
                    tvPatientLastNameLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvPatientLastNameLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewPatientLastNameHighlight, viewPatientLastNameSeparator.getWidth());
            }
        });

        llDOBLayout.setFocusableInTouchMode(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.applyPattern("dd MMM, yyyy");
        if (sessionFacade.getUserDob() != null) {
            tvDOBInput.setText(sdf.format(sessionFacade.getUserDob()));
            this.previousDob = sessionFacade.getUserDob();
        } else {
            this.previousDob = new Date();
        }
        llDOBLayout.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click DOB");
            showDatePickerDialog(DATE_OF_BIRTH, true, false);

        });
        llDOBLayout.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewDOBHighlight, viewDOBSeparator.getWidth());
                if (llDOBLayout.canResolveLayoutDirection()) {
                    llDOBLayout.performClick();
                }
            } else {
                if (tvDOBInput.getText().equals("")) {
                    tvDOBLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvDOBLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewDOBHighlight, viewDOBSeparator.getWidth());
            }
        });
        llDOBLayout.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
    }

    private void handleGeneralInfo() {
        // Information to be Disclosed Section
        llGeneralInfoLayout.setFocusable(true);
        llGeneralInfoLayout.setFocusableInTouchMode(true);
        llGeneralInfoLayout.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click General Info");
            showGeneralInfoDisclosure();

        });
        llGeneralInfoLayout.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "llGeneralInfoLayout onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewGeneralInfoHighlight, viewGeneralInfoSeparator.getWidth());
                if (llGeneralInfoLayout.canResolveLayoutDirection()) {
                    llGeneralInfoLayout.performClick();
                }
            } else {
                if (tvGeneralInfoInput.getText().equals("")) {
                    tvGeneralInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvGeneralInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewGeneralInfoHighlight, viewGeneralInfoSeparator.getWidth());
            }
        });
    }

    private void handleConfidentialInfo() {
        llConfidentialInfoLayout.setFocusable(true);
        llConfidentialInfoLayout.setFocusableInTouchMode(true);
        llConfidentialInfoLayout.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Confidential Info");
            showConfidentialInfoDisclosure();

        });
        llConfidentialInfoLayout.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
        llConfidentialInfoLayout.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "llConfidentialInfoLayout onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewConfidentialInfoHighlight, viewConfidentialInfoSeparator.getWidth());
                if (llConfidentialInfoLayout.canResolveLayoutDirection()) {
                    llConfidentialInfoLayout.performClick();
                }
            } else {
                if (tvConfidentialInfoInput.getText().equals("")) {
                    tvConfidentialInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else {
                    tvConfidentialInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                animateHighlightOut(viewConfidentialInfoHighlight, viewConfidentialInfoSeparator.getWidth());
            }
        });
    }

    private void handleSpinner() {
        //set adapter for medical center dropdown spinner
        Facility facility = sessionFacade.getPreferredFacility();
        List<MedicalCenter> medCenters = roiDetails.getFacilities();
        final List<String> medCenterArray = new ArrayList<>();
        for (int medCenter = 0; medCenter < medCenters.size(); medCenter++) {
            medCenterArray.add(medCenters.get(medCenter).value);
            if (medCenters.get(medCenter).key.equals(facility.getName())) {
                this.selection = medCenter;
            }
        }
        final SpinnerAdapter adapter = new SpinnerAdapter(context, R.layout.spinner_view_layout, medCenterArray);
        spMedicalCenter.setAdapter(adapter);
        spMedicalCenter.setSelection(this.selection);
        spMedicalCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
        spMedicalCenter.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "spMedicalCenter onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                animateHighlightIn(viewMedicalCenterHighlight, viewMedicalCenterSeparator.getWidth());
                if (spMedicalCenter.canResolveLayoutDirection()) {
                    spMedicalCenter.performClick();
                }
            } else {
                animateHighlightOut(viewMedicalCenterHighlight, viewMedicalCenterSeparator.getWidth());
            }
        });
        spMedicalCenter.setOnTouchListener((v, event) -> {
            dismissKeyboard();
            return false;
        });
    }

    public boolean formIsValid() {

        boolean isValid = true;

        boolean mainIsValid = validateMainSection();
        boolean datesOfServiceIsValid = validateDatesOfService();
        boolean infoToDiscloseIsValid = validateInfoToDisclose();
        boolean authIsValid = validateAuthorization();

        if (!mainIsValid || !datesOfServiceIsValid || !infoToDiscloseIsValid || !authIsValid) {
            isValid = false;
        }

        return isValid;
    }

    public boolean validateMainSection() {
        boolean mainIsValid = true;

        if (spMedicalCenter.getSelectedItemId() == -1) {
            mainIsValid = false;
            tvMedicalCenterLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.selectedFacility = getROIFacilityString((int) spMedicalCenter.getSelectedItemId());
            tvMedicalCenterLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if (etPatientFirstNameInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvPatientFirstNameLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.firstName = etPatientFirstNameInput.getText().toString();
            tvPatientFirstNameLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (etPatientLastNameInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvPatientLastNameLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.lastName = etPatientLastNameInput.getText().toString();
            tvPatientLastNameLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (tvDOBInput.getText().equals("")) {
            mainIsValid = false;
            tvDOBLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            // ROI Obj gets set when date is returned from DatePicker
            roiObj.dateOfBirth = tvDOBInput.getText().toString();
            tvDOBLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (tvDeliveryMethodInput.getText().equals("")) {
            mainIsValid = false;
            tvDeliveryMethodLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            // ROI Obj gets set when DeliveryMethod is returned from dialog fragment
            tvDeliveryMethodLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (llPickupDate.getVisibility() == View.VISIBLE) {
            if (tvPickupDateInput.getText().equals("")) {
                mainIsValid = false;
                tvPickupDateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                // ROI Obj gets set when PickupDate is returned from date picker
                tvPickupDateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        }
        if (tvAuthActionInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvAuthReleaseObtainLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            tvAuthReleaseObtainLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if (etAuthFacilityInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvAuthFacilityLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.facilityOrIndividual = etAuthFacilityInput.getText().toString();
            tvAuthFacilityLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (etAuthAddressInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvAuthAddressLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.address = etAuthAddressInput.getText().toString();
            tvAuthAddressLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (etAuthCityInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvAuthCityLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.city = etAuthCityInput.getText().toString();
            tvAuthCityLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (etAuthStateInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvAuthStateLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.state = etAuthStateInput.getText().toString();
            tvAuthStateLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (etAuthZipInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvAuthZipLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            roiObj.zip = etAuthZipInput.getText().toString();
            tvAuthZipLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (!TextUtils.isEmpty(etAuthPhoneInput.getText().toString())) {
            String phone = etAuthPhoneInput.getText().toString().replaceAll("[^0-9]", "");
            if (phone.length() >= 10) {
                phoneNoValid = true;
                tvAuthPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                roiObj.phoneNumber = etAuthPhoneInput.getText().toString();
            } else {
                mainIsValid = false;
                phoneNoValid = false;
                tvAuthPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            phoneNoValid = true;
            mainIsValid = false;
            tvAuthPhoneLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
        if (faxRequired || !etAuthFaxInput.getText().toString().isEmpty()) {
            if (!TextUtils.isEmpty(etAuthFaxInput.getText().toString())) {
                if (etAuthFaxInput.getText().toString().length() >= 10) {
                    faxNoValid = true;
                    tvAuthFaxLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    roiObj.fax = etAuthFaxInput.getText().toString();
                } else {
                    mainIsValid = false;
                    faxNoValid = false;
                    tvAuthFaxLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                }
            } else {
                mainIsValid = false;
                faxNoValid = true;
                tvAuthFaxLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            faxNoValid = true;
            tvAuthFaxLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if (emailRequired || !etAuthEmailInput.getText().toString().matches("")) {
            if (etAuthEmailInput.getText().toString().matches("")) {
                mainIsValid = false;
                emailIdValid = true;
                tvAuthEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else if (!isValidEmail(etAuthEmailInput.getText().toString())) {
                mainIsValid = false;
                emailIdValid = false;
                tvAuthEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                emailIdValid = true;
                roiObj.emailAddress = etAuthEmailInput.getText().toString();
                tvAuthEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        } else {
            emailIdValid = true;
            tvAuthEmailLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if (tvPurposeInput.getText().toString().matches("")) {
            mainIsValid = false;
            tvPurposeLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            // ROI Obj gets set when Purpose is returned from dialog fragment
            tvPurposeLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        return mainIsValid;
    }

    public boolean isPhoneNoValid() {
        return phoneNoValid;
    }

    public boolean isFaxNoValid() {
        return faxNoValid;
    }

    public boolean isEmailIdValid() {
        return emailIdValid;
    }

    public boolean validateDatesOfService() {
        boolean dosIsValid = true;

        if (TextUtils.isEmpty(tvDateOfServiceFrom.getText().toString())) {
            tvDatesOfServiceFrom.setTextColor(ContextCompat.getColor(context, R.color.red));
            dosIsValid = false;
        } else {
            tvDatesOfServiceFrom.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            roiObj.beginDate = getDatesOfServiceSpecificFromDate();
        }
        if (TextUtils.isEmpty(tvDateOfServiceTo.getText().toString())) {
            tvDatesOfServiceTo.setTextColor(ContextCompat.getColor(context, R.color.red));
            dosIsValid = false;
        } else {
            tvDatesOfServiceTo.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            roiObj.endDate = getDatesOfServiceSpecificToDate();
        }
        // Specific End Date must be greater or equal to specific Begin Date
        if (!TextUtils.isEmpty(tvDateOfServiceFrom.getText().toString()) && !TextUtils.isEmpty(tvDateOfServiceTo.getText().toString())) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

            try {
                Date end = sdf.parse(roiObj.endDate);
                Date begin = sdf.parse(roiObj.beginDate);

                if (end.before(begin)) {
                    dosIsValid = false;
                    tvDatesOfServiceFrom.setTextColor(ContextCompat.getColor(context, R.color.red));
                    tvDatesOfServiceTo.setTextColor(ContextCompat.getColor(context, R.color.red));
                    showDatesOfServiceOrderWarning();
                }
            } catch (ParseException e) {
                Log.e(TAG, "error " + e.getMessage());
            }
        }
        // Not really a validation since restrictions is not required
        if (!etDatesOfServiceRestrictionsInput.getText().toString().matches("")) {
            roiObj.restrictions = etAuthEmailInput.getText().toString();
        }

        return dosIsValid;
    }

    public boolean validateInfoToDisclose() {
        boolean discloseIsValid = true;

        if (tvGeneralInfoInput.getText().equals("")) {
            discloseIsValid = false;
            tvGeneralInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            tvGeneralInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if (tvConfidentialInfoInput.getText().equals("")) {
            discloseIsValid = false;
            tvConfidentialInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            tvConfidentialInfoLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        return discloseIsValid;
    }

    public boolean validateAuthorization() {
        boolean authIsValid = true;

        if (etAuthPatientPartyInput.getText().toString().matches("")) {
            authIsValid = false;
            tvAuthPatientPartyLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            tvAuthPatientPartyLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            roiObj.signature = etAuthPatientPartyInput.getText().toString();
        }
        // Not really a validation since relation is not required
        if (!etAuthPatientPartyInput.getText().toString().matches("")) {
            roiObj.patientRelation = etAuthPatientPartyInput.getText().toString();
        }

        return authIsValid;
    }

    public void showDatePickerDialog(String purpose, boolean addMax, boolean addMin) {
        Date previouslySelected;
        MoreFormsLibraryActivity activity = (MoreFormsLibraryActivity) context;
        MoreDatePickerFragment datePickerFragment = MoreDatePickerFragment.newInstance(activity, purpose, addMax, addMin);
        datePickerFragment.show(activity.getSupportFragmentManager(), "datePicker");
        switch (purpose) {
            case DATE_OF_BIRTH:
                previouslySelected = this.previousDob;
                break;
            case PICKUP_DATE:
                previouslySelected = this.previousPickupDate;
                break;
            case BEGINNING_DATE_OF_SERVICE:
                previouslySelected = this.previousBeginDateOfService;
                break;
            case ENDING_DATE_OF_SERVICE:
                previouslySelected = this.previousEndDateOfService;
                break;
            default:
                previouslySelected = new Date();
        }
        datePickerFragment.setPreviouslySelected(previouslySelected);
        dismissKeyboard();
    }

    public void setDateFromDatePicker(Date date, String purpose) {
        //set selected dates
        switch (purpose) {
            case DATE_OF_BIRTH:
                this.previousDob = date;
                break;
            case PICKUP_DATE:
                this.previousPickupDate = date;
                break;
            case BEGINNING_DATE_OF_SERVICE:
                this.previousBeginDateOfService = date;
                break;
            case ENDING_DATE_OF_SERVICE:
                this.previousEndDateOfService = date;
                break;
            default:
                break;
        }

        Log.d(TAG, "MoreReleaseOfInfoFragment setDOBDate: " + date + "::" + purpose);
        String mdyDateStr = "";

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        if (date != null) {
            mdyDateStr = sdf.format(date);
        }
        if (purpose.equals(DATE_OF_BIRTH)) {
            tvDOBInput.setText(mdyDateStr);
            roiObj.dateOfBirth = mdyDateStr;
        }
        if (purpose.equals(PICKUP_DATE)) {
            tvPickupDateInput.setText(mdyDateStr);
            roiObj.pickupDate = mdyDateStr;
        }
        if (purpose.equals(BEGINNING_DATE_OF_SERVICE)) {
            Log.d(TAG, "mdyDateStr: " + mdyDateStr + ".");
            tvDateOfServiceFrom.setText(mdyDateStr);
            roiObj.beginDate = mdyDateStr;
        }
        if (purpose.equals(ENDING_DATE_OF_SERVICE)) {
            tvDateOfServiceTo.setText(mdyDateStr);
            roiObj.endDate = mdyDateStr;
        }
    }

    public void cancelDateFromDatePicker() {
        Log.d(TAG, "cancelDateFromDatePicker");
    }

    public void showDeliveryMethodDialog() {
        MoreFormsLibraryActivity activity = (MoreFormsLibraryActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        DeliveryMethodDiscloseDialogFragment dialog = DeliveryMethodDiscloseDialogFragment.newInstance(activity);
        dialog.setPreviouslySelected(roiSelectedDeliveryMethod);
        dialog.show(manager, "");
        dismissKeyboard();
    }

    public void setDeliveryMethod(Map<String, Boolean> deliveryMethod) {

        Log.d(TAG, "setDeliveryMethod: deliveryMethod: " + deliveryMethod);
        String deliveryMethodTxt = "";
        roiSelectedDeliveryMethod.clear();
        for (String key : deliveryMethod.keySet()) {
            if (deliveryMethod.get(key)) {
                // Build String for display
                if (deliveryMethodTxt.equals("")) {
                    deliveryMethodTxt = key;
                } else {
                    deliveryMethodTxt = (new StringBuilder(deliveryMethodTxt)).append(", ").append(key).toString();
                }
                // Build array for ROI
                roiSelectedDeliveryMethod.add(key);
            }
        }
        faxRequired = deliveryMethodTxt.contains("Fax");
        emailRequired = deliveryMethodTxt.contains("Email");

        if (faxRequired && !emailRequired) {
            etAuthFaxInput.setHint(context.getString(R.string.input_text_placeholder));
            etAuthEmailInput.setHint("");
        } else if (emailRequired && !faxRequired) {
            etAuthEmailInput.setHint(context.getString(R.string.input_text_placeholder));
            etAuthFaxInput.setHint("");
        } else if (faxRequired && emailRequired) {
            etAuthFaxInput.setHint(context.getString(R.string.input_text_placeholder));
            etAuthEmailInput.setHint(context.getString(R.string.input_text_placeholder));
        } else {
            etAuthEmailInput.setHint("");
            etAuthFaxInput.setHint("");
        }

        if (emailRequired)
            etAuthEmailInput.setHint(context.getString(R.string.input_text_placeholder));

        displayPickup(deliveryMethodTxt.contains("Pick-up"));
        roiObj.selectedDeliveryMethod = roiSelectedDeliveryMethod;
        tvDeliveryMethodInput.setText(deliveryMethodTxt);
    }

    public void displayPickup(boolean visible) {
        if (visible) {
            llPickupDate.setVisibility(View.VISIBLE);
            llPickupDate.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);
        } else {
            llPickupDate.animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            llPickupDate.setVisibility(View.GONE);
                        }
                    });
        }
    }

    public void showPurposeDialog() {
        MoreFormsLibraryActivity activity = (MoreFormsLibraryActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        PurposeDiscloseDialogFragment dialog = PurposeDiscloseDialogFragment.newInstance(activity);
        dialog.setPreviouslySelected(roiSelectedPurposes);
        dialog.show(manager, "");
        dismissKeyboard();
    }

    public void showAuthActionDialog() {
        MoreFormsLibraryActivity activity = (MoreFormsLibraryActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        RoiAuthorizationDialogFragment dialog = RoiAuthorizationDialogFragment.newInstance(activity);
        dialog.setPreviouslySelected(roiSelectedAuthActions);
        dialog.show(manager, "");
        dismissKeyboard();
    }

    public ROI getRoiObj() {
        return roiObj;
    }

    public void setPurpose(Map<String, Boolean> purposes) {

        String purposesTxt = "";
        roiSelectedPurposes.clear();
        for (Map.Entry<String, Boolean> entry : purposes.entrySet()) {
            if (entry.getValue()) {
                // Build String for display
                if (purposesTxt.equals("")) {
                    purposesTxt = entry.getKey();
                } else {
                    purposesTxt = (new StringBuilder(purposesTxt)).append(", ").append(entry.getKey()).toString();
                }
                // Build array for ROI
                roiSelectedPurposes.add(entry.getKey());
            }
        }
        roiObj.selectedPurposes = roiSelectedPurposes;
        tvPurposeInput.setText(purposesTxt);
    }

    public void setAuthAction(Map<String, Boolean> authActions) {
        String authActionTxt = "";

        roiSelectedAuthActions.clear();
        for (Map.Entry<String, Boolean> entry : authActions.entrySet()) {
            if (entry.getValue()) {
                // Build String for display
                if (authActionTxt.equals("")) {
                    authActionTxt = entry.getKey().substring(0, entry.getKey().length() - 1);
                } else {
                    authActionTxt = (new StringBuilder(authActionTxt)).append(", ").append(entry.getKey().substring(0, entry.getKey().length() - 1)).toString();
                }
                // Build array for ROI
                roiSelectedAuthActions.add(entry.getKey());
            }
        }
        roiObj.selectedAuthorizationAction = roiSelectedAuthActions;
        tvAuthActionInput.setText(authActionTxt);
    }

    public void showGeneralInfoDisclosure() {
        MoreFormsLibraryActivity activity = (MoreFormsLibraryActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        GeneralInfoDiscloseDialogFragment dialog = GeneralInfoDiscloseDialogFragment.newInstance(activity);
        dialog.setPreviouslySelected(roiSelectedInfo, generalInfoOther);
        dialog.show(manager, "");
        dismissKeyboard();
    }

    public void setGeneralInfoInput(Map<String, Boolean> generalInfoAR, String generalInfoOther) {

        String generalInfoTxt = "";

        roiSelectedInfo.clear();
        for (Map.Entry<String, Boolean> entry : generalInfoAR.entrySet()) {
            if (entry.getValue()) {
                // Build String for display
                if (generalInfoTxt.equals("")) {
                    generalInfoTxt = entry.getKey();
                } else {
                    generalInfoTxt = (new StringBuilder(generalInfoTxt)).append(", ").append(entry.getKey()).toString();
                }
                // Build array for ROI
                roiSelectedInfo.add(entry.getKey());
            }
        }

        if (!TextUtils.isEmpty(generalInfoOther)) {
            if (generalInfoTxt.equals("")) {
                generalInfoTxt = generalInfoOther;
            } else {
                generalInfoTxt = generalInfoTxt + ", " + generalInfoOther;
            }
            roiObj.disclosureInformationOther = generalInfoOther;
            this.generalInfoOther = generalInfoOther;
        } else {
            this.generalInfoOther = "";
        }

        tvGeneralInfoInput.setText(generalInfoTxt);
        roiObj.selectedDisclosureInformation = roiSelectedInfo;
    }

    public void showConfidentialInfoDisclosure() {
        dismissKeyboard();
        MoreFormsLibraryActivity activity = (MoreFormsLibraryActivity) context;
        FragmentManager manager = activity.getSupportFragmentManager();
        ConfidentialInfoDiscloseDialogFragment dialog = ConfidentialInfoDiscloseDialogFragment.newInstance(activity);
        dialog.setPreviouslySelected(roiSelectedConfidentialInfo);
        dialog.show(manager, "");
    }

    public void setConfidentialInfoInputText(Map<String, Boolean> confidentialInfoAR) {

        String confidentialInfoTxt = "";
        String restrictionsText = "";

        roiSelectedConfidentialInfo.clear();
        for (Map.Entry<String, Boolean> entry : confidentialInfoAR.entrySet()) {
            if (entry.getValue()) {
                // Build String for display
                if (confidentialInfoTxt.equals("")) {
                    confidentialInfoTxt = entry.getKey();
                } else {
                    confidentialInfoTxt = (new StringBuilder(confidentialInfoTxt)).append(", ").append(entry.getKey()).toString();
                }
                // Build array for ROI
                roiSelectedConfidentialInfo.add(entry.getKey());
            } else {
                if (restrictionsText.equals("")) {
                    restrictionsText = entry.getKey();
                } else {
                    restrictionsText = (new StringBuilder(restrictionsText)).append(", ").append(entry.getKey()).toString();
                }
            }
        }

        tvConfidentialInfoInput.setText(confidentialInfoTxt);
        etDatesOfServiceRestrictionsInput.setText(restrictionsText);
        roiObj.selectedHighlyConfidentialDiscolosureInformation = roiSelectedConfidentialInfo;
    }

    public String getROIFacilityString(int idx) {
        return roiDetails.getFacilities().get(idx).key;
    }

    private void dismissKeyboard() {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

        if (imm != null && ((MoreFormsLibraryActivity) context).getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(((MoreFormsLibraryActivity) context).getCurrentFocus().getWindowToken(), 0);
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

    private String getDatesOfServiceSpecificFromDate() {
        return tvDateOfServiceFrom.getText().toString();
    }

    private String getDatesOfServiceSpecificToDate() {
        return tvDateOfServiceTo.getText().toString();
    }

    private void showDatesOfServiceOrderWarning() {
        Snackbar snackbar = Snackbar.make(mView, R.string.roi_dates_of_service_to_less_than_from, LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_white_24dp, 0, 0, 0);
        textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding));
        snackbar.show();
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MoreReleaseOfInfoFormFragment:showRequestFailure", CTCAAnalyticsConstants.EXCEPTION_REST_API, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyFetchError(String errMessage) {
        if (context != null)
            ((MoreFormsLibraryActivity) context).hideActivityIndicator();
        showRequestFailure(errMessage);
        prepareView();
    }

    @Override
    public void notifyFetchDetails(RoiDetails roiDetails) {
        downloadUserDetails();
    }

    private void setPhoneEditTextLength(int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        etAuthPhoneInput.setFilters(filterArray);
    }

    @Override
    public void notifyFetchSuccess(String purpose) {
        if (context != null)
            ((MoreFormsLibraryActivity) context).hideActivityIndicator();
        prepareView();
        etAuthPhoneInput.setText(sessionFacade.getUserContactNumber());
        if(sessionFacade.getUserContactNumber().length() == 10)
            setPhoneEditTextLength(14);
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        if (context != null)
            ((MoreFormsLibraryActivity) context).hideActivityIndicator();
    }
}
