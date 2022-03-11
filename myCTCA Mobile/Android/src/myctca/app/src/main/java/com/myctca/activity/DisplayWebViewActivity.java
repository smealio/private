package com.myctca.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.myctca.R;
import com.myctca.common.MyCTCAActivity;
import com.myctca.fragment.DisplayWebViewFragment;
import com.myctca.fragment.TermsAndConditionsFragment;

public class DisplayWebViewActivity extends MyCTCAActivity {
    private static final String TAG = DisplayWebViewActivity.class.getSimpleName();
    Fragment fragment;
    private boolean showTermsOfUseBottomView = false;
    private String url;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_webview);

        Bundle extras = getIntent().getExtras();
        String fragmentName = extras.getString("fragment");
        url = extras.getString("url");
        type = extras.getString("type");
        if (fragmentName != null && fragmentName.equals(TermsAndConditionsFragment.class.getSimpleName())) {
            this.showTermsOfUseBottomView = extras.getBoolean("showTermsOfUseBottomView");
            fragment = new TermsAndConditionsFragment();
        } else {
            fragment = DisplayWebViewFragment.newInstance();
        }
        addFragment(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "BACK BUTTON PRESSED");
            this.onBackPressed();
        } else
            Log.d(TAG, "DEFAULT");

        return true;
    }

    private void addFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("showTermsOfUseBottomView", showTermsOfUseBottomView);
        bundle.putString("url", url);
        bundle.putString("TOOLBAR_NAME", type);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.login_options_fragment_container, fragment);
        transaction.commit();
    }
}