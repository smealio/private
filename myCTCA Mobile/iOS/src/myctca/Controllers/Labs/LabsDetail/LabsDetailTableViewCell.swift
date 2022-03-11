//
//  LabsDetailTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/3/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class LabsDetailTableViewCell: UITableViewCell {

    @IBOutlet weak var itemNameLabel: UILabel!
    @IBOutlet weak var normalRangeLabel: UILabel!
    @IBOutlet weak var resultLabel: UILabel!
    @IBOutlet weak var notesLabel: UILabel!
    @IBOutlet weak var outOfRangeImageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView(_ labSetDetail: LabSetDetail) {
        
        if !labSetDetail.itemName.isEmpty {
            itemNameLabel.text = labSetDetail.itemName
        } else {
            
        }
        
        if let normalRange = labSetDetail.normalRange, !normalRange.isEmpty {
            normalRangeLabel.text = "Normal Range: \(normalRange)"
            normalRangeLabel.isHidden = false
        } else {
            normalRangeLabel.text = ""
            normalRangeLabel.isHidden = true
        }
        
        if let result = labSetDetail.result, !result.isEmpty {
            resultLabel.text = result
            if let abnormalityCodeCalculated = labSetDetail.abnormalityCodeCalculated, (abnormalityCodeCalculated == "N" || abnormalityCodeCalculated.isEmpty) {
                resultLabel.textColor = MyCTCAColor.ctcaGrey75.color
                outOfRangeImageView.isHidden = true;
                outOfRangeImageView.image = nil
            } else {
                resultLabel.textColor = UIColor.red
                outOfRangeImageView.isHidden = false;
                outOfRangeImageView.tintColor = UIColor.red
                if(labSetDetail.abnormalityCodeCalculated == "H") {
                    outOfRangeImageView.image = #imageLiteral(resourceName: "up_arrow")
                }
                if(labSetDetail.abnormalityCodeCalculated == "L") {
                    outOfRangeImageView.image = #imageLiteral(resourceName: "down_arrow")
                }
            }
            resultLabel.isHidden = false
        } else {
            resultLabel.text = ""
            resultLabel.isHidden = true
            outOfRangeImageView.isHidden = true;
            outOfRangeImageView.image = nil
        }
        
        if let notes = labSetDetail.notes, !notes.isEmpty {
            notesLabel.text = notes
            notesLabel.isHidden = false
        } else {
            notesLabel.text = ""
            notesLabel.isHidden = true
        }
    }
}
