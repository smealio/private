package com.myctca.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.MoreHealthHistoryActivity;
import com.myctca.model.UserPermissions;
import com.myctca.service.SessionFacade;

public class MoreHealthHistoryFragment extends Fragment {

    private int healthHistoryDisabledButtonCount = 0;
    private Button mVitalsButton;
    private Button mPrescriptionsButton;
    private Button mAllergiesButton;
    private Button mImmunizationsButton;
    private Button mHealthIssuesButton;
    private View mEmptyView;
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
        return inflater.inflate(R.layout.fragment_more_health_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        mVitalsButton = view.findViewById(R.id.health_history_vitals_button);
        mPrescriptionsButton = view.findViewById(R.id.health_history_prescriptions_button);
        mAllergiesButton = view.findViewById(R.id.health_history_allergies_button);
        mImmunizationsButton = view.findViewById(R.id.health_history_immunizations_button);
        mHealthIssuesButton = view.findViewById(R.id.health_history_health_issues_button);
        mEmptyView = view.findViewById(R.id.health_history_empty_view);
        setHealthHistoryTypeButtons();
    }

    private void setHealthHistoryTypeButtons() {
        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_VITAL_SIGNS)) {
            mVitalsButton.setVisibility(View.VISIBLE);
            mVitalsButton.setOnClickListener(v -> vitals());
        } else {
            mVitalsButton.setVisibility(View.GONE);
            healthHistoryDisabledButtonCount++;
        }

        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_PRESCRIPTIONS)) {
            mPrescriptionsButton.setVisibility(View.VISIBLE);
            mPrescriptionsButton.setOnClickListener(v -> prescriptions());
        } else {
            mPrescriptionsButton.setVisibility(View.GONE);
            healthHistoryDisabledButtonCount++;
        }

        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_ALLERGIES)) {
            mAllergiesButton.setVisibility(View.VISIBLE);
            mAllergiesButton.setOnClickListener(v -> allergies());
        } else {
            mAllergiesButton.setVisibility(View.GONE);
            healthHistoryDisabledButtonCount++;
        }

        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_IMMUNIZATIONS)) {
            mImmunizationsButton.setVisibility(View.VISIBLE);
            mImmunizationsButton.setOnClickListener(v -> immunizations());
        } else {
            mImmunizationsButton.setVisibility(View.GONE);
            healthHistoryDisabledButtonCount++;
        }

        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_HEALTH_ISSUES)) {
            mHealthIssuesButton.setVisibility(View.VISIBLE);
            mHealthIssuesButton.setOnClickListener(v -> healthIssues());
        } else {
            mHealthIssuesButton.setVisibility(View.GONE);
            healthHistoryDisabledButtonCount++;
        }

        if (healthHistoryDisabledButtonCount == 5) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void vitals() {
        MoreHealthHistoryActivity parent = (MoreHealthHistoryActivity) context;
        parent.showVitals();
    }

    private void prescriptions() {
        MoreHealthHistoryActivity parent = (MoreHealthHistoryActivity) context;
        parent.showPrescriptions();
    }

    private void allergies() {
        MoreHealthHistoryActivity parent = (MoreHealthHistoryActivity) context;
        parent.showAllergies();
    }

    private void immunizations() {
        MoreHealthHistoryActivity parent = (MoreHealthHistoryActivity) context;
        parent.showImmunizations();
    }

    private void healthIssues() {
        MoreHealthHistoryActivity parent = (MoreHealthHistoryActivity) context;
        parent.showHealthIssues();
    }

}
