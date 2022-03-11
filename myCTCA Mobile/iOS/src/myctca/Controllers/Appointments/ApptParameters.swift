//
//  ApptParameters.swift
//  myctca
//
//  Created by Tomack, Barry on 12/7/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

struct ApptParameters {

    let sessionParams = AppSessionParameters()
    
    let contentType: String = "application/x-www-form-urlencoded"
    let accept: String = "application/json"
    
    var getApptsURL: String {
        get {
            // https://v3devservice.myctca.com/api/v1/appointments/getappointments
            return "\(sessionParams.portalDataServer)/appointments/getappointments"
        }
    }
    
    var newApptRequestURL: String {
        get {
           return "\(sessionParams.portalDataServer)/appointments/requestnewappointment"
        }
    }
        
    var cancelApptRequestURL: String {
        get {
            return "\(sessionParams.portalDataServer)/appointments/cancelappointment"
        }
    }
    
    var rescheduleApptRequestURL: String {
        get {
            return "\(sessionParams.portalDataServer)/appointments/rescheduleappointment"
        }
    }
    
    var apptReportsURL: String {
        get {
            return "\(sessionParams.portalDataServer)/appointments/getappointmentsreport"
        }
    }
}
