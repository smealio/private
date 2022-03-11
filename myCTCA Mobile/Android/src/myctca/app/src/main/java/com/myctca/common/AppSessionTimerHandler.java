package com.myctca.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.activity.LoginActivity;
import com.myctca.activity.MoreFormsLibraryActivity;
import com.myctca.model.AccessToken;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.util.GeneralUtil;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by tomackb on 10/12/17.
 */

public class AppSessionTimerHandler {

    private static final String TAG = "CTCA-Time";
    //Idle Time out interval...
    private static final long DEFAULT_IDLE_TIMEOUT = 300000; // 5 min = 5m * 60s * 1000ms
    // Token Refresh in...
    private static final long DEFAULT_TOKEN_EXPIRES_IN = 3600000;
    private static final long DEFAULT_TOKEN_REFRESH_INTERVAL = 60000; // 1 min Before Expires = 1m * 60s * 1000ms
    private static final long DEFAULT_TOKEN_REFRESH_IN = DEFAULT_TOKEN_EXPIRES_IN - DEFAULT_TOKEN_REFRESH_INTERVAL; // 1 hour  (60m * 60s * 1000ms) - 1 minute before expires
    private static final long MAX_ACTIVITY_BACKGROUND_TIME_MS = 2000;

    private long mIdleTimeout = -1;
    private long mTokenRefreshTimeInterval = -1;
    // Session Idle Handler
    private Handler mIdleHandler;
    private Runnable mIdleR;
    // Session Token Refresh Handler
    private Handler mTokenRefreshHandler;
    private Runnable mTokenRefreshR;
    // In Background Timer
    private Timer mActivityBackgroundTimer;
    private TimerTask mActivityBackgroundTimerTask;
    private boolean wasInBackground;
    private long enterBackgroundTimestamp = -1;
    private AlertDialog dialog;

    // Idle Timer Methods
    public void resetIdleHandler() {
        Log.d(TAG, "resetIdleHandler mIdleHandler: " + mIdleHandler);
        if (mIdleHandler != null) {
            Log.d(TAG, "AppSessionTimerHandler resetIdleHandler RESET RESET RESET RESET RESET RESET RESET RESET");
            stopIdleHandler();
            startIdleHandler();
        }
    }

    public void stopIdleHandler() {
        Log.d(TAG, "stopIdleHandler: " + mIdleHandler);
        if (mIdleHandler != null) {
            mIdleHandler.removeCallbacks(mIdleR);
            mIdleHandler = null;
        }
    }

    public void killIdleHandler() {
        Log.d(TAG, "killIdleHandler");
        mIdleR = null;
        mIdleHandler = null;
    }

    public void startIdleHandler() {
        Log.d(TAG, "startIdleHandler START START START START");
        if (mIdleHandler == null) {
            mIdleHandler = new Handler(Looper.getMainLooper());
            Log.d(TAG, "startIdleHandler mIdleHandler: " + mIdleHandler);
            mIdleR = () -> {
                Log.d(TAG, "AppSessionManager IDLE TIMER FIRED UP");
                AppSessionManager.getInstance().setSessionExpired(true);
                if (MyCTCA.isIsInForeground() && (dialog == null || !dialog.isShowing())) {
                    createSessionExpiryDialog();
                }
            };
        }
        Log.d(TAG, "IDLE TIMEOUT: " + getIdleTimeout());
        mIdleHandler.postDelayed(mIdleR, getIdleTimeout());
    }

    public boolean isWasInBackground() {
        return wasInBackground;
    }

