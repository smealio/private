package com.myctca.model;

public class ImpersonationUserToken {
    private String access_token;
    private int expires_in;
    private String id_token;
    private String scope;
    private String session_state;
    private String state;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public String getAuthTokenString() {
        return token_type + " " + access_token;
    }


    public int getExpires_in() {
        return expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getId_token() {
        return id_token;
    }
}
