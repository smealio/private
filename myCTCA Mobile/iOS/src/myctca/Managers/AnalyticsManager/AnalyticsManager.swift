//
//  AnalyticsManager.swift
//  myctca
//
//  Created by Manjunath K on 10/1/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Firebase

final class AnalyticsManager {
    
    //singleton
    static let shared = AnalyticsManager()
    
    private init() { }
    
    func startAnalyticsReporting() {
        FirebaseAnalyticsStore.shared.startAppCenterReporting()
    }
    
    func stopAnalyticsReporting() {
        FirebaseAnalyticsStore.shared.stopAppCenterReporting()
    }
    
    func trackEvent(_ name:String,_ info:[String:String] = [String:String]()) {
        FirebaseAnalyticsStore.shared.trackEvent(name, info)
    }
    
    func trackPageView(_ pageName:String) {
        FirebaseAnalyticsStore.shared.trackPageView(pageName)
    }
    
    func logException(_ name:String, _ apiName:String, _ errorCode:String) {
        FirebaseAnalyticsStore.shared.logException(name, apiName, errorCode)
    }
    
    func trackTelehealthEvent(_ name:String, customInfo:[String:String]?, appointment:Appointment?) {
        if let appointment = appointment {
            var customDataDict = [String:String]()
            customDataDict["MeetingID"] = appointment.meetingId
            let userID = String(AppSessionManager.shared.currentUser.iUser?.userId ?? 0)
            customDataDict["identityUserID"] = userID

            if let customInfo = customInfo {
                customDataDict.merge(customInfo, uniquingKeysWith: +)
            }
            
            trackEvent(name, customDataDict)
        } else {
            trackEvent(name)
        }
    }
}
