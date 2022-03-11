//
//  HomeViewController.swift
//  myctca
//
//  Created by Manjunath K on 9/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class HomeViewController: CTCABaseViewController {
    
    @IBOutlet weak var nameLable: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var subTitleLabel: UILabel!
    
    @IBOutlet weak var caregiverView: UIView!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var gotoPortalButton: UIButton!
    @IBOutlet weak var logoutButton: UIButton!
    @IBOutlet weak var elipsesButton: UIButton!
    @IBOutlet weak var tableHeight: NSLayoutConstraint!
    
    var isSeeMoreOff = true
    let seeMoreText = "            See More..."
    let seeLessText = "            See Less..."
    
    let footerHeightPort = 565.0
    let footerHeightLand = 435.0
    let footerquickLinkHeightPort = 565.0 - 261.0
    let footerquickLinkHeightLand = 435.0 - 261.0
    
    let appInfoManager = AppInfoManager.shared
    let homeManager = HomeManager.shared
    
    let authenticationManager = AuthenticationManager()
    var currentUserId = ""
    var currentUserName = ""
    
    var alertDetailsList = [myCTCAAlert]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Home"
        //self.navigationController?.navigationBar.setValue(true, forKey: "hidesShadow")
        
        prepareView()
        
        checkAppUpdatesAndDisgnostics()
    }
    
    func prepareView() {
        if let name = AppSessionManager.shared.currentUser.iUser?.fullName {
            currentUserName = name
        }
        nameLable.text = currentUserName
        currentUserId = AppSessionManager.shared.currentUser.userProfile!.ctcaId
        
        if AppSessionManager.shared.getUserType() == .PATIENT {
            tableView.isHidden = false
            caregiverView.isHidden = true
            
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_HOME_PATIENT_VIEW)
            elipsesButton.isHidden = true
        } else {
            tableView.isHidden = false
            caregiverView.isHidden = true
            
            self.tabBarController?.tabBar.isHidden = true
            setElipsesButtonMenu()
        }
        
        self.tableView.register(
            PatientHomeFooterView.nib,
            forHeaderFooterViewReuseIdentifier:
                PatientHomeFooterView.reuseIdentifier
        )
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        checkAppUpdatesAndDisgnostics()
    }
    
    override func viewWillLayoutSubviews() {
        super.updateViewConstraints()
        self.tableHeight?.constant = self.tableView.contentSize.height
    }
    
    override func viewDidAppear(_ animated: Bool) {
        print("viewDidAppear")
        if self.homeManager.messages.count > 1, self.homeManager.messagesAll.count > 0, let firstMessage = self.homeManager.messagesAll.first {
            self.homeManager.messages.removeAll()
            self.homeManager.messages.append(firstMessage)
            isSeeMoreOff = true

            DispatchQueue.main.async(execute: {
                self.initiateTableReload()
            })
        }
        
        showAlerts()
    }
    
    func checkAppUpdatesAndDisgnostics() {
        showActivityIndicator(view: self.view, message: "Loading...")
                
        appInfoManager.fetchAppInfo() { [self]
            status in
            
            if status == .SUCCESS {
                let appUpdateStatus = self.appInfoManager.isUpdateAvailable()

                if appUpdateStatus == .None {
                    self.processViewInfo()
                } else {
                    self.displayAppUpdateMessage(status: appUpdateStatus)
                }
            } else {
                self.fadeOutActivityIndicator()
                
                self.displayAppUpdateMessage(status:.Failed)
            }
        }
        
    }
    
    func fetchMessages(_ showIndicator: Bool = false) {
        if showIndicator {
            showActivityIndicator(view: self.view, message: "Loading...")
        }
        
        homeManager.fetchAlertMessages() {
            status in
            
            self.fadeOutActivityIndicator()
            
            if status == .SUCCESS {
                if self.homeManager.messages.count > 0 {
                    DispatchQueue.main.async(execute: {
                        self.initiateTableReload()
                    })
                }
            } else {
                ErrorManager.shared.showServerError(error: self.homeManager.getLastServerError(), onView: self)
            }
        }
    }
    
    func saveUserPref(value:Bool) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        if let userId = AppSessionManager.shared.currentUser.iUser?.userId {
            var userInfo = UserPreference()
            userInfo.userId = userId
            userInfo.userPreferenceType = "AcceptedDiagnosticReporting"
            userInfo.userPreferenceValueString = value ? "True" : "False"
            
            showActivityIndicator(view: self.view, message: "Submitting...")
            
            homeManager.saveUserPrefrences(preference: userInfo.getPayloadVariant()) {
                status in
                
                self.fadeOutActivityIndicator()
                
                if status == .FAILED {
                    ErrorManager.shared.showServerError(error: self.homeManager.getLastServerError(), onView: self)
                }
            }
        }
    }
    
    func checkDignosticReportingUserPref() {
        for item in AppSessionManager.shared.currentUser.currentUserPref {
            if item.userPreferenceType == "AcceptedDiagnosticReporting" {
                if item.userPreferenceValueString == "True" {
                    print("[AppCenter_mk] Analytics is on for this user")
                } else if item.userPreferenceValueString == "False" {
                    AnalyticsManager.shared.stopAnalyticsReporting()
                    print("[AppCenter_mk] Stopped analytics for this user")
                } else { //Unspecified

                    var alertInfo = myCTCAAlert()
                    
                    alertInfo.title = CommonMsgConstants.reportingTitle
                    alertInfo.message = CommonMsgConstants.reportingMessageText
                    
                    alertInfo.rightBtnTitle = "Yes"
                    alertInfo.rightBtnAction = {
                        self.saveUserPref(value: true)
                        
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ACCEPT_ANALYTICS_REPORTING_YES , [String : String]())
                    }
                    
                    alertInfo.leftBtnTitle = "No"
                    alertInfo.leftBtnAction = {
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ACCEPT_ANALYTICS_REPORTING_NO , [String : String]())
                        
                        AnalyticsManager.shared.stopAnalyticsReporting()
                        
                        self.saveUserPref(value: false)
                    }
                    
                    //GenericHelper.shared.showAlert(info: alertInfo)
                    addToAlertList(info: alertInfo)
                }
                
                break
            }
        }
    }
    
    override func didRotate(from fromInterfaceOrientation: UIInterfaceOrientation) {
        initiateTableReload()
    }
    
    func initiateTableReload() {
        //This is required becuase, change in orientation resizes table
        DispatchQueue.main.async(execute: {
            CATransaction.begin()
            CATransaction.setCompletionBlock({
                super.updateViewConstraints()
                self.tableHeight?.constant = self.tableView.contentSize.height
            })
            print("reloading")
            self.tableView.reloadData()
            CATransaction.commit()
        })
    }
    
    func processViewInfo() {
        self.checkDignosticReportingUserPref()
        self.checkSITinfo()
        self.fetchMessages()
    }
    
    func displayAppUpdateMessage(status:AppUpdateMessageType) {
        
        if status == .Optional {
            
            var alertInfo = myCTCAAlert()
            
            alertInfo.title = AppUpdateMsgConstants.appUpdateMsgTitle
            alertInfo.message = AppUpdateMsgConstants.optionalAlertMsg
            
            alertInfo.rightBtnTitle = "Yes"
            alertInfo.rightBtnAction = {
                GenericHelper.shared.openInSafari(path: AppInfoAPIRouter.openAppStore.path)
                self.processViewInfo()
            }
            
            alertInfo.leftBtnTitle = "Not Now"
            alertInfo.leftBtnAction = {
                self.processViewInfo()
            }
            
            addToAlertList(info: alertInfo)
            
        } else if status == .manadatory {
            self.fadeOutActivityIndicator()
            
            let updateAction = UIAlertAction(title: "Update", style: .default) { (action) in
                GenericHelper.shared.openInSafari(path: AppInfoAPIRouter.openAppStore.path)
                self.logOutUser()
            }
            
            GenericHelper.shared.showAlert(withtitle: AppUpdateMsgConstants.appUpdateMsgTitle, andMessage: AppUpdateMsgConstants.optionalAlertMsg, onView: self, okaction: updateAction)
        } else {
            //Error
            ErrorManager.shared.showServerError(error: appInfoManager.getLastServerError(), onView: self)
        }
    }
    
    func addToAlertList(info:myCTCAAlert) {
        alertDetailsList.append(info)
    }
    
    func showAlerts() {
        for item in alertDetailsList {
            GenericHelper.shared.showAlert(info: item)
        }
        if alertDetailsList.count > 0 {
            alertDetailsList.removeAll()
        }
    }
    
    func checkSITinfo() {
        //this msg is just for iPads
        if UIDevice.current.userInterfaceIdiom == .pad && appInfoManager.shouldShowMessage(appInfoManager.SIT_MESSAGE_DISPLAY_DATE_KEY) &&
            GenericHelper.shared.hasPermissionTo(feature: .viewSITPortal) {
            homeManager.fetchUserSurveyInfo() {
                url, status in
                
                if status == .SUCCESS, let sitUrl = url, !sitUrl.isEmpty {
                    //need to show the SIT message
                    self.addSITSurveyMessage(urlString: sitUrl)
                }
            }
        }
    }
   
    func addSITSurveyMessage(urlString:String) {
        var alertInfo = myCTCAAlert()
        
        alertInfo.title = HomeMsgConstants.SITMsgTitle
        alertInfo.message = HomeMsgConstants.SITAlertMsg
        
        alertInfo.rightBtnTitle = "Yes"
        alertInfo.rightBtnAction = {
            self.logOutUser()
            GenericHelper.shared.openInSafari(path: urlString)
            self.alertDetailsList.removeAll()
        }
        
        alertInfo.leftBtnTitle = "Not Now"
        
        if self.isViewLoaded {
            print("isViewLoaded")
            GenericHelper.shared.showAlert(info: alertInfo)
        } else {
            print("NO isViewLoaded")
            addToAlertList(info: alertInfo)
        }
    }
}

