//
//  SymptomsTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 3/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class SymptomsTableViewCell: UITableViewCell {

    @IBOutlet weak var symptomNameLabel: UILabel!
    @IBOutlet weak var symptomRangeValue: UILabel!
    @IBOutlet weak var symptomValueLabel: UILabel!
    @IBOutlet weak var symptomImageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func prepareView(symptom:Symptom) {
        symptomNameLabel.text = symptom.itemName
        symptomRangeValue.text = "Normal Range: \(symptom.rangeValue ?? "")"
        symptomValueLabel.text = symptom.observationValue
        symptomImageView.isHidden = false

        if symptom.formatedTextEncoded != "INCREASED" {
            symptomImageView.isHidden = true
        }
    }
}
