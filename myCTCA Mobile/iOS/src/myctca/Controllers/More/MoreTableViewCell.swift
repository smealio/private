//
//  MoreTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/5/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class MoreTableViewCell: UITableViewCell {

    @IBOutlet weak var moreImageView: UIImageView!
    @IBOutlet weak var moreLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
