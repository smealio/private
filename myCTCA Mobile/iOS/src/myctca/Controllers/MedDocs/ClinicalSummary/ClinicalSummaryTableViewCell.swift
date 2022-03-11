//
//  ClinicalSummaryTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/14/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class ClinicalSummaryTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var createdLabel: UILabel!
    @IBOutlet weak var createLabelHtConst: NSLayoutConstraint!
    
    var isSelectedRec = false
    var id = ""
    
    func setSelectionStatusTo(state:Bool) {
        if  !state {
            isSelectedRec = false
            setSelected(false, animated: true)
            accessoryType = .none
        } else {
            isSelectedRec = true
            setSelected(true, animated: true)
            accessoryType = .checkmark
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
