//
//  ApptNextTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/6/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

protocol ApptNextTableViewProtocol : AnyObject {
    func showTeleHealthInfoTapped(url:String)
    func joinTelehealthMeetingTapped(url:String)
}

class ApptNextTableViewCell: UITableViewCell {

    @IBOutlet weak var apptLabel: UILabel!
    @IBOutlet weak var apptDateLabel: UILabel!
    @IBOutlet weak var apptTimeLabel: UILabel!
    @IBOutlet weak var apptLocLabel: UILabel!
    @IBOutlet weak var teleHealthButton: UIButton!
    
    weak var delegate: ApptNextTableViewProtocol?
    var teleHealthUrlToOpen = ""
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ appointment: Appointment) {
        apptLabel.text = appointment.description
        apptDateLabel.text = appointment.getFormattedStartDate()
        apptTimeLabel.text = appointment.getFormattedStartTime() + " " + appointment.facilityTimeZone
        
        if appointment.isTeleHealth {
            apptLocLabel.text = ""
            
            if AppSessionManager.shared.getUserType() == .PATIENT {
                teleHealthButton.isHidden = false

                if appointment.telehealthMeetingJoinUrl.count > 0 { //Disply Join Now
                    ErrorManager.shared.telehealthFallbackUrl = appointment.teleHealthUrl
                    teleHealthUrlToOpen = appointment.telehealthMeetingJoinUrl
                    teleHealthButton.setTitle(MyCTCAConstants.FormText.buttonJoinNow, for: .normal)
                    teleHealthButton.tag = 1000
                } else if appointment.teleHealthUrl.count > 0 { //Disply Join Now
                    teleHealthUrlToOpen = appointment.teleHealthUrl
                    teleHealthButton.setTitle(MyCTCAConstants.FormText.buttonJoinNow, for: .normal)
                    teleHealthButton.tag = 2000
                } else if appointment.telehealthInfoUrl.count > 0 { //Disply setup guide
                    teleHealthUrlToOpen = appointment.telehealthInfoUrl
                    teleHealthButton.setTitle(MyCTCAConstants.FormText.buttonSetupGuide, for: .normal)
                    teleHealthButton.tag = 3000
                } else {
                    print("Appointments response incorrect!!")
                }
            } else {
                teleHealthButton.isHidden = true
            }
            
        } else {
            apptLocLabel.text = appointment.location
        }
    }
    
    @IBAction func showTelehealthUrlTapped() {
        if let delegate = self.delegate {
            if teleHealthButton.tag == 1000 {
                delegate.joinTelehealthMeetingTapped(url: teleHealthUrlToOpen)
            } else {
                delegate.showTeleHealthInfoTapped(url: teleHealthUrlToOpen)
            }
        }
    }

}
