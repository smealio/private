//
//  LabsViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 11/10/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class LabsViewController: CTCABaseViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    let cellLatestHeight: CGFloat = 155.0
    let cellHeaderHeight: CGFloat = 44.0
    let cellLabResultHeight: CGFloat = 70.0
            
    let LABS_DET_SEGUE: String = "LabsDetailSegue"
    
    var selectedLabResult: LabResult?
    var firstLabResult: LabResult?
    var isSearchMode = false
    var sectionHeader:UIView?
    
    lazy var refreshCtrl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refreshLabsData(_:)), for: .valueChanged)
        refreshControl.tintColor = MyCTCAColor.ctcaSecondGreen.color
        refreshControl.attributedTitle = NSAttributedString(string: ActivityIndicatorMsgs.refreshLabResultsText, attributes: [
            NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Italic", size: 14.0)!,
            NSAttributedString.Key.foregroundColor : MyCTCAColor.ctcaGreen.color
        ])
        
        return refreshControl
    }()
    
    var hasViewLabsPermission: Bool = false
    
    let labsManager = LabsManager.shared
    var searchText = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Set Title
        self.title = "Lab Results"
        
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
        
        // Set tableview for empty data sets and
        // an empty footer to clear extra cells
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_LAB_RESULTS_VIEW)
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "LabResultsSectHeader")
        
        
        //Register subtitle Cell
        let sectHeader = UINib(nibName: "LabResultsSectHeader", bundle: nil)
        self.tableView.register(sectHeader, forCellReuseIdentifier: "LabResultsSectHeader")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        // Check Permissions
        let currentUserId = AppSessionManager.shared.currentUser.currentUserId
        let userProfile = AppSessionManager.shared.currentUser.userProfile!
        
        hasViewLabsPermission = userProfile.userCan(UserPermissionType.viewLabResults, viewerId: currentUserId)
        
        loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        if (hasViewLabsPermission == true) {

            if (labsManager.labResults == nil) {
                // Show Activity Indicator
                showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.retriveLabResultsText)
                
                // Fetch Data
                labsManager.fetchLabResults() {
                    status in
                    
                    self.fadeOutActivityIndicator(completion: nil);
                    
                    if status == .SUCCESS {
                        DispatchQueue.main.async{
                            self.tableView.reloadData()
                        }
                    } else {
                        ErrorManager.shared.showServerError(error: self.labsManager.getLastServerError(), onView: self)
                    }
                }
            }
        }
    }
    
    @objc private func refreshLabsData(_ sender: Any) {
        // Show Activity Indicator
        showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.refreshLabResultsText)
        
        // Fetch Data
        labsManager.fetchLabResults() {
            status in
            
            self.fadeOutActivityIndicator(completion: nil);
            
            if status == .SUCCESS {
                DispatchQueue.main.async{
                    self.refreshCtrl.endRefreshing()
                    self.tableView.reloadData()
                }
            } else {
                ErrorManager.shared.showServerError(error: self.labsManager.getLastServerError(), onView: self)
            }
        }
    }
    
    func dismissThis(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == LABS_DET_SEGUE {
            let destinationController = segue.destination as! LabsDetailViewController
            destinationController.labResult = self.selectedLabResult
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
      super.viewWillTransition(to: size, with: coordinator)
      DispatchQueue.main.async {
        if let contentView = self.sectionHeader {
            contentView.layoutIfNeeded()
            self.sectionHeader = contentView
        }
      }
    }
}

// MARK: Table View Data Source

extension LabsViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        // There are 2 sections...1 for the Latest Results and 1 for all  the results
        return 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if let allLabResults = labsManager.labResultsOriginal, allLabResults.count > 0 {
            if (section == 0) {
                // Latest Results Card
                if firstLabResult == nil {
                    firstLabResult = labsManager.labResults?.first
                }
                return 1
            } else {
                if let labResults = labsManager.labResults {
                    return labResults.count
                }
            }
        }
        
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (indexPath.section == 0) {
            // Latest Results Card
            let cell = tableView.dequeueReusableCell(withIdentifier: "labsLatestTableViewCell") as! LabsLatestTableViewCell
            
            cell.prepareView(firstLabResult!)
            return cell
        } else {
            // Regular Lab Result listings
            let cell = tableView.dequeueReusableCell(withIdentifier: "labResultsTableViewCell") as! LabsResultTableViewCell
            if let labResults = labsManager.labResults, indexPath.row < labResults.count {
                cell.prepareView(labResults[indexPath.row], highLigtText: searchText)
            }
            return cell
        }
        
    }
}

extension LabsViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (indexPath.section == 0) {
            return UITableView.automaticDimension
        }
        return cellLabResultHeight
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        if let allLabResults = labsManager.labResultsOriginal, allLabResults.count > 0, section > 0 {
            return cellHeaderHeight
        }

        return CGFloat.leastNonzeroMagnitude
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        if section > 0 {

            if let headerCell = tableView.dequeueReusableCell(withIdentifier: "LabResultsSectHeader") as? LabResultsSectHeader {
                headerCell.prepareView()
                headerCell.delegate = self
                
                let contentView = UIView()
                contentView.addSubview(headerCell)
                
                // add extra code to pin all the anchors
                headerCell.translatesAutoresizingMaskIntoConstraints = false
                headerCell.leadingAnchor.constraint(equalTo: contentView.leadingAnchor).isActive = true
                headerCell.trailingAnchor.constraint(equalTo: contentView.trailingAnchor).isActive = true
                headerCell.topAnchor.constraint(equalTo: contentView.topAnchor).isActive = true
                headerCell.bottomAnchor.constraint(equalTo: contentView.bottomAnchor).isActive = true
                
                sectionHeader = contentView

                return contentView
            }
        }
        return nil
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: true)
        
        if (indexPath.section ==  0) {
            self.selectedLabResult = labsManager.labResults![0]
        } else {
            self.selectedLabResult = labsManager.labResults![indexPath.row]
        }
        
        performSegue(withIdentifier: LABS_DET_SEGUE, sender: nil)
    }
}

