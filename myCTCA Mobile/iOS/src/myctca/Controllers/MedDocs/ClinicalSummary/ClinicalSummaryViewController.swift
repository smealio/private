//
//  ClinicalSummaryViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/14/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class ClinicalSummaryViewController: UIViewController, CTCAViewControllerProtocol, PeriodSelectionProtocol {

    @IBOutlet weak var tableView: UITableView!
    
    let CLINICALSUMMARY_DETAIL_SEGUE: String = "clinicalSummaryDetailSegue"
    var selectedCell: IndexPath?
    
    let fetchClinicalSummaryErrorTitle: String = ClinicalSummaryMsgConstants.fetchClinicalSummaryErrorTitle
    let fetchClinicalSummaryErrorResponse: String = ClinicalSummaryMsgConstants.fetchClinicalSummaryErrorResponse

    @IBOutlet weak var selectionModeButton: UIBarButtonItem!
    
    @IBOutlet weak var filterButton: UIBarButtonItem!
    
    var inSelectionMode = false
    var selectCell = ClinicalSummaryTableViewCell()
    
    @IBOutlet weak var BtnView: UIView!
    
    var filterMode = false
    @IBOutlet weak var downloadButton: UIButton!
    @IBOutlet weak var transitButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    
    let medDocsManager = MedDocsManager.shared
        
    lazy var refreshCtrl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refreshData), for: .valueChanged)
        refreshControl.tintColor = MyCTCAColor.ctcaSecondGreen.color
        refreshControl.attributedTitle = NSAttributedString(string: ActivityIndicatorMsgs.refreshClinicalSummariesText, attributes: [
            NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Italic", size: 14.0)!,
            NSAttributedString.Key.foregroundColor : MyCTCAColor.ctcaGreen.color
            ])
        
        return refreshControl
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "Clinical Summaries"
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
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CS_VIEW)
    }
    
    private func prepareView() {
        
        DispatchQueue.main.async {
            self.filterButton.isEnabled = false
            self.selectionModeButton.isEnabled = false
            
            self.cancelButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
            self.cancelButton.layer.borderWidth = 0.5
        }
        
        loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        fetchClinicalSummaries(withFromDate: "", andToDate: "")
    }
    
    @objc private func refreshData() {
        fetchClinicalSummaries(withFromDate: "", andToDate: "")
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func fetchClinicalSummaries(withFromDate:String, andToDate:String) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            
            DispatchQueue.main.async {
                self.filterMode = false
                self.filterButton.image = #imageLiteral(resourceName: "filter")
                
                self.filterButton.isEnabled = true
                self.selectionModeButton.isEnabled = true
                
                self.tableView.refreshControl = self.refreshCtrl
            }
            
            return
        }
        
        if !refreshCtrl.isRefreshing {
            showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.retriveClinicalSummariesText)
            self.filterButton.isEnabled = false
            self.selectionModeButton.isEnabled = false
        }
        
        medDocsManager.fetchClinicalSummaries(fromDate: withFromDate, toDate: andToDate) {
            result in
            
            if result == .SUCCESS {
                DispatchQueue.main.async {
                    if  self.medDocsManager.clinicalSummaryList.count > 0 {
                        self.filterButton.isEnabled = true
                        self.selectionModeButton.isEnabled = true
                    }
                    self.tableView.reloadData()
                }
            } else {
                ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
            }
            
            if self.refreshCtrl.isRefreshing {
                self.refreshCtrl.endRefreshing()
            } else {
                self.fadeOutActivityIndicator(completion: nil)
            }
        }
    }
    
    func dismissThis(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == CLINICALSUMMARY_DETAIL_SEGUE {
            let destinationController = segue.destination as! ClinicalSummaryDetailViewController
            print("prepare destination: \(destinationController)")
            if (selectedCell != nil) {
                destinationController.clinicalSummary = medDocsManager.clinicalSummaryList[selectedCell!.row]
                print("prepare clinicalSummary: \(medDocsManager.clinicalSummaryList[selectedCell!.row].csTitle)")
            }
        }
    }
    
    
    @IBAction func filterButtonTapped(_ sender: Any) {
        
        if self.medDocsManager.clinicalSummaryList.count == 0 && !filterMode {
            //show message
            GenericHelper.shared.showAlert(withtitle: ClinicalSummaryMsgConstants.noRecordsMessageTitle, andMessage: CommonMsgConstants.noRecordsFoundMsg, onView: self)
            return
        }
        
        if filterMode {
            filterMode = false
            filterButton.image = #imageLiteral(resourceName: "filter")
            
            prepareView()
            tableView.refreshControl = refreshCtrl
        } else {
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CS_FILTER_VIEW)
            
            let filterViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PeriodSelectionViewController") as! PeriodSelectionViewController

            filterViewController.delegate = self
            filterViewController.type = .CSDDocs
            self.present(filterViewController, animated: true, completion: nil)
        }
    }
    
    @IBAction func selectionModeButtonTapped(_ sender: Any) {
        
        if self.medDocsManager.clinicalSummaryList.count == 0 {
            //show message
            GenericHelper.shared.showAlert(withtitle: ClinicalSummaryMsgConstants.noRecordsMessageTitle, andMessage: CommonMsgConstants.noRecordsFoundMsg, onView: self)
            return
        }
        
        if inSelectionMode  {
            inSelectionMode = false
            tableView.refreshControl = refreshCtrl
        } else {
            inSelectionMode = true
            tableView.refreshControl = nil
        }
        
        selectionModeButton.isEnabled = false
        filterButton.isEnabled = false
        
        downloadButton.isEnabled = true
        transitButton.isEnabled = true
        
        BtnView.isHidden = false
        tableView.reloadData()
    }
    
    func getSelections() -> [String] {
        var selectedCS = [String]()
         let rows = tableView.numberOfRows(inSection: 1)
         for i in 1...rows {
             if let cell = tableView.cellForRow(at: IndexPath(row: i, section: 1)) as? ClinicalSummaryTableViewCell {
                 if cell.isSelectedRec {
                     selectedCS.append(cell.id)
                 }
             }
         }
        
        return selectedCS
    }
    
    @IBAction func downloadTapped(_ sender: Any) {
        if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.downloadCCDADocument) {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_GOTO_DOWNLOAD_TAP)
            let trasmitViewController = self.storyboard?.instantiateViewController(withIdentifier: "ClinicalSummaryDownloadViewController") as! ClinicalSummaryDownloadViewController
            
            trasmitViewController.documentIDs = getSelections()
            let backItem = UIBarButtonItem()
            backItem.title = "Back"
            navigationItem.backBarButtonItem = backItem
            self.navigationController?.pushViewController(trasmitViewController, animated: true)
        } else {
            GenericHelper.shared.showNoAccessMessage(view: nil)
        }
    }
    
    @IBAction func transmitTapped(_ sender: Any) {
        if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.transmitCCDADocuments) {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_GOTO_TRANSMIT_TAP)
            
            let trasmitViewController = self.storyboard?.instantiateViewController(withIdentifier: "ClinicalSummaryTrasmitViewController") as! ClinicalSummaryTrasmitViewController
            
            let backItem = UIBarButtonItem()
            backItem.title = "Back"
            navigationItem.backBarButtonItem = backItem
            trasmitViewController.documentIDs = getSelections()
            self.navigationController?.pushViewController(trasmitViewController, animated: true)
        } else {
            GenericHelper.shared.showNoAccessMessage(view: nil)
        }
    }
    
    @IBAction func cancelTapped(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_GOTO_CANCEL_TAP)

        selectionModeButtonTapped(UIButton())
        
        selectionModeButton.isEnabled = true
        filterButton.isEnabled = true
        
        BtnView.isHidden = true
    }
    
    func periodSelected(fromDate: String, toDate: String) {
        
        filterMode = true
        filterButton.image = #imageLiteral(resourceName: "filter_off")
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_FILTER_APPLY_TAP)
        fetchClinicalSummaries(withFromDate: fromDate, andToDate: toDate)
        tableView.refreshControl = nil
    }
    
    func changeSelection(toState:Bool) {
        let rows = tableView.numberOfRows(inSection: 1)
        for i in 1...rows {
            if let cell = tableView.cellForRow(at: IndexPath(row: i, section: 1)) as? ClinicalSummaryTableViewCell {
                if toState {
                    cell.accessoryType = .checkmark
                } else {
                    cell.accessoryType = .none
                }
                cell.setSelectionStatusTo(state: toState)
            }
        }
    }
    
    func changeButtonsState() {
        var state = false
        let rows = tableView.numberOfRows(inSection: 1)
        for i in 1...rows {
            if let cell = tableView.cellForRow(at: IndexPath(row: i, section: 1)) as? ClinicalSummaryTableViewCell {
                if cell.isSelectedRec {
                    state = true
                    break
                }
            }
        }
        
        if downloadButton.isEnabled != state {
            downloadButton.isEnabled = state
            transitButton.isEnabled = state
        }
    }
    
    func cancelledPeriodSelection() {
    }
}

