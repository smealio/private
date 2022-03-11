//
//  MoreContactUsDisclaimerTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreContactUsDisclaimerTableViewCell: UITableViewCell {

    @IBOutlet weak var imgView: UIImageView!
    @IBOutlet weak var disclaimerLabel: UILabel!
    
    let disclaimer: String = "If this is a medical emergency, call 911 or your local emergency number for assistance. We will respond to your email message within one (1) business day."
    
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
        
        imgView.image = image
        imgView.tintColor = MyCTCAColor.darkRedWarning.color
        
        disclaimerLabel.numberOfLines = 0
        disclaimerLabel.textColor = MyCTCAColor.darkRedWarning.color
        disclaimerLabel.text = disclaimer
        
        contentView.setNeedsLayout()
        contentView.layoutIfNeeded()
    }

}
