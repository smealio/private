//
//  CTCAUIConstants.swift
//  myctca
//
//  Created by Manjunath K on 7/9/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

enum FormValidationResults:Int {
    case VALID_FORM = 0
    case INVALID_FORM
    case INVALID_PHONE
    case INVALID_FAX
    case INVALID_EMAIL
    case NONE
}

struct CTCAUIConstants {
    static let maxLengthForPhoneTextField          = 15
    static let minLengthForPhoneTextField          = 10
    static let doneBarHeightForTextFields          = 50
    static let placeHolderString                  = "Required"
    static let homePageMessageString               = "In the meantime, please visit the myCTCA Portal via your browser at https://www.myctca.com or by selecting the button below."
}

struct ROIUIViewConstants {
    static let descriptionTitle    = "If you require a Release of your Medical Information, there are now two ways to speed up the process:"
    static let option1Title        = "1.  You can download and complete the following document prior to your upcoming visit and deliver it to the ROI office at your treatment facility."
    static let option2Title        = "2.  You can also complete the following form online and submit it directly."
    
    static let bottomDetailText    = "For further questions regarding the document or form, please contact the Health Information Management (ROI) team at your treatment facility."
    
    static let dobMessage: String = "Please enter your date of birth.\n\nTip: Change the year first."
    static let pickupMessage: String = "Please enter the date that the information should be picked up."
    static let datesOfServiceRestrictionsPlaceholder = "Please indicate any restrictions:"
    
    static let authorizationValid: String = "This authorization is valid for release of information for the dates listed on the request."
    static let authorizationBullet0 : String = "I understand the above medical center may not condition treatment, payment, enrollment or eligibility for benefits on whether I sign this authorization."
    static let authorizationBullet1 : String = "I understand that the use or disclosure of my health information is voluntary except in accordance with federal or state law and any mandatory reporting requirements."
    static let authorizationBullet2 : String = "I understand that once my health information is disclosed it may be re-disclosed by the recipient and the information may not be protected by federal or state privacy laws or regulations."
    static let authorizationBullet3 : String = "A photocopy or facsimile of this authorization will be treated in the same manner as the original"
    static let authorizationBullet4 : String = "I understand the information in my health record may contain information concerning Human Immunodeficiency Virus (HIV) testing and disease, psychiatric or behavioral health information or information concerning history of substance abuse and treatment."
    static let authorizationBullet5 : String = "I understand that this authorization will expire one year from the date signed on this form. Authorization may be revoked at any time by submitting a request in writing to the HIM department; the revocation will not apply to any information already released."
    static let authorizationBullet6 : String = "I understand that I may request a copy of this authorization."
    
    static let invalidFormResponse = "Please make sure that all of the fields in this form marked in red are complete and valid to submit this request."
    static let successfulSendTitle: String = "ROI Authorization"
    static let successfulSendResponse: String = "The Release of Information (ROI) Authorization Form has been sent to the Health Information Management (HIM) Team at the selected location for review.\n\nA notification email with this request has also been sent to the email address associated with this Patient account. The email has been encrypted for security purposes and will require authentication to view it."
    static let unsuccessfulSendTitle: String = "ROI Request Error"
    static let unsuccessfulSendResponse: String = "There seems to be some kind of problem sending your Release of Information request. You can try again later or call the Health Information Management (HIM) Team directly."
}

struct ClinicalSummaryUIConstants {
    
    static let firstLine = "You may password protect the attachment file by entering a password below."
    
    static let warningText = "You will have to provide the password to your email recipient separately for it to be opened."
    
    static let fileText = "Your health data will be attached as a ZIP file containing a human readable HTML file, as well as a machine readable XML file for importing into other systems."
    
    static let sendSecureText = "The encryption method used to password-protect the file(s) uses a more secure method that operating systems like Microsoft Windows may not support natively. Many zip utility programs do support this method - an example of one that is available to download and install for free is called 7-Zip. Your recipient will need to have the more advanced version of this technology to open your password-protected file(s)."
    
    static let passwordText = "You can password protect the downloaded file by entering a password below. If you don't wish to password protect the file leave the password field blank."

    static let downloadText = "Click the download button to begin downloading the file."

    static let warningTextDwnld = "Do not download this file if you are using a public computer."
    
