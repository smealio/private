//
//  AboutMyCTCAViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/19/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit
import SafariServices

class AboutMyCTCAViewController: UIViewController, SFSafariViewControllerDelegate {

    @IBOutlet weak var tableView: UITableView!
    
    let aboutCellHeight: CGFloat = 90.0
    let legalCellHeight: CGFloat = 45.0
    let headerHeight: CGFloat = 44.0
    
    let ABOUT_CERT_SEGUE = "AboutCertSegue"
    
    let TERMS_AND_CONDITIONS = "Terms of Use"
    let PRIVACY_POLICY = "Privacy Practices"
    let CERTIFICATIONS = "Certifications"
    
    var legalContent: [String] = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Set Title
        self.title = "About myCTCA"
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        self.legalContent = [TERMS_AND_CONDITIONS, PRIVACY_POLICY, CERTIFICATIONS]
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ABOUT_MYCTCA_VIEW)
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

// MARK: Table View Data Source

extension AboutMyCTCAViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if (section == 1) {
            return self.legalContent.count;
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (indexPath.section == 1) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "AboutMyCTCALegalTableViewCell") as! AboutMyCTCALegalTableViewCell
            cell.legalLabel.text = self.legalContent[indexPath.row]
            return cell
        }
        let cell = tableView.dequeueReusableCell(withIdentifier: "AboutMyCTCATableViewCell") as! AboutMyCTCATableViewCell
        cell.myCTCALabel.text = "myCTCA"
        cell.copyrightLabel.text = "Copyright © \(getCurrentYear()) IPB"
        if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
            cell.versionLabel.text = "Version \(version)"
        } else {
            cell.versionLabel.text = ""
        }
        return cell
    }
    
    func getCurrentYear() -> String {
        let date = Date()
        let calendar = Calendar.current
        
        let year = calendar.component(.year, from: date)
        return String(year)
    }
}

extension AboutMyCTCAViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        return self.headerHeight
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let cell = AboutSectionCell()
        
        var sectionText = ""
        if (section == 0) {
            sectionText = "VERSION"
        }
        if (section == 1) {
            sectionText = "LEGAL"
        }
        cell.prepareView(sectionText)
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (indexPath.section == 0) {
            return aboutCellHeight
        }
        return legalCellHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        if (indexPath.section == 1) {
            let selectContent = self.legalContent[indexPath.row]
            
            if (selectContent == TERMS_AND_CONDITIONS) {
                if let url = AuthenticationAPIRouter.openTermsOfUseLink.asUrl() {
                    GenericHelper.shared.launchSafariViewController(withUrl: url, forView: self)
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ABOUT_MYCTCA_TERMS_OF_USE_TAP)
                }
            }
            if (selectContent == PRIVACY_POLICY) {
                if let url = AuthenticationAPIRouter.openPrivacyPracticeLink.asUrl() {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ABOUT_MYCTCA_PRIVACY_POLICY_TAP)
                    GenericHelper.shared.launchSafariViewController(withUrl: url, forView: self)
                }
            }
            if (selectContent == CERTIFICATIONS) {
                performSegue(withIdentifier: ABOUT_CERT_SEGUE, sender: nil)
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ABOUT_MYCTCA_CERTIFICATION_TAP)

            }
        }
    }
    
}

