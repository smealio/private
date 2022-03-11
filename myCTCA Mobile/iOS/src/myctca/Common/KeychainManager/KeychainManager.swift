//
//  KeychainManager.swift
//  CTCAConnect
//
//  Created by Tomack, Barry on 11/8/16.
//  Copyright © 2016 CTCA. All rights reserved.
//

import Foundation

class KeychainManager {

    var keychainWrapper: KeychainAccess?
    
    static let shared = KeychainManager()
    
    private init() {}
    
    // Alternative...the first save method didn't work (issues with update)
     
     func save(data: [String: AnyObject], account: String) {
        if account.isEmpty {
            return
        }
        
        // Delete any existing token data and create new token data
        do {
            try keychainWrapper?.deleteData(account: account)
        }
        catch {
            print("KeychainManager save method deletion failed as saving it for first time")
        }
        
        
        do {
            try keychainWrapper?.createData(account: account, data: data)
        }
        catch {

            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, ["exception": error.localizedDescription, "method": "KeychainManager:save"])
            print("KeychainManager save method experienced error setting new \(account) data: \(error)")
        }
     }
 
    func delete(account: String) {
        if account.isEmpty {
            return
        }
 
        do {
            try keychainWrapper?.deleteData(account: account)
        }
        catch {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, ["exception": error.localizedDescription, "method": "KeychainManager:delete"])

            print("KeychainManager delete method experienced error deleting data for \(account): \(error)")
        }
    }
    
    func fetch(account: String) -> [String: AnyObject]? {
        if account.isEmpty {
            return nil
        }
        
        var dict: [String: AnyObject]? = nil
        do {
            try dict = keychainWrapper?.readData(account: account) 
        }
        catch {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, ["exception": error.localizedDescription, "method": "KeychainManager:fetch"])

            print("KeychainManager experienced error retrieving data for  \(account): \(error)")
        }
        return dict
    }
    
}
