package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.myctca.activity.DisplayWebViewActivity;

public class DisplayWebViewFragment extends Fragment {
    private static final String TAG = DisplayWebViewFragment.class.getSimpleName();
    private WebView webView;
    private String url;
    private String toolbar_name;
    private Context context;

    public static DisplayWebViewFragment newInstance() {
        return new DisplayWebViewFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_display_webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        url = bundle.getString("url");
        toolbar_name = bundle.getString("TOOLBAR_NAME");
        webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (toolbar_name.equals(getString(R.string.more_bill_pay)))
            webView.setInitialScale(100);
        openForgotPasswordScreen();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (context != null)
            ((DisplayWebViewActivity) context).setToolBar(toolbar_name);
    }

    private void openForgotPasswordScreen() {
        Log.d(TAG, "doForgotPassword: " + url);

        if (context != null)
            ((DisplayWebViewActivity) context).showActivityIndicator(getString(R.string.login_options_loading));
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (context != null)
                    ((DisplayWebViewActivity) context).hideActivityIndicator();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (context != null)
                    ((DisplayWebViewActivity) context).hideActivityIndicator();
            }
        });
    }
}