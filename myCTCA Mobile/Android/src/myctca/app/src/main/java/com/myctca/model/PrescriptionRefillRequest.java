package com.myctca.model;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionRefillRequest {

    public List<String> selectedPrescriptions = new ArrayList<>();
    public String patientPhone;
    public String pharmacyName;
    public String pharmacyPhone;
    public String comments;
    public List<CareTeam> to;

}
