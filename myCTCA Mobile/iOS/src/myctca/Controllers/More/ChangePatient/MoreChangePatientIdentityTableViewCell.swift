//
//  MoreChangePatientIdentityTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 4/12/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreChangePatientIdentityTableViewCell: UITableViewCell {

    @IBOutlet weak var infoImageView: UIImageView!
    @IBOutlet weak var identityLabel: UILabel!
    
    let identityText1: String = "You are currently viewing data loaded for "
    let identityText2: String = ". Tap on any active name below to switch to a different patient profile."
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ name: String) {
        
        infoImageView.tintColor = MyCTCAColor.ctcaGrey50.color
        
        let formattedString = NSMutableAttributedString()
        formattedString
            .normal(identityText1)
            .bold(name)
            .normal(identityText2)
        
        identityLabel.attributedText = formattedString
    }

}
