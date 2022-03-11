//
//  MoreROITableViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/26/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit
import QuartzCore

class MoreROITableViewController: UITableViewController, UITextViewDelegate, UITextFieldDelegate {
    
    let headerHeight: CGFloat = 44.0
    let cellDefaultHeight: CGFloat = 60.0
    let cellTextInput: CGFloat = 180.0
    let cellReleaseObtainHeight: CGFloat = 85.0
    let cellPatientSignHeight: CGFloat = 65.0
    let cellPatientRelationHeight: CGFloat = 85.0
    let doneToolbarHeight: CGFloat = 50.0
    
    // Main Section - Input
    @IBOutlet weak var treatmentFacilityTF: UITextField!
    @IBOutlet weak var firstNameTF: UITextField!
    @IBOutlet weak var lastNameTF: UITextField!
    @IBOutlet weak var dobTF: UITextField!
    @IBOutlet weak var deliveryMethodLabel: UILabel!
    @IBOutlet weak var releaseObtainLabel: UILabel!
    @IBOutlet weak var authFacilityTF: UITextField!
    @IBOutlet weak var authAddressTF: UITextField!
    @IBOutlet weak var authCityTF: UITextField!
    @IBOutlet weak var authStateTF: UITextField!
    @IBOutlet weak var authZipTF: UITextField!
    @IBOutlet weak var authPhoneTF: PhoneNumberTextField!
    @IBOutlet weak var authFaxTF: PhoneNumberTextField!
    @IBOutlet weak var authEmailTF: UITextField!
    @IBOutlet weak var authPurposeLabel: UILabel!
    // Main Section - Labels
    @IBOutlet weak var treatmentFacilityLabel: UILabel!
    @IBOutlet weak var firstNameLabel: UILabel!
    @IBOutlet weak var lastNameLabel: UILabel!
    @IBOutlet weak var dobLabel: UILabel!
    @IBOutlet weak var deliveryMethodFieldLabel: UILabel!
    @IBOutlet weak var releaseObtainFieldLabel: UILabel!
    @IBOutlet weak var authFacilityLabel: UILabel!
    @IBOutlet weak var authAddressLabel: UILabel!
    @IBOutlet weak var authCityLabel: UILabel!
    @IBOutlet weak var authStateLabel: UILabel!
    @IBOutlet weak var authZipLabel: UILabel!
    @IBOutlet weak var authPhoneLabel: UILabel!
    @IBOutlet weak var authFaxLabel: UILabel!
    @IBOutlet weak var authEmailLabel: UILabel!
    @IBOutlet weak var authPurposeFieldLabel: UILabel!
    // ImageViews
    @IBOutlet weak var dobImageView: UIImageView!
    
    // Dates of Service - Input
    @IBOutlet weak var serviceFromTF: UITextField!
    @IBOutlet weak var serviceToTF: UITextField!
    @IBOutlet weak var serviceRestrictionsTV: UITextView!
    // Dates of Service - Labels
    @IBOutlet weak var serviceFromLabel: UILabel!
    @IBOutlet weak var serviceToLabel: UILabel!
    @IBOutlet weak var serviceRestrictionsLabel: UILabel!
    
    // Information to be disclosed - Input
    @IBOutlet weak var generalInfoLabel: UILabel!
    @IBOutlet weak var confidentialInfoLabel: UILabel!
    // Information to be disclosed - Labels
    @IBOutlet weak var generalInfoFieldLabel: UILabel!
    @IBOutlet weak var confidentialInfoFieldLabel: UILabel!
    
    // Authorization - Input
    @IBOutlet weak var authorizeDisclaimerLabel: UILabel!
    @IBOutlet weak var patientSignTF: UITextField!
    @IBOutlet weak var patientRelationTF: UITextField!
    // Authorization - Labels
    @IBOutlet weak var patientSignLabel: UILabel!
    @IBOutlet weak var patientRelationLabel: UILabel!
    
    // Array of Text fields that can be looped through to do stuff like add a Done button to the keyboard
    var textFieldArray:[UITextField] = [UITextField]()
    var textLabelArray:[UILabel] = [UILabel]()
    
    var datePickerCaller:UITextField?
    @IBOutlet weak var dobImageView1: UIImageView!
    @IBOutlet weak var dobImageView2: UIView!
    
    static let BEGINNING_OF_TREATMENT: String = "Beginning of Treatment"
    static let END_OF_TREATMENT: String = "End of Treatment"
    
    let SEND_MESSAGE_SELECTION_TABLE_ID: String = "RecordSelectionViewController"
    
    let DELIVERY_METHOD_TITLE: String = "Delivery Method"
    let RELEASE_OBTAIN_TITLE: String = "Release or Obtain"
    let ROI_PURPOSE_TITLE: String = "ROI Purpose"
    
    let SERVICE_END_DATE_LESS_THAN_BEGIN_DATE: String = "The the \"To\" value for the Dates Of Service must be after the \"From\" value."
    
    var roiObj: ROI = ROI();
        
    var isFormDataLoaded = false
    var selectedGenaralInfoList = [String]()
    var selectedConfInfoList = [String]()
    
    let formsManager = FormsManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.backgroundColor = UIColor.white
        
        self.tableView.estimatedRowHeight = cellDefaultHeight
        self.tableView.rowHeight = UITableView.automaticDimension
        
        self.clearsSelectionOnViewWillAppear = false
        
        self.textFieldArray = [firstNameTF, lastNameTF, authFacilityTF, authAddressTF, authCityTF, authStateTF, authZipTF, authPhoneTF, authFaxTF, authEmailTF, patientSignTF, patientRelationTF]
        
        dobImageView.tintColor = MyCTCAColor.ctcaSecondGreen.color
        dobImageView1.tintColor = MyCTCAColor.ctcaSecondGreen.color
        dobImageView2.tintColor = MyCTCAColor.ctcaSecondGreen.color
        
        serviceRestrictionsTV.delegate = self
        serviceRestrictionsTV.text = ""
        serviceRestrictionsTV.textColor = MyCTCAColor.formContent.color
        serviceRestrictionsTV.layer.cornerRadius = 5
        serviceRestrictionsTV.layer.borderColor = MyCTCAColor.formLines.color.cgColor
        serviceRestrictionsTV.layer.borderWidth = 0.5
        
