//
//  MoreActivityLogHeaderViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreActivityLogHeaderViewCell: UITableViewCell {

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
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
    func prepareView(_ sectionDate: Date) {
        
        self.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
        
        self.addSubview(self.sectionTitle)
        
        self.sectionTitle.text = sectionDateAsString(sectionDate)
        
        self.sectionTitle.translatesAutoresizingMaskIntoConstraints = false
        self.sectionTitle.isUserInteractionEnabled = false
        
        self.sectionTitle.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 20.0).isActive = true
        self.sectionTitle.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: 20.0).isActive = true
        self.sectionTitle.heightAnchor.constraint(equalToConstant: 18.0).isActive = true
        self.sectionTitle.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant:-10.0).isActive = true
    }

    func sectionDateAsString(_ date: Date) -> String {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEEE, MMMM dd, yyyy"
        let dateString: String = dateFormatter.string(from: date)
        return dateString
    }
}
