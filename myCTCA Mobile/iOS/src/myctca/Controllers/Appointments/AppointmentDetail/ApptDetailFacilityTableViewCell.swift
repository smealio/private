//
//  ApptDetailFacilityTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/13/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptDetailFacilityTableViewCell: UITableViewCell {

    @IBOutlet weak var facilityLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ facName: String) {
        let facList = AppSessionManager.shared.currentUser.allFacilitesNamesList
        var name = facList[facName]
        if name == nil {
            name = facName
        }
        self.facilityLabel.text = name!
    }

}
