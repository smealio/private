//
//  CTCASubTitleTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 7/30/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class CTCASubTitleTableViewCell: UITableViewCell {
    
    @IBOutlet weak var subTitleLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        
        accessoryType = .disclosureIndicator
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setSubTitle(title:String) {
        subTitleLabel.text = title
    }
    
}
