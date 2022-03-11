//
//  NewApptFormCommentTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 16/12/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

@available(iOS 13.0, *)
class NewApptFormCommentTableViewCell: UITableViewCell {

    @IBOutlet weak var textView: NewCTCATextView!
    @IBOutlet weak var titleLabel: UILabel!
    
    var pageIndex:AppointmentsRequestFormPage = .NONE
    weak var parent: NewApptFormCommentsViewController?
    
    var setComments:((_ text:String) -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        
        textView.delegate = self
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func configure(index: AppointmentsRequestFormPage) {
        pageIndex = index
        
        if pageIndex == .COMMENTS || pageIndex == .EDITMODE_COMMENTS || pageIndex == .CANCEL_COMMENTS {
            textView.placeholderText = "Preferred provider, etc."

            let text = "Do you have any additional comments?"
            GenericHelper.shared.updateTitleWithPrefix(label: titleLabel, title: text, isOptional: true)
        } else {
            if AppointmentsManager.shared.requestType == .cancel {
                textView.placeholderText = "Reason for cancellation"
                
                let text = "Why are you cancelling your appointments?"
                GenericHelper.shared.updateTitleWithPrefix(label: titleLabel, title: text)
            } else {
                GenericHelper.shared.updateTitleWithPrefix(label: titleLabel, title: nil)
            }
        }
        
        var textDisplay = ""
        if pageIndex == .EDITMODE_REASON {
            textDisplay = AppointmentsManager.shared.requestAppointment.reason
            textView.text = textDisplay
        } else if pageIndex == .EDITMODE_COMMENTS {
            textDisplay = AppointmentsManager.shared.requestAppointment.additionalNotes
            textView.text = textDisplay
        } else {
            textDisplay = textView.text
        }
        
        if pageIndex == .REASON {
            AppointmentsManager.shared.requestAppointment.reason = textView.text
        } else if pageIndex == .COMMENTS || pageIndex == .CANCEL_COMMENTS {
            AppointmentsManager.shared.requestAppointment.additionalNotes = textView.text
        }
        
        if let action = setComments {
            action(textDisplay)
        }
        
    }
    
    func isValidText() -> Bool {
        if pageIndex == .REASON || pageIndex == .EDITMODE_REASON {
            var reasonTxt:String = textView.text!
            reasonTxt = reasonTxt.trimmingCharacters(in: .whitespacesAndNewlines)
            
            if (reasonTxt.isEmpty) {
                textView.markInvalidEntry()
                return false
            }
        }
        return true
    }

}

@available(iOS 13.0, *)
extension NewApptFormCommentTableViewCell: UITextViewDelegate {
    func textViewDidBeginEditing(_ textView: UITextView) {
        if let textV = textView as? NewCTCATextView {
            textV.doesHaveFocus()
        }
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        if let textV = textView as? NewCTCATextView {
            textV.doesNotHaveFocus()
        }
        
        if let action = setComments {
            action(textView.text!)
        }
    }
    
    func textViewDidChange(_ textView: UITextView) {
        if pageIndex == .REASON || pageIndex == .EDITMODE_REASON  {
            if let text = textView.text, !(text.trimmingCharacters(in: .whitespacesAndNewlines)).isEmpty {
                if let parentVC = parent {
                    parentVC.canMoveNext()
                }
            } else {
                if let parentVC = parent {
                    parentVC.cannotMoveNext()
                }
            }
        }
    }
}

