//
//  TelhealthViewController.swift
//  myctca
//
//  Created by Manjunath K on 4/7/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import AzureCommunicationCalling
import AVFoundation
import AVKit

protocol TelehealthProtocol : AnyObject {
    func didMeetingEnd()
}

class TelehealthViewController: AudioSupportViewController {
    
    var tapGesture:UITapGestureRecognizer?
    var actionViewHidden = false
    private var idleTimer: Timer?
    private var idleDuration = 5.0 //sec
    private var distance:CGFloat = 0.0
    private var timer:Timer?
    private var durationInSec = 0
    private var stopWatch = StopWatchTimer()
    
    @IBOutlet weak var meetingView: UIView!
    @IBOutlet weak var meetingTitleLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
    @IBOutlet weak var connectingTitleLabel: UILabel!
    @IBOutlet weak var remoteUserAvatarInitialsLabel: UILabel!
    @IBOutlet weak var localVideoView: UIView!
    @IBOutlet weak var localUserAvatarInitialLabel: UILabel!
    @IBOutlet weak var actionsView: UIView!
    @IBOutlet weak var cameraButton: SwitchButton!
    @IBOutlet weak var microphoneButton: SwitchButton!
    @IBOutlet weak var speakerButton: UIButton!
    @IBOutlet weak var hangupButton: UIButton!
    @IBOutlet weak var remoteMicrophoneButton: UIButton!
    @IBOutlet weak var inLobbyLabel: UILabel!
    @IBOutlet weak var remoteVideoView: UIView!
    @IBOutlet weak var remoteUserName: UILabel!
    @IBOutlet weak var flipCameraButton: UIButton!
    @IBOutlet weak var pateintView: UIView!
    @IBOutlet weak var waitingTimerView: UIView!
    @IBOutlet weak var waitingTimerLabel: UILabel!
    
    weak var delegate:TelehealthProtocol?
    
    var telehealthDetail:TelehealthDetail?
    var localUserName = AppSessionManager.shared.currentUser.userProfile?.fullName ?? ""
    
    let reachability = Reachability()!
    
    var radiantView:UIView?
    var radientLayer:RadialGradientLayer?
    private var audioOutManager:AudioOutManager?
    private var callMode:TelehealthCallMode = .IDLE
    private var userMessage = TelehealthMsgConstants.inLobbyMessage
    private let callManager = CallManager()
    private var patientWaitingTime = 1740//1800//sec, i.e 30 mins
    
