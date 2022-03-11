//
//  MailSendNewMessageTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailSendNewMessageTableViewCell: UITableViewCell, UITextViewDelegate, CTCATableViewCellProtocol {

    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var messageTV: CTCATextView!
    
    override func awakeFromNib() {
        super.awakeFromNib()

        self.messageTV.delegate = self
        doesNotHaveFocus();
        
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    // MARK: - Text View Delegate
    func textViewDidBeginEditing(_: UITextView) {
        doesHaveFocus()
    }
    
    func textViewDidEndEditing(_: UITextView) {
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        messageLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        messageTV.layer.cornerRadius = 5
        messageTV.layer.masksToBounds = true
        messageTV.layer.borderWidth = 0.5
        messageTV.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        messageLabel.textColor = MyCTCAColor.formLabel.color
        messageTV.layer.cornerRadius = 5
        messageTV.layer.masksToBounds = true
        messageTV.layer.borderWidth = 0.5
        messageTV.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func isValid() -> Bool {
        let msgTxt:String = messageTV.text!
        print("MailSendNewMessageTableViewCell isValid msgTxt: \(msgTxt)")
        if (msgTxt != "") {
            messageLabel.textColor = MyCTCAColor.formContent.color
            return true
        }
        messageLabel.textColor = UIColor.red
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
        
        messageTV.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        _ = messageTV.resignFirstResponder()
    }
    
    func getData() -> String {
        return self.messageTV.text!
    }
}
