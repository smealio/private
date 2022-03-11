package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tomackb on 11/13/17.
 */

public class LabResult implements Serializable {
    private String performedDate;
    private String collectedBy;
    private List<LabSet> summary = new ArrayList<>();

    // Getters Setters
    public String getCollectedBy() {
        return collectedBy;
    }

    public Date getPerformedDate() {
        return MyCTCADateUtils.convertShortStringToLocalDate(performedDate);
    }

    public String getPerformedDateStr() {
        return performedDate;
    }

    public List<LabSet> getSummary() {
        return summary;
    }

    public String getSummaryNames(String separatedBy) {
        String setNames = "";

        for (LabSet labSet : getSummary()) {
            if (setNames.equals("")) {
                setNames += labSet.getName();
            } else {
                setNames += separatedBy + labSet.getName();
            }
        }
        return setNames;
    }
}
