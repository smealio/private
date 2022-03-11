//
//  MoreChangePatientNameTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 4/12/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreChangePatientNameTableViewCell: UITableViewCell {

    @IBOutlet weak var nameLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ name: String, enabled: Bool) {
        print("prepareView name: \(name)")
        self.nameLabel.text = name
        if (enabled == false) {
            self.nameLabel.textColor = MyCTCAColor.ctcaGrey50.color
        } else {
            self.nameLabel.textColor = MyCTCAColor.formContent.color
        }
    }

}