    static let selectAll = "Select All"
    static let deSelectAll = "Deselect All"
}

struct ANNCUIConstants {
    static let statement1 = "During the Public Health Emergency (PHE), many insurance payers were providing coverage for telemedicine visits. However, this was only a temporary arrangement. It is possible that if your medical insurance doesn’t pay, you may be financially liable for the non-covered service. If you have received a request from our insurance verification team, please complete the following Advanced Notice of Non-Coverage form at least 48 hours BEFORE your scheduled telehealth visit, otherwise this telehealth visit will have to be canceled."
    
    static let statement2 = "You can complete the following form online and submit it directly."
    
    static let statement3 = "You can download your most recently submitted ANNC form here."
    
    static let anncStm1 = "In some cases, your insurance benefits for telehealth services may be changing. Please check with your insurance to determine if you have telehealth benefits for CTCA telehealth services prior to booking your telehealth appointment."

    static let anncStm2P1 = "Medical insurance plans do not pay for everything, even some care that you or your health care provider have good reason to think you need. We expect your medical insurance may not pay for the "
    
    static let anncStm2P2 = "below. If your medical insurance doesn't pay, you may be financially liable for the one-covered service."
    
    static let procedureTitle = "Procedure"
    static let reasonTitle = "Reason Insurance May Not Pay"
    static let costTitle = "Estimated Cost"
    
    static let procedureType = "Telehealth appointment"
    static let reasonType = "Does not have telehealth benefits for services provided by CTCA"
    static let costType = "Estimate price range:"
    static let cost1 = "New patient: $79.75-$495.55"
    static let cost2 = "Return patient: $26.95-$325.05"

    static let needToKnowText = "WHAT YOU NEED TO KNOW:"
    static let needToKnowPt1Text = "- Read this notice, so you can make an informed decision about your care."
    static let needToKnowPt2Text = "- Ask us any questions that you may have after you finish reading."
    static let needToKnowPt3P1Text = "- Choose an option below about whether to receive the "
    static let needToKnowPt3P2Text = " listed above."
    static let needToKnowNoteText = "If you choose Option 1 or 2, we may help you to use any other insurance that you might have, but your medical insurance plan cannot require us to do this."
    
    static let optionsText = "Check only one box. We cannot choose a box for you."
    
    static let option1Part1Text = "Option 1. I "
    static let option1Part2Text = " the Telehealth services listed above. You may ask to be paid now, but I also want my medical insurance billed for an official decision on payment, which is sent to me on an Explanation of Benefits (EOB). I understand that if my medical insurance doesn’t pay, I am responsible for payment, but "
    static let option1Part3Text = " to my medical insurance by contacting my plan to inquire how. If my medical insurance does pay, you will refund any payments I made to you, less co-pays or deductibles."
    
    static let option2Part1Text = "Option 2. I "
    static let option2Part2Text = " the Telehealth services listed above. I understand with this choice I am "
    static let option2Part3Text = " responsible for payment, and "
    static let option2Part4Text = "I cannot appeal to see if my medical insurance would pay."
    
    static let additionalInfoTitleText = "Additional Information: "
    static let additionalInfoP1Text = "Based on the information we currently have, we are providing this notice. It is not an official decision until the claim is submitted to your medical insurance plan."
    static let additionalInfoP2Text = "This form is not be used for Medicare patients."
    
    static let signConfText = "Signing below means that you have received and understand this notice. A copy of this notice can be made available at any time."
    
    //messages
    static let invalidFormResponse = "Please make sure that all of the fields in this form marked in red are complete and valid to submit this request."
    static let successfulSendTitle: String = "Information"
    static let successfulSendResponse: String = "Your Advanced Notice of Non-Coverage form was submitted."
    static let unsuccessfulSendTitle: String = "ANNC Form Error"
    static let unsuccessfulSendResponse: String = "There seems to be some kind of problem submitting your ANNC form. You can try again later or call the Health Information Management (HIM) Team directly."
    
    static let teleHealthText: String = "Telehealth Services "
}

struct ActivityIndicatorMsgs {
    //appointments
    static let retriveApptText          = "Retrieving Appointment Data..."
    static let refreshApptText          = "Refreshing Appointment data…"
    static let sendApptRequest          = "Sending Request..."
    static let downloadApptText         = "Downloading Appointments..."
    
