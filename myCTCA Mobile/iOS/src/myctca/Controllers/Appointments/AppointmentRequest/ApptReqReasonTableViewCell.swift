//
//  ApptReqReasonTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptReqReasonTableViewCell: UITableViewCell, UITextViewDelegate, CTCATableViewCellProtocol {

    @IBOutlet weak var reasonLabel: UILabel!
    @IBOutlet weak var reasonTV: CTCATextView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        // Placeholder text for Text View
        self.reasonTV.delegate = self
        doesNotHaveFocus();
        
        addDoneButtonOnKeyboard()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    

    func textViewDidBeginEditing(_ textView: UITextView) {
        doesHaveFocus()
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        doesNotHaveFocus()
    }
    
    func doesHaveFocus() {
        reasonLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        reasonTV.layer.cornerRadius = 5
        reasonTV.layer.masksToBounds = true
        reasonTV.layer.borderWidth = 0.5
        reasonTV.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        reasonLabel.textColor = MyCTCAColor.formLabel.color
        reasonTV.layer.cornerRadius = 5
        reasonTV.layer.masksToBounds = true
        reasonTV.layer.borderWidth = 0.5
        reasonTV.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func isValid() -> Bool {
        let reasonTxt:String = reasonTV.text!
        print("ApptReqDateTimeTableViewCell isValid fromTxt: \(reasonTxt)")
        if (reasonTxt != "" ) {
            reasonLabel.textColor = MyCTCAColor.formContent.color
            return true
        }
        reasonLabel.textColor = UIColor.red
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
        
        reasonTV.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        _ = reasonTV.resignFirstResponder()
    }

    func getData() -> String {
        return self.reasonTV.text!
    }
}
