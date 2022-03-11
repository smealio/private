//
//  TestViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 16/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class TestViewController: UIViewController {
    
    @IBOutlet var textField:UITextField!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        textField.delegate = PhoneNumberFormatter.shared
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        let text = "(123) 4567890"
        
        textField.text = PhoneNumberFormatter.shared.convertToStandardFormat(text)
    }
}