// MARK: Table View Data Source

extension HomeViewController: UITableViewDataSource, UITableViewDelegate, MessageTableViewCellProtocol {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return homeManager.messages.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "messageTableViewCell", for: indexPath) as! MessageTableViewCell
        
        let msg = self.homeManager.messages[indexPath.row]
        
        cell.messageLabel.text = msg.messageText
        
        if indexPath.row == 0 && isSeeMoreOff {
            cell.delegate = self
            cell.seeMoreBtn.setTitle(seeMoreText, for: .normal)
            cell.seeMoreHeightConst.constant = 25.0
        } else if indexPath.row == homeManager.messages.count - 1 && !isSeeMoreOff {
            cell.delegate = self
            cell.seeMoreBtn.setTitle(seeLessText, for: .normal)
            cell.seeMoreHeightConst.constant = 25.0
        } //last row
        else {
            cell.seeMoreHeightConst.constant = 0.0
            cell.seeMoreBtn.setTitle("", for: .normal)
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if UIApplication.shared.statusBarOrientation.isLandscape  {
            return CGFloat(footerHeightLand)
        }
        return CGFloat(footerHeightPort)
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        if let view = tableView.dequeueReusableHeaderFooterView(withIdentifier: PatientHomeFooterView.reuseIdentifier) as? PatientHomeFooterView {
            var quickLinkHeight = footerquickLinkHeightPort
            if UIApplication.shared.statusBarOrientation.isLandscape  {
                quickLinkHeight = footerquickLinkHeightLand
            }
            view.setUp(CGFloat(quickLinkHeight))
            view.parentVC = self
            return view
        }
        return nil
    }
    
    func didUserWantsToSeeMore() {
        
        if isSeeMoreOff { //off to on
            homeManager.messages.removeAll()
            homeManager.messages = homeManager.messagesAll
            
            DispatchQueue.main.async(execute: {
                self.initiateTableReload()
            })
        } else {  //on to ff
            homeManager.messages.removeAll()
            if let first = homeManager.messagesAll.first {
                homeManager.messages.append(first)
                DispatchQueue.main.async(execute: {
                    self.initiateTableReload()
                })
            }
        }
        
        isSeeMoreOff = !isSeeMoreOff
    }
}

