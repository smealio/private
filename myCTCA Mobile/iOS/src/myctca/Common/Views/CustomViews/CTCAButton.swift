//
//  CTCAButton.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCAButton: UIButton {
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setup()
    }
    
    override init(frame:CGRect) {
        super.init(frame: frame)
        setup()
    }
    
    func setup() {
        layer.cornerRadius = 6.0
        layer.borderWidth = 1.0
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
    }

    func setDisabled() {
        isEnabled = false
        backgroundColor = UIColor.ctca_button_disabled
        layer.borderColor = UIColor.clear.cgColor
    }
    
    func setEnabled() {
        isEnabled = true
        backgroundColor = UIColor.ctca_theme_middle
        layer.borderColor = UIColor.ctca_theme.cgColor
    }
}

class CTCAGreenButton: CTCAButton {
    override func setup() {
        super.setup()
        backgroundColor = UIColor.ctca_theme_middle
        layer.borderColor = UIColor.ctca_theme.cgColor
        setTitleColor(.white, for: .normal)
    }
}

//uses border props
class CTCAGrayButton: CTCAButton {
    override func setup() {
        super.setup()
        backgroundColor = UIColor.ctca_gray30
        layer.borderColor = UIColor.ctca_gray_border.cgColor
        setTitleColor(UIColor.ctca_gray150, for: .normal)
    }
}
