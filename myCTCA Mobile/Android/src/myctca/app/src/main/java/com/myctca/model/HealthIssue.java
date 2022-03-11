package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

import java.util.Date;

public class HealthIssue {

    private String shortName;
    private String name;
    private String status;
    private String enteredDate;

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Date getEnteredDate() {
        return MyCTCADateUtils.convertShortStringToLocalDate(enteredDate);
    }
}
