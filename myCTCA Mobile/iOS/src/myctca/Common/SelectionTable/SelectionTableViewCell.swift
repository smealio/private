//
//  SelectionTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/31/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class SelectionTableViewCell: UITableViewCell {

    @IBOutlet weak var selectionLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
    func setLabelText(_ txt: String) {
        selectionLabel.text = txt
    }

}