    private void createSessionExpiryDialog() {
        if (MyCTCA.isIsInForeground()) {

            MyCTCAActivity activity = (MyCTCAActivity) MyCTCA.getCurrentActivity();
            if (!activity.getClass().isAssignableFrom(LoginActivity.class)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);
                dialog = builder.setTitle(activity.getString(R.string.session_timeout_dialog_title))
                        .setMessage(activity.getString(R.string.session_timeout_dialog_message))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CTCAAnalyticsManager.createEvent("AppSessionTimerHandler:createSessionExpiryDialog", CTCAAnalyticsConstants.ALERT_SESSION_RENEWAL_YES, null, null);
                                AppSessionManager.getInstance().refreshToken();
                                AppSessionManager.getInstance().resetIdleHandler();
                                if (AppSessionManager.getInstance().isDeepLinking()) {
                                    // Go to Forms Library activity
                                    Intent intent = MoreFormsLibraryActivity.newIntent(MyCTCA.getAppContext());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MyCTCA.getAppContext().startActivity(intent);
                                }
                                AppSessionManager.getInstance().setSessionExpired(false);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CTCAAnalyticsManager.createEvent("AppSessionTimerHandler:createSessionExpiryDialog", CTCAAnalyticsConstants.ALERT_SESSION_RENEWAL_NO, null, null);
                                GeneralUtil.logoutApplication();
                            }
                        })
                        .create();
                dialog.setOnShowListener(arg0 -> dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.colorPrimary)));
                dialog.show();
            }
        }
    }

    public long getIdleTimeout() {
        if (mIdleTimeout == -1) {
            return DEFAULT_IDLE_TIMEOUT;
        }
        return mIdleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        mIdleTimeout = idleTimeout;
    }

    // Token Refresh Timer

    public void resetTokenRefreshHandler() {
        Log.d(TAG, "resetTokenRefreshHandler mResetTokenHandler: " + mTokenRefreshHandler);
        if (mTokenRefreshHandler != null) {
            Log.d(TAG, "AppSessionTimerHandler resetTokenRefreshHandler RESET RESET RESET RESET RESET RESET RESET RESET");
            stopTokenRefreshHandler();
            startTokenRefreshHandler();
        }
    }

    public void stopTokenRefreshHandler() {
        Log.d(TAG, "stopTokenRefreshHandler: " + mTokenRefreshHandler);
        if (mTokenRefreshHandler != null) {
            mTokenRefreshHandler.removeCallbacks(mTokenRefreshR);
            mTokenRefreshHandler = null;
        }
    }

    public void killTokenRefreshHandler() {
        Log.d(TAG, "killTokenRefreshHandler");
        mTokenRefreshR = null;
        mTokenRefreshHandler = null;
    }

    public void startTokenRefreshHandler() {
        Log.d(TAG, "startTokenRefreshHandler START START START START");
        if (mTokenRefreshHandler == null) {
            mTokenRefreshHandler = new Handler(Looper.getMainLooper());
            Log.d(TAG, "startTokenRefreshHandler mTokenRefreshHandler: " + mTokenRefreshHandler);
            mTokenRefreshR = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "AppSessionManager TOKENREFRESH TIMER FIRED UP");
                    // Refresh the token
                    AppSessionManager.getInstance().refreshToken();
                }
            };
        }
        Log.d(TAG, "startTokenRefreshHandler TOKEN Refresh In: " + getTokenRefreshIn());
        mTokenRefreshHandler.postDelayed(mTokenRefreshR, getTokenRefreshIn());
    }

    private long getTokenRefreshInterval() {

        if (mTokenRefreshTimeInterval == -1) {
            mTokenRefreshTimeInterval = DEFAULT_TOKEN_REFRESH_INTERVAL;
        }
        return mTokenRefreshTimeInterval;
    }

    public void setTokenRefreshInterval(long refreshTokenBefore) {

        AccessToken accessToken = AppSessionManager.getInstance().getAccessToken();
        if (accessToken != null) {
            long expiresIn = AppSessionManager.getInstance().getAccessToken().getExpiresIn();

            if (refreshTokenBefore < expiresIn) {
                mTokenRefreshTimeInterval = refreshTokenBefore;
                Log.d(TAG, "mTokenRefreshTimeInterval: " + mTokenRefreshTimeInterval);
                Log.d(TAG, "expiresIn: " + expiresIn);
            }
        }
    }

    private long getTokenRefreshIn() {
        AccessToken accessToken = AppSessionManager.getInstance().getAccessToken();

        if (accessToken != null) {
            long expiresIn = AppSessionManager.getInstance().getAccessToken().getExpiresIn() * 1000;
            long tokenRefreshInterval = getTokenRefreshInterval();
            if (expiresIn > tokenRefreshInterval) {
                long tokenRefreshIn = expiresIn - getTokenRefreshInterval();

                Log.d(TAG, "expiresIn: " + expiresIn);
                Log.d(TAG, "getTokenRefreshIn: " + tokenRefreshIn);
                return tokenRefreshIn;
            }
        }
        return DEFAULT_TOKEN_REFRESH_IN;
    }


    // Background Timer stuff
    public void startActivityBackgroundTimer() {
        this.mActivityBackgroundTimer = new Timer();
        this.mActivityBackgroundTimerTask = new TimerTask() {
            public void run() {
                wasInBackground = true;
                enterBackgroundTimestamp = System.currentTimeMillis() / 1000;
                Log.d(TAG, "startActivityBackgroundTimer BACKGROUND_TIMESTAMP: " + enterBackgroundTimestamp);
            }
        };

        this.mActivityBackgroundTimer.schedule(mActivityBackgroundTimerTask, MAX_ACTIVITY_BACKGROUND_TIME_MS);
    }

    public void stopActivityBackgroundTimer() {
        if (this.mActivityBackgroundTimerTask != null) {
            this.mActivityBackgroundTimerTask.cancel();
        }


        if (this.mActivityBackgroundTimer != null) {
            this.mActivityBackgroundTimer.cancel();
        }
        Log.d(TAG, "stopActivityBackgroundTimer  wasInBackground: " + wasInBackground);
        if (this.wasInBackground) {
            this.wasInBackground = false;
            Log.d(TAG, "stopActivityBackgroundTimer  OUT_OF_BACKGROUND: " + System.currentTimeMillis() / 1000);
            Log.d(TAG, "stopActivityBackgroundTimer WENT_TO_BACKGROUND: " + this.enterBackgroundTimestamp);
            if (this.enterBackgroundTimestamp > 0) {

                // Idle Timeout Check
                long timeInBackground = (System.currentTimeMillis() / 1000 - this.enterBackgroundTimestamp);
                long idleTimeout = AppSessionManager.getInstance().getIdleTimeout() / 1000;
                Log.d(TAG, "stopActivityBackgroundTimer TIME_IN_BACKGROUND: " + timeInBackground);
                Log.d(TAG, "stopActivityBackgroundTimer IDLE_TIMEOUT: " + idleTimeout);
                if (idleTimeout < timeInBackground) {
                    // IDLE TIMEOUT
                    Log.d(TAG, "END_CURRENT_SESSION");
                    AppSessionManager.getInstance().setSessionExpired(true);
                    if (MyCTCA.isIsInForeground() && (dialog == null || !dialog.isShowing())) {
                        createSessionExpiryDialog();
                    }
                }
                // CHECK IF TOKEN NEEDS AN UPDATE
                AccessToken accessToken = AppSessionManager.getInstance().getAccessToken();
                if (accessToken != null) {
                    long expiresIn = accessToken.getExpiresIn();
                    long refreshIn = getTokenRefreshIn() / 1000;
                    Log.d(TAG, "expiresIn: " + expiresIn);
                    Log.d(TAG, "refreshIn: " + refreshIn);
                    Log.d(TAG, "timeInBackground: " + timeInBackground);
                    if (timeInBackground > refreshIn) {
                        AppSessionManager.getInstance().refreshToken();
                    }
                }
            }
        }
    }
}
