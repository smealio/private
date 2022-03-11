
//  MyCTCAConstants.swift
//  myCTCA
//
//  Created by Tomack, Barry on 10/28/16.
//  Copyright © 2016 CTCA. All rights reserved.
//

import Foundation
import UIKit

/**
 Aside from the server urls, the strings are here to make it easier to do localization later
 */
struct MyCTCAConstants {
    
    struct UserPrefs {
        static let isRegistering            = "kIsRegistering"
        static let successfullyLoggedIn     = "kSuccessfullyLoggedIn"
        static let username                 = "kUsername"
        static let userFirstName            = "kUserFirstName"
        static let latestSignOnDate         = "kLatestSignOnDate"
        static let previousSignOnDate       = "kPreviousSignOnDate"
        static let enableBiometrics         = "kEnableBiometrics"
        static let biometricPreferenceSet   = "kBiometricPreferenceSet"
        
        static let deviceTokenString        = "kDeviceTokenString"
        static let installationId           = "kInstallationDevice"
        static let enterBackground          = "kEnterBackground"
        static let loginFailureAttempts     = "kLoginFailureAttempts"
    }
    
    struct Notifications {
        static let loginSuccessful          = "kLoginSuccessful"
        static let userProfileUpdated       = "kUserProfileUpdated"
    }
    
    struct ExternalURL {
        static let appleTouchIDURL          = "https://support.apple.com/en-us/HT204587"
        static let appleFaceIDURL           = "https://support.apple.com/en-us/HT208108"
        static let privacyPolicyFileName    = "https://www.cancercenter.com/privacy-policy/"
        static let faqFileName              = "https://www.cancercenter.com/privacy-policy/"
        static let termsConditionsFileName  = "https://www.cancercenter.com/privacy-policy/"
        static let portalLink               = "https://www.myctca.com"
    }
    
    struct FormValidation {
        static let requiredFieldTitle               = "Required Field"
        static let invalidFormatTitle               = "Invalid Format"
        static let illegalCharactersTitle           = "Illegal Characters"
        static let checkPasswordTitle               = "Check Password"
        static let loginRequiredFields              = "Please enter your CTCA ID and password."
    }
    
    struct ConnectivityErrorMessage {
        static let responseEmailExists              = "This CTCA ID is already in use."
        static let responseResumeReg                = "The user profile for this CTCA ID is incomplete. Would you like to resume registration?"
        static let responseRegistrationFailure      = "The registration process failed for unknown reasons. Please try again at a later time."
        static let responseEmailAlreadyConfirmed    = "This email has already been confirmed. If you are having trouble logging in, trying resetting your password."
        static let responseInvalidOrExpiredCode     = "The code you entered was invalid or expired. Please check that it matches the code in your email or request a new code and try again."
        static let responseInvalidCodeFormat        = "There was a problem with the code you entered. Please check that it matches the code in your email or request a new code and try again."
    }
    
    // MARK - Form Constants
    struct Form {
        static let textCharLimit = 50
        static let securityCharLimit = 256
        static let passwordCharMin = 8
    }
    
    struct FormText {
        // View Titles
        static let viewTitleTouchID             = "TouchID™"
        static let viewTitleTermsConditons      = "Terms and Conditons"
        
        // Form Sections
        static let formSectionName              = "Name"
        static let formSectionEmail             = "Email"
        
        // Placeholder Text
        static let placeholderFirstName         = "First name"
        static let placeholderLastName          = "Last name"
        static let placeholderEmail             = "name@example.com"
        static let placeholderPassword          = "Password"
        
