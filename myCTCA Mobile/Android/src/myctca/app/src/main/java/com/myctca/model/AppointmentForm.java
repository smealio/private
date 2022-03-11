package com.myctca.model;

public class AppointmentForm {
    private String appointmentId;
    private String from;
    private String subject;
    private String appointmentDate;
    private String phoneNumber;
    private String comments;
    private String facilityTimeZone;

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setFacilityTimeZone(String facilityTimeZone) {
        this.facilityTimeZone = facilityTimeZone;
    }
}
