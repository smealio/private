package com.myctca.model;

import java.util.List;

public class SymptomInventory {
    private String date;
    private List<PatientReportedSymptomInventory> symptomInventories;
    private boolean expanded = false;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<PatientReportedSymptomInventory> getSymptomInventories() {
        return symptomInventories;
    }

    public void setSymptomInventories(List<PatientReportedSymptomInventory> symptomInventories) {
        this.symptomInventories = symptomInventories;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
