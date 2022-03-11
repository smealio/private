package com.myctca.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.PostListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.MyCTCATask;
import com.myctca.network.PostClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SendMessageService implements PostListener {
    private static SendMessageService sendMessageService;
    private final String TAG = SendMessageService.class.getSimpleName();
    private SendMessageInterface messageInterface;
    private Context context;

    public static SendMessageService getInstance() {
        if (sendMessageService == null) {
            sendMessageService = new SendMessageService();
        }
        return sendMessageService;
    }

    public void sendMessage(Context context, SendMessageInterface sendMessageInterface, String body) {
        this.context = context;
        this.messageInterface = sendMessageInterface;
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_send_feedback);
        PostClient postClient = new PostClient(this, context, MyCTCATask.SEND_MESSAGE);
        postClient.sendData(url, body, null);
    }

    @Override
    public void notifyPostSuccess(String response, int task) {
        Log.d("", "");
        messageInterface.notifyPostSuccess(response);
    }

    @Override
    public void notifyPostError(VolleyError error, String message, int task) {
        if (error.networkResponse.statusCode == 400) {
            String responseBody = null;
            try {
                responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                message = responseBody;
                if (TextUtils.isEmpty(message)) {
                    message = VolleyErrorHandler.handleError(error, context);
                } else {
                    JSONObject data = new JSONObject(responseBody);
                    String jsonFirstItem = data.keys().next();
                    JSONObject json = data.getJSONObject(jsonFirstItem);
                    JSONArray errors = json.getJSONArray("errors");
                    JSONObject jsonMessage = errors.getJSONObject(0);
                    message = jsonMessage.getString("errorMessage");
                }
            } catch (JSONException e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("SendMessageActivity:submitForm", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG, "error" + e);
                message = VolleyErrorHandler.handleError(error, context);
            }
        } else {
            message = VolleyErrorHandler.handleError(error, context);
        }
        if(message.isEmpty())
            message = context.getString(R.string.error_400);
        messageInterface.notifyPostError(message);
    }

    public interface SendMessageInterface {

        void notifyPostSuccess(String equals);

        void notifyPostError(String message);
    }
}
