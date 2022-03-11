//
//  MoreHealthHistoryViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/24/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreHealthHistoryViewController: CTCABaseViewController {

    @IBOutlet weak var tableView: UITableView!
    
    let cellHeight:CGFloat = 50.0
    
    var healthHistory: [String] = [String]()
        
    let VITALS = "Vitals"
    let PRESCRIPTIONS = "Prescriptions"
    let ALLERGIES = "Allergies"
    let IMMUNIZATIONS = "Immunizations"
    let HEALTH_ISSUES = "Health Issues"
    
    let HEALTH_HISTORY_LIST_SEGUE: String = "HealthHistoryListSegue"
    
    var healthHistoryType: HealthHistoryType?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set Title
        self.title = "Health History"
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
        
        buildHealthHistoryTableData()
    }
    
    func buildHealthHistoryTableData() {
        healthHistory.removeAll()
        //["Vitals", "Prescriptions", "Allergies", "Immunizations", "Health Issues"]
        if GenericHelper.shared.hasPermissionTo(feature: .viewVitalSigns) {
            healthHistory.append(VITALS)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewPrescriptions) {
            healthHistory.append(PRESCRIPTIONS)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewAllergies) {
            healthHistory.append(ALLERGIES)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewImmunizations) {
            healthHistory.append(IMMUNIZATIONS)
        }
        if GenericHelper.shared.hasPermissionTo(feature: .viewHealthIssues) {
            healthHistory.append(HEALTH_ISSUES)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == HEALTH_HISTORY_LIST_SEGUE {

            let destinationController = segue.destination as! MoreHealthHistoryListViewController
            print("prepare destination: \(destinationController)")
            if (self.healthHistoryType != nil) {
                destinationController.healthHistoryType = self.healthHistoryType
            }
        }
    }

}

// MARK: Table View Data Source

extension MoreHealthHistoryViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return self.healthHistory.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "moreHealthHistoryTableViewCell") as! MoreHealthHistoryTableViewCell
        cell.healthHistoryLabel.text = self.healthHistory[indexPath.row]
        return cell
    }
}

extension MoreHealthHistoryViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return cellHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        healthHistoryType = HealthHistoryType(rawValue: healthHistory[indexPath.row])
        
        performSegue(withIdentifier: HEALTH_HISTORY_LIST_SEGUE, sender: nil)
    }
}

extension MoreHealthHistoryViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "Health History"
        
        let attrs = [NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: UIFont.TextStyle.headline),
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color]
        return NSAttributedString(string: str, attributes: attrs)
    }
    
    func description(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        
        let str = "You don't have permissions to view health history data for this patient."
        
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
        if self.healthHistory.count == 0 {
            self.tableView.reloadData()
        }
    }
}

