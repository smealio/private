//
//  ApptChangeDateTimeTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptChangeDateTimeTableViewCell: UITableViewCell, CTCATableViewCellProtocol, UITextFieldDelegate {

    @IBOutlet weak var dateTimeLabel: UILabel!
    @IBOutlet weak var dateTimeTF: UITextField!
    @IBOutlet weak var calendarImageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        dateTimeTF.delegate = self
        self.calendarImageView.tintColor = MyCTCAColor.ctcaSecondGreen.color
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setDateTime(dateTime: String) {
        self.dateTimeTF.text = dateTime
        
        if dateTime != "" {
            dateTimeLabel.textColor = MyCTCAColor.formLabel.color
        }
    }
    
    func hideCalendar(_ hidden: Bool) {
        self.calendarImageView.isHidden = hidden
    }

    func isValid() -> Bool {
        let dateTimeTxt:String = dateTimeTF.text!
        print("ApptReqDateTimeTableViewCell isValid fromTxt: \(dateTimeTxt)")
        if (dateTimeTxt != "") {
            dateTimeLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        dateTimeLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.dateTimeTF.text!
    }
}