    override func viewDidLoad() {
        super.viewDidLoad()
        callManager.delegate = self
        setup()
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_TELEHEALTH_MEETING)
    }
    
    deinit {
        print("TelehealthViewController.deinit")
    }
    
    func setup() {
        DispatchQueue.main.async(execute: { [self] in
            
            microphoneButton.onImage = UIImage(named: "mic_on")
            microphoneButton.offImage = UIImage(named: "mic_off")
            microphoneButton.toggle()
            
            cameraButton.onImage = UIImage(named: "camera_on")
            cameraButton.offImage = UIImage(named: "camera_off")
            cameraButton.toggle()
            
            if let name = telehealthDetail?.appointment?.description {
                meetingTitleLabel.text = name
            }
            
            audioOutRoutetype = telehealthDetail!.audioRoute
            setSpeakerButton()
        })
        
        audioOutManager = AudioOutManager(parent: self.view)
        audioOutManager?.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        distance = view.frame.height - (pateintView.frame.origin.y+self.pateintView.frame.height)
        
        switchToConnectingMode()
        AppSessionManager.shared.changeTimeoutPeriodForTelehealth()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    
        AppDelegate.AppUtility.lockOrientation(.portrait)
        stopWatch.reset()
        connect()
    }
    
    override func setSpeakerButton() {
        speakerButton.setImage(TelehealthServiceManager.shared.getImageForSpeaker(type: audioOutRoutetype), for: .normal)
    }
    
    func connect() {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
  
        if let _ = self.telehealthDetail?.appointment?.telehealthMeetingJoinUrl {
            
            TelehealthServiceManager.shared.setViews(remoteVideo: remoteVideoView, localVideo: localVideoView)
            
            TelehealthServiceManager.shared.delegate = self
            
            TelehealthServiceManager.shared.setUp(usernName:localUserName) {
                status in
                
                if status {
                    TelehealthServiceManager.shared.startCall() {
                        joined in
                        let timeTaken = self.stopWatch.stop()
                        print("connect timer : \(timeTaken)")
                        AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.DURATION_TELEHEALTH_JOIN_MEETING,  customInfo:["durationInSec": String(timeTaken)], appointment: self.telehealthDetail?.appointment)
                        if joined {
                            //Camera
                            if self.telehealthDetail!.cameraOn {
                                TelehealthServiceManager.shared.startVideo(withCameraFacing: self.telehealthDetail!.cameraType) {

                                    videoOn in

                                    if videoOn {
                                        DispatchQueue.main.async(execute: { [self] in
                                            self.pateintView.bringSubviewToFront(self.flipCameraButton)
                                            self.localUserAvatarInitialLabel.isHidden = true
                                        })
                                    } else {
                                        AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTRUPTED, customInfo:  ["methodName": "callAgent?.join"], appointment: self.telehealthDetail?.appointment)
                                        ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: self.telehealthDetail?.appointment)
                                    }
                                }
                            } else {
                                self.cameraButton.toggle()
                                self.flipCameraButton.isHidden = true
                            }
                            
                            //mic
                            if self.telehealthDetail!.micOn {
                                self.microphoneButtonTapped(UIButton())
                                self.microphoneButton.toggle()
                            }
                            
                            //network notification
                            self.registerForNetworkChangeNotification()
                            
                        } else {
                            AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTRUPTED, customInfo:  ["methodName": "callClient?.createCallAgent"], appointment: self.telehealthDetail?.appointment)
                            ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: self.telehealthDetail?.appointment)
                        }
                    }
                } else {
                    AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTRUPTED, customInfo:  ["methodName": "callAgent?.join"], appointment: self.telehealthDetail?.appointment)
                    ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: self.telehealthDetail?.appointment)
                }
            }
        } else {
            ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: self.telehealthDetail?.appointment)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool){
        super.viewWillDisappear(animated)
        AppSessionManager.shared.changeTimeoutPeriodToDefault()
        AppDelegate.AppUtility.lockOrientation(.all)
    }
    
    func applyAvatarLargeLabel(label:UILabel, name:String) {
        if name.isEmpty {
            label.text = ""
            return
        }
        let initials = name.components(separatedBy: " ").reduce("") { ($0 == "" ? "" : "\($0.first!)") + "\($1.first!)" }
        label.text = initials
        label.layer.cornerRadius = label.frame.height/2.0
        label.layer.borderWidth = 5.0
        label.layer.borderColor = UIColor.white.cgColor
        label.backgroundColor = .clear
        label.layer.backgroundColor = UIColor(red: 94/256, green: 156/256, blue: 211/256, alpha: 1.0).cgColor
    }
    
    func applyAvatarSmallLabel(label:UILabel, name:String) {
        if name.isEmpty {
            label.text = ""
            return
        }
        let initials = name.components(separatedBy: " ").reduce("") { ($0 == "" ? "" : "\($0.first!)") + "\($1.first!)" }
        label.text = initials
        label.layer.cornerRadius = label.frame.height/2.0
        label.backgroundColor = .clear
        label.layer.backgroundColor = UIColor(red: 94/256, green: 156/256, blue: 211/256, alpha: 1.0).cgColor
    }
    
    func switchToConnectingMode() {
        callMode = .CONNECTING
        
        print("switchToConnectingMode")
        DispatchQueue.main.async(execute: { [self] in
            
            applyAvatarLargeLabel(label: remoteUserAvatarInitialsLabel, name: localUserName)
            
            meetingView.isHidden = true
            pateintView.isHidden = true
            inLobbyLabel.isHidden = true
            remoteMicrophoneButton.isHidden = true
            remoteUserName.isHidden = true
            remoteVideoView.isHidden = true
            
            remoteUserAvatarInitialsLabel.isHidden = false
            cameraButton.isUserInteractionEnabled = false
            microphoneButton.isUserInteractionEnabled = false
            speakerButton.isUserInteractionEnabled = false
            
            cameraButton.alpha = 0.5
            microphoneButton.alpha = 0.5
        })
    }
    
    @objc func switchToInLobbyMode(_ forOnCall:Bool = false) {
        callMode = .IN_LOBBY

        if forOnCall {
            callMode = .WAITING
        }
        
        print("switchToInLobbyMode")
        stopWatch.reset()

        DispatchQueue.main.async(execute: { [self] in
            
            meetingView.isHidden = false
            remoteUserAvatarInitialsLabel.layer.borderColor = UIColor.clear.cgColor
            remoteUserAvatarInitialsLabel.isHidden = false
            inLobbyLabel.isHidden = false
            inLobbyLabel.text = userMessage
            applyAvatarLargeLabel(label: remoteUserAvatarInitialsLabel, name: localUserName)

            if self.actionViewHidden {
                self.actionsView.isHidden = false
                self.actionViewHidden = false
            } else {
                self.setUnsetTimer(set: false)
            }
            
            connectingTitleLabel.isHidden = true
            remoteMicrophoneButton.isHidden = true
            remoteUserName.isHidden = true
            pateintView.isHidden = true
            remoteVideoView.isHidden = true
            
            if callMode != .WAITING {
                startTimer()
            }
            showTimerIfNeeded()
        })
    }
    
    @objc func switchToWaitingMode() {
        callMode = .WAITING
        
        print("switchToWaitingMode")
        
        //if local video is rendering
        if cameraButton.status == true {
            
            DispatchQueue.main.async(execute: { [self] in
                
                meetingView.isHidden = false
                if self.actionViewHidden {
                    self.actionsView.isHidden = false
                    self.actionViewHidden = false
                } else {
                    self.setUnsetTimer(set: false)
                }
                
                connectingTitleLabel.isHidden = true
                remoteMicrophoneButton.isHidden = true
                remoteUserName.isHidden = true
                pateintView.isHidden = true
                
                remoteVideoView.isHidden = false
                
                TelehealthServiceManager.shared.switchLocalVideoViewToLarge(view: remoteVideoView)
                hideRemoteVideoStream()
                remoteUserAvatarInitialsLabel.isHidden = true
            })
        } else {
            switchToOnHoldMode(aboutToEnd: true)
            userMessage = TelehealthMsgConstants.waitingMessage
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                self.switchToInLobbyMode(true)
            }
        }
    }
    
    func switchToInCallMode() {
        callMode = .CONNECTED
        
        print("switchToInCallMode")

        let timeTaken = Int(self.stopWatch.stop())
        print("switchToInCallMode timer : \(timeTaken)")
        if timeTaken > 0 {
            AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.DURATION_TELEHEALTH_PATIENT_IN_LOBBY,  customInfo:["durationInSec": String(timeTaken)],
                                                         appointment: self.telehealthDetail?.appointment)
        }

        stopWatch.reset()
        
        DispatchQueue.main.async(execute: { [self] in
            remoteMicrophoneButton.isHidden = false
            pateintView.isHidden = false
            remoteUserName.isHidden = false
            remoteVideoView.isHidden = false
            inLobbyLabel.isHidden = true
            
            remoteUserAvatarInitialsLabel.layer.borderColor = UIColor.clear.cgColor
            
            applyAvatarSmallLabel(label: localUserAvatarInitialLabel, name: localUserName)
            
            self.setUnsetTimer(set: true)
            self.addTapGesture()
            
            cameraButton.isUserInteractionEnabled = true
            microphoneButton.isUserInteractionEnabled = true
            speakerButton.isUserInteractionEnabled = true
            
            cameraButton.alpha = 1.0
            microphoneButton.alpha = 1.0
            
            if radiantView == nil {
                radiantView = UIView(frame: self.view.frame)
                radiantView!.frame.origin = CGPoint(x: 0.0, y: 0.0)
                radiantView!.backgroundColor = MyCTCAColor.thBGColor.color
                self.view.addSubview(radiantView!)
                self.view.sendSubviewToBack(radiantView!)
            }
            
            if radientLayer == nil {
                radientLayer = RadialGradientLayer(centerColor: .white, edgeColor: MyCTCAColor.thBGColor.color)
                radientLayer!.frame = radiantView!.frame
            }
            
            self.waitingTimerView.isHidden = true
        })
    }
    
    func addRemoveGradient(add:Bool) {
        if let layer = radientLayer {
            if add {
                remoteUserAvatarInitialsLabel.layer.borderColor = UIColor.white.cgColor
                if let view = radiantView {
                    view.layer.addSublayer(layer)
                }
            } else {
                remoteUserAvatarInitialsLabel.layer.borderColor = UIColor.clear.cgColor
                layer.removeFromSuperlayer()
            }
        }
    }
    
    @objc func switchToDisconnectedMode() {
        callMode = .DISCONNECTED
        
        if let text = durationLabel.text {
            print("switchToDisconnectedMode - call duration : \(text)")
        } else {
            print("switchToDisconnectedMode")
        }
        
        if let call = TelehealthServiceManager.shared.call {
            print("callEndReason.code : \(call.callEndReason.code)")
            print("callEndReason.code : \(call.callEndReason.subcode)")
        }
        
        print("switchToDisconnectedMode : \(stopWatch.stop())")
       
        DispatchQueue.main.async(execute: { [self] in
            
            applyAvatarLargeLabel(label: remoteUserAvatarInitialsLabel, name: localUserName)
            
            meetingView.isHidden = true
            pateintView.isHidden = true
            inLobbyLabel.isHidden = true
            remoteMicrophoneButton.isHidden = true
            remoteUserName.isHidden = true
            remoteVideoView.isHidden = true
            connectingTitleLabel.isHidden = true
            actionsView.isHidden = false
            remoteUserAvatarInitialsLabel.isHidden = false

            cameraButton.isUserInteractionEnabled = false
            microphoneButton.isUserInteractionEnabled = false
            speakerButton.isUserInteractionEnabled = false
            
            stopTimer()
            
            if self.actionViewHidden {
                self.actionsView.isHidden = false
                self.actionViewHidden = false
            } else {
                self.setUnsetTimer(set: false)
            }
            
            cameraButton.alpha = 0.5
            microphoneButton.alpha = 0.5
        })
    }
    
    func switchToOnHoldMode(aboutToEnd:Bool = false) {
        callMode = .ON_HOLD_REMOTE
        
        print("switchToOnHoldMode")
        
        DispatchQueue.main.async(execute: { [self] in
            hideRemoteVideoStream()
            
            if aboutToEnd {
                inLobbyLabel.isHidden = false
                inLobbyLabel.text = TelehealthMsgConstants.leavingMessage
            } else {
                inLobbyLabel.isHidden = false
                inLobbyLabel.text = TelehealthMsgConstants.onHoldMessage
            }
        })
    }
    
    func switchToOnHoldEndedMode() {
        callMode = .CONNECTED
        
        print("switchToOnHoldEndedMode")

        DispatchQueue.main.async(execute: { [self] in
            inLobbyLabel.isHidden = true
        })
    }
    
    @IBAction func cameraButtonTapped(_ sender: Any) {
        if cameraButton.status {
            
            TelehealthServiceManager.shared.stopVideo() {
                status in
                if status {
                    print("startStopVideo")
                    
                    self.flipCameraButton.isHidden = true
                    for view in self.localVideoView.subviews {
                        if view.isKind(of: RendererView.self) {
                            view.removeFromSuperview()
                            break
                        }
                    }
                    
                    if self.callMode == .WAITING {
                        self.remoteUserAvatarInitialsLabel.isHidden = false
                        self.inLobbyLabel.isHidden = false
                        self.inLobbyLabel.text = TelehealthMsgConstants.waitingMessage
                    } else {
                        self.localUserAvatarInitialLabel.isHidden = false
                    }
                } else {
                    print("Failed to startStopVideo")
                }
            }
        } else {
            TelehealthServiceManager.shared.startVideo(withCameraFacing: .front) {
                
                videoOn in
                
                if videoOn {
                    self.flipCameraButton.isHidden = false
                    
                    AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.ACTION_TELEHEALTH_VIDEO_ON, customInfo: nil, appointment: self.telehealthDetail?.appointment)
                    
                    if self.callMode == .WAITING {
                        self.remoteUserAvatarInitialsLabel.isHidden = true
                        TelehealthServiceManager.shared.switchLocalVideoViewToLarge(view: self.remoteVideoView)
                        
                        self.inLobbyLabel.isHidden = true
                    } else {
                        self.localUserAvatarInitialLabel.isHidden = true
                    }
                    
                } else {
                    AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTRUPTED, customInfo:  ["methodName": "call?.startVideo"], appointment: self.telehealthDetail?.appointment)
                    ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: self.telehealthDetail?.appointment)
                }
            }
        }
    }
    
    @IBAction func microphoneButtonTapped(_ sender: Any) {
        TelehealthServiceManager.shared.muteUnmute(mute:
                                                microphoneButton.status) {
            status in
            
            if status {
                print("muteUnmute")
            } else {
                print("Failed to muteUnmute")
            }
        }
    }

    @IBAction func speakerButtonTapped(_ sender: Any) {
        audioOutManager?.showAudioRoutePicker()
    }
    
    @IBAction func hangupTapped(_ sender: Any) {
        if TelehealthServiceManager.shared.call?.state == .connected ||
            TelehealthServiceManager.shared.call?.state == .inLobby {
            
            DispatchQueue.main.async(execute: {
                let keyWindow = UIApplication.shared.windows.filter {$0.isKeyWindow}.first

                if var topController = keyWindow?.rootViewController {
                    while let presentedViewController = topController.presentedViewController {
                        topController = presentedViewController
                    }
                    let alertVC = UIStoryboard(name: "Secondary", bundle: nil).instantiateViewController(withIdentifier: "MyCTCAFormLeaveMessageViewController") as! MyCTCAFormLeaveMessageViewController
                    alertVC.modalPresentationStyle = .overCurrentContext
                    alertVC.leaveAction = self.endMeeting
                    topController.present(alertVC, animated: true, completion: nil)
                    alertVC.setupForTelehealth()
                }
            })
            
            return
            
        } else {
            self.endMeeting()
        }
    }
    
    @IBAction func remoteMicrophoneTapped(_ sender: Any) {
    }
    
    @IBAction func flipCameraTapped(_ sender: Any) {
        TelehealthServiceManager.shared.switchLocalCamera() {
            status in
            
            if status {
            } else {
                print("Failed to  flipCamera")
            }
        }
    }
    
    @objc func endMeeting() {
        callMode = .DISCONNECTED
        TelehealthServiceManager.shared.leaveMeeting() {
            status in
            
            if status {
                self.addRemoveGradient(add: false)
                TelehealthServiceManager.shared.cleanUp()
                
                self.dismiss(animated: true, completion: nil)

                if let del = self.delegate {
                    del.didMeetingEnd()
                }
            } else {
                print("Failed to leave")
            }
        }
    }
    
    func endMeetingDueToNetworkDisconnect() {
        callMode = .DISCONNECTED
        TelehealthServiceManager.shared.leaveMeeting() {
            status in
            
            if status {
                TelehealthServiceManager.shared.cleanUp()
                
                let closeAction = UIAlertAction(title: "OK",
                                             style: .default) { (_) in
                    
                    
                    self.dismiss(animated: true, completion: nil)

                    if let del = self.delegate {
                        del.didMeetingEnd()
                    }
                }
                
                GenericHelper.shared.showAlert(withtitle: TelehealthMsgConstants.noNetworkTelehealthErrorTitle, andMessage: TelehealthMsgConstants.noNetworkTelehealthMessage, onView: self,
                                               okaction:closeAction)
            } else {
                print("Failed to leave")
            }
        }
    }
    
    
    func addTapGesture() {
        tapGesture = UITapGestureRecognizer(target: self, action: #selector(tapGestureHandler(_:)))
        tapGesture!.numberOfTapsRequired = 1
        tapGesture!.numberOfTouchesRequired = 1
        
        self.view.addGestureRecognizer(tapGesture!)
        self.remoteVideoView.addGestureRecognizer(tapGesture!)
    }
    
    func removeTapGesture() {
        self.view.removeGestureRecognizer(tapGesture!)
        self.remoteVideoView.removeGestureRecognizer(tapGesture!)
        tapGesture = nil
    }
    
    @objc func tapGestureHandler(_ sender: UITapGestureRecognizer) {
        print("screen tapped")
        
        showOrHideActionView()
    }
    
    func moveLocalView(moveDown:Bool){
//        DispatchQueue.main.async(execute: { [self] in
//            print("moveLocalView : \(moveDown)  \(distance)")
//            if moveDown {
//                //move down
//                UIView.animate(withDuration: 0.1, delay: 0, usingSpringWithDamping: 0.8, initialSpringVelocity: 0, options: .curveEaseOut, animations: {
//                    self.pateintView.frame.origin.y = self.pateintView.frame.origin.y + distance
//                }, completion: nil)
//            }else{
//                //move up
//                UIView.animate(withDuration: 0.1, delay: 0, usingSpringWithDamping: 0.8, initialSpringVelocity: 0, options: .curveEaseOut, animations: {
//                    self.pateintView.frame.origin.y = self.pateintView.frame.origin.y - distance
//                }, completion: nil)
//            }
//        })
    }
    
    @objc func showOrHideActionView() {
        DispatchQueue.main.async(execute: {
            
            if self.actionViewHidden {
                self.actionsView.isHidden = false
                self.moveLocalView(moveDown:false)
                self.setUnsetTimer(set: true)
            } else {
                self.actionsView.isHidden = true
                self.moveLocalView(moveDown:true)
                self.setUnsetTimer(set: false)
            }
            
            self.actionViewHidden = !self.actionViewHidden
            
        })
    }
    
    func setUnsetTimer(set:Bool) {
        if (self.idleTimer != nil) {
            self.idleTimer?.invalidate()
            self.idleTimer = nil
        }
        
        if set {
            self.idleTimer = Timer.scheduledTimer(timeInterval: self.idleDuration,
                                                  target: self,
                                                  selector: #selector(self.showOrHideActionView),
                                                  userInfo: nil,
                                                  repeats: false)
        }
    }
    
    func switchToDisConnectingMode() {
        print("switchToDisConnectingMode")
    }
    
    override func didRotate(from fromInterfaceOrientation: UIInterfaceOrientation) {
        distance = view.frame.height - (pateintView.frame.origin.y+self.pateintView.frame.height)
        
        if let view = radiantView {
            view.frame = self.view.frame
            
            radientLayer = nil
            radientLayer = RadialGradientLayer(centerColor: .white, edgeColor: MyCTCAColor.thBGColor.color)
            radientLayer!.frame = view.frame
        }
        
    }
    
    func hideRemoteVideoStream() {
        self.remoteUserAvatarInitialsLabel.isHidden = false
        
        for view in self.remoteVideoView.subviews {
            if view.isKind(of: RendererView.self) {
                view.removeFromSuperview()
                break
            }
        }
    }
}

