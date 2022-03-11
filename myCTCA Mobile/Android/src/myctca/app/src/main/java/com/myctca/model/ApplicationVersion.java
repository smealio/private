package com.myctca.model;

public class ApplicationVersion {
    private String id;
    private String platform;
    private String versionNumber;
    private boolean mandatory;

    public String getId() {
        return id;
    }

    public String getPlatform() {
        return platform;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public boolean getMandatory() {
        return mandatory;
    }
}
