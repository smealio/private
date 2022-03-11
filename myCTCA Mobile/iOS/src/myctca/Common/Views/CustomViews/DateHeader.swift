//
//  DateHeader.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 06/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import JTAppleCalendar

protocol DateHeaderProtocol: AnyObject {
    func didTappedChange(_ index:Int)
}

@available(iOS 13.0, *)
class DateHeader: JTAppleCollectionReusableView {
    @IBOutlet weak var leftButton: UIButton!
    @IBOutlet weak var monthTitleLabel: UILabel!
    @IBOutlet weak var rightButton: UIButton!
    
    weak var delegate:DateHeaderProtocol?
    
    @IBAction func rightButtonTapped(_ sender: Any) {
        if let del = delegate {
            del.didTappedChange(1)
        }
    }
    
    @IBAction func leftButtonTapped(_ sender: Any) {
        if let del = delegate {
            del.didTappedChange(-1)
        }
    }
    
    func keepAllActive() {
        leftButton.isEnabled = true
        leftButton.tintColor = UIColor.label
        
        rightButton.isEnabled = true
        rightButton.tintColor = UIColor.label
    }
    
}
