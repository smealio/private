//
//  PrescriptionRefillCommentsTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class PrescriptionRefillCommentsTableViewCell: UITableViewCell, UITextViewDelegate, CTCATableViewCellProtocol {

    @IBOutlet weak var commentsLabel: UILabel!
    @IBOutlet weak var commentsInput: UITextView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        self.commentsInput.text = ""
        self.commentsInput.delegate = self
        doesNotHaveFocus();
        
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func prepareView() {
        
    }

    // MARK: - Text View Delegate
    func textViewDidBeginEditing(_: UITextView) {
        doesHaveFocus()
    }
    
    func textViewDidEndEditing(_: UITextView) {
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        commentsLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        commentsInput.layer.cornerRadius = 5
        commentsInput.layer.masksToBounds = true
        commentsInput.layer.borderWidth = 0.5
        commentsInput.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        commentsLabel.textColor = MyCTCAColor.formLabel.color
        commentsInput.layer.cornerRadius = 5
        commentsInput.layer.masksToBounds = true
        commentsInput.layer.borderWidth = 0.5
        commentsInput.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func isValid() -> Bool {
        let msgTxt:String = commentsInput.text!
        print("MailSendNewMessageTableViewCell isValid msgTxt: \(msgTxt)")
        if (msgTxt != "") {
            commentsLabel.textColor = MyCTCAColor.formContent.color
            return true
        }
        commentsLabel.textColor = UIColor.red
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
        
        commentsInput.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        commentsInput.resignFirstResponder()
    }
    
    func getData() -> String {
        return self.commentsInput.text!
    }
}
