package com.myctca.network;

import android.content.Context;
import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.myctca.common.AppSessionManager;
import com.myctca.interfaces.GetPdfClientListener;
import com.myctca.network.customRequests.InputStreamVolleyRequest;
import com.myctca.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomackb on 1/22/18.
 */

public class GetPDFClient {

    private GetPdfClientListener getPdfClientListener;
    private Context mContext;

    public GetPDFClient(GetPdfClientListener resultCallback, Context context) {
        getPdfClientListener = resultCallback;
        mContext = context;
    }

    public void downloadPdfFile(final String url, Map<String, String> params) {
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, buildUrl(url, params),
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        if (getPdfClientListener != null) {
                            getPdfClientListener.notifyGetPdfSuccess(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (getPdfClientListener != null) {
                    getPdfClientListener.notifyGetPdfError(error);
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                String token = AppSessionManager.getInstance().getAuthTokenString();
                params.put("Authorization", token);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.INITIAL_TIMEOUT_MS, Constants.MAX_NUM_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyService volleyService = VolleyService.getVolleyService(mContext);
        volleyService.addToRequestQueue(request);
    }

    private String buildUrl(String url, Map<String, String> params) {
        if (params != null) {
            Uri uri = Uri.parse(url);
            Uri.Builder builder = uri.buildUpon();
            for (String key : params.keySet()) {
                builder.appendQueryParameter(key, params.get(key));
            }
            return builder.build().toString();
        }
        return url;
    }
}
