//
//  PrescriptionRefillToTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillToTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    @IBOutlet weak var toLabel: UILabel!
    @IBOutlet weak var toInput: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ toTxt: String) {
        toInput.text = toTxt
    }
    
    func isValid() -> Bool {
        let toTxt:String = toInput.text!
        
        if (toTxt != CTCAUIConstants.placeHolderString) {
            toLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        toLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.toInput.text!
    }
}
