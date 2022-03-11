//
//  ApptViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 11/10/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit
import LocalAuthentication
import SafariServices

class ApptViewController: CTCABaseViewController, PeriodSelectionProtocol {
    
    @IBOutlet weak var tableView: CTCATableView!
    @IBOutlet weak var apptSelector: UISegmentedControl!
    
    let cellNextHeight: CGFloat = 190.0
    let cellHeaderHeight: CGFloat = 40.0
    let cellApptHeight: CGFloat = 107.0
    
    @IBOutlet weak var addApptButton: UIBarButtonItem!
    @IBOutlet weak var downloadButton: UIBarButtonItem!
    
    var selectedAppointment: Appointment?
    
    let APPT_REQ_SEGUE: String = "ApptRequestSegue"
    let APPT_DET_SEGUE: String = "ApptDetailSegue"
    
    let APPT_REQ_REQUEST_APPT: String = "Request Appointment"
    let APPT_REQ_NEW_APPT: String = "New appointment request"
    
    lazy var refreshCtrl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refreshApptData(_:)), for: .valueChanged)
        refreshControl.tintColor = MyCTCAColor.ctcaSecondGreen.color
        refreshControl.attributedTitle = NSAttributedString(string: ActivityIndicatorMsgs.refreshApptText, attributes: [
            NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Italic", size: 14.0)!,
            NSAttributedString.Key.foregroundColor : MyCTCAColor.ctcaGreen.color
            ])
        
        return refreshControl
    }()
    
    var hasViewApptPermission: Bool = false
    var hasRequestApptPermission: Bool = false
    
    var pastApptViewed = false
    
    let appointmentaManager = AppointmentsManager.shared
    var nextAppointment:Appointment?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Set Title
        self.title = "Appointments"
        // Navigation Bar
        self.navigationController?.navigationBar.setValue(true, forKey: "hidesShadow")
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        // Add Refresh Control (pull-to-refresh)
        if #available(iOS 10.0, *) {
            tableView.refreshControl = refreshCtrl
        } else {
            tableView.addSubview(refreshCtrl)
        }
        
        // Segmented Control
        self.apptSelector.selectedSegmentIndex = 0
        self.apptSelector.addTarget(self, action: #selector(apptSelectorChanged), for: .valueChanged)
        
        appointmentaManager.hostViewController = self
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_FUTURE_APPOINTMENTS_VIEW)
        
        let tableViewLoadingCellNib = UINib(nibName: "AppointmentsTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCellNib, forCellReuseIdentifier: "AppointmentsTableViewCell")
        
    }
    
    func promptforBiometric() {
        if (BiometricManager.shared.biometricCapable() == true) {
            self.promptForBiometricAuthentication()
        } else {
            print("NOT BIOMETRIC CAPABLE")
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        super.viewWillAppear(animated)
                
        self.navigationController?.navigationBar.setValue(true, forKey: "hidesShadow")
        
        // Check Permissions
        let currentUserId = AppSessionManager.shared.currentUser.currentUserId
        let userProfile = AppSessionManager.shared.currentUser.userProfile!
        
        hasViewApptPermission = userProfile.userCan(UserPermissionType.viewApppointments, viewerId: currentUserId)
        hasRequestApptPermission = userProfile.userCan(UserPermissionType.requestAppointments, viewerId: currentUserId)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        if appointmentaManager.appointments.count == 0 {
            downloadButton.isEnabled = false
        }
        
        if (hasRequestApptPermission) {
            addApptButton?.isEnabled = true
            //addApptButton?.tintColor = UIColor.ctca_theme_middle
        } else {
            addApptButton?.isEnabled = false
            //addApptButton?.tintColor = UIColor.ctca_gray90
        }
        
        self.loadViewData()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)

        if (hasViewApptPermission) {
            if appointmentaManager.appointments.count == 0 {
                // Show Activity Indicator
                showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.retriveApptText)
                
                // Fetch Data
                
                appointmentaManager.fetchAppointments() {
                    result in
                    
                    self.fadeOutActivityIndicator(completion: nil);
                    self.promptforBiometric()
                    
                    switch(result) {
                    case .FAILED:
                        ErrorManager.shared.showServerError(error: self.appointmentaManager.getLastServerError(), onView: self)
                    case .SUCCESS:
                        self.appointmentaManager.buildUpcomingAndPastData()
    
                        DispatchQueue.main.async {
                            self.tableView.reloadData()
                            if self.appointmentaManager.appointments.count > 0 {
                                self.downloadButton.isEnabled = true
                            }
                        }

                    }
                }
            } 
        } else {
            promptforBiometric()
        }
    }
    
    func getEmptyDataInfo() -> EmptyDataInfo {
        var info = EmptyDataInfo()
        if (self.apptSelector.selectedSegmentIndex == 0) {
            info.subTitle = AppointmentMsgConstants.noUpComingApptsMessage
        } else {
            info.subTitle = AppointmentMsgConstants.noUpPastApptsMessage
        }
        
        info.buttonAction = {
            self.requestAppt(UIBarButtonItem())
        }
        
        return info
    }
    
    @objc private func refreshApptData(_ sender: Any) {
        appointmentaManager.fetchAppointments() {
            result in
            
            self.fadeOutActivityIndicator(completion: nil);
            self.promptforBiometric()
            
            switch(result) {
            case .FAILED:
                ErrorManager.shared.showServerError(error: self.appointmentaManager.getLastServerError(), onView: self)
            case .SUCCESS:
                self.appointmentaManager.buildUpcomingAndPastData()
                
                DispatchQueue.main.async {
                    self.refreshCtrl.endRefreshing()
                    self.tableView.reloadData()
                    if self.appointmentaManager.appointments.count > 0 {
                        self.downloadButton.isEnabled = true
                    }
                }

            }
        }
    }
        
    @objc private func apptSelectorChanged() {
        if !pastApptViewed {
            //for tracking once
            pastApptViewed = true
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_PAST_APPOINTMENTS_VIEW)
        }
        
        self.tableView.quickFadeOut(completion: {
            (finished: Bool) -> Void in
            self.tableView.reloadData()
            self.tableView.quickFadeIn()
        })
    }
    
    @IBAction func requestAppt(_ sender: UIBarButtonItem) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_REQUEST_TAP)
        
        AppointmentsManager.shared.prepareAppointmentRequestFor(requestType: .new, appointment: nil)
        
        let storyboard = UIStoryboard(name: "Appointments", bundle: nil)
        if #available(iOS 13.0, *) {
            let appointmentReqVC = storyboard.instantiateViewController(withIdentifier: "NewApptRequestViewController") as! NewApptRequestViewController
            
            appointmentReqVC.title = "Request Appointment"
            let navController = UINavigationController(rootViewController: appointmentReqVC)
            self.present(navController, animated: true, completion: nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == APPT_REQ_SEGUE {
            let destinationNavigationController = segue.destination as! UINavigationController
            if let targetController = destinationNavigationController.topViewController as? ApptRequestViewController {
                targetController.title = self.APPT_REQ_REQUEST_APPT
                targetController.apptReqSubject = self.APPT_REQ_NEW_APPT
            }
        }
        if segue.identifier == APPT_DET_SEGUE {
            
            if let destinationController = segue.destination as? ApptDetailViewController, let appointment = self.selectedAppointment {
                destinationController.appointment = appointment
            }
        }
    }
    
    func dismissThis(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func downloadAppointmentsTapped(_ sender: Any) {

        if appointmentaManager.appointments.count == 0 {
            //show message
            GenericHelper.shared.showAlert(withtitle: AppointmentMsgConstants.noRecordsMessageTitle, andMessage: CommonMsgConstants.noRecordsFoundMsg, onView: self)
            return
        }
        
        
        downloadButton.isEnabled = false
        addApptButton.isEnabled = false
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_APPOINTMENTS_FILTER_VIEW)
        
        let filterViewController = UIStoryboard(name: "Secondary", bundle: nil).instantiateViewController(withIdentifier: "PeriodSelectionViewControllerNew") as! PeriodSelectionViewControllerNew

        filterViewController.delegate = self
        //filterViewController.isDateLimited = false
        //filterViewController.type = .Appointments
        self.present(filterViewController, animated: true, completion: nil)
        filterViewController.title = "Download Schedule"
    }
    
    func periodSelected(fromDate: String, toDate: String) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_DOWNLOAD_TAP)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        //quick fix for 3.1.1
        var newFromDateString = ""
        var newToDateString = ""

        if let newFromDate = DateConvertor.convertToDateFromString(dateString: fromDate, inputFormat: .usStandardForm2) {
            newFromDateString = DateConvertor.convertToStringFromDate(date: newFromDate, outputFormat: .dayAndBaseForm)
        }
        
        if let newToDate = DateConvertor.convertToDateFromString(dateString: toDate, inputFormat: .usStandardForm2) {
            newToDateString = DateConvertor.convertToStringFromDate(date: newToDate, outputFormat: .dayAndBaseForm)
        }
        
        if newToDateString.isEmpty || newFromDateString.isEmpty {
            return
        }
        
        var params = [String:String]()
                
        if fromDate != "" {
            params["startDate"] = newFromDateString
        }
        
        if toDate != "" {
            params["endDate"] = newToDateString
        }
        
        self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.downloadApptText)
        
        appointmentaManager.downloadAppointments(params: params) {
            status in
            
            self.dismissActivityIndicator()
            
            DispatchQueue.main.async {
                self.downloadButton.isEnabled = true
                self.addApptButton.isEnabled = true
            }
            
            if status == .FAILED {
                //display error
                ErrorManager.shared.showServerError(error: self.appointmentaManager.getLastServerError(), onView: self)
            }
        }
    }
    
    func cancelledPeriodSelection() {
        self.downloadButton.isEnabled = true
        addApptButton.isEnabled = true
    }
    
    func reloadAppts() {
        appointmentaManager.buildUpcomingAndPastData()
        
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
}

