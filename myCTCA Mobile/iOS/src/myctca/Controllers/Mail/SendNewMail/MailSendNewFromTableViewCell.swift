//
//  MailSendNewFromTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailSendNewFromTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    @IBOutlet weak var fromLabel: UILabel!
    @IBOutlet weak var fromTF: UITextField!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func isValid() -> Bool {
        let fromTxt:String = fromTF.text!
        print("MailSendNewFromTableViewCell isValid fromTxt: \(fromTxt)")
        if (fromTxt != "") {
            fromLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        fromLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.fromTF.text!
    }
}
