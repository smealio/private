//
//  ApptTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/7/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptTableViewCell: UITableViewCell {

    @IBOutlet weak var providerNamesLabel: UILabel!
    @IBOutlet weak var apptNameLabel: UILabel!
    @IBOutlet weak var apptTimeLocLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        
        self.preservesSuperviewLayoutMargins = false
        self.separatorInset = UIEdgeInsets.zero
        self.layoutMargins = UIEdgeInsets.zero
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()

        contentView.frame = contentView.frame.inset(by: UIEdgeInsets(top: 0, left: 0, bottom: 32, right: 0))
    }
    
    func prepareView(_ appointment: Appointment) {
        apptNameLabel.text = appointment.description
        if let loc = appointment.location {
            apptTimeLocLabel.text = appointment.getFormattedStartTime() + " " + appointment.facilityTimeZone + ", " + loc
        } else {
            apptTimeLocLabel.text = appointment.getFormattedStartTime() + " " + appointment.facilityTimeZone
        }
        
        providerNamesLabel.text = appointment.resources
    }

}
