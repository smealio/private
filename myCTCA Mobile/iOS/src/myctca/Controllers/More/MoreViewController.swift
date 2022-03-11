//
//  MoreViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 11/10/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit
import LocalAuthentication
import SafariServices

class MoreViewController: CTCABaseViewController, SFSafariViewControllerDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    
    let MORE_MEDDOC_SEGUE: String = "MoreMedDocSegue"
    let MORE_HEALTHHISTORY_SEGUE: String = "MoreHealthHistorySegue"
    let MORE_ROI_SEGUE: String = "MoreROISegue"
    let MORE_BILLPAY_SEGUE: String = "MoreBillPaySegue"
    let MORE_FORMSLIB_SEGUE: String = "MoreFormsLibrarySegue"
    
    let MORE_ACTIVITY_LOG_SEGUE: String = "MoreActivityLogSegue"
    
    let MORE_CONTACT_US_SEGUE: String = "MoreContactUsSegue"
    let MORE_BIOMETRIC_SEGUE: String = "MoreBiometricSegue"
    let MORE_ABOUT_SEGUE: String = "MoreAboutSegue"
    let MORE_CHANGE_PATIENT_SEGUE: String = "MoreChangePatientSegue"
    
    let cellHeight:CGFloat = 50.0
    let headerHeight:CGFloat = 44.0
    
    // (ImageName, TextDisplayed, NeedsDisclosureIndicator)
    var sectionData = [[(String, String, Int)]]()
    
    var section0 = [(String, String, Int)]()
    var section1 = [(String, String, Int)]()
    var section2 = [(String, String, Int)]()
    
    var actionSheet: UIAlertController?
    
    // Check Permissions
    var hasViewMedicalDocumentsPermission: Bool = false
    var hasViewHealthHistoryPermission: Bool = false
    var hasViewFormsLibraryPermission: Bool = false
    var hasViewBillPayPermissions: Bool = false
    var hasManageNotificationsPermission: Bool = false
    var hasViewAcctPermission: Bool = false
    var hasViewActivityLogPermission: Bool = false
    var fromUniversalLink: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Set Title
        self.title = "More"
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_TAP)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        // Check Permissions
        let currentUserId = AppSessionManager.shared.currentUser.currentUserId
        let userProfile = AppSessionManager.shared.currentUser.userProfile!
        
        hasViewMedicalDocumentsPermission = userProfile.userCan(UserPermissionType.viewMedicalDocuments, viewerId: currentUserId)
        hasViewHealthHistoryPermission = userProfile.userCan(UserPermissionType.viewHealthHistory, viewerId: currentUserId)
        hasViewFormsLibraryPermission = userProfile.userCan(UserPermissionType.viewFormsLibrary, viewerId: currentUserId)
        hasViewBillPayPermissions = userProfile.userCan(UserPermissionType.viewBillPay, viewerId: currentUserId)
        hasManageNotificationsPermission = userProfile.userCan(UserPermissionType.manageNotifications, viewerId: currentUserId)
        hasViewAcctPermission = userProfile.userCan(UserPermissionType.viewMyAccount, viewerId: currentUserId)
        hasViewActivityLogPermission = userProfile.userCan(UserPermissionType.viewUserLogs, viewerId: currentUserId)
        
        if (sectionData.count <= 0) {
            buildSectionData()
        }
    }
    
    func buildSectionData() {
        // Section 0
        buildDocumentSectionData()
        //My Resources will be available for both user types
        if (section0.count > 0) {
            sectionData.append(section0)
        }
        
        // Section 1
        buildMyCTCAIdSectionData()
        sectionData.append(section1)
        
        // Section 2
        buildHelpAndSettingsSectionData()
        sectionData.append(section2)
    }
    
    func buildDocumentSectionData() {
        if AppSessionManager.shared.getUserType() != .CARE_GIVER {
            section0.append( ("filetext", "Medical Documents", 1) )
            section0.append( ("heart", "Health History", 1) )
        
            section0.append( ("patient-reported", "Patient Reported", 1) )
            section0.append( ("checksquare", "Forms Library", 1) )
        }
        
        if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.viewExternalLink) {
            section0.append( ("my-resources", "My Resources", 1) )
        }
        if (hasViewBillPayPermissions == true) {
            section0.append( ("billpay", "Bill Pay", 1) )
        }
    }
    
    func buildMyCTCAIdSectionData() {
        if AppSessionManager.shared.getUserType() != .CARE_GIVER {
            if hasViewAcctPermission {
                section1.append( ("user", "User Profile", 1) )
            }
        } else {
            section1.append( ("user", "User Profile", 1) )
        }
        if hasViewActivityLogPermission {
            section1.append( ("list", "View Activity Log", 1) )
        }
        section1.append( ("signout", "Sign out", 0) )
    }
    
    func buildHelpAndSettingsSectionData() {
        
        section2.append( ("phone", "Contact Us", 1) )
        
        let laContext = LAContext()
        if (BiometricManager.shared.biometricCapable(context: laContext)) {
            section2.append( ("biometric", "Touch Id Preferences", 1) )
        }
        section2.append( ("info", "About myCTCA", 1) )
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == MORE_FORMSLIB_SEGUE {
            if let controller = segue.destination as? MoreFromsLibraryViewController {
                controller.fromUniversalLink = fromUniversalLink
            }
        }
    }
}

