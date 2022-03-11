//
//  CTCAInputView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 20/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCAInputView: UIView {
    @IBOutlet weak var textField: CTCATextField!
    @IBOutlet weak var errorLabel: UILabel!
    @IBOutlet weak var contentView: CTCACardView!
    
    @IBOutlet weak var errorLabelHtConstraint: NSLayoutConstraint!
    private let errorLabelHt = 17.0
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func  commonInit() {
        Bundle(for: CTCAInputView.self).loadNibNamed("CTCAInputView", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = self.bounds
        contentView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    }
    
    func setInvalid() {
        //textField.markInvalidEntry()
        errorLabel.textColor = UIColor.ctca_alert_red_middle
    }
}
