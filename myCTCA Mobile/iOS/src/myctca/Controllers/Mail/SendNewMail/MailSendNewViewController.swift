//
//  MailSendNewViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailSendNewViewController: UIViewController, CTCAViewControllerProtocol {
    
    @IBOutlet weak var cancelNewMail: UIBarButtonItem!
    @IBOutlet weak var sendNewMail: UIBarButtonItem!
    
    @IBOutlet weak var tableView: UITableView!
    
    let cellMailDisclaimerHeight: CGFloat = 115.0
    let cellMailMessageHeight: CGFloat = 230.0
    let cellMailDefaultHeight: CGFloat = 60.0
    
    var mailSendNewData : NewMailInfo = NewMailInfo()
    
    var respondingMail: Mail?
    
    var otherTableRowHeights: CGFloat = 0.0
    
    weak var mailBoxDelegate: MailBoxDelegate?
    var toSelections = ""
    
    var seletedOptionsArray = [String]()
    let mailmanager = MailManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        // Self-sizing magic!
        self.tableView.rowHeight = UITableView.automaticDimension
        self.tableView.estimatedRowHeight = cellMailDefaultHeight
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_MAIL_NEW_VIEW)
    }
    
    func fetchCareTeam() {
        showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.retriveCareTeamText)
        
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
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if (respondingMail == nil) {
            title = "Send New Mail"
        } else {
            title = "Reply to Mail"
        }
        
        loadViewData()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        //Get CareTeam Name
        if (AppSessionManager.shared.currentUser.careTeams == nil) {
            fetchCareTeam()
        }
    }
    
    override func didReceiveMemoryWarning() {   
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func dismissView() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.001) {
            self.view.endEditing(true)
            self.dismiss(animated: true, completion: nil)
        }
    }
    
    func dismissThis() {
        if (self.mailBoxDelegate != nil) {
            self.mailBoxDelegate!.didSendMail()
        }
        dismissView()
    }
    
    @IBAction func cancelTapped(_ sender: UIBarButtonItem) {
        GenericHelper.shared.showFormLeaveAlert(leaveAction: dismissView)
    }
    
    @IBAction func sendTapped(_ sender: UIBarButtonItem) {
        print("SENDNEWMAIL")
        self.view.endEditing(true)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        if (self.isFormValid()) {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_NEW_MAIL_SEND_TAP)
            
            print("FORM IS VALID!!!!")
            self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.sendMailText)
            
            mailmanager.sendNewMail(newMailInfo: mailSendNewData) {
                status in
                
                self.fadeOutActivityIndicator(completion: nil)

                 if status == .SUCCESS {
                     let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: CommonMsgConstants.commonSuccessTitle,
                                                                           message: MailMsgConstants.successfulSendResponse,
                                                                           state: true,
                                                                           buttonAction: self.dismissThis)
                     GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                    
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_NEW_MAIL_SUCCESS)
                     
                 } else {
                     let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: CommonMsgConstants.serverErrorTitle,
                                                                           message: self.mailmanager.getLastServerError().errorMessage,
                                                                           state: false,
                                                                           buttonAction: nil)
                         
                     GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
                    
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_NEW_MAIL_FAIL)
                 }
            }
        } else {
            print("FORM IS INVALID")
            let alert = ctcaInfoAlert(title: "Form Incomplete", message: MailMsgConstants.invalidFormResponse)
            alert.view.tintColor = MyCTCAColor.ctcaGreen.color
            self.present(alert,
                         animated: true,
                         completion: nil)
        }
    }
    
    private func isFormValid() -> Bool {
        var validForm = true
        for index in 1...4 {
            let indexPath = IndexPath(row: index, section: 0)
            switch (index) {
            case 1:
                if let cell: MailSendNewFromTableViewCell = self.tableView.cellForRow(at: indexPath) as? MailSendNewFromTableViewCell {
                    if (!cell.isValid()) {
                        validForm = false
                    } else {
                        mailSendNewData.from = cell.getData()
                    }
                } else {
                    validForm = false
                }
            case 2:
                if let cell: MailSendNewToTableViewCell = self.tableView.cellForRow(at: indexPath) as? MailSendNewToTableViewCell {
                    if (!cell.isValid()) {
                        validForm = false
                    }
                } else {
                    validForm = false
                }
            case 3:
                if let cell: MailSendNewSubjectTableViewCell = self.tableView.cellForRow(at: indexPath) as? MailSendNewSubjectTableViewCell {
                    if (!cell.isValid()) {
                        validForm = false
                    } else {
                        mailSendNewData.subject = cell.getData()
                    }
                } else {
                    validForm = false
                }
            case 4:
                if let cell: MailSendNewMessageTableViewCell = self.tableView.cellForRow(at: indexPath) as? MailSendNewMessageTableViewCell {
                    if (!cell.isValid()) {
                        validForm = false
                    } else {
                        mailSendNewData.comments = cell.getData()
                    }
                } else {
                    validForm = false
                }
            default:
                validForm = true
            }
        }
        if (validForm) {
            if (respondingMail == nil) {
                // New Message
                mailSendNewData.parentMessageId = ""
                mailSendNewData.folderName = ""
            } else {
                // Replying Message
                if let parentMessageId = respondingMail?.parentMessageId, let folderName = respondingMail?.folderName {
                    mailSendNewData.parentMessageId = parentMessageId
                    mailSendNewData.folderName = folderName
                }
            }
            // Need a data in Sent even though it gets overwritten by server
            mailSendNewData.sent = getISO8601DateString()
            // All Secure Mail is type 1
            mailSendNewData.messageType = "1"
        }
        
        return validForm
    }
    
    func getISO8601DateString() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyy-MM-dd'T'HH:mm:ss"
        let dateString: String = dateFormatter.string(from: Date())
        return dateString
    }
    
    func selectCareTeamForPayload(name:String) {
        if let careTeams = AppSessionManager.shared.currentUser.careTeams {
            for careTeam in careTeams {
                if name == careTeam.name {
                    mailSendNewData.to.append(careTeam)
                    mailSendNewData.selectedTo.append(careTeam)
                }
            }
        }
    }
    
    func setToSelection(values:String) {
        let index = IndexPath(row: 2, section: 0)
        if let toCell = tableView.cellForRow(at: index) as? MailSendNewToTableViewCell {
            toCell.toInput.text = values
            toCell.toInput.textColor = MyCTCAColor.formContent.color
            toCell.toLabel.textColor = MyCTCAColor.formLabel.color
        }
    }
}