extension TelehealthViewController: TelehealthServiceProtocol {
    
    func didRemoteParticipantJoined() {
        print("didRemoteParticipantJoined")
        
        //This happens when patient is on call and docotor leaves the meeting and rejoins the call back.
        print(callMode)
        if callMode == .WAITING {
            TelehealthServiceManager.shared.setViews(remoteVideo: remoteVideoView, localVideo: localVideoView)
            
            if cameraButton.status == true {
                TelehealthServiceManager.shared.switchLocalVideoViewToSmall(view: localVideoView)
                flipCameraButton.isHidden = false
            } else {
                localUserAvatarInitialLabel.isHidden = false
                flipCameraButton.isHidden = true
            }
            
            switchToInCallMode()
        }
    }
    
    func didHostMuted(state: Bool) {
        print("didHostMuted")
        if state {
            microphoneButton.toggleToOff()
        } else {
            microphoneButton.toggleToOn()
        }
    }
    
    func didRemoteAudioStateChanged(state: Bool) {
        print("didRemoteAudioStateChanged")
        if state {
            remoteMicrophoneButton.setImage(UIImage(named: "mic_off"), for: .normal)
        } else {
            remoteMicrophoneButton.setImage(UIImage(named: "mic_on"), for: .normal)
        }
    }
    
