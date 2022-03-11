package com.myctca.model;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRequestData {
    private String appointmentId = "";
    private String appointmentDate = "";
    private String from = "";
    private String subject = "";
    private String phoneNumber = "";
    private String Email = "";
    private String communicationPreference = "";
    private String reason = "";
    private String additionalNotes = "";
    private List<AppointmentDateTime> appointmentDateTimes = new ArrayList<>();

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCommunicationPreference() {
        return communicationPreference;
    }

    public void setCommunicationPreference(String communicationPreference) {
        this.communicationPreference = communicationPreference;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public List<AppointmentDateTime> getAppointmentDateTimes() {
        return appointmentDateTimes;
    }

    public void setAppointmentDateTimes(List<AppointmentDateTime> appointmentDateTimes) {
        this.appointmentDateTimes = appointmentDateTimes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

