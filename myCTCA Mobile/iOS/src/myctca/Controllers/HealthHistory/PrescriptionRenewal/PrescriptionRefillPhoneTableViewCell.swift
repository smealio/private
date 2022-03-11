//
//  PrescriptionRefillPhoneTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillPhoneTableViewCell: UITableViewCell, CTCATableViewCellProtocol, UITextFieldDelegate {

    @IBOutlet weak var phoneLabel: UILabel!
    @IBOutlet weak var phoneInput: PhoneNumberTextField!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        phoneInput.delegate = self
        doesNotHaveFocus()
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func isValidPhone() -> FormValidationResults {
        let phoneTxt:String = phoneInput.text!
        if (phoneTxt == "") {
            phoneLabel.textColor = UIColor.red
            return .INVALID_FORM
        } else {
            let digitsPhoneTxt = PhoneNumberFormatter.shared.removeAllNonDigits(phoneTxt)
            if digitsPhoneTxt.count < CTCAUIConstants.minLengthForPhoneTextField {
                phoneLabel.textColor = UIColor.red
                return .INVALID_PHONE
            }
        }
        phoneLabel.textColor = MyCTCAColor.formLabel.color
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
        
        phoneInput.inputAccessoryView = doneToolbar
    }
    
    // MARK - UITextFieldDelegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        doesHaveFocus()
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        phoneLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        phoneInput.layer.cornerRadius = 5
        phoneInput.layer.masksToBounds = true
        phoneInput.layer.borderWidth = 0.5
        phoneInput.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        phoneLabel.textColor = MyCTCAColor.formLabel.color
        phoneInput.layer.cornerRadius = 5
        phoneInput.layer.masksToBounds = true
        phoneInput.layer.borderWidth = 0.5
        phoneInput.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        phoneInput.resignFirstResponder()
    }
    
    func getData() -> String {
        return self.phoneInput.text!
    }
    
    //UITextField delegates
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if let phoneTextField = textField as? PhoneNumberTextField {
            return phoneTextField.processInput(inputText: textField.text ?? "", range: range, string: string)
        }
        return true
    }

}

