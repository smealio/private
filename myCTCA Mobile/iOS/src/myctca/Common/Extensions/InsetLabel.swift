//
//  InsetLabel.swift
//  myctca
//
//  Created by Manjunath K on 9/4/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class InsetLabel: UILabel {

    let inset = UIEdgeInsets(top: 10, left: 2, bottom: 10, right: 2)

    override func drawText(in rect: CGRect) {
        super.drawText(in: rect.inset(by: inset))
    }

    override var intrinsicContentSize: CGSize {
        var intrinsicContentSize = super.intrinsicContentSize
        intrinsicContentSize.width += inset.left + inset.right
        intrinsicContentSize.height += inset.top + inset.bottom
        return intrinsicContentSize
    }
}
