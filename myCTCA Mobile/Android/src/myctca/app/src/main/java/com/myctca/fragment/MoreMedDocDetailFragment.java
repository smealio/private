package com.myctca.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MoreMedDocDetailActivity;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.network.GetClient;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.myctca.util.GeneralUtil.noNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreMedDocDetailFragment extends Fragment
        implements GetListener {

    private static final String TAG = "myCTCA-MedDocDet";

    private static final String PURPOSE = "MED_DOC_DETAIL";
    private String mMedDocType = "";

    private String mMedDocId;
    private WebView mWebView;
    private OnFragmentInteractionListener listener;
    private Button cancelButton;
    private Button downloadButton;
    private Context context;

    public MoreMedDocDetailFragment() {
        // Required empty public constructor
    }

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
        View view = inflater.inflate(R.layout.fragment_more_med_doc_detail, container, false);

        mWebView = view.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        LinearLayout labsBottomButtonLayout = view.findViewById(R.id.labs_bottom_button_layout);
        cancelButton = labsBottomButtonLayout.findViewById(R.id.btn_close_clinical_summary);
        downloadButton = labsBottomButtonLayout.findViewById(R.id.btn_download_clinical_summary);
        downloadButton.setVisibility(View.VISIBLE);

        fetchMedDoc();
        handleButtonActions();

        return view;
    }

    private void fetchMedDoc() {

        if (context != null)
            ((MoreMedDocDetailActivity) context).showActivityIndicator("Retrieving documents…");

        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_med_doc, mMedDocType.toLowerCase(), mMedDocId);
        Log.d(TAG, "URL: " + url);

        GetClient getClient = new GetClient(this, this.context);
        getClient.fetch(url, null, PURPOSE);
    }

    private void handleButtonActions() {
        cancelButton.setOnClickListener(view -> {
            if (context != null)
                ((MoreMedDocDetailActivity) context).onBackPressed();
        });
        downloadButton.setOnClickListener(view -> listener.addFragment(DownloadPdfFragment.newInstance()));
    }

    private void showRequestFailure(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.setTitle(context.getString(R.string.failure_title))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.nav_alert_ok), (dialog1, which) -> {
                    dialog1.dismiss();
                    ((MoreMedDocDetailActivity) context).finish();
                })
                .create();
        dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary)));
        dialog.show();
    }

    public void setmMedDocType(String mMedDocType) {
        this.mMedDocType = mMedDocType;
    }

    public void setmMedDocId(String mMedDocId) {
        this.mMedDocId = mMedDocId;
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            JSONObject jsonObject = new JSONObject(parseSuccess);
            String mHTML;
            try {
                String docText = jsonObject.getString("documentText");
                String opener = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, user-scalable=yes'><style>body{font-family: Sans-Serif;}p{padding-bottom: 15px;}</style></head><body>";
                String closer = "</body></html>";
                mHTML = opener + docText + closer;

            } catch (JSONException e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("MoreMedDocDetailFragment:notifyFetchSuccess", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG, "JSONERROR: " + e);
                mHTML = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'><style>* {font-family: sans-serif; padding-right: 5px;} </style></head><body>Error retrieving Clinical Summary item</body></html>";
            }

            Log.d(TAG, "mHTML: " + mHTML);
            mWebView.loadDataWithBaseURL(null, mHTML, "text/html", "utf-8", null);
            if (context != null)
                ((MoreMedDocDetailActivity) context).hideActivityIndicator();
        } catch (Exception e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("MoreMedDocDetailFragment:notifyFetchSuccess", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG, "error:" + e);
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_med_doc, mMedDocType.toLowerCase(), mMedDocId);
        CTCAAnalyticsManager.createEvent("MoreMedDocClinicalSummaryDetailFragment:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        if (context != null)
            ((MoreMedDocDetailActivity) context).hideActivityIndicator();
        Log.d("Error.Response", noNull(error.getMessage()));
        showRequestFailure(VolleyErrorHandler.handleError(error, context));
    }

    /**
     * END - More Fetch Client Listener Methods
     */

    public void scrollWebViewToTop() {
        if (mWebView != null) {
            mWebView.scrollTo(0, 0);
        }
    }

    public interface OnFragmentInteractionListener {
        void addFragment(Fragment fragment);
    }
}
