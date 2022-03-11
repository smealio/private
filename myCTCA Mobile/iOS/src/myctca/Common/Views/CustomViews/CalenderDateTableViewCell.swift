//
//  CalenderDateTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 19/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import JTAppleCalendar

class CalenderDateTableViewCell: JTAppleCell {
    @IBOutlet weak var noBGView: UIView!
    @IBOutlet weak var dateLabel: UILabel!
    var date:Date?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func  addShadow() {
        layer.shadowOpacity = 1
        layer.shadowOffset = CGSize(width: 0, height: 0)
        layer.shadowRadius = 15
        layer.shadowColor = UIColor(red: 0.404, green: 0.192, blue: 0.378, alpha: 0.13).cgColor
    }
}
