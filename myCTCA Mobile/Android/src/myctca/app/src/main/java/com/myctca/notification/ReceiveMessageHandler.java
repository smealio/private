package com.myctca.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.myctca.MyCTCA;

/**
 * Created by vachhans on 5/30/17.
 */

public class ReceiveMessageHandler extends FirebaseMessagingService {

    protected static final String TAG = "CTCA-PUSH";

    private Context ctx;
    public static final int NOTIFICATION_ID = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        ctx = MyCTCA.getAppContext();
        Log.d(TAG, "ReceiveMessageHandler onMessageReceived From: " + remoteMessage.getFrom());
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "ReceiveMessageHandler Message data payload: " + remoteMessage.getData());
        }
        if(remoteMessage.getNotification() !=null) {
            String nMessage = remoteMessage.getNotification().getBody();
            sendNotification(nMessage);
        }

    }

    private void sendNotification(String msg) {

        Intent intent = new Intent(ctx,  MyCTCA.getAppContext().getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        //.setSmallIcon(R.drawable.ic_connect_push_default)
                        .setContentTitle("CTCA Connect")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                ;

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