        // Authorization Disclaimer text
        authorizeDisclaimerLabel.attributedText = buildAuthorizationDisclaimerText()
        
        // Add Toolbar to keyboard for text fields
        self.addDoneButtonOnKeyboard()
        
        loadViewData()
        
        //placeholders
        deliveryMethodLabel.text = CTCAUIConstants.placeHolderString
        deliveryMethodLabel.textColor = MyCTCAColor.placeHolder.color
        generalInfoLabel.text = CTCAUIConstants.placeHolderString
        generalInfoLabel.textColor = MyCTCAColor.placeHolder.color
        authPurposeLabel.text = CTCAUIConstants.placeHolderString
        authPurposeLabel.textColor = MyCTCAColor.placeHolder.color
        releaseObtainLabel.text = CTCAUIConstants.placeHolderString
        releaseObtainLabel.textColor = MyCTCAColor.placeHolder.color
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ROI_FORM_VIEW)
    }
    
    override func loadViewData() {
        if !isFormDataLoaded {
            //NetworkStatusManager.shared.registerForReload(view: self)
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            getROIFormData()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillHideNotification, object: nil)
    
        authFaxTF.textFieldType = .PHONE
        authPhoneTF.textFieldType = .PHONE
        
        textFieldDoesNotHaveFocus(authFaxTF)
        textFieldDoesNotHaveFocus(authPhoneTF)
    }
    
    @IBAction func backButtonTapped(_ sender: Any) {
        GenericHelper.shared.showFormLeaveAlert(leaveAction: {
            self.navigationController?.popViewController(animated: true)
        })
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func adjustForKeyboard(notification: NSNotification) {
        
        let userInfo = notification.userInfo!
        
        let keyboardScreenEndFrame = (userInfo[UIResponder.keyboardFrameEndUserInfoKey] as! NSValue).cgRectValue
        let keyboardViewEndFrame = view.convert(keyboardScreenEndFrame, from: view.window)
        
        if notification.name == UIResponder.keyboardWillHideNotification {
            tableView.contentInset = UIEdgeInsets.zero
        } else {
            tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: keyboardViewEndFrame.height+doneToolbarHeight, right: 0)
        }
        
        tableView.scrollIndicatorInsets = tableView.contentInset
    }
    
    func buildAuthorizationDisclaimerText() -> NSAttributedString {
        
        let attrStr:NSMutableAttributedString = NSMutableAttributedString( string: "\(ROIUIViewConstants.authorizationValid)\n\n",
            attributes: [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue-Bold", size: 15.0)!,
                         NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color])
        
        let bulletArray = [ROIUIViewConstants.authorizationBullet0, ROIUIViewConstants.authorizationBullet1, ROIUIViewConstants.authorizationBullet2, ROIUIViewConstants.authorizationBullet3, ROIUIViewConstants.authorizationBullet4, ROIUIViewConstants.authorizationBullet5, ROIUIViewConstants.authorizationBullet6]
        
        let bulletItems = self.bullletedList(stringList: bulletArray, font: authorizeDisclaimerLabel.font)
        
        attrStr.append(bulletItems)
        
        return attrStr
    }
    
    override func  viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        // Listen For DatePicker Notification
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.setDTPODateTime(_:)),
                                               name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION), object: nil)
    }
    
    override func  viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
    }
    
    func addDoneButtonOnKeyboard() {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: self.tableView.bounds.width, height: doneToolbarHeight))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        for textInput:UITextField in textFieldArray {
            textInput.inputAccessoryView = doneToolbar
            textInput.delegate = self
        }
        
        self.serviceRestrictionsTV.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        for textInput:UITextField in textFieldArray {
            textInput.resignFirstResponder()
        }
        self.serviceRestrictionsTV.resignFirstResponder()
    }
    
    @IBAction func SendROI(_ sender: Any) {
        
        // Validate Form
        let formStatus = self.formIsValid()
        
        if (formStatus == .VALID_FORM) {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ROI_SEND_TAP)
            
            print("SEND ROI FORM IS VALID")
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            self.showActivityIndicator(view: self.view, message: "Submitting ROI Request")
            
            formsManager.submitROIForm(roiInfo: roiObj) {
                success, expt, status in
                
                self.dismissActivityIndicator()
                
                if status == .FAILED {
                    self.showFailureMessage(message: self.formsManager.getLastServerError().errorMessage)
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ROI_SUBMIT_FAIL)
                } else {
                    if success {
                        self.showSuccessMessage()
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ROI_SUBMIT_SUCCESS)
                    } else {
                        var exceptionString = ""
                        if let exptnErr = expt?.exception {
                            
                            if exptnErr.errors.count > 0 {
                                exceptionString = exptnErr.errors[0].errorMessage
                            }
                            
                        } else {
                            exceptionString = ANNCUIConstants.unsuccessfulSendResponse
                        }
 
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ROI_SUBMIT_FAIL)

                        self.showFailureMessage(message: exceptionString)
                    }
                }
            }
        } else {
            switch formStatus {
            case .INVALID_PHONE:
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.phoneNumberValidationTitle, andMessage: CommonMsgConstants.phoneNumberValidationMessage, onView: self)
            case .INVALID_FAX:
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.faxNumberValidationTitle, andMessage: CommonMsgConstants.faxNumberValidationMessage, onView: self)
            case .INVALID_EMAIL:
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.emailValidationTitle, andMessage: CommonMsgConstants.emailValidationMessage, onView: self)
            default:
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.formValidationTitle, andMessage: ROIUIViewConstants.invalidFormResponse, onView: self)
            }
        }
    }
    
    func formIsValid() -> FormValidationResults {
        var isValid = true
        // Main Section
        let mainIsValid = validateMainSection()
        let datesOfServiceValid = validateDatesOfService()
        let infoDisclosedValid = validateInfoDisclosed()
        let authValid = validateAuthorization()
        
        if (mainIsValid != .VALID_FORM || datesOfServiceValid == false || infoDisclosedValid == false || authValid == false) {
            isValid = false
        }
        
        if isValid {
            return .VALID_FORM
        } else {
            if mainIsValid == .INVALID_FAX || mainIsValid == .INVALID_PHONE || mainIsValid == .INVALID_EMAIL {
                return mainIsValid
            } else {
                return .INVALID_FORM
            }
        }
        
    }
    
    func validateMainSection() -> FormValidationResults {
        var mainIsValid = true
        
        if let treatmentFacTxt: String = treatmentFacilityTF.text {
            // If treatment facility was selected, the ROI object would have been updated when the data was returned
            if (treatmentFacTxt != "") {
                treatmentFacilityLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                treatmentFacilityLabel.textColor = UIColor.red
            }
        }
        if let firstNameTxt: String = firstNameTF.text {
            if (firstNameTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.firstName = firstNameTxt.trimmingCharacters(in: .whitespaces)
                firstNameLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                firstNameLabel.textColor = UIColor.red
            }
        }
        if let lastNameTxt: String = lastNameTF.text {
            if (lastNameTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.lastName = lastNameTxt.trimmingCharacters(in: .whitespaces)
                lastNameLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                lastNameLabel.textColor = UIColor.red
            }
        }
        if let dobTxt: String = dobTF.text {
            // If dob was filled in, the ROI object would have been updated with the date returned from the datePicker
            if (dobTxt != "") {
                dobLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                dobLabel.textColor = UIColor.red
            }
        }
        
        if let dMTxt: String = deliveryMethodLabel.text {
            if (dMTxt == CTCAUIConstants.placeHolderString) {
                mainIsValid = false;
                deliveryMethodFieldLabel.textColor = UIColor.red
            } else if (dMTxt.contains("Pick-up date")) { //if date not selected
                let stringArr = dMTxt.split(separator: " ")
                if stringArr.last == "date" {
                    mainIsValid = false;
                    deliveryMethodFieldLabel.textColor = UIColor.red
                }
            }
            else {
                deliveryMethodFieldLabel.textColor = MyCTCAColor.formLabel.color
            }
        }

        if let relObTxt: String = releaseObtainLabel.text {
            // If releaseObtainLabel was filled in, the array in the ROI object would have been updated
            if (relObTxt == CTCAUIConstants.placeHolderString) {
                mainIsValid = false;
                releaseObtainFieldLabel.textColor = UIColor.red
            } else {
                releaseObtainFieldLabel.textColor = MyCTCAColor.formLabel.color
            }
        }
        if let aFacTxt: String = authFacilityTF.text {
            if (aFacTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.facilityOrIndividual = aFacTxt.trimmingCharacters(in: .whitespaces)
                authFacilityLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                authFacilityLabel.textColor = UIColor.red
            }
        }
        if let aAddTxt: String = authAddressTF.text {
            if (aAddTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.address = aAddTxt.trimmingCharacters(in: .whitespaces)
                authAddressLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                authAddressLabel.textColor = UIColor.red
            }
        }
        if let aCityTxt: String = authCityTF.text {
            if (aCityTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.city = aCityTxt.trimmingCharacters(in: .whitespaces)
                authCityLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                authCityLabel.textColor = UIColor.red
            }
        }
        if let aStateTxt: String = authStateTF.text {
            if (aStateTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.state = aStateTxt.trimmingCharacters(in: .whitespaces)
                authStateLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                authStateLabel.textColor = UIColor.red
            }
        }
        if let aZipTxt: String = authZipTF.text {
            if (aZipTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.zip = aZipTxt.trimmingCharacters(in: .whitespaces)
                authZipLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                mainIsValid = false;
                authZipLabel.textColor = UIColor.red
            }
        }
        if var aPhoneTxt: String = authPhoneTF.text {
            aPhoneTxt = aPhoneTxt.trimmingCharacters(in: .whitespaces)
            
            if aPhoneTxt == "" {
                mainIsValid = false;
                authPhoneLabel.textColor = UIColor.red
            } else {
                let digitsPhoneTxt = PhoneNumberFormatter.shared.removeAllNonDigits(aPhoneTxt)
                if digitsPhoneTxt.count < CTCAUIConstants.minLengthForPhoneTextField {
                    mainIsValid = false;
                    authPhoneLabel.textColor = UIColor.red
                    
                    return .INVALID_PHONE
                } else {
                    roiObj.phoneNumber = aPhoneTxt
                    authPhoneLabel.textColor = MyCTCAColor.formLabel.color
                }
            }
        }
        if var aFaxTxt: String = authFaxTF.text {
            aFaxTxt = aFaxTxt.trimmingCharacters(in: .whitespaces)
            
            if aFaxTxt == "" {
                // Fax is mandatory if Fax was selected as a delivery method
                if let dMTxt: String = deliveryMethodLabel.text {
                    if dMTxt.lowercased().range(of:"fax") != nil {
                        //Fax Required
                        mainIsValid = false;
                        authFaxLabel.textColor = UIColor.red
                    } else {
                        authFaxLabel.textColor = MyCTCAColor.formLabel.color
                    }
                }
            } else {
                let digitsPhoneTxt = PhoneNumberFormatter.shared.removeAllNonDigits(aFaxTxt)
                if digitsPhoneTxt.count < CTCAUIConstants.minLengthForPhoneTextField {
                    mainIsValid = false
                    authPhoneLabel.textColor = UIColor.red
                    
                    return .INVALID_FAX
                } else {
                    roiObj.fax = aFaxTxt
                    authFaxLabel.textColor = MyCTCAColor.formLabel.color
                }
                
//                if aFaxTxt.count < CTCAUIConstants.minLengthForPhoneTextField {
//                    mainIsValid = false;
//                    authFaxLabel.textColor = UIColor.red
//
//                    return .INVALID_FAX
//                } else {
//                    roiObj.fax = aFaxTxt
//                    authFaxLabel.textColor = MyCTCAColor.formLabel.color
//                }
            }
        }
        if let aEmailTxt: String = authEmailTF.text {
            print("aEmailTxt: \(aEmailTxt)")
            if (aEmailTxt.trimmingCharacters(in: .whitespaces) != "") {
                
                if !GenericHelper.shared.isValidEmail(aEmailTxt) {
                    authEmailLabel.textColor = UIColor.red
                    //Email invalid
                    mainIsValid = false;
                    return .INVALID_EMAIL
                } else {
                    roiObj.emailAddress = aEmailTxt.trimmingCharacters(in: .whitespaces)
                    authEmailLabel.textColor = MyCTCAColor.formLabel.color
                }
                
            } else {
                // Email is mandatory if Email was selected as a delivery method
                if let dMTxt: String = deliveryMethodLabel.text {
                    if dMTxt.lowercased().range(of:"email") != nil {
                        //Email Required
                        mainIsValid = false;
                        authEmailLabel.textColor = UIColor.red
                    } else {
                        authEmailLabel.textColor = MyCTCAColor.formLabel.color
                    }
                }
            }
        }
        if let purposeTxt: String = authPurposeLabel.text {
            // If authPurposeLabel was filled in, the array in the ROI object would have been updated
            if (purposeTxt == CTCAUIConstants.placeHolderString) {
                mainIsValid = false;
                authPurposeFieldLabel.textColor = UIColor.red
            } else {
                authPurposeFieldLabel.textColor = MyCTCAColor.formLabel.color
            }
        }
        return mainIsValid ? .VALID_FORM : .INVALID_FORM
    }
    
    func validateDatesOfService() -> Bool {
        var serviceDatesValid = true
        if let serviceFromTxt: String = serviceFromTF.text {
            if (serviceFromTxt != "") {
                // if "date of service from" is a date, it was added to the ROI object when it was selected
                if (serviceFromTxt.lowercased() == "beginning of treatment") {
                    roiObj.beginDate = nil
                    roiObj.beginningOfTreatment = true
                } else {
                    roiObj.beginDate = serviceFromTxt
                    roiObj.beginningOfTreatment = false
                }
                serviceFromLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                serviceDatesValid = false;
                serviceFromLabel.textColor = UIColor.red
            }
        }
        if let serviceToTxt: String = serviceToTF.text {
            if (serviceToTxt != "") {
                // if "date of service from" is a date, it was added to the ROI object when it was selected
                if (serviceToTxt.lowercased() == "end of treatment") {
                    roiObj.endOfTreatment = true
                    roiObj.endDate = nil
                } else {
                    roiObj.endDate = serviceToTxt
                    roiObj.endOfTreatment = false
                }
                serviceToLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                serviceDatesValid = false;
                serviceToLabel.textColor = UIColor.red
            }
        }
        if (roiObj.endDate != nil && roiObj.beginDate != nil) {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "MMM dd, yyyy"
            let end:Date = dateFormatter.date(from: roiObj.endDate!)!
            let begin:Date = dateFormatter.date(from: roiObj.beginDate!)!
            if (end < begin) {
                serviceDatesValid = false;
                serviceFromLabel.textColor = UIColor.red
                serviceToLabel.textColor = UIColor.red
                let banner = Banner(title: self.SERVICE_END_DATE_LESS_THAN_BEGIN_DATE,
                                    subtitle: nil,
                                    image: UIImage(named: "warning_triangle")!.withRenderingMode(.alwaysTemplate),
                                    backgroundColor: UIColor.red.withAlphaComponent(0.75))
                banner.dismissesOnTap = true
                banner.position = .top
                banner.springiness = .none
                banner.show(duration: 3.0)
            }
        }
        if let restrictionTxt: String = serviceRestrictionsTV.text {
            // Not a required field
            roiObj.restrictions = restrictionTxt
        } else {
            roiObj.restrictions = nil
        }
        return serviceDatesValid
    }
    
    func validateInfoDisclosed() -> Bool{
        var discloseValid = true
        
        if (generalInfoLabel.text == CTCAUIConstants.placeHolderString) {
            discloseValid = false
            generalInfoFieldLabel.textColor = UIColor.red
        } else {
            generalInfoFieldLabel.textColor = MyCTCAColor.formLabel.color
            confidentialInfoFieldLabel.textColor = MyCTCAColor.formLabel.color
        }
        
        return discloseValid
    }
    
    func validateAuthorization() -> Bool {
        var authValid = true
        
        if let patientSignTxt: String = patientSignTF.text {
            if (patientSignTxt.trimmingCharacters(in: .whitespaces) != "") {
                roiObj.signature = patientSignTxt.trimmingCharacters(in: .whitespaces)
                patientSignLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                authValid = false;
                patientSignLabel.textColor = UIColor.red
            }
        }
        if let patientRelationTxt: String = patientRelationTF.text {
            if (patientRelationTxt != "") {
                roiObj.patientRelation = patientRelationTxt
                patientSignLabel.textColor = MyCTCAColor.formLabel.color
            }
        }
        
        return authValid
    }
    
    func dismissAlertHandler(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: TextField Delegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        textFieldDoesHaveFocus(textField)
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        textFieldDoesNotHaveFocus(textField)
    }
    
    func textFieldDoesHaveFocus(_ textField: UITextField) {
        
        if let label: UILabel = matchLabelToTextField(textField) {
            label.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        
        textField.layer.cornerRadius = 5
        textField.layer.masksToBounds = true
        textField.layer.borderWidth = 0.5
        textField.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func textFieldDoesNotHaveFocus(_ textField: UITextField) {
        
        if let label: UILabel = matchLabelToTextField(textField) {
            label.textColor = MyCTCAColor.formLabel.color
        }
        
        textField.layer.cornerRadius = 5
        textField.layer.masksToBounds = true
        textField.layer.borderWidth = 0.5
        textField.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if let phoneTextField = textField as? PhoneNumberTextField {
            return phoneTextField.processInput(inputText: textField.text ?? "", range: range, string: string)
        }

        return true
    }
    
    func matchLabelToTextField(_ textField: UITextField) -> UILabel? {
        var label: UILabel? = nil
        if (textField == firstNameTF) {
            label = firstNameLabel
        }
        if (textField == lastNameTF) {
            label = lastNameLabel
        }
        if (textField == authFacilityTF) {
            label = authFacilityLabel
        }
        if (textField == authAddressTF) {
            label = authAddressLabel
        }
        if (textField == authCityTF) {
            label = authCityLabel
        }
        if (textField == authStateTF) {
            label = authStateLabel
        }
        if (textField == authZipTF) {
            label = authZipLabel
        }
        if (textField == authPhoneTF) {
            label = authPhoneLabel
        }
        if (textField == authFaxTF) {
            label = authFaxLabel
        }
        if (textField == authEmailTF) {
            label = authEmailLabel
        }
        if (textField == patientSignTF) {
            label = patientSignLabel
        }
        if (textField == patientRelationTF) {
            label = patientRelationLabel
        }
        return label
    }
    
    // MARK: - Text View Delegate
    func textViewDidBeginEditing(_ textView: UITextView) {
        textViewDoesHaveFocus(textView)
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        textViewDoesNotHaveFocus(textView)
    }
    
    func textViewDoesHaveFocus(_ textView: UITextView) {
        if (textView == self.serviceRestrictionsTV) {
            serviceRestrictionsLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        textView.layer.cornerRadius = 5
        textView.layer.masksToBounds = true
        textView.layer.borderWidth = 0.5
        textView.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func textViewDoesNotHaveFocus(_ textView: UITextView) {
        if (textView == self.serviceRestrictionsTV) {
            serviceRestrictionsLabel.textColor = MyCTCAColor.formLabel.color
        }
        textView.layer.cornerRadius = 5
        textView.layer.masksToBounds = true
        textView.layer.borderWidth = 0.5
        textView.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    // MARK: - Table view delegate
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        print("didSelectRowAt: \(indexPath)")
        
        switch indexPath.section {
        case 0:
            mainSectionSelection(at: indexPath.row)
        case 1:
            section2Selection(at: indexPath.row)
        case 2:
            datesOfServiceSelection(at: indexPath.row)
        case 3:
            infoToBeDisclosedSelection(at: indexPath.row)
        default:
            print("Invalid section selection")
        }
    }
    
     func mainSectionSelection(at row: Int) {
        
        switch (row) {
        case 0:
            // Treatment Facility
            let treatmentFacilityViewController: RecordSelectionViewController = self.storyboard?.instantiateViewController(withIdentifier: SEND_MESSAGE_SELECTION_TABLE_ID) as! RecordSelectionViewController
            treatmentFacilityViewController.delegate = self
            treatmentFacilityViewController.selectionType = .treatmentFacility
            
            var facilities = [(String,String,String)]()
            
            for fac in formsManager.roiFormInfo.facilitiesDetailList {
                if let facValue = fac["value"], let index = facValue.lastIndex(of: ",")  {
                    let facility = facValue[facValue.startIndex ..< index ]
                    var facilitPhone = facValue[index ..<  facValue.endIndex]
                    let indx = facilitPhone.index(facilitPhone.startIndex, offsetBy: 2)
                    facilitPhone = facilitPhone[indx ..<  facValue.endIndex]
                    facilities.append((String(facility), String(facilitPhone), facValue))
                }
                
            }
            
            if let text = treatmentFacilityTF.text, text != "" {
                treatmentFacilityViewController.selectedOption = text
            }
            
            treatmentFacilityViewController.useForROI = true
            treatmentFacilityViewController.treatmentFacilities = facilities
            self.navigationController?.pushViewController(treatmentFacilityViewController, animated: true)
        case 3:
            // Date Of Birth
            self.view.endEditing(true)
            
            //set date if already choosen
            var curSelectedDate:Date? = nil
            if let cellText = dobTF.text {
                if let date = DateConvertor.convertToDateFromString(dateString: cellText, inputFormat: .baseForm) {
                    curSelectedDate = date
                }
            }
            
            let bgImage:UIImage = UIImage(named: "check-square-o_white.png")!

            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: ROIUIViewConstants.dobMessage,
                                                 bgImage: bgImage,
                                                 mode: .date,
                                                 maxDate: Date().addingTimeInterval(60 * 60 * 24),
                                                 currDate: curSelectedDate,
                                                 dateForField:dobTF)
        default:
            break
        }
    }
    
    func section2Selection(at row: Int) {
        
        switch (row) {
        case 0:
            // Delivery Method
            let selectionTableViewController: SelectionTableViewController = self.storyboard?.instantiateViewController(withIdentifier: "SelectionTableViewController") as! SelectionTableViewController
            selectionTableViewController.delegate = self
            selectionTableViewController.choices = formsManager.roiFormInfo.deliveryMethods
            selectionTableViewController.viewTitle = DELIVERY_METHOD_TITLE
            selectionTableViewController.tableSelectionType = .multi
            
            if let text = deliveryMethodLabel.text {
                var list = text.components(separatedBy: ", ")
                var count = 0
                for item in list {
                    if item.starts(with: "Pick-up date") {
                        list.remove(at: count)
                        list.append("Pick-up date")
                        break
                    }
                    count = count+1
                }
                
                selectionTableViewController.selectedElements = list
            }
            
            self.navigationController?.pushViewController(selectionTableViewController, animated: true)
            
        case 1:
            // Release/Obtain
            let selectionTableViewController: SelectionTableViewController = self.storyboard?.instantiateViewController(withIdentifier: "SelectionTableViewController") as! SelectionTableViewController
            selectionTableViewController.delegate = self
            selectionTableViewController.choices = formsManager.roiFormInfo.authorizationActions
            selectionTableViewController.viewTitle = RELEASE_OBTAIN_TITLE
            selectionTableViewController.tableSelectionType = .multi
            
            if let text = releaseObtainLabel.text {
                selectionTableViewController.selectedElements = text.components(separatedBy: ", ")
            }
            
            self.navigationController?.pushViewController(selectionTableViewController, animated: true)
        case 10:
            // Purpose
            let selectionTableViewController: SelectionTableViewController = self.storyboard?.instantiateViewController(withIdentifier: "SelectionTableViewController") as! SelectionTableViewController
            selectionTableViewController.delegate = self
            selectionTableViewController.choices = formsManager.roiFormInfo.purposes
            selectionTableViewController.viewTitle = ROI_PURPOSE_TITLE
            selectionTableViewController.tableSelectionType = .multi
            
            if let text = authPurposeLabel.text {
                selectionTableViewController.selectedElements = text.components(separatedBy: ", ")
            }
            
            self.navigationController?.pushViewController(selectionTableViewController, animated: true)
        default:
            break
        }
    }
    
    // Dates of Service Section
    func datesOfServiceSelection(at row: Int) {
        let bgImage:UIImage = UIImage(named: "check-square-o_white.png")!

        switch (row) {
        case 1:
            var curDate = Date()
            if let dateTimeStr = serviceFromTF.text, dateTimeStr != "" {
                print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "MMM d, yyyy"
                dateFormatter.timeZone = .current// TimeZone(abbreviation: "GMT+0:00") //Current time zone
                curDate = dateFormatter.date(from: dateTimeStr)!
            }
            
            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: "Select start date of the service",
                                                 bgImage: bgImage,
                                                 mode: .date,
                                                 maxDate: Date(),
                                                 currDate: curDate,
                                                 dateForField:serviceFromTF)
        case 2:
            var curDate = Date()
            if let dateTimeStr = serviceToTF.text, dateTimeStr != "" {
                print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "MMM d, yyyy"
                dateFormatter.timeZone = .current// TimeZone(abbreviation: "GMT+0:00") //Current time zone
                curDate = dateFormatter.date(from: dateTimeStr)!
            }
            
            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: "Select end date of the service",
                                                 bgImage: bgImage,
                                                 mode: .date,
                                                 maxDate: Date(),
                                                 currDate: curDate,
                                                 dateForField:serviceToTF)
        default:
            break
        }
    }
    // Information to be Disclosed Section
    func infoToBeDisclosedSelection (at row: Int) {
        switch (row) {
        case 0:
            // General Info
            let discloseNavController = self.storyboard?.instantiateViewController(withIdentifier: "DiscloseInfoNav") as! UINavigationController
            let discloseInfoTableViewController: DiscloseInfoTableViewController = (discloseNavController.topViewController as? DiscloseInfoTableViewController)!
            discloseInfoTableViewController.delegate = self
            discloseInfoTableViewController.discloseType = .general
            
            var generalList: [(name: String, selected: Bool)] = [(String,Bool)]()
            
            for item in formsManager.roiFormInfo.disclosureInformationList {
                if selectedGenaralInfoList.contains(item) {
                    generalList.append((item, true))
                } else {
                    generalList.append((item, false))
                }
            }
            
            discloseInfoTableViewController.generalInfoAR = generalList
            self.navigationController?.pushViewController(discloseInfoTableViewController, animated: true)
            
        case 1:
            // Highly Confidential Info
            let discloseNavController = self.storyboard?.instantiateViewController(withIdentifier: "DiscloseInfoNav") as! UINavigationController
            let discloseInfoTableViewController: DiscloseInfoTableViewController = (discloseNavController.topViewController as? DiscloseInfoTableViewController)!
            discloseInfoTableViewController.delegate = self
            discloseInfoTableViewController.discloseType = .confidential
            
            var confidentialInfoList: [(name: String, selected: Bool)] = [(String,Bool)]()

            for item in formsManager.roiFormInfo.highlyConfidentialInformationList{
                if selectedConfInfoList.count == 0 {
                    confidentialInfoList.append((item, true))
                }//first time
                else {
                    if selectedConfInfoList.contains(item) {
                        confidentialInfoList.append((item, true))
                    } else {
                        confidentialInfoList.append((item, false))
                    }
                }
            }
            
            discloseInfoTableViewController.confidentialInfoAR = confidentialInfoList
            self.navigationController?.pushViewController(discloseInfoTableViewController, animated: true)
        default:
            break
        }
    }
    
    // Call back for DateTimePickerOverlay
    @objc func setDTPODateTime(_ notification: NSNotification) {
        print("setDTPODateTime object: \(String(describing: notification.object))::: userInfo: \(String(describing: notification.userInfo))")
        var setDateString:String = ""
        if let dateTimeStr = notification.object as? String {
            print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = DatePickerOverlay.DATE_FORMAT
            dateFormatter.timeZone = .current// TimeZone(abbreviation: "GMT+0:00") //Current time zone
            let birthDate = dateFormatter.date(from: dateTimeStr)
            dateFormatter.dateFormat = "MMM d, yyyy"
            setDateString = dateFormatter.string(from: birthDate!)
        }
        
        let notificationDict = notification.userInfo
        if (notificationDict != nil) {
            let callerField:UIView = notificationDict![DatePickerOverlay.DATE_FIELD_KEY] as! UIView
            if (callerField == dobTF) {
                roiObj.dateOfBirth = notification.object as? String
                dobTF.text = setDateString
                dobLabel.textColor = MyCTCAColor.formLabel.color
            }
            else if (callerField == deliveryMethodLabel) {
                roiObj.pickupDate = notification.object as? String
                // Need to write the date after the word Pick-up
                let textAR = deliveryMethodLabel.text!.components(separatedBy: "Pick-up date")
                deliveryMethodLabel.text = "\(String(describing: textAR[0]))Pick-up date: \(setDateString) \(String(describing: textAR[1]))"
                deliveryMethodFieldLabel.textColor = MyCTCAColor.formLabel.color
            } else if (callerField == serviceFromTF) {
                roiObj.beginDate = setDateString
                serviceFromTF.text = setDateString
                serviceFromLabel.textColor = MyCTCAColor.formLabel.color
            } else if (callerField == serviceToTF) {
                roiObj.beginDate = setDateString
                serviceToTF.text = setDateString
                serviceToLabel.textColor = MyCTCAColor.formLabel.color
            }
        }
    }
    /**
     TableView Stuff
     */
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        return headerHeight
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        // Authorization Section
        if (indexPath.section == 1) {
            // DeliveryMethod Cell, Authorize Cell, Purpose Cell
            if (indexPath.row == 0 || indexPath.row == 1 || indexPath.row == 10) {
                return UITableView.automaticDimension
            }
        }
        
        // Dates of Service Section
        if (indexPath.section == 2) {
            if (indexPath.row == 0 ) {
                return UITableView.automaticDimension
            }
        }
        
        // Information to be Disclosed Section
        if (indexPath.section == 3) {
            // Restrictions Cell
            if (indexPath.row == 3) {
                return cellTextInput
            }
            
            return UITableView.automaticDimension
        }
        
        // Authorization Section
        if (indexPath.section == 4) {
            // Disclaimer Text Cell
            if (indexPath.row == 0) {
                return UITableView.automaticDimension
            }
            if (indexPath.row == 1) {
                return cellPatientSignHeight
            }
            if (indexPath.row == 2) {
                return cellPatientRelationHeight
            }
        }
        
        return cellDefaultHeight
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let cell = MoreROISectionCell()
        
        var sectionTitle = ""
        switch (section) {
        case 0:
            sectionTitle = "PATIENT INFORMATION"
        case 1:
            sectionTitle = "RELEASE INFORMATION"
        case 2:
            sectionTitle = "DATES OF SERVICE"
        case 3:
            sectionTitle = "INFORMATION TO BE DISCLOSED"
        case 4:
            sectionTitle = "AUTHORIZATION"
        default:
            sectionTitle = ""
        }
        cell.prepareView(sectionTitle)
        
        return cell
    }
    
    // MARK: - STUFF COPPIED FROM CTCAViewControllerProtocol
    
    func showActivityIndicator(view: UIView, message: String? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.showOverlay(view: self.navigationController!.view, message: message)
        })
    }
    
    func dismissActivityIndicator(completion: (() -> Swift.Void)? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.fadeOutOverlay()
            
            if (completion != nil) {
                completion!()
            }
        })
    }
    
    func bullletedList(stringList: [String],
                       font: UIFont,
                       bullet: String = "\u{2022}",
                       indentation: CGFloat = 20,
                       lineSpacing: CGFloat = 2,
                       paragraphSpacing: CGFloat = 12,
                       textColor: UIColor = MyCTCAColor.ctcaGrey75.color,
                       bulletColor: UIColor = MyCTCAColor.ctcaGrey.color) -> NSAttributedString {
        
        let textAttributes: [NSAttributedString.Key: Any] = [NSAttributedString.Key.font: font, NSAttributedString.Key.foregroundColor: textColor]
        let bulletAttributes: [NSAttributedString.Key: Any] = [NSAttributedString.Key.font: font, NSAttributedString.Key.foregroundColor: bulletColor]
        
        let paragraphStyle = NSMutableParagraphStyle()
        let nonOptions = [NSTextTab.OptionKey: Any]()
        paragraphStyle.tabStops = [
            NSTextTab(textAlignment: .left, location: indentation, options: nonOptions)]
        paragraphStyle.defaultTabInterval = indentation
        //paragraphStyle.firstLineHeadIndent = 0
        //paragraphStyle.headIndent = 20
        //paragraphStyle.tailIndent = 1
        paragraphStyle.lineSpacing = lineSpacing
        paragraphStyle.paragraphSpacing = paragraphSpacing
        paragraphStyle.headIndent = indentation
        
        let bulletList = NSMutableAttributedString()
        for string in stringList {
            let formattedString = "\(bullet)\t\(string)\n"
            let attributedString = NSMutableAttributedString(string: formattedString)
            
            attributedString.addAttributes(
                [NSAttributedString.Key.paragraphStyle : paragraphStyle],
                range: NSMakeRange(0, attributedString.length))
            
            attributedString.addAttributes(
                textAttributes,
                range: NSMakeRange(0, attributedString.length))
            
            let string:NSString = NSString(string: formattedString)
            let rangeForBullet:NSRange = string.range(of: bullet)
            attributedString.addAttributes(bulletAttributes, range: rangeForBullet)
            bulletList.append(attributedString)
        }
        
        return bulletList
    }
    
    func getROIFormData() {
        self.showActivityIndicator(view: self.view, message: "Loading..") //dismiss after populating
        
        formsManager.fetchROIFormInfo() {
            status in
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.formsManager.getLastServerError(), onView: self)
            } else {
                DispatchQueue.main.async(execute: {
                    self.tableView.reloadData()
                })
            }
            
            self.setPrePopulatingValues()
            self.isFormDataLoaded = true
        }
    }
    
    func showSuccessMessage() {
        let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: ROIUIViewConstants.successfulSendTitle,
                                                              message: ROIUIViewConstants.successfulSendResponse,
                                                              state: true,
                                                              buttonAction: {
                                                                self.navigationController?.popViewController(animated: true)
                                                              })
            
        GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
    }
    
    func showFailureMessage(message:String) {
        let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: ROIUIViewConstants.unsuccessfulSendTitle,
                                                              message: message,
                                                              state: false,
                                                              buttonAction: nil)
            
        GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
    }
    
    func setPrePopulatingValues() {
        var facility = ""
        var selectedFacility = ""

        if let primFac = AppSessionManager.shared.currentUser.primaryFacility?.facilityCode {
            for item in formsManager.roiFormInfo.facilitiesDetailList {
                if let fac = item["key"], fac == primFac {
                    facility = item["value"] ?? ""
                    selectedFacility = item["key"] ?? ""
                    break;
                }
            }
        }
        
        //DPE-1725
        let aPhoneTxt = AppSessionManager.shared.currentUser.getUsersPreferedContactnumber()
        authPhoneTF.setPhoneNumber(number: aPhoneTxt)
        roiObj.phoneNumber = aPhoneTxt

        DispatchQueue.main.async(execute: {
            //facility
            self.roiObj.selectedFacility = selectedFacility
            self.treatmentFacilityTF.text = facility
        
            //name, dob
            if let contact = AppSessionManager.shared.currentUser.userContacts {
                self.firstNameTF.text = contact.firstName
                self.roiObj.firstName = contact.firstName
                self.lastNameTF.text = contact.lastName
                self.roiObj.lastName = contact.lastName
                
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "MMM dd, yyyy"
                
                if let userDob = contact.dateOfBirth {
                    let dob = dateFormatter.string(from: userDob)
                    self.dobTF.text = dob
                    self.roiObj.dateOfBirth = dob
                }
            }
            
            //confidential info
            self.confidentialInfoLabel.text = (self.formsManager.roiFormInfo.highlyConfidentialInformationList.map{String($0)}).joined(separator: ", ")

            self.roiObj.selectedHighlyConfidentialDiscolosureInformation = self.formsManager.roiFormInfo.highlyConfidentialInformationList

            self.dismissActivityIndicator()
        })
    }
}

