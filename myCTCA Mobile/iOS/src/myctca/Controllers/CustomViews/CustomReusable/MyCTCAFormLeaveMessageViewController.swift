//
//  MyCTCAFormLeaveMessageViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 19/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class MyCTCAFormLeaveMessageViewController: UIViewController {
    var leaveAction:(() -> Void)?
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var topButton: UIButton!
    
    @IBOutlet weak var bottomButton: UIButton!
    @IBOutlet weak var alertView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        alertView.layer.cornerRadius = 15
        alertView.layer.borderWidth = 0.2
        alertView.layer.borderColor = UIColor.lightGray.cgColor
        alertView.layer.shadowColor = UIColor.lightGray.cgColor
        alertView.layer.shadowOpacity = 0.2
        alertView.layer.shadowOffset = CGSize(width: 0, height: 0)
        alertView.layer.shadowRadius = 4
    }
    
    func setupForTelehealth() {
        titleLabel.text = TelehealthMsgConstants.leavingWarninghMessage
        topButton.setTitle("Resume Telehealth", for: .normal)
        bottomButton.setTitle("Leave Telehealth", for: .normal)
    }
    
    @IBAction func leavePageTapped(_ sender: Any) {
        if let leaveAction = leaveAction {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.01) {
                leaveAction()
            }
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func stayOnPageTapped(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
}
