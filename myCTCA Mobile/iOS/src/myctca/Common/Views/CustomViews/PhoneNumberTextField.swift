//
//  PhoneNumberTextField.swift
//  myctca
//
//  Created by Manjunath K on 7/9/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class PhoneNumberTextField : CTCATextField {
    
    var textFieldType:InputTextType = .NONE
    
    func setPhoneNumber(number:String) {
        let digits = PhoneNumberFormatter.shared.removeAllNonDigits(number)
        if digits.count > 10 {
            text = number
        } else {
            text = PhoneNumberFormatter.shared.format(phoneNumber: number)
        }
    }
    
    func isMaxLengthReached(_ range: NSRange, _ string: String) -> Bool {

        if let currentString: NSString = self.text as NSString? {
            
            let newString: NSString =  currentString.replacingCharacters(in: range, with: string) as NSString
            
            return newString.length <= CTCAUIConstants.maxLengthForPhoneTextField
            
        }
        return false
    }
    
    func processInput(inputText:String, range: NSRange, string: String) -> Bool {
        
        if textFieldType == .PHONE {
            var fullString = inputText

            let digits = PhoneNumberFormatter.shared.removeAllNonDigits(inputText)
            if digits.count >= CTCAUIConstants.maxLengthForPhoneTextField && range.length != 1 {
                return false
            }
            
            if fullString.first == "+" {
                return true
            }
            
            fullString.append(string)
            if range.length == 1 {
                text = PhoneNumberFormatter.shared.format(phoneNumber: fullString, shouldRemoveLastDigit: true)
            } else {
                text = PhoneNumberFormatter.shared.format(phoneNumber: fullString)
            }
            
            return false
        }
        
        return true
    }
    
    func validate() -> FormValidationResults {
        if textFieldType == .PHONE {
            return isValidPhone()
        } else if textFieldType == .EMAIL {
            return isValidEmail()
        }
        return .NONE
    }
    
    func isValidPhone() -> FormValidationResults {
        if let phoneTxt = text {
            if (phoneTxt == "") {
                markInvalidEntry()
                return .INVALID_FORM
            } else {
                let digits = PhoneNumberFormatter.shared.removeAllNonDigits(phoneTxt)
                if digits.count < CTCAUIConstants.minLengthForPhoneTextField {
                    markInvalidEntry()
                    return .INVALID_PHONE
                }
            }
        }
        
        return .NONE
    }
    
    func isValidEmail() -> FormValidationResults {
        if let emailTxt = text {
            if (emailTxt == "") {
                markInvalidEntry()
                return .INVALID_FORM
            } else {
                if GenericHelper.shared.isValidEmail(emailTxt) {
                    return .VALID_FORM
                }
                return .INVALID_EMAIL
            }
        }
        
        return .NONE
    }
}

