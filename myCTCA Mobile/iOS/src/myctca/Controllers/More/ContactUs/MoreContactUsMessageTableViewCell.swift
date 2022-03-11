//
//  MoreContactUsMessageTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreContactUsMessageTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var cellImageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func prepareView(_ title: String, image: UIImage) {
        
        self.titleLabel.text = title
        
        self.cellImageView.image = image
        self.cellImageView.tintColor = MyCTCAColor.ctcaGrey75.color
    }
}
