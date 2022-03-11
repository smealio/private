package com.myctca.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.DisplayWebViewActivity;
import com.myctca.activity.MoreCertificationsActivity;

import java.util.Calendar;

public class MoreAboutMyCTCAFragment extends Fragment {

    private static final String TAG = "myCTCA-MOREABOUTMYCTCA";
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_about_my_ctca, container, false);

        TextView mCopyrightTV = view.findViewById(R.id.more_about_myctca_copyright_tv);
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        mCopyrightTV.setText(context.getString(R.string.more_about_myctca_copyright, year));

        TextView mVersionTV = view.findViewById(R.id.more_about_myctca_version_tv);
        String versionName = BuildConfig.VERSION_NAME;
        mVersionTV.setText(context.getString(R.string.more_about_myctca_version, versionName));

        Button mTermsButton = view.findViewById(R.id.more_about_myctca_terms_button);

        mTermsButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: TERMS");
            doTerms();
        });

        Button mPrivacyButton = view.findViewById(R.id.more_about_myctca_privacy_button);
        mPrivacyButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: PRIVACY");
            doPrivacy();
        });

        Button mCertificationButton = view.findViewById(R.id.more_about_myctca_certifications_button);
        mCertificationButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: PRIVACY");
            doCertifications();
        });

        return view;
    }

    private void doTerms() {
        Intent myIntent = new Intent(context, DisplayWebViewActivity.class);
        myIntent.putExtra("fragment", TermsAndConditionsFragment.class.getSimpleName()); //Optional parameters
        startActivity(myIntent);
    }

    private void doPrivacy() {
        Intent myIntent = new Intent(context, DisplayWebViewActivity.class);
        String privacyUrlStr = BuildConfig.myctca_server + context.getString(R.string.link_privacy_policy);
        myIntent.putExtra("type", context.getString(R.string.create_account_privacy));
        myIntent.putExtra("url", privacyUrlStr);
        startActivity(myIntent);
    }

    private void doCertifications() {
        Intent intent = MoreCertificationsActivity.newIntent(this.context);
        this.context.startActivity(intent);
    }
}
