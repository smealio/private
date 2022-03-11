//
//  AboutSectionCell.swift
//  myctca
//
//  Created by Tomack, Barry on 2/19/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class AboutSectionCell: UITableViewCell {
        
    lazy var sectionTitleLabel: UILabel = {
        let label = UILabel()
        label.backgroundColor = UIColor.clear
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
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
    func prepareView(_ labelText: String) {
        
        self.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
        
        self.addSubview(self.sectionTitleLabel)
        
        self.sectionTitleLabel.text = labelText
        
        self.sectionTitleLabel.translatesAutoresizingMaskIntoConstraints = false
        self.sectionTitleLabel.isUserInteractionEnabled = false
        
        self.sectionTitleLabel.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 20.0).isActive = true
        self.sectionTitleLabel.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: 20.0).isActive = true
        self.sectionTitleLabel.heightAnchor.constraint(equalToConstant: 18.0).isActive = true
        self.sectionTitleLabel.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant:-10.0).isActive = true
    }
        
}

