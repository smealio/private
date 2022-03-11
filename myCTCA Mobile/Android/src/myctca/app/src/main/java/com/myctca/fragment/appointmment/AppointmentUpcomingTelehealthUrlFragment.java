package com.myctca.fragment.appointmment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.AppointmentUpcomingTelehealthUrlActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentUpcomingTelehealthUrlFragment extends Fragment {

    private static final String TAG = AppointmentUpcomingTelehealthUrlFragment.class.getSimpleName();
    private WebView mWebView;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appt_upcoming_telehealth_url, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = view.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        fetchTelehealthUrl();
    }

    private void fetchTelehealthUrl() {
        if (context != null)
            ((AppointmentUpcomingTelehealthUrlActivity) context).showActivityIndicator(context.getResources().getString(R.string.telehealth_loading_indicator));
        AppointmentUpcomingTelehealthUrlActivity activity = (AppointmentUpcomingTelehealthUrlActivity) context;
        final String telehealthUrl = activity.getTelehealthUrl();
        mWebView.loadUrl(telehealthUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (context != null)
                    ((AppointmentUpcomingTelehealthUrlActivity) context).hideActivityIndicator();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, error.toString());
            }
        });
    }
}