//Caregiver related changes
extension HomeViewController : RecordSelectionDelegate {
    func didMakeSelection(value: Any, type: SelectionType) {
        print("didMakeSelection - \(value)")
        if let footer = self.tableView.footerView(forSection: 0) as? PatientHomeFooterView {
            let selection: (String, String) = value as! (String, String)
            AppSessionManager.shared.proxyPatientId = selection.1
            footer.viewRecordsButton.isHidden = false
            footer.patientNameField.text = selection.0
        }
    }
    
    func viewRecords() {
        if (self.tableView.footerView(forSection: 0) as? PatientHomeFooterView) != nil {
            switchUsers()
        }
    }
    
    @IBAction func gotoPortalTapped(_ sender: Any) {
        guard let url = URL(string: MyCTCAConstants.ExternalURL.portalLink) else { return }
        UIApplication.shared.open(url)
        
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_GOTO_PORTAL_TAP)
    }
    
    @IBAction func logoutButtonTapped(_ sender: Any) {
        logOutUser()
    }
    
    func logOutUser() {
        AppSessionManager.shared.endCurrentSession()
        
        DispatchQueue.main.async(execute: {
            if self.presentingViewController != nil {
                self.dismiss(animated: false, completion: {
                    self.navigationController!.popToRootViewController(animated: true)
                })
            } else {
                self.tabBarController?.navigationController!.popToRootViewController(animated: true)
            }
        })
        
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_SIGNOUT_CAREGIVER_TAP)
    }
    
    @objc func showMore() {
        let moreVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreViewController") as! MoreViewController
        self.show(moreVC, sender: nil)
    }
    
    @objc func convertAccount() {
        var alertInfo = myCTCAAlert()
        
        alertInfo.title = HomeMsgConstants.warningMsgTitle
        alertInfo.message = HomeMsgConstants.convertAccntAlertMsg
        
        alertInfo.rightBtnTitle = "Yes"
        alertInfo.rightBtnAction = {
            if let ctcaId = AppSessionManager.shared.currentUser.userProfile?.ctcaId {
                if let url = AuthenticationAPIRouter.openCovertAccntLink(userId: ctcaId).asUrl() {
                    //GenericHelper.shared.launchSafariViewController(withUrl: url, forView: self)
                    GenericHelper.shared.openInSafari(path: url.absoluteString)
                    self.logOutUser()
                } else {
                    ErrorManager.shared.showDefaultError(onView: self)
                }
            }
        }
        
        alertInfo.leftBtnTitle = "Not Now"
        
        GenericHelper.shared.showAlert(info: alertInfo)
    }
    
    @objc func backToMyAccount() {
        switchUsers()
    }
    
    func switchUsers() {
        var tProxy = true
        var ctcaId = AppSessionManager.shared.proxyPatientId
        
        self.showActivityIndicator(view: self.view, message: "Loading...")
        
        if AppSessionManager.shared.getUserType() == .PROXY {
            tProxy = false
            ctcaId = currentUserId
        }
        
        authenticationManager.switchToProxyUser(proxyUserId: ctcaId, toPatient: tProxy) {
            error, status in
                        
            if status == .SUCCESS {
                
                if AppSessionManager.shared.getUserType() != .PROXY {
                    self.setBackToCareGiver()
                    return
                }
                                
                self.authenticationManager.fetchUserProfile() {
                    status in

                    if status == .SUCCESS {
                                                
                        if let facCode = AppSessionManager.shared.currentUser.userProfile?.primaryFacilityCode {
                            if !facCode.isEmpty {
                                AppSessionManager.shared.setPrimaryFacility(facilityCode: facCode)
                            }
                        }
                        
                        self.authenticationManager.fetchIdentityUserInfo() {
                            status in
                            
                            if status == .SUCCESS {
                                self.fadeOutActivityIndicator()
                                self.retriveFacData()
                            } else {
                                self.failToSwitchToProxy()
                            }
                        }
                    } else {
                        self.failToSwitchToProxy()
                    }
                }
            } else if let sError = error {
                self.fadeOutActivityIndicator()
                
                ErrorManager.shared.showServerError(error: sError, onView: self)
            } else {
                self.fadeOutActivityIndicator()

                ErrorManager.shared.showDefaultError(onView: self)
            }
        }
    }
    
    func failToSwitchToProxy() {
        self.fadeOutActivityIndicator()
        
        ErrorManager.shared.showServerError(error: self.authenticationManager.getLastServerError(), onView: self)
        
        self.authenticationManager.changeToOriginalUser()
    }
    
    func retriveFacData() {
        let facList = AppSessionManager.shared.currentUser.allFacilitesList
        
        if facList.count > 0 {
            self.setViewForProxyUser()

            return
        }
        
        self.showActivityIndicator(view: self.view, message: "Loading...")
        
        if let facCode = AppSessionManager.shared.currentUser.userProfile?.primaryFacilityCode {
            if !facCode.isEmpty {
                authenticationManager.fetchAllFacilites(facCode:facCode) {
                    status in
                    if (status == .SUCCESS) {
                        self.fadeOutActivityIndicator()

                        self.setViewForProxyUser()
                    } else {
                        self.failToSwitchToProxy()
                    }
                }
            }
        }
    }
    
    func setElipsesButtonMenu() {
        if #available(iOS 14.0, *) {
            
            if AppSessionManager.shared.getUserType() == .PROXY {
                let moreAction =  UIAction(title: "Back to my account", image: nil, handler: { _ in
                                            self.backToMyAccount()})
                
                let items = UIMenu(title: "", options: .displayInline, children: [
                                    moreAction])
                elipsesButton.menu = UIMenu(title: "", children: [items])
            } else if AppSessionManager.shared.getUserType() == .CARE_GIVER {
    
                let moreAction =  UIAction(title: "More", image: nil, handler: { _ in
                                            self.showMore()})
                
                if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.viewConvertAccountSect) {
                    let convertAcctAction = UIAction(title: "Convert Account", image: nil, handler: { _ in
                                                        self.convertAccount()})
                    
                    let items = UIMenu(title: "", options: .displayInline, children: [
                                        moreAction, convertAcctAction])
                    elipsesButton.menu = UIMenu(title: "", children: [items])
                } else {
                    let items = UIMenu(title: "", options: .displayInline, children: [
                                        moreAction])
                    elipsesButton.menu = UIMenu(title: "", children: [items])
                }
            }
            
            elipsesButton.showsMenuAsPrimaryAction = true
            
        } else {
            elipsesButton.addTarget(self, action: #selector(elipsesButtonTapped), for: .touchUpInside)
        }
    }
    
    @objc private func elipsesButtonTapped() {
        var items = [UIButton]()
        
        if AppSessionManager.shared.getUserType() == .PROXY {
            let moreBtn = UIButton()
            moreBtn.setImage(nil, for: .normal)
            moreBtn.addTarget(self, action: #selector(backToMyAccount), for: .touchUpInside)
            moreBtn.setTitle("Back to my account", for: .normal)
            items = [moreBtn]
            
            let navigationBarHeight: CGFloat = self.navigationController!.navigationBar.frame.height + UIApplication.shared.statusBarFrame.size.height
            
            MenuOverlay.shared.showOverlay(view:view, buttons: &items, origin:navigationBarHeight)
            
        } else if AppSessionManager.shared.getUserType() == .CARE_GIVER {
            let moreBtn = UIButton()
            moreBtn.setImage(nil, for: .normal)
            moreBtn.addTarget(self, action: #selector(showMore), for: .touchUpInside)
            moreBtn.setTitle("More", for: .normal)
            items = [moreBtn]
            
            if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.viewConvertAccountSect) {
                let convertAcctBtn = UIButton()
                convertAcctBtn.setImage(nil, for: .normal)
                convertAcctBtn.addTarget(self, action: #selector(convertAccount), for: .touchUpInside)
                convertAcctBtn.setTitle("Convert Account", for: .normal)
                
                items.append(convertAcctBtn)
            }
            
            let navigationBarHeight: CGFloat = self.navigationController!.navigationBar.frame.height + UIApplication.shared.statusBarFrame.size.height
            
            MenuOverlay.shared.showOverlay(view:view, buttons: &items, origin:navigationBarHeight)
        }
    }
    
    func setViewForProxyUser() {
                
        DispatchQueue.main.async(execute: {
            var proxyName = self.currentUserName
            
            if let proxies = AppSessionManager.shared.currentUser.userProfile?.proxies {
                for item in proxies {
                    if item.isImpersonating == true {
                        proxyName = item.firstName + " " + item.lastName
                        break
                    }
                }
            }

            self.nameLable.text = "\(self.currentUserName) (viewing \(proxyName))"
            
            self.tabBarController?.tabBar.isHidden = false
            
            self.homeManager.messages.removeAll()
            self.homeManager.messagesAll.removeAll()
            
            self.elipsesButton.isHidden = false
                        
            self.tableView.reloadSections(IndexSet(integer: 0), with: .fade)
            
            self.initiateTableReload()
            
            self.fetchMessages(true)
            
            self.setElipsesButtonMenu()
            
            if let myCTCATabBarController = self.tabBarController as? MyCTCATabBarController {
                myCTCATabBarController.resetMainVCs()
            }
        })
    }
    
    func setBackToCareGiver() {
        
        DispatchQueue.main.async(execute: {

            self.nameLable.text = self.currentUserName
                        
            self.homeManager.messages.removeAll()
            self.homeManager.messagesAll.removeAll()
                   
            self.tableView.reloadSections(IndexSet(integer: 0), with: .fade)
            
            self.initiateTableReload()
            
            self.fetchMessages(false)
            
            if AppSessionManager.shared.getUserType() == .PATIENT {
                self.elipsesButton.isHidden = true
                self.tabBarController?.tabBar.isHidden = false
            } else {
                self.tabBarController?.tabBar.isHidden = true
            }
            
            self.setElipsesButtonMenu()
            
            if let myCTCATabBarController = self.tabBarController as? MyCTCATabBarController {
                myCTCATabBarController.resetMainVCs()
            }
            
        })
    }
}

