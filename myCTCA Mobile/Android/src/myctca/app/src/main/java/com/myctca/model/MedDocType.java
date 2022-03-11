package com.myctca.model;

/**
 * Created by tomackb on 2/15/18.
 */

public class MedDocType {

    public static final String CLINICAL = "Clinical";
    public static final String RADIATION = "Radiation";
    public static final String IMAGING = "Imaging";
    public static final String INTEGRATIVE = "Integrative";

    public final String docType;

    public MedDocType(String docType) {
        this.docType = docType;
    }
}
