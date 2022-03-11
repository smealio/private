//
//  ApptDetailTeleHealthTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 7/13/20.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit


class ApptDetailTeleHealthTableViewCell : UITableViewCell {

    @IBOutlet weak var alertImageView: UIImageView!
    @IBOutlet weak var showTeleHealthButton: UIButton!
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
        if AppSessionManager.shared.getUserType() == .PROXY {
            showTeleHealthButton.isHidden = true
        } else {

            if appointment.telehealthMeetingJoinUrl.count > 0 {
                alertImageView.image = #imageLiteral(resourceName: "telehealth_live")
                showTeleHealthButton.setTitle(MyCTCAConstants.FormText.buttonJoinNow, for: .normal)
                
                teleHealthUrlToOpen = appointment.telehealthMeetingJoinUrl
                ErrorManager.shared.telehealthFallbackUrl = appointment.teleHealthUrl
                showTeleHealthButton.tag = 1000
            } else if appointment.teleHealthUrl.count > 0  {
                alertImageView.image = #imageLiteral(resourceName: "telehealth_live")
                showTeleHealthButton.setTitle(MyCTCAConstants.FormText.buttonJoinNow, for: .normal)
                
                teleHealthUrlToOpen = appointment.teleHealthUrl
                showTeleHealthButton.tag = 2000
            } else if appointment.telehealthInfoUrl.count > 0 {
                alertImageView.image = #imageLiteral(resourceName: "notification_imp")
                showTeleHealthButton.setTitle(MyCTCAConstants.FormText.buttonSetupGuide, for: .normal)
                
                teleHealthUrlToOpen = appointment.telehealthInfoUrl
                showTeleHealthButton.tag = 3000
            } else {
                //assert
                print("Invalid json")
            }
        }

    }
    
    @IBAction func showTeleHealthTapped(_ sender: Any) {
//        if let delegate = self.delegate {
//            if showTeleHealthButton.tag == 1000 {
//                delegate.joinTelehealthMeetingTapped(url: teleHealthUrlToOpen)
//            } else {
//                delegate.showTeleHealthInfoTapped(url: teleHealthUrlToOpen)
//            }
//        }
    }
    
}
