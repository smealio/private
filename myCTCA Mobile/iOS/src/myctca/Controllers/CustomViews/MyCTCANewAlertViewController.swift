//
//  MyCTCANewAlertViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 02/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class MyCTCANewAlertViewController: UIViewController {

    @IBOutlet weak var alertView: RoundedCornerView!
    @IBOutlet weak var checkImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var okButton: UIButton!
    
    var alertInfo:MyCTCANewAlertInfo?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        setUpUI()
    }
    
    func setUpUI() {
        if let info = alertInfo {
            if !info.state {
                checkImageView.image = #imageLiteral(resourceName: "round-X")
                titleLabel.textColor = UIColor.ctca_alert_red_middle
            }
            titleLabel.text = info.title
            messageLabel.text = info.message
        }
    }
    
    @IBAction func okButtonTapped(_ sender: Any) {
        if let info = alertInfo, let action = info.buttonAction {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.01) {
                action()
            }
        }
        
        self.dismiss(animated: true, completion: nil)
    }

}
