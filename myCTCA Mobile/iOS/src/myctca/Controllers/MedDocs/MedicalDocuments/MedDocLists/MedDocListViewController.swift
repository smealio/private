//
//  MedDocListViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/15/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MedDocListViewController: UIViewController, CTCAViewControllerProtocol {

    @IBOutlet weak var searchNavButton: UIBarButtonItem!
    
    @IBOutlet weak var tableView: UITableView!
    
    let cellHeight:CGFloat = 55.0
    var selectedCell: IndexPath?
    
    var medDocType:MedDocType?
    
    let MEDDOC_DETAIL_SEGUE: String = "MedDocDetailSegue"
    
    let CLINICAL = "Clinical"
    let RADIATION = "Radiation"
    let INTEGRATIVE = "Integrative"
    let IMAGING = "Imaging"
    
    let medDocsFetchErrorTitle: String = "Medical Doc Error"
    let medDocsFetchErrorResponse: String = "There seems to be some kind of problem retrieving medical document data. You can try again later or call the Care Manager directly."
    
    let searchController = UISearchController(searchResultsController: nil)
    var searchControllerShown = false
    var searchText = ""
    let medDocsManager = MedDocsManager.shared
    
    lazy var refreshCtrl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(fetchMedDocs), for: .valueChanged)
        refreshControl.tintColor = MyCTCAColor.ctcaSecondGreen.color
        refreshControl.attributedTitle = NSAttributedString(string: getRefreshIndicatorMessage(), attributes: [
            NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Italic", size: 14.0)!,
            NSAttributedString.Key.foregroundColor : MyCTCAColor.ctcaGreen.color
            ])
        
        return refreshControl
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = ""
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        // Set tableview for empty data sets and
        // an empty footer to clear extra cells
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        self.tableView.refreshControl = refreshCtrl
        
        self.prepareView()
        showSearchController()
    }
    
    func showSearchController() {
        searchController.searchResultsUpdater = self
        searchController.obscuresBackgroundDuringPresentation = false
        searchController.searchBar.placeholder = "Search..."
        searchController.searchBar.delegate = self
        if #available(iOS 13.0, *) {
            let appearance = UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self])
            appearance.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGreen.color], for: .normal);
        }
        definesPresentationContext = true
        if #available(iOS 13.0, *) {
            searchController.searchBar.searchTextField.clearButtonMode = .never
            let appearance = UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self])
            appearance.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGreen.color], for: .normal);
        }
    }
    
    func hideSearchController() {
        searchControllerShown = false
        
        DispatchQueue.main.async {
            self.tableView.refreshControl = self.refreshCtrl
            
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
    
    @IBAction func searchTapped(_ sender: Any) {
        if searchControllerShown {
            hideSearchController()
        } else {
            self.tableView.refreshControl = nil

            searchControllerShown = true
            tableView.tableHeaderView = searchController.searchBar
            searchController.searchBar.showsCancelButton = true
            searchController.isActive = true
        }
    }
    
    private func prepareView() {
        setTitleForDocType()
        
        if #available(iOS 13.0, *) {
        } else {
            searchNavButton.image = #imageLiteral(resourceName: "magnifying")
        }
        
        loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        fetchMedDocs()
    }
    
    private func setTitleForDocType() {
        if (medDocType != nil) {
            switch (self.medDocType!) {
            case .clinical:
                self.title = CLINICAL
                AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CLINICAL_VIEW)
            case .radiation:
                self.title = RADIATION
                AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_RADIATION_VIEW)
            case .integrative:
                self.title = INTEGRATIVE
                AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_INTEGRATIVE_VIEW)
            case .imaging:
                self.title = IMAGING
                AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_IMAGING_VIEW)
            }
        }
    }
    
    private func getActivityIndicatorMsg() -> String {
        var actMessage = ""

        switch (self.medDocType!) {
        case .clinical:
            actMessage = ActivityIndicatorMsgs.retriveClinicalText
        case .radiation:
            actMessage = ActivityIndicatorMsgs.retriveRadiationText
        case .integrative:
            actMessage = ActivityIndicatorMsgs.retriveIntegrativeText
        case .imaging:
            actMessage = ActivityIndicatorMsgs.retriveImagingText
        }
        
        return actMessage
    }
    
    private func getRefreshIndicatorMessage() -> String {
        
        var actMessage = ""

        switch (self.medDocType!) {
        case .clinical:
            actMessage = ActivityIndicatorMsgs.refreshClinicalText
        case .radiation:
            actMessage = ActivityIndicatorMsgs.refreshRadiationText
        case .integrative:
            actMessage = ActivityIndicatorMsgs.refreshIntegrativeText
        case .imaging:
            actMessage = ActivityIndicatorMsgs.refreshImagingText
        }
        
        return actMessage
    }
    
    @objc private func fetchMedDocs() {
        
        if !self.refreshCtrl.isRefreshing {
            let  actMessage = getActivityIndicatorMsg()

            showActivityIndicator(view: self.view, message: actMessage)
        } else {
            searchNavButton.isEnabled = false
        }
        
        if (medDocType == .imaging) {
            medDocsManager.fetchImagingDocs() {
                status in
                
                if !self.refreshCtrl.isRefreshing {
                    self.fadeOutActivityIndicator(completion: nil);
                }
                
                if status == .FAILED {
                    ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
                } else {
                    DispatchQueue.main.async {
                        if self.refreshCtrl.isRefreshing {
                            self.refreshCtrl.endRefreshing()
                            self.searchNavButton.isEnabled = true
                        }

                        self.tableView.reloadData()
                        if self.medDocsManager.imagingDocsOriginal.count == 0 {
                            self.searchNavButton.isEnabled = false
                        }
                    }
                }
            }
        } else {
            medDocsManager.fetchMedDocs(type: medDocType!) {
                status in
                
                if !self.refreshCtrl.isRefreshing {
                    self.fadeOutActivityIndicator(completion: nil);
                }
                    
                if status == .FAILED {
                    ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
                } else {
                    DispatchQueue.main.async {
                        if self.refreshCtrl.isRefreshing {
                            self.refreshCtrl.endRefreshing()
                            self.searchNavButton.isEnabled = true
                        }

                        self.tableView.reloadData()
                        if self.medDocsManager.medDocsOriginal.count == 0 {
                            self.searchNavButton.isEnabled = false
                        }
                    }
                }
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

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == MEDDOC_DETAIL_SEGUE {
            let destinationController = segue.destination as! MedDocDetailViewController
            if (selectedCell != nil) {
                if (medDocType == .imaging) {
                    destinationController.imagingDoc = medDocsManager.imagingDocs[selectedCell!.row]
                    destinationController.medDocType = .imaging
                } else {
                    destinationController.medDoc = medDocsManager.medDocs[selectedCell!.row]
                    destinationController.medDocType = medDocType!
                }
            }
        }
    }
}

// MARK: Table View Data Source

extension MedDocListViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if (medDocType == .imaging) {
            return self.medDocsManager.imagingDocs.count
        }
        return self.medDocsManager.medDocs.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "medDocListTableViewCell") as! MedDocListTableViewCell
        if (medDocType == .imaging) {
            let imagingDoc: ImagingDocNew = self.medDocsManager.imagingDocs[indexPath.row]
            cell.nameLabel.setTitleWithHighlight(title: self.medDocsManager.imagingDocs[indexPath.row].itemName, text: searchText)
            cell.authoredLabel.setTitleWithHighlight(title:  "Created \(imagingDoc.documentDateString)", text: searchText)
        } else {
            cell.nameLabel.setTitleWithHighlight(title: self.medDocsManager.medDocs[indexPath.row].docName, text: searchText)
            var authorText: String = "Authored \(self.medDocsManager.medDocs[indexPath.row].docAuthoredDateString) by \(self.medDocsManager.medDocs[indexPath.row].docAuthor)"
            if (self.medDocsManager.medDocs[indexPath.row].docAuthorOccupationCode != "") {
                authorText += " (\(self.medDocsManager.medDocs[indexPath.row].docAuthorOccupationCode))"
            }
            cell.authoredLabel.setTitleWithHighlight(title: authorText, text: searchText)
        }
        
        return cell
    }
}

