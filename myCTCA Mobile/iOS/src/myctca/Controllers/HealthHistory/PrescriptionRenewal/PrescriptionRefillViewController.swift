//
//  PrescriptionRefillViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillViewController: CTCABaseViewController {
    
    @IBOutlet weak var cancelNewMail: UIBarButtonItem!
    @IBOutlet weak var sendNewMail: UIBarButtonItem!
    @IBOutlet weak var tableView: UITableView!
    
    let cellInfoHeight: CGFloat = 115.0
    let cellCommentsHeight: CGFloat = 230.0
    let cellDefaultHeight: CGFloat = 60.0
    let cellHeaderHeight: CGFloat = 55.0
    
    let doneToolbarHeight: CGFloat = 50.0
    
    var otherTableRowHeights: CGFloat = 0.0
    
    var prescriptionRefills: [Prescription]?
    var toSelections = ""
    
    var seletedOptionsArray = [String]()
    let mailmanager = MailManager()
    
    let healthHistoryManager = HealthHistoryManager()
    
    var prescriptionsAsString: String {
        get {
            var prescriptions: String = ""
            if (prescriptionRefills != nil) {
                for prescription in prescriptionRefills! {
                    if (prescriptions == "") {
                        prescriptions = prescription.drugName
                    } else {
                        prescriptions += ", \(prescription.drugName)"
                    }
                }
            }
            return prescriptions
        }
    }
    
    var  prescriptionIDArray: [String] {
        get {
            var pScriptIdAR: [String] = [String]()
            
            if (prescriptionRefills != nil) {
                for prescription in prescriptionRefills! {
                    pScriptIdAR.append(prescription.prescriptionId)
                }
            }
            
            return pScriptIdAR
        }
    }
    
    var prescriptionRefillRequest: PrescriptionRefillRequest = PrescriptionRefillRequest()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        // Self-sizing magic!
        self.tableView.rowHeight = UITableView.automaticDimension
        self.tableView.estimatedRowHeight = cellDefaultHeight
        
        loadViewData()
        
        tableView.sectionHeaderHeight = UITableView.automaticDimension
        tableView.estimatedSectionHeaderHeight = 64
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_PRESCRIPTION_RENEWAL_VIEW)
    }
    
    override func loadViewData() {
        
        NetworkStatusManager.shared.registerForReload(view: self)
        
         if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
             return
         }
        
        //Get CareTeam Name
        if (AppSessionManager.shared.currentUser.careTeams == nil) {
            mailmanager.fetchCareTeams() {
                status in
                
                self.fadeOutActivityIndicator()
                
                if status == .FAILED {
                    ErrorManager.shared.showServerError(error: self.mailmanager.getLastServerError(), onView: self)
                } else {
                    AppSessionManager.shared.currentUser.careTeams = self.mailmanager.careTeams
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                    }
                }
            }
        }
    }
        
    func dismissThis() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.001) {
            self.view.endEditing(true)
            self.dismiss(animated: true, completion: nil)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.reloadData()
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
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

    @IBAction func cancelTapped(_ sender: UIBarButtonItem) {
        GenericHelper.shared.showFormLeaveAlert(leaveAction: dismissThis)
    }
    
    @IBAction func sendTapped(_ sender: UIBarButtonItem) {
        self.view.endEditing(true)
        
        let formStatus = self.isFormValid()
                          
        if (formStatus == .VALID_FORM) {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_PRESCRIPTION_RENEWAL_SEND_TAP)
            
            print("FORM IS VALID")
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            self.showActivityIndicator(view: self.view, message: "Sending…")
            
            healthHistoryManager.sendPrescriptionRenewalRequest(request: prescriptionRefillRequest) {
                status in
                
                if status == .SUCCESS {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_PRESCRIPTION_RENEWAL_SUCCESS)
                    
                    let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: CommonMsgConstants.commonSuccessTitle,
                                                                          message: PrescriptionsMsgConstants.SUCCESSFUL_SEND_RESPONSE,
                                                                          state: true,
                                                                          buttonAction: self.dismissThis)
                        
                    GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                    
                } else {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_PRESCRIPTION_RENEWAL_FAIL)
                    
                    let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: CommonMsgConstants.serverErrorTitle,
                                                                          message: self.healthHistoryManager.getLastServerError().errorMessage,
                                                                          state: false,
                                                                          buttonAction: nil)
                        
                    GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                }
                
                self.fadeOutActivityIndicator(completion: nil)
            }
        } else {
            if formStatus == .INVALID_PHONE {
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.phoneNumberValidationTitle, andMessage: CommonMsgConstants.phoneNumberValidationMessage, onView: self)
            } else {
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.formValidationTitle, andMessage: PrescriptionsMsgConstants.INVALID_FORM_RESPONSE, onView: self)
            }
        }
    }
    
    private func isFormValid() -> FormValidationResults {
        
        var validForm = true
        
        for index in 1...7 {
            let indexPath = IndexPath(row: index, section: 0)
            switch (index) {
            case 1:
                let cell: PrescriptionRefillToTableViewCell = self.tableView.cellForRow(at: indexPath) as! PrescriptionRefillToTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                }
            case 4:
                let cell: PrescriptionRefillPhoneTableViewCell = self.tableView.cellForRow(at: indexPath) as! PrescriptionRefillPhoneTableViewCell
                let phoneState = cell.isValidPhone()
                if (phoneState == .INVALID_PHONE) {
                    validForm = false
                    return .INVALID_PHONE
                } else if (phoneState == .INVALID_FORM) {
                    validForm = false
                } else {
                    prescriptionRefillRequest.patientPhone = cell.getData()
                }
                
            case 5:
                let cell: PrescriptionRefillPharmacyTableViewCell = self.tableView.cellForRow(at: indexPath) as! PrescriptionRefillPharmacyTableViewCell
                if (!cell.isValid()) {
                    validForm = false
                } else {
                    prescriptionRefillRequest.pharmacyName = cell.getData()
                }
            case 6:
                let cell: PrescriptionRefillPharmPhoneTableViewCell = self.tableView.cellForRow(at: indexPath) as! PrescriptionRefillPharmPhoneTableViewCell

                let phoneState = cell.isValidPhone()
                if (phoneState == .INVALID_PHONE) {
                    validForm = false
                    return .INVALID_PHONE
                } else if (phoneState == .INVALID_FORM) {
                    validForm = false
                } else {
                    prescriptionRefillRequest.pharmacyPhone = cell.getData()
                }
                
            case 7:
                // Not required field
                let cell: PrescriptionRefillCommentsTableViewCell = self.tableView.cellForRow(at: indexPath) as! PrescriptionRefillCommentsTableViewCell
                prescriptionRefillRequest.comments = cell.getData()
            default:
                print("default")
                //validForm = true
            }
        }
        
        if (validForm) {
            
            prescriptionRefillRequest.selectedPrescriptions = self.prescriptionIDArray
        }
        
        return validForm ? .VALID_FORM : .INVALID_FORM
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension PrescriptionRefillViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return 8
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (indexPath.section == 0) {
            if (indexPath.row == 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillFrom") as! PrescriptionRefillFromTableViewCell
                cell.prepareView((AppSessionManager.shared.currentUser.userProfile?.fullName)!)
                return cell
            }
            if (indexPath.row == 1) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillTo") as! PrescriptionRefillToTableViewCell
                if (AppSessionManager.shared.currentUser.careTeams != nil) {
                    if toSelections != "" {
                        cell.prepareView(toSelections)
                        cell.toInput.textColor = MyCTCAColor.formContent.color
                        cell.toLabel.textColor = MyCTCAColor.formLabel.color
                    } else {
                        cell.prepareView(CTCAUIConstants.placeHolderString)
                        cell.toInput.textColor = MyCTCAColor.placeHolder.color
                    }
                }
                return cell
            }
            if (indexPath.row == 2) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillSubject") as! PrescriptionRefillSubjectTableViewCell
                cell.prepareView(PrescriptionsMsgConstants.REQUEST_SUBJECT)
                return cell
            }
            if (indexPath.row == 3) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillPrescription") as! PrescriptionRefillPrescriptionTableViewCell
                cell.prepareView(self.prescriptionsAsString)
                return cell
            }
            if (indexPath.row == 4) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillPhone") as! PrescriptionRefillPhoneTableViewCell
                cell.phoneInput?.setPhoneNumber(number: AppSessionManager.shared.currentUser.getUsersPreferedContactnumber())
                return cell
            }
            if (indexPath.row == 5) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillPharmacy") as! PrescriptionRefillPharmacyTableViewCell
                
                return cell
            }
            if (indexPath.row == 6) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillPharmacyPhone") as! PrescriptionRefillPharmPhoneTableViewCell
                
                return cell
            }
            if (indexPath.row == 7) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "prescriptionRefillComments") as! PrescriptionRefillCommentsTableViewCell
                
                return cell
            }
        }
        let cell = UITableViewCell()
        return cell
    }
}

