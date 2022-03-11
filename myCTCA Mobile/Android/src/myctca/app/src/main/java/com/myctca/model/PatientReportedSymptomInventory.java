package com.myctca.model;

import com.myctca.MyCTCA;
import com.myctca.util.MyCTCADateUtils;

public class PatientReportedSymptomInventory {

    private String performedDate;
    private String displayPerformedDate;
    private String itemName;
    private String observationValue;
    private String rangeValue;
    private String abnormalityCode;
    private String formatedTextEncoded;
    private String abnormalityCodeDescription;

    public String getPerformedDate() {
        return performedDate;
    }

    public String getDisplayPerformedDate() {
        return displayPerformedDate;
    }

    public String getItemName() {
        return itemName;
    }

    public String getObservationValue() {
        return observationValue;
    }

    public String getRangeValue() {
        return rangeValue;
    }

    public String getAbnormalityCode() {
        return abnormalityCode;
    }

    public String getFormatedTextEncoded() {
        return formatedTextEncoded;
    }

    public String getAbnormalityCodeDescription() {
        return abnormalityCodeDescription;
    }

    public String getPerformedDisplayDate() {
        return MyCTCADateUtils.getFullMonthString(MyCTCADateUtils.convertShortStringToLocalDate(performedDate));

    }
}
