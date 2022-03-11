//
//  NewApptFormDateTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class NewApptFormDateTableViewCell: UITableViewCell {

    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var cardView: CTCACardView!
        
    var editAction:((_ date:NewApptCalenderSelection) -> Void)?
    var deleteAction:((_ date:NewApptCalenderSelection) -> Void)?
    private var curDate:NewApptCalenderSelection?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        selectionStyle = .none
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func editButtonTapped(_ sender: Any) {
        if let action = editAction, let date = curDate {
            action(date)
        }
    }
    
    @IBAction func deleteButtonTapped(_ sender: Any) {
        if let action = deleteAction, let date = curDate {
            action(date)
        }
    }
    
    func prepareCell(data:NewApptCalenderSelection) {
        guard let date = data.date else {
            return
        }
        
        cardView.setFocused()
        
        curDate = data
                        
        var calanderDay = DateConvertor.convertToStringFromDate(date: date, outputFormat: .onlyWeeekDayForm)
        calanderDay = calanderDay + ", "
        
        if data.session1 && data.session2 {
            calanderDay = calanderDay + "8 am – 4 pm"
        } else if data.session2 {
            calanderDay = calanderDay + "1 pm – 4 pm"
        } else {
            calanderDay = calanderDay + "8 am – 12 pm"
        }
        
        let dateString = DateConvertor.convertToStringFromDate(date: date, outputFormat: .fullMonthForm)
        timeLabel.text = calanderDay
        dateLabel.text = dateString
    }
    
}


