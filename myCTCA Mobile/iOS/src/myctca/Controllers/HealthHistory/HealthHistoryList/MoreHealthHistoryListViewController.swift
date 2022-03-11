//
//  MoreHealthHistoryListViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreHealthHistoryListViewController: UIViewController, CTCAViewControllerProtocol {

    @IBOutlet weak var tableView: UITableView!
    
    let cellDefaultHeight: CGFloat = 80.0
    let headerHeight: CGFloat = 44.0
    let cellDetailHeight: CGFloat = 95.0
    
    var selectedCell: IndexPath?
    
    var healthHistoryType:HealthHistoryType?
    
    let HEALTH_HISTORY_DETAIL_SEGUE: String = "HealthHistoryDetailSegue"
    let PRESCRIPTION_RENEWAL_SEGUE: String = "PrescriptionRenewalSegue"
    
    var refillRequest:[String:Prescription]?
    
    let searchController = UISearchController(searchResultsController: nil)
    var searchControllerShown = false
    
    var downloadBarButton = UIBarButtonItem()
    var searchBarButton = UIBarButtonItem()
    
    var searchText = ""
    var hiddenSections = Set<Int>()
    
    let healthHistoryManager = HealthHistoryManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if (healthHistoryType != nil) {
            self.title = self.healthHistoryType!.rawValue
        }
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        // Set tableview for empty data sets and
        // an empty footer to clear extra cells
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        
        self.prepareView()
        
        switch healthHistoryType {
        case .vitals:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_VITALS_VIEW)
        case .prescriptions:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_PRESCRIPTIONS_VIEW)
        case .allergies:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ALLERGIES_VIEW)
        case .immunizations:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_IMMUNIZATIONS_VIEW)
        case .healthIssues:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_HEALTH_ISSUES_VIEW)
        default:
            break
        }
    }
    
    private func prepareView() {
        
        healthHistoryManager.hostViewController = self
        loadViewData()
    
        //search
        let searchButton = UIButton(type: .system)
        if #available(iOS 13.0, *) {
            searchButton.setImage(UIImage(systemName: "magnifyingglass"), for: .normal)
        } else {
            searchButton.setImage(UIImage(named: "magnifying"), for: .normal)
        }
        searchButton.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
        searchButton.addTarget(self, action: #selector(searchButtonTapped), for: .touchUpInside)
        searchBarButton = UIBarButtonItem(customView: searchButton)
        searchBarButton.target = self
        searchBarButton.isEnabled = false

        //download
        let downloadButton = UIButton(type: .system)
        downloadButton.setImage(UIImage(named: "download_big"), for: .normal)
        downloadButton.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
        downloadButton.addTarget(self, action: #selector(downloadButtonTapped), for: .touchUpInside)
        downloadBarButton = UIBarButtonItem(customView: downloadButton)
        downloadBarButton.target = self
        downloadBarButton.isEnabled = false
        
        self.navigationItem.setRightBarButtonItems([searchBarButton, downloadBarButton], animated: true)
        
        showSearchController()
    }
    
    func showSearchController() {
        searchController.searchResultsUpdater = self
        searchController.obscuresBackgroundDuringPresentation = false
        searchController.searchBar.placeholder = "Search..."

        if #available(iOS 13.0, *) {
            searchController.searchBar.searchTextField.clearButtonMode = .never
            let appearance = UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self])
            appearance.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGreen.color], for: .normal);
        }
        
        searchController.searchBar.delegate = self
        definesPresentationContext = true
    }

    func hideSearchController() {
        searchControllerShown = false
        
        DispatchQueue.main.async {
            self.tableView.tableHeaderView!.quickFadeOut(completion: {
                (finished: Bool) -> Void in
                self.tableView.tableHeaderView = nil
                
                self.searchController.searchBar.endEditing(true)
                self.searchController.searchBar.showsCancelButton = false
                self.searchController.resignFirstResponder()
                
                self.searchController.isActive = false
            })
        }
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        fetchHealthHistoryNew()
    }
    
    private func fetchHealthHistoryNew() {
        var actvityIndicatorMsg = ""
        
        switch healthHistoryType {
        case .vitals:
            actvityIndicatorMsg = ActivityIndicatorMsgs.retriveVitalsText
        case .prescriptions:
            actvityIndicatorMsg = ActivityIndicatorMsgs.retrivePrescriptionsText
        case .allergies:
            actvityIndicatorMsg = ActivityIndicatorMsgs.retriveAllergiesText
        case .immunizations:
            actvityIndicatorMsg = ActivityIndicatorMsgs.retriveImmunizationsText
        case .healthIssues:
            actvityIndicatorMsg = ActivityIndicatorMsgs.retriveHealthIssuesText
        default:
            break
        }
        
        showActivityIndicator(view: self.view, message: actvityIndicatorMsg)
        print("fetchHealthHistory: \(String(describing: healthHistoryType))")
        
        if healthHistoryType == .prescriptions {
            healthHistoryManager.fetchPrescriptions() {
                status in

                if status == .SUCCESS {

                    DispatchQueue.main.async {
                        if let count = self.healthHistoryManager.prescriptions?.count, count > 0 {
                            self.searchBarButton.isEnabled = true
                            self.downloadBarButton.isEnabled = true
                        }

                        self.tableView.reloadData()
                    }
                } else {
                    ErrorManager.shared.showServerError(error: self.healthHistoryManager.getLastServerError(), onView: self)
                }

                self.fadeOutActivityIndicator(completion: nil);

            }
        } else {
            healthHistoryManager.fetchHelathHistory(healthHistoryType: healthHistoryType!) {
                status in
                
                if status == .SUCCESS {
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                        
                        var enableButtons = false
                        
                        switch self.healthHistoryType {
                        case .vitals:
                            if self.healthHistoryManager.vitalsDocs.count > 0  {
                                enableButtons = true
                            }
                        case .allergies:
                            if let count = self.healthHistoryManager.allergies?.count, count > 0 {
                                enableButtons = true
                            }
                        case .immunizations:
                            if let count = self.healthHistoryManager.immunizations?.count, count > 0 {
                                enableButtons = true
                            }
                        case .healthIssues:
                            if let count = self.healthHistoryManager.healthIssues?.count, count > 0 {                enableButtons = true
                            }
                        default:
                            break
                        }
                        
                        if enableButtons {
                            self.searchBarButton.isEnabled = true
                            self.downloadBarButton.isEnabled = true
                        }
                    }
                } else {
                    ErrorManager.shared.showServerError(error: self.healthHistoryManager.getLastServerError(), onView: self)
                }
                
                self.fadeOutActivityIndicator(completion: nil);
                
            }
        }
    }

    func dismissThis(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func requestRefill() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_PRESCRIPTION_RENEWAL_GOTO_TAP)
        
        if(self.refillRequest != nil && self.refillRequest!.count > 0) {
            print("REQUEST REFILL")
            performSegue(withIdentifier: PRESCRIPTION_RENEWAL_SEGUE, sender: self)
        } else {
            if let count = self.healthHistoryManager.prescriptions?.count, count == 0 {
                GenericHelper.shared.showAlert(withtitle: PrescriptionsMsgConstants.noPrescriptionMessage, andMessage: PrescriptionsMsgConstants.invalidPrescriptionMessage, onView: self)
            } else {
                var hasCTCAPrescription = false
                if let list = healthHistoryManager.prescriptions {
                    for item in list {
                        if (item.prescriptionType == item.CTCA_PRESCRIBED) {
                            hasCTCAPrescription = true
                            break
                        }
                    }
                }
                if hasCTCAPrescription {
                    GenericHelper.shared.showAlert(withtitle: PrescriptionsMsgConstants.prescriptionMsgTitle, andMessage: PrescriptionsMsgConstants.invalidPrescriptionMessage, onView: self)
                } else {
                    GenericHelper.shared.showAlert(withtitle: PrescriptionsMsgConstants.prescriptionMsgTitle, andMessage: PrescriptionsMsgConstants.noCTCAPrescriptionMessage, onView: self)
                }
            }
        }
    }
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == HEALTH_HISTORY_DETAIL_SEGUE {
            if (self.selectedCell != nil && self.healthHistoryManager.prescriptions != nil) {
                if let selectedPrescription:Prescription = self.healthHistoryManager.prescriptions?[self.selectedCell!.row] {
                    let destinationController = segue.destination as! MoreHealthHistoryDetailViewController
                    destinationController.prescription = selectedPrescription
                    if (selectedPrescription.prescriptionType == selectedPrescription.CTCA_PRESCRIBED) {
                        destinationController.showRefillBarButton = true
                        if (refillRequest != nil) {
                            if refillRequest![selectedPrescription.prescriptionId] != nil {
                                destinationController.shouldRefill = true
                            } else {
                                destinationController.shouldRefill = false
                            }
                        } else {
                            destinationController.shouldRefill = false
                        }
                    }
                    print("destinationController prescription: \(selectedPrescription)")
                }
            }
        }
        if segue.identifier == PRESCRIPTION_RENEWAL_SEGUE {
            print("\(PRESCRIPTION_RENEWAL_SEGUE)")
            let destinationNavigationController = segue.destination as! UINavigationController
            if let targetController = destinationNavigationController.topViewController as? PrescriptionRefillViewController {
                print("\(targetController)")
                if (self.refillRequest != nil ) {
                    print("self.refillRequest!.values: \(self.refillRequest!.values)")
                    targetController.prescriptionRefills = Array(self.refillRequest!.values)
                }
            }
        }
    }
    
    @objc private func searchButtonTapped() {
        
        if searchControllerShown {
            hideSearchController()
        } else {
            searchControllerShown = true
            tableView.tableHeaderView = searchController.searchBar
            searchController.searchBar.showsCancelButton = true
            searchController.isActive = true
        }
    }
    
    @objc private func downloadButtonTapped() {

        var actvityIndicatorMsg = ""
        
        switch healthHistoryType {
        case .vitals:
            actvityIndicatorMsg = ActivityIndicatorMsgs.downloadVitalsText
        case .prescriptions:
            actvityIndicatorMsg = ActivityIndicatorMsgs.downloadPrescriptionsText
        case .allergies:
            actvityIndicatorMsg = ActivityIndicatorMsgs.downloadAllergiesText
        case .immunizations:
            actvityIndicatorMsg = ActivityIndicatorMsgs.downloadImmunizationsText
        case .healthIssues:
            actvityIndicatorMsg = ActivityIndicatorMsgs.downloadHealthIssuesText
        default:
            break
        }
        
        self.showActivityIndicator(view: self.view, message: actvityIndicatorMsg)

        healthHistoryManager.downloadHealthHistory(healthHistoryType: healthHistoryType!) {
            status in
            
            self.fadeOutActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.healthHistoryManager.getLastServerError(), onView: self)
            }
        }
    }

    @objc private func menuBarButtonTapped() {
        
        let downloadBtn = UIButton()
        downloadBtn.setImage(UIImage(named: "download_big"), for: .normal)
        downloadBtn.addTarget(self, action: #selector(downloadButtonTapped), for: .touchUpInside)
        downloadBtn.setTitle("Download", for: .normal)
        
        let searchdBtn = UIButton()
        searchdBtn.setImage(UIImage(named: "magnifying"), for: .normal)
        searchdBtn.addTarget(self, action: #selector(searchButtonTapped), for: .touchUpInside)
        searchdBtn.setTitle("Search", for: .normal)
        
        var items = [downloadBtn, searchdBtn]
        
        let navigationBarHeight: CGFloat = self.navigationController!.navigationBar.frame.height + UIApplication.shared.statusBarFrame.size.height

        MenuOverlay.shared.showOverlay(view:view, buttons: &items, origin:navigationBarHeight)
    }
}

// MARK: Table View Data Source

extension MoreHealthHistoryListViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        if (self.healthHistoryType == .vitals) {
            return self.healthHistoryManager.vitalsDocs.count
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("MoreHealthHistoryListViewController numberOfRowsInSection healthHistoryType: \(String(describing: self.healthHistoryType))")
        if (self.healthHistoryType == .vitals) {
            
            if self.hiddenSections.contains(section) {
                return 0
            }
            
            let vitalsDateAR = self.healthHistoryManager.vitalsDates
            let date = vitalsDateAR[section]
            // Couldn't have gotten a vitalDates array without vitalDocs so just assume it's there and unwrap it
            if let vitalsAR = self.healthHistoryManager.vitalsDocs[date] {
                return vitalsAR.count
            }
        }
        if (self.healthHistoryType == .prescriptions){
            if let prescripitionsAR = self.healthHistoryManager.prescriptions {
                return prescripitionsAR.count
            }
        }
        if (self.healthHistoryType == .allergies) {
            if let allergiesAR = self.healthHistoryManager.allergies {
                return allergiesAR.count
            }
        }
        if (self.healthHistoryType == .immunizations){
            if let immunizationsAR = self.healthHistoryManager.immunizations {
                print("Number of Rows in Section: \(section)")
                return immunizationsAR.count
            }
        }
        if (self.healthHistoryType == .healthIssues){
            if let healthIsssuesAR = self.healthHistoryManager.healthIssues {
                print("Number of Rows in Section: \(section)")
                return healthIsssuesAR.count
            }
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (healthHistoryType == .vitals) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "TitleSubCell") as! MoreHealthHistoryTitleSubTableViewCell
             let vitalsDatesAR = self.healthHistoryManager.vitalsDates
                let date = vitalsDatesAR[indexPath.section]
                // Couldn't have gotten a vitalDates array without vitalDocs so just assume it's there and unwrap it
                let vitalsAR = self.healthHistoryManager.vitalsDocs[date]
                let vitals = vitalsAR![indexPath.row]

            cell.titleLabel.setTitleWithHighlight(title: vitals.observationItem!, text: searchText)
                cell.subTitleLabel.text = vitals.value
            
            
            return cell
        }
        if (healthHistoryType == .prescriptions) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "TitleSubActionCell") as! MoreHealthHistoryButtonTableViewCell
            if let prescriptionsAR = self.healthHistoryManager.prescriptions {
                let prescription = prescriptionsAR[indexPath.row]
                cell.prepareView(prescription, searchText:searchText)
                cell.isUserInteractionEnabled = true
            }
            
            return cell
        }
        if (healthHistoryType == .allergies) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "TitleSubDetailCell") as! MoreHealthHistoryTitleSubDetailTableViewCell
            if let allergiesAR = self.healthHistoryManager.allergies {
                let allergy = allergiesAR[indexPath.row]
                
                cell.titleLabel.setTitleWithHighlight(title: allergy.substance, text: searchText)
                cell.subtitleLabel.setTitleWithHighlight(title: allergy.status, text: searchText)
                if !allergy.reactionSeverity.isEmpty {
                    cell.detailLabel.setTitleWithHighlight(title: "Reaction: \(allergy.reactionSeverity)", text: searchText)
                } else {
                    cell.detailLabel.text = ""
                }
            }
            
            return cell
        }
        if (healthHistoryType == .immunizations) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "TitleSubDetailCell") as! MoreHealthHistoryTitleSubDetailTableViewCell
            if let immunizationsAR = self.healthHistoryManager.immunizations {
                let immunization = immunizationsAR[indexPath.row]

                cell.titleLabel.setTitleWithHighlight(title: immunization.immunizationName, text: searchText)
                cell.subtitleLabel.setTitleWithHighlight(title: immunization.vaccineName, text: searchText)
                cell.detailLabel.setTitleWithHighlight(title: "Performed \(immunization.performedDate) by \(immunization.performedBy)", text: searchText)
            }
            
            return cell
        }
        if (healthHistoryType == .healthIssues) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "TitleSubDetailCell") as! MoreHealthHistoryTitleSubDetailTableViewCell
            if let ihealthIssuesAR = self.healthHistoryManager.healthIssues {
                let healthIssue = ihealthIssuesAR[indexPath.row]

                cell.titleLabel.setTitleWithHighlight(title: healthIssue.shortName, text: searchText)
                cell.subtitleLabel.setTitleWithHighlight(title: "\(healthIssue.status), entered \(healthIssue.getFormattedSlashedEnteredDate())", text: searchText)
                cell.detailLabel.setTitleWithHighlight(title: healthIssue.name, text: searchText)
            }
            
            return cell
        }
        
        let cell:UITableViewCell = UITableViewCell()
        return cell
    }
}

