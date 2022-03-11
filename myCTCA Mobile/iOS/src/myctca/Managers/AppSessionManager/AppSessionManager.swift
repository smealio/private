//
//  AppSessionManager.swift
//  myctca
//
//  Created by Tomack, Barry on 11/2/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

/**
 AppSessionManager manages most aspects of the application session.
 It is a singleton and can be accessed from anywhere within the app.
 */

import Foundation
import UserNotifications
import UIKit

class AppSessionManager {
    
    // Singleton Parameter. This is the easiest way to iplement a Singleton in Swift
    static let shared = AppSessionManager()
    
    var currentUser = MyCTCAUser()
    var originalUser = MyCTCAUser()
    
    let authenticationManager = AuthenticationManager()
    
    private var inProxyMode = false
    private var didUserSwitch = false
    
    var proxyPatientId: String = ""
        
    //This prevents others from using the default '()' initializer for this class.
    private init() { }
    
    func getSessionState() -> MyCTCASessionState {
        return currentUser.sessionState
    }
    
    func beginCurrentSession() {
        // Idle time currently is set to 5 minutes (60 seconds/minute x 5 minutes)
        let idleTime: Double = UserValues.sessionTimeOutPeriod
        self.currentUser.sessionIdleTimer = AppSessionIdleTimer(idleTime: idleTime)
        
        self.currentUser.createdAt = Date()
        currentUser.sessionState = .ACTIVE
    }
    
    func endCurrentSession() {
        clearAllSessionParameters()
        print("AppSessionManager endCurrentSesson")
        
        if (self.currentUser.sessionIdleTimer != nil) {
            self.currentUser.sessionIdleTimer!.killTimer()
        }
        
        popToRoot()
        currentUser.sessionState = .INACTIVE
        changeProxyMode(set: false)
    }
    
    func toLoginScreen() {
        DispatchQueue.main.async(execute: {
            let appDelegate = UIApplication.shared.delegate as! AppDelegate

            if let rootNavCtrl = appDelegate.window?.rootViewController as? UINavigationController {
                if rootNavCtrl.presentingViewController != nil {
                    rootNavCtrl.dismiss(animated: false, completion: {
                        rootNavCtrl.navigationController!.popToRootViewController(animated: true)
                    })
                } else {
                    rootNavCtrl.tabBarController?.navigationController!.popToRootViewController(animated: true)
                }
            }
        })
    }
    
    func clearAllSessionParameters() {
        currentUser.cleanup()
        clearPatientSpecificData() 
    }
    
    func popToRoot() {
        let appDelegate:AppDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.popToRoot()
    }
    
    /**
     Called from class MyCTCAApp (extension of UIApplication) which overrides
     the sendEvent method to capture all touches. A touch constitiutes activity
     therefore resets the idle timer.
     */
    func resetIdleTimer() {
        if (self.getSessionState() == .ACTIVE) {
            if (self.currentUser.sessionIdleTimer != nil) {
                self.currentUser.sessionIdleTimer!.resetIdleTimer()
            }
        }
    }
    
    /**
     Called to refresh an existing token
     */
    func refreshToken() {
        print("AppSessionManager refreshToken")
        
        GenericHelper().showActivityIndicator(message: "Renewing session...")
        
        if getUserType() == .PROXY {
            let ctcaId = proxyPatientId
            authenticationManager.refreshingToken = true
            authenticationManager.switchToProxyUser(proxyUserId: ctcaId, toPatient: true) {
                error, status in
                
                GenericHelper().dismissActivityIndicator()
                
                if status == .SUCCESS {
                    self.beginCurrentSession()
                    _ = AppSessionManager.shared.currentUser.accessToken!.isExpired()
                } else if let sError = error {
                    self.displayMessage(title: "Error", message: sError.errorMessage)
                } else {
                    self.displayMessage(title: "Error", message: ErrorManager.shared.getDefaultServerError().errorMessage)
                }
            }
        } else {
            authenticationManager.authenticateUser(username: BiometricManager.shared.loginParameters.username, password: BiometricManager.shared.loginParameters.password) {
                
                (error: ServerError?, status: RESTResponse) in
                
                GenericHelper().dismissActivityIndicator()
                
                if status == .SUCCESS {
                    self.beginCurrentSession()
                    _ = AppSessionManager.shared.currentUser.accessToken!.isExpired()
                } else {
                    print("Error refrshing token in: \(String(describing: self.authenticationManager.getLastServerError())) : \(String(describing: self.authenticationManager.getLastServerError().errorMessage))")
                    self.displayMessage(title: "Error", message: self.authenticationManager.getLastServerError().errorMessage)
                }
            }
        }
    }
    
    /**
     Never implemented. Called from AppSessionReconnectionTimer
    */
    func reconnect() {
    }
    
    func setAllFacilityInfo(_ facilityList:[Facility]?) {
        if let list = facilityList {
            currentUser.allFacilitesList = list
            for item in list {
                currentUser.allFacilitesNamesList[item.facilityCode] = item.displayName
            }
        }
    }
    
