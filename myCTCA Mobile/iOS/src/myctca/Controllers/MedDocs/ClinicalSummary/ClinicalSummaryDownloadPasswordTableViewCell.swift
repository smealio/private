//
//  ClinicalSummaryDownloadEmailTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 8/17/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class ClinicalSummaryDownloadPasswordTableViewCell: UITableViewCell, UITextFieldDelegate {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var toggelEyeButton: UIButton!
    var passwordIsVisible = false
    
    let doneToolbarHeight: CGFloat = 50.0
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        emailTF.delegate = self
        doesNotHaveFocus()
        addDoneButtonOnKeyboard()
        emailTF.isSecureTextEntry = true
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
        titleLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        emailTF.layer.cornerRadius = 5
        emailTF.layer.masksToBounds = true
        emailTF.layer.borderWidth = 0.5
        emailTF.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        titleLabel.textColor = MyCTCAColor.formLabel.color
        emailTF.layer.cornerRadius = 5
        emailTF.layer.masksToBounds = true
        emailTF.layer.borderWidth = 0.5
        emailTF.layer.borderColor = MyCTCAColor.formLines.color.cgColor
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

        emailTF.inputAccessoryView = doneToolbar
    }

    @objc func doneButtonAction() {
        print("DONE")
        emailTF.resignFirstResponder()
    }
    
    @IBAction func toggelEyeButtonTapped(_ sender: Any) {
        self.emailTF.isSecureTextEntry = self.passwordIsVisible

        self.passwordIsVisible = !self.passwordIsVisible

        if (self.passwordIsVisible == false) {
            self.toggelEyeButton.setImage(#imageLiteral(resourceName: "eyeslash") , for: .normal)
        } else {
            self.toggelEyeButton.setImage(#imageLiteral(resourceName: "eye") , for: .normal)
        }
    }
}