extension MoreHealthHistoryListViewController: UITableViewDelegate {

    @objc
    private func hideSection(sender: UIButton) {
        let section = sender.tag
        let vitalsDatesAR = self.healthHistoryManager.vitalsDates
            let date = vitalsDatesAR[section]
            func indexPathsForSection() -> [IndexPath] {
                var indexPaths = [IndexPath]()
                let vitalsAR = self.healthHistoryManager.vitalsDocs[date]
                for row in 0..<vitalsAR!.count {
                    indexPaths.append(IndexPath(row: row,
                                                section: section))
                }
                
                return indexPaths
            }
            
            if self.hiddenSections.contains(section) {
                self.hiddenSections.remove(section)
                self.tableView.insertRows(at: indexPathsForSection(),
                                          with: .fade)
                sender.imageView!.rotate(0.0)
            } else {
                self.hiddenSections.insert(section)
                self.tableView.deleteRows(at: indexPathsForSection(),
                                          with: .fade)
                sender.imageView!.rotate(.pi)
            }
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        if (healthHistoryType == .vitals) {
            if self.healthHistoryManager.vitalsDates.count > 0 {
                return headerHeight
            }
        }
        return CGFloat.leastNonzeroMagnitude
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        if (self.healthHistoryType == .vitals) {
             let vitalsDatesAR = self.healthHistoryManager.vitalsDates
                let dayDateTime: String = getDayDateTime(date: vitalsDatesAR[section])

                let secHeaderView = UIView.init(frame: CGRect.init(x: 0, y: 0, width: tableView.frame.width, height: headerHeight))
                secHeaderView.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
                
                let label = UILabel()
                label.frame = CGRect.init(x: 20, y: 13, width: secHeaderView.frame.width-70, height: 18)
                label.backgroundColor = UIColor.clear
                label.numberOfLines = 1
                label.font = UIFont(name: "HelveticaNeue", size: 13)
                label.minimumScaleFactor = 0.75
                label.adjustsFontSizeToFitWidth = true
                label.textColor = MyCTCAColor.ctcaGrey75.color
                label.lineBreakMode = .byTruncatingTail
                label.text = dayDateTime
                secHeaderView.addSubview(label)

                let sectionButton = UIButton()
                sectionButton.frame = CGRect.init(x: 20+label.frame.width+6, y: 15, width: 20, height: 20)
                sectionButton.backgroundColor = .clear
                sectionButton.tag = section
                sectionButton.addTarget(self, action: #selector(self.hideSection(sender:)), for: .touchUpInside)
                sectionButton.setImage(#imageLiteral(resourceName: "collapse_triangle"), for: .normal)
                sectionButton.setTitleColor(MyCTCAColor.ctcaGreen.color, for: .normal)
                secHeaderView.addSubview(sectionButton)
                
                return secHeaderView
            
        }
        return nil
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (healthHistoryType == .allergies) || (healthHistoryType == .immunizations) || (healthHistoryType == .healthIssues){
            return UITableView.automaticDimension;
        }
        if (healthHistoryType == .prescriptions) {
            return UITableView.automaticDimension
        }
        return cellDefaultHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        self.selectedCell = indexPath
        performSegue(withIdentifier: HEALTH_HISTORY_DETAIL_SEGUE, sender: nil)
    }
    
    func getDayDateTime(date: Date) -> String {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEEE, MMMM dd, yyyy, h:mm"
        let dateString: String = dateFormatter.string(from: date)
        return dateString
    }
}

extension MoreHealthHistoryListViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "myCTCA Health History: \(self.healthHistoryType!.rawValue)"
        
        let attrs = [NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: UIFont.TextStyle.headline),
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color]
        return NSAttributedString(string: str, attributes: attrs)
    }
    
    func description(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        return GenericHelper.shared.getNoRecordsMessageWithStyle()
    }
    
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        if UIDevice.current.orientation.isLandscape {
            return UIImage(named: "myctca_logo_128")
        } else {
            return UIImage(named: "myctca_logo_256")
        }
    }
    
    override func didRotate(from fromInterfaceOrientation: UIInterfaceOrientation) {
        if (self.healthHistoryType == .vitals) {
            if self.healthHistoryManager.vitalsDocs.count == 0 {
                self.tableView.reloadData()
            }
        }  else if (self.healthHistoryType == .prescriptions){
            if let prescripitionsAR = self.healthHistoryManager.prescriptions, prescripitionsAR.count == 0 {
                self.tableView.reloadData()
            }
        } else if (self.healthHistoryType == .allergies){
            if let allergiesAR = self.healthHistoryManager.allergies, allergiesAR.count == 0  {
                self.tableView.reloadData()
            }
        } else if (self.healthHistoryType == .immunizations){
            if let immunizationsAR = self.healthHistoryManager.immunizations, immunizationsAR.count == 0  {
                self.tableView.reloadData()
            }
        } else if (self.healthHistoryType == .healthIssues){
            if let healthIsssuesAR = self.healthHistoryManager.healthIssues, healthIsssuesAR.count == 0  {
                self.tableView.reloadData()
            }
        }
    }
}