// MARK: Table View Data Source

extension MailSendNewViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return 5
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (indexPath.section == 0) {
            if (indexPath.row == 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailSendNewDisclaimerCell") as! MailSendNewDisclaimerTableViewCell
                cell.prepareView()
                return cell
            }
            if (indexPath.row == 1) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailSendNewFromCell") as! MailSendNewFromTableViewCell
                cell.fromTF.text = AppSessionManager.shared.currentUser.iUser!.fullName
                return cell
            }
            if (indexPath.row == 2) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailSendNewToCell") as! MailSendNewToTableViewCell
                if (AppSessionManager.shared.currentUser.careTeams != nil) {
                    if toSelections != "" {
                        cell.toInput.text = toSelections
                        cell.toInput.textColor = MyCTCAColor.formContent.color
                        cell.toLabel.textColor = MyCTCAColor.formLabel.color
                    } else {
                        cell.toInput.text = CTCAUIConstants.placeHolderString
                        cell.toInput.textColor = MyCTCAColor.placeHolder.color
                    }
                }
                return cell
            }
            if (indexPath.row == 3) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailSendNewSubjectCell") as! MailSendNewSubjectTableViewCell
                if (respondingMail != nil) {
                    cell.subjectTF.text = "RE: \(respondingMail!.subject)"
                }
                return cell
            }
            if (indexPath.row == 4) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailSendNewMessageCell") as! MailSendNewMessageTableViewCell
                if (respondingMail != nil) {
                    cell.messageTV.setText(str: "\n\n\n--------------------\n\n\(respondingMail!.comments)")
                    cell.messageTV.textColor = MyCTCAColor.formContent.color
                    cell.messageTV.scrollRangeToVisible(NSMakeRange(0, 0))
                }
                return cell
            }
        }
        let cell = UITableViewCell()
        return cell
    }
}

extension MailSendNewViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (indexPath.section == 0) {
            
            if (indexPath.row == 4) {
                return tableView.frame.size.height - self.otherTableRowHeights
            } else {
                let rowHeight: CGFloat = UITableView.automaticDimension
                self.otherTableRowHeights += rowHeight
                return rowHeight
            }
        }
        return cellMailDefaultHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if (indexPath.row == 2) {
            
            let discloseInfoTableViewController: DiscloseInfoTableViewController = self.storyboard?.instantiateViewController(withIdentifier: "DiscloseInfoTableViewController") as! DiscloseInfoTableViewController
            
            discloseInfoTableViewController.delegate = self
            discloseInfoTableViewController.discloseType = .careTeams
            discloseInfoTableViewController.customTitle = "Add Recipients"
                        
            if let careTeams = AppSessionManager.shared.currentUser.careTeams {
                var generalList: [(name: String, selected: Bool)] = [(String,Bool)]()
                
                for item in  careTeams {
                    if seletedOptionsArray.contains(item.name) {
                        generalList.append((item.name, true))
                    } else {
                        generalList.append((item.name, false))
                    }
                }
                discloseInfoTableViewController.careTeamsList = generalList
            }
            
            self.navigationController?.pushViewController(discloseInfoTableViewController, animated: true)
        }
        tableView.deselectRow(at: indexPath, animated: false)
    }
}

extension MailSendNewViewController : DiscloseInfoSelectDelegate {
    func didSelectCareTeams(value: String, data: [String]) {
        for choice in data {
            selectCareTeamForPayload(name: choice)
        }
        seletedOptionsArray = data
        toSelections = value
        setToSelection(values: value)
    }
}
