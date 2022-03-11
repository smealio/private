//
//  ApptDetailViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 12/12/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptDetailViewController: UIViewController, CTCAViewControllerProtocol {

    @IBOutlet weak var tableView: UITableView!
    
    var appointment: Appointment = Appointment()
    
    let cellHeaderHeight: CGFloat = 44.0
    let cellDefaultHeight: CGFloat = 55.0
    let cellMainHeight: CGFloat = 125.0
    let cellTeleHealthHeight: CGFloat = 100.0
    
    let APPT_MAP_SEGUE = "ApptMapSegue"
    let APPT_CHANGE_SEGUE = "ApptChangeSegue"
    
    var apptRequestType: ApptRequestType?
    
    var hasCancelApptPermission: Bool = false
    var hasRescheduleApptPermission: Bool = false
    
    let appointmentaManager = AppointmentsManager.shared
    var canRescheduleAppt = false
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        self.tableView.estimatedRowHeight = cellDefaultHeight
        self.tableView.rowHeight = UITableView.automaticDimension
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_APPOINTMENTS_DETAILS_VIEW)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        // Check Permissions
        let currentUserId = AppSessionManager.shared.currentUser.currentUserId
        let userProfile = AppSessionManager.shared.currentUser.userProfile!
        
        hasRescheduleApptPermission = userProfile.userCan(UserPermissionType.rescheduleAppointment, viewerId: currentUserId)
        hasCancelApptPermission = userProfile.userCan(UserPermissionType.cancelAppointment, viewerId: currentUserId)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func rescheduleAppt() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_RESCHEDULE_TAP)

        AppointmentsManager.shared.prepareAppointmentRequestFor(requestType: .reschedule, appointment:appointment)

        let storyboard = UIStoryboard(name: "Appointments", bundle: nil)
        if #available(iOS 13.0, *) {
            let appointmentReqVC = storyboard.instantiateViewController(withIdentifier: "NewApptRequestViewController") as! NewApptRequestViewController
            
            appointmentReqVC.title = "Reschedule Request"
            appointmentReqVC.requestType = .reschedule
            let navController = UINavigationController(rootViewController: appointmentReqVC)
            self.present(navController, animated: true, completion: nil)
        }
    }
    
    func cancelAppt() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_CANCEL_TAP)

        AppointmentsManager.shared.prepareAppointmentRequestFor(requestType: .cancel, appointment:appointment)
        
        let storyboard = UIStoryboard(name: "Appointments", bundle: nil)
        if #available(iOS 13.0, *) {
            let appointmentReqVC = storyboard.instantiateViewController(withIdentifier: "NewApptRequestViewController") as! NewApptRequestViewController

            appointmentReqVC.title = "Cancellation Request"
            appointmentReqVC.requestType = .cancel
            let navController = UINavigationController(rootViewController: appointmentReqVC)
            self.present(navController, animated: true, completion: nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        print("PREPARE REQUEST APPOINTMENT CHANGE")
        if segue.identifier == APPT_CHANGE_SEGUE {
            let destinationNavigationController = segue.destination as! UINavigationController
            if let targetController = destinationNavigationController.topViewController as? ApptChangeViewController {
                targetController.appointment = self.appointment
                if (self.apptRequestType != nil) {
                    targetController.apptRequestType = self.apptRequestType!
                } else {
                    targetController.apptRequestType = ApptRequestType.reschedule
                }
            }
        }
    }
    
    func shareThisAppt() {
        self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.downloadApptText)
        
        appointmentaManager.downloadAppointment(id: appointment.appointmentId) {
            status in
            
            self.dismissActivityIndicator()
            
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_PDF_SHARE_TAP)

            if status == .FAILED {
                //display error
                ErrorManager.shared.showServerError(error: self.appointmentaManager.getLastServerError(), onView: self)
            }
        }
    }
}

// MARK: Table View Data Source

