package com.myctca.model;

public class StoredPreferences {
    private String previousUsername;
    private boolean hasPreviousUsername;
    private boolean isFingerAuthEnabled;
    private boolean isFingerAuthPrefSet;
    private boolean isFingerAuthSupported;

    public String getPreviousUsername() {
        return previousUsername;
    }

    public void setPreviousUsername(String previousUsername) {
        this.previousUsername = previousUsername;
    }

    public boolean getHasPreviousUsername() {
        return hasPreviousUsername;
    }

    public void setHasPreviousUsername(boolean hasPreviousUsername) {
        this.hasPreviousUsername = hasPreviousUsername;
    }

    public boolean isHasPreviousUsername() {
        return hasPreviousUsername;
    }

    public boolean isFingerAuthEnabled() {
        return isFingerAuthEnabled;
    }

    public void setFingerAuthEnabled(boolean fingerAuthEnabled) {
        isFingerAuthEnabled = fingerAuthEnabled;
    }

    public boolean isFingerAuthPrefSet() {
        return isFingerAuthPrefSet;
    }

    public void setFingerAuthPrefSet(boolean fingerAuthPrefSet) {
        isFingerAuthPrefSet = fingerAuthPrefSet;
    }

    public boolean isFingerAuthSupported() {
        return isFingerAuthSupported;
    }

    public void setFingerAuthSupported(boolean fingerAuthSupported) {
        isFingerAuthSupported = fingerAuthSupported;
    }
}
