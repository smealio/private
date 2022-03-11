//
//  MoreSelectionHeaderTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 8/5/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class MoreSelectionHeaderTableViewCell: UITableViewCell {

    @IBOutlet weak var headerLabel: UILabel!
    @IBOutlet weak var headerImageView: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()
        
        headerImageView.tintColor = MyCTCAColor.ctcaGrey75.color
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
