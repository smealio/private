package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

import java.util.Date;

/**
 * Created by tomackb on 2/15/18.
 */

public class MedDoc {

    private String documentId;
    private String documentAuthoredDate;
    private String documentAuthor;
    private String documentAuthorOccupationCode;
    private String documentName;
    private String documentText;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentAuthoredDate() {
        return documentAuthoredDate;
    }

    public void setDocumentAuthoredDate(String documentAuthoredDate) {
        this.documentAuthoredDate = documentAuthoredDate;
    }

    public String getDocumentAuthor() {
        return documentAuthor;
    }

    public void setDocumentAuthor(String documentAuthor) {
        this.documentAuthor = documentAuthor;
    }

    public String getDocumentAuthorOccupationCode() {
        return documentAuthorOccupationCode;
    }

    public void setDocumentAuthorOccupationCode(String documentAuthorOccupationCode) {
        this.documentAuthorOccupationCode = documentAuthorOccupationCode;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentText() {
        return documentText;
    }

    public void setDocumentText(String documentText) {
        this.documentText = documentText;
    }

    public String getSlashedDateFullYearString() {
        return MyCTCADateUtils.getSlashedDateFullYearStr(MyCTCADateUtils.convertShortStringToLocalDate(documentAuthoredDate));
    }
}
