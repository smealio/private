//
//  AuthenticationManager.swift
//  myctca
//
//  Created by Manjunath K on 12/16/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class AuthenticationManager: BaseManager {
    
    var clientId = "CTCA.Portal.UI.IOS"
    var grantType = "password"
    var scope = "openid profile email api external.identity.readwrite impersonation"
    var response_type = "id_token token"
    
    var username: String = ""
    var password: String = ""
    
    let invalidScope: String = "invalid_scope"
    let invalidGrant: String = "invalid_grant"
    
    var refreshingToken = false
        
    var loginPostString: String {
        get {
            return "username=\(username)&password=\(password)&client_id=\(clientId)&client_secret=\(Environment().configuration(PlistKey.ClientSecret))&grant_type=\(grantType)&scope=\(scope)"
        }
    }
    
    func authenticateUser(username:String, password:String, completion:@escaping (ServerError?, RESTResponse) -> Void) {
        self.username = username
        self.password = password
        
        let params = ["username": username, "password": password, "client_id": clientId, "client_secret": Environment().configuration(PlistKey.ClientSecret), "grant_type": grantType, "scope": scope]
        
        AuthenticationStore().authenticateUser(route: AuthenticationAPIRouter.login(valueDict: params)) { [weak self]
            authModel, error, status in
            
            guard let self = self else { return }
            
            if status == .SUCCESS, let result = authModel {
                if let responseError = result.error {
                    var sError = ErrorManager.shared.getDefaultServerError()
                    if (responseError == self.invalidScope) {
                        sError = ServerError(errorCode:(AuthServiceError.invalidScope).rawValue)
                        if let msg = result.errorMsg {
                            sError.errorMessage = msg
                        }
                    } else if (responseError == self.invalidGrant){
                        var errMsg = ""
                        if let msg = result.errorMsg {
                            errMsg = msg
                        }
                        if (errMsg == CommonMsgConstants.userAccountLockedMessage) {
                            sError = ServerError(errorCode:(AuthServiceError.userIsLocked).rawValue)
                        } else {
                            sError = ServerError(errorCode:(AuthServiceError.userAccessDenied).rawValue)
                        }
                        sError.errorMessage = errMsg
                    } else {
                        sError =  ServerError(errorCode:(AuthServiceError.accessTokenResponseError).rawValue)
                    }
                    AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, "/connect/token", "\(PARSE_ERROR_CODE)")
                    completion(sError, .FAILED)
                } else {
                    let accessToken = AccessToken(model: authModel!)
                    AppSessionManager.shared.currentUser.accessToken = accessToken

                    completion(nil, .SUCCESS)
                }
            } else {
                AnalyticsManager.shared.logException(CTCAAnalyticsConstants.LOGIN_REST_API_FAILURE, "/connect/token", "\(API_ERROR_CODE)")
                completion(error, .FAILED)
            }
        }
    }
    
    func fetchIdentityUserInfo(completion:@escaping (RESTResponse) -> Void) {
        AuthenticationStore().fetchIdentityUserInfo(route: AuthenticationAPIRouter.getUserInfo) { [weak self]
            identityUser, error, status in
            
            guard let self = self else { return }
            
            if status == .FAILED {
                self.serverError = error
                completion(status)
            } else {
                AppSessionManager.shared.currentUser.iUser = identityUser
                AnalyticsManager.shared.logException(CTCAAnalyticsConstants.LOGIN_REST_API_FAILURE, "/connect/userinfo", "\(API_ERROR_CODE)")
                completion(status)
            }
        }
    }
    
    func fetchUserProfile(completion:@escaping (RESTResponse) -> Void) {
        AuthenticationStore().fetchUserProfile(route: AuthenticationAPIRouter.getUserProfile) { [weak self]
            userProfile, error, status in
            
            guard let self = self else { return }
            
            if status == .FAILED {
                self.serverError = error
                completion(status)
                AnalyticsManager.shared.logException(CTCAAnalyticsConstants.LOGIN_REST_API_FAILURE, "/userprofile/getuserprofile", "\(API_ERROR_CODE)")
            } else {
                
                AppSessionManager.shared.currentUser.userProfile = userProfile
                
                if let epiId = userProfile?.epiId {
                    AppSessionManager.shared.currentUser.currentUserId = epiId
                } else {
                    AppSessionManager.shared.currentUser.currentUserId = ""
                }
                completion(status)
            }
        }
    }
    
    func fetchAllFacilites(facCode:String = "", completion:@escaping (RESTResponse) -> Void) {
        AuthenticationStore().fetchAllFacilities(route: AuthenticationAPIRouter.getAllFacilities) { [weak self]
            facilities, error, status in
            
            guard let self = self else { return }
            
            if status == .FAILED {
                self.serverError = error
                completion(status)
                AnalyticsManager.shared.logException(CTCAAnalyticsConstants.LOGIN_REST_API_FAILURE, "/support/getfacilityinfoall", "\(API_ERROR_CODE)")
            } else {
                AppSessionManager.shared.setAllFacilityInfo(facilities)
                if !facCode.isEmpty {
                    AppSessionManager.shared.setPrimaryFacility(facilityCode: facCode)
                }
                completion(status)
            }
        }
    }
    
    func fetchUserPreferences(completion:@escaping (RESTResponse) -> Void) {
        AuthenticationStore().fetchUserPreferences(route: AuthenticationAPIRouter.getUserPreferences) { [weak self]
            preferences, error, status in
            
            guard let self = self else { return }

            if status == .FAILED {
                self.serverError = error
                completion(status)
                AnalyticsManager.shared.logException(CTCAAnalyticsConstants.LOGIN_REST_API_FAILURE, "/userprofile/getuserpreferences", "\(API_ERROR_CODE)")
            } else {
                if let list = preferences {
                    AppSessionManager.shared.currentUser.currentUserPref = list
                }
                completion(status)
            }
        }
    }
    
    func switchToProxyUser(proxyUserId: String, toPatient:Bool, completion:@escaping (ServerError?, RESTResponse) -> Void) {
        var params = ["proxyUserId": proxyUserId]
            
        if toPatient {
            params["IsImpersonating"] = "true"
        } else {
            params["IsImpersonating"] = "false"
        }
        
        AuthenticationStore().switchToProxyUser(route: AuthenticationAPIRouter.switchToProxy(valueDict: params)) { [weak self]

            authModel, error, status in
            
            guard let self = self else { return }
            
            if !self.refreshingToken && AppSessionManager.shared.getUserType() == .PROXY {
                self.changeToOriginalUser()
                completion(nil, .SUCCESS)
                return
            }
            
            if status == .SUCCESS, let result = authModel {
                if let responseError = result.error {
                    var sError = ErrorManager.shared.getDefaultServerError()
                    if (responseError == self.invalidScope) {
                        sError = ServerError(errorCode:(AuthServiceError.invalidScope).rawValue)
                        if let msg = result.errorMsg {
                            sError.errorMessage = msg
                        }
                    } else if (responseError == self.invalidGrant){
                        sError = ServerError(errorCode:(AuthServiceError.userAccessDenied).rawValue)
                        if let msg = result.errorMsg {
                            sError.errorMessage = msg
                        }
                    } else {
                        sError =  ServerError(errorCode:(AuthServiceError.accessTokenResponseError).rawValue)
                    }
                    AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, "/connect/token", "\(PARSE_ERROR_CODE)")
                    completion(sError, .FAILED)
                } else {
                    let accessToken = AccessToken(model: authModel!)
                    if !self.refreshingToken {
                        //set user mode
                        AppSessionManager.shared.changeProxyMode(set: true)
                        //back up details
                        AppSessionManager.shared.switchToImpersonation()
                        
                        self.refreshingToken = false
                    }
                    //set new access token
                    AppSessionManager.shared.setAccessToken(token: accessToken)
                    
                    completion(nil, .SUCCESS)
                }
            } else {
                completion(error, .FAILED)
            }
            
        }
    }

    func changeToOriginalUser() {
        if AppSessionManager.shared.getUserType() == .PROXY {
            //set user mode
            AppSessionManager.shared.changeProxyMode(set: false)
            //set back to original records
            AppSessionManager.shared.switchBackToCaregiverUser()
        }
    }
    
    func fetchUserContactInfo(completion: @escaping(RESTResponse) -> Void) {
        AuthenticationStore().fetchUserContactInfo(route: AuthenticationAPIRouter.getUserContactInfo) { [weak self]
            
            result, error, status in
            
            guard let self = self else { return }
            
            if status == .SUCCESS, let contact = result {
                AppSessionManager.shared.currentUser.userContacts = contact
                completion(.SUCCESS)
            } else {
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(.FAILED)
                }
            }
        }
    }
}
