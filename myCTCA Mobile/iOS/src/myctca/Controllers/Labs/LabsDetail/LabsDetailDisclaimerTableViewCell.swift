//
//  LabsDetailDisclaimerTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/5/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

enum DisclaimerType: String {
    case basic = "basic"
    case twentyFourHours = "twentyFourHours"
    case externalSource = "ExternalSource"
}

class LabsDetailDisclaimerTableViewCell: UITableViewCell {

    @IBOutlet weak var infoImageView: UIImageView!
    @IBOutlet weak var disclaimerLabel: UILabel!
    
    let FROM_EXTERNAL_SOURCE: String = "These lab results were collected by an external provider and reported to CTCA."
    
    let LESS_THAN_24_HOURS: String = "You have selected documents which include results posted in the last 24 hours. These results may not have been reviewed by your care team yet, and may require further discussion between members of your care team for clarification before finalization. Those final results will be discussed with you by your care team during your upcoming scheduled appointment. If you do not have an appointment scheduled, or wish to discuss these results earlier, please contact your Care Manager for assistance."
    
    let NOT_OFFICIAL_TEXT: String = "This is not an official lab report. Please contact your Care Management team if you have any questions."
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
    func prepareView(disclaimers: [DisclaimerType]) {
        
        infoImageView.tintColor = MyCTCAColor.ctcaGrey75.color
        
        var disclaimerTxt = ""
        
        for disclaimerType in disclaimers {
            if (disclaimerType == .externalSource ) {
                disclaimerTxt += "\(FROM_EXTERNAL_SOURCE)\n\n"
            }
            if (disclaimerType == .twentyFourHours ) {
                disclaimerTxt += "\(LESS_THAN_24_HOURS)\n\n"
            }
            if (disclaimerType == .basic) {
                disclaimerTxt += NOT_OFFICIAL_TEXT
            }
        }
        
        print("disclaimerTxt: \(disclaimerTxt)" )
        self.disclaimerLabel.text = disclaimerTxt
    }
}
