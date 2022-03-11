//
//  MoreROISectionCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/31/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreROISectionCell: UITableViewCell {

    lazy var sectionTitle: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.backgroundColor = UIColor.clear
        label.isUserInteractionEnabled = false
        label.numberOfLines = 1
        label.font = UIFont(name: "HelveticaNeue", size: 12)
        label.minimumScaleFactor = 0.75
        label.adjustsFontSizeToFitWidth = true
        label.textColor = .white
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
        
        self.backgroundColor = MyCTCAColor.ctcaGreen.color
        
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
