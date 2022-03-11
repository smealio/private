//
//  AppSessionTokenTimer.swift
//  myctca
//
//  Created by Tomack, Barry on 4/23/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

/**
 Timer to monitor token during a App Session
 
 When the token expiration is within the set value of the expirationTimeInterval,
 the token will automatically refresh.
 
 The timer is set when the AccessToken is obtained. The benefit of using a timer
 is that it runs independently from the device's calendar which can be changed by the user.
 This is a more secure method in that the calendar date and time can be changed.
 */

class AppSessionRefreshTokenTimer: NSObject {
    
    private var refreshTokenTimer: Timer?
    
    static let defaultRefreshesInTimeInterval: TimeInterval = 120.0
    var refreshesInTimeInterval: TimeInterval

    // Calculated based on the expiresIn value of the AccessToken minus
    // the value of the refreshesInTimeInterval
    var refreshTime: TimeInterval?
    
    init(refreshesBeforeExpired: TimeInterval?) {
        
        self.refreshesInTimeInterval = AppSessionRefreshTokenTimer.defaultRefreshesInTimeInterval
        if (refreshesBeforeExpired != nil) {
            // Make sure it is less than the token expiresIn
            if let accessToken = AppSessionManager.shared.currentUser.accessToken {
                if (Int(refreshesBeforeExpired!) < accessToken.expiresIn) {
                    self.refreshesInTimeInterval = refreshesBeforeExpired!
                }
            }
        }
        
        // Get the expiresInValue from the AccessToken
        // The call to initialize the AppSessionRefreshTokenTimer would not have occured if the access token wasn't aquired
        if let accessToken = AppSessionManager.shared.currentUser.accessToken {
            
            let expiresIn:TimeInterval = TimeInterval(Double(accessToken.expiresIn))
            
            self.refreshTime = expiresIn - self.refreshesInTimeInterval
        }
        super.init()
        self.resetRefreshTokenTimer()
    }
    
    func resetRefreshTokenTimer() {
        print("RESET REFRESH TOKEN TIMER refreshTime: \(String(describing: self.refreshTime))")
        if(self.refreshTokenTimer != nil) {
            self.killTimer()
        }
        
        if self.refreshTime != nil {
            self.refreshTokenTimer = Timer.scheduledTimer(timeInterval: self.refreshTime!,
                                                          target: self,
                                                          selector: #selector(self.refreshAccessToken),
                                                          userInfo: nil,
                                                          repeats: true)
        }
    }
    
    
    
    func killTimer() {
        if (self.refreshTokenTimer != nil) {
            self.refreshTokenTimer?.invalidate()
            self.refreshTokenTimer = nil
        }
    }
    
    @objc func refreshAccessToken() {
        print("AppSessionTokenTimer refreshAccessToken")
        AppSessionManager.shared.refreshToken()
    }
}
