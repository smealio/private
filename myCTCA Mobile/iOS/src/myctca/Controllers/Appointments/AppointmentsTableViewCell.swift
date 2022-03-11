//
//  AppointmentsTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 28/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

protocol AppointmentsTableViewCellProtocol: AnyObject {
    func didSelectApptCell(appointment:Appointment)
    func didSelectJoinNow(appointment:Appointment)
}

class AppointmentsTableViewCell: UITableViewCell {

    @IBOutlet weak var sectionTitleLabel: UILabel!
    @IBOutlet weak var sectionHeaderLabel: UIView!
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var containerCardView: CTCACardView!
    weak var delegate:AppointmentsTableViewCellProtocol?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    override func prepareForReuse() {
        for subView in stackView.arrangedSubviews where type(of: subView) == AppointmentsShortCell.self {
            subView.removeFromSuperview()
        }
        containerCardView.setUnFocused()
    }
    
    func prepareView(labelText: String, appointmentsList: [Appointment], isNextAppt:Bool = false, moveApptToPast:(() -> Void)?) {
        sectionTitleLabel.text = labelText
        
        var isNextAppt_OnlyFirst = isNextAppt
        
        let count = appointmentsList.count
        for index in 0...count-1 {
            let appointmentCell = AppointmentsShortCell()
                        
            if index == count-1 {
                appointmentCell.config(appointment: appointmentsList[index], isNextAppt: isNextAppt_OnlyFirst, needBottomLine: false, moveApptToPast:moveApptToPast)
            } else {
                appointmentCell.config(appointment: appointmentsList[index], isNextAppt: isNextAppt_OnlyFirst, needBottomLine: true, moveApptToPast:moveApptToPast)
            }
            appointmentCell.delegate = self
            stackView.addArrangedSubview(appointmentCell)
            
            isNextAppt_OnlyFirst = (isNextAppt && isNextAppt_OnlyFirst) ? !isNextAppt_OnlyFirst : isNextAppt_OnlyFirst
        }
        
        if isNextAppt {
            containerCardView.setFocused()
        }
        
        self.layoutIfNeeded()
    }
    
}

extension AppointmentsTableViewCell: AppointmentsShortCellProtocol {
    func didSelectApptCell(appointment:Appointment) {
        if let del = delegate {
            del.didSelectApptCell(appointment:appointment)
        }
    }
    func didSelectJoinNow(appointment:Appointment) {
        if let del = delegate {
            del.didSelectJoinNow(appointment:appointment)
        }
    }
}
