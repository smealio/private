package com.myctca.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.DisplayWebViewActivity;
import com.myctca.activity.NavActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.common.fingerprintauth.FingerprintHandler;
import com.myctca.model.MyCTCAProxy;
import com.myctca.model.UserPermissions;
import com.myctca.model.UserType;
import com.myctca.service.SessionFacade;
import com.myctca.util.GeneralUtil;

import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {

    private static final String TAG = "myCTCA-MORE";

    private int mainSectionDisabledButtonCount = 0;
    private MoreFragmentInteractionListener listener;
    private Context context;

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (MoreFragmentInteractionListener) context;
            this.context = context;
        } catch (ClassCastException exception) {
            Log.e(TAG, "exception: " + exception);
        }
    }

    public boolean isProxyUser() {
        SessionFacade sessionFacade = new SessionFacade();
        List<MyCTCAProxy> proxies = sessionFacade.getProxies();
        boolean isImpersonating = false;
        for (MyCTCAProxy proxy : proxies) {
            if (sessionFacade.getMyCtcaUserProfile().getCtcaId().equals(proxy.getToCtcaUniqueId())) {
                isImpersonating = proxy.isImpersonating();
            }
        }
        return !isImpersonating;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionFacade sessionFacade = new SessionFacade();

        // Main Section
        CardView mMainSectionCard = view.findViewById(R.id.main_section_card);
        LinearLayout mMedDocsLayout = view.findViewById(R.id.medical_docs_layout);

        if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER
                && !isProxyUser()) {
            mMedDocsLayout.setVisibility(View.GONE);
        } else {
            mMedDocsLayout.setVisibility(View.VISIBLE);
            mMedDocsLayout.setOnClickListener(v -> {
                if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_MEDICAL_DOCUMENTS)) {
                    if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_CARE_PLAN)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_CCDA_DOCUMENTS)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_INTEGRATIVE_DOCUMENTS)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_IMAGING_DOCUMENTS)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_RADIATION_LINKS)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_CLINICAL_DOCUMENTS))
                        medDocs();
                    else
                        ((NavActivity) context).showPermissionAlert();
                } else
                    ((NavActivity) context).showPermissionAlert();
            });
        }

        LinearLayout mHealthHistoryLayout = view.findViewById(R.id.health_history_layout);
        if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER
                && !isProxyUser()) {
            mHealthHistoryLayout.setVisibility(View.GONE);
        } else {
            mHealthHistoryLayout.setVisibility(View.VISIBLE);
            mHealthHistoryLayout.setOnClickListener(v -> {
                if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_HEALTH_HISTORY)) {
                    if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_VITAL_SIGNS)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_PRESCRIPTIONS)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_ALLERGIES)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_IMMUNIZATIONS)
                            || sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_HEALTH_ISSUES))
                        healthHistory();
                    else
                        ((NavActivity) context).showPermissionAlert();
                } else
                    ((NavActivity) context).showPermissionAlert();
            });
        }

        LinearLayout patientReportedLayout = view.findViewById(R.id.patient_reported_layout);
        if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER
                && !isProxyUser()) {
            patientReportedLayout.setVisibility(View.GONE);
        } else {
            patientReportedLayout.setVisibility(View.VISIBLE);
            patientReportedLayout.setOnClickListener(v -> {
                if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_PATIENT_REPORTED_DOCUMENTS)) {
                    patientReported();
                } else
                    ((NavActivity) context).showPermissionAlert();
            });
        }

        LinearLayout mROILayout = view.findViewById(R.id.roi_layout);
        if (AppSessionManager.getInstance().getIdentityUser().getUserType() == UserType.CAREGIVER
                && !isProxyUser()) {
            mROILayout.setVisibility(View.GONE);
        } else {
            mROILayout.setVisibility(View.VISIBLE);
            mROILayout.setOnClickListener(v -> {
                if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_FORMS_LIBRARY)) {
                    if (AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.SUBMIT_ROI_FORM)
                            || AppSessionManager.getInstance().getUserProfile().userCan(UserPermissions.SUBMIT_ANNC_FORM))
                        formsLibrary();
                    else
                        ((NavActivity) context).showPermissionAlert();
                } else
                    ((NavActivity) context).showPermissionAlert();
            });
        }
        LinearLayout myResourcesLayout = view.findViewById(R.id.my_resources_layout);
        myResourcesLayout.setVisibility(View.VISIBLE);
        myResourcesLayout.setOnClickListener(v -> myResources());

        LinearLayout mBillPayLayout = view.findViewById(R.id.bill_pay_layout);
        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_BILLPAY)) {
            mBillPayLayout.setVisibility(View.VISIBLE);
            mBillPayLayout.setOnClickListener(v -> billPay());
        } else {
            mBillPayLayout.setVisibility(View.GONE);
            mainSectionDisabledButtonCount++;
        }
        // Hide entire card if all buttons are GONE
        int mainSectionButtonCount = 5;
        if (mainSectionButtonCount == mainSectionDisabledButtonCount) {
            mMainSectionCard.setVisibility(View.GONE);
        }

        // Middle Section
        LinearLayout userProfileLayout = view.findViewById(R.id.user_profile_layout);
        userProfileLayout.setOnClickListener(v -> changeCTCAID());

        LinearLayout mActivityLogsLayout = view.findViewById(R.id.view_activity_layout);
        if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_USER_LOGS)) {
            mActivityLogsLayout.setVisibility(View.VISIBLE);
            mActivityLogsLayout.setOnClickListener(v -> {
                Log.d(TAG, "Tap that butt…Activity Logs");
                activityLogs();
            });
        } else {
            mActivityLogsLayout.setVisibility(View.GONE);
        }

        LinearLayout mSignOutLayout = view.findViewById(R.id.sign_out_layout);
        mSignOutLayout.setOnClickListener(v -> {
            Log.d(TAG, "Tap that butt…sign out");
            signOut();
        });

        // Bottom Section
        // Contact Us
        LinearLayout mContactUsLayout = view.findViewById(R.id.contact_us_layout);
        mContactUsLayout.setOnClickListener(v -> {
            Log.d(TAG, "Tap that butt…bill pay");
            contactUs();
        });
        // Fingerprint Authorization
        LinearLayout mFingerprintPrefLayout = view.findViewById(R.id.fingerprint_pref_layout);
        if (Build.VERSION.SDK_INT >= 23) {
            if (FingerprintHandler.getInstance().isCapable()) {

                mFingerprintPrefLayout.setOnClickListener(v -> {
                    Log.d(TAG, "Tap that butt…fingerprint");
                    fingerprintAuthPrefs();
                });
            } else {
                ViewGroup layout = (ViewGroup) mFingerprintPrefLayout.getParent();
                if (null != layout) //for safety only  as you are doing onClick
                    layout.removeView(mFingerprintPrefLayout);
            }
        } else {
            ViewGroup layout = (ViewGroup) mFingerprintPrefLayout.getParent();
            if (null != layout) //for safety only  as you are doing onClick
                layout.removeView(mFingerprintPrefLayout);
        }

        LinearLayout mAboutMyCTCALayout = view.findViewById(R.id.about_myctca_layout);
        mAboutMyCTCALayout.setOnClickListener(v -> {
            Log.d(TAG, "Tap that butt…bill pay");
            aboutMyCTCA();
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");

        listener.setToolBar(context.getString(R.string.more_title));

        inflater.inflate(R.menu.menu_more, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Top Section
    private void medDocs() {
        listener.showMedDocs();
    }

    private void healthHistory() {
        listener.showHealthHistory();
    }

    private void patientReported() {
        listener.showPatientReported();
    }

    private void formsLibrary() {
        listener.showFormsLibrary();
    }

    private void myResources() {
        listener.showMyResources();
    }

    private void billPay() {
        listener.showBillPay();
    }

    // My CTCA ID Section
    private void changeCTCAID() {
        Intent myIntent = new Intent(context, DisplayWebViewActivity.class);
        myIntent.putExtra("type", context.getString(R.string.login_user_profile_title_text));
        String ctcaHost = BuildConfig.server_ctca_host;
        String resetPasswordEndpoint = context.getString(R.string.change_ctca_id);
        String resetPasswordURL = ctcaHost + resetPasswordEndpoint;
        myIntent.putExtra("url", resetPasswordURL);
        startActivity(myIntent);
    }

    private void activityLogs() {
        listener.showActivityLogs();
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.nav_back_tapped_title))
                .setMessage(context.getString(R.string.nav_back_tapped_text))
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.cancel();
                    GeneralUtil.logoutApplication();
                })
                .setNegativeButton(getString(R.string.nav_alert_cancel), (dialog12, which) -> dialog12.cancel())
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    private void contactUs() {
        listener.showContactUs();
    }

    private void fingerprintAuthPrefs() {
        listener.learnAboutFingerprintAuthorization();
    }

    private void aboutMyCTCA() {
        listener.showAboutMyCTCA();
    }

    public interface MoreFragmentInteractionListener {

        void showContactUs();

        void learnAboutFingerprintAuthorization();

        void showAboutMyCTCA();

        void showActivityLogs();

        void showMedDocs();

        void showHealthHistory();

        void showFormsLibrary();

        void showMyResources();

        void showBillPay();

        void setToolBar(String string);

        void showPatientReported();
    }
}
