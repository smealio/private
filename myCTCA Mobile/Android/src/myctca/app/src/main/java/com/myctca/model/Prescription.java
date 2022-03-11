package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

import java.io.Serializable;

public class Prescription implements Serializable {

    private String prescriptionId;
    private String drugName;
    private String instructions;
    private String startDate;
    private String expireDate;
    private String prescriptionType;
    private String statusType;
    private String comments;
    private boolean allowRenewal;

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartDateAsSlashDate() {
        if (startDate == null || startDate.equals("")) {
            return "Not provided";
        }
        return MyCTCADateUtils.getSlashedDateFullYearStr(MyCTCADateUtils.convertShortStringToLocalDate(startDate));
    }


    public String getExpireDate() {
        return expireDate;
    }

    public String getExpireDateAsSlashDate() {
        if (expireDate == null || expireDate.equals("")) {
            return "Not provided";
        }
        return MyCTCADateUtils.getSlashedDateFullYearStr(MyCTCADateUtils.convertShortStringToLocalDate(expireDate));
    }

    public String getPrescriptionType() {
        return prescriptionType;
    }

    public String getStatusType() {
        return statusType;
    }

    public String getComments() {
        return comments;
    }

    public boolean getAllowRenewal() {
        return allowRenewal;
    }
}

/**
 * [
 * {
 * "prescriptionId": "6",
 * "drugName": "Aloe vera Juice",
 * "instructions": "10 mL orally once a day x 30 days.  To help decrease constipation.   ",
 * "startDate": "2016-11-04",
 * "expireDate": "2017-02-01",
 * "prescriptionType": "CTCA-Prescribed",
 * "statusType": "Active",
 * "comments": "",
 * "allowRenewal": true
 * },
 * ...
 * ]
 */
