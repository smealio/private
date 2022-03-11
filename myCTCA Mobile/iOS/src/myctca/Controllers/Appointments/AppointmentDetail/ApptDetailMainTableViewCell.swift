//
//  ApptDetailMainTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/12/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

protocol ApptDetailTeleHealthTableViewProtocol : AnyObject {
    func showTeleHealthInfoTapped(url:String)
    func joinTelehealthMeetingTapped(url:String)
}

class ApptDetailMainTableViewCell: UITableViewCell {

    @IBOutlet weak var apptLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var locLabel: UILabel!
    @IBOutlet weak var providersLabel: UILabel!
    @IBOutlet weak var facLabel: UILabel!
    @IBOutlet weak var joinNowButton: CTCAGreenButton!
    @IBOutlet weak var joinButtonHtConstraint: NSLayoutConstraint!
    
    var teleHealthUrlToOpen = ""
    weak var appointment:Appointment?
    weak var delegate: ApptDetailTeleHealthTableViewProtocol?

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ appointment: Appointment) {
        self.appointment = appointment
        apptLabel.text = appointment.description
        dateLabel.text = appointment.getFormattedStartDate()
        timeLabel.text = appointment.getFormattedStartTime() + " " + appointment.facilityTimeZone
        
        providersLabel.setLineHeight(lineHeight: 3.0)
        providersLabel.text = appointment.getResourceNames()

        if appointment.isTeleHealth {
            facLabel.text = "Telehealth"
            locLabel.text = appointment.location
            
            if !GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.joinTelehealth) {
                joinButtonHtConstraint.constant = 0
            } else {
                if appointment.telehealthMeetingJoinUrl.count > 0 {
                    joinNowButton.setTitle(MyCTCAConstants.FormText.buttonJoinNow, for: .normal)
                    
                    teleHealthUrlToOpen = appointment.telehealthMeetingJoinUrl
                    ErrorManager.shared.telehealthFallbackUrl = appointment.teleHealthUrl
                    joinNowButton.tag = 1000
                } else if appointment.teleHealthUrl.count > 0  {
                    joinNowButton.setTitle(MyCTCAConstants.FormText.buttonJoinNow, for: .normal)
                    
                    teleHealthUrlToOpen = appointment.teleHealthUrl
                    joinNowButton.tag = 2000
                } else if appointment.telehealthInfoUrl.count > 0 {
                    joinNowButton.setTitle(MyCTCAConstants.FormText.buttonSetupGuide, for: .normal)
                    
                    joinNowButton.backgroundColor = UIColor.ctca_gray10
                    joinNowButton.layer.borderColor = UIColor.clear.cgColor
                    joinNowButton.setTitleColor(UIColor.ctca_theme_middle, for: .normal)
                    teleHealthUrlToOpen = appointment.telehealthInfoUrl
                    joinNowButton.tag = 3000
                } else {
                    //assert
                    print("Invalid json")
                }
            }
        } else {
            facLabel.text = appointment.facility.displayName
            
            var address = appointment.facility.address.address1 ?? ""
            if (appointment.facility.address.address2 != nil) {
                address += "\n\(String(describing: appointment.facility.address.address2 ?? ""))"
            }
            address += "\n\(appointment.facility.address.city ?? ""), \(appointment.facility.address.state ?? "") \(appointment.facility.address.postalCode ?? "")"
    
            locLabel.text = address
            
            joinNowButton.setTitle(MyCTCAConstants.FormText.buttongetDirections, for: .normal)
            joinNowButton.backgroundColor = UIColor.ctca_gray10
            joinNowButton.layer.borderColor = UIColor.clear.cgColor
            joinNowButton.setTitleColor(UIColor.ctca_theme_middle, for: .normal)
            teleHealthUrlToOpen = appointment.telehealthInfoUrl
            joinNowButton.tag = 4000
        }
        self.layoutIfNeeded()
    }
    
    @IBAction func joinNowTapped(_ sender: Any) {
        if joinNowButton.tag == 4000 {
            //get directions
            GenericHelper.shared.openFacAddressInMap(facility: appointment?.facility)
        } else {
            if let delegate = self.delegate {
                if joinNowButton.tag != 3000 {
                    delegate.joinTelehealthMeetingTapped(url: teleHealthUrlToOpen)
                } else {
                    delegate.showTeleHealthInfoTapped(url: teleHealthUrlToOpen)
                }
            }
        }
    }
}
