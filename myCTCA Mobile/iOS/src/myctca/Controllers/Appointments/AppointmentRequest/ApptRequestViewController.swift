//
//  ApptRequestViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 12/7/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptRequestViewController: CTCABaseViewController  {

    @IBOutlet weak var cancelApptRequest: UIBarButtonItem!
    @IBOutlet weak var sendApptRequest: UIBarButtonItem!
    
    @IBOutlet weak var tableView: UITableView!
    
    var textFieldArray: Array<UITextField>?
    
    let cellApptReasonHeight: CGFloat = 230.0
    let cellApptReqHeight: CGFloat = 60.0
    let doneToolbarHeight: CGFloat = 50.0
    
    var apptReqSubject: String?
    
    var apptRequestData: [String: String] = [String:String]()
    let appointmentsManager = AppointmentsManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        
        print("Add Notification: \(DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION)")
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_APPOINTMENTS_REQUEST_VIEW)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        // Listen For DatePicker Notification
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.setDTPODateTime(_:)),
                                               name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION), object: nil)
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.cancelledDTPO(_:)),
                                               name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION), object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    override func  viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        
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
    
    @IBAction func cancelTapped(_ sender: Any) {
        dismissThis()
    }
    
    @IBAction func sendTapped(_ sender: Any) {
        
        self.view.endEditing(true)
        
        let formStatus = self.isFormValid()
        
        if (formStatus == .VALID_FORM){
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_REQUEST_SEND_TAP)
            
            print("FORM IS VALID!!!!")
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            if let abbreviation = TimeZone.abbreviationDictionary
                .first(where: { $1 == TimeZone.current.identifier })?.key {
                apptRequestData["facilityTimeZone"] = abbreviation
            }
            
            self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.sendApptRequest)
            
            appointmentsManager.requestOrChangeAppointment(requestType: .new, params: apptRequestData) { [self]
                result in
                
                self.fadeOutActivityIndicator(completion: nil)
                switch(result) {
                
                case .FAILED:
                    let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: CommonMsgConstants.requestFailedErrorTitle, message: appointmentsManager.getLastServerError().errorMessage, state: false, buttonAction: self.dismissThis)
                    
                    GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)

                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_REQUEST_FAIL)
                    
                case .SUCCESS:
                    let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: AppointmentMsgConstants.successfulRequestTitle, message: AppointmentMsgConstants.successfulRequestResponse, state: true, buttonAction: self.dismissThis)
                    
                    GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)

                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_REQUEST_SUCCESS)
                }
            }
        } else {
            if formStatus == .INVALID_PHONE {
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.phoneNumberValidationTitle, andMessage: CommonMsgConstants.phoneNumberValidationMessage, onView: self)
            } else {
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.formValidationTitle, andMessage: AppointmentMsgConstants.invalidFormResponse, onView: self)
            }
        }
    }
    
    private func isFormValid() -> FormValidationResults {
        var validForm = true
        for index in 0...4 {
            let indexPath = IndexPath(row: index, section: 0)
            switch (index) {
            case 0:
                let cell: ApptReqFromTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptReqFromTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                } else {
                    apptRequestData["From"] = cell.getData()
                }
            case 1:
                let cell: ApptReqSubjectTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptReqSubjectTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                } else {
                    apptRequestData["Subject"] = cell.getData()
                }
            case 2:
                let cell: ApptReqDateTimeTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptReqDateTimeTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                } else {
                    apptRequestData["AppointmentDate"] = cell.getData()
                }
            case 3:
                let cell: ApptReqPhoneTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptReqPhoneTableViewCell
                let phoneState = cell.isValidPhone()
                if (phoneState == .INVALID_PHONE) {
                    validForm = false
                    return .INVALID_PHONE
                } else if (phoneState == .INVALID_FORM) {
                    validForm = false
                } else {
                    apptRequestData["PhoneNumber"] = cell.getData()
                }
            case 4:
                let cell: ApptReqReasonTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptReqReasonTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                } else {
                    apptRequestData["Comments"] = cell.getData()
                }
            default:
                validForm = true
            }
        }
        
        return validForm ? .VALID_FORM : .INVALID_FORM
    }
    
    // Call back for DateTimePickerOverlay
    @objc func setDTPODateTime(_ notification: NSNotification) {
        print("setDTPODateTime object: \(String(describing: notification.object))::: userInfo: \(String(describing: notification.userInfo))")
        if let dateTimeStr = notification.object as? String {
            print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
            let indexPath = IndexPath(row: 2, section: 0)
            let cell: ApptReqDateTimeTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptReqDateTimeTableViewCell
            cell.setDateTime(dateTime: dateTimeStr)
        }
    }
    @objc func cancelledDTPO(_ notification: NSNotification) {
        print("cancelledDTPO)")
    }
}

// MARK: Table View Data Source

extension ApptRequestViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return 5
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.section == 0) {
            if (indexPath.row == 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptReqFromCell") as! ApptReqFromTableViewCell
                cell.fromNameLabel.text = AppSessionManager.shared.currentUser.iUser!.fullName
                return cell
            }
            if (indexPath.row == 1) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptReqSubjectCell") as! ApptReqSubjectTableViewCell
                if (self.apptReqSubject != nil) {
                    cell.subjectDisplayLabel.text = self.apptReqSubject
                } else {
                    cell.subjectDisplayLabel.text = "Appointment Request"
                }
                return cell
            }
            if (indexPath.row == 2) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptReqDateTimeCell") as! ApptReqDateTimeTableViewCell
                return cell
            }
            if (indexPath.row == 3) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptReqPhoneCell") as! ApptReqPhoneTableViewCell
                cell.phoneTF?.setPhoneNumber(number:  AppSessionManager.shared.currentUser.getUsersPreferedContactnumber())
                return cell
            }
            if (indexPath.row == 4) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptReqReasonCell") as! ApptReqReasonTableViewCell
                return cell
            }
    }
        let cell = UITableViewCell()
        return cell
    }
}

extension ApptRequestViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (indexPath.section == 0) {
            if (indexPath.row == 4) {
                return cellApptReasonHeight
            }
        }
        return cellApptReqHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        print("didSelectRowAt didSelectRowAt didSelectRowAt didSelectRowAt didSelectRowAt didSelectRowAt: \(indexPath)")
        tableView.deselectRow(at: indexPath, animated: false)
        if (indexPath.row == 2) {
            // Show Date Picker
            self.view.endEditing(true)
            
            //set date if already choosen
            var curSelectedDate:Date? = nil
            let indexPath = IndexPath(row: 2, section: 0)
            let cell = self.tableView.cellForRow(at: indexPath) as! ApptReqDateTimeTableViewCell
            if let cellText = cell.dateTimeTF.text {
                if let date = DateConvertor.convertToDateFromString(dateString: cellText, inputFormat: .appointmentsForm) {
                    curSelectedDate = date
                }
            }
            
            let bgImage:UIImage = UIImage(named: "calendar_white.png")!
            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: AppointmentMsgConstants.twentyFourHourRequestMessage,
                                                 bgImage: bgImage,
                                                 minDate: Date().addingTimeInterval(60 * 60 * 24),
                                                 currDate: curSelectedDate)
        }
    }
}
