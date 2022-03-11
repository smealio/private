//
//  ApptReqPhoneTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptReqPhoneTableViewCell: UITableViewCell, CTCATableViewCellProtocol, UITextFieldDelegate {

    @IBOutlet weak var phoneLabel: UILabel!
    @IBOutlet weak var phoneTF: PhoneNumberTextField!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        phoneTF.delegate = self
        doesNotHaveFocus()
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
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
        phoneTF.layer.cornerRadius = 5
        phoneTF.layer.masksToBounds = true
        phoneTF.layer.borderWidth = 0.5
        phoneTF.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        phoneLabel.textColor = MyCTCAColor.formLabel.color
        phoneTF.layer.cornerRadius = 5
        phoneTF.layer.masksToBounds = true
        phoneTF.layer.borderWidth = 0.5
        phoneTF.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func isValidPhone() -> FormValidationResults {
        let phoneTxt:String = phoneTF.text!
        print("ApptReqDateTimeTableViewCell isValid fromTxt: \(phoneTxt)")
        if (phoneTxt == "") {
            phoneLabel.textColor = UIColor.red
            return .INVALID_FORM
        } else {
            let digits = PhoneNumberFormatter.shared.removeAllNonDigits(phoneTxt)
            if digits.count < CTCAUIConstants.minLengthForPhoneTextField {
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

        phoneTF.inputAccessoryView = doneToolbar
    }

    @objc func doneButtonAction() {
        print("DONE")
        phoneTF.resignFirstResponder()
    }

    func getData() -> String {
        return self.phoneTF.text!
    }
    
    //UITextField delegates
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if let phoneTextField = textField as? PhoneNumberTextField {
            return phoneTextField.processInput(inputText: textField.text ?? "", range: range, string: string)
        }

        return true
    }
    
    
}
