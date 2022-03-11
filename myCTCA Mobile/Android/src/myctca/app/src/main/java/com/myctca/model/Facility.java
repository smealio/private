package com.myctca.model;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by tomackb on 12/18/17.
 */

public class Facility implements Serializable {

    private String name = "";
    private String displayName = "";
    private String shortDisplayName = "";
    private FacilityAddress address;
    private String mainPhone = "";
    private String schedulingPhone = "";
    private String schedulingSecondaryPhone = "";
    private String accommodationsPhone = "";
    private String transportationPhone = "";
    private String himroiPhone = "";
    private String travelAndAccommodationsPhone = "";
    private String careManagementPhone = "";
    private String financialCounselingPhone = "";
    private String billingPhone = "";
    private String pharmacyPhone = "";

    public String getFinancialCounselingPhone() {
        return financialCounselingPhone;
    }

    public String getShortDisplayName() {
        return shortDisplayName;
    }

    public String getSchedulingSecondaryPhone() {
        return schedulingSecondaryPhone;
    }

    public String getTravelAndAccommodationsPhone() {
        return travelAndAccommodationsPhone;
    }

    public String getCareManagementPhone() {
        return careManagementPhone;
    }

    public String getBillingPhone() {
        return billingPhone;
    }

    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public FacilityAddress getAddress() {
        return address;
    }

    public String getMainPhone() {
        return mainPhone;
    }

    public String getSchedulingPhone() {
        return schedulingPhone;
    }

    public String getAccommodationsPhone() {
        return accommodationsPhone;
    }

    public String getTransportationPhone() {
        return transportationPhone;
    }


    public String getHimroiPhone() {
        return himroiPhone;
    }


    public String getStreetAddress() {
        String streetAddress = getAddress().getLine1();
        if (getAddress().getLine2() != null && !getAddress().getLine2().equals("")) {
            Log.d("FACILITY", "ADDRESS2: " + getAddress().getLine2() + ".");
            streetAddress += "\n" + getAddress().getLine2();
        }
        return streetAddress;
    }

    public String getCityStateZip() {
        return getAddress().getCity() + ", " + getAddress().getState() + " " + getAddress().getPostalCode();
    }
}
