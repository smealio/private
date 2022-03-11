package com.myctca.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.DisplayWebViewActivity;
import com.myctca.activity.NavActivity;
import com.myctca.common.AppSessionManager;
import com.myctca.model.IdentityUser;
import com.myctca.util.GeneralUtil;

public class TermsAndConditionsFragment extends Fragment {
    private static final String TAG = TermsAndConditionsFragment.class.getSimpleName();
    private static final String TERMS_OF_USE_BV = "showTermsOfUseBottomView";
    private WebView wvTermsConditions;
    private LinearLayout termsOfUseBottomView;
    private RadioButton rbAcceptTermsOfUse;
    private RadioButton rbDeclineTermsOfUse;
    private Button submitTermsOfUse;
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
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wvTermsConditions = view.findViewById(R.id.wv_terms_conditions);
        termsOfUseBottomView = view.findViewById(R.id.terms_of_use_bottom_view);
        rbAcceptTermsOfUse = view.findViewById(R.id.rb_accept_terms_of_use);
        rbDeclineTermsOfUse = view.findViewById(R.id.rb_decline_terms_of_use);
        submitTermsOfUse = view.findViewById(R.id.terms_of_use_submit_button);

        wvTermsConditions.getSettings().setJavaScriptEnabled(true);
        wvTermsConditions.getSettings().setDomStorageEnabled(true);
        prepareView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (context != null)
            ((DisplayWebViewActivity) context).setToolBar(context.getString(R.string.create_account_terms));
    }

    private void prepareView() {
        String termsUrlStr = BuildConfig.myctca_server + context.getString(R.string.link_terms_conditions);
        Log.d(TAG, "terms & conditions url: " + termsUrlStr);

        if (getArguments().getBoolean(TERMS_OF_USE_BV))
            termsOfUseBottomView.setVisibility(View.VISIBLE);
        else
            termsOfUseBottomView.setVisibility(View.GONE);

        if (context != null)
            ((DisplayWebViewActivity) context).showActivityIndicator(context.getString(R.string.login_options_loading));
        wvTermsConditions.loadUrl(termsUrlStr);
        wvTermsConditions.setWebViewClient(new WebViewClient() {
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


        submitTermsOfUse.setOnClickListener(view -> {
            if (rbAcceptTermsOfUse.isChecked()) {
                //Terms of use accepted
                // Go to NavActivity
                IdentityUser user = AppSessionManager.getInstance().getIdentityUser();
                Intent navIntent = NavActivity.newIntent(context, user.getEmail(), user.getPword(), false);
                startActivity(navIntent);
            } else if (rbDeclineTermsOfUse.isChecked()) {
                //Terms of use declined.
                GeneralUtil.logoutApplication();
            }
        });
    }
}