//
//  ApptChangeSubjectTableViewCell.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class ApptChangeSubjectTableViewCell: UITableViewCell, CTCATableViewCellProtocol {

    @IBOutlet weak var subjectLabel: UILabel!
    @IBOutlet weak var subjectContentLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setContent(_ txt: String) {
        self.subjectContentLabel.text = txt
    }
    
    func isValid() -> Bool {
        let subjectTxt:String = subjectContentLabel.text!
        print("ApptReqSubjectTableViewCell isValid fromTxt: \(subjectTxt)")
        if (subjectTxt != "") {
            subjectLabel.textColor = MyCTCAColor.formLabel.color
            return true
        }
        subjectLabel.textColor = UIColor.red
        return false
    }

    func getData() -> String {
        return self.subjectContentLabel.text!
    }
}