// MARK: Table View Data Source

extension MoreViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return sectionData.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sectionData[section].count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "moreTableViewCell") as! MoreTableViewCell
        
        let dataArray = sectionData[indexPath.section]
        let data = dataArray[indexPath.row]
        
        if (data.0 != "biometric") {
            cell.moreImageView.image = UIImage(named: data.0)
            cell.moreLabel.text = data.1
        } else {
            let laContext: LAContext = LAContext()
            cell.moreImageView.image = BiometricManager.shared.getBiometricImage(context: laContext)
            cell.moreLabel.text = BiometricManager.shared.getBiometricName(context: laContext)
        }
        
        if (cellNeedsDisclosure(indexPath, data: data) == true) {
            cell.accessoryType = .disclosureIndicator;
        }
        
        return cell
    }
    
    func cellNeedsDisclosure(_ indexPath: IndexPath, data: (String, String, Int) ) -> Bool{
        
        if (data.2 == 0) {
            return false
        }
        return true
    }
}

extension MoreViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return cellHeight
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        let heightForHeader: CGFloat
        
        // Some permissions allow for first section visible
        switch section {
        case 0:
            // hide the header
            heightForHeader = CGFloat.leastNonzeroMagnitude
        default:
            heightForHeader = headerHeight
        }

        return heightForHeader
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if section > 0 {
            let cell = MoreSectionCell()
            var sectionText = ""
            
            if section == 1 {
                sectionText = "MY ACCOUNT"
            } else if section == 2 {
                sectionText = "HELP AND SETTINGS"
            }
            cell.prepareView(sectionText)
            return cell
        }
        return nil
    }
    
    func canSeeMedDocs() -> Bool {
        return GenericHelper.shared.hasPermissionTo(feature: .viewCarePlan) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewCCDADocuments) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewClinicalDocuments) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewRadiationDocuments) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewImagingDocuments) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewIntegrativeDocuments)
    }
    
    func canSeeHealthHistory() -> Bool {
        return GenericHelper.shared.hasPermissionTo(feature: .viewVitalSigns) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewPrescriptions) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewAllergies) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewImmunizations) ||
               GenericHelper.shared.hasPermissionTo(feature: .viewHealthIssues)
    }
    
    func canSeeFormsLibrary() -> Bool {
        return GenericHelper.shared.hasPermissionTo(feature: .submitROIForm) ||
               GenericHelper.shared.hasPermissionTo(feature: .submitANNCForm)
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let dataArray = sectionData[indexPath.section]
        let data = dataArray[indexPath.row]
        
        switch data.0 {
        // Main Section
        case "filetext":
            if canSeeMedDocs() {
                doMedDocs()
            } else {
                GenericHelper.shared.showNoAccessMessage(view: self)
            }
        case "heart":
            if canSeeHealthHistory() {
                doHealthHistory()
            } else {
                GenericHelper.shared.showNoAccessMessage(view: self)
            }
        case "checksquare":
            if canSeeFormsLibrary() {
                doFormsLib()
            } else {
                GenericHelper.shared.showNoAccessMessage(view: self)
            }
        case "patient-reported":
            if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.viewPatientReportedDoc) {
                MyCTCAListViewManager().showPatientReported(parentVC: self)
            } else {
                GenericHelper.shared.showNoAccessMessage(view: self)
            }
        case "billpay":
            doBillPay()
        case "my-resources":
            MyCTCAListViewManager().showMyResources(parentVC: self)
            
        // MyCTCA Section
        case "user":
            doChangeCTCAId()
        case "list":
            doActivityLogs()
        case "signout":
            let cell = self.tableView.cellForRow(at: indexPath) as! MoreTableViewCell
            doSignOut(cell)
            
        // Help and Settings Section
        case "phone":
            doContactUs()
        case "biometric":
            doAboutBiometrics()
        case "info":
            doAboutMyCTCA()
        default:
            break
        }
    }
    
    // Main Section
    func doMedDocs() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_MED_DOCS_TAP)
        performSegue(withIdentifier: MORE_MEDDOC_SEGUE, sender: nil)
    }
    func doHealthHistory() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_HEALTH_HISTORY_TAP)
        
        performSegue(withIdentifier: MORE_HEALTHHISTORY_SEGUE, sender: nil)
    }
    func doFormsLib(isFromUL:Bool = false) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_FORMS_LIBRARY_TAP)
        fromUniversalLink = isFromUL
        performSegue(withIdentifier: MORE_FORMSLIB_SEGUE, sender: nil)
    }
    func doBillPay() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_BILL_PAY_TAP)
        
        performSegue(withIdentifier: MORE_BILLPAY_SEGUE, sender: nil)
    }
    
    // MyCTCA ID Section
    func doChangeCTCAId() {
        if let url  = AuthenticationAPIRouter.openUserProfile.asUrl() {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_USER_PROFILE_TAP)

            let svc = SFSafariViewController(url: url)
            svc.delegate = self
            svc.preferredBarTintColor = MyCTCAColor.tableHeaderGrey.color
            svc.preferredControlTintColor = MyCTCAColor.ctcaGreen.color
            self.present(svc, animated: true, completion: nil)
        }
    }
    
    func doChangePassword() {
        if let url = AuthenticationAPIRouter.openResetPasswordLink.asUrl() {
            let svc = SFSafariViewController(url: url)
            svc.delegate = self
            svc.preferredBarTintColor = MyCTCAColor.tableHeaderGrey.color
            svc.preferredControlTintColor = MyCTCAColor.ctcaGreen.color
            self.present(svc, animated: true, completion: nil)
        }
    }
    
    func doActivityLogs() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_ACTIVITY_LOGS_TAP)
        
        performSegue(withIdentifier: MORE_ACTIVITY_LOG_SEGUE, sender: nil)
    }
    
    func doSignOut(_ signOutCell: MoreTableViewCell) {
        GenericHelper.shared.doSignOut(onView: self, sourceView:signOutCell)
    }
    
    // Help And Settings Section
    func doContactUs() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_CONTACT_US_TAP)
        
        performSegue(withIdentifier: MORE_CONTACT_US_SEGUE, sender: nil)
    }
    
    func doAboutBiometrics() {
        performSegue(withIdentifier: MORE_BIOMETRIC_SEGUE, sender: nil)
    }
    
    func doAboutMyCTCA() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_ABOUT_MY_CTCA_TAP)
        
        performSegue(withIdentifier: MORE_ABOUT_SEGUE, sender: nil)
    }
}
