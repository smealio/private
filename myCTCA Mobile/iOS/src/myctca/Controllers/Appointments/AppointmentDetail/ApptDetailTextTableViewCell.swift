//
//  ApptDetailTextTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/12/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptDetailTextTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var bodyLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func prepareView(body: String) {
        bodyLabel.text = body
    }
}
