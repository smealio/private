//
//  SendMessageTableViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 11/27/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class SendMessageTableViewController: CTCABaseTableViewController, UITextFieldDelegate, UITextViewDelegate, RecordSelectionDelegate {
    
    @IBOutlet weak var fromTF: UITextField!
    @IBOutlet weak var fromEmailTF: UITextField!
    @IBOutlet weak var subjectTF: UITextField!
    @IBOutlet weak var phoneTF: PhoneNumberTextField!
    @IBOutlet weak var concernTF: UITextField!
    @IBOutlet weak var facilityTF: UITextField!
    
    @IBOutlet weak var messageTV: CTCATextView!
    
    @IBOutlet weak var fromLabel: UILabel!
    @IBOutlet weak var fromEmailLabel: UILabel!
    @IBOutlet weak var toLabel: UILabel!
    @IBOutlet weak var toContentLabel: UILabel!
    @IBOutlet weak var subjectLabel: UILabel!
    @IBOutlet weak var phoneLabel: UILabel!
    @IBOutlet weak var concernLabel: UILabel!
    @IBOutlet weak var facilityLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    
    var textFieldArray: Array<UITextField>?
    
    var sendMessageData: [String: String] = [String: String]()

    let SEND_MESSAGE_SELECTION_TABLE_ID = "RecordSelectionViewController"
    let sendMessageManager = SendMessageManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Send Message"
        // tableView is a stored property of a TableViewController
        self.tableView.tableFooterView = UIView()
        self.tableView.separatorStyle = .none
        
        // Text View
        self.messageTV.delegate = self
        self.messageTV.layer.cornerRadius = 5
        self.messageTV.layer.masksToBounds = true
        self.messageTV.layer.borderWidth = 0.5
        self.messageTV.layer.borderColor = MyCTCAColor.formLines.color.cgColor
        
        // Text Fields
        self.fromTF.delegate = self
        self.fromEmailTF.delegate = self
        self.subjectTF.delegate = self
        self.phoneTF.delegate = self
        
        // Populate TextInput Array
        textFieldArray = [fromTF, fromEmailTF, subjectTF, phoneTF, concernTF, facilityTF]
        
        // Add Toolbar to keyboard for text fields
        self.addDoneButtonOnKeyboard()
        // Uncomment the following line to preserve selection between presentations
        //self.clearsSelectionOnViewWillAppear = false
        if let iUser = AppSessionManager.shared.currentUser.iUser {
            self.fromTF.text = iUser.fullName
            self.fromEmailTF.text = iUser.userName
            self.phoneTF.setPhoneNumber(number: AppSessionManager.shared.currentUser.getUsersPreferedContactnumber())
        }
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_SEND_MESSAGE_VIEW)
        if let primaryFac = AppSessionManager.shared.currentUser.primaryFacility {
            facilityTF.text = primaryFac.displayName
        }
    }
    
    func addDoneButtonOnKeyboard() {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: 320, height: 50))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaSecondGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        if (self.textFieldArray != nil) {
            for textInput:UITextField in textFieldArray! {
                textInput.inputAccessoryView = doneToolbar
            }
        }
        self.messageTV.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        if (self.textFieldArray != nil) {
            for textInput:UITextField in textFieldArray! {
                textInput.resignFirstResponder()
            }
        }
        _ = self.messageTV.resignFirstResponder()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func dismissThis() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.001) {
            self.view.endEditing(true)
            self.dismiss(animated: true, completion: nil)
        }
    }
    
    @IBAction func cancelSendMessage(_ sender: Any) {
        GenericHelper.shared.showFormLeaveAlert(leaveAction: {
            self.dismissThis()
        })
    }

    @IBAction func sendMessage(_ sender: Any) {
        
        self.view.endEditing(true)
        let formStatus = self.isFormValid()
        if formStatus == .VALID_FORM {
            print("FORM IS VALID!!!!")
            
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_SEND_MESSAGE_TAP)
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            self.showActivityIndicator(view: (self.navigationController?.view)!, message: "Sending Request")
            
            sendMessageManager.sendMessage(content: sendMessageData) {
                status in
                
                self.dismissActivityIndicator()
                
                if status == .SUCCESS {
                    let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(
                        title:SendMessageMsgConstants.successfulSendTitle,
                        message: SendMessageMsgConstants.successfulSendResponse,
                        state: true,
                        buttonAction: self.dismissThis)
                    
                    GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                    
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SEND_MESSAGE_SUCCESS)
                } else {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SEND_MESSAGE_FAIL)
                    
                    let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(
                        title:CommonMsgConstants.serverErrorTitle,
                        message: self.sendMessageManager.getLastServerError().errorMessage,
                        state: false,
                        buttonAction: nil)
                    
                    GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                }
            }
        } else {
            switch formStatus {
            case .INVALID_EMAIL:
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.emailValidationTitle, andMessage: CommonMsgConstants.emailValidationMessage, onView: self)
            case .INVALID_PHONE:
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.phoneNumberValidationTitle, andMessage: CommonMsgConstants.phoneNumberValidationMessage, onView: self)
            default:
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.formValidationTitle, andMessage: SendMessageMsgConstants.invalidFormResponse, onView: self)
            }
        }
    }
    
    private func isFormValid() -> FormValidationResults {
        var validForm = true;
        sendMessageData.removeAll()
        if let fromTxt: String = fromTF.text {
            if (fromTxt != "") {
                sendMessageData["UserName"] = fromTxt
                fromLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                validForm = false;
                fromLabel.textColor = UIColor.red
            }
        }
        if let fromEmailTxt: String = fromEmailTF.text {
            if (fromEmailTxt != "") {
                if GenericHelper.shared.isValidEmail(fromEmailTxt) {
                    sendMessageData["EmailAddress"] = fromEmailTxt
                    fromEmailLabel.textColor = MyCTCAColor.formLabel.color
                } else {
                    validForm = false;
                    fromEmailLabel.textColor = UIColor.red
                    return .INVALID_EMAIL
                }
            } else {
                validForm = false;
                fromEmailLabel.textColor = UIColor.red
            }
        }
        if let subjectTxt: String = subjectTF.text {
            if (subjectTxt != "") {
                sendMessageData["Subject"] = subjectTxt
                subjectLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                validForm = false;
                subjectLabel.textColor = UIColor.red
            }
        }
        if let concernTxt: String = concernTF.text {
            if (concernTxt != "") {
                sendMessageData["AreaOfConcern"] = concernTxt
                concernLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                validForm = false;
                concernLabel.textColor = UIColor.red
            }
        }
        if let facilityTxt: String = facilityTF.text {
            if (facilityTxt != "") {
                sendMessageData["Facility"] = facilityTxt
                facilityLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                validForm = false;
                facilityLabel.textColor = UIColor.red
            }
        }
        if let messageTxt: String = messageTV.text {
            if (messageTxt != "") {
                sendMessageData["Comments"] = messageTxt
                messageLabel.textColor = MyCTCAColor.formLabel.color
            } else {
                validForm = false;
                messageLabel.textColor = UIColor.red
            }
        }
        // Phone is not required. If entered, need to verify that it is >= 10 digits
        if let phoneTxt: String = phoneTF.text {
            if phoneTxt != "" {
                let digits = PhoneNumberFormatter.shared.removeAllNonDigits(phoneTxt)
                if (digits.count >= CTCAUIConstants.minLengthForPhoneTextField) {
                    sendMessageData["PhoneNumber"] = phoneTxt
                    phoneLabel.textColor = MyCTCAColor.formLabel.color
                } else {
                    validForm = false;
                    phoneLabel.textColor = UIColor.red
                    return .INVALID_PHONE
                }
            }
        }
        
        return validForm ? .VALID_FORM : .INVALID_FORM
    }
    
    // MARK: - Text View Delegate
    func textViewDidBeginEditing(_ textView: UITextView) {
        textViewDoesHaveFocus(textView)
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        textViewDoesNotHaveFocus(textView)
    }
    
    func textViewDoesHaveFocus(_ textView: UITextView) {
        if (textView == self.messageTV) {
            messageLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        textView.layer.cornerRadius = 5
        textView.layer.masksToBounds = true
        textView.layer.borderWidth = 0.5
        textView.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func textViewDoesNotHaveFocus(_ textView: UITextView) {
        if (textView == self.messageTV) {
            messageLabel.textColor = MyCTCAColor.formLabel.color
        }
        textView.layer.cornerRadius = 5
        textView.layer.masksToBounds = true
        textView.layer.borderWidth = 0.5
        textView.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    // MARK: TextField Delegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        textFieldDoesHaveFocus(textField)
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        textFieldDoesNotHaveFocus(textField)
    }
    
    func textFieldDoesHaveFocus(_ textField: UITextField) {
        if (textField == fromTF) {
            fromLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        if (textField == fromEmailTF) {
            fromEmailLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        if (textField == subjectTF) {
            subjectLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        if (textField == phoneTF) {
            phoneLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        textField.layer.cornerRadius = 5
        textField.layer.masksToBounds = true
        textField.layer.borderWidth = 0.5
        textField.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func textFieldDoesNotHaveFocus(_ textField: UITextField) {
        if (textField == fromTF) {
            fromLabel.textColor = MyCTCAColor.formLabel.color
        }
        if (textField == fromEmailTF) {
            fromEmailLabel.textColor = MyCTCAColor.formLabel.color
        }
        if (textField == subjectTF) {
            subjectLabel.textColor = MyCTCAColor.formLabel.color
        }
        if (textField == phoneTF) {
            phoneLabel.textColor = MyCTCAColor.formLabel.color
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
    
    // MARK: SendMessageSelectionDelegate
    func didMakeSelection(value:Any, type: SelectionType) {
        let val = value as! String
        if (type == .treatmentFacility) {
            facilityTF.text = val
            facilityLabel.textColor = MyCTCAColor.formLabel.color
        }
        if (type == .areaOfConcern) {
            concernTF.text = val
            concernLabel.textColor = MyCTCAColor.formLabel.color
        }
    }
    
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 8
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        //TODO: This can be done better but since it's static, we'll leave it for now
        // Show Date Picker
        self.view.endEditing(true)
        let selectionTableViewController: RecordSelectionViewController = self.storyboard?.instantiateViewController(withIdentifier: SEND_MESSAGE_SELECTION_TABLE_ID) as! RecordSelectionViewController
        selectionTableViewController.delegate = self

        if (indexPath.row == 5) {
            if let text = concernTF.text, text != "" {
                selectionTableViewController.selectedOption = text
            }
            selectionTableViewController.selectionType = .areaOfConcern
        }
        if (indexPath.row == 6) {
            if let text = facilityTF.text, text != "" {
                selectionTableViewController.selectedOption = text
            }
            selectionTableViewController.selectionType = .treatmentFacility
        }
        self.navigationController?.pushViewController(selectionTableViewController, animated: true)
    }

    // MARK: - STUFF COPPIED FROM CTCAViewControllerProtocol
    
    func showActivityIndicator(view: UIView, message: String? = nil) {
        print("Show Activity Indicator: view: \(view) :::message: \(String(describing: message))")
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.showOverlay(view: view, message: message)
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
    
    func ctcaInfoAlert(title:String,
                       message: String,
                       okaction: UIAlertAction? = nil,
                       otheraction:UIAlertAction? = nil) -> UIAlertController {
        
        let alertController = UIAlertController(title: title,
                                                message: message,
                                                preferredStyle:.alert)
        
        if(okaction != nil) {
            alertController.addAction(okaction!)
        } else {
            let okAction = UIAlertAction(title: "OK",
                                         style: .default,
                                         handler: nil)
            alertController.addAction(okAction)
        }
        
        if (otheraction != nil) {
            alertController.addAction(otheraction!)
        }
        
        return alertController
    }

}
