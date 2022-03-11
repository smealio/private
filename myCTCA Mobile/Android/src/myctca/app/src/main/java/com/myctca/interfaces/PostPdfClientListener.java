package com.myctca.interfaces;

import com.android.volley.VolleyError;

public interface PostPdfClientListener {
    void notifyPostPdfSuccess(byte[] response, int task);

    void notifyPostPdfError(VolleyError error, int task);
}
