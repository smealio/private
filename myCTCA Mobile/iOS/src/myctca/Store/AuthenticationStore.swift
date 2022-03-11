//
//  AuthenticationStore.swift
//  myctca
//
//  Created by Manjunath K on 12/16/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class AuthenticationStore : BaseStore {

    func authenticateUser(route:AuthenticationAPIRouter, completion:@escaping (AuthModel?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeLoginRequest(urlRequest: route, decodingType: AuthModel.self) {
            response, sError in
            if let model = response as? AuthModel {
                completion(model, nil, .SUCCESS)
            } else {
                completion(nil, sError, . FAILED)
            }
        }
    }
    
    func fetchIdentityUserInfo(route:AuthenticationAPIRouter, completion:@escaping (IdentityUser?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: IdentityUser.self) {
            result, error in
            if let iUser = result as? IdentityUser {
                completion(iUser, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchUserProfile(route:AuthenticationAPIRouter, completion:@escaping (MyCTCAUserProfile?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: MyCTCAUserProfile.self) {
            result, error in
            if let userProfile = result as? MyCTCAUserProfile {
                completion(userProfile, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchAllFacilities(route:AuthenticationAPIRouter, completion:@escaping ([Facility]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: Array<Facility>.self) {
            result, error in
            if let userProfile = result as? [Facility] {
                completion(userProfile, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchUserPreferences(route:AuthenticationAPIRouter, completion:@escaping ([UserPreference]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: Array<UserPreference>.self) {
            result, error in
            if let userPrefsList = result as? [UserPreference] {
                completion(userPrefsList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func switchToProxyUser(route:AuthenticationAPIRouter, completion:@escaping (AuthModelProxy?,ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeLoginRequest(urlRequest: route, decodingType: AuthModelProxy.self) {
            response, sError in
            if let model = response as? AuthModelProxy {
                completion(model, nil, .SUCCESS)
            } else {
                completion(nil, sError, . FAILED)
            }
        }
    }
    
    func fetchUserContactInfo(route:AuthenticationAPIRouter, completion:@escaping (MyCTCAUserContacts?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: MyCTCAUserContacts.self) {
            result, error in
            if let contact = result as? MyCTCAUserContacts {
                completion(contact, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }

}
