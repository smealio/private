//
//  MoreActivityLogLoadingViewCell.swift
//  myctca
//
//  Created by Manjunath K on 7/23/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class MoreActivityLogLoadingViewCell: UITableViewCell {

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        activityIndicator.isHidden = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func showAnimating() {
        activityIndicator.isHidden = false
        activityIndicator.startAnimating()
    }
}
