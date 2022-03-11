//
//  DiscloseInfoTableViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/30/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

@objc protocol DiscloseInfoSelectDelegate : AnyObject {
    @objc optional func didSelectGeneralInfo(displayString:String, data: [String], other:String?)
    @objc optional func didSelectConfidentialInfo(value:String, data: [String])
    @objc optional func didSelectCareTeams(value:String, data: [String])
}

enum DiscloseInfoType: String {
    case general = " General Information"
    case confidential = " Highly Confidential Information"
    case careTeams = " Care Teams"
}

class DiscloseInfoTableViewController: UITableViewController, UITextViewDelegate {

    weak var delegate: DiscloseInfoSelectDelegate?
    
    let headerHeight: CGFloat = 44.0
    let cellDefaultHeight: CGFloat = 44.0
    let infoOtherCellHeight: CGFloat = 90.0
    
    let SELECT_ALL: String = "Select All"
    let DESELECT_ALL: String = "Deselect All"
    let discloseOtherPlaceholder : String = "Other (specify)"
    
    var allSelected: Bool = false
    
    var discloseType: DiscloseInfoType = .general
    
    var discloseInfoAR: [(name: String, selected: Bool)] = [(name: String, selected: Bool)]()
    
    var discloseOtherText: String = ""
    
    var generalInfoAR: [(name: String, selected: Bool)] = [(String,Bool)]()
    var confidentialInfoAR: [(name: String, selected: Bool)] = [(String,Bool)]()
    var careTeamsList: [(name: String, selected: Bool)] = [(String,Bool)]()

    var customTitle = ""
    var customNote = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if (discloseType == .general) {
            discloseInfoAR = generalInfoAR
        } else if (discloseType == .confidential){
            discloseInfoAR = confidentialInfoAR
        } else {
            discloseInfoAR = careTeamsList
        }
        
        self.tableView.tableFooterView = UIView()
        self.tableView.backgroundColor = UIColor.white
        
