//
//  CTCACommentsTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 22/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCACommentsTableViewCell: UITableViewCell {

    @IBOutlet weak var cardView: CTCACardView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var subTitleLabel: UILabel!
    
    var index:AppointmentsRequestFormPage?
    var showEditPage:((AppointmentsRequestFormPage) -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func configureCell(title:String, subTitle:String) {
        cardView.setFocused()
        
        titleLabel.text = title
        subTitleLabel.text = subTitle
    }
    
    @IBAction func editTapped(_ sender: Any) {
        if let action = showEditPage, let pageId = index {
            action(pageId)
        }
    }
    
    func configContactCell() {
        titleLabel.text = "CONTACT PREFERENCE"
        
        var contactType = ""
        var contact = ""
        if AppointmentsManager.shared.requestAppointment.communicationPreference == CommunicationPref.CALL {
            contactType = "Phone call"
            contact = AppointmentsManager.shared.requestAppointment.phoneNumber
        } else if AppointmentsManager.shared.requestAppointment.communicationPreference == CommunicationPref.EMAIL {
            contactType = "Email message"
            contact = AppointmentsManager.shared.requestAppointment.Email
        }
        
        let attrStr1:NSMutableAttributedString = NSMutableAttributedString(string: contactType,
            attributes: [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue-Medium", size: 16.0)!])
        let attrStr2:NSMutableAttributedString = NSMutableAttributedString(string: "\n\(contact)", attributes: [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue", size: 14.0)!])
        
        attrStr1.append(attrStr2)

        // *** Create instance of `NSMutableParagraphStyle`
        let paragraphStyle = NSMutableParagraphStyle()

        // *** set LineSpacing property in points ***
        paragraphStyle.lineSpacing = 6 // Whatever line spacing you want in points

        // *** Apply attribute to string ***
        attrStr1.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attrStr1.length))

        // *** Set Attributed String to your label ***
        subTitleLabel.attributedText = attrStr1
    }
}
