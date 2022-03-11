//
//  ApptChangeDateLabelTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/14/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class ApptChangeDateLabelTableViewCell: UITableViewCell {

    @IBOutlet weak var apptDateLabel: UILabel!
    @IBOutlet weak var apptDateContentLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
    func setContent(_ txt: String) {
        self.apptDateContentLabel.text = txt
    }
    
    func isValid() -> Bool {
        let apptTxt:String = apptDateContentLabel.text!
        print("ApptChangeApptTableViewCell isValid fromTxt: \(apptTxt)")
        if (apptTxt != "") {
            apptDateLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        apptDateLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.apptDateContentLabel.text!
    }

}
