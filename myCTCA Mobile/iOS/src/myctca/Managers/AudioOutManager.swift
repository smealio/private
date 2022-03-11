//
//  AudioOutManager.swift
//  myctca
//
//  Created by Manjunath K on 6/15/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import AVFoundation
import AVKit

let TITLE_AUDIO_OFF = "Audio off"
let TITLE_SPEAKER = "Speaker"
let TITLE_IPHONE = "iPhone"
let TITLE_IPAD = "iPad"
let TITLE_BT = "Bluetooth"
let CHECKED_KEY = "checked"

protocol AudioOutRouteChangeDelegate : AnyObject {
    func didChangeAudioRoute(to:AudioOutRouteType)
}

class AudioOutManager : NSObject {
    
    var parentView:UIView?
    var audioOuputPorts = [AVAudioSessionPortDescription]()
    var audioRouteOptionSheet:UIAlertController?
    var selectedPortName:String?
    var isSessionStarted = false
    var triggerButton:UIButton?
    let audioSession = AVAudioSession.sharedInstance()
    weak var delegate:AudioOutRouteChangeDelegate?
    
    init(parent:UIView) {
        parentView = parent
    }
    
    deinit {
        setSessionPlayerOff()
    }
    
    private lazy var routePickerView : AVRoutePickerView = {
            let routePickerView = AVRoutePickerView(frame: .zero)
            routePickerView.isHidden = true
            parentView!.addSubview(routePickerView)
            routePickerView.delegate = self
            return routePickerView
        }()
    
    func showAudioRoutePicker() {
        routePickerView.present()
    }
    
    func getCurrentAudioOutRoute() -> AudioOutRouteType {
        let outputs = audioSession.currentRoute.outputs
        
        var type:AudioOutRouteType = .NONE
        
        if outputs.count > 0 {
            let output = outputs.first
            
            switch output?.portType {
            
                case AVAudioSession.Port.builtInMic, AVAudioSession.Port.builtInReceiver:
                    type = .PHONE_MIC
                    break
                    
                case AVAudioSession.Port.bluetoothA2DP, AVAudioSession.Port.bluetoothHFP, AVAudioSession.Port.bluetoothLE :
                    type = .BLUETOOTH
                    break
                    
            case AVAudioSession.Port.builtInSpeaker:
                    type = .SPEAKER

                default:
                    type = .OTHER
                    break
            }
        }
        return type
    }

    func isBluetoothDeviceConnected() -> Bool {
        
        if audioOuputPorts.count == 0 {
            if let outputs = audioSession.availableInputs {
                audioOuputPorts = outputs
            }
        }

       print(audioSession.currentRoute.outputs.count)

        for op in audioSession.currentRoute.outputs {
            print(op.description)
        }

        for item in audioOuputPorts {
            if item.portType == AVAudioSession.Port.bluetoothA2DP ||
                item.portType == AVAudioSession.Port.bluetoothHFP ||
                item.portType == AVAudioSession.Port.bluetoothLE {
                return true
            }
        }

        return false
    }
    
    func showAudioRouteOptionSheet(onView:UIViewController, forButton:UIButton) {

        triggerButton = forButton
        
        if audioRouteOptionSheet == nil {
            createAudioRouteActionSheet()
        }
        
        if let actionSheet = audioRouteOptionSheet {
            alertViewSetupForIpad(actionSheet, forButton)
            onView.present(actionSheet, animated: true, completion: nil)
        }
    }
    
    private func createAudioRouteActionSheet() {
        #if targetEnvironment(simulator)
            return
        #else
        
        audioRouteOptionSheet = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        audioRouteOptionSheet!.view.tintColor = MyCTCAColor.ctcaGreen.color

        var actionsList = [UIAlertAction]()
        var defaultMicPort:AVAudioSessionPortDescription?
        var defaultMicPortAction: UIAlertAction?
        
        var headphonePort:AVAudioSessionPortDescription?
        var headphonePortAction: UIAlertAction?
        
        if audioOuputPorts.count == 0 {
            if let outputs = audioSession.availableInputs {
                audioOuputPorts = outputs
            }
        }
        
        for audioPort in audioOuputPorts {
            
            switch audioPort.portType {
            
                case AVAudioSession.Port.builtInMic, AVAudioSession.Port.builtInReceiver:
                    defaultMicPort = audioPort
                    defaultMicPortAction = getDefaultMicAction(defaulRcvrPort: audioPort)
                    break
                    
//                case AVAudioSession.Port.bluetoothA2DP, AVAudioSession.Port.bluetoothHFP, AVAudioSession.Port.bluetoothLE :
//                    let action = getActionForPort(port: audioPort, name: TITLE_BT)
//                    actionsList.append(action)
//                    break
                    
               case AVAudioSession.Port.headphones, AVAudioSession.Port.headsetMic:
                    headphonePort = audioPort
                    headphonePortAction = getActionForPort(port: audioPort, name: audioPort.portName)
                    actionsList.append(headphonePortAction!)
                    break
                
                case AVAudioSession.Port.carAudio:
                    let action = getActionForPort(port: audioPort, name: audioPort.portName)
                    actionsList.append(action)
                    break
                    
                default:
                    break
            }
        }
        
        if let _ = defaultMicPort {
            audioRouteOptionSheet!.addAction(defaultMicPortAction!)
        }
        
        audioRouteOptionSheet!.addAction(getSpeakerAction())
        
        if isBluetoothDeviceConnected() {
            audioRouteOptionSheet!.addAction(getBluetoothAction())
        }
        
        let offAction = getAudioOffAction()
        actionsList.append(offAction)
        
        for action in actionsList {
            audioRouteOptionSheet!.addAction(action)
        }
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel, handler: {
            (alert: UIAlertAction!) -> Void in
        })
        
