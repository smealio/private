//
//  ApptSectionCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/6/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptSectionCell: UITableViewHeaderFooterView {
    
    lazy var sectionTitle: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.isUserInteractionEnabled = false
        label.numberOfLines = 1
        label.font = UIFont(name: "HelveticaNeue-Bold", size: 14)
        label.textColor = UIColor.ctca_selection_purple
        label.lineBreakMode = .byTruncatingTail
        return label
    }()
    
    func prepareView(_ labelText: String) {
        self.contentView.backgroundColor = UIColor.clear
        
        self.addSubview(self.sectionTitle)
        
        self.sectionTitle.text = labelText
        
        self.sectionTitle.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 16.0).isActive = true
        self.sectionTitle.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: 16.0).isActive = true
        self.sectionTitle.topAnchor.constraint(equalTo: self.topAnchor, constant:20.0).isActive = true
        self.sectionTitle.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant:-6.0).isActive = true
    }
    
}
