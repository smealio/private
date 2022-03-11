//
//  PrescriptionRefillInfoTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillInfoTableViewCell: UITableViewCell {

    @IBOutlet weak var infoImageView: UIImageView!
    @IBOutlet weak var InfoTextLabel: UILabel!
    
    let refillInfoText: String = "If this is an existing prescription, please contact your pharmacy directly. Please use this prescription request form for new prescriptions, changes in prescriptions, and/or change in pharmacy. Please allow 1 business day for your care team to contact you upon prescription request."
    
    override func awakeFromNib() {
        super.awakeFromNib()
        InfoTextLabel.translatesAutoresizingMaskIntoConstraints = false
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView() {
        let image = UIImage(named: "iInfo")!.withRenderingMode(.alwaysTemplate)
        
        infoImageView.image = image
        infoImageView.tintColor = MyCTCAColor.ctcaGrey75.color
        
        InfoTextLabel.numberOfLines = 0
        InfoTextLabel.textColor = MyCTCAColor.ctcaGrey75.color
        InfoTextLabel.text = refillInfoText
        
        contentView.setNeedsLayout()
        contentView.layoutIfNeeded()
    }

}
