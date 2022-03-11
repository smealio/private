//
//  MyCTCAAlertLargeViewController.swift
//  myctca
//
//  Created by Manjunath K on 6/25/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class MyCTCAAlertLargeViewController: UIViewController {
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var imageHtConstraint: NSLayoutConstraint!
    @IBOutlet weak var imageTopHtConstraint: NSLayoutConstraint!
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    
    @IBOutlet weak var topActnButton: UIButton!
    @IBOutlet weak var bottomActnButton: UIButton!
    @IBOutlet weak var alertView: UIView!
    
    @IBOutlet weak var bottomHtConstraint: NSLayoutConstraint!
    
    var topBtnAction:(()->Void)?
    var bottomBtnAction:(()->Void)?
    
    let bottomHt = 31.0
    let imageHt = 120.0
    let imageTopHt = 40.0
    
    @IBAction func topActnButtonTapped(_ sender: Any) {
        alertView.isHidden = true
        self.dismiss(animated: true, completion: topBtnAction)
    }
    
    @IBAction func bottomActnButtonTapped(_ sender: Any) {
        alertView.isHidden = true
        self.dismiss(animated: true, completion: bottomBtnAction)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setup(alertInfo:myCTCAAlert?) {
        topActnButton.layer.borderWidth = 0.5
        topActnButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
        topActnButton.layer.cornerRadius = 5
        
        alertView.layer.cornerRadius = 15
        alertView.layer.borderWidth = 0.2
        alertView.layer.borderColor = UIColor.lightGray.cgColor
        alertView.layer.shadowColor = UIColor.lightGray.cgColor
        alertView.layer.shadowOpacity = 0.2
        alertView.layer.shadowOffset = CGSize(width: 0, height: 0)
        alertView.layer.shadowRadius = 4
        
        if let info = alertInfo {
            if let image = info.image {
                imageView.image = image
                imageHtConstraint.constant = CGFloat(imageHt)
            } else {
                imageHtConstraint.constant = 0.0
            }
            
            titleLabel.text = info.title
            if let attributeMessage = info.attributeMessage {
                messageLabel.attributedText = attributeMessage
            } else {
                messageLabel.text = info.message
            }
            
            if let topBtnTitle = info.leftBtnTitle {
                topActnButton.setTitle(topBtnTitle, for: .normal)
            } else {
                topActnButton.setTitle("OK", for: .normal)
            }
            
            if let topBtnActn = info.leftBtnAction {
                topBtnAction = topBtnActn
            }
            
            if let bottomBtnActn = info.rightBtnAction {
                bottomBtnAction = bottomBtnActn
            }
            
            if let bottomBtnTitle = info.rightBtnTitle {
                bottomActnButton.isHidden = false
                bottomHtConstraint.constant = CGFloat(bottomHt)
                bottomActnButton.setTitle(bottomBtnTitle, for: .normal)
            } else {
                bottomActnButton.isHidden = true
                bottomHtConstraint.constant = 0.0
            }
            
            if info.isDistructive {
                bottomActnButton.setTitleColor(.red, for: .normal)
            }
        }
    }
}
