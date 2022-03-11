//
//  NumberExtraTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 26/08/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class NumberExtraTableViewCell: UITableViewCell {
    @IBOutlet weak var number1Button: UIButton!
    @IBOutlet weak var number2Button: UIButton!
    @IBOutlet weak var numberDescriptionLabel: UILabel!
    @IBOutlet weak var numberTypeLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    @IBAction func numberButtonTapped(_ sender: Any) {
        if let button = sender as? UIButton, let number = button.title(for: .normal) {
            GenericHelper.shared.tryToCall(telNo: number, parentVC: nil)
        }
    }
    
}
