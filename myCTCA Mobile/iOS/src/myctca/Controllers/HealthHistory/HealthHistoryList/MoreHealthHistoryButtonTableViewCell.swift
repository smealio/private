//
//  MoreHealthHistoryButtonTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/12/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreHealthHistoryButtonTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var subtitleLabel: UILabel!
    var prescription: Prescription?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func prepareView(_ prescription: Prescription, searchText:String = "") {
        titleLabel.setTitleWithHighlight(title: prescription.drugName, text: searchText)
        subtitleLabel.setTitleWithHighlight(title:prescription.subText, text: searchText)
        self.prescription = prescription
    }
}
