//
//  CTCALabel.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 22/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCALabel: UILabel {
    
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
        
        unsetFocus()
    }
    
    func setFocus() {
        self.layer.borderColor = UIColor.ctca_blue_focus.cgColor
        layer.shadowColor = UIColor.ctca_blue_focus_shadow.cgColor
    }
    
    func unsetFocus() {
        self.layer.borderColor = UIColor.ctca_gray90.cgColor
        layer.shadowColor = UIColor.clear.cgColor
    }
}
