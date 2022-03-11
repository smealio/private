//
//  ClinicalSummarySelectionTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 8/17/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

protocol ClinicalSummarySelectionProtocol : AnyObject {
    func didOptForPasswordProtection(state:Bool);
}

class ClinicalSummarySelectionTableViewCell: UITableViewCell {

    @IBOutlet weak var descriptionLabel: UILabel!
    
    var stateChecked = false
    
    weak var delegate:ClinicalSummarySelectionProtocol?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
    }

    @IBOutlet weak var selectionImage: UIImageView!
    
    //@IBAction func selectionButtonTapped(_ sender: Any) {
    func setState() {
        if stateChecked {
            stateChecked = false
            
            selectionImage.image = #imageLiteral(resourceName: "checkbox_empty")
        } else {
            stateChecked = true
            
            selectionImage.image = #imageLiteral(resourceName: "checkbox_checked")
        }
        
        if let del = delegate, tag == 1001 {
            del.didOptForPasswordProtection(state: stateChecked)
        }
    }
}
