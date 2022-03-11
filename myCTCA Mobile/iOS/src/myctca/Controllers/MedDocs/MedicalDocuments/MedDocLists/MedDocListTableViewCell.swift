//
//  MedDocListTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/15/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MedDocListTableViewCell: UITableViewCell {

    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var authoredLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }

}
