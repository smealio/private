//
//  CallManager.swift
//  myctca
//
//  Created by Manjunath K on 6/28/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import CoreTelephony
import CallKit

protocol TelephonyDelgate : AnyObject {
    func didPhoneCallConnected()
    func didPhoneCallEnded()
}

class CallManager: NSObject, CXCallObserverDelegate {
    var observer : CXCallObserver?
    weak var delegate: TelephonyDelgate?
    
    override init() {
        super.init()
        
        self.observer = CXCallObserver()
        self.observer?.setDelegate(self, queue: nil)
    }
    
    func callObserver(_ callObserver: CXCallObserver, callChanged call: CXCall) {
        
        if let del = delegate {
            if call.hasEnded {
                print("CXCall.hasEnded")
                del.didPhoneCallEnded()
            } else if call.hasConnected {
                print("CXCall.hasConnected")
                del.didPhoneCallConnected()
            } else {
                print("CXCall.stateOther")
            }
        }
    }
    
}
