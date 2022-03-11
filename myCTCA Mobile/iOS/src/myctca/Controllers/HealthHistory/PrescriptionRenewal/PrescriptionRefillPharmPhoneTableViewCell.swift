//
//  PrescriptionRefillPharmPhoneTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillPharmPhoneTableViewCell: UITableViewCell, CTCATableViewCellProtocol, UITextFieldDelegate {

    @IBOutlet weak var pharmPhoneLabel: UILabel!
    @IBOutlet weak var pharmPhoneInput: PhoneNumberTextField!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        pharmPhoneInput.delegate = self
        doesNotHaveFocus()
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func isValid() -> Bool {
        return false
    }
    
    func isValidPhone() -> FormValidationResults {
        let phoneTxt:String = pharmPhoneInput.text!
        if (phoneTxt == "") {
            pharmPhoneLabel.textColor = UIColor.red
            return .INVALID_FORM
        } else {
            let digitsPhoneTxt = PhoneNumberFormatter.shared.removeAllNonDigits(phoneTxt)
            if digitsPhoneTxt.count < CTCAUIConstants.minLengthForPhoneTextField {
                pharmPhoneLabel.textColor = UIColor.red
                return .INVALID_PHONE
            }
        }
        pharmPhoneLabel.textColor = MyCTCAColor.formLabel.color
        return .NONE
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
        
        pharmPhoneInput.inputAccessoryView = doneToolbar
    }
    
    // MARK - UITextFieldDelegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        doesHaveFocus()
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        pharmPhoneLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        pharmPhoneInput.layer.cornerRadius = 5
        pharmPhoneInput.layer.masksToBounds = true
        pharmPhoneInput.layer.borderWidth = 0.5
        pharmPhoneInput.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        pharmPhoneLabel.textColor = MyCTCAColor.formLabel.color
        pharmPhoneInput.layer.cornerRadius = 5
        pharmPhoneInput.layer.masksToBounds = true
        pharmPhoneInput.layer.borderWidth = 0.5
        pharmPhoneInput.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        pharmPhoneInput.resignFirstResponder()
    }
    
    func getData() -> String {
        return self.pharmPhoneInput.text!
    }

    //UITextField delegates
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if let phoneTextField = textField as? PhoneNumberTextField {
            return phoneTextField.processInput(inputText: textField.text ?? "", range: range, string: string)
        }
        return true
    }
}

