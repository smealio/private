//
//  MoreContactUsPhoneTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreContactUsPhoneTableViewCell: UITableViewCell {

    @IBOutlet weak var phoneImgView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var phoneNumberLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ titleText: String, phoneText: String) {
        titleLabel.text = titleText
        phoneNumberLabel.text = phoneText
        
        phoneImgView.tintColor = MyCTCAColor.ctcaGrey75 .color
    }

}
