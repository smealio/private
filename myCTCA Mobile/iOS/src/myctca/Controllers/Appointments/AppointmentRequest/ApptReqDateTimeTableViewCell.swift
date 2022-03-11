//
//  ApptReqDateTimeTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptReqDateTimeTableViewCell: UITableViewCell, CTCATableViewCellProtocol, UITextFieldDelegate {

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
    
    // MARK - UITextFieldDelegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print("BECOMES FIRST REPSONDER")
        doesHaveFocus()
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print("RESIGN FIRST REPSONDER")
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        dateTimeLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        dateTimeTF.layer.cornerRadius = 5
        dateTimeTF.layer.masksToBounds = true
        dateTimeTF.layer.borderWidth = 0.5
        dateTimeTF.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
        self.becomeFirstResponder()
    }
    
    func doesNotHaveFocus() {
        dateTimeLabel.textColor = MyCTCAColor.formLabel.color
        dateTimeTF.layer.cornerRadius = 5
        dateTimeTF.layer.masksToBounds = true
        dateTimeTF.layer.borderWidth = 0.5
        dateTimeTF.layer.borderColor = MyCTCAColor.formLines.color.cgColor
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