// MARK: Table View Data Source

extension ApptViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        if (self.apptSelector.selectedSegmentIndex == 0) {
            if appointmentaManager.nextSections.count > 0 {
                return 2
            }
        }
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        var retVal = 0
        
        if (self.apptSelector.selectedSegmentIndex == 0) {
            if section == 0 && appointmentaManager.nextSections.count > 0 {
                retVal = 1
            } else {
                retVal = appointmentaManager.upcomingSections.count
            }
        } else if (self.apptSelector.selectedSegmentIndex == 1) {
            retVal = appointmentaManager.pastSections.count
        }
        
        if let tabelV = tableView as? CTCATableView {
            if retVal == 0 {
                tabelV.setEmptyView(info:getEmptyDataInfo())
            } else {
                tabelV.restoreBGView()
            }
        }
        return retVal
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (self.apptSelector.selectedSegmentIndex == 0) {
            // Upcoming Appointemnt
            if (self.apptSelector.selectedSegmentIndex == 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "AppointmentsTableViewCell") as! AppointmentsTableViewCell

                if indexPath.section == 0 && appointmentaManager.nextSections.count > 0 {
                    let sectionStr = appointmentaManager.nextSections[0]
                    let apptArray = appointmentaManager.nextAppt[sectionStr]!
                    nextAppointment = apptArray[0]
                    cell.prepareView(labelText: sectionStr, appointmentsList: apptArray, isNextAppt: true, moveApptToPast: reloadAppts)
                    cell.delegate = self
                    return cell
                } else {
                    let rowIndex = indexPath.row
                    let sectionStr = appointmentaManager.upcomingSections[rowIndex]
                    let apptArray = appointmentaManager.upcomingAppt[sectionStr]!
                    cell.prepareView(labelText: sectionStr, appointmentsList: apptArray, moveApptToPast: nil)
                    cell.delegate = self
                    return cell
                }
            }
        } else if (self.apptSelector.selectedSegmentIndex == 1) {
            // Past Appointemnt
            let cell = tableView.dequeueReusableCell(withIdentifier: "AppointmentsTableViewCell") as! AppointmentsTableViewCell
            let sectionStr = appointmentaManager.pastSections[indexPath.row]
            let apptArray = appointmentaManager.pastAppt[sectionStr]!
            cell.prepareView(labelText: sectionStr, appointmentsList: apptArray, moveApptToPast: nil)
            cell.delegate = self
            return cell
        }
        let cell = UITableViewCell()
        return cell
    }
}

