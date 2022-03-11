//
//  PrescriptionRefillSubjectTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillSubjectTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    @IBOutlet weak var subjectLabel: UILabel!
    @IBOutlet weak var subjectInput: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ subjectTxt: String) {
        subjectInput.text = subjectTxt
    }

    func isValid() -> Bool {
        let subjectTxt:String = subjectInput.text!
        
        if (subjectTxt != "") {
            subjectLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        subjectLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.subjectInput.text!
    }
}
