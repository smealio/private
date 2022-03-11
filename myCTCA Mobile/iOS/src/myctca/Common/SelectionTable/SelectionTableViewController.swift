//
//  SelectionTableViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/31/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

enum SelectionTableType: Int {
    case single = 0
    case multi = 1
}

@objc protocol SelectionTableDelegate : AnyObject {
    
    @objc optional func didMakeSingleSelection(title: String, value:String)
    @objc optional func didMakeMultiSelections(title: String, choices:[String:Bool])
}

class SelectionTableViewController: UITableViewController {

    weak var delegate: SelectionTableDelegate?
    
    var choices: [String] = [String]()
    var viewTitle: String = ""
    
    var tableSelectionType: SelectionTableType = .single
    
    var itemsSelected:[String:Bool]?
    
    let CELL_ID: String = "SelectionCell"
    let HEADER_ID: String = "SelectionTableHeaderCell"
    
    let cellDefaultHeight: CGFloat = 60.0
    let cellHeaderHeight: CGFloat = 55.0
    
    let singleSelectionHeaderText: String = "Please select one of the items below: "
    let multiSelectionHeaderText: String = "Please select all items below that apply: "
    
    var selectedElements = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.tableFooterView = UIView()
        self.tableView.backgroundColor = UIColor.white
        
        self.tableView.estimatedRowHeight = cellDefaultHeight
        self.tableView.rowHeight = UITableView.automaticDimension
        
        self.title = self.viewTitle
        
        if (tableSelectionType == .multi) {
            self.tableView.allowsMultipleSelection = true
            // add a done button
            let rightButtonItem = UIBarButtonItem.init(
                title: "Done",
                style: .done,
                target: self,
                action: #selector(doneButtonTapped(sender:))
            )
            self.navigationItem.rightBarButtonItem = rightButtonItem
            
            itemsSelected = [String:Bool]()
            for choice in choices {
                itemsSelected = [choice:false];
            }
        }
    }
    
    @objc func doneButtonTapped(sender:UIBarButtonItem) {
        // Can only be called from multi selection table
        print("SELECTION DONE BUTTON TAPPED")
        self.delegate!.didMakeMultiSelections!(title: self.viewTitle, choices: itemsSelected!)
        _ = navigationController?.popViewController(animated: true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return choices.count
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return UITableView.automaticDimension
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return cellHeaderHeight
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = self.tableView.dequeueReusableCell(withIdentifier: HEADER_ID) as! SelectionTableHeaderCell
        if (section == 0) {
            if (tableSelectionType == .single) {
                header.headerLabel.text = singleSelectionHeaderText
            } else {
                header.headerLabel.text = multiSelectionHeaderText
            }
        }
        return header
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: CELL_ID, for: indexPath) as! SelectionTableViewCell
        
        let choice = self.choices[indexPath.row]
        
        cell.setLabelText(choice)
        if selectedElements.contains(where: choice.contains) {
            cell.accessoryType = .checkmark
            itemsSelected![cell.selectionLabel.text!] = true
        }
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        
        if (tableSelectionType == .multi) {
            let cell:SelectionTableViewCell = tableView.cellForRow(at: indexPath) as! SelectionTableViewCell
            if (cell.accessoryType == .none) {
                cell.accessoryType = .checkmark
                itemsSelected![cell.selectionLabel.text!] = true
            } else {
                cell.accessoryType = .none
                itemsSelected![cell.selectionLabel.text!] = false
            }
        } else {
            if(delegate != nil) {
                self.delegate!.didMakeSingleSelection!(title: self.viewTitle, value: self.choices[indexPath.row])
            }
            _ = navigationController?.popViewController(animated: true)
        }
    }
    
    override func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        print("DESELECTED")
    }
}
