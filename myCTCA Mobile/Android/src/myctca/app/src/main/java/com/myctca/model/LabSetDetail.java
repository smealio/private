package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tomackb on 1/3/18.
 */

public class LabSetDetail implements Serializable {
    private String id;
    private String performedDateTime;
    private String itemName;
    private String abnormalityCodeCalculated;
    private String abnormalityCodeDescription;
    private String result;
    private String normalRange;
    private String notes;
    private int displaySequence;

    public String getId() {
        return id;
    }

    public Date getPerformedDateTime() {
        return MyCTCADateUtils.convertStringToLocalDate(performedDateTime);
    }

    public String getItemName() {
        return itemName;
    }

    public String getAbnormalityCodeCalculated() {
        return abnormalityCodeCalculated;
    }

    public String getAbnormalityCodeDescription() {
        return abnormalityCodeDescription;
    }

    public String getResult() {
        return result;
    }

    public String getNormalRange() {
        return normalRange;
    }

    public String getNotes() {
        return notes;
    }

    public int getDisplaySequence() {
        return displaySequence;
    }
}