        if customTitle != "" {
            self.title = customTitle
        }
    }
    
    func getSelectCellText() -> String {
        
        var checkAllSelected = true
        for item in self.discloseInfoAR {
            if (item.selected == false) {
                checkAllSelected = false
                break
            }
        }
        self.allSelected = checkAllSelected
        
        if (self.allSelected == true) {
            return DESELECT_ALL
        }
        return SELECT_ALL
    }

    @IBAction func doneButtonTapped(_ sender: Any) {
        self.tableView.endEditing(true)
        if self.delegate != nil {
            // Generate a comma separated string
            var commaSeparatedStr: String = ""
            var dataArray: [String] = [String]()
            for item in self.discloseInfoAR {
                if (item.selected == true && item.name != "Other") {
                    if (commaSeparatedStr == "") {
                        commaSeparatedStr += item.name
                    } else {
                        commaSeparatedStr += ", \(item.name)"
                    }
                    dataArray.append(item.name)
                }
            }
            if (discloseType == .general) {
            // Check for other
                if (self.discloseOtherText != "") {
                    if (commaSeparatedStr == "") {
                        commaSeparatedStr += self.discloseOtherText
                    } else {
                        commaSeparatedStr += ", \(self.discloseOtherText)"
                    }
                }
                print("commaSeparatedStr: \(commaSeparatedStr)")
            }

            if (discloseType == .general) {
                if (discloseOtherText == "") {
                    if let delFunc = self.delegate?.didSelectGeneralInfo {
                        delFunc(commaSeparatedStr, dataArray, nil)
                    }
                } else {
                    if let delFunc = self.delegate?.didSelectGeneralInfo {
                        delFunc(commaSeparatedStr, dataArray, self.discloseOtherText)
                    }
                }
            } else if (discloseType == .confidential) {
                if let delFunc = self.delegate?.didSelectConfidentialInfo {
                    delFunc(commaSeparatedStr, dataArray)
                }
            } else {
                if let delFunc = self.delegate?.didSelectCareTeams {
                    delFunc(commaSeparatedStr, dataArray)
                }
            }
            _ = navigationController?.popViewController(animated: true)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch(section) {
        case 0:
            return 1 //sub title
        case 1://select/deselect section
            if discloseInfoAR.count > 1 {
                return 1
            } else {
                return 0
            }
        case 2:
            if (discloseType == .general) {
                return discloseInfoAR.count + 1 // +1 for others
            } else {
                return discloseInfoAR.count
            }
        default:
            return 0
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        if (discloseType == .general) {
            if (indexPath.section == 2) {
                if (indexPath.row == discloseInfoAR.count) {
                    return infoOtherCellHeight
                }
            }
        }
        if (discloseType == .confidential) {
             return UITableView.automaticDimension
        }
        if (discloseType == .careTeams) {
             return UITableView.automaticDimension
        }
        return cellDefaultHeight
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if (indexPath.section == 0) {
            let cell: MoreSelectionHeaderTableViewCell = tableView.dequeueReusableCell(withIdentifier: "moreSelectionHeaderCell") as! MoreSelectionHeaderTableViewCell
            
            if (discloseType == .confidential) {
                cell.headerLabel.text = "Uncheck selections below as desired."
            } else if discloseType == .careTeams {
                cell.headerLabel.text = "Choose Recipients."
            }
            
            return cell
        } else if (indexPath.section == 1) {
            let cell: DiscloseInfoTableViewCell = tableView.dequeueReusableCell(withIdentifier: "DisclosedInfoCell") as! DiscloseInfoTableViewCell
            cell.discloseInfoLabel.text = getSelectCellText()
            cell.accessoryType = .none
            return cell
        } else if (indexPath.section == 2) {
            if (indexPath.row < discloseInfoAR.count) {
                let cell: DiscloseInfoTableViewCell = tableView.dequeueReusableCell(withIdentifier: "DisclosedInfoCell") as! DiscloseInfoTableViewCell
                let item = discloseInfoAR[indexPath.row]
                print("item[\(indexPath.row)]: \(discloseInfoAR[indexPath.row])")
                cell.discloseInfoLabel.text = item.name
                cell.isSelected = item.selected
                if (cell.isSelected == true) {
                    cell.accessoryType = .checkmark
                    cell.discloseInfoLabel!.textColor = MyCTCAColor.formContent.color
                } else {
                    cell.accessoryType = .none
                    cell.discloseInfoLabel!.textColor = MyCTCAColor.formContent.color
                }
                return cell
            } else {
                if (discloseType == .general) {
                    let cell: DiscloseInfoSpecifyTableViewCell = tableView.dequeueReusableCell(withIdentifier: "DisclosedInfoSpecifyCell") as! DiscloseInfoSpecifyTableViewCell
                    cell.discloseInfoOtherTV.text = discloseOtherPlaceholder
                    cell.discloseInfoOtherTV.textColor = MyCTCAColor.formContent.color
                    cell.discloseInfoOtherTV.delegate = self
                    self.addDoneButtonOnKeyboard(cell.discloseInfoOtherTV)
                    return cell
                } else {
                    return UITableViewCell()
                }
            }
        }
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        if (indexPath.section == 1) {
            // Select/Deselect All
            var numRows = self.tableView.numberOfRows(inSection: 2)
            if (discloseType == .general) {
                // Don't count the last row which is for "Other"
                numRows = numRows - 1
            }
            for row in 0..<numRows {
                self.discloseInfoAR[row].selected = !self.allSelected
                
                if let cell = tableView.cellForRow(at: IndexPath(row: row, section: 2)) as? DiscloseInfoTableViewCell {
                    if (self.discloseInfoAR[row].selected == true) {
                        cell.accessoryType = .checkmark
                        cell.discloseInfoLabel!.textColor = MyCTCAColor.formContent.color
                    } else {
                        cell.accessoryType = .none
                        cell.discloseInfoLabel!.textColor = MyCTCAColor.formContent.color
                    }
                }
            }
            self.allSelected = true
            if let cell = tableView.cellForRow(at: indexPath) as? DiscloseInfoTableViewCell {
                cell.discloseInfoLabel.text = getSelectCellText()
            }
        } else {
            if ( indexPath.row < discloseInfoAR.count) {
                if (self.discloseInfoAR[indexPath.row].selected == true) {
                    //Deselecting item
                    self.discloseInfoAR[indexPath.row].selected = false
                    if let cell = tableView.cellForRow(at: indexPath) as? DiscloseInfoTableViewCell {
                        cell.accessoryType = .none
                        cell.discloseInfoLabel!.textColor = MyCTCAColor.formContent.color
                    }
                } else {
                    // Selecting item
                    self.discloseInfoAR[indexPath.row].selected = true
                    if let cell = tableView.cellForRow(at: indexPath) as? DiscloseInfoTableViewCell {
                        cell.accessoryType = .checkmark
                        cell.discloseInfoLabel!.textColor = MyCTCAColor.formContent.color
                    }
                }
                if let selectCell = tableView.cellForRow(at: IndexPath(row: 0, section: 1)) as? DiscloseInfoTableViewCell {
                    selectCell.discloseInfoLabel.text = getSelectCellText()
                }
            }
        }
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 || section == 2 {
            return 0
        }
        return headerHeight
    }
    
    override func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        let header = view as! UITableViewHeaderFooterView
        
        let label = UILabel()
        
        header.addSubview(label)
        
        label.translatesAutoresizingMaskIntoConstraints = false
        label.backgroundColor = UIColor.clear
        label.isUserInteractionEnabled = false
        label.numberOfLines = 1
        label.font = UIFont(name: "HelveticaNeue", size: 13)
        label.minimumScaleFactor = 0.75
        label.adjustsFontSizeToFitWidth = true
        label.textColor = .white
        label.lineBreakMode = .byTruncatingTail
        
        var sectionTitle = ""
        switch (section) {
        case 0:
            sectionTitle = " "
        case 1:
            switch discloseType {
            case .careTeams:
                sectionTitle = "CARE TEAMS"
            case .confidential:
                sectionTitle = "HIGHLY CONFIDENTIAL INFORMATION"
            case .general:
                sectionTitle = "GENERAL INFORMATION"
            }
        default:
            sectionTitle = ""
        }
        label.text = sectionTitle
        
        label.leadingAnchor.constraint(equalTo: header.leadingAnchor, constant: 20.0).isActive = true
        label.trailingAnchor.constraint(equalTo: header.trailingAnchor, constant: 20.0).isActive = true
        label.heightAnchor.constraint(equalToConstant: 18.0).isActive = true
        label.bottomAnchor.constraint(equalTo: header.bottomAnchor, constant:-10.0).isActive = true
        
        header.contentView.backgroundColor = MyCTCAColor.ctcaGreen.color
        header.textLabel?.text = ""
    }

    // MARK: - Text View Delegate
    func textViewDidBeginEditing(_ textView: UITextView) {
        if textView.text == discloseOtherPlaceholder {
            textView.text = nil
            textView.textColor = MyCTCAColor.formContent.color
        }
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        if textView.text.isEmpty {
            textView.text = discloseOtherPlaceholder
            textView.textColor = MyCTCAColor.formContent.color
        }
        if (textView.text != discloseOtherPlaceholder) {
            self.discloseOtherText = textView.text
        }
        print("textViewDidEndEditing text: \(String(describing: textView.text))")
    }
    
    func addDoneButtonOnKeyboard(_ textView: UITextView) {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: 320, height: 50))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        textView.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        
        self.tableView.endEditing(true)
    }
}
