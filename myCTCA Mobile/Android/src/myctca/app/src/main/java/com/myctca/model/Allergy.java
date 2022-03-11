package com.myctca.model;

/**
 * Created by tomackb on 3/7/18.
 */

public class Allergy extends HealthHistoryDoc {

    private String substance;
    private String reactionSeverity;
    private String status;

    public String getSubstance() {
        return substance;
    }

    public String getReactionSeverity() {
        return reactionSeverity;
    }

    public String getStatus() {
        return status;
    }
}
