//
//  PeriodSelectionViewController.swift
//  myctca
//
//  Created by Manjunath K on 8/13/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

protocol PeriodSelectionProtocol : AnyObject {
    func periodSelected(fromDate: String, toDate: String)
    func cancelledPeriodSelection()
}

class PeriodSelectionViewController: UIViewController {

    @IBOutlet weak var fromDateTF: CTCATextField!
    @IBOutlet weak var toDateTF: CTCATextField!
    
    @IBOutlet weak var fromLabel: UILabel!
    @IBOutlet weak var toLabel: UILabel!
    
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var applyButton: UIButton!
    @IBOutlet weak var subTitle: UILabel!
    
    var isDateLimited = true
    
    let fromDateRequestMessage = ClinicalSummaryMsgConstants.fromDateRequestMessage
    var userSelectedFromDate:Date?
    
    let toDateRequestMessage = ClinicalSummaryMsgConstants.toDateRequestMessage
    var userSelectedToDate:Date?
    
    weak var delegate:PeriodSelectionProtocol?
    var type:PeriodSelectionType = .None
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        // Listen For DatePicker Notification
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.setDTPODateTime(_:)),
                                               name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION), object: nil)
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
    }
    
    @IBAction func fromDateButtonTapped(_ sender: Any) {
        showDatePicker(forField: fromDateTF, withMessage: fromDateRequestMessage, withCurDate: fromDateTF.text)
    }
    
    @IBAction func toDateButtonTapped(_ sender: Any) {
        showDatePicker(forField: toDateTF, withMessage: toDateRequestMessage, withCurDate: toDateTF.text)
    }
    
    func showDatePicker(forField:UITextField, withMessage:String, withCurDate:String?) {
        self.view.endEditing(true)
        
        //set date if already choosen
        var curSelectedDate:Date? = nil
        if let cellText = withCurDate {
            if let date = DateConvertor.convertToDateFromString(dateString: cellText, inputFormat: .baseForm) {
                curSelectedDate = date
            }
        }
        
        let bgImage:UIImage = UIImage(named: "calendar_white.png")!
        
        if isDateLimited {
            DatePickerOverlay.shared.showOverlay(view: (self.view)!,
                                            message: withMessage,
                                            bgImage: bgImage, mode: .date,
                                            maxDate: Date(),
                                            currDate: curSelectedDate,
                                            dateForField: forField)
        } else {
            DatePickerOverlay.shared.showOverlay(view: (self.view)!,
                                             message: withMessage,
                                             bgImage: bgImage, mode: .date,
                                             currDate: curSelectedDate,
                                             dateForField: forField)
        }
    }
    
    // Call back for DateTimePickerOverlay
    @objc func setDTPODateTime(_ notification: NSNotification) {
        
        var setDateString = ""
        if let dateTimeStr = notification.object as? String {
            print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = DatePickerOverlay.DATE_FORMAT
            dateFormatter.timeZone = .current// TimeZone(abbreviation: "GMT+0:00") //Current time zone
            let birthDate = dateFormatter.date(from: dateTimeStr)
            dateFormatter.dateFormat = "MMM d, yyyy"
            setDateString = dateFormatter.string(from: birthDate!)
        }
        
        let notificationDict = notification.userInfo
        
        if (notificationDict != nil) {
            let callerField:UIView = notificationDict![DatePickerOverlay.DATE_FIELD_KEY] as! UIView
            if (callerField == fromDateTF) {
                fromDateTF.text = setDateString
                if let text = toDateTF.text, text.isEmpty && type == .Appointments {
                    toDateTF.text = setDateString
                }
            } else {
                toDateTF.text = setDateString
            }
        }
    }

    @IBAction func applyTapped(_ sender: Any) {
        if let fromDateText = fromDateTF.text, let toDateText = toDateTF.text, !fromDateText.isEmpty, !toDateText.isEmpty {
            if let vcDel = delegate {
                if GenericHelper.shared.isValidateStartAndEndDates(fromDateText,toDateText) {
                    
                    vcDel.periodSelected(fromDate: fromDateText, toDate: toDateText)
                    dismiss(animated: true, completion: nil)
                    
                } else {
                    GenericHelper.shared.showAlert(withtitle: "myCTCA", andMessage: CommonMsgConstants.invalidDatesMessage, onView: self)
                }
            }
        } else {
            GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.dateFilerTitle, andMessage: CommonMsgConstants.dateFilerMessage, onView: self)
        }
    }
    
    @IBAction func cancelTapped(_ sender: Any) {
        if let vcDel = delegate {
            vcDel.cancelledPeriodSelection()
        }
        dismiss(animated: true, completion: nil)
    }
}
