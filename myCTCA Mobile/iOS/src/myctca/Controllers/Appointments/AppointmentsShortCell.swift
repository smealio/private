//
//  AppointmentsShort.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 28/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

protocol AppointmentsShortCellProtocol:AnyObject {
    func didSelectApptCell(appointment:Appointment)
    func didSelectJoinNow(appointment:Appointment)
}

class AppointmentsShortCell: UIView {

    @IBOutlet weak var titleLabelCell: UILabel!
    @IBOutlet weak var providersLabel: UILabel!
    @IBOutlet weak var timeAndLocLabel: UILabel!
    @IBOutlet weak var actionButton: UIButton!
    @IBOutlet weak var actionButtonHtConstraint: NSLayoutConstraint!
    @IBOutlet weak var providerLabelHtConstratint: NSLayoutConstraint!
    @IBOutlet var contentView: UIView!
    @IBOutlet weak var bottomBarView: UIView!
    private var appointment =  Appointment()
    private var moveApptToPast:(() -> Void)?
    
    private var cellState:ApptTimeState = .NONE
    
    weak var delegate:AppointmentsShortCellProtocol?
    
    @IBAction func actionButtonTapped(_ sender: Any) {
        if let del = delegate {
            del.didSelectJoinNow(appointment:appointment)
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func  commonInit() {
        Bundle(for: AppointmentsShortCell.self).loadNibNamed("AppointmentsShortCell", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = self.bounds
        
        contentView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    }
    
    func config(appointment: Appointment, isNextAppt:Bool = false, needBottomLine:Bool, moveApptToPast:(() -> Void)?) {
        self.appointment = appointment
        if let description = appointment.description {
            titleLabelCell.text = description
        }
        if let loc = appointment.location {
            timeAndLocLabel.text = appointment.getFormattedStartTime() + " " + appointment.facilityTimeZone + ", " + loc
        } else {
            timeAndLocLabel.text = appointment.getFormattedStartTime() + " " + appointment.facilityTimeZone
        }
        
        if let resources = appointment.resources, !resources.isEmpty {
            providersLabel.text = resources
        } else {
            providersLabel.text = ""
        }
        
        if needBottomLine {
            bottomBarView.backgroundColor = UIColor.ctca_gray40
        }
        
        if isNextAppt {
            let text = setApptTimeState()
            
            switch cellState {
            case .NONE:
                actionButton.isHidden = true
                actionButtonHtConstraint.constant = 0.0
                self.layoutIfNeeded()
            default:
                actionButton.setTitle(text, for: .normal)
            }
            setButtonUI()
            self.moveApptToPast = moveApptToPast
            
        } else {
            actionButton.isHidden = true
            actionButtonHtConstraint.constant = 0.0
        }
        self.layoutIfNeeded()
    }
    
    func setApptTimeState() -> String {
        var label = ""

        if !appointment.isTeleHealth {
            actionButton.isUserInteractionEnabled = false
        }
        
        if let startDateInLocalTZ = appointment.startDateInLocalTZ {
            
            if Calendar.current.isDateInToday(startDateInLocalTZ) {
                let currentTime = Date()
                let diffComponents = Calendar.current.dateComponents([.second, .hour, .minute], from: currentTime, to: startDateInLocalTZ)
                
                if let minute = diffComponents.minute,
                   let hr = diffComponents.hour, let secs = diffComponents.second {
                    
                    if hr == 0  {
                        if appointment.isTeleHealth {
                            if minute <= 30 && minute >= -30 {
                                cellState = .JOIN_TELEHEALTH
                                label = MyCTCAConstants.FormText.buttonJoinNow
                                setNowTimer(minutes: minute+30)
                            } else {
                                cellState = .START_LATER
                                label = "  Starts in \(minute) mins"
                                setMinTimer(hrs: 0, minutes: minute, secs: secs)
                            }
                        } else {
                            if minute <= 0 && minute >= -30 {
                                cellState = .NOW
                                label = "Now"
                                setNowTimer(minutes: minute)
                            } else {
                                cellState = .START_LATER
                                label = "  Starts in \(minute) mins"
                                setMinTimer(hrs: 0, minutes: minute, secs: secs)
                            }
                        }
                    } else {
                        cellState = .START_LATER
                        label = "  Starts in \(hr) hrs \(minute) mins"
                        setMinTimer(hrs: hr, minutes: minute, secs: secs)
                    }
                }
            } else if Calendar.current.isDateInTomorrow(startDateInLocalTZ) {
                label = "  Tomorrow"
                cellState = .TOMORROW
                setTomorrowElapseTimer()
            } else {
                cellState = .NONE
                
                if appointment.isTeleHealth {
                    if appointment.telehealthInfoUrl.count > 0 { //Disply setup guide
                        cellState = .SETUP_TELEHEALTH
                        label = MyCTCAConstants.FormText.buttonSetupGuide
                    }
                }
            }
        }
        
        return label
    }
    
    func setTomorrowElapseTimer() {
        var tomorrow = Calendar.current.date(byAdding: .day, value: 1, to: Date())!
        var components = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .second], from: tomorrow)

        components.minute = 0
        components.second = 0
        components.hour = 0

        tomorrow = Calendar.current.date(from: components)!
        
        let diffComponents = Calendar.current.dateComponents([.second, .hour, .minute], from: Date(), to: tomorrow)
        
        if let minute = diffComponents.minute,
           let hr = diffComponents.hour, let secs = diffComponents.second {
            
            var timeLeft = (hr * 60 * 60) + (minute * 60) + secs
            
            Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [self] timer in
                
                timeLeft -= 1
                
                if(timeLeft == 0) {
                    timer.invalidate()
                    
                    let diffComponentsToday = Calendar.current.dateComponents([.second, .hour, .minute], from: appointment.startDateInLocalTZ!)
                    if let mins = diffComponentsToday.minute,
                       let hrs = diffComponentsToday.hour, let sec = diffComponentsToday.second {
                        cellState = .START_LATER
                        setButtonUI()
                        setMinTimer(hrs: hrs, minutes: mins, secs:sec)
                    }
                    return
                }
            }
        }
    }

