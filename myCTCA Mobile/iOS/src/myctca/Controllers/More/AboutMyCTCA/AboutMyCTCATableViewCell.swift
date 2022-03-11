//
//  AboutMyCTCATableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/19/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class AboutMyCTCATableViewCell: UITableViewCell {

    @IBOutlet weak var myCTCALabel: UILabel!
    @IBOutlet weak var copyrightLabel: UILabel!
    @IBOutlet weak var versionLabel: UILabel!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
