//
//  ClinicalSummaryTrasmitViewController.swift
//  myctca
//
//  Created by Manjunath K on 8/14/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class ClinicalSummaryTrasmitViewController: UIViewController, ClinicalSummarySelectionProtocol, CTCAViewControllerProtocol, ClinicalSummaryTransmitActionProtocol {

    var selectionCellTagConst = 1001
    @IBOutlet weak var tableView: UITableView!
    let doneToolbarHeight = 50.0
    
    var descriptionCells = [String]()
    var securityOptionsCells = [String]()
    var emailOptionsCells = [String]()
    var documentIDs = [String]()
    
    var noOfRows = 9
    var showSecureView = false
        
    var passwordCell = ClinicalSummarySelectionTableViewCell()
    var sendSecureCell = ClinicalSummarySelectionTableViewCell()
    var sendNonSecureCell = ClinicalSummarySelectionTableViewCell()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.delegate = self
        tableView.dataSource = self
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CS_TRANSMIT_VIEW)
        
        prepareView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillHideNotification, object: nil)
        tableView.setNeedsDisplay()
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
            tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: keyboardViewEndFrame.height+CGFloat(doneToolbarHeight), right: 0)
        }
        
        tableView.scrollIndicatorInsets = tableView.contentInset
    }
    
    func prepareView() {
        //Description cells
        descriptionCells = [ClinicalSummaryUIConstants.firstLine, ClinicalSummaryUIConstants.warningText, ClinicalSummaryUIConstants.fileText, "Security:",  ClinicalSummaryUIConstants.sendSecureText]
        
        securityOptionsCells = ["Add Password Protection", "Send Secure", "Send Non-Secure (via normal email delivery)"]

        emailOptionsCells = ["Regular Email:", "File Password:"]
    }
    
    func didOptForPasswordProtection(state: Bool) {
        
        showSecureView = state
        
        if state { //show secure view
            noOfRows = 11
        } else {
            noOfRows = 9
        }
        selectionCellTagConst = 1001
        tableView.reloadData()
    }
    
    func didPerformAction(state: Bool) {

        if state {
            if !passwordCell.stateChecked && !sendSecureCell.stateChecked && !sendNonSecureCell.stateChecked {
                GenericHelper.shared.showAlert(withtitle: ClinicalSummaryMsgConstants.transmitAlertTitle, andMessage: ClinicalSummaryMsgConstants.securityValidMessage, onView: self)
                
                return
            }
            
            var transmitInfo = ClinicalSummaryTrasmitInfo()
            transmitInfo.documentId = documentIDs
            
                if showSecureView {
                    if let cell = tableView.cellForRow(at: IndexPath(row: 8, section: 0)) as? ClinicalSummaryTransmitPasswordTableViewCell {
                        let passText = cell.emailTF.text!
                        if passText == "" {
                            GenericHelper.shared.showAlert(withtitle: ClinicalSummaryMsgConstants.transmitAlertTitle, andMessage: ClinicalSummaryMsgConstants.passwordValidationMessage, onView: self)
                            return
                        } else {
                            transmitInfo.filePass = passText
                        }
                    }
                    
                    if let cell = tableView.cellForRow(at: IndexPath(row: 9, section: 0)) as? ClinicalSummaryTransmitEmailTableViewCell {
                        if GenericHelper.shared.isValidEmail(cell.emailTF.text!) {
                            transmitInfo.directAddress = cell.emailTF.text!
                        } else {
                            GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.emailValidationTitle, andMessage: CommonMsgConstants.emailValidationMessage, onView: self)
                            return
                        }
                    }
                    
                    if sendSecureCell.stateChecked {
                        transmitCS(transmitDetails:transmitInfo, withSecurity: true)
                    } else {
                        transmitCS(transmitDetails:transmitInfo,withSecurity: false)
                    }

                } else {
                    if let cell = tableView.cellForRow(at: IndexPath(row: 7, section: 0)) as? ClinicalSummaryTransmitEmailTableViewCell {
                        if GenericHelper.shared.isValidEmail(cell.emailTF.text!) {
                            transmitInfo.directAddress = cell.emailTF.text!
                        } else {
                            GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.emailValidationTitle, andMessage: CommonMsgConstants.emailValidationMessage, onView: self)
                            return
                        }
                    }
                    transmitCS(transmitDetails:transmitInfo, withSecurity: false)
                }
                
        } else {
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    func transmitCS(transmitDetails:ClinicalSummaryTrasmitInfo, withSecurity:Bool) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_TRANSMIT_TAP)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.transmitCSText)
                
        MedDocsManager.shared.transmitCSDoc(isSecure: withSecurity, transmitInfo: transmitDetails) {
            success, expt, error in
            
            self.dismissActivityIndicator()
            
            if success {
                GenericHelper.shared.showAlert(withtitle: ClinicalSummaryMsgConstants.transmitStatusAlertTitle, andMessage: ClinicalSummaryMsgConstants.transmitSuccessMessage, onView: self)
                
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_CS_TRANSMIT_SUCCESS)
            } else {
                var exceptionString = ""
                if let exptnErr = expt?.exception {

                    if exptnErr.errors.count > 0 {
                        exceptionString = exptnErr.errors[0].errorMessage
                    }

                } else {
                    exceptionString = ClinicalSummaryMsgConstants.transmitFailMessage
                }
                
                GenericHelper.shared.showAlert(withtitle: ClinicalSummaryMsgConstants.transmitStatusAlertTitle, andMessage: exceptionString, onView: self)
                
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_CS_TRANSMIT_FAIL)

            }
        }
    }
}

extension ClinicalSummaryTrasmitViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        noOfRows
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row < 4 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryDescriptionTableViewCell") as! ClinicalSummaryDescriptionTableViewCell
            
            if indexPath.row == 1 {
                let labelText = NSMutableAttributedString(string: "Warning: \(descriptionCells[indexPath.row])", attributes: [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue-Medium", size: 15.0)!])

                labelText.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.red, range: NSRange(location:0,length:8))
                
                cell.descriptionLabel.attributedText = labelText
                
            } else {
                cell.descriptionLabel.text = descriptionCells[indexPath.row]
            }
            
            return cell
        }
        else if indexPath.row < 7 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummarySelectionTableViewCell") as! ClinicalSummarySelectionTableViewCell
            cell.descriptionLabel.text = securityOptionsCells[indexPath.row-4]
            cell.tag = selectionCellTagConst
            selectionCellTagConst += 1
            cell.delegate = self
            
            switch indexPath.row {
                case 4:
                    passwordCell = cell
                case 5:
                    sendSecureCell = cell
                case 6:
                    sendNonSecureCell = cell
            default:
                break
            }
            return cell
        }
        
        if showSecureView {
            if indexPath.row == 7 {
             let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryDescriptionTableViewCell") as! ClinicalSummaryDescriptionTableViewCell
                cell.descriptionLabel.text = ClinicalSummaryUIConstants.sendSecureText
                return cell
            } else if indexPath.row == 8 {
                let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryTransmitPasswordTableViewCell") as! ClinicalSummaryTransmitPasswordTableViewCell
                cell.titleLabel.text = emailOptionsCells[1]
                cell.emailTF.text = ""
                return cell
            }  else if indexPath.row == 9 {
                let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryTransmitEmailTableViewCell") as! ClinicalSummaryTransmitEmailTableViewCell
                cell.titleLabel.text = emailOptionsCells[0]
                cell.emailTF.text = ""
                cell.emailTF.keyboardType = .emailAddress
                return cell
            } else {
                let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryTransmitActionTableViewCell") as! ClinicalSummaryTransmitActionTableViewCell
                cell.closeButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
                cell.closeButton.layer.borderWidth = 0.5
                cell.delegate = self
                return cell
            }
        } else {
            if indexPath.row == 7 {
                let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryTransmitEmailTableViewCell") as! ClinicalSummaryTransmitEmailTableViewCell
                cell.titleLabel.text = emailOptionsCells[0]
                cell.emailTF.keyboardType = .emailAddress
                cell.emailTF.text = ""
                return cell
            }
            else {
                let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryTransmitActionTableViewCell") as! ClinicalSummaryTransmitActionTableViewCell
                cell.closeButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
                cell.closeButton.layer.borderWidth = 0.5
                cell.delegate = self
                return cell
            }
        }
    }
}

extension ClinicalSummaryTrasmitViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.row > 3 && indexPath.row < 7 {

            if indexPath.row == 4 {
                if !sendSecureCell.stateChecked {
                    passwordCell.setState()
                }
                
                if sendNonSecureCell.stateChecked  {
                    sendNonSecureCell.setState()
                }
            } else if indexPath.row == 6 {
                sendNonSecureCell.setState()
                
                if passwordCell.stateChecked  {
                    passwordCell.setState()
                }
                
                if sendSecureCell.stateChecked  {
                     sendSecureCell.setState()
                }
                
            } else if indexPath.row == 5 {
                sendSecureCell.setState()
                if !passwordCell.stateChecked  {
                    passwordCell.setState()
                }
                
                if sendNonSecureCell.stateChecked  {
                     sendNonSecureCell.setState()
                }
            }
        }
    }
}
