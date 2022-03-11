//
//  RadioButton.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 04/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class RadioButton: SwitchButton {

    override func draw(_ rect: CGRect) {
        super.draw(rect)
        
        if #available(iOS 13.0, *) {
            self.layer.borderColor = UIColor.systemGray5.cgColor
        } else {
            self.layer.borderColor = UIColor.gray.cgColor
        }
        self.layer.borderWidth = 1.0
        self.layer.cornerRadius = 10.0
    }
}
