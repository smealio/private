package com.myctca.model;

import java.util.List;

public class ApplicationInfo {
    private String techSupportNumber;
    private List<ApplicationVersion> applicationVersions;

    public List<ApplicationVersion> getApplicationVersions() {
        return applicationVersions;
    }

    public String getTechSupportNumber() {
        return techSupportNumber;
    }
}