extension MoreROITableViewController: RecordSelectionDelegate {
    
    func didMakeSelection(value:Any, type: SelectionType) {
        let val = value as! String

        if (type == .treatmentFacility) {
            treatmentFacilityTF.text = val
            treatmentFacilityLabel.textColor = MyCTCAColor.formLabel.color
            if let roiFacility = findFacilityInList(val) {
                roiObj.selectedFacility = roiFacility
                print("roiFacility: \(String(describing: roiFacility))")
            }
            print("roiObj.selectedFacility: \(String(describing: roiObj.selectedFacility))")
        }
    }
    
    func findFacilityInList(_ search: String) -> String? {
        for item in formsManager.roiFormInfo.facilitiesDetailList {
            if let fac = item["key"], fac == search {
                return item["value"]
            }
        }
        return nil
    }
}

extension MoreROITableViewController: SelectionTableDelegate {
    func didMakeSingleSelection(title: String, value:String) {
        
    }
    
    func didMakeMultiSelections(title: String, choices:[String: Bool]) {
        print("didMakeSelections title: \(title) ::: choices: \(choices)")
        if (title == DELIVERY_METHOD_TITLE) {
            deliveryMethodLabel.text = ""
            roiObj.selectedDeliveryMethod = [String]()
            for choice in choices{
                if (choice.value == true) {
                    roiObj.selectedDeliveryMethod.append(choice.key)
                    
                    if deliveryMethodLabel.text == "" {
                        deliveryMethodLabel.text = choice.key
                    } else {
                        deliveryMethodLabel.text = "\(String(describing: deliveryMethodLabel.text!)), \(choice.key)"
                    }
                }
            }
            self.tableView.reloadData()
            if (choices["Pick-up date"] == true) {
                self.view.endEditing(true)
                let bgImage:UIImage = UIImage(named: "check-square-o_white.png")!
                DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                     message: ROIUIViewConstants.pickupMessage,
                                                     bgImage: bgImage,
                                                     mode: .date,
                                                     minDate: Date().addingTimeInterval(60 * 60 * 24),
                                                     dateForField: deliveryMethodLabel)
            }
            deliveryMethodFieldLabel.textColor = MyCTCAColor.formLabel.color
            deliveryMethodLabel.textColor = MyCTCAColor.formContent.color
        }
        if (title == ROI_PURPOSE_TITLE) {
            authPurposeLabel.text = ""
            roiObj.selectedPurposes = [String]()
            for choice in choices {
                if (choice.value == true) {
                    roiObj.selectedPurposes.append(choice.key)
                    if (authPurposeLabel.text == "") {
                        authPurposeLabel.text = choice.key
                    } else {
                        authPurposeLabel.text = "\(String(describing: authPurposeLabel.text!)), \(choice.key)"
                    }
                }
            }
            authPurposeFieldLabel.textColor = MyCTCAColor.formLabel.color
            authPurposeLabel.textColor = MyCTCAColor.formContent.color
            self.tableView.reloadData()
        }
        if (title == RELEASE_OBTAIN_TITLE) {
            releaseObtainLabel.text = ""
            roiObj.selectedAuthorizationAction = [String]()
            for (index, choice) in choices.enumerated() {
                if (choice.value == true) {
                    roiObj.selectedAuthorizationAction.append(formsManager.roiFormInfo.authorizationActions[index])
                    if (releaseObtainLabel.text == "") {
                        releaseObtainLabel.text = choice.key
                    } else {
                        if var existingText = releaseObtainLabel.text {
                            if existingText.last == ":" {
                                _ = existingText.popLast()
                            }
                            
                            releaseObtainLabel.text = "\(String(describing: existingText)), \(choice.key)"
                        }
                    }
                }
            }
            releaseObtainLabel.textColor = MyCTCAColor.formContent.color
            releaseObtainFieldLabel.textColor = MyCTCAColor.formLabel.color
            print("roiObj.selectedAuthorizationAction: \(roiObj.selectedAuthorizationAction)")
            self.tableView.reloadData()
        }
    }
}

