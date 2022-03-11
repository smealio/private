//
//  MyCTCAAlertViewController.swift
//  myctca
//
//  Created by Manjunath K on 6/8/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class MyCTCAAlertViewController: UIViewController {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    
    @IBOutlet weak var leftActnButton: UIButton!
    @IBOutlet weak var rightActnButton: UIButton!
    @IBOutlet weak var alertView: UIView!
    
    var leftBtnAction:(()->Void)?
    var rightBtnAction:(()->Void)?
    
    @IBAction func leftActnButtonTapped(_ sender: Any) {
        alertView.isHidden = true
        self.dismiss(animated: true, completion: leftBtnAction)
    }
    
    @IBAction func rightActnButtonTapped(_ sender: Any) {
        alertView.isHidden = true
        self.dismiss(animated: true, completion: rightBtnAction)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //alertView.isHidden = true
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        //alertView.isHidden = false
    }
    
    
    func setup(alertInfo:myCTCAAlert?) {
        leftActnButton.layer.borderWidth = 0.5
        leftActnButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
        leftActnButton.layer.cornerRadius = 5
        
        rightActnButton.layer.cornerRadius = 5
        
        alertView.layer.cornerRadius = 2
        alertView.layer.borderWidth = 0.2
        alertView.layer.borderColor = UIColor.lightGray.cgColor
        alertView.layer.shadowColor = UIColor.lightGray.cgColor
        alertView.layer.shadowOpacity = 0.2
        alertView.layer.shadowOffset = CGSize(width: 0, height: 0)
        alertView.layer.shadowRadius = 4
        
        if let info = alertInfo {
            titleLabel.text = info.title
            messageLabel.text = info.message
            
            if let leftBtnTitle = info.leftBtnTitle {
                leftActnButton.setTitle(leftBtnTitle, for: .normal)
            }
            
            if let leftBtnActn = info.leftBtnAction {
                leftBtnAction = leftBtnActn
            }

            if let rightBtnActn = info.rightBtnAction {
                rightBtnAction = rightBtnActn
            }
            
            if let rightBtnTitle = info.rightBtnTitle {
                rightActnButton.setTitle(rightBtnTitle, for: .normal)
            } else {
                rightActnButton.setTitle("OK", for: .normal)
            }
        }
    }
}