// MARK: Table View Data Source

extension ClinicalSummaryViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        if inSelectionMode {
            return 2
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if inSelectionMode && section == 0 {
            return 1
        }
        if inSelectionMode {
            return medDocsManager.clinicalSummaryList.count > 0 ? self.medDocsManager.clinicalSummaryList.count+1 : 0 //+1 for select all
        }
        
        return self.medDocsManager.clinicalSummaryList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if inSelectionMode && indexPath.section == 0 {
            let cell: MoreSelectionHeaderTableViewCell = tableView.dequeueReusableCell(withIdentifier: "moreSelectionHeaderCell") as! MoreSelectionHeaderTableViewCell
            
            cell.isUserInteractionEnabled = false
            return cell
        } else if inSelectionMode && indexPath.section == 1 && indexPath.row == 0 {
            //select all cell
            let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummariesTableViewCell") as! ClinicalSummaryTableViewCell
            
            cell.titleLabel.text = ClinicalSummaryUIConstants.deSelectAll
            cell.isSelectedRec = true
            cell.createdLabel.text = ""
            cell.accessoryType = .none
            cell.createLabelHtConst.constant = 0.0
            selectCell = cell

            return cell
        } else if inSelectionMode {
            let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummariesTableViewCell") as! ClinicalSummaryTableViewCell
            
            cell.titleLabel.text = self.medDocsManager.clinicalSummaryList[indexPath.row-1].csTitle
            cell.createdLabel.text = "Created \(self.medDocsManager.clinicalSummaryList[indexPath.row-1].getSlashFormattedCreationDate())"
            cell.accessoryType = inSelectionMode ? .checkmark : .disclosureIndicator
            cell.isSelectedRec = true
            cell.id = self.medDocsManager.clinicalSummaryList[indexPath.row-1].csId
            
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "clinicalSummariesTableViewCell") as! ClinicalSummaryTableViewCell
            
