//
//  KeychainConfiguration.swift
//  CTCAConnect
//
//  Created by Tomack, Barry on 11/7/16.
//  Copyright © 2016 CTCA. All rights reserved.
//

/*
Abstract:
A simple struct (based on Apple Generic Keychain Configuration) that defines the service name and access group to be used by the CTCA apps.
*/

import Foundation

struct KeychainConfiguration {
    
    static let serviceName = "CTCA.MyCTCA"
    
    /*
     Specifying an access group to use
     will create items shared across apps.
     
     For information on App ID prefixes, see:
     https://developer.apple.com/library/ios/documentation/General/Conceptual/DevPedia-CocoaCore/AppID.html
     and:
     https://developer.apple.com/library/ios/technotes/tn2311/_index.html
     */
    //    static let accessGroup = "[YOUR APP ID PREFIX].com.example.apple-samplecode.GenericKeychainShared"
    
    /*
     Not specifying an access group to use with `KeychainWrapper` instances
     will create items specific to each app.
     */
    static let accessGroup: String? = nil
}

enum KeychainDataKey: String {
    
    case password = "password"
    case token = "token"
}