        audioRouteOptionSheet!.addAction(cancelAction)
        
        if let port = headphonePort {
            do {
                try audioSession.setPreferredInput(port)
                setSelection(forAction: headphonePortAction)
                
                if let title = headphonePortAction?.title {
                    self.updateTriggerButton(output: title)
                    self.selectedPortName = title
                }
            } catch let error as NSError {
                print("audioSession error change to input: \(port.portName) with error: \(error.localizedDescription)")
            }
        } else if let port = defaultMicPort {
            do {
                try audioSession.setPreferredInput(port)
                setSelection(forAction: defaultMicPortAction)
                
                if let title = defaultMicPortAction?.title {
                    self.updateTriggerButton(output: title)
                    self.selectedPortName = title
                }
            } catch let error as NSError {
                print("audioSession error change to input: \(port.portName) with error: \(error.localizedDescription)")
            }
        }
                        
        #endif
    }
    
    private func getAudioOffAction() -> UIAlertAction {
        let offAction = UIAlertAction(title: TITLE_AUDIO_OFF, style: .default, handler: {
            (alert: UIAlertAction!) -> Void in
            
            do {
                // remove speaker if needed
                try self.audioSession.overrideOutputAudioPort(AVAudioSession.PortOverride.none)
                self.setSelection(forAction: nil)
                self.updateTriggerButton(output: TITLE_AUDIO_OFF)
                self.selectedPortName = ""
            } catch let error as NSError {
                print("audioSession error change to input: \(AVAudioSession.PortOverride.none.rawValue) with error: \(error.localizedDescription)")
            }
        })
        
        if let image = UIImage(named: "speaker_off") {
            offAction.setValue(image, forKey: "image")
        }
        
        return offAction
    }
    
    private func setSelection(forAction:UIAlertAction?) {
        if let actionSheet = audioRouteOptionSheet {
            for item in actionSheet.actions {
                item.setValue(false, forKey: CHECKED_KEY)
            }
        }
        
        if let action = forAction {
            action.setValue(true, forKey: CHECKED_KEY)
        }
    }
    
    private func getActionForPort(port: AVAudioSessionPortDescription, name:String) -> UIAlertAction {
            
        let action = UIAlertAction(title: name, style: .default) { (action) in
            do {
                try self.audioSession.setPreferredInput(port)
                self.selectedPortName = name
                self.setSelection(forAction: action)
                self.updateTriggerButton(output: name)
            } catch let error as NSError {
                print("audioSession error change to input: \(port.portName) with error: \(error.localizedDescription)")
            }
        }

        if let image = getImageForPort(port: port) {
            action.setValue(image, forKey: "image")
        }
        
        return action
    }
    
    private func getImageForPort(port: AVAudioSessionPortDescription) -> UIImage? {
        var newImage = UIImage(named: "speaker_on")

        switch port.portType {
            case .bluetoothA2DP, .bluetoothLE, .bluetoothHFP:
                newImage = UIImage(named: "bluetooth_speaker")
            case .builtInSpeaker:
                break
            case .builtInMic, .builtInReceiver:
                newImage = UIImage(named: "speaker_iphone")
                break
            default:
                break
        }
        return newImage
    }
    
    private func getSpeakerAction() -> UIAlertAction {
        let speakerOutput = UIAlertAction(title: TITLE_SPEAKER, style: .default, handler: {  (action) in
            self.audioSession.setPortToSpeaker()
            self.selectedPortName = TITLE_SPEAKER
            self.setSelection(forAction: action)
            self.updateTriggerButton(output: TITLE_SPEAKER)
        })
        
        if let image = UIImage(named: "speaker_on") {
            speakerOutput.setValue(image, forKey: "image")
        }
        
        return speakerOutput
    }
    
    private func getBluetoothAction() -> UIAlertAction {
        let speakerOutput = UIAlertAction(title: TITLE_SPEAKER, style: .default, handler: {  (action) in
            
//            do {
//                try self.audioSession.setPreferredInput(.)
//            } catch let error as NSError {
//                print("audioSession error turning on speaker: \(error.localizedDescription)")
//            }
            
            self.selectedPortName = TITLE_BT
            self.setSelection(forAction: action)
            self.updateTriggerButton(output: TITLE_BT)
        })
        
        if let image = UIImage(named: "bluetooth_speaker") {
            speakerOutput.setValue(image, forKey: "image")
        }
        
        return speakerOutput
    }
    
    
    private func getDefaultMicAction(defaulRcvrPort: AVAudioSessionPortDescription) -> UIAlertAction {
        //built in speaker
        var deviceName = TITLE_IPHONE
        if UIDevice.current.userInterfaceIdiom == .pad {
            deviceName = TITLE_IPAD
        }
        
        let action = UIAlertAction(title: deviceName, style: .default) { (action) in
            do {
                try self.audioSession.setPreferredInput(defaulRcvrPort)
                self.selectedPortName = deviceName
                self.setSelection(forAction: action)
                self.updateTriggerButton(output: deviceName)
            } catch let error as NSError {
                print("audioSession error change to input: \(deviceName) with error: \(error.localizedDescription)")
            }
        }
        
        if let image = UIImage(named: "speaker_iphone") {
            action.setValue(image, forKey: "image")
        }
        
        return action
    }
    
    private func alertViewSetupForIpad(_ optionMenu: UIAlertController, _ speakerButton: UIButton) {
        optionMenu.modalPresentationStyle = .popover
        if let presenter = optionMenu.popoverPresentationController {
            presenter.sourceView = speakerButton;
            presenter.sourceRect = speakerButton.bounds;
        }
    }
    
    func setSessionPlayerOn()
    {
        do {
            try audioSession.setCategory(.playAndRecord, options: [.allowBluetooth, .allowAirPlay, .allowBluetoothA2DP])
        } catch _ {
            print("Failed:SetSessionPlayerOn()-setCategory")
        }
        do {
            try audioSession.setActive(true)
        } catch _ {
            print("Failed:SetSessionPlayerOn()-setActive-true")
        }
        print("audioSession activated!!")
    }
    
    @objc func audioRouteChangeListener(notification:NSNotification) {
        let audioRouteChangeReason = notification.userInfo![AVAudioSessionRouteChangeReasonKey] as! UInt

            switch audioRouteChangeReason {
            case AVAudioSession.RouteChangeReason.newDeviceAvailable.rawValue:
                print("headphone plugged in")
            case AVAudioSession.RouteChangeReason.oldDeviceUnavailable.rawValue:
                print("headphone pulled out")
            case AVAudioSession.RouteChangeReason.override.rawValue:
                print("override")
            default:
                break
            }
    }
    
    func setSessionPlayerOff()
    {
        do {
            try audioSession.setActive(false)
        } catch _ {
            print("Failed:SetSessionPlayerOn()-setActive-false")
        }
    }
    
    func updateTriggerButton(output:String) {
        
        guard let button = triggerButton else {
            return
        }
        
        var newImage = UIImage(named: "speaker_on")
        
        switch output {
        case TITLE_IPHONE:
            print("iPhone")
            button.tag = AudioOutRouteType.PHONE_MIC.rawValue
            newImage = UIImage(named: "speaker_iphone")
            break
        case "iPad":
            print("iPad")
            button.tag = AudioOutRouteType.PHONE_MIC.rawValue
            newImage = UIImage(named: "speaker_iphone")
            break
        case TITLE_SPEAKER:
            print("Speaker")
            button.tag = AudioOutRouteType.SPEAKER.rawValue
            break
        case TITLE_BT:
            newImage = UIImage(named: "bluetooth_speaker")
            button.tag = AudioOutRouteType.BLUETOOTH.rawValue
            break
        case TITLE_AUDIO_OFF:
            newImage = UIImage(named: "speaker_off")
            triggerButton?.tag = AudioOutRouteType.AUDIO_OFF.rawValue
            break
        default:
            button.tag = AudioOutRouteType.OTHER.rawValue
            break
        }
        
        button.setImage(newImage, for: .normal)
        
        if let title = button.currentTitle, !title.isEmpty {
            button.setTitle(output, for: .normal)
        }
    }
}

extension AudioOutManager : AVRoutePickerViewDelegate {
    func routePickerViewDidEndPresentingRoutes(_ routePickerView: AVRoutePickerView) {
        print("routePickerViewDidEndPresentingRoutes")
        
        if let del = delegate {
            del.didChangeAudioRoute(to:getCurrentAudioOutRoute())
        }
    }
}