extension PrescriptionRefillViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = self.tableView.dequeueReusableCell(withIdentifier: PrescriptionsMsgConstants.HEADER_ID) as! PrescriptionRefillInfoTableViewCell
        header.prepareView()
        return header
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (indexPath.row == 7) {
            return cellCommentsHeight
        } else if (indexPath.row == 3 || indexPath.row == 2) {
            return UITableView.automaticDimension
        }
        return cellDefaultHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if (indexPath.row == 1) {
 
            let discloseInfoTableViewController: DiscloseInfoTableViewController = self.storyboard?.instantiateViewController(withIdentifier: "DiscloseInfoTableViewController") as! DiscloseInfoTableViewController

            discloseInfoTableViewController.delegate = self
            discloseInfoTableViewController.discloseType = .careTeams
            discloseInfoTableViewController.customTitle = "Add Recipients"
                        
            if let careTeams = AppSessionManager.shared.currentUser.careTeams {
                var generalList: [(name: String, selected: Bool)] = [(String,Bool)]()
                
                for item in  careTeams{
                    if seletedOptionsArray.contains(item.name) {
                        generalList.append((item.name, true))
                    } else {
                        generalList.append((item.name, false))
                    }
                }
                discloseInfoTableViewController.careTeamsList = generalList

            }
            
            self.navigationController?.pushViewController(discloseInfoTableViewController, animated: true)
        } else {
            tableView.deselectRow(at: indexPath, animated: false)
        }
    }
    
    func selectCareTeamForPayload(name:String) {
        if let careTeams = AppSessionManager.shared.currentUser.careTeams {
            for careTeam in careTeams {
                if name == careTeam.name {
                    prescriptionRefillRequest.to.append(careTeam)
                }
            }
        }
    }
}

extension PrescriptionRefillViewController : DiscloseInfoSelectDelegate {
    func didSelectCareTeams(value: String, data: [String]) {
        for choice in data {
            selectCareTeamForPayload(name: choice)
        }
        seletedOptionsArray = data
        toSelections = value
        if value != "" {
            tableView.reloadData()
        }
    }
}
