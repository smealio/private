//
//  CTCAAlertMessageConstants.swift
//  myctca
//
//  Created by Manjunath K on 8/18/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct CommonMsgConstants {
     static let noRecordsFoundMsg = "No Records Found."
    
     static let noInternetTitle = "No internet connection detected"
     static let noInternetsubtitle = "You cannot continue without an internet connection"
    
    static let activeInternetTitle = "Internet connection detected"
    static let activeInternetsubtitle = "Resuming.."
    
    static let serverErrorMessage = "An unexpected error has occurred. If this problem persists, please contact technical support."
    static let serverErrorTitle = "Error Occurred"
    static let sessionExpiryMessage = "Your session is about to expire. Do you want to renew it?"
    static let sessionExpiryTitle = "Session Renewal"
    
    static let reportingTitle = "Diagnostic Reporting"
    static let reportingMessageText = "Do you want to allow diagnostic reporting to improve app functionality?"
    
    static let accountLockWarningTitle = "Login Warning"
    static let accountLockWarningMessageText = "Username and/or Password incorrect. Repeated invalid login attempts have been made and could eventually lock the account. Please reset password to avoid account lockout."
    
    static let phoneNumberValidationTitle = "Phone Number Validation"
    static let phoneNumberValidationMessage = "The phone number entered is invalid. Please correct the phone number to continue."
    
    static let faxNumberValidationTitle = "Fax Number Validation"
    static let faxNumberValidationMessage = "The fax number entered is invalid. Please correct the phone number to continue."
    
    static let formValidationTitle = "Form Incomplete"
    
    static let emailValidationTitle = "Email Address Validation"
    static let emailValidationMessage = "You must provide a valid email to continue."

    static let noAccessTitle = "Unable To Access"
    static let noAccessMessage = "Unable to display information - you may not have been provided the necessary permissions to view it, or there may be no information to display."
    
    static let noMsgAccessMessage = "The send secure messages functionality has been disabled."
    static let noPrescriptionRenewalAccessMessage = "The send prescription renewal functionality has been disabled."
    
    static let dateFilerTitle = "Select Date Range"
    static let dateFilerMessage = "Please enter a valid start and end date."
    static let invalidDatesMessage = "End Date cannot be earlier than the Start Date"
    
    static let requestFailedErrorTitle = "Request Failed"
    static let commonSuccessTitle = "Success"
    
    static let userAccountLockedMessage = "This user account has been locked out due to repeated invalid login attempts. Please contact Technical Support for assistance to unlock account."
    
    
    static let formExitMessage = "Your request will not be submitted. Are you sure you wish to leave the page?"
    static let submitFailMessage = "We were unable to process your request. Please try again."
}

struct ClinicalSummaryMsgConstants {
    static let downloadAlertTitle = "Download Status"
    static let downloadSuccessMessage = "Downloaded successfully. Please save the file and you can use free zip utility programs like iZip, WinZip or any other app from App Store to unzip the file."

    static let fromDateRequestMessage = "Please select Start date."
    static let toDateRequestMessage = "Please select End date."
        
    static let ClinicalSummaryAlertTitle = "Clinical Summary"

    static let passwordValidationMessage =  "You must provide a password"
    static let transmitStatusAlertTitle =  "Transmission Status"
    
    static let transmitAlertTitle =  "Transmit Document"

    static let transmitSuccessMessage = "Your email has been sent to the requested recipient."
    static let transmitFailMessage = "Failed to send email to the requested recipient."

    static let fetchClinicalSummaryErrorTitle: String = "Clinical Summary Retrieval Error"
    static let fetchClinicalSummaryErrorResponse: String = "There seems to be some kind of problem retrieving Clinical Summary data. You can try again later or call the Care Manager directly."
    
    static let securityValidMessage = "You must select a Security option."
    
    static let noRecordsMessageTitle = "Clinical Summaries Download"
}

struct AppointmentMsgConstants {
    static let noRecordsMessageTitle = "Appointments Download"
    
    static let successfulRequestResponse = "Thank you for submitting an appointment request. Your scheduling team will contact you within 1 business day to confirm the status of your request."
    static let twentyFourHourRequestMessage = "Appointment requests must be at least 24 hours in advance.\n\nFor earlier requests, please call the scheduling department directly."
    static let invalidFormResponse = "Please make sure that all of the fields in the form are complete to submit this request."
    
    static let rescheduleTitle = "Reschedule Request"
    static let cancelTitle = "Cancellation Request"
    static let defaultTitle = "Change Request"
    
    static let newSubject = "Appointment new request"
    static let rescheduleSubject = "Appointment reschedule request"
    static let cancelSubject = "Appointment cancellation request"
    
    static let dateTimeLabelReschedule = "New Date/Time:"
    static let dateTimeLabelCancel = "Date/Time:"
    
