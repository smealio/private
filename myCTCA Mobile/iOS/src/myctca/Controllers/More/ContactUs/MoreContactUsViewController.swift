//
//  MoreContactUsViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreContactUsViewController: CTCABaseViewController {

    @IBOutlet weak var tableView: UITableView!
    
    let cellDefaultHeight:CGFloat = 55.0
    let cellHeaderHeight: CGFloat = 44.0
    
    var isPatientUser = (AppSessionManager.shared.getUserType() == .CARE_GIVER) ? false : true
    let authenticationManager = AuthenticationManager()

    override func viewDidLoad() {
        super.viewDidLoad()

        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        self.tableView.estimatedRowHeight = cellDefaultHeight
        self.tableView.rowHeight = UITableView.automaticDimension
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CONTACT_US_VIEW)
        self.title = "Contact Us"
        
        loadViewData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
         if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
             return
         }
        
        if !isPatientUser && AppSessionManager.shared.currentUser.allFacilitesNamesList.count == 0 {
            fetchFacList()
        }
    }
    
    func fetchFacList() {
        showActivityIndicator(view: self.view, message:"Loading…")
                
        authenticationManager.fetchAllFacilites() {
            status in
            self.fadeOutActivityIndicator()
            if status == .FAILED {
                GenericHelper.shared.showAlert(withtitle: NSLocalizedString("NetworkingDataProblemTitle", comment: "Title on alert pop-up when there is an error retrieving information."),
                                               andMessage: NSLocalizedString("NetworkingDataProblem", comment: "Error message displayed when there is a problem retrieving data from CTCA services."),
                                               onView: self)
                
            } else {
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            }
        }
    }
}

// MARK: Table View Data Source

extension MoreContactUsViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if (section == 1) {
            if isPatientUser {
                return 7
            } else {
                //care giver
                return AppSessionManager.shared.currentUser.allFacilitesList.count
            }
        }
        if (section == 2) {
            return 2
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (indexPath.section == 0) {
            if (indexPath.row == 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsDisclaimerCell") as! MoreContactUsDisclaimerTableViewCell
                cell.prepareView()
                return cell
            }
        }
        if (indexPath.section == 1) {
            if isPatientUser {
                if let facility = AppSessionManager.shared.currentUser.primaryFacility {
                    if (indexPath.row == 0) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsFacilityCell") as! MoreContactUsFacilityTableViewCell
                        cell.prepareView(facility.displayName)
                        cell.selectionStyle = .none
                        return cell
                    }
                    if (indexPath.row == 1) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsPhoneCell") as! MoreContactUsPhoneTableViewCell
                        cell.prepareView("Call main phone number:", phoneText: facility.mainPhone ?? "")
                        return cell
                    }
                    if (indexPath.row == 2) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsPhoneCell") as! MoreContactUsPhoneTableViewCell
                        cell.prepareView("Call Scheduling:", phoneText: facility.schedulingPhone ?? "")
                        return cell
                    }
                    if (indexPath.row == 3) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsPhoneCell") as! MoreContactUsPhoneTableViewCell
                        cell.prepareView("Call Accommodations:", phoneText: facility.accommodationsPhone ?? "")
                        return cell
                    }
                    if (indexPath.row == 4) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsPhoneCell") as! MoreContactUsPhoneTableViewCell
                        cell.prepareView("Call Transportation:", phoneText: facility.transportationPhone ?? "")
                        return cell
                    }
                    if (indexPath.row == 5) {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsPhoneCell") as! MoreContactUsPhoneTableViewCell
                        var ph = ""
                        if let phone = facility.himROIPhone {
                            ph = phone
                        }
                        cell.prepareView("Call for Release of Information help:", phoneText: ph)

                        return cell
                    }
                    if (indexPath.row == 6) {
                        
                        var address = facility.address.address1 ?? ""
                        if (facility.address.address2 != nil) {
                            address += "\n\(String(describing: facility.address.address2 ?? ""))"
                        }
                        address += "\n\(facility.address.city ?? ""), \(facility.address.state ?? "") \(facility.address.postalCode ?? "")"
                        
                        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsMapCell") as! MoreContactUsMapTableViewCell
                        cell.prepareView(address)
                        return cell
                    }
                } else {
                    let cell = UITableViewCell()
                    return cell
                }
            } else {
                //care giver
                let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsPhoneCell") as! MoreContactUsPhoneTableViewCell
                
                if AppSessionManager.shared.currentUser.allFacilitesList.count > 0  {
                    if indexPath.row < AppSessionManager.shared.currentUser.allFacilitesList.count {
                        let fac = AppSessionManager.shared.currentUser.allFacilitesList[indexPath.row]
                        cell.prepareView(fac.displayName, phoneText: fac.mainPhone ?? "")
                    }
                } else {
                    cell.prepareView("", phoneText: "")
                }
                return cell
            }
        }
        if (indexPath.section == 2) {
            if (indexPath.row == 0) {
                let number = AppInfoManager.shared.appInfo?.techSupportNumber ?? ""

                let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsPhoneCell") as! MoreContactUsPhoneTableViewCell
                cell.prepareView("Call", phoneText: number)
                cell.selectionStyle = .gray
                return cell
            }
            if (indexPath.row == 1) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "ContactUsMessageCell") as! MoreContactUsMessageTableViewCell
                let mailImg: UIImage = #imageLiteral(resourceName: "Mail")
                cell.prepareView("Send message", image: mailImg)
                return cell
            }
        }
        let cell = UITableViewCell()
        return cell
    }
}

