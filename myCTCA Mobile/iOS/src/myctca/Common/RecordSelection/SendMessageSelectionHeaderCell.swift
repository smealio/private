//
//  SendMessageSelectionHeaderCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/15/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class SendMessageSelectionHeaderCell: UITableViewCell {

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