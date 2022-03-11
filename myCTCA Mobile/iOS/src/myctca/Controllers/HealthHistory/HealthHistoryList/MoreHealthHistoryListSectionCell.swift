//
//  MoreHealthHistoryListSectionCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/28/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreHealthHistoryListSectionCell: UITableViewCell {

    lazy var sectionTitle: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.backgroundColor = UIColor.clear
        label.isUserInteractionEnabled = false
        label.numberOfLines = 1
        label.font = UIFont(name: "HelveticaNeue", size: 13)
        label.minimumScaleFactor = 0.75
        label.adjustsFontSizeToFitWidth = true
        label.textColor = MyCTCAColor.ctcaGrey75.color
        label.lineBreakMode = .byTruncatingTail
        return label
    }()
    
    func prepareView(_ labelText: String) {
        print("MoreHealthHistoryListSectionCell prepareView: \(labelText)")
        self.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
        
        self.addSubview(self.sectionTitle)
        
        self.sectionTitle.text = labelText
        print("MoreHealthHistoryListSectionCell prepareView: \(String(describing: self.sectionTitle.text))")
        self.sectionTitle.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 20.0).isActive = true
        self.sectionTitle.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: 20.0).isActive = true
        self.sectionTitle.heightAnchor.constraint(equalToConstant: 18.0).isActive = true
        self.sectionTitle.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant:-10.0).isActive = true
    }

}
