//
//  SymptomInventoryViewController.swift
//  myctca
//
//  Created by Manjunath K on 3/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class SymptomInventoryViewController: CTCABaseViewController {
    
    @IBOutlet weak var tableView: UITableView!
    private let patientReportedManager = PatientReportedManager()
    
    let headerHeight: CGFloat = 44.0
    var hiddenSections = Set<Int>()
    
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
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        fetchSymptomInventory()
    }
    
    private func fetchSymptomInventory() {
        
        self.showActivityIndicator(view: self.view, message: "Loading Symptom Inventories...")
        
        patientReportedManager.fetchSymptomInventory() {
            status in
            
            self.fadeOutActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.patientReportedManager.getLastServerError(), onView: self)
            } else {
                DispatchQueue.main.async(execute: {
                    self.tableView.reloadData()
                })
            }
        }
    }
}


// MARK: Table View Data Source

extension SymptomInventoryViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return patientReportedManager.symptomsDates.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.hiddenSections.contains(section) {
            return 0
        }
        
        if section < patientReportedManager.symptomsDates.count {
            let date = patientReportedManager.symptomsDates[section]
            return patientReportedManager.symptomsDocs[date]!.count
        }
        
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SymptomsTableViewCell") as! SymptomsTableViewCell
        
        if indexPath.section < patientReportedManager.symptomsDates.count {
            let date = patientReportedManager.symptomsDates[indexPath.section]
            let list = patientReportedManager.symptomsDocs[date]
            if indexPath.row < list!.count {
                cell.prepareView(symptom: list![indexPath.row])
                return cell
            }
        }
        
        return cell
    }
}

extension SymptomInventoryViewController: UITableViewDelegate {
    
    @objc
    private func hideSection(sender: UIButton) {
        let section = sender.tag
        let symptomsDates = self.patientReportedManager.symptomsDates
        let date = symptomsDates[section]
        func indexPathsForSection() -> [IndexPath] {
            var indexPaths = [IndexPath]()
            let symptomsDocs = self.patientReportedManager.symptomsDocs[date]
            for row in 0..<symptomsDocs!.count {
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
        return headerHeight
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let symptomsDates = self.patientReportedManager.symptomsDates
        let dayDateTime: String = getDayDateTime(date: symptomsDates[section])
        
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
    
    func getDayDateTime(date: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMMM dd, yyyy"
        let dateString: String = dateFormatter.string(from: date)
        return dateString
    }
}

extension SymptomInventoryViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "myCTCA Symptom Inventory"
        
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
        if self.patientReportedManager.symptomsDates.count == 0 {
            self.tableView.reloadData()
        }
    }
}
