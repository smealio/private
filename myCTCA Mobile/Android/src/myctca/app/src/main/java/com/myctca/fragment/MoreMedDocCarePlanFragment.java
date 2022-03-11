package com.myctca.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreMedDocCarePlanActivity;
import com.myctca.service.MoreMedicalDocumentsService;
import com.myctca.service.SessionFacade;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreMedDocCarePlanFragment extends Fragment implements MoreMedicalDocumentsService.MoreMedDocListenerGet {

    private static final String TAG = "More_CAREPLAN";
    final String purpose = "CARE_PLAN";
    private WebView mWebView;
    private LinearLayout emptyLayout;
    private Button cancelButton;
    private Button downloadButton;
    private OnFragmentInteractionListener listener;
    private Context context;
    private SessionFacade sessionFacade;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
        this.context = context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_med_doc_care_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        mWebView = view.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        LinearLayout labsBottomButtonLayout = view.findViewById(R.id.labs_bottom_button_layout);
        cancelButton = labsBottomButtonLayout.findViewById(R.id.btn_close_clinical_summary);
        downloadButton = labsBottomButtonLayout.findViewById(R.id.btn_download_clinical_summary);
        downloadButton.setVisibility(View.VISIBLE);

        emptyLayout = view.findViewById(R.id.empty_view);
        TextView emptyMessage = view.findViewById(R.id.med_doc_care_plan_empty_message);
        emptyMessage.setText(getString(R.string.empty_list_message, context.getString(R.string.more_medical_docs), ": " + context.getString(R.string.more_med_doc_care_plan)));

        fetchCarePlan();
        handleButtonActions();
    }

    private void fetchCarePlan() {
        if (context != null)
            ((MoreMedDocCarePlanActivity) context).showActivityIndicator(getString(R.string.get_care_plan_indicator));
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_care_plan);
        sessionFacade.getMedicalDocumentsData(context, this, purpose, url, null);
    }

    private void handleButtonActions() {
        cancelButton.setOnClickListener(view -> {
            if (context != null)
                ((MoreMedDocCarePlanActivity) context).onBackPressed();
        });
        downloadButton.setOnClickListener(view -> listener.addFragment(DownloadPdfFragment.newInstance()));
    }

    private void showRequestFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreMedDocCarePlanActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    private void setWebView(String response) {
        mWebView.loadDataWithBaseURL(null, response, "text/html", "utf-8", null);
        if (context != null)
            ((MoreMedDocCarePlanActivity) context).hideActivityIndicator();
    }

    @Override
    public void fetchCarePlan(String documentText) {
        if (context != null)
            ((MoreMedDocCarePlanActivity) context).hideActivityIndicator();
        if (documentText.isEmpty())
            emptyLayout.setVisibility(View.VISIBLE);
        else {
            setWebView(documentText);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public <T> void fetchMedicalDocSuccess(List<T> medicalDocs) {
        //do nothing
    }

    @Override
    public void notifyFetchError(String errorMessage) {
        if (context != null)
            ((MoreMedDocCarePlanActivity) context).hideActivityIndicator();
        emptyLayout.setVisibility(View.VISIBLE);
        showRequestFailure(errorMessage);
    }

    public interface OnFragmentInteractionListener {
        void addFragment(Fragment fragment);
    }
}
