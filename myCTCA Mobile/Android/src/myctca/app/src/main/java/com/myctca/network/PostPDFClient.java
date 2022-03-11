package com.myctca.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.myctca.common.AppSessionManager;
import com.myctca.interfaces.PostPdfClientListener;
import com.myctca.network.customRequests.InputStreamVolleyRequest;
import com.myctca.util.Constants;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PostPDFClient {

    private static final String TAG = "myCTCA-MorePostClient";

    private PostPdfClientListener postPdfClientListener;
    private Context mContext;
    private int mTask;

    public PostPDFClient(PostPdfClientListener resultCallback, Context context, int task) {
        postPdfClientListener = resultCallback;
        mContext = context;
        mTask = task;
    }

    public void downloadPdf(final String url, final String body) {
        Log.d("TAG", "json: " + body);

        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.POST, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                if (postPdfClientListener != null) {
                    postPdfClientListener.notifyPostPdfSuccess(response, mTask);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (postPdfClientListener != null) {
                    postPdfClientListener.notifyPostPdfError(error, mTask);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                String token = AppSessionManager.getInstance().getAuthTokenString();
                params.put("Authorization", token);
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public byte[] getBody() {
                return body == null ? null : body.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyService volleyService = VolleyService.getVolleyService(mContext);
        volleyService.addToRequestQueue(request);
    }
}