    //lab results
    static let retriveLabResultsText    = "Retrieving Lab Result Data..."
    static let refreshLabResultsText    = "Refreshing Lab Result Data..."
    static let downloadLabResultsText   = "Downloading Lab Results..."
    
    //Secure mail
    static let retriveInboxMailsText    = "Retrieving Inbox Secure Mail..."
    static let retriveSentMailsText     = "Retrieving Sent Secure Mail..."
    static let retriveArchivedMailsText = "Retrieving Archived Secure Mail..."
    static let refreshMailText          = "Refreshing Secure Mail..."
    static let archiveMailText          = "Archiving Mail..."
    static let sendMailText             = "Sending Secure Mail..."
    static let retriveCareTeamText      = "Retrieving Care Team Data..."
    static let updateSecureMails        = "Updating Mail Data..."
    
    //Health history
    static let retriveVitalsText           = "Retrieving Vitals..."
    static let retrivePrescriptionsText    = "Retrieving Prescriptions..."
    static let retriveAllergiesText        = "Retrieving Allergies..."
    static let retriveImmunizationsText    = "Retrieving Immunizations..."
    static let retriveHealthIssuesText     = "Retrieving Health Issues..."
    static let downloadVitalsText          = "Downloading Vitals..."
    static let downloadPrescriptionsText   = "Downloading Prescriptions..."
    static let downloadAllergiesText       = "Downloading Allergies..."
    static let downloadImmunizationsText   = "Downloading Immunizations..."
    static let downloadHealthIssuesText    = "Downloading Health Issues..."
    
    //Med docs
    static let retriveClinicalText           = "Retrieving Clinical Documents..."
    static let retriveRadiationText          = "Retrieving Radiation Documents..."
    static let retriveImagingText            = "Retrieving Imaging Documents..."
    static let retriveIntegrativeText        = "Retrieving Integrative Documents..."
    static let retriveClinicalSummariesText  = "Retrieving Clinical Summaries…"
    static let downloadCarePlanText          = "Downloading Care Plan Document..."
    static let downloadCSText                = "Downloading Clinical Summary Document..."
    static let transmitCSText                = "Transmitting Clinical Summary Document..."
    static let downloadClinicalText          = "Downloading Clinical Document..."
    static let downloadRadiationText         = "Downloading Radiation Document..."
    static let downloadImagingText           = "Downloading Imaging Document..."
    static let downloadIntegrativeText       = "Downloading Integrative Document..."
    static let retriveSClinicalText          = "Retrieving Clinical Document..."
    static let retriveSRadiationText         = "Retrieving Radiation Document..."
    static let retriveSImagingText           = "Retrieving Imaging Document..."
    static let retriveSIntegrativeText       = "Retrieving Integrative Document..."
    static let retriveSClinicalSummariesText = "Retrieving Clinical Summary…"
    static let refreshClinicalText           = "Refreshing Clinical Documents..."
    static let refreshRadiationText          = "Refreshing Radiation Documents..."
    static let refreshImagingText            = "Refreshing Imaging Documents..."
    static let refreshIntegrativeText        = "Refreshing Integrative Documents..."
    static let refreshClinicalSummariesText  = "Refreshing Clinical Summaries…"
}

struct CareGiverMsgs {
    static let noPatientsAssingText = "No patients have shared their information with you. Please contact the patient directly."
}

struct ImportantNumbersStrings {
    static let numberTitles = [
        "General Inquiries",
        "Technical Support",
        "Care Management",
        "Scheduling",
        "Travel & Accommodations",
        "Medical Records",
        "Financial Counseling",
        "Billing",
        "Pharmacy"
    ]
    
    static let numberDescs = [
        "For concerns regarding your care, access to medical records and any general questions you might have.",
        "For questions related to creating a portal account, accessing the portal, and password resets.",
        "Connect with a care manager (RN) for questions about your treatment, symptoms or other related concerns you may have.",
        "For changes in your appointments, request for new appointments.",
        "For help with booking flights, car service, hotel stays, rates.",
        "To access patient medical records and help with forms.",
        "For assistance with insurance concerns and financial barriers.",
        "For questions and concerns about services rendered, insurance and billing statements.",
        "Connect with a pharmacist for prescription refills, requesting supplements or other related questions."
    ]
}

