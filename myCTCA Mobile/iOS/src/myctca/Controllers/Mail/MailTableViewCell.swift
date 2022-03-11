//
//  MailTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailTableViewCell: UITableViewCell {

    @IBOutlet weak var fromLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var subjectLabel: UILabel!
    
    @IBOutlet weak var unreadIndicator: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

    func prepareView(mail: Mail) {
        
        fromLabel.text = mail.from
        dateLabel.text = mail.getMonthDaySentString()
        subjectLabel.text = mail.subject
        
        if (mail.isRead == false) {
            fromLabel.font = UIFont(name:"HelveticaNeue-Medium", size: 15.0)
//            dateLabel.font = UIFont(name:"HelveticaNeue-Bold", size: 15.0)
//            subjectLabel.font = UIFont(name:"HelveticaNeue-Bold", size: 12.0)
            unreadIndicator.backgroundColor = MyCTCAColor.ctcaSecondGreen.color
        } else {
            fromLabel.font = UIFont(name:"HelveticaNeue", size: 15.0)
//            dateLabel.font = UIFont(name:"HelveticaNeue", size: 15.0)
//            subjectLabel.font = UIFont(name:"HelveticaNeue", size: 12.0)
            unreadIndicator.backgroundColor = UIColor.clear
        }
    }
}
