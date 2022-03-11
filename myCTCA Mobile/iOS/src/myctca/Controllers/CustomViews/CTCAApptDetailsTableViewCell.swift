//
//  CTCAApptDetailsTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 04/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCAApptDetailsTableViewCell: UITableViewCell {

    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var doctorLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var bgCardView: CTCACardView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func configure() {
        bgCardView.setFocused()
        
        if let apptDetails = AppointmentsManager.shared.getAppointmentDetailsForId(appointmentId: AppointmentsManager.shared.requestAppointment.appointmentId) {
            let dateText = DateConvertor.convertToStringFromDate(date: apptDetails.startDateInLocalTZ, outputFormat: .onlyWeeekDayForm) +
                            ", " +
                            DateConvertor.convertToStringFromDate(date: apptDetails.startDateInLocalTZ, outputFormat: .fullMonthForm)
            dateLabel.text = dateText.uppercased()
            titleLabel.text = apptDetails.description
            doctorLabel.setLineHeight(lineHeight: 3.0)
            doctorLabel.text = apptDetails.getResourceNames()
            timeLabel.text = apptDetails.getFormattedStartTime() + " " + apptDetails.facilityTimeZone +
                            ", " + (apptDetails.location ?? "")
        }
    }
}
