package com.myctca.notification;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.util.Constants;
import com.myctca.util.GeneralUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by vachhans on 5/30/17.
 */

public class RegisterDevice {
    private SharedPreferences settings;
    private String INSTALLATION_KEY = "installationID";
    private String Backend_Endpoint;

    protected static final String TAG = "CTCA-PUSH";

    RegisterDevice() {
        super();
        this.settings = PreferenceManager.getDefaultSharedPreferences(MyCTCA.getAppContext());
        Backend_Endpoint = MyCTCA.getAppContext().getString(R.string.server_push_notifications) + MyCTCA.getAppContext().getString(R.string.endpoint_notification_device_reg);
    }

    void registerForNotifications(String handle) throws  JSONException {
        Log.d(TAG, "RegisterDevice Registration Started handle: " + handle);
        String installationId;
        installationId = settings.getString(INSTALLATION_KEY, null);

        if (handle != null) {

            JSONObject templateBodyObj = new JSONObject();
            templateBodyObj.put("body","$(message)");
            templateBodyObj.put("sound","default");
            templateBodyObj.put("icon","ic_connect_push_default");

            JSONObject templateNotificationObj = new JSONObject();
            templateNotificationObj.put("notification",templateBodyObj);

            JSONObject templateObj = new JSONObject();
            templateObj.put("Name","default");
            templateObj.put("TemplateBody",templateNotificationObj.toString());

            JSONArray templateObjArr = new JSONArray();
            templateObjArr.put(templateObj);

            JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("Handle", handle);
            deviceInfo.put("Platform", "gcm");
            deviceInfo.put("Templates", templateObjArr);
            deviceInfo.put("InstallationId", installationId);

            Log.d(TAG, "RegisterDevice Registration data" + deviceInfo.toString());
            VolleyLog.setTag("UpsertRegistration");
            RequestQueue queue = Volley.newRequestQueue(MyCTCA.getAppContext());
            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            final JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, Backend_Endpoint, deviceInfo, future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + AppSessionManager.getInstance().getAccessToken().getToken());
                    return headers;
                }
            };
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonRequest);

            try {
                JSONObject response;
                response = future.get(10, TimeUnit.SECONDS);
                installationId = response.getString("installationId");
                Log.d(TAG, "RegisterDevice Installation ID: " + installationId);
                settings.edit().putString(INSTALLATION_KEY, installationId).apply();

            } catch (InterruptedException | ExecutionException e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("RegisterDevice:registerForNotifications", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                if (e.getCause() instanceof VolleyError) {
                    //grab the volley error from the throwable and cast it back
                    VolleyError error = (VolleyError) e.getCause();
                    Log.e(TAG, "RegisterDevice " + error.toString());
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            // Show timeout error message
                            Toast.makeText(MyCTCA.getAppContext(),
                                    "Network timeout error, check connection and try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        throw new RuntimeException("Network timeout error, check connection and try again.");
                    } else {
                        NetworkResponse response = error.networkResponse;
                        if(error.networkResponse.statusCode == 401) {
                            Toast.makeText(MyCTCA.getAppContext(), "Authorization Error", Toast.LENGTH_SHORT).show();
                            GeneralUtil.logoutApplication();
                        }
                        String json = new String(response.data);
                        Log.e(TAG, "RegisterDevice: " + response.statusCode + " " + json);

                    }
                } else {
                    Log.e(TAG,"error "+e.getMessage());
                    throw new RuntimeException("Unhandlel Exception");
                }
            } catch (TimeoutException e) {
                CTCAAnalyticsManager.createEventForSystemExceptions("RegisterDevice:registerForNotifications", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
                Log.e(TAG,"error "+e.getMessage());
                throw new RuntimeException("Timeout Exception");
            }
            Log.d(TAG, "RegisterDevice Registration End");

        } else {
            Log.d(TAG, "RegisterDevice Registration Failed handle is " + handle);
        }
    }
}
