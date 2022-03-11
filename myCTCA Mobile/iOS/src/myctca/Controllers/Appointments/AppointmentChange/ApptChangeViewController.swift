//
//  ApptChangeViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 12/14/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptChangeViewController: UIViewController, CTCAViewControllerProtocol {

    @IBOutlet weak var cancelBarButton: UIBarButtonItem!
    @IBOutlet weak var sendBarButton: UIBarButtonItem!
    @IBOutlet weak var tableView: UITableView!
    
    let cellTextViewHeight: CGFloat = 230.0
    let cellDefaultHeight: CGFloat = 60.0
    let doneToolbarHeight: CGFloat = 50.0
    
    var appointment: Appointment?
    
    var apptRequestType: ApptRequestType = ApptRequestType.reschedule
    let appointmentsManager = AppointmentsManager()
    
    var apptRequestData: [String: String] = [String:String]()
        
    override func viewDidLoad() {
        super.viewDidLoad()

        // Title
        if (apptRequestType == ApptRequestType.reschedule) {
            self.title = AppointmentMsgConstants.defaultTitle
        } else if (apptRequestType == ApptRequestType.reschedule) {
            self.title = AppointmentMsgConstants.rescheduleTitle
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_APPOINTMENTS_RESCHEDULE_VIEW)
        } else if (apptRequestType == ApptRequestType.cancel) {
            self.title = AppointmentMsgConstants.cancelTitle
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_APPOINTMENTS_CANCEL_VIEW)
        }
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
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
        print("Add Notification: \(DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION)")
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.cancelledDTPO(_:)),
                                               name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION), object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillHideNotification, object: nil)
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
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
        
        if (formStatus == .VALID_FORM) {
            if apptRequestType == .cancel {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_CANCEL_SEND_TAP)
            } else {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_RESCHEDULE_SEND_TAP)
            }
            
            print("FORM IS VALID!!!!")
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            if let abbreviation = TimeZone.abbreviationDictionary
                .first(where: { $1 == TimeZone.current.identifier })?.key {
                apptRequestData["facilityTimeZone"] = abbreviation
            }
            
            self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.sendApptRequest)
            sendAppointmentRequest()            
        } else {
            if formStatus == .INVALID_PHONE {
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.phoneNumberValidationTitle, andMessage: CommonMsgConstants.phoneNumberValidationMessage, onView: self)
            } else {
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.formValidationTitle, andMessage: AppointmentMsgConstants.invalidFormResponse, onView: self)
            }
        }
    }
    
    private func isFormValid() -> FormValidationResults {
        var validForm = true;
        for index in 0...5 {
            let indexPath = IndexPath(row: index, section: 0)
            apptRequestData["AppointmentId"] = self.appointment!.appointmentId
            switch (index) {
            case 0:
                let cell: ApptChangeFromTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptChangeFromTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                } else {
                    apptRequestData["From"] = cell.getData()
                }
            case 1:
                let cell: ApptChangeSubjectTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptChangeSubjectTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                } else {
                    apptRequestData["Subject"] = cell.getData()
                }
            case 2:
                let cell: ApptChangeApptTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptChangeApptTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                }
            case 3:
                if (apptRequestType == ApptRequestType.reschedule) {
                    let cell: ApptChangeDateTimeTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptChangeDateTimeTableViewCell
                    if (!cell.isValid()) {
                        validForm = false
                    } else {
                        apptRequestData["AppointmentDate"] = cell.getData()
                    }
                } else {
                    let cancelCell = tableView.dequeueReusableCell(withIdentifier: "apptChangeApptDateLabelCell") as! ApptChangeDateLabelTableViewCell
                    if (!cancelCell.isValid()) {
                        validForm = false
                    }
                }
            case 4:
                let cell: ApptChangePhoneTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptChangePhoneTableViewCell
                let phoneState = cell.isValidPhone()
                if (phoneState == .INVALID_PHONE) {
                    validForm = false
                    return .INVALID_PHONE
                } else if (phoneState == .INVALID_FORM) {
                    validForm = false
                } else {
                    apptRequestData["PhoneNumber"] = cell.getData()
                }
            case 5:
                let cell: ApptChangeCommentsTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptChangeCommentsTableViewCell
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
    
    @objc func setDTPODateTime(_ notification: NSNotification) {
        print("setDTPODateTime object: \(String(describing: notification.object))::: userInfo: \(String(describing: notification.userInfo))")
        if let dateTimeStr = notification.object as? String {
            print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
            let indexPath = IndexPath(row: 3, section: 0)
            let cell: ApptChangeDateTimeTableViewCell = self.tableView.cellForRow(at: indexPath) as! ApptChangeDateTimeTableViewCell
            cell.setDateTime(dateTime: dateTimeStr)
        }
    }
    
    @objc func cancelledDTPO(_ notification: NSNotification) {
        print("cancelledDTPO: \(apptRequestType)")
    }
    
    func sendAppointmentRequest() {

        appointmentsManager.requestOrChangeAppointment(requestType:apptRequestType, params: apptRequestData) {
            result in
            
            self.fadeOutActivityIndicator(completion: nil)
            
            switch(result) {
            
            case .FAILED:
                //TODo - refactoring servererror
                let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: CommonMsgConstants.requestFailedErrorTitle, message: self.appointmentsManager.getLastServerError().errorMessage, state: false, buttonAction: self.dismissThis)
                
                GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                
                if self.apptRequestType == .cancel {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_CANCEL_FAIL)
                } else {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_RESCHEDULE_FAIL)
                }
                
            case .SUCCESS:
                var msg: String = AppointmentMsgConstants.successfulRescheduleResponse
                
                if (self.apptRequestType == ApptRequestType.cancel) {
                    msg = AppointmentMsgConstants.successfulCancellationResponse
                }
                
                let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: AppointmentMsgConstants.successfulRequestTitle, message: msg, state: true, buttonAction: self.dismissThis)
                
                GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                
                if self.apptRequestType == .cancel {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_CANCEL_SUCCESS)
                } else {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_RESCHEDULE_SUCCESS)
                }
            }
        }
    }

}

