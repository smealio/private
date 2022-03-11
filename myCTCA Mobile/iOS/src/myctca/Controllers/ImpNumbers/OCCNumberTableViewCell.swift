//
//  OCCNumberTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 27/08/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class OCCNumberTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var addressButton: UIButton!
    @IBOutlet weak var callButton: UIButton!
    @IBOutlet weak var buttonHtConstant: NSLayoutConstraint!
    
    var displayName = ""
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func callButtonTapped(_ sender: Any) {
        if let button = sender as? UIButton, let number = button.title(for: .normal) {
            GenericHelper.shared.tryToCall(telNo: number, parentVC: nil)
        }
    }
    
    @IBAction func addressButtonTapped(_ sender: Any) {
        if var title = addressButton.titleLabel?.text {
            title = title.replacingOccurrences(of: "\n", with: "")
            GenericHelper.shared.openFacAddressInMap(facility: nil, locAddress: title, displayName: displayName)
        }
    }
    
}
