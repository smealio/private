//
//  ApptDetailPhoneTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/12/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptDetailPhoneTableViewCell: UITableViewCell {
    
    @IBOutlet weak var transportationPhoneLabel: UILabel!
    @IBOutlet weak var accomodationPhoneLabel: UILabel!
    @IBOutlet weak var generalEnqPhoneLabel: UILabel!
    @IBOutlet weak var schedulingPhoneLabel: UILabel!
    
    var phoneLabels = [UILabel]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ appointment: Appointment) {
        transportationPhoneLabel.text = appointment.facility.transportationPhone ?? ""
        accomodationPhoneLabel.text = appointment.facility.accommodationsPhone ?? ""
        generalEnqPhoneLabel.text = appointment.facility.mainPhone ?? ""
        schedulingPhoneLabel.text = appointment.facility.schedulingPhone ?? ""
        
        phoneLabels = [transportationPhoneLabel, accomodationPhoneLabel, generalEnqPhoneLabel, schedulingPhoneLabel]
    }
    
    @IBAction func callButtonTapped(_ sender: Any) {
        let tag = (sender as! UIButton).tag
        
        for phoneNo in phoneLabels {
            if let telNo = phoneNo.text, !telNo.isEmpty && phoneNo.tag == tag {
                GenericHelper.shared.tryToCall(telNo: telNo, parentVC: nil)
                break
            }
        }
    }
    
}
