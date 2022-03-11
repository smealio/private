//
//  AppSessionParameters.swift
//  myctca
//
//  Created by Tomack, Barry on 11/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

struct AppSessionParameters {
    
    //Example (for alpha environment): ctcaHostServer = "https://alphaaccounts.myctca.com"
    let ctcaHostServer: String = "\(Environment().configuration(PlistKey.ServerProtocol))://\(Environment().configuration(PlistKey.CTCAHostServer))"
    
    let accessTokenValidationEndpoint: String = "/accesstokenvalidation"
    
    let deviceRegistrationURL: String = "https://alphapushnotification.myctca.com/api/v1/deviceregistration"
    
    //Example (for alpha environment): portalDataServer = "https://v3alphaservice.myctca.com/api/v1"
    let portalDataServer: String = "\(Environment().configuration(PlistKey.ServerProtocol))://\(Environment().configuration(PlistKey.PortalDataServer))"
    
    let oauthContentType: String = "application/x-www-form-urlencoded"
    let accept: String = "application/json"
    
    var tokenPostString: String {
        get {
            return "token=\(String(describing: AppSessionManager.shared.currentUser.accessToken))"
        }
    }
    
    var revokeTokenPostString: String {
        get {
            let aToken = AppSessionManager.shared.currentUser.accessToken
            return "token=\(String(describing: aToken!.accessToken))&token_type_hint=access_token"
        }
    }
    
    let identityService:String = "https://alphaidentity.myctca.com/api/v1/users/"
    
    var tokenRevocationEndpoint:String {
        get {
            return "\(ctcaHostServer)/connect/revocation"
        }
    }
    
    var tokenRevocationAuthorizaton: String {
        get {
            let loginParams = LoginParameters()
            let authString = "\(loginParams.clientId):\(Environment().configuration(PlistKey.ClientSecret))"
            let base64String = Data(authString.utf8).base64EncodedString()
            //            let utf8str = authString.data(using: String.Encoding.utf8)
            //            let base64Encoded = utf8str?.base64EncodedStringWithOptions(NSData.Base64EncodingOptions(rawValue: 0))
            return "Basic \(base64String)"
        }
    }
    
    let boundary: String = UUID().uuidString
    var imageUploadContentType: String? {
        get {
            return "multipart/form-data; boundary=\(boundary)"
        }
    }
    
    var submitFeedbackURL: String {
        get {
            return "\(self.portalDataServer)/support/sendfeedback"
        }
    }
}
