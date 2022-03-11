//
//  AppInfoManager.swift
//  myctca
//
//  Created by Manjunath K on 2/3/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class AppInfoManager: BaseManager {
    
    let UPDATE_MESSAGE_DISPLAY_DATE_KEY = "UPDATE_MESSAGE_DISPLAY_DATE"
    let SIT_MESSAGE_DISPLAY_DATE_KEY = "SIT_MESSAGE_DISPLAY_DATE"
    
    var appInfo:AppInfo?
    
    static let shared = AppInfoManager()
    
    func shouldShowMessage(_ message:String) -> Bool {
        if let savedDate = GenericHelper.shared.getFromUserDefaults(forKey: message) as? String {
            if let date = DateConvertor.convertToDateFromString(dateString: savedDate, inputFormat: .usStandardForm1) {
                if Calendar.current.isDateInToday(date) {
                    return false
                } else {
                    saveTodaysDate(forMessage: message)
                    return true
                }
            } else {
                saveTodaysDate(forMessage: message)
                return true
            }
        }
        
        saveTodaysDate(forMessage: message)
        return true
    }

    func shouldCheckForAppUpdate() -> Bool {
        if let savedDate = GenericHelper.shared.getFromUserDefaults(forKey: UPDATE_MESSAGE_DISPLAY_DATE_KEY) as? String {
            if let date = DateConvertor.convertToDateFromString(dateString: savedDate, inputFormat: .usStandardForm1) {
                if Calendar.current.isDateInToday(date) {
                    return false
                } else {
                    return true
                }
            } else {
                return true
            }
        }
        
        return true
    }
    
    func saveTodaysDate(forMessage:String) {
        let date = DateConvertor.convertToStringFromDate(date: Date(), outputFormat: .usStandardForm1)
        GenericHelper.shared.saveInUserDefaults(object: date, key: forMessage)
    }
    
    func checkForAppUpdate() -> AppUpdateMessageType {
        if let appsInfo = self.appInfo {
            var iOSVersionsList = [AppVersions]()

            for item in appsInfo.applicationVersions {
                if item.platform == "iOS" {
                    iOSVersionsList.append(item)
                }
            }
            
            if iOSVersionsList.count > 2 || iOSVersionsList.count < 1 {
                return .Failed
            }
            
            var mandatoryVersion = ""
            var optionalVersion = ""
            
            for item in iOSVersionsList {
                if item.mandatory {
                    mandatoryVersion = item.versionNumber
                } else {
                    optionalVersion = item.versionNumber
                }
            }
                    
            if mandatoryVersion.isEmpty && optionalVersion.isEmpty {
                return .None
            } else {
                let currentAppVersion = Bundle.main.infoDictionary!["CFBundleShortVersionString"] as! String

                if !mandatoryVersion.isEmpty && currentAppVersion.compare(mandatoryVersion, options: .numeric) == .orderedAscending {
                        //currentAppVersion < mandatoryVersion
                        return .manadatory
                } else if currentAppVersion.compare(optionalVersion, options: .numeric) == .orderedAscending {
                    //currentAppVersion < optionalVersion
                    return .Optional
                }
                return .None
            }
        }
        return .Failed
    }
    
    func isUpdateAvailable() -> AppUpdateMessageType {
        let updateStatus = checkForAppUpdate()
        
        if updateStatus == .Optional {
            if shouldShowMessage(UPDATE_MESSAGE_DISPLAY_DATE_KEY) {
                return updateStatus
            }
            return .None
        }
        
        return updateStatus
    }
    
    func fetchAppInfo(completion: @escaping(RESTResponse) -> Void) {
        if appInfo != nil {
            return completion(.SUCCESS)
        }
        
        let appInfoStore = AppInfoStore()
        appInfoStore.fetchAppInfo(route: AppInfoAPIRouter.getApplicationInfo) { [weak self]
             response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let info = response {
                    self.appInfo = info
                    completion(.SUCCESS)
                } else {
                    completion(.FAILED)
                }
            }
        }
    }
    
    func getTechSupportPhoneNumber(completion: @escaping(String?, RESTResponse) -> Void) {
        fetchAppInfo() {
            status in
            
            if status == .FAILED {
                completion(nil, .FAILED)
            } else {
                if let appsInfo = self.appInfo {
                    completion(appsInfo.techSupportNumber, .SUCCESS)
                }
            }
        }
    }
}