extension LabsViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "myCTCA Lab Results"
        
        let attrs = [NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: UIFont.TextStyle.headline),
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color]
        return NSAttributedString(string: str, attributes: attrs)
    }
    
    func description(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        
        if (!hasViewLabsPermission) {
            
            var str =  "You don't have permission to view Lab Result records for this patient"
            if let currentViewablePatient: String = AppSessionManager.shared.currentUser.userProfile?.fullName {
                str += " \(currentViewablePatient)"
            }
            str += ". If this is incorrect, please call the regional medical center directly."
            
            let style = NSMutableParagraphStyle()
            style.alignment = NSTextAlignment.center
            
            let attrs = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 14.0),
                         NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color,
                         NSAttributedString.Key.paragraphStyle: style ]
            
            return NSAttributedString(string: str, attributes: attrs)
        } else {
            return GenericHelper.shared.getNoRecordsMessageWithStyle()
        }
    }
    
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        if UIDevice.current.orientation.isLandscape {
            return UIImage(named: "myctca_logo_128")
        } else {
            return UIImage(named: "myctca_logo_256")
        }
    }
    
    override func didRotate(from fromInterfaceOrientation: UIInterfaceOrientation) {
        if let rows = labsManager.labResults, rows.count == 0 {
            self.tableView.reloadData()
        }
        
    }
    
    func filterLabReportsBy() {
        if let list = labsManager.labResultsOriginal {
            let listFilterdCollectedBy = list.filter{$0.collectedBy.localizedCaseInsensitiveContains(searchText)}

            var filterList = listFilterdCollectedBy
            
            for labResult in list {
                let newList = filterLabSetsBy(list: labResult.labSets)
                if newList.count > 0 {
                    filterList.append(labResult)
                }
            }
            
            let objectSet = Set(filterList.filter({ $0.performedDate != nil }).map { $0 })
            labsManager.labResults = Array(objectSet).sorted { $0.performedDate! > $1.performedDate! }
        }
    }
    
    func filterLabSetsBy(list:[LabSet]) -> [LabSet] {
        
        //let list1 = list.filter{$0.orderId.localizedCaseInsensitiveContains(searchText)}
        let list = list.filter{$0.name.localizedCaseInsensitiveContains(searchText)}
        
        let filterList = list
        //filterList.append(contentsOf: list2)
        
//        for labSet in list {
//            let newList = filterLabSetDetailsBy(list: labSet.details)
//            if newList.count > 0 {
//                filterList.append(labSet)
//            }
//        }
        let objectSet = Set(filterList.map { $0 })
        return Array(objectSet)
    }
    
    func filterLabSetDetailsBy(list:[LabSetDetail]) -> [LabSetDetail] {
        let list1 = list.filter{$0.id.localizedCaseInsensitiveContains(searchText)}
        let list2 = list.filter{$0.itemName.localizedCaseInsensitiveContains(searchText)}
        let list3 = (list.filter({ $0.abnormalityCodeCalculated != nil })).filter{$0.abnormalityCodeCalculated!.localizedCaseInsensitiveContains(searchText)}
        let list4 = (list.filter({ $0.abnormalityCodeDescription != nil })).filter{$0.abnormalityCodeDescription!.localizedCaseInsensitiveContains(searchText)}
        let list5 = (list.filter({ $0.result != nil })).filter{$0.result!.localizedCaseInsensitiveContains(searchText)}
        let list6 = (list.filter({ $0.normalRange != nil })).filter{$0.normalRange!.localizedCaseInsensitiveContains(searchText)}
        let list7 = (list.filter({ $0.notes != nil })).filter{$0.notes!.localizedCaseInsensitiveContains(searchText)}
        
        var filterList = list1
        filterList.append(contentsOf: list2)
        filterList.append(contentsOf: list3)
        filterList.append(contentsOf: list4)
        filterList.append(contentsOf: list5)
        filterList.append(contentsOf: list6)
        filterList.append(contentsOf: list7)
        
        let objectSet = Set(filterList.map { $0 })
        return Array(objectSet)
    }
    
    func resetLabsResultsList() {
        labsManager.labResults = labsManager.labResultsOriginal
        
        DispatchQueue.main.async {
            self.tableView.refreshControl = self.refreshCtrl

            self.tableView.reloadData()
        }
    }
}

extension LabsViewController: LabResultsSectHeaderViewProtocols {
    func filterListBy(text: String) {
        searchText = text
        filterLabReportsBy()
        
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
    
    func resetList() {
        searchText = ""
        resetLabsResultsList()
    }
    
    func didStartedSearchMode() {
        
    }
    
    func didEndedSearchMode() {
        searchText = ""
        resetLabsResultsList()
    }
}

