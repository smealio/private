package com.myctca.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.MoreMedicalDocActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.MedDocType;
import com.myctca.model.UserPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreMedicalDocFragment extends Fragment {

    private static final String TAG = "myCTCA-MOREMEDDOC";

    private int medDocDisabledButtonCount = 0;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (AppSessionManager.getInstance().getUserProfile() == null) {
            ((MoreMedicalDocActivity) context).getFragmentManager().popBackStackImmediate();
            return null;
        } else {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_more_medical_doc, container, false);

            Button mCarePlanButton = view.findViewById(R.id.med_doc_care_plan_button);
            if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.VIEW_CARE_PLAN)) {
                mCarePlanButton.setVisibility(View.VISIBLE);
                mCarePlanButton.setOnClickListener(v -> carePlan());
            } else {
                mCarePlanButton.setVisibility(View.GONE);
                medDocDisabledButtonCount++;
            }

            Button mClinicalSummaryButton = view.findViewById(R.id.med_doc_clinical_summary_button);
            if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.VIEW_CCDA_DOCUMENTS)) {
                mClinicalSummaryButton.setVisibility(View.VISIBLE);
                mClinicalSummaryButton.setOnClickListener(v -> clinicalSummary());
            } else {
                mClinicalSummaryButton.setVisibility(View.GONE);
                medDocDisabledButtonCount++;
            }

            Button mClinicalButton = view.findViewById(R.id.med_doc_clinical_button);
            if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.VIEW_CLINICAL_DOCUMENTS)) {
                mClinicalButton.setVisibility(View.VISIBLE);
                mClinicalButton.setOnClickListener(v -> medDocList(MedDocType.CLINICAL));
            } else {
                mClinicalButton.setVisibility(View.GONE);
                medDocDisabledButtonCount++;
            }

            Button mRadiationButton = view.findViewById(R.id.med_doc_radiation_button);
            if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.VIEW_RADIATION_LINKS)) {
                mRadiationButton.setVisibility(View.VISIBLE);
                mRadiationButton.setOnClickListener(v -> medDocList(MedDocType.RADIATION));
            } else {
                mRadiationButton.setVisibility(View.GONE);
                medDocDisabledButtonCount++;
            }

            Button mImagingButton = view.findViewById(R.id.med_doc_imaging_button);
            if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.VIEW_IMAGING_DOCUMENTS)) {
                mImagingButton.setVisibility(View.VISIBLE);
                mImagingButton.setOnClickListener(v -> imagingDocList());
            } else {
                mImagingButton.setVisibility(View.GONE);
                medDocDisabledButtonCount++;
            }

            Button mIntegrativeButton = view.findViewById(R.id.med_doc_integrative_button);
            if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.VIEW_INTEGRATIVE_DOCUMENTS)) {
                mIntegrativeButton.setVisibility(View.VISIBLE);
                mIntegrativeButton.setOnClickListener(v -> medDocList(MedDocType.INTEGRATIVE));
            } else {
                mIntegrativeButton.setVisibility(View.GONE);
                medDocDisabledButtonCount++;
            }
            int medDocButtonCount = 6;
            if (medDocButtonCount == medDocDisabledButtonCount) {
                View mEmptyView = view.findViewById(R.id.med_doc_empty_view);
                mEmptyView.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    private void carePlan() {

        MoreMedicalDocActivity parent = (MoreMedicalDocActivity) context;
        parent.showCarePlan();
    }

    private void clinicalSummary() {
        Log.d(TAG, "CLINICAL SUMMARY");
        MoreMedicalDocActivity parent = (MoreMedicalDocActivity) context;
        parent.showClinicalSummary();
    }

    private void medDocList(String medDocType) {
        Log.d(TAG, "MED DOC LIST");
        MoreMedicalDocActivity parent = (MoreMedicalDocActivity) context;
        parent.showMedDocList(medDocType);
    }

    private void imagingDocList() {
        Log.d(TAG, "IMAGING DOC LIST");
        MoreMedicalDocActivity parent = (MoreMedicalDocActivity) context;
        parent.showImagingDocList();
    }

}
