package com.myctca.model;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by tomackb on 6/30/17.
 */

public class AccessToken {

    private static final Date createdOn = new Date();
    private String token_type;
    private int expires_in;
    private String access_token;
    private String id_token;

    public String prettyPrint() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getTokenType() {
        return token_type;
    }

    public long getExpiresIn() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdOn);
        cal.add(Calendar.SECOND, expires_in);
        return ((cal.getTime().getTime() - (new Date().getTime())) / 1000);
    }

    public String getToken() {
        if (getExpiresIn() < 60) {
            return null;
        } else
            return access_token;
    }

    public String getAuthTokenString() {
        return token_type + " " + access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
