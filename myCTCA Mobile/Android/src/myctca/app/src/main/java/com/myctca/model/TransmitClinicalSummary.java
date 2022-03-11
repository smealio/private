package com.myctca.model;

import java.util.ArrayList;

public class TransmitClinicalSummary {
    ArrayList<String> documentId;
    String directAddress;
    String filePass;

    public TransmitClinicalSummary(ArrayList<String> selectedClinicalSummary, String directAddress, String filePassword) {
        this.documentId = selectedClinicalSummary;
        this.directAddress = directAddress;
        this.filePass = filePassword;
    }
}
