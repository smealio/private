//
//  MyCTCAApp.swift
//  myctca
//
//  Created by Tomack, Barry on 11/27/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation
import UIKit

/**
 UIApplication is extended to capture all Touches as a way of determining if there is activity.
 */

@objc(MyCTCAApp) class MyCTCAApp: UIApplication {
    
    
    // Any touch event resets the Idle Timer in the AppSessionManager
    override func sendEvent(_ event: UIEvent) {
        super.sendEvent(event)
        if let allTouches: Set<UITouch> = event.allTouches {
            if (allTouches.count > 0) {
                let phase: UITouch.Phase = (allTouches.first?.phase)!
                if (phase == .began || phase == .ended) {
                    AppSessionManager.shared.resetIdleTimer()
                }
            }
        }
    }
}
