package com.myctca.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserPreference {
    private int userId;
    private String userPreferenceType;
    private String userPreferenceValue;

    public UserPreference() {

    }

    public UserPreference(JSONObject object) {
        try {
            userId = object.getInt("userId");
            userPreferenceValue = object.getString("userPreferenceValue");
            userPreferenceType = object.getString("userPreferenceType");
        } catch (JSONException e) {
            CTCAAnalyticsManager.createEventForSystemExceptions("UserPreference", CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, e);
            e.printStackTrace();
        }
    }

    public String getUserPreferenceType() {
        return userPreferenceType;
    }

    public String getUserPreferenceValue() {
        return userPreferenceValue;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserPreferenceType(String userPreferenceType) {
        this.userPreferenceType = userPreferenceType;
    }

    public void setUserPreferenceValue(String userPreferenceValue) {
        this.userPreferenceValue = userPreferenceValue;
    }

}