    func didCallStateChanged(state: CallState) {
        print("didCallStateChanged : \(state.rawValue)")

        switch state {
        case .connecting:
            switchToConnectingMode()
        case .inLobby:
            switchToInLobbyMode()
        case .connected:
            switchToInCallMode()
        case .disconnecting:
            switchToDisConnectingMode()
        case .disconnected:
            switchToOnHoldMode(aboutToEnd: true)
            perform(#selector(endMeeting), with: nil, afterDelay: 2)
        case .ringing:
            print("Ringing")
        case .remoteHold:
            print("Remote on Hold")
        case .localHold:
            print("Local on Hold")
        case .none:
            //called in case of doctor denies patients call
            switchToDisconnectedMode()
        case .earlyMedia:
            print("earlyMedia")
        default:
            print("Something went wrong during telehealth call")
        }
    }
    
    func didRemoteVideoRendered() {
        self.remoteUserAvatarInitialsLabel.isHidden = true
    }
    
    func didRemoteUserNameAvailable(name:String) {
        remoteUserName.text = name
        applyAvatarLargeLabel(label: remoteUserAvatarInitialsLabel, name: name)
        remoteUserAvatarInitialsLabel.layer.borderColor = UIColor.clear.cgColor
    }
}

extension TelehealthViewController:  RemoteParticipantDelegate {
    func remoteParticipant(_ remoteParticipant: RemoteParticipant, didChangeState args: PropertyChangedEventArgs) {
        
        if remoteParticipant == TelehealthServiceManager.shared.physician {
            switch remoteParticipant.state {
            case .connected:
                print("didChangeState - connected")
                switchToOnHoldEndedMode()
                break
            case .disconnected:
                print("didChangeState - disconnected")
                let timeTaken = Int(self.stopWatch.stop())
                print("didChangeState - disconnected timer : \(timeTaken)")
                if timeTaken > 0 {
                    AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.DURATION_TELEHEALTH_TOTAL_MEETING,  customInfo:["durationInSec": String(timeTaken)],
                                                                 appointment: self.telehealthDetail?.appointment)
                }
                switchToWaitingMode()
                break
            case .hold:
                print("didChangeState - hold")
                switchToOnHoldMode()
                break
            case .idle:
                print("didChangeState - idle")
                break
            case .earlyMedia:
                print("didChangeState - earlyMedia")
                break
            case .ringing:
                print("didChangeState - ringing")
                break
            case .inLobby:
                print("didChangeState - inLobby")
                break
            case .connecting:
                print("didChangeState - connecting")
                break
            default:
                break
            }
        }

    }
    
