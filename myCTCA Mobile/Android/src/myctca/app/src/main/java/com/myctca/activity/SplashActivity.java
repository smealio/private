package com.myctca.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.interfaces.GetListener;
import com.myctca.model.ApplicationInfo;
import com.myctca.network.GetClient;

public class SplashActivity extends AppCompatActivity implements GetListener {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final String PURPOSE = "TECHNICAL_SUPPORT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Splash screen is set in Splash theme
        getTechnicalSupportPhone();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getTechnicalSupportPhone() {
        String url = BuildConfig.myctca_server + getString(R.string.myctca_get_application_info);
        GetClient getClient = new GetClient(this, this);
        getClient.fetch(url, null, PURPOSE);
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            ApplicationInfo applicationInfo = new Gson().fromJson(parseSuccess, ApplicationInfo.class);
            AppSessionManager.getInstance().setTechnicalSupport(applicationInfo.getTechSupportNumber());
            AppSessionManager.getInstance().setApplicationVersions(applicationInfo.getApplicationVersions());
        } catch (JsonParseException exception) {
            Log.e(TAG, "Unable to fetch technical support number");
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.e(TAG, "Unable to fetch technical support number: ", error);
    }
}
