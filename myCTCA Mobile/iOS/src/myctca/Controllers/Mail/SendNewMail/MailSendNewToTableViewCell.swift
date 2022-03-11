//
//  MailSendNewToTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailSendNewToTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

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