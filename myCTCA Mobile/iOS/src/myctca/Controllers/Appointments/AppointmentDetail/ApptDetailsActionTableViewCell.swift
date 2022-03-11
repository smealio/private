//
//  ApptDetailsActionTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 08/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class ApptDetailsActionTableViewCell: UITableViewCell {
    
    var cancelAction:(() -> Void)?
    var rescheduleAction:(() -> Void)?
    var shareAction:(() -> Void)?
    
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var rescheduleButton: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    func configCell(isUpcoming: Bool, canRescheduleAppt: Bool) {
        if isUpcoming && canRescheduleAppt {
            rescheduleButton.layer.borderColor = UIColor.ctca_gray30.cgColor
            rescheduleButton.layer.borderWidth = 1.0
            
            //this is for caregiver impersonating patient
            if cancelAction == nil {
                stackView.removeArrangedSubview(cancelButton)
                cancelButton.isHidden = true
            }
            if rescheduleAction == nil {
                stackView.removeArrangedSubview(rescheduleButton)
                rescheduleButton.isHidden = true
            }
        } else {
            stackView.removeArrangedSubview(cancelButton)
            stackView.removeArrangedSubview(rescheduleButton)
            cancelButton.isHidden = true
            rescheduleButton.isHidden = true
        }
        
        self.layoutIfNeeded()
    }
    
    @IBAction func actionButtonTapped(_ sender: Any) {
        let tag = (sender as! UIButton).tag
        
        switch(tag) {
        case 1001:
            if let shareAction = shareAction {
                shareAction()
            }
            print("share")
        case 1002:
            if let rescheduleAction = rescheduleAction {
                rescheduleAction()
            }
            print("Reschedule")//
        case 1003:
            if let cancelAction = cancelAction {
                cancelAction()
            }
            print("cancel")//
        default:
            break
        }
    }
}
