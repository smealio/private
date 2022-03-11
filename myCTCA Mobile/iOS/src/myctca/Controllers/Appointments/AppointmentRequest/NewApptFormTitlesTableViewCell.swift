//
//  NewApptFormTitlesTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 06/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class NewApptFormTitlesTableViewCell: UITableViewCell {

    @IBOutlet weak var subTitleLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    
    let titleLabelText = "What is your preferred date and time?"
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config() {
        GenericHelper.shared.updateTitleWithPrefix(label: titleLabel, title: titleLabelText)
    }

}
