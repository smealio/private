//
//  LabResultsSectHeader.swift
//  myctca
//
//  Created by Manjunath K on 8/3/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

protocol LabResultsSectHeaderViewProtocols: AnyObject {
    func filterListBy(text: String)
    func resetList()
    func didStartedSearchMode()
    func didEndedSearchMode()
}

class LabResultsSectHeader: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var searchButton: UIButton!
    @IBOutlet weak var searchTextField: UITextField!
    
    let searchController = UISearchController(searchResultsController: nil)
    var isSearchMode = false
    
    weak var delegate: LabResultsSectHeaderViewProtocols?
    
    func prepareView() {        
        searchTextField.isHidden = true
        searchButton.setTitle("", for: .normal)
        searchButton.setImage(#imageLiteral(resourceName: "magnifying"), for: .normal)
        titleLabel.isHidden = false
        
        searchTextField.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
        contentView.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
    }

    @IBAction func searchBttunTapped(_ sender: Any) {
        guard let del = delegate else {
            return
        }
        
        isSearchMode = !isSearchMode
        
        print("Search tapped!!")
        
        if isSearchMode {
            searchTextField.isHidden = false
            searchButton.setTitle("Cancel", for: .normal)
            searchButton.setImage(nil, for: .normal)
            titleLabel.isHidden = true
            del.didStartedSearchMode()
            contentView.backgroundColor = .clear
        } else {
            searchTextField.isHidden = true
            searchButton.setTitle("", for: .normal)
            searchButton.setImage(#imageLiteral(resourceName: "magnifying"), for: .normal)
            titleLabel.isHidden = false
            del.didEndedSearchMode()
            contentView.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
            searchTextField.resignFirstResponder()
        }
    }
    
    @objc func textFieldDidChange(_ textField: UITextField) {
        if let text = textField.text,
           let del = delegate {
            
            if text.isEmpty {
                del.resetList()
            } else {
                del.filterListBy(text: text)
            }
        }
    }
}

