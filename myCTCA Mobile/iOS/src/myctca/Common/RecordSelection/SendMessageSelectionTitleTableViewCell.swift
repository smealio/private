//
//  SelectionTitleTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/15/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class SelectionTitleTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
