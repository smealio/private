//
//  ApptChangeFromTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptChangeFromTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    
    @IBOutlet weak var fromLabel: UILabel!
    @IBOutlet weak var fromContentLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setContent(_ txt: String) {
        self.fromContentLabel.text = txt
    }
    
    func isValid() -> Bool {
        let fromTxt:String = fromContentLabel.text!
        print("ApptReqFromTableViewCell isValid fromTxt: \(fromTxt)")
        if (fromTxt != "") {
            fromLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        fromLabel.textColor = UIColor.red
        return false
    }

    func getData() -> String {
        return self.fromContentLabel.text!
    }
}
