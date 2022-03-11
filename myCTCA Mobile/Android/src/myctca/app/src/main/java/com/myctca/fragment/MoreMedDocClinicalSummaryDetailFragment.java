package com.myctca.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreMedDocClinicalSummaryActivity;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MyCTCATask;
import com.myctca.model.UserPermissions;
import com.myctca.service.MoreMedicalDocumentsService;
import com.myctca.service.SessionFacade;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreMedDocClinicalSummaryDetailFragment extends Fragment
        implements MoreMedicalDocumentsService.MoreMedDocClinicalSummaryListenerPost {

    private WebView mWebView;
    private Button closeClinicalSummary;
    private Button transmitClinicalSummary;
    private Button downloadClinicalSummary;
    private MoreMedDocClinicalSummaryDetailListener listener;
    private String selectedClinicalSummaryName;
    private List<String> selectedClinicalSummary = new ArrayList<>();
    private Context context;
    private SessionFacade sessionFacade;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (MoreMedDocClinicalSummaryDetailListener) context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_med_doc_clinical_summary_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        mWebView = view.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        LinearLayout detailClinicalSummaryBottomLayout;
        detailClinicalSummaryBottomLayout = view.findViewById(R.id.detail_clinical_summary_bottom_layout);
        closeClinicalSummary = detailClinicalSummaryBottomLayout.findViewById(R.id.btn_close_clinical_summary);
        transmitClinicalSummary = detailClinicalSummaryBottomLayout.findViewById(R.id.btn_transmit_clinical_summary);
        downloadClinicalSummary = detailClinicalSummaryBottomLayout.findViewById(R.id.btn_download_clinical_summary);
        transmitClinicalSummary.setVisibility(View.VISIBLE);
        downloadClinicalSummary.setVisibility(View.VISIBLE);

        getAllArguments();
        fetchClinicalSummary();
        handleButtonClickListeners();
    }

    private void handleButtonClickListeners() {
        closeClinicalSummary.setOnClickListener(view13 -> ((MoreMedDocClinicalSummaryActivity) context).onBackPressed());
        transmitClinicalSummary.setOnClickListener(view12 -> {
            if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.TRANSMIT_CCDA_DOCUMENTS)) {
                listener.addFragment(MoreMedDocClinicalSummaryTransmitFragment.newInstance(), selectedClinicalSummary, "");
            } else {
                showPermissionAlert();
            }
        });
        downloadClinicalSummary.setOnClickListener(view1 -> {
            if (sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.DOWNLOAD_CCDA_DOCUMENTS))
                listener.addFragment(new MoreMedDocClinicalSummaryDownloadFragment(), selectedClinicalSummary, "");
            else {
                showPermissionAlert();
            }
        });
    }

    private void getAllArguments() {
        // Inflate the layout for this fragment
        //get args
        final String KEY = "SELECTED_CLINICAL_SUMMARY_ID";
        final String NAME = "SELECTED_CLINICAL_SUMMARY_NAME";
        selectedClinicalSummary = getArguments().getStringArrayList(KEY);
        selectedClinicalSummaryName = getArguments().getString(NAME);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((MoreMedDocClinicalSummaryActivity) context).setToolBar(selectedClinicalSummaryName);
        menu.clear();
    }

    public void showPermissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(context.getString(R.string.permisson_not_granted_title))
                .setMessage(context.getString(R.string.permisson_not_granted_message_proxy))
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    //do nothing
                }).create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    private void fetchClinicalSummary() {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).showActivityIndicator("Retrieving Clinical Summary...");
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_clinical_summary_detail);
        Map<String, List<String>> formData = new HashMap<>();
        formData.put("DocumentId", selectedClinicalSummary);
        sessionFacade.postClinicalSummaryData(context, this, url, MyCTCATask.CLINICAL_SUMMARY_DETAIL, new JSONObject(formData).toString(), null);
    }

    private void showClinicalSummaryDetailFailure(String message) {
        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryDetailFragment:showClinicalSummaryDetailFailure", CTCAAnalyticsConstants.ALERT_CLINICAL_SUMMARIES_DETAIL_FAIL, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.new_appt_request_failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreMedDocClinicalSummaryActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    @Override
    public void notifyPostSuccess(String text) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).hideActivityIndicator();
        mWebView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
    }

    @Override
    public void notifyPostError(String message) {
        if (context != null)
            ((MoreMedDocClinicalSummaryActivity) context).hideActivityIndicator();
        showClinicalSummaryDetailFailure(message);
    }

    public interface MoreMedDocClinicalSummaryDetailListener {
        void addFragment(Fragment fragment, List<String> selectedClinicalSummaryIDs, String selectedItem);
    }

}
