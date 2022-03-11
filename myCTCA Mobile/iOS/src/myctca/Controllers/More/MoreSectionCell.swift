//
//  MoreSectionCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/6/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class MoreSectionCell: UITableViewHeaderFooterView {

    lazy var sectionTitle: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.backgroundColor = UIColor.clear
        label.isUserInteractionEnabled = false
        label.numberOfLines = 1
        label.font = UIFont(name: "HelveticaNeue", size: 12)
        label.minimumScaleFactor = 0.75
        label.adjustsFontSizeToFitWidth = true
        label.textColor = MyCTCAColor.ctcaGrey75.color
        label.lineBreakMode = .byTruncatingTail
        return label
    }()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func prepareView(_ labelText: String) {
     
        // Setting the background color on UITableViewHeaderFooterView has been deprecated. - BT 4/23/18
        //self.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
        self.contentView.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
        self.addSubview(self.sectionTitle)
        
        self.sectionTitle.text = labelText
        
        self.sectionTitle.translatesAutoresizingMaskIntoConstraints = false
        self.sectionTitle.isUserInteractionEnabled = false
        
        self.sectionTitle.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 20.0).isActive = true
        self.sectionTitle.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: 20.0).isActive = true
        self.sectionTitle.heightAnchor.constraint(equalToConstant: 18.0).isActive = true
        self.sectionTitle.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant:-10.0).isActive = true
    }

}
