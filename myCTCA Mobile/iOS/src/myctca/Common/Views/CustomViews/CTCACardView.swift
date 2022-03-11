//
//  CTCACardView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 03/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCACardView: UIView {
    var isSelected = false
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func  commonInit() {
        layer.cornerRadius = 6.0
        layer.borderColor = UIColor.ctca_gray_border.cgColor
        layer.borderWidth = 0.5
        if #available(iOS 13.0, *) {
            backgroundColor = UIColor.systemBackground
        } else {
            backgroundColor = UIColor.white
        }
        
        layer.shadowColor = UIColor.clear.cgColor
        layer.shadowOpacity = 1
        layer.shadowOffset = CGSize(width: 0, height: 0)
        layer.shadowRadius = 15
    }
    
    func setUnFocused() {
        layer.shadowColor = UIColor.clear.cgColor
        isSelected = false
    }
    
    func setFocused(color:UIColor? = nil) {
        if let color = color {
            layer.shadowColor = color.cgColor
        } else {
            layer.shadowColor = UIColor(red: 0.404, green: 0.192, blue: 0.378, alpha: 0.13).cgColor
        }
        isSelected = true
    }
}
