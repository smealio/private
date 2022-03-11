//
//  AppSessionReconnectionTimer.swift
//  myCTCA
//
//  Created by Tomack, Barry on 10/19/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

/**
 Meant to attempt a reconnection if Reachability reports a loss
 
 ** NOT IMPLEMENTED **
 */

class AppSessionReconnectionTimer : NSObject {
    
    private var reconnTimer: Timer?
    var reconnTimeInterval: TimeInterval = 30.0
    static var reconnCount: Int = 0
    static let reconnTries: Int = 2
    
    init(reconnectionTimeInterval:TimeInterval?) {
        super.init()
        if (reconnectionTimeInterval != nil) {
            reconnTimeInterval = reconnectionTimeInterval!
        }
        AppSessionReconnectionTimer.reconnCount = 0;
        resetReconnTimer()
    }
    
    func resetReconnTimer() {
        print("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ reset REconn timer")
        if(self.reconnTimer != nil) {
            self.killTimer()
        }
        self.reconnTimer = Timer.scheduledTimer(timeInterval: self.reconnTimeInterval,
                                                 target: self,
                                                 selector: #selector(self.tryReconnect),
                                                 userInfo: nil,
                                                 repeats: true)
        print("New Reconn Timer @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    }
    
    func killTimer() {
        if (self.reconnTimer != nil) {
            print("SessionReconencitonTimer exists - lets invalidate")
            self.reconnTimer?.invalidate()
            self.reconnTimer = nil
        }
    }
    
    @objc func tryReconnect() {
        if (AppSessionReconnectionTimer.reconnCount <= AppSessionReconnectionTimer.reconnTries) {
            AppSessionManager.shared.reconnect()
        }
        AppSessionReconnectionTimer.reconnCount += 1
    }
}
