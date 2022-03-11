//
//  LoginParameters.swift
//  myctca
//
//  Created by Tomack, Barry on 11/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

struct LoginParameters {
    var clientId: String = "CTCA.Portal.UI.IOS"
    var grantType: String = "password"
    var scope: String = "openid profile email api external.identity.readwrite impersonation"
    
    var username: String = ""
    var password: String = ""
    
    let oauthContentType: String = "application/x-www-form-urlencoded"
    let jsonContentType: String = "application/json"
    let accept: String = "application/json"
    
    var authorizeEndpoint: String? {
        get {
            let sessionParams = AppSessionParameters()
            return "\(sessionParams.ctcaHostServer)/connect/token"
        }
    }
    var userInfoEndpoint: String? {
        get {
            let sessionParams = AppSessionParameters()
            return "\(sessionParams.ctcaHostServer)/connect/userinfo?schema=openid"
        }
    }
    
    var loginEndpoint: String? {
        get {
            let sessionParams = AppSessionParameters()
            return "\(sessionParams.ctcaHostServer)/login"
        }
    }
    
    var authEndpointHost: String? {
        
        get {
            guard let url = URL(string: authorizeEndpoint!) else { return nil }
            let urlComponents = URLComponents(url: url,
                                              resolvingAgainstBaseURL: true)
            return urlComponents?.host
        }
    }
    
    var authEndpointPath: String? {
        
        get {
            guard let url = URL(string: authorizeEndpoint!) else { return nil }
            let urlComponents = URLComponents(url: url,
                                              resolvingAgainstBaseURL: true)
            return urlComponents?.path
        }
    }
    
    var userProfileEndpoint: String? {
        get {
            let sessionParams = AppSessionParameters()
            return "\(sessionParams.portalDataServer)/userprofile/getuserprofile"
        }
    }
    
    func facilityInfo(facilityCode: String) -> String {
        let sessionParams = AppSessionParameters()
        return "\(sessionParams.portalDataServer)/support/getfacilityinfo?facility=\(facilityCode)"
    }
    
    var jsonObject: [String: String] {
        
        get {
            let params = [
                "username":username,
                "password":password,
                "grant_type":grantType,
                "client_id":clientId,
                "client_secret":Environment().configuration(PlistKey.ClientSecret),
                "scope":scope
            ]
            
            return params
        }
    }
    
    var loginPostString: String {
        
        get {
            return "username=\(username)&password=\(password)&client_id=\(clientId)&client_secret=\(Environment().configuration(PlistKey.ClientSecret))&grant_type=\(grantType)&scope=\(scope)"
        }
    }
    
    func getImpersonatePostString(ctcaUniqueId: String) -> String {
        
        return "\(loginPostString)&acr_values=impersonate:\(ctcaUniqueId)"
    }
    

    var changeUserProfileEndpoint: String? {
        get {
            let sessionParams = AppSessionParameters()
            return "\(sessionParams.ctcaHostServer)/user/detail"
        }
    }

}
