//
//  NumberTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 26/08/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class NumberTableViewCell: UITableViewCell {

    @IBOutlet weak var numberButton: UIButton!
    @IBOutlet weak var numberDescriptionLabel: UILabel!
    @IBOutlet weak var numberTypeLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func numberButtonTapped(_ sender: Any) {
        if let button = sender as? UIButton, let number = button.title(for: .normal) {
            GenericHelper.shared.tryToCall(telNo: number, parentVC: nil)
        }
    }

}
