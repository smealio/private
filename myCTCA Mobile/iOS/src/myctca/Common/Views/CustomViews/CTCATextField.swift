//
//  CTCATextField.swift
//  myctca
//
//  Created by Manjunath K on 7/24/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class CTCATextField: UITextField {    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func  commonInit() {
        backgroundColor = UIColor.ctca_white
        layer.cornerRadius = 6
        layer.borderWidth = 1
        layer.shadowOpacity = 1
        layer.shadowOffset = .zero
        layer.shadowRadius = 15
        clipsToBounds = true
        
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        self.layer.borderColor = UIColor.ctca_blue_focus.cgColor
        layer.shadowColor = UIColor.ctca_blue_focus_shadow.cgColor
    }
    
    func doesNotHaveFocus() {
        self.layer.borderColor = UIColor.ctca_gray90.cgColor
        layer.shadowColor = UIColor.clear.cgColor
    }
    
    func markInvalidEntry() {
        self.layer.borderColor = UIColor.ctca_alert_red_middle.cgColor
        layer.shadowColor = UIColor.ctca_alert_red_middle_shadow.cgColor
    }
}