// MARK: Table View Data Source

extension ApptChangeViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return 6
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (indexPath.section == 0) {
            if (indexPath.row == 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptChangeFromCell") as! ApptChangeFromTableViewCell
                cell.setContent(AppSessionManager.shared.currentUser.iUser!.fullName)
                return cell
            }
            if (indexPath.row == 1) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptChangeSubjectCell") as! ApptChangeSubjectTableViewCell
                if (apptRequestType == ApptRequestType.reschedule) {
                    cell.setContent(AppointmentMsgConstants.rescheduleSubject)
                } else if (apptRequestType == ApptRequestType.cancel) {
                    cell.setContent(AppointmentMsgConstants.cancelSubject)
                }
                return cell
            }
            if (indexPath.row == 2) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptChangeApptCell") as! ApptChangeApptTableViewCell
                cell.setContent((self.appointment?.description)!)
                return cell
            }
            if (indexPath.row == 3) {
                if (apptRequestType == ApptRequestType.reschedule) {
                    let cell = tableView.dequeueReusableCell(withIdentifier: "apptChangeDateTimeCell") as! ApptChangeDateTimeTableViewCell
                    cell.dateTimeLabel.text = AppointmentMsgConstants.dateTimeLabelReschedule
                    cell.isUserInteractionEnabled = true
                    cell.hideCalendar(false)
                    return cell
                }
                let cancelCell = tableView.dequeueReusableCell(withIdentifier: "apptChangeApptDateLabelCell") as! ApptChangeDateLabelTableViewCell
                cancelCell.setContent("\(appointment!.getFormattedStartDate()), \(appointment!.getFormattedStartTime())")
                cancelCell.isUserInteractionEnabled = false
                return cancelCell
            }
            if (indexPath.row == 4) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptChangePhoneCell") as! ApptChangePhoneTableViewCell
                cell.phoneTF?.setPhoneNumber(number: AppSessionManager.shared.currentUser.getUsersPreferedContactnumber())
                return cell
            }
            if (indexPath.row == 5) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptChangeCommentsCell") as! ApptChangeCommentsTableViewCell
                return cell
            }
        }
        let cell = UITableViewCell()
        return cell
    }
}

extension ApptChangeViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (indexPath.section == 0) {
            if (indexPath.row == 5) {
                return cellTextViewHeight
            }
        }
        return cellDefaultHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        // This can be done better but since it's static, we'll leave it for now
        if (indexPath.row == 3) {
            // Show Date Picker
            print("PICKED ROW 3")
            self.view.endEditing(true)
            
            //set date if already choosen
            var curSelectedDate:Date? = nil
            let indexPath = IndexPath(row: 3, section: 0)
            let cell = self.tableView.cellForRow(at: indexPath) as! ApptChangeDateTimeTableViewCell
            if let cellText = cell.dateTimeTF.text {
                if let date = DateConvertor.convertToDateFromString(dateString: cellText, inputFormat: .appointmentsForm) {
                    curSelectedDate = date
                }
            }
            
            
            let bgImage: UIImage = UIImage(named: "calendar_white.png")!
            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: AppointmentMsgConstants.twentyFourHourRequestMessage,
                                                 bgImage: bgImage,
                                                 minDate: Date().addingTimeInterval(60 * 60 * 24),
                                                 currDate: curSelectedDate)
        }
    }
}
