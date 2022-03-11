//
//  MoreMedDocViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/24/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreMedDocViewController: CTCABaseViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    let cellHeight:CGFloat = 50.0
    
    var medDocs: [String] =  [String]()

    var selectedIndexPath: IndexPath?
    
    let MEDDOC_CAREPLAN_SEGUE: String = "MedDocCarePlanSegue"
    let MEDDOC_CLINICALSUMMARY_SEGUE: String = "MedDocClinicalSummarySegue"
    let MEDDOC_DOCLIST_SEGUE: String = "MedDocDocListSegue"
    
    let CARE_PLAN = "Care Plan"
    let CLINICAL_SUMMARIES = "Clinical Summaries"
    let CLINICAL = "Clinical"
    let RADIATION = "Radiation"
    let IMAGING = "Imaging"
    let INTEGRATIVE = "Integrative"
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Set Title
        self.title = "Medical Documents"
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        // Set tableview for empty data sets and
        // an empty footer to clear extra cells
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        buildMedDocData()
    }
    
    func buildMedDocData() {
        medDocs.removeAll()
        //["Care Plan", "Clinical Summaries", "Clinical", "Radiation", "Imaging", "Integrative"]
        if GenericHelper.shared.hasPermissionTo(feature: .viewCarePlan) {
            medDocs.append(CARE_PLAN)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewCCDADocuments) {
            medDocs.append(CLINICAL_SUMMARIES)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewClinicalDocuments) {
            medDocs.append(CLINICAL)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewRadiationDocuments) {
            medDocs.append(RADIATION)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewImagingDocuments) {
            medDocs.append(IMAGING)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewIntegrativeDocuments) {
            medDocs.append(INTEGRATIVE)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == MEDDOC_CAREPLAN_SEGUE {
            let backItem = UIBarButtonItem()
            backItem.title = "Med Docs"
            navigationItem.backBarButtonItem = backItem
        }
        if segue.identifier == MEDDOC_CLINICALSUMMARY_SEGUE {
            let backItem = UIBarButtonItem()
            backItem.title = "Med Docs"
            navigationItem.backBarButtonItem = backItem
        }
        if segue.identifier == MEDDOC_DOCLIST_SEGUE {
            let backItem = UIBarButtonItem()
            backItem.title = "Med Docs"
            navigationItem.backBarButtonItem = backItem
            
            let destinationController = segue.destination as! MedDocListViewController
            let selectedDocs = medDocs[self.selectedIndexPath!.row]
            switch (selectedDocs) {
            case CLINICAL:
                destinationController.medDocType = .clinical
            case RADIATION:
                destinationController.medDocType = .radiation
            case IMAGING:
                destinationController.medDocType = .imaging
            case INTEGRATIVE:
                destinationController.medDocType = .integrative
            default:
                break
            }
        }
    }
}

// MARK: Table View Data Source

extension MoreMedDocViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return self.medDocs.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "moreMedDocTableViewCell") as! MoreMedDocTableViewCell
        
        cell.docTypeLabel.text = self.medDocs[indexPath.row]
        
        return cell
    }
}

extension MoreMedDocViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return cellHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        self.selectedIndexPath = indexPath
        
        let selectedDocs = medDocs[indexPath.row]
        
        switch (selectedDocs) {
            
        case CARE_PLAN:
            performSegue(withIdentifier: MEDDOC_CAREPLAN_SEGUE, sender: nil)
        case CLINICAL_SUMMARIES:
            performSegue(withIdentifier: MEDDOC_CLINICALSUMMARY_SEGUE, sender: nil)
        case CLINICAL, RADIATION, INTEGRATIVE:
            performSegue(withIdentifier: MEDDOC_DOCLIST_SEGUE, sender: nil)
        case IMAGING:
            performSegue(withIdentifier: MEDDOC_DOCLIST_SEGUE, sender: nil)
        default:
            break;
        }
    }
    
}

extension MoreMedDocViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "Medical Documents"
        
        let attrs = [NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: UIFont.TextStyle.headline),
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color]
        return NSAttributedString(string: str, attributes: attrs)
    }
    
    func description(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        
        let str = "You don't have permissions to view any medical documents for this patient."
        
        let style = NSMutableParagraphStyle()
        style.alignment = NSTextAlignment.center
        
        let attrs = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 14.0),
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
        let rowCount = self.tableView.numberOfRows(inSection: 0)
        if rowCount == 0 {
            self.tableView.reloadData()
        }
    }
}
