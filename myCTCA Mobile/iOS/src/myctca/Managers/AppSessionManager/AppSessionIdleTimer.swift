//
//  AppSessionIdleTimer.swift
//  myCTCA
//
//  Created by Tomack, Barry on 1/17/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation


struct UserValues {
    static let defaultSessionTimeOutPeriod = 300.0 //sec, i.e 5 mins
    static var sessionTimeOutPeriod = 300.0 //sec, i.e 5 mins
}

class AppSessionIdleTimer: NSObject {
    
    private var idleTimer: Timer?
    
    init(idleTime: TimeInterval?) {
        super.init()
        resetIdleTimer()
    }
    
    func resetIdleTimer() {
        print("RESET TIMER")
        if(self.idleTimer != nil) {
            self.killTimer()
        }
        self.idleTimer = Timer.scheduledTimer(timeInterval: UserValues.sessionTimeOutPeriod,
                                              target: self,
                                              selector: #selector(self.sesssionExpiryHandler),
                                              userInfo: nil,
                                              repeats: false)
    }
    
    func killTimer() {
        if (self.idleTimer != nil) {
            self.idleTimer?.invalidate()
            self.idleTimer = nil
        }
    }
    
    @objc func endCurrentSession() {
        print("AppSessionIdleTimer endCurrentSession")
        AppSessionManager.shared.endCurrentSession()
    }
    
    @objc func sesssionExpiryHandler() {
        AppSessionManager.shared.didSessionExpired()
    }
}
