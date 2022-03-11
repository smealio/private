//
//  StopWatchTimer.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 21/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import CoreFoundation

class StopWatchTimer {

    var startTime:CFAbsoluteTime?
    var endTime:CFAbsoluteTime?

    init() {
        startTime = CFAbsoluteTimeGetCurrent()
    }

    func stop() -> CFAbsoluteTime {
        endTime = CFAbsoluteTimeGetCurrent()

        return duration!
    }
    
    func reset() {
        startTime = nil
        startTime = CFAbsoluteTimeGetCurrent()
    }

    var duration:CFAbsoluteTime? {
        if let endTime = endTime, let startTime = startTime {
            return endTime - startTime
        } else {
            return nil
        }
    }
}
