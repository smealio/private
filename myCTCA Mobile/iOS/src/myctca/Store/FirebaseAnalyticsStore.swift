//
//  FirebaseAnalyticsStore.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 01/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Firebase

final class FirebaseAnalyticsStore {
    //singleton
    static let shared = FirebaseAnalyticsStore()
    private init() { }
    
    func startAppCenterReporting() {
        FirebaseApp.configure()
        Analytics.setAnalyticsCollectionEnabled(true)
    }
    
    func stopAppCenterReporting() {
        Analytics.setAnalyticsCollectionEnabled(false)
    }
    
    func trackEvent(_ name:String) {
        Analytics.logEvent(name, parameters: nil)
    }
    
    func trackPageView(_ pageName:String) {
        Analytics.logEvent(AnalyticsEventScreenView,
                           parameters: [AnalyticsParameterScreenName: pageName])
    }
    
    func trackEvent(_ name:String, _ infoDict:[String:String] = [String:String]()) {
        //if CTCAAnalyticsConstants.filteredEventList.contains(name) {
            if infoDict.count > 0 {
                var newInfoDict = infoDict
                let userID = String(AppSessionManager.shared.currentUser.iUser?.userId ?? 0)
                newInfoDict["identityUserID"] = userID
                Analytics.logEvent(name, parameters: newInfoDict)
            } else {
                Analytics.logEvent(name, parameters:nil)
            }
        //}
    }
    
    func logException(_ name:String, _ apiName:String, _ errorCode:String) {
        let userID = String(AppSessionManager.shared.currentUser.iUser?.userId ?? 0)
        let infoDict = ["APIName":apiName, "identityUserID":userID, "errorCode":errorCode]
        Analytics.logEvent(name,parameters: infoDict)
    }
}
