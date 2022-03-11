package com.myctca.model;

/**
 * Created by tomackb on 2/27/18.
 */

public class Vitals extends HealthHistoryDoc {

    private static final String TAG = "VITALS";
    private String observationItem;
    private String value;
    private int displaySequence;

    public String getObservationItem() {
        return observationItem;
    }

    public String getValue() {
        return value;
    }

    public int getDisplaySequence() {
        return displaySequence;
    }
}
