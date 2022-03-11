//
//  DatePickerOverlay.swift
//  myctca
//
//  Created by Tomack, Barry on 12/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

/**
 This class returns a view containing a datepicker and a green box for a message/instruciton.
 */
class DatePickerOverlay {
    
    var overlayView: UIView = UIView()
    var datePicker: UIDatePicker = UIDatePicker(frame: CGRect(x: 0, y: 0, width: 250, height: 250))
    var controlView: UIView = UIView()
    
    var msgView: UIView = UIView()
    var msgLabel: UILabel?
    var bgImageView: UIImageView?
    
    var cancelButton: UIButton = UIButton()
    var selectButton: UIButton = UIButton()
    
    var callerTextField: UIView?
    
    static let shared: DatePickerOverlay = DatePickerOverlay()
    
    static let SELECTED_DATE_TIME_NOTIFICATION: String = "selectedDateTimeNotification"
    static let CANCELLED_DATE_TIME_NOTIFICATION: String = "cancelledDateTimeNotification"
    static let DATE_TIME_KEY: String = "dateTimeKey"
    static let DATE_FIELD_KEY: String = "dateForField"
    static let DATE_OBJECT: String = "dateObject"
    
    static let DATE_FORMAT: String = "EEE, MMM dd, yyyy, h:mm a"
    
    //This prevents others from using the default '()' initializer for this class.
    private init() { }
    