extension ApptViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if (self.apptSelector.selectedSegmentIndex == 0 && section == 0) {
            if (appointmentaManager.pastSections.count > 0) {
                return cellHeaderHeight
            }
        }
        
        return CGFloat.leastNonzeroMagnitude
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if (self.apptSelector.selectedSegmentIndex == 0 && section == 0) {
            if (appointmentaManager.nextSections.count > 0) {
                let cell = ApptSectionCell()
                cell.prepareView("NEXT APPOINTMENT")
                return cell
            }
        }

        return UIView()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
}

extension ApptViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        
        if (self.apptSelector.selectedSegmentIndex == 1) {
            let str = "myCTCA Past Appointments"
            
            let attrs = [NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: UIFont.TextStyle.headline),
                         NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color]
            
            return NSAttributedString(string: str, attributes: attrs)
        }
        return NSAttributedString(string: "", attributes: nil)
    }
    
    func description(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        var str = CommonMsgConstants.noRecordsFoundMsg
        let style = NSMutableParagraphStyle()
        style.alignment = NSTextAlignment.center
        
        if (self.apptSelector.selectedSegmentIndex == 0) {
            str = "You have no upcoming appointments."

            if (!hasViewApptPermission) {
                str = "You don't have permission to view upcoming appointments. If this is incorrect, please call the scheduling department directly."
                style.alignment = NSTextAlignment.left
            }
        } else {
            if (!hasViewApptPermission) {
                str = "You don't have permission to view past appointments. If this is incorrect, please call the scheduling department directly."
                style.alignment = NSTextAlignment.left
            }
        }

        let attrs = [NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Medium", size: 15.0)!,
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color,
                     NSAttributedString.Key.paragraphStyle: style ]
        
        return NSAttributedString(string: str, attributes: attrs)
    }
    
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        if UIDevice.current.orientation.isLandscape {
            return UIImage(named: "myctca_logo_128")
        } else {
            return UIImage(named: "myctca_logo_256")
        }
    }
    
    override func didRotate(from fromInterfaceOrientation: UIInterfaceOrientation) {
        if (self.apptSelector.selectedSegmentIndex == 0) {
            if (appointmentaManager.upcomingSections.count == 0) {
                self.tableView.reloadData()
            }
        } else if (self.apptSelector.selectedSegmentIndex == 1) {
            if (appointmentaManager.pastSections.count == 0) {
                self.tableView.reloadData()
            }
        }
    }
    
    func buttonTitle(forEmptyDataSet scrollView: UIScrollView, for state: UIControl.State) -> NSAttributedString? {
        if GenericHelper.shared.hasPermissionTo(feature: .requestAppointments) {
            let style = NSMutableParagraphStyle()
            style.alignment = NSTextAlignment.center
            
            let attrs = [NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Medium", size: 15.0)!,
                         NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGreen.color,
                         NSAttributedString.Key.underlineStyle: NSUnderlineStyle.single.rawValue,
                         NSAttributedString.Key.paragraphStyle: style ] as [NSAttributedString.Key : Any]
            return NSAttributedString(string: "Request an appointment", attributes: attrs)
        }
        return NSAttributedString(string: "", attributes: nil)
    }
    
    func emptyDataSet(_ scrollView: UIScrollView, didTap button: UIButton) {
        if GenericHelper.shared.hasPermissionTo(feature: .requestAppointments) {
            print("Request an appointment")
            requestAppt(UIBarButtonItem())
        }
    }
}

