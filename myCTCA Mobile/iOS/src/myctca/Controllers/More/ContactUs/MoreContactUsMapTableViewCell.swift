//
//  MoreContactUsMapTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreContactUsMapTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
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
