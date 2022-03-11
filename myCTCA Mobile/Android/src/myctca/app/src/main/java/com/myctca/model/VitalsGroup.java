package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomackb on 2/28/18.
 */

public class VitalsGroup {

    private String enteredDate;
    private List<Vitals> details = new ArrayList<>();
    private List<Vitals> filterdDetails = new ArrayList<>();
    private boolean expanded = true;

    public List<Vitals> getDetails() {
        return details;
    }

    public List<Vitals> getFilteredDetails() {
        return filterdDetails;
    }

    public void setFilteredDetails(List<Vitals> filterdDetails) {
        this.filterdDetails = filterdDetails;
    }

    public String getDisplayEnteredDate() {
        return MyCTCADateUtils.getDayDateTimeStr(MyCTCADateUtils.convertStringToLocalDate(enteredDate));
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
