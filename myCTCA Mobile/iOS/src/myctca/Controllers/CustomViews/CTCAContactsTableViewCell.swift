//
//  CTCAContactsTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 22/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCAContactsTableViewCell: UITableViewCell {
    
    @IBOutlet weak var sectionTitleLabel: UILabel!
    @IBOutlet weak var cardView: CTCACardView!
    
    @IBOutlet weak var detailsView1: UIView!
    @IBOutlet weak var title1Label: UILabel!
    @IBOutlet weak var subTitle1Label: UILabel!

    @IBOutlet weak var detailsView2: UIView!
    @IBOutlet weak var title2Label: UILabel!
    @IBOutlet weak var subTitle2Label: UILabel!
    @IBOutlet weak var dashedLineView2: UIView!
    
    @IBOutlet weak var detailsView3: UIView!
    @IBOutlet weak var title3Label: UILabel!
    @IBOutlet weak var subTitle3Label: UILabel!
    @IBOutlet weak var dashedLineView3: UIView!
    
    @IBOutlet weak var stackView: UIStackView!
    var index:AppointmentsRequestFormPage?
    var showEditPage:((AppointmentsRequestFormPage) -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        selectionStyle = .none
    }
    
    override func prepareForReuse() {
        print("prepareForReuse")
        stackView.removeArrangedSubview(detailsView1)
        stackView.removeArrangedSubview(detailsView2)
        stackView.removeArrangedSubview(detailsView3)
        
        title1Label.text = ""
        title2Label.text = ""
        title3Label.text = ""
        
        subTitle1Label.text = ""
        subTitle2Label.text = ""
        subTitle3Label.text = ""
        
        stackView.addArrangedSubview(detailsView1)
        stackView.addArrangedSubview(detailsView2)
        stackView.addArrangedSubview(detailsView3)
        
        detailsView1.isHidden = false
        detailsView2.isHidden = false
        detailsView3.isHidden = false
        
        dashedLineView2.fillWithDashedLine(color: UIColor.ctca_gray40)
        dashedLineView3.fillWithDashedLine(color: UIColor.ctca_gray40)

    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func configureDateCell() {
        cardView.setFocused()
        sectionTitleLabel.text = "PREFERRED DATES AND TIMES"

        let datesList = AppointmentsManager.shared.requestAppointment.appointmentDateTimes
        if datesList.count > 0 {
            let date1 = datesList[0]
            setDateAndTime(displayDate: date1, titleLabel: title1Label, subTitleLabel: subTitle1Label)
            
            if datesList.count > 1 {
                let date2 = datesList[1]
                setDateAndTime(displayDate: date2, titleLabel: title2Label, subTitleLabel: subTitle2Label)
                
                if datesList.count > 2 {
                    let date3 = datesList[2]
                    setDateAndTime(displayDate: date3, titleLabel: title3Label, subTitleLabel: subTitle3Label)
                } else {
                    stackView.removeArrangedSubview(detailsView3)
                    detailsView3.isHidden = true
                }
            } else {
                stackView.removeArrangedSubview(detailsView2)
                stackView.removeArrangedSubview(detailsView3)
                detailsView2.isHidden = true
                detailsView3.isHidden = true
            }
        } else {
            stackView.removeArrangedSubview(detailsView1)
            stackView.removeArrangedSubview(detailsView2)
            stackView.removeArrangedSubview(detailsView3)
        }
    }
    
    func configContactCell() {
        cardView.setFocused()
        sectionTitleLabel.text = "CONTACT PREFERENCE"
        
        if AppointmentsManager.shared.requestAppointment.communicationPreference == CommunicationPref.CALL {
            title1Label.text = "Phone call"
            subTitle1Label.text = AppointmentsManager.shared.requestAppointment.phoneNumber
        } else if AppointmentsManager.shared.requestAppointment.communicationPreference == CommunicationPref.EMAIL {
            title1Label.text = "Email message"
            subTitle1Label.text = AppointmentsManager.shared.requestAppointment.Email
        }
        
        detailsView3.isHidden = true
        detailsView2.isHidden = true
    }
    
    @IBAction func editTapped(_ sender: Any) {
        if let action = showEditPage, let pageId = index {
            action(pageId)
        }
    }
    
    private func setDateAndTime(displayDate:AppointmentDateTimes, titleLabel:UILabel, subTitleLabel:UILabel) {
        
        titleLabel.text = DateConvertor.convertToStringFromDate(date: displayDate.date, outputFormat: .fullMonthForm)
        
        var dayAndSlot = DateConvertor.convertToStringFromDate(date: displayDate.date, outputFormat: .onlyWeeekDayForm)
        
        switch displayDate.timePreference {
        case ApptTimePref.MORNING:
            dayAndSlot = dayAndSlot + ", 8 am to 12 pm"
        case ApptTimePref.AFTERNOON:
            dayAndSlot = dayAndSlot + ", 1 pm to 4 pm"
        case ApptTimePref.ALL_DAY:
            dayAndSlot = dayAndSlot + ", 8 am to 4 pm"
        default:
            dayAndSlot = ""
        }
        
        subTitleLabel.text = dayAndSlot
    }
}
