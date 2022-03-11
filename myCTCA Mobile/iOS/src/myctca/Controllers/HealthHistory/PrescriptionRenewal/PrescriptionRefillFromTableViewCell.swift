//
//  PrescriptionRefillFromTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillFromTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    @IBOutlet weak var fromLabel: UILabel!
    @IBOutlet weak var fromInput: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ fromTxt: String) {
        fromInput.text = fromTxt
    }
    
    func isValid() -> Bool {
        let fromTxt:String = fromInput.text!
        print("isValid fromTxt: \(fromTxt)")
        if (fromTxt != "") {
            fromLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        fromLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.fromInput.text!
    }

}