extension ApptViewController {
    
    func promptForBiometricAuthentication() {
        let localAuthorization = BiometricManager.shared.biometricCapable()
        let hasSuccessfullyLoggedIn = UserDefaults.standard.bool(forKey: MyCTCAConstants.UserPrefs.successfullyLoggedIn)
        let hasSetBiometricPreferences = UserDefaults.standard.bool(forKey: MyCTCAConstants.UserPrefs.biometricPreferenceSet)
        if (localAuthorization == true && hasSuccessfullyLoggedIn == true && hasSetBiometricPreferences == false) {
            print("Show BIOMETRIC ALERT")
            let alertTitle = "Please Note"
            let alertMessage = BiometricManager.shared.getBiometricEnablePrompt(context: nil)
            
            let biometryAlert = UIAlertController(title: alertTitle,
                                                  message: alertMessage,
                                                  preferredStyle:.alert)
            
            let okAction = UIAlertAction(title: "OK", style: .default) { (action) in
                self.enableBiometrics()
            }
            biometryAlert.addAction(okAction)
            
            let learnAction = UIAlertAction(title: "Learn More", style: .default) { (action) in
                print("LEARN MORE")
                self.learnMoreAboutTouchId()
            }
            biometryAlert.addAction(learnAction)
            
            let cancelAction = UIAlertAction(title: "Cancel", style: .cancel) { (action) in
                print("Cancel AlertController")
                self.disableBiometrics()
            }
            biometryAlert.addAction(cancelAction)
            biometryAlert.view.tintColor  = MyCTCAColor.ctcaGreen.color
            self.present(biometryAlert,
                         animated: true,
                         completion: nil)
            
        }
    }
    
    func enableBiometrics() {
        BiometricManager.shared.setBiometricPreference(enabled: true)
        BiometricManager.shared.storeLoginParameters();
        
        let laContext = LAContext()
        let alertConfirmation = BiometricManager.shared.getBiometricEnabledConfirmation(context: laContext)
        
        let biometryAlert = UIAlertController(title: alertConfirmation.title,
                                              message: alertConfirmation.msg,
                                              preferredStyle:.alert)
        
        let okAction = UIAlertAction(title: "OK", style: .default) { (action) in
            self.dismiss(animated: true, completion: nil)
        }
        biometryAlert.addAction(okAction)
        biometryAlert.view.tintColor  = MyCTCAColor.ctcaGreen.color
        self.present(biometryAlert,
                     animated: true,
                     completion: nil)
    }
    
