//
//  NewCTCATextView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class NewCTCATextView: CTCATextView {
    
    override func didMoveToWindow() {
        super.didMoveToWindow()
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