extension ApptDetailViewController: UITableViewDataSource {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if appointment.isUpcoming {
            canRescheduleAppt = appointmentaManager.canRescheduleAppt(startDateInLocalTZ: appointment.startDateInLocalTZ)
            if !canRescheduleAppt {
                return 6
            }
            return 5
        }
        return 4
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
            if (indexPath.row == 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailMainCell") as! ApptDetailMainTableViewCell
                cell.prepareView(appointment)
                cell.delegate = self
                return cell
            } else if (indexPath.row == 1) {
                var instructions = "None"
                if !appointment.patientInstructions.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                    instructions = appointment.patientInstructions
                }
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailTextCell") as! ApptDetailTextTableViewCell
                cell.prepareView(body: instructions)
                return cell
            } else if (indexPath.row == 2) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailNotesTableViewCell") as! ApptDetailNotesTableViewCell
                cell.prepareView(appointment:appointment)
                return cell
            } else if (indexPath.row == 3) {
                if appointment.isUpcoming {
                    let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailsActionTableViewCell") as! ApptDetailsActionTableViewCell
                    if hasCancelApptPermission {
                        cell.cancelAction = cancelAppt
                    }
                    if hasRescheduleApptPermission {
                        cell.rescheduleAction = rescheduleAppt
                    }
                    cell.shareAction = shareThisAppt
                    cell.configCell(isUpcoming: appointment.isUpcoming, canRescheduleAppt: canRescheduleAppt)
                    return cell
                } else { //no share option for past appointments
                    let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailPhoneCell") as! ApptDetailPhoneTableViewCell
                    cell.prepareView(appointment)
                    return cell
                }
            } else if (indexPath.row == 4) {
                if appointment.isUpcoming && !canRescheduleAppt {
                    let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailsCallTechTableViewCell") as! ApptDetailsCallTechTableViewCell
                    cell.config(schedulingPhone: appointment.facility.schedulingPhone ?? "")
                    return cell
                } else {
                    let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailPhoneCell") as! ApptDetailPhoneTableViewCell
                    cell.prepareView(appointment)
                    return cell
                }
            } else if (indexPath.row == 5) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "apptDetailPhoneCell") as! ApptDetailPhoneTableViewCell
                cell.prepareView(appointment)
                return cell
            }
        let cell = UITableViewCell()
        return cell
    }
}

extension ApptDetailViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        if (indexPath.section == 1) {
            if (indexPath.row == 5) {
                GenericHelper.shared.openFacAddressInMap(facility: appointment.facility)
            } else if indexPath.row >= 1 && indexPath.row < 5 {
                var telNo: String = ""
                
                switch (indexPath.row) {
                case 1:
                    telNo = appointment.facility.mainPhone ?? ""
                case 2:
                    telNo = appointment.facility.schedulingPhone ?? ""
                case 3:
                    telNo = appointment.facility.accommodationsPhone ?? ""
                case 4:
                    telNo = appointment.facility.transportationPhone ?? ""
                default:
                    telNo = appointment.facility.mainPhone ?? ""
                }

                GenericHelper.shared.tryToCall(telNo: telNo, parentVC: nil)
            }
        }
    }
}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
	return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}

extension ApptDetailViewController: ApptDetailTeleHealthTableViewProtocol {

    //Delegate from cell to display Telhealth url
    func showTeleHealthInfoTapped(url: String) {
        if !url.isEmpty {
            GenericHelper.shared.openInSafari (path: url)
        }
    }
    
    func joinTelehealthMeetingTapped(url: String) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        if !TelehealthServiceManager.shared.verifyAccessPermissions() {
            return
        }
        
        //Commented as per DPE - 2475
//        if TelehealthServiceManager.shared.canMeetingStart(appt: self.appointment) == false {
//            return
//        }
            
        showActivityIndicator(view: view, message: "Loading...")
                
        AppointmentsManager.shared.fetchTelehealthAccessToken() {
            tokenString, status in
            
            self.fadeOutActivityIndicator()
            
            if status == .SUCCESS, tokenString != nil {
                self.showTelehealthView(token: tokenString!)
            } else {
                //fallback error
                AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.EXCEPTION_REST_API, customInfo:  ["APIName" : "https://apim.ctca-hope.com/test/telehealth/api/accesstoken", "errorCode": "\(PARSE_ERROR_CODE)"], appointment: self.appointment)
                                                            
                ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: self.appointment)
            }
        }
    }
    
    func showTelehealthView(token:String) {
        DispatchQueue.main.async {
            let preTelehealthCallVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PreCallViewController") as! PreCallViewController
            
            var telehealthDetail = TelehealthDetail(withValue: self.appointment)
            telehealthDetail.accessToken = token
            preTelehealthCallVC.telehealthDetail = telehealthDetail
            
            self.show(preTelehealthCallVC, sender: nil)
        }
    }
    
}
