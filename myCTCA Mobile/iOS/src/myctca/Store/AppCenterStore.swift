//
//  AppCenterStore.swift
//  myctca
//
//  Created by Manjunath K on 9/30/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import AppCenter
import AppCenterAnalytics
import AppCenterCrashes


final class AppCenterStore {
    //singleton
    static let shared = AppCenterStore()
    private init() { }
    
    func startAppCenterReporting() {
        let appSecret = (Environment().configuration(PlistKey.AppSecret))
        
        #if DEBUG
        AppCenter.logLevel = .verbose
        #endif

        AppCenter.start(withAppSecret: appSecret, services:[
          Analytics.self,
          Crashes.self
        ])
    }
    
    func stopAppCenterReporting() {
        Analytics.enabled = false
    }
    
    func trackEvent(_ name:String) {
        if CTCAAnalyticsConstants.filteredEventList.contains(name) {
            Analytics.trackEvent(name)
        }
    }
    
    func trackPageView(_ pageName:String) {
       //Analytics.trackEvent(pageName)
    }
    
    func trackEvent(_ name:String, _ infoDict:[String:String] = [String:String]()) {
        if CTCAAnalyticsConstants.filteredEventList.contains(name) {
            if infoDict.count > 0 {
                var newInfoDict = infoDict
                let userID = String(AppSessionManager.shared.currentUser.iUser?.userId ?? 0)
                newInfoDict["identityUserID"] = userID
                Analytics.trackEvent(name,withProperties: newInfoDict)
            } else {
                Analytics.trackEvent(name)
            }
        }
    }
    
    func logException(_ name:String, _ apiName:String, _ errorCode:String) {
        let userID = String(AppSessionManager.shared.currentUser.iUser?.userId ?? 0)
        let infoDict = ["APIName":apiName, "identityUserID":userID, "errorCode":errorCode]
        Analytics.trackEvent(name,withProperties: infoDict)
    }
}