    func remoteParticipant(_ remoteParticipant: RemoteParticipant, didChangeMuteState args: PropertyChangedEventArgs) {
        if remoteParticipant.isMuted {
            remoteMicrophoneButton.setImage(UIImage(named: "mic_off"), for: .normal)
        } else {
            remoteMicrophoneButton.setImage(UIImage(named: "mic_on"), for: .normal)
        }
        print("didChangeMuteState")
    }
    
    func remoteParticipant(_ remoteParticipant: RemoteParticipant, didChangeDisplayName args: PropertyChangedEventArgs) {
        print("didChangeDisplayName")
    }
    
    func remoteParticipant(_ remoteParticipant: RemoteParticipant, didChangeSpeakingState args: PropertyChangedEventArgs) {
        print("didChangeSpeakingState")
        
        if remoteParticipant.isSpeaking {
             addRemoveGradient(add: true)
         } else {
             addRemoveGradient(add: false)
         }
    }
    
    func remoteParticipant(_ remoteParticipant: RemoteParticipant, didUpdateVideoStreams args: RemoteVideoStreamsEventArgs) {
        
        print("didUpdateVideoStreams")
        DispatchQueue.main.async(execute: {
            
            if let videoStream = remoteParticipant.videoStreams.first, let remoteRenderer = try? VideoStreamRenderer(remoteVideoStream: videoStream), let videoView = self.remoteVideoView {
                if let targetRemoteParticipantView: RendererView = try? remoteRenderer.createView(withOptions: CreateViewOptions(scalingMode: ScalingMode.crop))
                {
                    let newFramr = CGRect(origin: CGPoint(x: 0, y: 0), size: videoView.frame.size)
                    targetRemoteParticipantView.frame = newFramr
                    
                    videoView.addSubview(targetRemoteParticipantView)
                    
                    self.didRemoteVideoRendered()
                }
                
                TelehealthServiceManager.shared.resetRemoteVideo()
                
                self.remoteUserAvatarInitialsLabel.isHidden = true
                
            } else {
                if remoteParticipant.state != .disconnected {
                    self.hideRemoteVideoStream()
                }
            }
        })
    }
}

