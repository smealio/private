//
//  LabsLatestTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/2/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class LabsLatestTableViewCell: UITableViewCell {

    @IBOutlet weak var latestResultContainerView: UIView!
    
    @IBOutlet weak var latestResultsDateLabel: UILabel!
    @IBOutlet weak var latestResultNameLabel: UILabel!
    @IBOutlet weak var latestResultCollectedLabel: UILabel!
        
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }

    func prepareView(_ labResult: LabResult) {
        latestResultsDateLabel.text = labResult.getFormattedPerformedDateString()
        latestResultNameLabel.text = labResult.getLabSetsNames(separatedBy: " \n")
        latestResultCollectedLabel.text = "Collected by: \(labResult.collectedBy)"
    }
}
