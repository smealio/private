package com.myctca.notification;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import org.json.JSONException;

/**
 * Created by vachhans on 5/31/17.
 */

public class RegistrationIntent extends IntentService {
    protected static final String TAG = "CTCA-PUSH";

    public RegistrationIntent() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        RegisterDevice registerDevice = new RegisterDevice();
        try {
            registerDevice.registerForNotifications(AppSessionManager.getInstance().getFCMToken());
        } catch (JSONException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("RegisterIntent:onHandleIntent", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            Log.e(TAG,"error "+e.getMessage());
        }
    }
}