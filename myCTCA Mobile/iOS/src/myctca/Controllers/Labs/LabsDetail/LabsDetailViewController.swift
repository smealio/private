//
//  LabsDetailViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/2/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class LabsDetailViewController: UIViewController, CTCAViewControllerProtocol {
    
    var labResult: LabResult?
    
    let cellHeaderHeight: CGFloat = 44.0
    let cellDefaultHeight: CGFloat = 90.0
    
    @IBOutlet weak var tableView: UITableView!
    let labsManager = LabsManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //        self.view.backgroundColor = MyCTCAColor.superLightGrey.color
        // Title
        self.title = "Lab Results - \(String(describing: (labResult?.getPerformedDateStringWithSlashes())!))"
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        self.tableView.estimatedRowHeight = cellDefaultHeight
        self.tableView.rowHeight = UITableView.automaticDimension
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_LAB_RESULT_DETAIL_VIEW)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func didPerformAction(state: Bool) {
        if state {
            print("Download Tapped")
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            downloadLabReports()
            
        } else {
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    @IBAction func downloadTapped(_ sender: Any) {
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        downloadLabReports()
    }
    
    @IBAction func cancelTapped(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    func downloadLabReports() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_LAB_RESULT_DOWNLOAD_TAP)
        
        if let currentReport = labResult {
            self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.downloadLabResultsText)
            
            labsManager.downloadLabReport(report: currentReport) {
                filepath, status in
                
                self.dismissActivityIndicator()

                if status == .SUCCESS, let fileurl = URL(string: filepath!) {
                    self.showPDF(fileURL: fileurl)
                } else {
                    //display error
                    ErrorManager.shared.showServerError(error: self.labsManager.getLastServerError(), onView: self)
                }
            }
        }
    }
}


extension LabsDetailViewController {

    func showPDF(fileURL:URL) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.PAGE_LAB_RESULT_PDF_VIEW)

        DispatchQueue.main.async {[weak self] in
            
            guard let self = self else { return }
            
            let pdfViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PDFViewController") as! PDFViewController
            
            pdfViewController.fileURL = fileURL
            self.present(pdfViewController, animated: true, completion: nil)
            pdfViewController.titleLabel.text = MyCTCAConstants.FileNameConstants.LabReportsPDFName
            pdfViewController.pdfDocType = .labResults
        }
    }
}



// MARK: Table View Data Source

extension LabsDetailViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        // There is an extra section for the disclaimer AND + 1 for last download cell
        return labResult!.labSets.count + 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if (section < labResult!.labSets.count) {
            let labSetDetails: [LabSetDetail] = labResult!.labSets[section].details
            return labSetDetails.count
        }
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        //Disclaimer Cell
        if (indexPath.section == labResult!.labSets.count) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "labsDetailDisclaimerTableViewCell") as! LabsDetailDisclaimerTableViewCell
            
            var dTypes: [DisclaimerType] = [DisclaimerType]()
            if (labResult!.collectedBy != "CTCA" ) {
                dTypes.append(.externalSource)
            }
            if (labResult!.isLessThan24HoursAgo() ) {
                dTypes.append(.twentyFourHours)
            }
            dTypes.append(.basic)
            cell.prepareView(disclaimers: dTypes)
            
            return cell
        } else {
            // Regular Detail Cells
            let labSetDetails: [LabSetDetail] = labResult!.labSets[indexPath.section].details
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "labsDetailTableViewCell") as! LabsDetailTableViewCell
            cell.prepareView(labSetDetails[indexPath.row])
            
            return cell
        }
    }
}

extension LabsDetailViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        
        if (section < labResult!.labSets.count) {
            return cellHeaderHeight
        }
        return CGFloat.leastNonzeroMagnitude
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        if (section < labResult!.labSets.count) {
            let labSet = labResult!.labSets[section]
            
            let cell = LabsSectionCell()
            cell.prepareView("LAB SET: \(labSet.name)")
            return cell
        }
        return nil
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}
