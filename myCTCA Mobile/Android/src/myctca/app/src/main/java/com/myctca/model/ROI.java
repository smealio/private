package com.myctca.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomackb on 2/5/18.
 */

public class ROI {

    public String selectedFacility;
    public String firstName;
    public String lastName;
    public String dateOfBirth;
    public List<String> selectedDeliveryMethod = new ArrayList<>();
    public String pickupDate;
    public List<String> selectedAuthorizationAction = new ArrayList<>();
    public String facilityOrIndividual;
    public String address;
    public String city;
    public String state;
    public String zip;
    public String phoneNumber;
    public String fax;
    public String emailAddress;
    public List<String> selectedPurposes = new ArrayList<>();
    public boolean beginningOfTreatment = false;
    public String beginDate;
    public boolean endOfTreatment = false;
    public String endDate;
    public String restrictions;
    public List<String> selectedDisclosureInformation = new ArrayList<>();
    public String disclosureInformationOther;
    public List<String> selectedHighlyConfidentialDiscolosureInformation = new ArrayList<>();
    public String signature;
    public String patientRelation;

}
