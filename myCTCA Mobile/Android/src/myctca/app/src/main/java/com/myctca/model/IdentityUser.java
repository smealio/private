package com.myctca.model;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by tomackb on 8/2/17.
 */

public class IdentityUser implements Serializable {
    private int sub;
    private String given_name;
    private String family_name;
    private String email;
    private String name;
    private String image_uploaded;
    private String color_id;
    private String user_type;
    private String role;
    private String picture;
    private String pword;

    public int getUserId() {
        return sub;
    }

    public String getFirstName() {
        return given_name;
    }


    public String getLastName() {
        return family_name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getColorId() {
        return color_id;
    }

    public int getUserType() {
        int userType = -1;
        try {
            userType = Integer.parseInt(user_type);
        } catch (NumberFormatException exception) {
            Log.e("IdentityUser", "exception: " + exception);
        }
        return userType;
    }

    public String getPword() {
        return pword;
    }

    public void setPword(String pword) {
        this.pword = pword;
    }
}
