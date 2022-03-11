package com.myctca.model;

import java.util.List;

public class RoiDetails {
    private List<MedicalCenter> facilities;
    private List<String> deliveryMethods;
    private List<String> authorizationActions;
    private List<String> purposes;
    private List<String> disclosureInformationList;
    private List<String> highlyConfidentialInformationList;

    public List<MedicalCenter> getFacilities() {
        return facilities;
    }

    public List<String> getDeliveryMethods() {
        return deliveryMethods;
    }

    public List<String> getAuthorizationActions() {
        return authorizationActions;
    }

    public List<String> getPurposes() {
        return purposes;
    }

    public List<String> getDisclosureInformationList() {
        return disclosureInformationList;
    }

    public List<String> getHighlyConfidentialInformationList() {
        return highlyConfidentialInformationList;
    }
}
