//
//  MessageTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 9/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

protocol MessageTableViewCellProtocol : AnyObject {
    func didUserWantsToSeeMore()
}

class MessageTableViewCell: UITableViewCell {
    
    @IBOutlet weak var seeMoreHeightConst: NSLayoutConstraint!
    @IBOutlet weak var seeMoreBtn: UIButton!
    @IBOutlet weak var messageLabel: UILabel!
    
    weak var delegate: MessageTableViewCellProtocol?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func seeMoreTapped(_ sender: Any) {
        guard let del = delegate else {
            return
        }
        
        del.didUserWantsToSeeMore()
    }
}
