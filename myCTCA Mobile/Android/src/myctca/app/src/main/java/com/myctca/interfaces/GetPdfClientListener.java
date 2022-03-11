package com.myctca.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by tomackb on 1/22/18.
 */

public interface GetPdfClientListener {
    void notifyGetPdfSuccess(byte[] response);

    void notifyGetPdfError(VolleyError error);
}