    static let successfulRequestTitle: String = "Request Sent"
    static let successfulRescheduleResponse: String = "Thank you for submitting the appointment rescheduling request. Your scheduling team will contact you within 1 business day to confirm the status of your request."
    static let successfulCancellationResponse: String = "Thank you for submitting appointment cancellation request. Your scheduling team will contact you within 1 business day to confirm the status of your request."

    static let unsuccessfulRequestTitle: String = "Request Error"
    static let unsuccessfulRequestResponse: String = "There seems to be some kind of problem with your change request. You can try again later or call the scheduling team directly."
    
    static let closeSubmissionMessage: String = "Your request will not be submitted. Are you sure you wish to leave the page?"
    
    static let noUpComingApptsMessage = "You don't have upcoming appointments but you can request a new one."
    static let noUpPastApptsMessage = "You don’t have past appointments but you can request a new one."
}

struct PrescriptionsMsgConstants {
    static let prescriptionMsgTitle = "Prescription Renewal"
    static let invalidPrescriptionMessage = "Please include at least one prescription in your renewal request."
    static let noPrescriptionMessage = "No records found."
    static let noCTCAPrescriptionMessage = "Prescription renewal requests can only be submitted for CTCA prescribed prescriptions."
    
    static let HEADER_ID: String = "prescriptionRefillInfo"
    static let REQUEST_SUBJECT: String = "Prescription Renewal Request"
    static let INVALID_FORM_RESPONSE = "There are some required fields in the form that are incomplete. Please fill in all of the fields indicated in red to send this prescription renewal request."
    static let SUCCESSFUL_SEND_RESPONSE: String = "Your renewal request has been sent."
    
    static let discontinuedPrescriptionMessage = "Only active CTCA-prescribed prescriptions can be renewed. Please contact your Care Team with prescription questions."
}

struct AppUpdateMsgConstants {
    static let appUpdateMsgTitle = "Update Available"
    static let optionalAlertMsg = "A new version of the app is available. Would you like to update?"
    static let mandatoryAlertMsg = "A new version of myCTCA is available. Please update to the latest version."
}

struct HomeMsgConstants {
    static let warningMsgTitle = "Warning"
    static let convertAccntAlertMsg = "You are leaving myCTCA and you will be logged out. Do you want to continue?"
    
    static let SITMsgTitle = "Symptom Inventory Survey"
    static let SITAlertMsg = "You have a Symptom Inventory Survey available. If you would like to take the survey, select Yes. You will need to login again on the browser to complete the survey."
}

struct SendMessageMsgConstants {
    static let successfulSendTitle: String = "Message Sent"
    static let successfulSendResponse: String = "Thank you for submitting a feedback message. A member of the Technical Support staff will contact you within 1 business day to confirm the status of your message."
    static let unsuccessfulSendTitle: String = "Message Error"
    static let unsuccessfulSendResponse: String = "There seems to be some kind of problem with sending your message. You can try again later or call the Technical Support team directly."
    static let invalidFormResponse = "Please make sure that all of the fields labelled in red are complete to submit this message."
    
}

struct MailMsgConstants {
    static let successfulSendTitle: String = "Send New Mail"
    static let successfulSendResponse: String = "Your message has been sent."
    static let invalidFormResponse = "Please make sure that all of the fields in the form are complete to send a new message."
}

struct TelehealthMsgConstants {
    static let defaultTelehealthErrorTitle = "Error"
    static let defaultTelehealthErrorMessage = "Failed to connect. Select Continue to join this meeting on Safari."
    
    static let noNetworkTelehealthErrorTitle = "Internet Connection Lost"
    static let noNetworkTelehealthMessage = "You can rejoin your meeting once you have reconnected to internet."
    
    static let leavingWarningTitle = "Warning"
    static let leavingWarninghMessage = "Your Telehealth meeting is in progress. Are you sure you want to leave?"
    static let inLobbyMessage = "When meeting starts, we'll let people know that you're waiting in the lobby."
    static let onHoldMessage = "On Hold..."
    static let leavingMessage = "Leaving..."
    static let waitingMessage = "Waiting for others to join..."
    static let noOneJoinedMsgTitle = "Sorry for the inconvenience."
    static let noOneJoinedMessage = "Your Physician is not able to join today.\n\n\nSomeone from your Care Team will be reaching out to you in the next 24 hours to reschedule."
    static let noPermissionsErrorTitle = "\"myCTCA\" Would like Access to the Microphone and Camera"
    static let noPermissionsErrorMessage = "In order to hear and see your physician during the telehealth appointment, please go to Settings and enable the Microphone and Camera for myCTCA."
    static let cantJoinMeetingMessage1 = "Your appointment does not start until:"
    static let cantJoinMeetingMessage2 = "Please join during that time."
}

