//
//  TelehealthServiceManager.swift
//  myctca
//
//  Created by Manjunath K on 4/1/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import AzureCommunicationCalling
import AVFoundation

protocol TelehealthServiceProtocol: AnyObject {
    func didRemoteParticipantJoined()
    func didCallStateChanged(state:CallState)
    func didRemoteVideoRendered()
    func didRemoteUserNameAvailable(name:String)
    func didRemoteAudioStateChanged(state:Bool)
    func didHostMuted(state:Bool)
}

final class TelehealthServiceManager {
    
    static let shared = TelehealthServiceManager()
    
    var callClient: CallClient?
    var callAgent: CallAgent?
    var call: Call?
    var deviceManager: DeviceManager?
    
    var localVideoMainView:UIView?
    var remoteVideoMainView:UIView?
    
    var localVideoView:RendererView?
    var remoteVideoView:RendererView?
    
    var localVideoStream: LocalVideoStream?
    var remoteVideoStream: RemoteVideoStream?
    
    var localVideoStreamRenderer: VideoStreamRenderer?
    var callObserver: CallObserver?
    
    private var acsAccessToken = ""
    private var meetingUrl:String?
    
    var physician:RemoteParticipant?
    weak var delegate: TelehealthServiceProtocol?
    
    func verifyAccessPermissions() -> Bool {
        if ( AVCaptureDevice.authorizationStatus(for: AVMediaType.audio) ==  .denied ||
                AVCaptureDevice.authorizationStatus(for: AVMediaType.video) == .denied) {

            var alertInfo = myCTCAAlert()

            alertInfo.title = TelehealthMsgConstants.noPermissionsErrorTitle
            alertInfo.message = TelehealthMsgConstants.noPermissionsErrorMessage

            alertInfo.rightBtnTitle = "Settings"
            alertInfo.rightBtnAction = {
                // open the app permission in Settings app
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url, options: [:], completionHandler: nil)
                }
            }

            alertInfo.leftBtnTitle = "Not Now"

