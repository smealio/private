package com.myctca.notification;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by vachhans on 5/30/17.
 */

public class FirebaseInstanceService extends FirebaseMessagingService {
    protected static final String TAG = "CTCA-PUSH";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FirebaseInstanceService Refreshing FCM Registration Token" + refreshedToken);

        //Update Server: cant do it, we do not have access_token to connect pushnotification service
        /*RegisterDevice client = new RegisterDevice();
        try {
            client.register(refreshedToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    //    @Override
    //    public void onTokenRefresh() {
    //        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    //        Log.d(TAG, "FirebaseInstanceService Refreshing FCM Registration Token" + refreshedToken);
    //
    //        //Update Server: cant do it, we do not have access_token to connect pushnotification service
    //        /*RegisterDevice client = new RegisterDevice();
    //        try {
    //            client.register(refreshedToken);
    //        } catch (JSONException e) {
    //            e.printStackTrace();
    //        }*/
    //    }
}
