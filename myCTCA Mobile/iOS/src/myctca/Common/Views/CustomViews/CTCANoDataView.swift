//
//  CTCANoDataView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 28/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCANoDataView: UIView {

    @IBOutlet var contentView: UIView!
    @IBOutlet weak var actionButton: CTCAGreenButton!
    @IBOutlet weak var subTitleLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var imageView: UIImageView!
    
    private var buttonAction: (() -> Void)?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func  commonInit() {
        Bundle(for: CTCANoDataView.self).loadNibNamed("CTCANoDataView", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = self.bounds
        
        contentView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    }
    
    func setup(info:EmptyDataInfo) {
        if !info.title.isEmpty {
            titleLabel.text = info.title
        }
        if !info.subTitle.isEmpty {
            subTitleLabel.text = info.subTitle
        }
        if let buttonTitle = info.buttonTitle, !buttonTitle.isEmpty {
            actionButton.setTitle(buttonTitle, for: .normal)
        }
        if let image = info.image {
            imageView.image = image
        }
        if let action = info.buttonAction {
            buttonAction = action
        }
    }
    
    @IBAction func actionButtonTapped(_ sender: Any) {
        if let action = buttonAction {
            action()
        }
    }
    
}