extension MoreROITableViewController: ServiceDateSelectDelegate {
    func didSelectServiceDate(value:String, tf: UITextField) {
        tf.text = value
    }
}

extension MoreROITableViewController: DiscloseInfoSelectDelegate {
    func didSelectGeneralInfo(displayString:String, data: [String], other:String? = nil) {
        roiObj.selectedDisclosureInformation = data
        roiObj.disclosureInformationOther = other
        generalInfoLabel.text = displayString
        generalInfoLabel.setNeedsDisplay()
        self.tableView.reloadData()
        generalInfoFieldLabel.textColor = MyCTCAColor.formLabel.color
        generalInfoLabel.textColor = MyCTCAColor.formContent.color
        selectedGenaralInfoList = data
    }
    
    func didSelectConfidentialInfo(value:String, data: [String]) {
        roiObj.selectedHighlyConfidentialDiscolosureInformation = data
        
        var commaSeparatedStr = ""
        for item in formsManager.roiFormInfo.highlyConfidentialInformationList {
            if !data.contains(item) {
                if (commaSeparatedStr == "") {
                    commaSeparatedStr += item
                } else {
                    commaSeparatedStr += ", \(item)"
                }
            }
        }
        
        if commaSeparatedStr.count > 0 {
            serviceRestrictionsTV.text = "Withhold: \(commaSeparatedStr)"
        }
        
        confidentialInfoLabel.text = value
        self.tableView.reloadData()
        confidentialInfoFieldLabel.textColor = MyCTCAColor.formLabel.color
        selectedConfInfoList = data
    }
}

