package com.myctca.model;

public class AnncForm {
    private String facilityName;
    private String insuranceName;
    private String patientName;
    private String mrn;
    private String dateOfService;
    private String paymentOption;
    private String patientSignature;
    private String responsibleParty;
    private String dateSigned;

    public String getFacilityName() {
        return facilityName;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getMrn() {
        return mrn;
    }

    public String getDateOfService() {
        return dateOfService;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public String getPatientSignature() {
        return patientSignature;
    }

    public String getResponsibleParty() {
        return responsibleParty;
    }

    public String getDateSigned() {
        return dateSigned;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
    }

    public void setPatientSignature(String patientSignature) {
        this.patientSignature = patientSignature;
    }

    public void setResponsibleParty(String responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    public void setDateSigned(String dateSigned) {
        this.dateSigned = dateSigned;
    }

}
