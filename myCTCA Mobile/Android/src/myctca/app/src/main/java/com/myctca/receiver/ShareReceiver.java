package com.myctca.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.myctca.model.CTCAAnalytics;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;

import static android.content.Intent.EXTRA_CHOSEN_COMPONENT;

public class ShareReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName componentName = (ComponentName) intent.getExtras().get(EXTRA_CHOSEN_COMPONENT);
        if (componentName != null) {
            CTCAAnalyticsManager.createCommonEvent(new CTCAAnalytics("ShareReceiver:onReceive", CTCAAnalyticsConstants.ACTION_APPTS_PDF_SHARE_SELECTED_APP_TAP, componentName));
        }
    }
}
