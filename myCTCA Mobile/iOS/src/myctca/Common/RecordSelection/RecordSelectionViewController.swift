//
//  RecordSelectionViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 11/28/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

protocol RecordSelectionDelegate : AnyObject {
    func didMakeSelection(value:Any, type: SelectionType)
}

enum SelectionType: Int {
    case areaOfConcern = 0
    case treatmentFacility
    case patients
    case others
}

class RecordSelectionViewController: UIViewController, CTCAViewControllerProtocol, UITableViewDelegate, UITableViewDataSource {
    
    weak var delegate: RecordSelectionDelegate?
    
    var selectionType: SelectionType = .areaOfConcern
    
    @IBOutlet weak var tableView: UITableView!
    
    var areasOfConcern = ["Billing", "Care Management", "Medical Records", "Registration", "Scheduling", "Technical Issues", "Other"]
    
    var recordsList = [String]()
    var treatmentFacilities = [(String,String,String)]()
    var patientsList = [(String,String)]()
    var useForROI = false
    var choices: [Any] = [Any]()
    var selectedOption = ""
    var titleString = ""
    
    var singleSelectionHeaderText: String = "Please select one of the items below: "
    
    let cellDefaultHeight: CGFloat = 60.0
    let cellHeaderHeight: CGFloat = 55.0
    
    let HEADER_ID: String = "HeaderCell"
    let TITLE_ID = "TitleCell"
    let TITLE_SUB_ID = "TitleSubCell"
    
    let authenticationManager = AuthenticationManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.tableFooterView = UIView()
        
        loadViewData()
    }
    
    override func loadViewData() {
        if  (selectionType == .treatmentFacility) {
            NetworkStatusManager.shared.registerForReload(view: self)
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            if !useForROI && AppSessionManager.shared.currentUser.allFacilitesNamesList.count == 0 {
                fetchFacList()
            } else {
                prepareDisplayView()
            }
        } else if selectionType == .areaOfConcern {
            self.title = "Area of Concern"
            choices = areasOfConcern
        } else if selectionType == .patients {
            self.title = "Select Patient"
            singleSelectionHeaderText = "Please select the account you want to view"
            choices = patientsList
        } else {
            self.title = titleString
            choices = recordsList
        }
    }
    
    func prepareDisplayView() {
        if !useForROI {
            let facList = AppSessionManager.shared.currentUser.allFacilitesNamesList
            var facilities = [(String,String,String)]()
            for item in facList {
                facilities.append((item.value, item.key, item.value))
            }
            self.treatmentFacilities = facilities
        }
        
        DispatchQueue.main.async(execute: {
            
            self.title = "Treatment Facility"
            self.choices = self.treatmentFacilities
            
            self.tableView.reloadData()
        })
        
    }
    
    
    func fetchFacList() {
        showActivityIndicator(view: self.view, message:"Loading…")
            
        authenticationManager.fetchAllFacilites() {
            status in
            self.fadeOutActivityIndicator()
            if status == .SUCCESS {
                self.prepareDisplayView()
            } else {
                 let alert = self.ctcaInfoAlert(title:NSLocalizedString("NetworkingDataProblemTitle", comment: "Title on alert pop-up when there is an error retrieving information."),
                                                message: NSLocalizedString("NetworkingDataProblem", comment: "Error message displayed when there is a problem retrieving data from CTCA services."),
                                                okaction: nil,
                                                otheraction: nil)
                 self.present(alert,
                              animated: true,
                              completion: nil)
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source and delegate

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return choices.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (selectionType == .areaOfConcern) {
            let concernCell: SelectionTitleTableViewCell = tableView.dequeueReusableCell(withIdentifier: TITLE_ID, for: indexPath) as! SelectionTitleTableViewCell
            
            let choice:String = self.choices[indexPath.row] as! String
            if choice == selectedOption {
                concernCell.accessoryType = .checkmark
            }
            
            concernCell.titleLabel!.text = choice
            return concernCell
        } else if selectionType == .treatmentFacility {
            let tfCell: SelectionTitleSubTableViewCell = tableView.dequeueReusableCell(withIdentifier: TITLE_SUB_ID, for: indexPath) as! SelectionTitleSubTableViewCell
            
            let choice: (String, String, String) = self.choices[indexPath.row] as! (String, String, String)
            tfCell.titleLabel!.text = choice.0
            tfCell.subTitleLabel!.text = choice.1
            
            if choice.0 == selectedOption {
                tfCell.accessoryType = .checkmark
            } else {
                if let indx = selectedOption.lastIndex(of: ",") {
                    if choice.0 == selectedOption[selectedOption.startIndex ..< indx] {
                        tfCell.accessoryType = .checkmark
                    }
                }
            }

            return tfCell
        } else if selectionType == .patients {
            let tfCell: SelectionTitleSubTableViewCell = tableView.dequeueReusableCell(withIdentifier: TITLE_SUB_ID, for: indexPath) as! SelectionTitleSubTableViewCell
            
            let choice: (String, String) = self.choices[indexPath.row] as! (String, String)
            tfCell.titleLabel!.text = choice.0
            tfCell.subTitleLabel!.text = ""

            if choice.0 == selectedOption {
                tfCell.accessoryType = .checkmark
            } else {
                if let indx = selectedOption.lastIndex(of: ",") {
                    if choice.0 == selectedOption[selectedOption.startIndex ..< indx] {
                        tfCell.accessoryType = .checkmark
                    }
                }
            }

            return tfCell
        } else {
            let tfCell: SelectionTitleSubTableViewCell = tableView.dequeueReusableCell(withIdentifier: TITLE_SUB_ID, for: indexPath) as! SelectionTitleSubTableViewCell
            
            let choice = self.choices[indexPath.row] as! (String)
            tfCell.titleLabel!.text = choice
            tfCell.subTitleLabel!.text = ""

            if choice == selectedOption {
                tfCell.accessoryType = .checkmark
            } else {
                if let indx = selectedOption.lastIndex(of: ",") {
                    if choice == selectedOption[selectedOption.startIndex ..< indx] {
                        tfCell.accessoryType = .checkmark
                    }
                }
            }

            return tfCell
        }
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return cellHeaderHeight
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header:SendMessageSelectionHeaderCell = self.tableView.dequeueReusableCell(withIdentifier: HEADER_ID) as! SendMessageSelectionHeaderCell
        header.headerLabel.text = singleSelectionHeaderText
        
        return header
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        if(delegate != nil) {
            if selectionType == .patients {
                let selection: (String, String) = self.choices[indexPath.row] as! (String, String)
                self.delegate!.didMakeSelection(value: selection, type: selectionType)
            } else {
                var choice: String = ""
                if (selectionType == .areaOfConcern) {
                    choice = self.choices[indexPath.row] as! String
                } else if (selectionType == .treatmentFacility) {
                    let selection: (String, String, String) = self.choices[indexPath.row] as! (String, String, String)
                    choice = selection.2
                } else {
                    choice = self.choices[indexPath.row] as! String
                }
                self.delegate!.didMakeSelection(value: choice, type: selectionType)
            }
        }
        _ = navigationController?.popViewController(animated: true)
    }
}
