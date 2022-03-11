//
//  MoreActivityLogViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 3/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreActivityLogViewController: CTCABaseViewController, ActivityLogFilterProtocol {
    
    @IBOutlet weak var fillterButton: UIBarButtonItem!
    
    var isFetchInProgress = false
    var currentPage = 0
    
    @IBOutlet weak var tableView: UITableView!
    
    let cellDefaultHeight:CGFloat = 55.0
    let cellHeaderHeight: CGFloat = 44.0
    
    var didFetchAllRecords = false
    let activityLogManager = ActivityLogManager()
    
    var activityLogs = [Date:[ActivityLog]]()
    var activityLogDates: [Date] = [Date]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        // Set tableview for empty data sets and
        // an empty footer to clear extra cells
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        
        //Register Loading Cell
        let tableViewLoadingCellNib = UINib(nibName: "MoreActivityLogLoadingViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCellNib, forCellReuseIdentifier: "moreActivityLogLoadingViewCell")
        
        activityLogManager.applyFilter = false
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ACTIVITY_LOG_VIEW)
        
        loadMoreData()
    }
    
    func fetchLog() {
        
        if currentPage == 0 { //Displaying only for the first time
            DispatchQueue.main.async {
                self.fillterButton.isEnabled = false
                self.showActivityIndicator(view: self.view, message: "Retrieving Logs…")
            }
        }
        
        activityLogManager.fetchActivityLogs(page: currentPage) {
            activityLogDict, status in
            
            self.isFetchInProgress = false
            
            if status == .SUCCESS {
                if let dict = activityLogDict {
                    self.currentPage += 1
                    
                    if self.activityLogs.count  > 0 {
                        self.activityLogs.merge(dict, uniquingKeysWith: +)
                    } else {
                        self.activityLogs = dict
                    }

                    var recCount = 0
                    for item in dict {
                        recCount += item.value.count
                    }
                    
                    //TODO : Revisit AL limit
                    if recCount != 19 && recCount < self.activityLogManager.activityLogPageSize {
                        self.didFetchAllRecords = true
                    }
                    
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                    }
                }
                
                self.activityLogDates = Array(self.activityLogs.keys)
                self.activityLogDates.sort{ $0 > $1 }
                print("fetchActivityLog: \(String(describing: self.activityLogDates))")
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
                
            } else {
                ErrorManager.shared.showServerError(error: self.activityLogManager.getLastServerError(), onView: self)
                
                if (self.activityLogManager.applyFilter) {
                    self.activityLogManager.applyFilter = false
                    self.fillterButton.image = #imageLiteral(resourceName: "filter")
                }
            }
            self.fadeOutActivityIndicator(completion: nil);
            self.fillterButton.isEnabled = true
        }
    }

    func dismissThis(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func filterTapped(_ sender: Any) {
        
        if activityLogManager.applyFilter  {
            activityLogManager.applyFilter = false
            fillterButton.image = #imageLiteral(resourceName: "filter")
            clearData()
            fetchLog()
        } else {
            
            activityLogManager.filterDate = ""
            activityLogManager.filterMessage = ""
            activityLogManager.filterUsername = ""
            
            let filterViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreActivityLogFilterViewController") as! MoreActivityLogFilterViewController
            
            filterViewController.delegate = self
            self.present(filterViewController, animated: true, completion: nil)
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ACTIVITY_LOG_APPLY_FILTER_TAP)
        }
    }
    
    override func loadViewData() {
        //will be called only for reload for offiline to active case
        NetworkStatusManager.shared.registerForReload(view: self)
        loadMoreData()
    }
}

// MARK: Table View Data Source

extension MoreActivityLogViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return (activityLogDates.count == 0) ? activityLogDates.count : activityLogDates.count+1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        //Return the amount of items
        if section == activityLogDates.count { //last row
            return 1
        } else {
            let sectionDate = activityLogDates[section]
            let activityLogItems:[ActivityLog] = activityLogs[sectionDate]!
            return activityLogItems.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if indexPath.section == activityLogDates.count { //last row
            let cell = tableView.dequeueReusableCell(withIdentifier: "moreActivityLogLoadingViewCell", for: indexPath) as! MoreActivityLogLoadingViewCell
            if activityLogDates.count > 0 && !didFetchAllRecords {
                if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                    cell.activityIndicator.isHidden = true
                } else {
                    cell.showAnimating()
                }
            } else {
                cell.activityIndicator.isHidden = true
            }
            return cell
        } else {
            let sectionDate = activityLogDates[indexPath.section]
            let activityLogItems: [ActivityLog]  = activityLogs[sectionDate]!
            let activityLogItem: ActivityLog = activityLogItems[indexPath.row]
            
            let cell: MoreActivityLogTableViewCell = tableView.dequeueReusableCell(withIdentifier: "MoreActivityLogTableViewCell") as! MoreActivityLogTableViewCell
            if indexPath.row <= activityLogItems.count {
                cell.prepareView(logRec: activityLogItem)
            } else {
                cell.prepareView(logRec: .none)
            }
            return cell
        }
    }
}

extension MoreActivityLogViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return cellDefaultHeight
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        if section == activityLogDates.count { //last row
            return 0.0
        } else {
            return cellHeaderHeight
        }
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        if section == activityLogDates.count { //last row
            return UIView()
        } else {
            let cell = MoreActivityLogHeaderViewCell()
            
            let sectionDate = activityLogDates[section]
            print("viewForHeaderInSection: \(sectionDate)")
            cell.prepareView(sectionDate)
            
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}

extension MoreActivityLogViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "myCTCA Activity Logs"
        
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
        if activityLogDates.count == 0 {
            self.tableView.reloadData()
        }
    }
}

//For proving infinite scrolling
extension MoreActivityLogViewController {
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if !didFetchAllRecords {
            let offsetY = scrollView.contentOffset.y
            let contentHeight = scrollView.contentSize.height
            
            if (offsetY > contentHeight - scrollView.frame.height * 4) && !isFetchInProgress {
                loadMoreData()
            }
        }
    }
    
    func loadMoreData() {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        if !self.isFetchInProgress {
            self.isFetchInProgress = true
            DispatchQueue.global().async {
                self.fetchLog()
            }
        }
    }
    
    func applyFilterOnActivityLogs(date: String, username: String, message: String) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            activityLogManager.applyFilter = false
            fillterButton.image = #imageLiteral(resourceName: "filter")
            currentPage = 0
            return
        }
        
        //Reset current page
        clearData()
        
        activityLogManager.filterDate = date
        activityLogManager.filterMessage = message
        activityLogManager.filterUsername = username
        
        activityLogManager.applyFilter = true
        fillterButton.image = #imageLiteral(resourceName: "filter_off")
        
        fetchLog()
    }
    
    func clearData() {
        //Reset current page
        currentPage = 0
        activityLogs.removeAll()
        activityLogDates.removeAll()
        didFetchAllRecords = false
    }
}
