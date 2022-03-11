//
//  Enums.swift
//  myctca
//
//  Created by Manjunath K on 12/9/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import AzureCommunicationCalling
import AVFoundation

enum ApptRequestType: String {
    case reschedule = "reschedule"
    case cancel = "cancel"
    case new = "new"
}

enum UserPermissionType: String {
    case addAlternateContacts = "Add Alternate Contact"
    case cancelAppointment = "Cancel Appointment"
    case downloadCCDADocument = "Download CCDA Documents"
    case editAlternateContact = "Edit Alternate Contact"
    case editContact = "Edit Contact"
    case manageAssignableClaims = "Manage assignable claims"
    case manageNotifications = "Manage Notifications"
    case requestAppointments = "Request Appointment"
    case requestPrescritionRefill = "Request Prescription Refill"
    case rescheduleAppointment = "Reschedule Appointment"
    case sendSecureMessages = "Send Secure Messages"
    case submitROIForm = "Submit ROI Form"
    case transmitCCDADocuments = "Transmit CCDA Documents"
    case viewActivityLogs = "View Activity Logs"
    case viewAllergies = "View Allergies"
    case viewApppointments = "View Appointments"
    case viewBillPay = "View Bill Pay"
    case viewCarePlan = "View Care Plan"
    case viewCCDADocuments = "View CCDA Documents"
    case viewClinicalDocuments = "View Clinical Documents"
    case viewFormsLibrary = "View Forms Library"
    case viewHealthHistory = "View Health History"
    case viewHealthIssues = "View Health Issues"
    case viewImagingDocuments = "View Imaging Documents"
    case viewImmunizations = "View Immunizations"
    case viewIntegrativeDocuments = "View Integrative Documents"
    case viewLabResults = "View Lab Results"
    case viewMedicalContacts = "View Medical Contacts"
    case viewMedicalDocuments = "View Medical Documents"
    case viewPrescriptions = "View Prescriptions"
    case viewQuickLinks = "View Quick Links"
    case viewRadiationDocuments = "View Radiation Documents"
    case viewSecureMessages = "View Secure Messages"
    case viewVitalSigns = "View Vital Signs"
    case submitANNCForm = "Submit ANNC Form"
    case viewExternalLink = "View External Links"
    case viewConvertAccountSect = "View Convert Account Section"
    case viewMyAccount = "View My Account"
    case viewPatientReportedDoc = "View Patient Reported Documents"
    case viewUserLogs = "View User Logs"
    case viewSITPortal = "Submit Patient Reported Documents"
    case joinTelehealth = "Join Telehealth Meeting"
}

enum UserType:Int {
    case CARE_GIVER = 0
    case PATIENT
    case PROXY
    case NONE
}

enum MailBox: String {
    case inbox = "INBOX"
    case sent = "SENT"
    case archive = "ARCHIVE"
}

enum HealthHistoryType: String {
    case vitals = "Vitals"
    case prescriptions = "Prescriptions"
    case allergies = "Allergies"
    case immunizations = "Immunizations"
    case healthIssues = "Health Issues"
}

enum PrescriptionType: String {
    case RX = "RX"
    case HX = "HX"
}

enum MedDocType: Int {
    case clinical = 0
    case radiation = 1
    case imaging = 2
    case integrative = 3
}

enum AppUpdateMessageType {
    case manadatory, Optional, None, Failed
}

enum MyCTCASessionState : Int {
    case ACTIVE, INACTIVE, EXPIRED
}

enum PeriodSelectionType {
    case Appointments, CSDDocs, None
}

enum AudioOutRouteType:Int {
    case SPEAKER = 1001
    case PHONE_MIC, BLUETOOTH, AUDIO_OFF, OTHER, NONE
}

enum TelehealthCallMode {
    case CONNECTING, IN_LOBBY, CONNECTED
    case ON_HOLD_REMOTE, ON_HOLD_LOCAL, DISCONNECTED
    case IDLE, WAITING
}

enum ApptInputTextType {
    case COMMENTS, REASONS, NONE
}


enum CommunicationPref: String, Codable {
    case CALL = "CALL"
    case EMAIL = "EMAIL"
    case NONE = "NONE"
}

enum ApptTimePref: String, Codable {
    case MORNING = "MORNING"
    case AFTERNOON = "AFTERNOON"
    case ALL_DAY = "ALL_DAY"
    case NONE = "NONE"
}

enum AppointmentsRequestFormPage: Int {
    case RESCHEDULE_DATES = -1
    case NONE = 0
    
    case REASON = 1
    case DATES = 2
    case CONTACTS = 3
    case COMMENTS = 4
    case SUMMARY = 5
    
    case EDITMODE = 6
    case EDITMODE_COMMENTS = 7
    case EDITMODE_REASON = 8
    
    case CANCEL_COMMENTS = 10
}

enum InputTextType {
    case EMAIL, PHONE, NONE
}

enum ApptTimeState {
    case NONE, NOW, JOIN_TELEHEALTH, START_LATER, SETUP_TELEHEALTH, TOMORROW
}

