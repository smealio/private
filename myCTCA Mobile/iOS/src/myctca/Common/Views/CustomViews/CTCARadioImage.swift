//
//  CTCARadioImage.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 15/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class CTCARadioImage : UIImageView {
    var isSelected = false
    
    override func draw(_ rect: CGRect) {
        super.draw(rect)
        
        frame.size.width = 20.0
        frame.size.height = 20.0
        
        setUnSelected() 
    }
    
    func setSelected() {
        isSelected = true
        
        self.image = UIImage(named: "round-check")
        self.tintColor = UIColor.ctca_selection_purple
    }
    
    func setUnSelected() {
        isSelected = false
        
        self.image = UIImage(named: "round-gray")
        self.backgroundColor = UIColor.clear
    }
    
    @objc func toggle() {
        if isSelected {
            setUnSelected()
        } else {
            setSelected() 
        }
    }
}
