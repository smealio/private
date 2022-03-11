//
//  ApptDetailNotesTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 08/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class ApptDetailNotesTableViewCell: UITableViewCell {

    @IBOutlet weak var scheduleNotes: UILabel!
    @IBOutlet weak var additionalNotes: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(appointment:Appointment) {
        if !appointment.schedulerNotes.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            scheduleNotes.text = appointment.schedulerNotes
        }
        if let info = appointment.additionalInfo, !info.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            additionalNotes.text = info
        }
    }
}
