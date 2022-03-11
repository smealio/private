package com.myctca;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.myctca.common.AppSessionManager;

public class MyCTCA extends Application {

    private static final String TAG = "myCTCA-App";

    private static Context mContext;
    private static boolean isInForeground;
    private static Activity mCurrentActivity = null;

    public static boolean isIsInForeground() {
        return isInForeground;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mContext = this;
        Log.d(TAG, "MyCTCA Application onCreate");
        registerActivityLifecycleCallbacks(new LifecycleCallback());

        if(BuildConfig.enable_diagnostic_reporting) {
            AppCenter.setEnabled(true);
            if (!AppCenter.isConfigured())
                AppCenter.start(this, BuildConfig.app_center_secret, Analytics.class, Crashes.class);
            enableDiagnosticReporting(true);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "MyCTCA Application LOW MEMORY");
    }

    @Override
    public void onTerminate() {
        AppSessionManager.getInstance().endCurrentSession();
        super.onTerminate();
    }

    public static Context getAppContext(){
        Log.d("MYCTCA", "AppCONTEXT: " + mContext );
        return mContext;
    }

    public static Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public static void enableDiagnosticReporting(boolean enable) {
        if (enable) {
            Crashes.setEnabled(true);
            Analytics.setEnabled(true);
        } else {
            Crashes.setEnabled(false);
            Analytics.setEnabled(false);
        }
        AppCenter.setLogLevel(Log.VERBOSE);
    }

    public static class LifecycleCallback implements ActivityLifecycleCallbacks{

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            MyCTCA.mCurrentActivity = activity;
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            isInForeground = true;
            MyCTCA.mCurrentActivity = activity;
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            isInForeground = false;
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }

}
