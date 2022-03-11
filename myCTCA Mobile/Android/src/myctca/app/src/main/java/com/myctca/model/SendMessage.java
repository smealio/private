package com.myctca.model;

public class SendMessage {
    private String userName;
    private String emailAddress;
    private String subject;
    private String phoneNumber;
    private String areaOfConcern;
    private String facility;
    private String comments;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAreaOfConcern(String areaOfConcern) {
        this.areaOfConcern = areaOfConcern;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
