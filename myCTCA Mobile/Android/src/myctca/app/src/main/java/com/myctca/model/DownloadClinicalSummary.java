package com.myctca.model;

import java.util.List;

public class DownloadClinicalSummary {
    List<String> documentId;
    String filePass;

    public DownloadClinicalSummary(List<String> selectedClinicalSummary, String filePassword) {
        this.documentId = selectedClinicalSummary;
        this.filePass = filePassword;
    }
}
