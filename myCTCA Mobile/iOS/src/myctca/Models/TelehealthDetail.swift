//
//  TelehealthDetail.swift
//  myctca
//
//  Created by Manjunath K on 5/7/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import AzureCommunicationCalling

struct TelehealthDetail {
    var accessToken = ""
    var duration = 0
    weak var appointment:Appointment?
    
    var cameraOn = false
    var micOn = false
    var cameraType:CameraFacing = .front
    var audioRoute:AudioOutRouteType = .PHONE_MIC
    
    init(withValue: Appointment) {
        appointment = withValue
    }
}