    public func showOverlay(view: UIView,
                            message: String?,
                            bgImage: UIImage?,
                            mode: UIDatePicker.Mode? = nil,
                            minDate: Date? = nil,
                            maxDate: Date? = nil,
                            currDate: Date? = nil,
                            dateForField: UIView? = nil) {
                
        let width = view.frame.size.width
        let height = view.frame.size.height
        let size = (width > height) ? width : height
        
        self.overlayView.frame = CGRect(x:view.frame.origin.x, y: view.frame.origin.y, width: size, height: size);
        self.overlayView.center = view.center
        self.overlayView.backgroundColor = MyCTCAColor.calanderOverlayBackground.color
        self.overlayView.clipsToBounds = true
        
        self.overlayView.addSubview(datePicker)

        datePicker.reloadInputViews()
        datePicker.translatesAutoresizingMaskIntoConstraints = false
        
        if #available(iOS 14.0, *) {
            if view.frame.height - (320.0+50.0) < 216.0 {
                //for devices with smaller screen
                datePicker.topAnchor.constraint(equalTo: self.overlayView.topAnchor, constant: 0.0).isActive = true
            } else {
                
                let appDelegate = UIApplication.shared.delegate as! AppDelegate
                if let rootNavCtrl = appDelegate.window?.rootViewController as? UINavigationController {
                    
                    let topSpace = rootNavCtrl.navigationBar.frame.height + UIApplication.shared.statusBarFrame.size.height
                    
                    datePicker.topAnchor.constraint(equalTo: self.overlayView.topAnchor, constant: topSpace).isActive = true

                } else {
                    datePicker.topAnchor.constraint(equalTo: self.overlayView.topAnchor, constant: 60.0).isActive = true
                }
            }
            
            datePicker.heightAnchor.constraint(equalToConstant: 340.0).isActive = true
            datePicker.widthAnchor.constraint(equalToConstant: 300.0).isActive = true
            datePicker.layer.borderWidth = 0.5
            datePicker.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
            
            let centerHorizontallyPicker = NSLayoutConstraint(item: datePicker,
                                                              attribute: .centerX,
                                                              relatedBy: .equal,
                                                              toItem: self.overlayView,
                                                              attribute: .centerX,
                                                              multiplier: 1.0,
                                                              constant: 0.0)
            
            if (minDate != nil) {
                datePicker.minimumDate = minDate!
                datePicker.date = minDate!
            }
            if (maxDate != nil) {
                datePicker.maximumDate = maxDate!
                if (minDate == nil) {
                    datePicker.date = maxDate!
                }
            }
            if (minDate == nil && maxDate == nil) {
                datePicker.date = Date()
            }
            
            if currDate != nil {
                datePicker.date = currDate!
            }
            
            if (mode != nil) {
                datePicker.datePickerMode = mode!
            }
            
            datePicker.tintColor = MyCTCAColor.ctcaGreen.color
            datePicker.preferredDatePickerStyle = .inline
            callerTextField = dateForField
            
            view.addSubview(self.overlayView)
            
            self.overlayView.addSubview(self.controlView)
            self.controlView.translatesAutoresizingMaskIntoConstraints = false
            self.controlView.heightAnchor.constraint(equalToConstant: 40).isActive = true
            self.controlView.widthAnchor.constraint(equalToConstant: 300).isActive = true
            self.controlView.topAnchor.constraint(equalTo: self.datePicker.bottomAnchor, constant: 5.0).isActive = true
            
            let centerHorizontallyControl = NSLayoutConstraint(item: controlView,
                                                               attribute: .centerX,
                                                               relatedBy: .equal,
                                                               toItem: self.overlayView,
                                                               attribute: .centerX,
                                                               multiplier: 1.0,
                                                               constant: 0.0)
            
            NSLayoutConstraint.activate([centerHorizontallyPicker, centerHorizontallyControl])
            
            
            self.controlView.addSubview(self.cancelButton)
            self.cancelButton.translatesAutoresizingMaskIntoConstraints = false
            self.cancelButton.leadingAnchor.constraint(equalTo: self.controlView.leadingAnchor, constant: 50.0).isActive = true
            self.cancelButton.centerYAnchor.constraint(equalTo: self.controlView.centerYAnchor).isActive = true
            cancelButton.heightAnchor.constraint(equalToConstant: 30.0).isActive = true
            cancelButton.widthAnchor.constraint(equalToConstant: 80.0).isActive = true
            
            cancelButton.titleLabel?.font =  UIFont(name: "HelveticaNeue-Bold", size: 15)
            cancelButton.layer.cornerRadius = 5
            cancelButton.layer.masksToBounds = true
            self.cancelButton.setTitle("Cancel",for: .normal)
            self.cancelButton.setTitleColor(MyCTCAColor.ctcaGreen.color, for: .normal)
            self.cancelButton.addTarget(self, action: #selector(cancelTapped(_:)), for: .touchUpInside)
            
            self.controlView.addSubview(self.selectButton)
            self.selectButton.translatesAutoresizingMaskIntoConstraints = false
            self.selectButton.trailingAnchor.constraint(equalTo: self.controlView.trailingAnchor, constant: -50.0).isActive = true
            self.selectButton.centerYAnchor.constraint(equalTo: self.controlView.centerYAnchor).isActive = true
            selectButton.heightAnchor.constraint(equalToConstant: 30.0).isActive = true
            selectButton.widthAnchor.constraint(equalToConstant: 80.0).isActive = true
            
            self.selectButton.setTitle("Select",for: .normal)
            self.selectButton.setTitleColor(MyCTCAColor.ctcaGreen.color, for: .normal)
            selectButton.titleLabel?.font =  UIFont(name: "HelveticaNeue-Bold", size: 15)
            selectButton.layer.cornerRadius = 5
            selectButton.layer.masksToBounds = true
            selectButton.backgroundColor = MyCTCAColor.ctcaGreen.color
            self.selectButton.setTitleColor(UIColor.white, for: .normal)
            self.selectButton.addTarget(self, action: #selector(selectTapped(_:)), for: .touchUpInside)
            
            //message view
            if let msg = message {
                addMessageToOverlay(message: msg, bgImage: bgImage)
            }
            
        } else {
            datePicker.heightAnchor.constraint(equalToConstant: 250).isActive = true
            datePicker.leadingAnchor.constraint(equalTo:self.overlayView.leadingAnchor).isActive = true
            datePicker.trailingAnchor.constraint(equalTo: self.overlayView.trailingAnchor).isActive = true
            datePicker.bottomAnchor.constraint(equalTo: self.overlayView.bottomAnchor).isActive = true
            
            if (minDate != nil) {
                datePicker.minimumDate = minDate!
                datePicker.date = minDate!
            }
            if (maxDate != nil) {
                datePicker.maximumDate = maxDate!
                if (minDate == nil) {
                    datePicker.date = maxDate!
                }
            }
            if (minDate == nil && maxDate == nil) {
                datePicker.date = Date()
            }
            
            if (mode != nil) {
                datePicker.datePickerMode = mode!
            }
            
            callerTextField = dateForField
            
            view.addSubview(self.overlayView)
            
            self.overlayView.addSubview(self.controlView)
            self.controlView.translatesAutoresizingMaskIntoConstraints = false
            self.controlView.heightAnchor.constraint(equalToConstant: 60).isActive = true
            self.controlView.leadingAnchor.constraint(equalTo:self.overlayView.leadingAnchor).isActive = true
            self.controlView.trailingAnchor.constraint(equalTo: self.overlayView.trailingAnchor).isActive = true
            self.controlView.bottomAnchor.constraint(equalTo: self.datePicker.topAnchor, constant: 25.0).isActive = true
            self.controlView.backgroundColor = MyCTCAColor.tableHeaderGrey.color
            
            self.controlView.addSubview(self.cancelButton)
            self.cancelButton.translatesAutoresizingMaskIntoConstraints = false
            self.cancelButton.leadingAnchor.constraint(equalTo: self.controlView.leadingAnchor, constant: 20.0).isActive = true
            self.cancelButton.centerYAnchor.constraint(equalTo: self.controlView.centerYAnchor).isActive = true
            self.cancelButton.setTitle("Cancel",for: .normal)
            self.cancelButton.setTitleColor(MyCTCAColor.ctcaGreen.color, for: .normal)
            self.cancelButton.addTarget(self, action: #selector(cancelTapped(_:)), for: .touchUpInside)
            
            self.controlView.addSubview(self.selectButton)
            self.selectButton.translatesAutoresizingMaskIntoConstraints = false
            self.selectButton.trailingAnchor.constraint(equalTo: self.controlView.trailingAnchor, constant: -20.0).isActive = true
            self.selectButton.centerYAnchor.constraint(equalTo: self.controlView.centerYAnchor).isActive = true
            self.selectButton.setTitle("Select",for: .normal)
            self.selectButton.setTitleColor(MyCTCAColor.ctcaGreen.color, for: .normal)
            self.selectButton.addTarget(self, action: #selector(selectTapped(_:)), for: .touchUpInside)
            
            if let msg = message {
                addMessageToOverlay(message: msg, bgImage: bgImage)
            }
        }
        
        self.overlayView.translatesAutoresizingMaskIntoConstraints = false
        self.overlayView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        self.overlayView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        self.overlayView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        self.overlayView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        
        self.overlayView.quickFadeIn()
    }
    
    private func addMessageToOverlay(message: String, bgImage: UIImage?) {
        self.overlayView.addSubview(self.msgView)
        
        self.msgView.center = CGPoint(x: overlayView.bounds.width / 2,
                                       y: overlayView.bounds.height / 2 + 40.0)
        
        self.msgView.layer.cornerRadius = 5
        self.msgView.layer.masksToBounds = true
        
        self.msgView.translatesAutoresizingMaskIntoConstraints = false
        self.msgView.leadingAnchor.constraint(equalTo:self.overlayView.leadingAnchor, constant: 20.0).isActive = true
        self.msgView.trailingAnchor.constraint(equalTo:self.overlayView.trailingAnchor, constant: -20.0).isActive = true
        if #available(iOS 14.0, *) {
            self.msgView.topAnchor.constraint(equalTo: self.controlView.bottomAnchor, constant: 20.0 ).isActive = true
        } else {
            self.msgView.bottomAnchor.constraint(equalTo: self.controlView.topAnchor, constant: -50.0 ).isActive = true
        }
        self.msgView.heightAnchor.constraint(greaterThanOrEqualToConstant: 80.0).isActive = true
        
        self.msgView.backgroundColor = MyCTCAColor.ctcaGreen.color
        
        self.overlayView.addSubview(self.msgView);
        
        if (self.msgLabel == nil) {
            self.msgLabel = UILabel()
            self.msgView.addSubview(self.msgLabel!)
        }
        
        self.msgLabel!.backgroundColor = UIColor.clear
        self.msgLabel!.numberOfLines = 6
        self.msgLabel!.lineBreakMode = .byWordWrapping
        self.msgLabel!.text = message
        
        self.msgLabel!.font = UIFont(name: "HelveticaNeue-Bold", size: 15)
        self.msgLabel!.numberOfLines = 5
        self.msgLabel!.textAlignment = .left
        self.msgLabel!.textColor = UIColor.white
        
        self.msgLabel!.center = CGPoint(x: overlayView.bounds.width / 2,
                                        y: overlayView.bounds.height / 2 + 40.0)
        
        self.msgLabel!.translatesAutoresizingMaskIntoConstraints = false
        self.msgLabel!.leadingAnchor.constraint(equalTo:self.msgView.leadingAnchor, constant: 20.0).isActive = true
        self.msgLabel!.trailingAnchor.constraint(equalTo: self.msgView.trailingAnchor, constant: -20.0).isActive = true
        self.msgLabel!.bottomAnchor.constraint(equalTo: self.msgView.bottomAnchor, constant: -20.0 ).isActive = true
        self.msgLabel!.topAnchor.constraint(equalTo: self.msgView.topAnchor, constant: 20.0 ).isActive = true
        
        if (bgImage != nil) {
            print("Got a new BGImage")
            if (self.bgImageView == nil) {
                self.bgImageView = UIImageView()
            }
            bgImageView!.image = bgImage
            bgImageView!.alpha = 0.25
            bgImageView!.contentMode = .scaleAspectFit
            
            self.msgView.addSubview(self.bgImageView!)
            self.msgView.sendSubviewToBack(self.bgImageView!)
            
            bgImageView!.translatesAutoresizingMaskIntoConstraints = false
            bgImageView!.heightAnchor.constraint(equalToConstant: 70.0).isActive = true
            bgImageView!.widthAnchor.constraint(equalToConstant: 70.0).isActive = true
            bgImageView!.trailingAnchor.constraint(equalTo: self.msgView.trailingAnchor, constant: -8.0).isActive = true
            bgImageView!.bottomAnchor.constraint(equalTo: self.msgView.bottomAnchor, constant: -8.0).isActive = true
        } else {
            self.bgImageView = nil
        }
    }
    
    @objc public func cancelTapped(_ sender: UIButton) {
        
        let notificationName = NSNotification.Name(rawValue: DatePickerOverlay.CANCELLED_DATE_TIME_NOTIFICATION)
        NotificationCenter.default.post(name: notificationName, object: nil, userInfo: nil)
        fadeOutOverlay()
    }
    
    @objc public func selectTapped(_ sender: UIButton) {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEE, MMM dd, yyyy, h:mm a"
        
        let inputDate = dateFormatter.string(from:datePicker.date)
        print("selectTapped: \(inputDate)")
                
        var dateTimeDict = [String: Any]()
        dateTimeDict[DatePickerOverlay.DATE_TIME_KEY] = inputDate
        dateTimeDict[DatePickerOverlay.DATE_OBJECT] = datePicker.date

        if (callerTextField != nil) {
            dateTimeDict[DatePickerOverlay.DATE_FIELD_KEY] = callerTextField!
        }
        
        let notificationName = NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION)
        NotificationCenter.default.post(name: notificationName, object: inputDate, userInfo: dateTimeDict)
        fadeOutOverlay()
    }
    
    public func hideOverlay() {
        overlayView.removeFromSuperview()
        controlView.removeFromSuperview()
        datePicker.removeFromSuperview()
        datePicker = UIDatePicker()
        
    }
    
    public func fadeOutOverlay() {
        self.overlayView.quickFadeOut(completion: { (finished: Bool) -> Void in
            self.hideOverlay()
        })
    }
}
