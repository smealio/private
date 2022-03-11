package com.myctca.model;

import java.util.Date;

/**
 * Created by tomackb on 1/26/18.
 */

public class CarePlan {

    private static final String TAG = "MODEL_CARE_PLAN";
    private String systemId;
    private String patientId;
    private String visitId;
    private Date primaryDocumentAuthoredDate;
    private String primaryDocumentAuthorOccupationCode;
    private String primaryDocumentAuthor;
    private String documentName;
    private String documentStatus;
    private String documentText;
    private Date lastModifiedDate;
    private boolean allowExport;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        systemId = systemId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public Date getPrimaryDocumentAuthoredDate() {
        return primaryDocumentAuthoredDate;
    }

    public void setPrimaryDocumentAuthoredDate(Date primaryDocumentAuthoredDate) {
        primaryDocumentAuthoredDate = primaryDocumentAuthoredDate;
    }

    public String getPrimaryDocumentAuthor() {
        return primaryDocumentAuthor;
    }

    public void setPrimaryDocumentAuthor(String primaryDocumentAuthor) {
        primaryDocumentAuthor = primaryDocumentAuthor;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        documentName = documentName;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        documentStatus = documentStatus;
    }

    public String getDocumentText() {
        return documentText;
    }

    public void setDocumentText(String documentText) {
        documentText = documentText;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean getAllowExport() {
        return allowExport;
    }

    public void setAllowExport(boolean allowExport) {
        this.allowExport = allowExport;
    }
}
