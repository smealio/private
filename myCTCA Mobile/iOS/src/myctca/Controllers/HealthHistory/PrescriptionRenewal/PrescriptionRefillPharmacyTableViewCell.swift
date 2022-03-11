//
//  PrescriptionRefillPharmacyTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillPharmacyTableViewCell: UITableViewCell, CTCATableViewCellProtocol, UITextFieldDelegate {
    
    @IBOutlet weak var pharmLabel: UILabel!
    @IBOutlet weak var pharmInput: UITextField!

    override func awakeFromNib() {
        super.awakeFromNib()
        pharmInput.delegate = self
        doesNotHaveFocus()
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func isValid() -> Bool {
        let phoneTxt:String = pharmInput.text!
        
        if (phoneTxt != "") {
            pharmLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        pharmLabel.textColor = UIColor.red
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
        
        pharmInput.inputAccessoryView = doneToolbar
    }
    
    // MARK - UITextFieldDelegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        doesHaveFocus()
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        pharmLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        pharmInput.layer.cornerRadius = 5
        pharmInput.layer.masksToBounds = true
        pharmInput.layer.borderWidth = 0.5
        pharmInput.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        pharmLabel.textColor = MyCTCAColor.formLabel.color
        pharmInput.layer.cornerRadius = 5
        pharmInput.layer.masksToBounds = true
        pharmInput.layer.borderWidth = 0.5
        pharmInput.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        pharmInput.resignFirstResponder()
    }
    
    func getData() -> String {
        return self.pharmInput.text!
    }
}