extension MoreContactUsViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return UITableView.automaticDimension
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        var headerHeight: CGFloat
        
        if (section == 0) {
            // hide the header
            headerHeight = CGFloat.leastNonzeroMagnitude
        } else {
            headerHeight = cellHeaderHeight
        }
        
        return headerHeight
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let cell = MoreContactUsHeaderTableViewCell()
        
        if (section == 1) {
            cell.prepareView("CONTACT YOUR TREATMENT FACILITY")
        }
        if (section == 2) {
            cell.prepareView("CONTACT TECHNICAL SUPPORT")
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        if (indexPath.section == 1) {
            if let facility = AppSessionManager.shared.currentUser.primaryFacility, isPatientUser {
                if (indexPath.row == 6) {
                    GenericHelper.shared.openFacAddressInMap(facility: facility)
                } else if indexPath.row >= 1 && indexPath.row < 6 {
                    var telNo: String = ""
                    switch (indexPath.row) {
                    case 1:
                        telNo = facility.mainPhone ?? ""
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CALL_MAIN_PHONE_TAP)
                        
                    case 2:
                        telNo = facility.schedulingPhone ?? ""
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CALL_SCHEDULING_TAP)
                        
                    case 3:
                        telNo = facility.accommodationsPhone ?? ""
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CALL_ACCOMODATIONS_TAP)
                        
                    case 4:
                        telNo = facility.transportationPhone ?? ""
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CALL_TRANSPORTATIONS_TAP)
                        
                    case 5:
                        var ROIPhone = ""
                        if let phone = facility.himROIPhone {
                            ROIPhone = phone
                        }
                        telNo = ROIPhone
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CALL_ROI_TAP)
                        
                    default:
                        telNo = facility.mainPhone ?? ""
                    }
                    GenericHelper.shared.tryToCall(telNo: telNo, parentVC: self)
                }
            } else if !isPatientUser {
                if indexPath.row < AppSessionManager.shared.currentUser.allFacilitesList.count {
                    let fac = AppSessionManager.shared.currentUser.allFacilitesList[indexPath.row]
                    GenericHelper.shared.tryToCall(telNo: fac.mainPhone ?? "", parentVC: self)
                }
            }
        }
        if (indexPath.section == 2) {
            if (indexPath.row == 0) {
                let telNo = AppInfoManager.shared.appInfo?.techSupportNumber ?? ""

                GenericHelper.shared.tryToCall(telNo: telNo, parentVC: self)
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CALL_TECH_SUPPORT_TAP)
            }
            if (indexPath.row == 1) {
                sendMessage(self)
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CONTACT_US_SEND_MESSAGE_TAP)
            }
        }
    }
    
    /**
     Call SendMessageViewController as modal
     */
    func sendMessage(_ sender: AnyObject) {
        print("ContactSupportComposer sendMessage")
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let sendMessageVC = storyboard.instantiateViewController(withIdentifier: "SendMessageController") as! UINavigationController
        
        self.present(sendMessageVC, animated: true, completion: nil)
    }
    
}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
	return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}
