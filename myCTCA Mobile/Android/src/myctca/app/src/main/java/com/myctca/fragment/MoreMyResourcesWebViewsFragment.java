package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
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
import com.myctca.activity.MyResourcesActivity;

public class MoreMyResourcesWebViewsFragment extends Fragment {

    private WebView wvFaqs;
    private String url = "";
    private String toolbarName = "";
    private Context context;

    public static MoreMyResourcesWebViewsFragment newInstance() {
        return new MoreMyResourcesWebViewsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_more_my_resources_webviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        url = bundle.getString("url");
        toolbarName = bundle.getString("TOOLBAR_NAME");
        wvFaqs = view.findViewById(R.id.my_resources_faqs_webview);

        wvFaqs.getSettings().setJavaScriptEnabled(true);
        wvFaqs.getSettings().setDomStorageEnabled(true);
        fetchFAQs();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_my_resources, menu);
        ((MyResourcesActivity) context).setToolBar(toolbarName);
    }

    private void fetchFAQs() {
        if (context != null)
            ((MyResourcesActivity) context).showActivityIndicator("Loading...");
        wvFaqs.loadUrl(url);
        wvFaqs.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (context != null)
                    ((MyResourcesActivity) context).hideActivityIndicator();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (context != null)
                    ((MyResourcesActivity) context).hideActivityIndicator();
            }
        });
    }

}