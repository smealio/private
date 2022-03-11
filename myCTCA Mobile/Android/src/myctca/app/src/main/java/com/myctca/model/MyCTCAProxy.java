package com.myctca.model;

/**
 * Created by tomackb on 2/23/18.
 */

public class MyCTCAProxy {

    private String toCtcaUniqueId;
    private String relationshipType;
    private String firstName;
    private String lastName;
    private boolean isImpersonating;

    public String getFullName() {
        return firstName + " " + lastName;
    }


    public String getToCtcaUniqueId() {
        return toCtcaUniqueId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isImpersonating() {
        return isImpersonating;
    }
}
