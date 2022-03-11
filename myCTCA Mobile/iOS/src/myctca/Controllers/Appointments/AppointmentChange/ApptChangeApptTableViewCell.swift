//
//  ApptChangeApptTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptChangeApptTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    @IBOutlet weak var apptLabel: UILabel!
    @IBOutlet weak var apptContentLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setContent(_ txt: String) {
        self.apptContentLabel.text = txt
    }
    
    func isValid() -> Bool {
        let apptTxt:String = apptContentLabel.text!
        print("ApptChangeApptTableViewCell isValid fromTxt: \(apptTxt)")
        if (apptTxt != "") {
            apptLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        apptLabel.textColor = UIColor.red
        return false
    }

    func getData() -> String {
        return self.apptContentLabel.text!
    }
}
