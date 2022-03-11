//
//  PreCallViewController.swift
//  myctca
//
//  Created by Manjunath K on 5/7/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import AzureCommunicationCalling
import AVFoundation
import AVKit

class PreCallViewController: AudioSupportViewController {

    @IBOutlet weak var meetingNameLabel: UILabel!
    @IBOutlet weak var camerPreview: UIView!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var continueButton: UIButton!
    
    @IBOutlet weak var speakerButton: UIButton!
    @IBOutlet weak var micButton: SwitchButton!
    @IBOutlet weak var cameraButton: SwitchButton!
    
    var telehealthDetail:TelehealthDetail?
    
    private var videoRendererView:RendererView?
    private var localVideoStreamRenderer: VideoStreamRenderer?
    private var switchCameraButton: UIButton?
    private var renderContentView = UIView()
    private var TELEHEALTH_SEGUE = "telehealthContinueSegue"
    
    private var audioOutManager:AudioOutManager?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        audioOutManager = AudioOutManager(parent: self.view)
        audioOutManager?.delegate = self
        audioOutManager?.setSessionPlayerOn()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        setupACS()
        setupUI()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        AppDelegate.AppUtility.lockOrientation(.portrait)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        AppDelegate.AppUtility.lockOrientation(.all)
        TelehealthServiceManager.shared.cleanUp()
    }
    
    func setupUI() {
        micButton.onImage = UIImage(named: "mic_on")
        micButton.offImage = UIImage(named: "mic_off")
        micButton.onTitle = "Mic is on"
        micButton.offTitle = "Mic is off"
        micButton.toggle()
        micButton.alignTextBelow()
        
        cameraButton.onImage = UIImage(named: "camera_on")
        cameraButton.offImage = UIImage(named: "camera_off")
        cameraButton.onTitle = "Video is on"
        cameraButton.offTitle = "Video is off"
        cameraButton.toggle()
        cameraButton.alignTextBelow()
        
        cancelButton.layer.cornerRadius = 5
        cancelButton.layer.masksToBounds = true
        cancelButton.layer.borderWidth = 0.5
        cancelButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
        
        continueButton.layer.cornerRadius = 5
        continueButton.layer.masksToBounds = true
        
        if let name = telehealthDetail?.appointment?.description {
            meetingNameLabel.text = name
        }
        
        //speaker setup
        audioOutRoutetype = (audioOutManager?.getCurrentAudioOutRoute())!
        setSpeakerButton()
        speakerButton.alignTextBelow()
    }
    
    override func setSpeakerButton() {
        speakerButton.setImage(TelehealthServiceManager.shared.getImageForSpeaker(type: audioOutRoutetype), for: .normal)
        speakerButton.setTitle(TelehealthServiceManager.shared.getTitleForAudioRoute(type: audioOutRoutetype), for: .normal)
    }
    
    func setupACS() {
        TelehealthServiceManager.shared.checkCameraPermissions() { [self]
            permitted in
            
            if permitted {
                
                TelehealthServiceManager.shared.checkMicroPhonePermission() {
                    permitted in
                    
                    if permitted {
                        TelehealthServiceManager.shared.setDetails(token: telehealthDetail!.accessToken, url: (telehealthDetail?.appointment!.telehealthMeetingJoinUrl)!)
                        {
                            status in
                            
                            if status {
                                //peview setup
                                TelehealthServiceManager.shared.setViews(remoteVideo: nil, localVideo: camerPreview)
                                                                
                                //To turnoff video on start
                                
                                self.cameraButton.toggle()
                                
//                                if TelehealthServiceManager.shared.createLocalVideoRendereView(facing: .front) {
//                                    resetRenderView()
//                                    addCameraSwitchButton()
//                                } else {
//                                    print("Failed to render, triying again")
//                                    if TelehealthServiceManager.shared.createLocalVideoRendereView(facing: .front) {
//                                        resetRenderView()
//                                        addCameraSwitchButton()
//                                    } else {
//                                        print("Failed to render, tried twice")
//                                        self.cameraButton.toggle()
//                                    }
//                                }
                                
                            } else {
                                AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.EXCEPTION_TELEHEALTH_MEETING_INTRUPTED, customInfo:  ["methodName": "callClient!.getDeviceManager"], appointment: self.telehealthDetail?.appointment)
                                ErrorManager.shared.showDefaultTelehealthError(onView: self, appointment: self.telehealthDetail?.appointment)
                            }
                        }
                    }  else {
                           //permission denied
                           cancelOperation()
                    }
                }
            } else {
                //permission denied
                cancelOperation()
            }
        }
    }
    
    @IBAction func micTapped(_ sender: Any) {
        
    }
    
    @IBAction func cameraTapped(_ sender: Any) {
        if !cameraButton.status {
            //showCameraPreview()
            if TelehealthServiceManager.shared.createLocalVideoRendereView() {
                resetRenderView()
                addCameraSwitchButton()
            }
        } else {
            self.stopVideoRendering()
        }
    }
    
    func stopVideoRendering() {
        TelehealthServiceManager.shared.stopVideo() {
            status in
            
            if status {
                self.removeCameraSwitchButton()
                
                for view in self.renderContentView.subviews {
                    if view.isKind(of: RendererView.self) {
                        view.removeFromSuperview()
                        
                        if let rView = view as? RendererView {
                            rView.dispose()
                        }
                        
                        break
                    }
                }
                
            } else {
                print("Failed to  stop camera")
            }
        }
    }
    
    @IBAction func speakerTapped(_ sender: Any) {
       //AudioOutputDeviceHandler.sharedInstance.listOfAvailableDevices(controller: self, speakerButton: speakerButton)
       //audioOutManager?.showAudioRouteOptionSheet(onView: self, forButton: speakerButton)
        audioOutManager?.showAudioRoutePicker()
    }
    
    @IBAction func cancelTapped(_ sender: Any) {
        cancelOperation()
        self.stopVideoRendering()
    }
    
    @IBAction func continueTapped(_ sender: Any) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        telehealthDetail?.cameraOn = cameraButton.status
        telehealthDetail?.micOn = micButton.status
        telehealthDetail?.audioRoute = audioOutRoutetype
        
        self.stopVideoRendering()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == TELEHEALTH_SEGUE {
            if let destinationController = segue.destination as? TelehealthViewController {
                UIApplication.shared.isIdleTimerDisabled = true
                
                destinationController.telehealthDetail = self.telehealthDetail
                destinationController.modalPresentationStyle = .fullScreen
                destinationController.delegate = self
            }
        }
    }
    
    func stopCameraPreview() {
        localVideoStreamRenderer?.dispose()
        localVideoStreamRenderer = nil
        
        renderContentView.removeFromSuperview()
        removeCameraSwitchButton()
    }
    
    func cancelOperation() {
        if Thread.isMainThread {
            self.navigationController?.popViewController(animated:true)
        } else {
          _ = DispatchQueue.main.sync {
            self.navigationController?.popViewController(animated:true)
          }
        }
    }
    
    func addCameraSwitchButton() {
        if switchCameraButton == nil {
            switchCameraButton = UIButton(frame: CGRect(x: camerPreview.frame.width - 30, y: 0, width: 30, height: 30))
            switchCameraButton?.setImage(UIImage(named: "flip_camera"), for: .normal)
            switchCameraButton?.addTarget(self, action: #selector(self.flipCameraTapped), for: .touchUpInside)
        }
        
        camerPreview.addSubview(switchCameraButton!)
    }
    
    func removeCameraSwitchButton() {
        switchCameraButton?.removeFromSuperview()
    }
    
    @objc func flipCameraTapped(_ sender: Any) {
        TelehealthServiceManager.shared.switchLocalCamera() {
            status in
            
            if status {
                if let type = self.telehealthDetail?.cameraType {
                    if  (type == .front) {
                        self.telehealthDetail?.cameraType = CameraFacing.back
                    } else {
                        self.telehealthDetail?.cameraType = CameraFacing.front
                    }
                }
            } else {
                print("Failed to  flipCamera")
            }
        }
    }
    
    func resetRenderView() {
        for view in self.camerPreview.subviews {
            if view.isKind(of: RendererView.self) {
                view.removeFromSuperview()
                
                renderContentView = UIView(frame: CGRect(x: 0, y: 0, width: camerPreview.frame.width, height: camerPreview.frame.height - cameraButton.frame.height))
                renderContentView.backgroundColor = .clear
                camerPreview.addSubview(renderContentView)
                
                print(view.frame)
                view.frame = renderContentView.frame
                print(view.frame)
                renderContentView.addSubview(view)
                print(view.frame)

                break
            }
        }
    }
}

extension PreCallViewController : TelehealthProtocol {
    func didMeetingEnd() {
        UIApplication.shared.isIdleTimerDisabled = false
        
        continueButton.isEnabled = false
        cancelButton.isEnabled = false

        audioOutManager?.setSessionPlayerOff()
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.00001) {
            self.navigationController?.popViewController(animated: true)
        }
    }
}
