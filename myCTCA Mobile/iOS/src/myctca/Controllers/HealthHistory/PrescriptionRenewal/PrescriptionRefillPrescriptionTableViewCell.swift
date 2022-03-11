//
//  PrescriptionRefillPrescriptionTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillPrescriptionTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    @IBOutlet weak var prescriptionLabel: UILabel!
    @IBOutlet weak var prescriptionInput: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func prepareView(_ prescriptionTxt: String) {
        prescriptionInput.text = prescriptionTxt
    }

    func isValid() -> Bool {
        let prescriptionTxt:String = prescriptionInput.text!
        
        if (prescriptionTxt != "") {
            prescriptionLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        prescriptionLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.prescriptionInput.text!
    }
}