            GenericHelper.shared.showAlert(info: alertInfo)
            return false
        }
        return true
    }
    
    func setDetails(token:String, url:String, completion: @escaping(Bool) -> Void) {
        acsAccessToken = token
        meetingUrl = url
        
        self.callClient = CallClient()

        self.callClient!.getDeviceManager(
            completionHandler: {
                [weak self]
                (deviceManager, error) in
                
                guard let self = self else { return }

                if (error == nil) {
                    print("Got device manager instance")
                    self.deviceManager = deviceManager
                    completion(true)
                } else {
                    print("Failed to get device manager instance")
                    completion(false)
                }
            })
    }
    
    func setViews(remoteVideo:UIView?, localVideo:UIView?) {
        remoteVideoMainView = remoteVideo
        localVideoMainView = localVideo
    }
    
    func setUp(usernName:String = "", completion: @escaping(Bool) -> Void) {
        if callClient != nil {
            // Initialize call agent
            var userCredential: CommunicationTokenCredential?
            do {
                userCredential = try CommunicationTokenCredential(token: acsAccessToken)
            } catch {
                print("ERROR: It was not possible to create user credential.")
                completion(false)
            }
            
            let callAgentOptions = CallAgentOptions()
            callAgentOptions.displayName = usernName
            
            // Creates the call agent
            self.callClient?.createCallAgent(userCredential: userCredential!, options: callAgentOptions) {
                
                [weak self]
                (agent, error) in
                
                guard let self = self else { return }
                
                if error == nil {
                    self.callAgent = agent
                    completion(true)
                } else {
                    completion(false)
                }
            }
        } else {
            completion(false)
        }
    }
    
    //Get Micro phone permissions
    func checkMicroPhonePermission(completion: @escaping(Bool) -> Void) {
        AVAudioSession.sharedInstance().requestRecordPermission ({(granted: Bool)-> Void in
            completion(granted)
        })
    }
    
    //Get Camera permissions
    func checkCameraPermissions(completion: @escaping(Bool) -> Void)  {
        AVCaptureDevice.requestAccess(for: .video) {granted in
            completion(granted)
        }
    }
    
    //Connect to teams meeting with url
    func startCall(completion: @escaping(Bool) -> Void) {
        
        DispatchQueue.main.async {
            [weak self] in
            guard let self = self else { return }
            
            let joinCallOptions = JoinCallOptions()
            let teamsMeetingLinkLocator = TeamsMeetingLinkLocator(meetingLink: self.meetingUrl!)
                        
            self.callAgent?.join(with: teamsMeetingLinkLocator, joinCallOptions: joinCallOptions) {

                [weak self]
                call, error in
                
                guard let self = self else { return }
                
                if let currentCall = call {
                    print("call id after join : \(currentCall.id )")
                    self.call = currentCall
                    self.callObserver = CallObserver(parentObj: self)
                    self.call!.delegate = self.callObserver
                                        
                    completion(true)
                } else {
                    completion(false)
                }
            }
        }
    }
    
    func startVideo(withCameraFacing:CameraFacing, completion: @escaping(Bool) -> Void) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }

            if self.localVideoStreamRenderer == nil || self.localVideoStream == nil {
                if !self.createLocalVideoRendereView(facing: withCameraFacing) {
                    completion(false)
                }
            }
            
            self.call?.startVideo(stream: self.localVideoStream!) {
                [weak self]  error in
                
                guard let _ = self else { return }
                
                DispatchQueue.main.async {
                    
                    if (error == nil) {
                        
                        print("Meeting -> Local video started successfully")
                        completion(true)
                    }
                    else {
                        print("Meeting -> Local video failed to start")
                        completion(false)
                    }
                }
            }
        }
    }
    
    func stopVideo(completion: @escaping(Bool) -> Void) {
        
        if let videoStream = localVideoStream {
            if call != nil {
                call!.stopVideo(stream: videoStream) {
                    [weak self]  error in
                    
                    guard let self = self else { return }
                    
                    if (error == nil) {
                        self.localVideoView?.removeFromSuperview()
                        
                        self.localVideoView = nil
                        self.localVideoStream = nil
                        self.localVideoStreamRenderer?.dispose()
                            
                        print("Local video stopped successfully")
                        completion(true)
                    } else {
                        print("Local video failed to stop")
                        completion(false)
                    }
                }
            } else {
                self.localVideoView?.removeFromSuperview()
                self.localVideoView?.dispose()
                
                self.localVideoView = nil
                self.localVideoStream = nil
                
                self.localVideoStreamRenderer?.dispose()
                    
                print("Local video stopped successfully")
                completion(true)
            }
        }
    }
    
    func switchLocalVideoViewToLarge(view:UIView) {
        DispatchQueue.main.async {
            if let localVideoView = self.localVideoView, var localVideoMainView = self.localVideoMainView, let remoteVideoMainView = self.remoteVideoMainView {
                
                localVideoView.removeFromSuperview()
                if remoteVideoMainView != view {
                    localVideoMainView = view
                } else {
                    localVideoMainView = remoteVideoMainView
                }
                
                localVideoView.frame = CGRect(origin: CGPoint(x: 0, y: 0), size: localVideoMainView.frame.size)
                print(localVideoView.frame)
                
                localVideoMainView.addSubview(localVideoView)
            }
        }
    }
    
    func switchLocalVideoViewToSmall(view:UIView) {
        DispatchQueue.main.async {
            if let localVideoView = self.localVideoView, var localVideoMainView = self.localVideoMainView {
                
                self.remoteVideoMainView = localVideoMainView
                
                localVideoView.removeFromSuperview()
                localVideoMainView = view
                
                localVideoView.frame = CGRect(origin: CGPoint(x: 0, y: 0), size: localVideoMainView.frame.size)
                print(localVideoView.frame)
                
                localVideoMainView.addSubview(localVideoView)
                localVideoMainView.bringSubviewToFront(localVideoView)
            }
        }
    }

    func createLocalVideoRendereView(facing:CameraFacing = .front) -> Bool {
        if let cameras = self.deviceManager?.cameras, cameras.count > 0 {
            
        var useCamera:VideoDeviceInfo?
            for camera in cameras {
                if camera.cameraFacing == facing {
                    useCamera = camera
                    break
                }
            }
        
            if useCamera != nil {
                self.localVideoStream = LocalVideoStream(camera: useCamera!)
                self.localVideoStreamRenderer = try? VideoStreamRenderer(localVideoStream: self.localVideoStream!)
                
                if let lw = try? self.localVideoStreamRenderer?.createView(withOptions: CreateViewOptions(scalingMode: .crop)) {
                    self.localVideoView = lw
                    self.localVideoView!.frame.size = self.localVideoMainView!.frame.size
                    self.localVideoView!.frame.origin = CGPoint(x: 0, y: 0)
                    self.localVideoView!.translatesAutoresizingMaskIntoConstraints = false
                    self.localVideoView!.bounds.size = self.localVideoMainView!.bounds.size
                    self.localVideoView!.center = self.localVideoMainView!.center
                    self.localVideoMainView!.addSubview(self.localVideoView!)
                    
                    return true
                } 
            }
        }
        return false
    }
    
    //end call
    func leaveMeeting(completion: @escaping(Bool) -> Void) {
         if let call = call {
            
            if call.state == .connecting ||
                call.state == .ringing ||
                call.state == .connected ||
                call.state == .localHold ||
                call.state == .inLobby ||
                call.state == .remoteHold {
                 call.hangUp(options: nil, completionHandler: { (error) in
                     if error == nil {
                        print("Leaving Teams meeting was successful")
                        completion(true)
                     } else {
                        print("Leaving Teams meeting failed")
                        completion(false)
                     }
                 })
            } else {
                completion(true)
            }
         } else {
            completion(true)
         }
     }
    
    
    func muteUnmute(mute:Bool = false, completion: @escaping(Bool) -> Void) {
        if mute {
            call!.mute {
                [weak self]  error in
                
                guard let _ = self else { return }
                
                if error == nil {
                    completion(true)
                    print("Successfully muted")
                } else {
                    print("Failed to mute")
                    completion(false)
                }
            }
        } else {
            call!.unmute {
                [weak self]  error in
                
                guard let _ = self else { return }
                
                if error == nil {
                    completion(true)
                    print("Successfully un-muted")
                } else {
                    print("Failed to unmute")
                    completion(false)
                }
            }
        }
    }
    
    func resetRemoteVideo() {
        self.remoteVideoView?.removeFromSuperview()

        self.remoteVideoView = nil
        self.remoteVideoStream = nil
    }
        
    func switchLocalCamera(completion: @escaping(Bool) -> Void) {
        if let cameras = deviceManager?.cameras, cameras.count > 1 {
            let currentCamera = localVideoStream?.source
            
            var newCamera:VideoDeviceInfo?
            if currentCamera?.cameraFacing == CameraFacing.front {
                for camera in cameras {
                    if camera.cameraFacing == CameraFacing.back {
                        newCamera = camera
                        break
                    }
                }
            } else {
                for camera in cameras {
                    if camera.cameraFacing == CameraFacing.front {
                        newCamera = camera
                        break
                    }
                }
            }
            
            if let camera = newCamera {
                localVideoStream?.switchSource(camera: camera) {
                    [weak self]  error in
                    
                    guard let _ = self else { return }
                    if error == nil {
                        completion(true)
                        print("Successfully switched camera")
                    } else {
                        print("Failed to switch")
                        completion(false)
                    }
                }
            } else {
                print("Failed to switch")
                completion(false)
            }
        } else {
            print("cannot switch")
            completion(true)
        }
    }
    
    func isVideoStreaming() {
    }
    
    func cleanUp() {
        localVideoView?.dispose()
        localVideoStreamRenderer?.dispose()
        remoteVideoView?.dispose()
        
        localVideoStream = nil
        remoteVideoStream = nil
        remoteVideoView = nil
    }
    
    func getImageForSpeaker(type: AudioOutRouteType) -> UIImage? {
        var newImage = UIImage(named: "speaker_on")

        switch type {
            case .BLUETOOTH:
                newImage = UIImage(named: "bluetooth_speaker")
            case .PHONE_MIC:
                newImage = UIImage(named: "speaker_iphone")
                break
            default:
                break
        }
        return newImage
    }
    
    func getTitleForAudioRoute(type: AudioOutRouteType) -> String {
        var newTitle = TITLE_SPEAKER

        switch type {
            case .BLUETOOTH:
                newTitle = TITLE_BT
            case .PHONE_MIC:
                if UIDevice.current.userInterfaceIdiom == .pad {
                    newTitle = TITLE_IPAD
                } else {
                    newTitle = TITLE_IPHONE
                }
                break
            default:
                break
        }
        return newTitle
    }
    
    func callResume(completion: @escaping(Bool) -> Void) {
        if let call = self.call, call.state == .localHold {
            call.resume() {
                [weak self]  error in
                
                guard let _ = self else { return }
                if error == nil {
                    completion(true)
                    print("Successfully callUnHold")
                } else {
                    print("Failed callUnHold")
                    completion(false)
                }
            }
        } else {
            print("call not onHold")
            completion(true)
        }
    }
    
    func callOnHold(completion: @escaping(Bool) -> Void) {
        if let call = self.call, (call.state == .connected || call.state == .inLobby) {
            call.hold() {
                [weak self]  error in
                
                guard let _ = self else { return }
                if error == nil {
                    completion(true)
                    print("Successfully callOnHold - \(call.state)")
                } else {
                    print("Failed callOnHold")
                    completion(false)
                }
            }
        } else {
            //never
            completion(true)
        }
    }
    
    func canMeetingStart(appt:Appointment) -> Bool {
        
        guard let scheduledDateTime = appt.startDateInLocalTZ else {
            return false
        }
        
        let currentTime = Date()
        let diffComponents = Calendar.current.dateComponents([.hour, .minute, .second], from: currentTime, to: scheduledDateTime)
        
        if let sec = diffComponents.second, let minute = diffComponents.minute,
           let hr = diffComponents.hour {
            let timeLeftInSec = (hr * 3600) + (minute * 60) + sec
            
            // <5 min
            if timeLeftInSec <= 300 {
                return true
            }
        }
        
        let scheduledTime = DateConvertor.convertToStringFromDate(date: appt.startDate, outputFormat: .justTimeForm)
        var alertInfo = myCTCAAlert()

        alertInfo.attributeMessage = NSMutableAttributedString()
            .normal(TelehealthMsgConstants.cantJoinMeetingMessage1 + "\n\n", fontSize: 14.0)
            .bold(scheduledTime + " " + appt.facilityTimeZone + "\n\n", fontSize: 18.0)
            .normal(TelehealthMsgConstants.cantJoinMeetingMessage2, fontSize: 14.0)
        
        GenericHelper.shared.showAlertLarge(info: alertInfo)
        
        return false
    }
}


