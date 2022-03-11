package com.myctca.model;

import androidx.annotation.StringDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tomackb on 2/27/18.
 */

public class HealthHistoryType implements Serializable {

    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @StringDef({VITALS, PRESCRIPTIONS, ALLERGIES, IMMUNIZATIONS, HEALTH_ISSUES})
    // Create an interface for validating int types
    public @interface HealthHistoryTypeDef {}

    public static final String VITALS = "Vitals";
    public static final String PRESCRIPTIONS = "Prescriptions";
    public static final String ALLERGIES = "Allergies";
    public static final String IMMUNIZATIONS = "Immunizations";
    public static final String HEALTH_ISSUES = "Health Issues";

    public String healthHistoryType;

    public void setHealthHistoryType(@HealthHistoryType.HealthHistoryTypeDef String hhType) {
        this.healthHistoryType = hhType;
    }


}