            cell.titleLabel.text = self.medDocsManager.clinicalSummaryList[indexPath.row].csTitle
            cell.createdLabel.text = "Created \(self.medDocsManager.clinicalSummaryList[indexPath.row].getSlashFormattedCreationDate())"
            cell.accessoryType = inSelectionMode ? .none : .disclosureIndicator
            cell.id = self.medDocsManager.clinicalSummaryList[indexPath.row].csId
            cell.createLabelHtConst.constant = 14.0
            
            return cell
        }
    }
}

extension ClinicalSummaryViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return UITableView.automaticDimension
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if inSelectionMode {

            if let cell = tableView.cellForRow(at: indexPath) as? ClinicalSummaryTableViewCell {
                
                if indexPath.row == 0 {
                    cell.titleLabel.text = cell.isSelectedRec ? ClinicalSummaryUIConstants.selectAll : ClinicalSummaryUIConstants.deSelectAll
                    
                    changeSelection(toState: !cell.isSelectedRec)
                    
                    cell.setSelectionStatusTo(state: !cell.isSelectedRec)
                    cell.accessoryType = .none
                    
                } else {
                    cell.setSelectionStatusTo(state: !cell.isSelectedRec)
                        
                    if !cell.isSelectedRec && selectCell.isSelectedRec {
                        selectCell.titleLabel.text = ClinicalSummaryUIConstants.selectAll
                        selectCell.setSelectionStatusTo(state: !selectCell.isSelectedRec)
                    }
                }
                changeButtonsState()
            }
        } else {
            tableView.deselectRow(at: indexPath, animated: true)
            self.selectedCell = indexPath
            performSegue(withIdentifier: CLINICALSUMMARY_DETAIL_SEGUE, sender: nil)
        }
    }
}

extension ClinicalSummaryViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "myCTCA Medical Documents: Clinical Summaries"
        
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
        if medDocsManager.clinicalSummaryList.count == 0 {
        self.tableView.reloadData()
        }
    }
}