    func disableBiometrics() {
        BiometricManager.shared.setBiometricPreference(enabled: false)
        
        let laContext = LAContext()
        let alertConfirmation = BiometricManager.shared.getBiometricDisabledConfirmation(context: laContext)
        
        let biometryAlert = UIAlertController(title: alertConfirmation.title,
                                              message: alertConfirmation.msg,
                                              preferredStyle:.alert)
        
        let okAction = UIAlertAction(title: "OK", style: .default) { (action) in
            self.dismiss(animated: true, completion: nil)
        }
        biometryAlert.addAction(okAction)
        
        self.present(biometryAlert,
                     animated: true,
                     completion: nil)
    }
    
    func learnMoreAboutTouchId() {
        
        let storyboard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let aboutBiometricsVC: AboutBiometricsViewController = storyboard.instantiateViewController(withIdentifier: "AboutBiometricsViewController") as! AboutBiometricsViewController
        
        let navCtrl: UINavigationController = UINavigationController()
        navCtrl.viewControllers = [aboutBiometricsVC]
        
        aboutBiometricsVC.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .stop, target: self, action: #selector(self.stopButtonAction))
        aboutBiometricsVC.navigationItem.leftBarButtonItem?.tintColor = MyCTCAColor.ctcaGreen.color
        
        self.present(navCtrl, animated: true, completion: nil)
    }
    
    @objc func stopButtonAction() {
        dismiss(animated: true, completion: nil)
    }
}

extension ApptViewController: ApptNextTableViewProtocol {

    func joinTelehealthMeetingTapped(url: String) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.ACTION_TELEHEALTH_JON_NOW_TAP,
                                                     customInfo:nil,
                                                     appointment: self.nextAppointment)
        
        if !TelehealthServiceManager.shared.verifyAccessPermissions() {
            return
        }
        
        //Commented as per DPE - 2475
        //if let appt = self.nextAppointment, TelehealthServiceManager.shared.canMeetingStart(appt: appt) == false {
        //    return
        //}
        
        showActivityIndicator(view: view, message: "Loading...")
        
        AppointmentsManager.shared.fetchTelehealthAccessToken() {
            tokenString, status in
            
            self.fadeOutActivityIndicator()
            
            if status == .SUCCESS, tokenString != nil {
                self.showTelehealthView(token: tokenString!)
            } else {
                //fallback error
                AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.EXCEPTION_REST_API, customInfo: ["APIName" : "https://apim.ctca-hope.com/test/telehealth/api/accesstoken", "errorCode": "\(PARSE_ERROR_CODE)"], appointment: self.nextAppointment)
                ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: self.nextAppointment)
            }
        }
    }
    
    func showTelehealthView(token:String) {
        DispatchQueue.main.async { [self] in

            if let appointment = self.nextAppointment {
                let preTelehealthCallVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PreCallViewController") as! PreCallViewController
                
                var telehealthDetail = TelehealthDetail(withValue: appointment)
                telehealthDetail.accessToken = token
                
                preTelehealthCallVC.telehealthDetail = telehealthDetail
                
                self.show(preTelehealthCallVC, sender: nil)
            }
        }
    }
    
    //Delegate from cell to display Telhealth url
    func showTeleHealthInfoTapped(url: String) {
        if !url.isEmpty {
            GenericHelper.shared.openInSafari (path: url)
        }
    }
}

extension ApptViewController: AppointmentsTableViewCellProtocol {

    func didSelectApptCell(appointment: Appointment) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_DETAIL_TAP)
        self.selectedAppointment = appointment
        performSegue(withIdentifier: APPT_DET_SEGUE, sender: nil)
    }
    
    func didSelectJoinNow(appointment:Appointment) {
        if appointment.telehealthMeetingJoinUrl.count > 0 { //Disply Join Now
            joinTelehealthMeetingTapped(url: appointment.telehealthMeetingJoinUrl)
        } else if appointment.teleHealthUrl.count > 0 { //Disply Join Now
            joinTelehealthMeetingTapped(url: appointment.teleHealthUrl)
        } else if appointment.telehealthInfoUrl.count > 0 { //Disply setup guide
            showTeleHealthInfoTapped(url: appointment.telehealthInfoUrl)
        } else {
            ErrorManager.shared.showDefaultTelehealthError(onView: self, appointment: appointment)
        }
    }
}
