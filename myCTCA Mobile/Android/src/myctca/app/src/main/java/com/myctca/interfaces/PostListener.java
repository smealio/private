package com.myctca.interfaces;

import com.android.volley.VolleyError;

public interface PostListener {
    void notifyPostSuccess(String response, int task);
    void notifyPostError(VolleyError error, String message, int task);
}