extension MoreHealthHistoryListViewController: UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        if let searchText = searchController.searchBar.text {
            if !searchText.isEmpty {
                self.searchText = searchText

                switch(self.healthHistoryType) {
                case .vitals:
                    healthHistoryManager.filterVitalsList(filterText: searchText)
                    
                case .prescriptions:
                    healthHistoryManager.filterPrescriptionsList(filterText: searchText)
                    
                case .allergies:
                    healthHistoryManager.filterAllergiesList(filterText: searchText)
                    
                case .healthIssues:
                    healthHistoryManager.filterHelathIssuesList(filterText: searchText)
                
                case .immunizations:
                    healthHistoryManager.filterImmunizationsList(filterText: searchText)
                    
                case .none:
                    break
                }
                
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            } else {
                resetListView()
            }
        }
    }
}

extension MoreHealthHistoryListViewController: UISearchBarDelegate {
    func searchBarShouldBeginEditing(_ searchBar: UISearchBar) -> Bool {
        searchController.searchBar.showsCancelButton = true
        return true
    }

    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        searchController.searchBar.endEditing(true)
        searchController.resignFirstResponder()
    }

    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        searchController.resignFirstResponder()
    }

    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        hideSearchController()
        resetListView()
    }
    
    func resetListView() {
        DispatchQueue.main.async {
            self.searchText = ""
            
            switch(self.healthHistoryType) {
            case .vitals:
                self.healthHistoryManager.vitalsDocs = self.healthHistoryManager.vitalsDocsOriginal
                self.healthHistoryManager.vitalsDates = self.healthHistoryManager.vitalsDatesOriginal
                
            case .prescriptions:
                self.healthHistoryManager.prescriptions = self.healthHistoryManager.prescriptionsOriginal
                
            case .allergies:
                self.healthHistoryManager.allergies = self.healthHistoryManager.allergiesOriginal
                
            case .healthIssues:
                self.healthHistoryManager.healthIssues = self.healthHistoryManager.healthIssuesOriginal
            
            case .immunizations:
                self.healthHistoryManager.immunizations = self.healthHistoryManager.immunizationsOriginal
                
            case .none:
                break
            }
            self.tableView.reloadData()
        }
    }
}