    func setPrimaryFacility(facilityCode: String) {
        for item in currentUser.allFacilitesList {
            if item.facilityCode == facilityCode {
                currentUser.primaryFacility = item
                break
            }
        }
    }
    
    func getBearerAccessToken() -> String {
        return "\(AppSessionManager.shared.currentUser.accessToken!.tokenType) \(AppSessionManager.shared.currentUser.accessToken!.accessToken)"
    }
    
    func getBearerToken() -> String? {
        if let accessToken = AppSessionManager.shared.currentUser.accessToken {
            return "\(accessToken.tokenType) \(accessToken.accessToken)"
        }
        return nil
    }
    
    func getBearerTokenForOriginalUser() -> String? {
        if let accessToken = AppSessionManager.shared.originalUser.accessToken {
            return "\(accessToken.tokenType) \(accessToken.accessToken)"
        }
        return nil
    }
    
    func didSessionExpired() {
        if currentUser.sessionState == .ACTIVE {
            currentUser.sessionState = .EXPIRED
            
            var alertInfo = myCTCAAlert()
             
             alertInfo.title = CommonMsgConstants.sessionExpiryTitle
             alertInfo.message = CommonMsgConstants.sessionExpiryMessage
             
             alertInfo.rightBtnTitle = "Yes"
             alertInfo.rightBtnAction = {
                self.getRefreshToken()
             }
             
             alertInfo.leftBtnTitle = "No"
             alertInfo.leftBtnAction = {
                self.endSession()
             }
             
             GenericHelper.shared.showAlert(info: alertInfo)
        } else if let timer = self.currentUser.sessionIdleTimer {
            timer.killTimer()
        }
    }
    
    func displayMessage(title: String, message:String) {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        if let rootNavCtrl = appDelegate.window?.rootViewController as? UINavigationController {
            GenericHelper.shared.showAlert(withtitle: title, andMessage: message, onView: rootNavCtrl)
        } else {
            print("Cannot display error")
        }
    }
    
    func getRefreshToken() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SESSION_RENEWAL_YES)
        refreshToken()
    }
    
    func endSession() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SESSION_RENEWAL_NO)
        endCurrentSession()
    }
    
    func switchToImpersonation() {
        originalUser.accessToken = currentUser.accessToken
        originalUser.careTeams = currentUser.careTeams
        originalUser.createdAt = currentUser.createdAt
        originalUser.iUser = currentUser.iUser
        originalUser.primaryFacility = currentUser.primaryFacility
        originalUser.sessionIdleTimer = currentUser.sessionIdleTimer
        originalUser.sessionState = currentUser.sessionState
        originalUser.userProfile = currentUser.userProfile
        originalUser.currentUserId = currentUser.currentUserId
        
        currentUser.cleanup()
        clearPatientSpecificData()
    }

    func switchBackToCaregiverUser() {
        currentUser.accessToken = originalUser.accessToken
        currentUser.careTeams = originalUser.careTeams
        currentUser.createdAt = originalUser.createdAt
        currentUser.iUser = originalUser.iUser
        currentUser.primaryFacility = originalUser.primaryFacility
        currentUser.sessionIdleTimer = originalUser.sessionIdleTimer
        currentUser.sessionState = originalUser.sessionState
        currentUser.userProfile = nil
        currentUser.userProfile = originalUser.userProfile
        currentUser.currentUserId = originalUser.currentUserId
        clearPatientSpecificData()
        
        proxyPatientId = ""
    }
    
    func changeProxyMode(set:Bool) {
        if set {
            inProxyMode = true
        } else {
            inProxyMode = false
        }
        
        didUserSwitch = true
    }
    
    func getUserType() -> UserType {
        if inProxyMode {
            return .PROXY
        } else {
            if let type = currentUser.userProfile?.userType {
                return type
            }
            return .NONE
        }
    }
    
    func setAccessToken(token:AccessToken?) {
        if let accessToken = token {
            self.currentUser.accessToken = accessToken
        }
    }
    
    func clearPatientSpecificData() {
        let lock = NSLock()
        lock.lock()
        
        AppointmentsManager.shared.appointments.removeAll()
        
        MailManager.shared.newMails.removeAll()
        MailManager.shared.sentMails.removeAll()
        MailManager.shared.archivedMails.removeAll()
        MailManager.shared.careTeams.removeAll()
        
        LabsManager.shared.labResultsOriginal?.removeAll()
        LabsManager.shared.labResultsOriginal = nil
        LabsManager.shared.labResults?.removeAll()
        LabsManager.shared.labResults = nil
        
        lock.unlock()
    }
    
    func changeTimeoutPeriodForTelehealth() {
        let timeOutPeriod = 3600.0 //sec, i.e 5 mins
        UserValues.sessionTimeOutPeriod = timeOutPeriod
        resetIdleTimer()
    }
    
    func changeTimeoutPeriodToDefault() {
        UserValues.sessionTimeOutPeriod = UserValues.defaultSessionTimeOutPeriod
        resetIdleTimer()
    }
}
