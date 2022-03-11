package com.myctca.model;

public class ImpersonatedUserProfile {
    private String ToCtcaUniqueId;
    private boolean IsImpersonating;

    public String getToCtcaUniqueId() {
        return ToCtcaUniqueId;
    }

    public void setToCtcaUniqueId(String toCtcaUniqueId) {
        ToCtcaUniqueId = toCtcaUniqueId;
    }

    public boolean isImpersonating() {
        return IsImpersonating;
    }

    public void setImpersonating(boolean impersonating) {
        IsImpersonating = impersonating;
    }
}
