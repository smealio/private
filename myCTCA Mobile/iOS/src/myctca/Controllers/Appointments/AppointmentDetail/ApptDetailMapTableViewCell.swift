//
//  ApptDetailMapTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/12/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptDetailMapTableViewCell: UITableViewCell {

    @IBOutlet weak var addressLabel: UILabel!
    @IBOutlet weak var mapImage: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func prepareView(_ address: String) {
        addressLabel.text = address
        
        mapImage.tintColor = MyCTCAColor.ctcaGrey75.color
    }
}
