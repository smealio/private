package com.myctca.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreFormsLibraryActivity;
import com.myctca.adapter.SpinnerAdapter;
import com.myctca.common.AppSessionManager;
import com.myctca.model.AnncForm;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.Facility;
import com.myctca.model.MedicalCenter;
import com.myctca.service.MoreFormsLibraryService;
import com.myctca.service.SessionFacade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MoreAnncFormFragment extends Fragment implements
        MoreFormsLibraryService.MoreFormsLibraryANNCListenerGet {

    private static final String TAG = MoreAnncFormFragment.class.getSimpleName();
    private static final String DATE_OF_SERVICE = "DATE_OF_SERVICE";
    private static final String DATE_SIGNED = "DATE_SIGNED";
    private static final String PURPOSE_FACILITY_LIST = "FACILITY_LIST";
    private static final String PURPOSE_MRN = "MRN";
    private AnncForm anncFormObject;
    private TextView anncPatientInfoTitle;
    private EditText patientFirstnameEt;
    private EditText patientLastnameEt;
    private TextView mrnEt;
    private TextView tvDateOfService;
    private TextView anncTitle;
    private TextView anncOptionsTitle;
    private Spinner spMedicalCenter;
    private int selection = 0;
    private CheckBox cbNeedTelehealthServices;
    private CheckBox cbDontNeedTelehealthServices;
    private LinearLayout llDateOfService;
    private EditText etInsuranceName;
    private EditText patientResponsiblePartyInput;
    private EditText patientRelationInput;
    private TextView anncDateSignedText;
    private LinearLayout llDateSigned;
    private TextView insuranceNameTv;
    private TextView patientLastnameTv;
    private TextView patientFirstnameTv;
    private TextView anncMrnTitle;
    private TextView anncDateOfService;
    private TextView moreAnncPatientPartyTitle;
    private TextView anncDateSignedLabel;
    private Date previousDateOfService;
    private Date previousDateSigned;
    private SessionFacade sessionFacade;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_annc_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        initializeViews(view);
        getMrn();
        prepareView();
    }

    private void initializeViews(View view) {
        //initialize annc obj
        anncFormObject = new AnncForm();
        //initialize titles
        anncPatientInfoTitle = view.findViewById(R.id.annc_patient_info_title);
        anncTitle = view.findViewById(R.id.annc_title);
        anncOptionsTitle = view.findViewById(R.id.annc_options_title);

        //initialize labels
        insuranceNameTv = view.findViewById(R.id.insurance_name_tv);
        patientLastnameTv = view.findViewById(R.id.patient_lastname_tv);
        patientFirstnameTv = view.findViewById(R.id.patient_firstname_tv);
        anncMrnTitle = view.findViewById(R.id.annc_mrn_title);
        anncDateOfService = view.findViewById(R.id.annc_date_of_service);
        moreAnncPatientPartyTitle = view.findViewById(R.id.more_annc_patient_party_title);
        anncDateSignedLabel = view.findViewById(R.id.annc_date_label);

        //initialize edit texts
        spMedicalCenter = view.findViewById(R.id.medical_center_spinner);
        etInsuranceName = view.findViewById(R.id.insurance_et);
        patientFirstnameEt = view.findViewById(R.id.patient_firstname_et);
        patientLastnameEt = view.findViewById(R.id.patient_lastname_et);
        mrnEt = view.findViewById(R.id.mrn_input);
        tvDateOfService = view.findViewById(R.id.dos_input);
        patientResponsiblePartyInput = view.findViewById(R.id.patient_responsible_party_input);
        patientRelationInput = view.findViewById(R.id.patient_relation_input);
        anncDateSignedText = view.findViewById(R.id.annc_date_signed_text);

        //initialize checkboxes
        cbNeedTelehealthServices = view.findViewById(R.id.cb_need_telehealth);
        cbDontNeedTelehealthServices = view.findViewById(R.id.cb_dont_need_telehealth);

        //initialize linear layouts
        llDateOfService = view.findViewById(R.id.ll_date_of_service);
        llDateSigned = view.findViewById(R.id.ll_date_signed);
        setFacilityList(AppSessionManager.getInstance().getFacilityAll(), AppSessionManager.getInstance().getPreferredFacility());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_annc, menu);
        ((MoreFormsLibraryActivity) context).setToolBar(context.getString(R.string.adv_notice_title));

        // Tint the Send Button in the Menu
        Drawable drawable = menu.findItem(R.id.toolbar_annc_send).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.colorAccent));
        menu.findItem(R.id.toolbar_annc_send).setIcon(drawable);
    }

    private void prepareView() {
        //set titles
        anncPatientInfoTitle.setText(context.getString(R.string.roi_patient_info_title));
        anncTitle.setText(context.getString(R.string.adv_notice_title));
        anncOptionsTitle.setText(context.getString(R.string.annc_options_title));

        //Main Sections
        //insurance
        etInsuranceName.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientInsuranceInput onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                changePriorityOfField(insuranceNameTv, etInsuranceName.getText().toString());
            }
        });

        //first name
        patientFirstnameEt.setText(sessionFacade.getMyCtcaUserProfile().getFirstName());
        patientFirstnameEt.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientFirstNameInput onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                changePriorityOfField(patientFirstnameTv, patientFirstnameEt.getText().toString());
            }
        });

        //last name
        patientLastnameEt.setText(sessionFacade.getMyCtcaUserProfile().getLastName());
        patientLastnameEt.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientLastNameInput onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                changePriorityOfField(patientLastnameTv, patientLastnameEt.getText().toString());
            }
        });

        //mrn
        mrnEt.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientMRNInput onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                changePriorityOfField(anncMrnTitle, mrnEt.getText().toString());
            }
        });

        //patient responsible party
        patientResponsiblePartyInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "etPatientPartyInput onFocusChange hasFocus: " + hasFocus);
            if (!hasFocus) {
                changePriorityOfField(moreAnncPatientPartyTitle, patientResponsiblePartyInput.getText().toString());
            }
        });

        //date of service
        llDateOfService.setFocusable(true);
        llDateOfService.setFocusableInTouchMode(true);
        llDateOfService.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Date of Service");
            showDatePickerDialog(DATE_OF_SERVICE, false, true);

        });
        llDateOfService.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                if (llDateOfService.canResolveLayoutDirection()) {
                    llDateOfService.performClick();
                }
            } else {
                changePriorityOfField(anncDateOfService, tvDateOfService.getText().toString());
            }
        });

        //date signed
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String anncSignedDate = sdf.format(date);
        anncFormObject.setDateSigned(anncSignedDate);
        anncDateSignedText.setText(anncSignedDate);
        llDateSigned.setFocusable(true);
        llDateSigned.setFocusableInTouchMode(true);
        llDateSigned.setOnClickListener(v -> {
            Log.d(TAG, "Got A Click Date Signed");
            showDatePickerDialog(DATE_SIGNED, false, false);

        });
        llDateSigned.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "onFocusChange hasFocus: " + hasFocus);
            if (hasFocus) {
                if (llDateSigned.canResolveLayoutDirection()) {
                    llDateSigned.performClick();
                }
            } else {
                changePriorityOfField(anncDateSignedLabel, anncDateSignedText.getText().toString());
            }
        });

        //payment options
        setCheckboxColor(R.color.colorPrimary);

        cbNeedTelehealthServices.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                cbDontNeedTelehealthServices.setChecked(false);
                setCheckboxColor(R.color.colorPrimary);
            }
        });
        cbDontNeedTelehealthServices.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                cbNeedTelehealthServices.setChecked(false);
                setCheckboxColor(R.color.colorPrimary);
            }
        });
    }

    private void getMrn() {
        if (context != null)
            ((MoreFormsLibraryActivity) context).showActivityIndicator(context.getString(R.string.more_annc_records_retrieving));
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_mrn);
        sessionFacade.getANNCFormInfo(this, url, context, PURPOSE_MRN);
    }

    private void setMrn(String response) {
        mrnEt.setText(response);
        anncFormObject.setMrn(response);
    }

    private void setFacilityList(List<MedicalCenter> facilityList, Facility preferredFacility) {
        //set adapter for medical center dropdown spinner
        List<String> medCenterArray = new ArrayList<>();
        for (int medCenter = 0; medCenter < facilityList.size(); medCenter++) {
            medCenterArray.add(facilityList.get(medCenter).value);
            if (facilityList.get(medCenter).key.equals(preferredFacility.getName())) {
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
    }

    public AnncForm getAnncFormObject() {
        return anncFormObject;
    }

    private void setCheckboxColor(int color) {
        cbNeedTelehealthServices.setButtonTintList(ContextCompat.getColorStateList(context, color));
        cbDontNeedTelehealthServices.setButtonTintList(ContextCompat.getColorStateList(context, color));
    }

    public void showDatePickerDialog(String purpose, boolean addMax, boolean addMin) {
        Date previouslySelected;
        MoreFormsLibraryActivity activity = (MoreFormsLibraryActivity) context;
        MoreDatePickerFragment datePickerFragment = MoreDatePickerFragment.newInstance(activity, purpose, addMax, addMin);
        datePickerFragment.show(activity.getSupportFragmentManager(), "datePicker");
        switch (purpose) {
            case DATE_OF_SERVICE:
                previouslySelected = this.previousDateOfService;
                break;
            case DATE_SIGNED:
                previouslySelected = this.previousDateSigned;
                break;
            default:
                previouslySelected = new Date();
        }
        datePickerFragment.setPreviouslySelected(previouslySelected);
    }


    public boolean formIsValid() {
        boolean isValid = true;

        //selected facility name
        if (spMedicalCenter.getSelectedItemId() == -1) {
            isValid = false;
        } else {
            anncFormObject.setFacilityName(AppSessionManager.getInstance().getFacilityAll().get((int) spMedicalCenter.getSelectedItemId()).key);
        }

        //patient name
        if (patientFirstnameEt.getText().toString().matches("")) {
            isValid = false;
            changePriorityOfField(patientFirstnameTv, patientFirstnameEt.getText().toString());
        } else {
            changePriorityOfField(patientFirstnameTv, patientFirstnameEt.getText().toString());
        }
        if (patientLastnameEt.getText().toString().matches("")) {
            isValid = false;
            changePriorityOfField(patientLastnameTv, patientLastnameEt.getText().toString());
        } else {
            changePriorityOfField(patientLastnameTv, patientLastnameEt.getText().toString());
        }
        if (!patientFirstnameEt.getText().toString().matches("") && !patientLastnameEt.getText().toString().matches("")) {
            anncFormObject.setPatientName(patientFirstnameEt.getText().toString() + " " + patientLastnameEt.getText().toString());
        }

        //insurance
        if (etInsuranceName.getText().toString().matches("")) {
            isValid = false;
            changePriorityOfField(insuranceNameTv, etInsuranceName.getText().toString());
        } else {
            anncFormObject.setInsuranceName(etInsuranceName.getText().toString());
            changePriorityOfField(insuranceNameTv, etInsuranceName.getText().toString());
        }

        //mrn
        if (mrnEt.getText().toString().matches("")) {
            isValid = false;
            changePriorityOfField(anncMrnTitle, mrnEt.getText().toString());
        } else {
            anncFormObject.setMrn(mrnEt.getText().toString());
            changePriorityOfField(anncMrnTitle, mrnEt.getText().toString());
        }
        //date of service
        if (tvDateOfService.getText().toString().matches("")) {
            isValid = false;
            changePriorityOfField(anncDateOfService, tvDateOfService.getText().toString());
        } else {
            anncFormObject.setDateOfService(tvDateOfService.getText().toString());
            changePriorityOfField(anncDateOfService, tvDateOfService.getText().toString());
        }

        //payment option
        if (cbNeedTelehealthServices.isChecked()) {
            anncFormObject.setPaymentOption("option1");
            setCheckboxColor(R.color.colorPrimary);
        } else if (cbDontNeedTelehealthServices.isChecked()) {
            anncFormObject.setPaymentOption("option2");
            setCheckboxColor(R.color.colorPrimary);
        } else {
            isValid = false;
            setCheckboxColor(R.color.red);
        }

        //patient responsible party & relation
        if (patientResponsiblePartyInput.getText().toString().matches("")) {
            isValid = false;
            changePriorityOfField(moreAnncPatientPartyTitle, patientResponsiblePartyInput.getText().toString());
        } else {
            anncFormObject.setPatientSignature(patientResponsiblePartyInput.getText().toString());
            changePriorityOfField(moreAnncPatientPartyTitle, patientResponsiblePartyInput.getText().toString());
        }
        anncFormObject.setResponsibleParty(patientRelationInput.getText().toString());

        //date signed
        if (anncDateSignedText.getText().toString().matches("")) {
            isValid = false;
            changePriorityOfField(anncDateSignedLabel, anncDateSignedText.getText().toString());
        } else {
            anncFormObject.setDateSigned(anncDateSignedText.getText().toString());
            changePriorityOfField(anncDateSignedLabel, anncDateSignedText.getText().toString());
        }

        return isValid;
    }

    private void changePriorityOfField(TextView inputFieldLabel, String inputFieldText) {
        if (inputFieldText.isEmpty()) {
            inputFieldLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            inputFieldLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
    }

    public void setDateFromDatePicker(Date date, String purpose) {
        if (DATE_OF_SERVICE.equals(purpose)) {
            this.previousDateOfService = date;
        } else if (DATE_SIGNED.equals(purpose)) {
            this.previousDateSigned = date;
        }

        String mdyDateStr = "";

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        if (date != null) {
            mdyDateStr = sdf.format(date);
        }

        if (purpose.equals(DATE_OF_SERVICE)) {
            tvDateOfService.setText(mdyDateStr);
            anncFormObject.setDateOfService(mdyDateStr);
        } else if (purpose.equals(DATE_SIGNED)) {
            anncDateSignedText.setText(mdyDateStr);
            anncFormObject.setDateSigned(mdyDateStr);
        }
    }

    private void showRequestFailure(String message) {
        CTCAAnalyticsManager.createEvent("MoreAnncFormFragment:showRequestFailure", CTCAAnalyticsConstants.ALERT_ANNC_REQUEST_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void ifANNCExists(boolean anncExists) {
        //do nothing on this screen. This is used in previous screen.
    }

    @Override
    public void notifyMrn(String response) {
        if (context != null)
            ((MoreFormsLibraryActivity) context).hideActivityIndicator();
        setMrn(response);
    }

    @Override
    public void notifyFetchError(String errMessage) {
        if (context != null)
            ((MoreFormsLibraryActivity) context).hideActivityIndicator();
        showRequestFailure(errMessage);
    }
}