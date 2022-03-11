//
//  AudioSupportViewController.swift
//  myctca
//
//  Created by Manjunath K on 6/22/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class AudioSupportViewController: UIViewController {
    
    var audioOutRoutetype: AudioOutRouteType = .NONE

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setSpeakerButton() {
        
    }

}

extension AudioSupportViewController : AudioOutRouteChangeDelegate {
    func didChangeAudioRoute(to: AudioOutRouteType) {
        audioOutRoutetype = to
        setSpeakerButton()
    }
    
}
