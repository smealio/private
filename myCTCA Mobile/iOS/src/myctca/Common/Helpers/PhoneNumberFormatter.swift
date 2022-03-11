//
//  PhoneNumberFormatter.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 16/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class PhoneNumberFormatter : NSObject {
    private override init() {
        super.init()
    }
    
    static let shared = PhoneNumberFormatter()
    
    func format(phoneNumber: String, shouldRemoveLastDigit: Bool = false) -> String {
        guard !phoneNumber.isEmpty else { return "" }
        guard let regex = try? NSRegularExpression(pattern: "[\\s-\\(\\)]", options: .caseInsensitive) else { return "" }
        let r = NSString(string: phoneNumber).range(of: phoneNumber)
        var number = regex.stringByReplacingMatches(in: phoneNumber, options: .init(rawValue: 0), range: r, withTemplate: "")

        if number.count > 10 {
            let tenthDigitIndex = number.index(number.startIndex, offsetBy: 10)
            number = String(number[number.startIndex..<tenthDigitIndex])
        }

        if shouldRemoveLastDigit {
            let end = number.index(number.startIndex, offsetBy: number.count-1)
            number = String(number[number.startIndex..<end])
        }

        if number.count < 7 {
            let end = number.index(number.startIndex, offsetBy: number.count)
            let range = number.startIndex..<end
            number = number.replacingOccurrences(of: "(\\d{3})(\\d+)", with: "($1) $2", options: .regularExpression, range: range)

        } else {
            let end = number.index(number.startIndex, offsetBy: number.count)
            let range = number.startIndex..<end
            number = number.replacingOccurrences(of: "(\\d{3})(\\d{3})(\\d+)", with: "($1) $2-$3", options: .regularExpression, range: range)
        }

        return number
    }
    
    func removeAllNonDigits(_ inputText:String) -> String {
        var text = inputText
        let vowels: Set<Character> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
        text.removeAll(where: { !vowels.contains($0) })
        return text
    }
    
    func convertToStandardFormat(_ inputText:String) -> String {
        //remove all non-digits
        var newText = removeAllNonDigits(inputText)
        
        if newText.count == 10 { //format only if count is 10
            newText = format(phoneNumber: newText)
        } else {
            newText = inputText
        }
        return newText
    }
    
}

extension PhoneNumberFormatter : UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        var fullString = textField.text ?? ""
        
        if fullString.first == "+" {
            return true
        }
        
        fullString.append(string)
        if range.length == 1 {
            textField.text = format(phoneNumber: fullString, shouldRemoveLastDigit: true)
        } else {
            textField.text = format(phoneNumber: fullString)
        }
        return false
    }
}