extension TelehealthViewController {
    func startTimer() {
        durationInSec = 0
        timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(fire), userInfo: nil, repeats: true)
    }
    
    func stopTimer() {
        if let time = timer {
            time.invalidate()
            timer = nil
        }
    }

    @objc func fire() {
        var timeValue = ""
        durationInSec = durationInSec + 1
        
        if (durationInSec / 60) > 99 {
            let hrs = durationInSec / 3600
            var mns = durationInSec - (3600 * hrs)
            mns = mns / 60
            let sec = durationInSec - ((3600 * hrs) + (mns * 60))
            timeValue = String(format:"%02i:%02i:%02i", hrs, mns, sec)
        } else {
            timeValue = String(format:"%02i:%02i", (durationInSec / 60), (durationInSec % 60))
        }
        
        DispatchQueue.main.async(execute: {
            self.durationLabel.text = timeValue
        })
    }
        
    func registerForNetworkChangeNotification() {
            NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(note:)), name: ReachabilityChangedNotification, object: reachability)
            do{
                try reachability.startNotifier()
            }catch{
                print("could not start reachability notifier")
            }
    }
    
    func unRegisterForNetworkChangeNotification() {
        NotificationCenter.default.removeObserver(self, name: ReachabilityChangedNotification, object: nil)
    }
    
    @objc func reachabilityChanged(note: Notification) {
        
        let reachability = note.object as! Reachability
        
        switch reachability.currentReachabilityStatus {
        case .reachableViaWiFi:
            print("Network is back")
            
        case .reachableViaWWAN:
            print("Network is back")

        case .notReachable:
            print("Network not reachable")
            endMeetingDueToNetworkDisconnect()
            unRegisterForNetworkChangeNotification()
        }
    }
}

