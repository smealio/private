//
//  MailSendNewDisclaimerTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/11/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailSendNewDisclaimerTableViewCell: UITableViewCell {

    @IBOutlet weak var warningImage: UIImageView!
    @IBOutlet weak var disclaimerLabel: UILabel!
    
    let disclaimer: String = "If this is a medical emergency, call 911 or your local emergency number for assistance. If you need an immediate response please call your Care Manager. We will respond to your email message within one (1) business day."
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView() {
        let image = UIImage(named: "warning_triangle")!.withRenderingMode(.alwaysTemplate)
        
        warningImage.image = image
        warningImage.tintColor = MyCTCAColor.darkRedWarning.color
        
        disclaimerLabel.numberOfLines = 0
        disclaimerLabel.textColor = MyCTCAColor.darkRedWarning.color
        disclaimerLabel.text = disclaimer
        
        contentView.setNeedsLayout()
        contentView.layoutIfNeeded()
    }
}