        // Button Text
        static let buttonNext                   = "Next"
        static let buttonEnableTouchId          = "Enable TouchID™"
        static let buttonNoThanks               = "No Thanks"
        static let buttonYes                    = "Yes"
        static let buttonNo                     = "No"
        static let buttonVerify                 = "Verify"
        static let buttonOK                     = "OK"
        static let buttonCancel                 = "Cancel"
        static let buttonTermsConditions        = "Terms and Conditions"
        static let buttonPrivacyPolicy          = "Privacy Practices"
        static let buttonWhatsThis              = "What's this?"
        static let buttonRegistrationCode       = "Enter Registration Code"
        static let buttonSendNewCode            = "Send a new code"
        static let buttonShow                   = "Show"
        static let buttonHide                   = "Hide"
        static let buttonSignIn                 = "Sign In"
        static let buttonSignOut                = "Sign Out"
        static let buttonJoinNow                = "Join Telehealth Appointment"
        static let buttonSetupGuide             = "View Setup Guide"
        static let buttongetDirections          = "Get Directions"
        
    }
    
    struct SettingsText {
        static let signInText                   = "You last signed in on"
        static let firstSignIn                  = "This appears to be your first time signing in."
        static let settingsMyProfile            = "My Profile"
        static let settingsAdvanced             = "Advanced"
        static let settingsSecurityQuestions    = "Security Questions"
        static let settingsTouchId              = "TouchID™"
        static let signOutMessage               = "Are you sure you want to sign out?"
        static let settingsVersion              = "v"
        static let settingsHi                   = "Hi"
        static let settingsSignOut              = "Sign Out"
        static let settingsAccessibility        = "Accessibility"
        static let settingsHelpSupport          = "Help and Support"
        static let settingsSubmitFeedback       = "Submit Feedback"
        static let settingsSampleMessageTitle   = "Sample Message"
        static let settingsSampleMessageText    = "This is a sample message so that you can preview how messages will look with the font size you set."
        static let settingsTextSizeInstructions = "Use the slider to adjust the message text to your preferred reading size."
        static let settingsTextSizeSliderInstructions = "Touch to drag the slider below."
        static let settingsEdit                 = "Edit"
    }
    
    struct ServerNoDataMessages {
        static let carePlanNoDataMsg            = "No care plan document is currently available"
    }
    
    struct FileNameConstants {
        static let ROIPDFName            = "ROI Authorization Form.pdf"
        static let CSZipName            = "CCDA.zip"
        static let ApptReportsPDFName            = "Appointment_Schedule.pdf"
        static let LabReportsPDFName            = "Lab Results.pdf"
        static let ANNCPDFName            = "ANNC Form.pdf"
        static let SSLCertificateName            = "myctca.com"
        static let CarePlanPDFName              = "Comprehensive Care Plan.pdf"
        static let VitalsPDFName              = "Vitals.pdf"
        static let AllergiesPDFName              = "Allergies.pdf"
        static let PrescriptionsPDFName              = "Prescriptions.pdf"
        static let ImmunizationPDFName              = "Immunizations.pdf"
        static let HealthIssuesPDFName              = "Health-Issues.pdf"
        static let SingleApptReportsPDFName            = "Schedule.pdf"
        
        static let UserGuidePDFName              = "User Guide"
        static let YourHealthPDFName              = "Your Health, Your Future"
        static let NutritionBasicsPDFName              = "Nutrition Basics"
        static let YourMenuPDFName              = "Enhancing Your Menu"
        static let SymptomManagementPDFName              = "Symptom Management"
        static let EatingChallengePDFName              = "When Eating is a Challenge"
        static let SITDocPDFName              = "SYMPTOM INVENTORY TOOLS(SIT)"
    }
    
    struct PlatformErrorMessage {
        static let lowerOSError            = "You cannot open file in this version of the iOS."
    }
    
    struct UserTypeStrings {
        static let portalCaregiver           = "Portal Caregiver"
        static let portalPatient             = "Portal Patient"
    }
    
    struct UniverslaLinkPages {
        static let anncForm           = "ANNC Form"
        static let loginPage           = "Login"
    }
    
    struct NavigationPages {
        static let appointmentsReason     = "AppointmentsReasons"
        static let appointmentsComments     = "AppointmentsComments"
        static let appointmentsDates     = "AppointmentsDates"
        static let appointmentsContacts     = "AppointmentsContacts"
    }
}

