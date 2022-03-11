//
//  ApptDetailsCallTechTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 17/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class ApptDetailsCallTechTableViewCell: UITableViewCell {

    @IBOutlet weak var numberButton: UIButton!
    @IBOutlet weak var descriptionLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func config(schedulingPhone:String) {
        descriptionLabel.attributedText = NSMutableAttributedString()
            .bold("Need to Reschedule or Cancel this appointment?", fontSize: 16.0)
            .normal(" Call Scheduling at", fontSize: 16.0)
        
        numberButton.setTitle(schedulingPhone, for: .normal)
    }
    
    @IBAction func numberButtonTapped(_ sender: Any) {
        if let telNo = numberButton.title(for: .normal), !telNo.isEmpty {
            GenericHelper.shared.tryToCall(telNo: telNo, parentVC: nil)
        }
    }
}