    func setHrTimer(days:Int, hrs:Int, secs:Int) {
        var timeLeft = (days * 24) + (hrs * 60)
        let secsInHr = 60 * 60
        Timer.scheduledTimer(withTimeInterval: TimeInterval(secsInHr), repeats: true) { [self] timer in
            
            timeLeft -= 1

            if(timeLeft == hrs) {
                timer.invalidate()
                setMinTimer(hrs: hrs, minutes: 60, secs: 0)
                return
            }
        }
    }
    
    func setMinTimer(hrs:Int, minutes:Int, secs: Int) {
        var timeLeft = (hrs * 60) + minutes
        let secsInMin = 60.0
        let startMeetingStartOn = appointment.isTeleHealth ? 30 : 0
        let remainingSeconds = secs < 0 ? 0 : secs

        print("remainingSeconds = \(remainingSeconds)")
        print("time now - \(Date())")
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(remainingSeconds), execute: {
            print("time now - \(Date())")
            Timer.scheduledTimer(withTimeInterval: secsInMin, repeats: true) { [self] timer in
                timeLeft -= 1

                if(timeLeft <= startMeetingStartOn) {
                    timer.invalidate()
                    if appointment.isTeleHealth {
                        cellState = .JOIN_TELEHEALTH
                    } else {
                        cellState = .NOW
                    }

                    setButtonUI()
                    setNowTimer(minutes:30)
                    return
                }
                
                if (timeLeft / 60 > 0) {
                    let timeText = "  Starts in \(timeLeft / 60) hrs \(timeLeft % 60) mins"
                    actionButton.setTitle(timeText, for: .normal)
                } else {
                    let timeText = "  Starts in \(timeLeft) mins"
                    actionButton.setTitle(timeText, for: .normal)
                }
            }
        })
    }
    
    func setNowTimer(minutes:Int) {
        var timeLeft = minutes
        let secsInMin = 60.0
        
        if appointment.isTeleHealth {
            actionButton.setTitle(MyCTCAConstants.FormText.buttonJoinNow, for: .normal)
        } else {
            actionButton.setTitle("Now", for: .normal)
        }
        
        Timer.scheduledTimer(withTimeInterval: secsInMin, repeats: true) { [self] timer in
            
            timeLeft -= 1
            
            if(timeLeft == -30 || timeLeft == 0) {
                timer.invalidate()
                setNotNextAppt()
                return
            }
        }
    }
    
    func setNotNextAppt() {
        print("setNotNextAppt")
        if let action = moveApptToPast {
            action()
        }
    }
    
    func setButtonUI() {
        //applicable for caregivers
        if !GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.joinTelehealth) &&
            (cellState == .JOIN_TELEHEALTH || cellState == .SETUP_TELEHEALTH) {
            cellState = .NONE
        }
        
        switch cellState {
        case .NOW, .SETUP_TELEHEALTH:
            actionButton.setImage(UIImage(named: ""), for: .normal)
            actionButton.backgroundColor = UIColor.ctca_gray10
            actionButton.setTitleColor(UIColor.ctca_theme_middle, for: .normal)
            actionButton.layer.borderColor = UIColor.clear.cgColor
        case .JOIN_TELEHEALTH:
            actionButton.setImage(UIImage(named: ""), for: .normal)
            actionButton.backgroundColor = UIColor.ctca_theme_middle
            actionButton.setTitleColor(.white, for: .normal)
        case .START_LATER, .TOMORROW:
            actionButton.setImage(UIImage(named: "timer"), for: .normal)
            actionButton.backgroundColor = UIColor.ctca_gray10
            actionButton.setTitleColor(UIColor.ctca_selection_purple, for: .normal)
            actionButton.layer.borderColor = UIColor.clear.cgColor
        default:
            actionButton.isHidden = true
            actionButtonHtConstraint.constant = 0.0
            self.layoutIfNeeded()
        }
        
        actionButton.layoutIfNeeded()
    }

    @IBAction func selectionBtnTapped(_ sender: Any) {
        if let del = delegate {
            del.didSelectApptCell(appointment:appointment)
        }
    }
}