class CallObserver : NSObject, CallDelegate {
    
    weak var delegate: TelehealthServiceProtocol?
    var remoteVideoView:UIView?
    weak var parent:TelehealthServiceManager?
    
    init(parentObj:TelehealthServiceManager) {
        parent = parentObj
        delegate = parentObj.delegate
        remoteVideoView = parentObj.remoteVideoMainView
    }
        
    func call(_ call: Call, didChangeState args: PropertyChangedEventArgs) {
        print("onStateChanged : \(call.state.rawValue)")

        if let telehealthDelegate = delegate {
            telehealthDelegate.didCallStateChanged(state: call.state)
        }
    }
    
    func call(_ call: Call, didUpdateRemoteParticipant args: ParticipantsUpdatedEventArgs) {
        print("didUpdateRemoteParticipant")

        if  call.remoteParticipants.first != nil {

            parent?.physician = call.remoteParticipants.first
            parent?.physician?.delegate = delegate as? RemoteParticipantDelegate
            
            guard let telehealthDelegate = delegate else {return}
            telehealthDelegate.didRemoteParticipantJoined()
            
            if let videoStream = parent?.physician?.videoStreams.first, let remoteRenderer = try? VideoStreamRenderer(remoteVideoStream: videoStream), let videoView = remoteVideoView {
                if let targetRemoteParticipantView: RendererView = try? remoteRenderer.createView(withOptions: CreateViewOptions(scalingMode: ScalingMode.crop))
                {
                    parent?.remoteVideoStream = videoStream
                    targetRemoteParticipantView.frame = videoView.frame
                    targetRemoteParticipantView.frame.origin = CGPoint(x: 0, y: 0)

                    videoView.addSubview(targetRemoteParticipantView)
                    
                    telehealthDelegate.didRemoteVideoRendered()
                }
            }
            
            telehealthDelegate.didRemoteUserNameAvailable(name: (parent?.physician?.displayName) ?? "")
            
            if ((parent?.physician?.isMuted) != nil) {
                telehealthDelegate.didRemoteAudioStateChanged(state:false)
            }
        } else {
            //remote user left call
            print("didUpdateRemoteParticipant: remotreuser.count == 0")
        }
    }
    
    func call(_ call: Call, didChangeMuteState args: PropertyChangedEventArgs) {
        if let telehealthDelegate = delegate {
            telehealthDelegate.didHostMuted(state: call.isMuted)
        }
    }
}

