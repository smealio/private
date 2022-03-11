//
//  MailSendNewSubjectTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailSendNewSubjectTableViewCell: UITableViewCell, CTCATableViewCellProtocol, UITextFieldDelegate {

    @IBOutlet weak var subjectLabel: UILabel!
    @IBOutlet weak var subjectTF: UITextField!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        subjectTF.delegate = self
        doesNotHaveFocus()
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func isValid() -> Bool {
        let subjectTxt:String = subjectTF.text!
        print("MailSendNewSubjectTableViewCell isValid subjectTxt: \(subjectTxt)")
        if (subjectTxt != "") {
            subjectLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        subjectLabel.textColor = UIColor.red
        return false
    }
    
    func addDoneButtonOnKeyboard() {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: 320, height: 50))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaSecondGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        subjectTF.inputAccessoryView = doneToolbar
    }
    
    // MARK - UITextFieldDelegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        doesHaveFocus()
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        subjectLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        subjectTF.layer.cornerRadius = 5
        subjectTF.layer.masksToBounds = true
        subjectTF.layer.borderWidth = 0.5
        subjectTF.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        subjectLabel.textColor = MyCTCAColor.formLabel.color
        subjectTF.layer.cornerRadius = 5
        subjectTF.layer.masksToBounds = true
        subjectTF.layer.borderWidth = 0.5
        subjectTF.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        subjectTF.resignFirstResponder()
    }
    
    func getData() -> String {
        return self.subjectTF.text!
    }
}
