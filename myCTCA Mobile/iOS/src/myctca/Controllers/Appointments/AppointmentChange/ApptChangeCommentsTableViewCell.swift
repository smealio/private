//
//  ApptChangeCommentsTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptChangeCommentsTableViewCell: UITableViewCell, UITextViewDelegate, CTCATableViewCellProtocol {

    @IBOutlet weak var commentLabel: UILabel!
    @IBOutlet weak var commentTV: CTCATextView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        self.commentTV.textColor = MyCTCAColor.formContent.color
        self.commentTV.delegate = self

        doesNotHaveFocus()
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
        commentLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        commentTV.layer.cornerRadius = 5
        commentTV.layer.masksToBounds = true
        commentTV.layer.borderWidth = 0.5
        commentTV.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func doesNotHaveFocus() {
        commentLabel.textColor = MyCTCAColor.formLabel.color
        commentTV.layer.cornerRadius = 5
        commentTV.layer.masksToBounds = true
        commentTV.layer.borderWidth = 0.5
        commentTV.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func isValid() -> Bool {
        let commentTxt:String = commentTV.text!
        print("ApptChangeCommentTableViewCell isValid fromTxt: \(commentTxt)")
        if (commentTxt != "") {
            commentTV.textColor = MyCTCAColor.formContent.color
            return true
        }
        commentLabel.textColor = UIColor.red
        return false
    }
    
    func getData() -> String {
        return self.commentTV.text!
    }
    
    func addDoneButtonOnKeyboard() {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: 320, height: 50))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        commentTV.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        print("DONE")
        _ = commentTV.resignFirstResponder()
    }

}