extension TelehealthViewController : TelephonyDelgate {
    func didPhoneCallConnected() {
        TelehealthServiceManager.shared.callOnHold() {
            status in
            
            if status {
                print("call on hold")
            } else {
                print("Failed to callOnHold")
            }
        }
    }
    
    func didPhoneCallEnded() {
        TelehealthServiceManager.shared.callResume() {
            status in
            
            if status {
                print("call resume")
            } else {
                print("Failed to resume")
            }
        }
    }
    
}

extension TelehealthViewController {
    func showTimerIfNeeded() {
        //telehealthDetail?.appointment?.startDateTime = "2021-07-08T17:30:00"
        if let startDateInLocalTZ = telehealthDetail?.appointment?.startDateInLocalTZ, callMode == .IN_LOBBY {
            
            let currentTime = Date()
            let diffComponents = Calendar.current.dateComponents([.hour, .minute, .second], from: currentTime, to: startDateInLocalTZ)
            
            //if schduled time is in 30minutes, show timer
            if let sec = diffComponents.second, let minute = diffComponents.minute,
               let hr = diffComponents.hour {
                var timeLeft = (hr * 3600) + (minute * 60) + sec
                self.perform(#selector(self.endWaiting), with: nil, afterDelay: TimeInterval(self.patientWaitingTime), inModes: [RunLoop.Mode.common])
                
                if timeLeft < 1 {
                    print("Timer on")
                    return
                }
                                
                waitingTimerView.isHidden = false
                waitingTimerLabel.text = ""
                
                Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [self] timer in
                    timeLeft -= 1
                    
                    if(timeLeft < 0) {
                        timer.invalidate()
                        self.waitingTimerView.isHidden = true
                        return
                    }
                    
                    var timeText = ""

                    let hrs = timeLeft / 3600
                    var mns = timeLeft - (3600 * hrs)
                    mns = mns / 60
                    let sec = timeLeft - ((3600 * hrs) + (mns * 60))
                    
                    if hrs > 0 {
                        timeText = String(format:"%i hours %i mins", hrs, mns)
                    } else if mns > 0 {
                        timeText = String(format:"%i mins %i secs", mns, sec)
                    } else {
                        timeText = String(format:"%i secs", sec)
                    }
                    
                    self.waitingTimerLabel.text = timeText
                }
            }
        }
    }
    
    @objc func endWaiting() {
        //show message if user is still in lobby
        if (self.callMode == .IN_LOBBY) {
            print("patient waited enough")
            
            var alertInfo = myCTCAAlert()
            alertInfo.leftBtnAction = {
                self.endMeeting()
            }
            
            alertInfo.image = UIImage(named: "no_one_joined")
            alertInfo.title = TelehealthMsgConstants.noOneJoinedMsgTitle
            alertInfo.message = TelehealthMsgConstants.noOneJoinedMessage
            
            GenericHelper.shared.showAlertLarge(info: alertInfo)
        }
    }
}
