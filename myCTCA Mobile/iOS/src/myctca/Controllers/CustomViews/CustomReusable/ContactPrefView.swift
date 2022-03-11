//
//  ContactPrefView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 15/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class ContactPrefView: UIView {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var radioButtonImage: CTCARadioImage!
    @IBOutlet weak var subTitleLabel: UILabel!
    @IBOutlet weak var closeEditButton: SwitchButton!
    @IBOutlet weak var mainViewHeight: NSLayoutConstraint!
    @IBOutlet weak var contactInputView: CTCAContactInputView!
    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var cardView: CTCACardView!
    @IBOutlet weak var editView: UIView!
    
    private let mainViewHeightFull = 170.0
    private let mainViewHeightHalf = 80.0
    private var selButton:UIButton?
    
    var didStateChange:((_ contactView:ContactPrefView) -> Void)?
    var isSelected:Bool = false
    var textFieldType:InputTextType = .NONE
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func  commonInit() {
        Bundle(for: ContactPrefView.self).loadNibNamed("ContactPrefView", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = self.bounds
        
        contentView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    }
    
    func configCell(title:String, subTitle:String) {
        titleLabel.text = title
        subTitleLabel.text = subTitle
        
        closeEditButton.onImage = #imageLiteral(resourceName: "edit_pen")
        closeEditButton.offImage = #imageLiteral(resourceName: "close")
        closeEditButton.toggle()
        
        mainViewHeight.constant = CGFloat(mainViewHeightHalf)
        radioButtonImage.setUnSelected()
        editView.isHidden = true
        addSelectionButton()
        
        contactInputView.textField.textFieldType = textFieldType
    }
    
    @IBAction func saveButtonTapped(_ sender: Any) {
        contactInputView.closeKeyPad()

        if contactInputView.validate() {
            subTitleLabel.text = contactInputView.inputText
            mainViewHeight.constant = CGFloat(mainViewHeightHalf)
            editView.isHidden = true
            closeEditButton.toggle()
            subTitleLabel.isHidden = false
        }
    }

    @IBAction func closeEditButtonTapped(_ sender: Any) {
        if let button = sender as? SwitchButton {
            if button.status == true {
                changeHeightWithAnimation(height: CGFloat(mainViewHeightFull))

                //mainViewHeight.constant = CGFloat(mainViewHeightFull)
                editView.isHidden = false
                subTitleLabel.isHidden = true
                contactInputView.setContact(contact: subTitleLabel.text ?? "")
                addSelectionButton(short: true)
            } else {
                changeHeightWithAnimation(height: CGFloat(mainViewHeightHalf))
                //mainViewHeight.constant = CGFloat(mainViewHeightHalf)
                editView.isHidden = true
                subTitleLabel.isHidden = false
                contactInputView.closeKeyPad()
                addSelectionButton()
            }
        }
    }
    
    func addSelectionButton(short: Bool = false) {
        var btnSize = CGSize(width: contactInputView.frame.origin.x + contactInputView.frame.width, height: mainViewHeight.constant)
        
        if short {
            btnSize = CGSize(width: editView.frame.origin.x , height: mainViewHeight.constant)
        }
        
        if selButton == nil {
            selButton = UIButton(frame: CGRect(origin: CGPoint(x: 0,y: 0), size: btnSize))
            selButton!.addTarget(self, action: #selector(cardSelectionTapped), for: .touchUpInside)
            
            cardView.addSubview(selButton!)
        } else {
            selButton?.frame = CGRect(origin: CGPoint(x: 0,y: 0), size: btnSize)
        }
    }
    
    @objc func cardSelectionTapped() {
        radioButtonImage.toggle()
        if radioButtonImage.isSelected {
            cardView.setFocused()
            titleLabel.textColor = UIColor.ctca_selection_purple
            subTitleLabel.textColor = UIColor.ctca_selection_purple
        } else {
            cardView.setUnFocused()
            titleLabel.textColor = UIColor.ctca_dark_gray
            subTitleLabel.textColor = UIColor.ctca_dark_gray
        }
        
        isSelected = cardView.isSelected
        
        if let changeAction = didStateChange {
            changeAction(self)
        }
    }
    
    func setContactSelected() {
        radioButtonImage.setSelected()
        cardView.setFocused()
        titleLabel.textColor = UIColor.ctca_selection_purple
        
        isSelected = cardView.isSelected
        
        if let changeAction = didStateChange {
            changeAction(self)
        }
    }
    
    func setUnSelected() {
        radioButtonImage.setUnSelected()
        cardView.setUnFocused()
        titleLabel.textColor = UIColor.ctca_dark_gray
        subTitleLabel.textColor = UIColor.ctca_dark_gray
        isSelected = cardView.isSelected
    }
    
    func changeHeightWithAnimation(height:CGFloat) {
        let option:UIView.AnimationOptions = (height == CGFloat(mainViewHeightFull)) ? UIView.AnimationOptions.transitionCurlDown: UIView.AnimationOptions.transitionCurlUp
        UIView.animate(withDuration: 0.2,
        delay: 0.2,
        options: option,
        animations: {
            self.mainViewHeight.constant = height
        self.cardView.layoutIfNeeded()
        },completion: nil)
    }
}
