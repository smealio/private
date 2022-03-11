package com.myctca.util;

import com.myctca.MyCTCA;
import com.myctca.common.AppSessionManager;
import com.myctca.common.MyCTCAActivity;

public class GeneralUtil {

    public static String noNull(String error) {
        if (error == null)
            return "unknown";
        else
            return error;
    }

    public static void logoutApplication() {
        boolean deepLinking = AppSessionManager.getInstance().isDeepLinking();
        AppSessionManager.getInstance().endCurrentSession();
        MyCTCAActivity currentActivity = (MyCTCAActivity) MyCTCA.getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.returnToRoot(deepLinking);
        }
    }
}
