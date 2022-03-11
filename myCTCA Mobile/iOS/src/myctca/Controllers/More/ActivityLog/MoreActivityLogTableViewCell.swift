//
//  MoreActivityLogTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 3/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreActivityLogTableViewCell: UITableViewCell {

    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var activityLabel: UILabel!
    @IBOutlet weak var userLabel: UILabel!
    
    func prepareView(logRec: ActivityLog?) {
        
        if let logItem = logRec {
            timeLabel.text = logItem.timeStr
            activityLabel.text = logItem.formattedMessage
            userLabel.text = logItem.userName
        } else {
            timeLabel.text = ""
            activityLabel.text = ""
            userLabel.text = ""
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
