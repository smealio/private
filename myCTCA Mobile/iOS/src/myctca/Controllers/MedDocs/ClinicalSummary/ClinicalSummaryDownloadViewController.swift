//
//  ClinicalSummaryDownloadViewController.swift
//  myctca
//
//  Created by Manjunath K on 8/18/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class ClinicalSummaryDownloadViewController: UIViewController, CTCAViewControllerProtocol, ClinicalSummaryDownloadActionProtocol {
    
    @IBOutlet weak var tableView: UITableView!
    let doneToolbarHeight = 50.0
    
    var documentIDs = [String]()
    
    var screenTextList = [String]()
    var passwordCell = ClinicalSummaryDownloadPasswordTableViewCell()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CS_DOWNLOAD_VIEW)
        
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
    
    func prepareView() {
        screenTextList.append(ClinicalSummaryUIConstants.passwordText)
        screenTextList.append(ClinicalSummaryUIConstants.downloadText)
        screenTextList.append(ClinicalSummaryUIConstants.warningTextDwnld)
        screenTextList.append(ClinicalSummaryUIConstants.fileText)
        screenTextList.append(ClinicalSummaryUIConstants.sendSecureText)
    }
    
    func didPerformAction(state: Bool) {
        if state {
            print("Download Tapped")
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            downloadCSNew(password: passwordCell.emailTF.text!)
            
        } else {
            self.navigationController?.popViewController(animated: true)
        }
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
    
    func showActivityWindow(fileURL:URL) {
        
        DispatchQueue.main.async {
            if #available(iOS 11.0, *) {
                let activityController = UIActivityViewController(activityItems: [fileURL], applicationActivities: nil)
                
                //For ipad
                if let popoverController = activityController.popoverPresentationController {
                    popoverController.sourceRect = CGRect(x: UIScreen.main.bounds.width / 2, y: UIScreen.main.bounds.height / 2, width: 0, height: 0)
                    popoverController.sourceView = self.view
                    popoverController.permittedArrowDirections = UIPopoverArrowDirection(rawValue: 0)
                }
                
                self.present(activityController, animated: true, completion: nil)
            } else {
                GenericHelper.shared.showAlert(withtitle: ClinicalSummaryMsgConstants.downloadAlertTitle, andMessage: MyCTCAConstants.PlatformErrorMessage.lowerOSError, onView:self)
            }
        }
    }
    
    func downloadCSNew(password:String) {
        self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.downloadCSText)
        
        let downloadInfo = ClinicalSummaryDownloadInfo(list:documentIDs, password:password)
        
        MedDocsManager.shared.downloadCSDoc(downloadInfo: downloadInfo) {
            localPath, status in
            
            self.dismissActivityIndicator()
            
            if status == .FAILED {
                //download failed error
                ErrorManager.shared.showServerError(error: MedDocsManager.shared.getLastServerError(), onView: self)
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_CS_DOWNLOAD_FAIL)
            } else  {
                guard let path = localPath, let url = URL(string: path) else {
                    ErrorManager.shared.showDefaultError(onView: self)
                    return
                }
                
                var alertInfo = myCTCAAlert()
                
                alertInfo.title = ClinicalSummaryMsgConstants.downloadAlertTitle
                alertInfo.message = ClinicalSummaryMsgConstants.downloadSuccessMessage
                
                alertInfo.rightBtnTitle = "Save"
                alertInfo.rightBtnAction = {
                    self.showActivityWindow(fileURL: url)
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_DOWNLOAD_ZIP_SAVE)
                }
                
                alertInfo.leftBtnTitle = "Cancel"
                alertInfo.leftBtnAction = {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_DOWNLOADED_ZIP_CANCEL)
                }
                
                GenericHelper.shared.showAlert(info: alertInfo)
                
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_CS_DOWNLOAD_SUCCESS)
            }
        } 
    }

    func getCSZipFileName() -> URL? {
        let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        var targetURL:URL?
        
        var index = 1
        var stringIndex = ""
        
        while (targetURL == nil) {
            
            targetURL = documentsURL.appendingPathComponent("\(MyCTCAConstants.FileNameConstants.CSZipName+stringIndex)")
            
            if FileManager.default.fileExists(atPath: (targetURL?.path)!) {
                targetURL = nil
            }
            
            stringIndex = " (\(index))"
            index += 1
            
            //Some point we need to break
            if index == 50 {
                break
            }
        }
        
        return targetURL
    }
}

extension ClinicalSummaryDownloadViewController : UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        7
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 5 { //password field
            let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryDownloadPasswordTableViewCell") as! ClinicalSummaryDownloadPasswordTableViewCell
            cell.titleLabel.text = "Optional File Password:"
            passwordCell = cell
            return cell
        }
        
        if indexPath.row ==  6 { //button field
            let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryDownloadActionTableViewCell") as! ClinicalSummaryDownloadActionTableViewCell
            cell.delegate = self
            cell.closeButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
            cell.closeButton.layer.borderWidth = 0.5
            return cell
        }
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummaryDescriptionTableViewCell") as! ClinicalSummaryDescriptionTableViewCell
        
        if indexPath.row == 2 {
            let labelText = NSMutableAttributedString(string: "Warning: \(screenTextList[indexPath.row])", attributes: [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue-Medium", size: 15.0)!])
            
            labelText.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.red, range: NSRange(location:0,length:8))
            
            cell.descriptionLabel.attributedText = labelText
            
        } else {
            cell.descriptionLabel.text = screenTextList[indexPath.row]
        }
        
        return cell
    }
    
}
