//
//  MoreActivityLogFilterViewController.swift
//  myctca
//
//  Created by Manjunath K on 7/24/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

protocol ActivityLogFilterProtocol : AnyObject {
    func applyFilterOnActivityLogs(date: String, username: String, message: String);
}

class MoreActivityLogFilterViewController: UIViewController {
    
    @IBOutlet weak var dateTF: CTCATextField!
    @IBOutlet weak var usernameTF: CTCATextField!
    @IBOutlet weak var messageTF: CTCATextField!
    
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var usernameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    
    weak var delegate:ActivityLogFilterProtocol?
    
    let twentyFourHourRequestMessage = "Please select a date to which you want to search activity logs."
    var userSelectedDate:Date?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        usernameTF.delegate = self
        messageTF.delegate = self
        
        addDoneButtonOnKeyboard()
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

        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.cancelledDTPO(_:)),
                                               name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION), object: nil)
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
    }
  
    func addDoneButtonOnKeyboard() {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: 320, height: CTCAUIConstants.doneBarHeightForTextFields))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        self.usernameTF.inputAccessoryView = doneToolbar
        self.messageTF.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        self.usernameTF.resignFirstResponder()
        self.messageTF.resignFirstResponder()
    }
    
    @IBAction func applyTapped(_ sender: Any) {
        if let dateText = dateTF.text,
            let unText = usernameTF.text,
            let msgText = messageTF.text {
            if (dateText.count > 0 || unText.count > 0 || msgText.count > 0), let vcDel = delegate {                
                vcDel.applyFilterOnActivityLogs(date: dateText, username: unText, message: msgText)
            }
        }
        
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func cancelTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func dateTFTapped(_ sender: Any) {
        self.view.endEditing(true)
        
        //set date if already choosen
        var curSelectedDate:Date? = nil
        if let cellText = dateTF.text {
            if let date = DateConvertor.convertToDateFromString(dateString: cellText, inputFormat: .dayAndBaseForm) {
                curSelectedDate = date
            }
        }
        
        let bgImage:UIImage = UIImage(named: "calendar_white.png")!
        DatePickerOverlay.shared.showOverlay(view: (self.view)!,
                                             message: twentyFourHourRequestMessage,
                                             bgImage: bgImage, mode: .date,
                                             maxDate: Date(), currDate: curSelectedDate)
    }
    
    // Call back for DateTimePickerOverlay
    @objc func setDTPODateTime(_ notification: NSNotification) {
        if let dateTimeStr = notification.object as? String {
            
            let index = dateTimeStr.index(dateTimeStr.startIndex, offsetBy: 17)
            let dateSubstring = dateTimeStr[..<index]
            dateTF.text = String(dateSubstring)
        }
    }
    
    @objc func cancelledDTPO(_ notification: NSNotification) {
    }
}

extension MoreActivityLogFilterViewController : UITextFieldDelegate {
    func textFieldDidBeginEditing(_ textField: UITextField) {
        
        switch textField {
        case dateTF:
            dateTF.doesHaveFocus()
            dateLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
            
        case usernameTF:
            usernameTF.doesHaveFocus()
            usernameLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
            
        case messageTF:
            messageTF.doesHaveFocus()
            messageLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
            
        default:
            fatalError()
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        switch textField {
        case dateTF:
            dateTF.doesNotHaveFocus()
            dateLabel.textColor = MyCTCAColor.formLabel.color
            
        case usernameTF:
            usernameTF.doesNotHaveFocus()
            usernameLabel.textColor = MyCTCAColor.formLabel.color
            
        case messageTF:
            messageTF.doesNotHaveFocus()
            messageLabel.textColor = MyCTCAColor.formLabel.color
            
        default:
            fatalError()
        }
    }
}
