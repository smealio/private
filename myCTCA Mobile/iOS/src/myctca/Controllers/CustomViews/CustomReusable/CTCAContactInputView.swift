//
//  CTCAContactInputView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 20/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCAContactInputView: UIView {
    @IBOutlet weak var textField: PhoneNumberTextField!
    @IBOutlet weak var errorLabel: UILabel!
    @IBOutlet weak var contentView: UIView!
    
    var inputText = ""
    let errorTextPhEmpty = "Enter phone number"
    let errorTextEmailEmpty = "Enter email id"
    let errorTextPhInvalid = "Invalid phone number"
    let errorTextEmailInvalid = "Invalid email id"
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func  commonInit() {
        Bundle(for: CTCAContactInputView.self).loadNibNamed("CTCAContactInputView", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = self.bounds
        contentView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        errorLabel.text = ""
    }
    
    func validate() -> Bool {
        let result = textField.validate()
  
        switch result {
        case .INVALID_FORM:
            if textField.textFieldType == .PHONE {
                errorLabel.text = errorTextPhEmpty
            } else {
                errorLabel.text = errorTextEmailEmpty
            }
        case .INVALID_PHONE:
            errorLabel.text = errorTextPhInvalid
        case .INVALID_EMAIL:
            errorLabel.text = errorTextEmailInvalid
        default:
            return true
        }
        
        return false
    }
    
    func setContact(contact:String) {
        errorLabel.text = ""
        textField.text = contact
        textField.doesNotHaveFocus()
        
        if textField.textFieldType == .EMAIL {
            textField.keyboardType = .emailAddress
        }
    }
    
    func closeKeyPad() {
        textField.resignFirstResponder()
    }
}

extension CTCAContactInputView: UITextFieldDelegate {
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if let phoneTextField = textField as? PhoneNumberTextField {
            phoneTextField.doesHaveFocus()
            errorLabel.text = ""
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        if let phoneTextField = textField as? PhoneNumberTextField {
            phoneTextField.doesNotHaveFocus()
        }
        inputText = textField.text ?? ""
    }
    
    //UITextField delegates
     func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if let phoneTextField = textField as? PhoneNumberTextField {
            return phoneTextField.processInput(inputText: textField.text ?? "", range: range, string: string)
        }
        return true
     }
}
