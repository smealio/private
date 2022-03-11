package com.myctca.model;

public class PhoneNumber {
    private String systemId;
    private String phone;
    private int type;
    private String phoneType;
    private boolean primary;
    private String createdBy;
    private String changeType;

    public String getPhoneType() {
        return phoneType.toLowerCase();
    }

    public String getPhone() {
        return phone;
    }
}