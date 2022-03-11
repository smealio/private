//
//  LabsResultTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/2/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class LabsResultTableViewCell: UITableViewCell {

    @IBOutlet weak var labResultDateLabel: UILabel!
    @IBOutlet weak var labResultDetailsLabel: UILabel!
    @IBOutlet weak var collectedByLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ labResult: LabResult, highLigtText:String) {
        labResultDateLabel.text = labResult.getFormattedPerformedDateString()
        labResultDetailsLabel.setTitleWithHighlight(title: labResult.getLabSetsNames(separatedBy: ", "), text: highLigtText)
        collectedByLabel.setTitleWithHighlight(title: ("Collected by: \(labResult.collectedBy)"), text: highLigtText)
    }

}
