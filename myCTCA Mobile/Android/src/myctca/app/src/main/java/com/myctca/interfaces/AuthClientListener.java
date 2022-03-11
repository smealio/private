package com.myctca.interfaces;

import org.json.JSONException;

public interface AuthClientListener {

    void notifyTokenRefreshSuccess(boolean refreshSuccess, JSONException exception);
}