extension MedDocListViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return cellHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        self.selectedCell = indexPath
        performSegue(withIdentifier: MEDDOC_DETAIL_SEGUE, sender: nil)
    }
}

extension MedDocListViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        var str = "myCTCA Medical Documents"
        if (medDocType != nil) {
            if (self.medDocType! == .clinical) {
                str = "\(str): \(CLINICAL)"
            } else if(self.medDocType! == .radiation) {
                str = "\(str): \(RADIATION)"
            } else if(self.medDocType! == .integrative) {
                str = "\(str): \(INTEGRATIVE)"
            } else if(self.medDocType! == .imaging) {
                str = "\(str): \(IMAGING)"
            }
        }
        
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
        if self.medDocsManager.medDocs.count == 0 {
            self.tableView.reloadData()
        }
    }
}

extension MedDocListViewController: UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        if let searchText = searchController.searchBar.text {
            if !searchText.isEmpty {
                self.searchText = searchText
                filterList(filterText: searchText)
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            } else {
                resetListView()
            }
        }
    }
    
    func filterList(filterText:String) {
        
        if (medDocType == .imaging) {
            let listFilterdName = medDocsManager.imagingDocsOriginal.filter{$0.itemName.localizedCaseInsensitiveContains(filterText)}
            let listFilterdDate = medDocsManager.imagingDocsOriginal.filter{$0.documentDateString.localizedCaseInsensitiveContains(filterText)}
            
            var filterList = listFilterdName
            filterList.append(contentsOf: listFilterdDate)
            
            let objectSet = Set(filterList.map { $0 })
            medDocsManager.imagingDocs = Array(objectSet).sorted { $0.documentDate > $1.documentDate }
        } else {
            let listFilterdAuthor = medDocsManager.medDocsOriginal.filter{$0.docAuthor.localizedCaseInsensitiveContains(filterText)}
            let listFilterdDocName = medDocsManager.medDocsOriginal.filter{$0.docName.localizedCaseInsensitiveContains(filterText)}
            let listFilterdDate = medDocsManager.medDocsOriginal.filter{$0.docAuthoredDateString.localizedCaseInsensitiveContains(filterText)}
            
            var filterList = listFilterdAuthor
            filterList.append(contentsOf: listFilterdDocName)
            filterList.append(contentsOf: listFilterdDate)

            let objectSet = Set(filterList.map { $0 })
            medDocsManager.medDocs = Array(objectSet).sorted { $0.docAuthoredDate > $1.docAuthoredDate }
        }
    }
}

extension MedDocListViewController: UISearchBarDelegate {
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
            if (self.medDocType == .imaging) {
                self.medDocsManager.imagingDocs = self.medDocsManager.imagingDocsOriginal
            } else {
                self.medDocsManager.medDocs = self.medDocsManager.medDocsOriginal
            }
            self.tableView.reloadData()
        }
    }
}
