//
//  AuthenticationClient
//  CTCAConnect
//
//  Created by Tomack, Barry on 10/26/16.
//  Copyright © 2016 CTCA. All rights reserved.
//

import UIKit

enum AuthServiceError : Int {
    case userIsLocked = 990
    case userAccessDenied
    case accessTokenResponseError
    case invalidScope
    case badJSONDataReturned
    case dataEmptyError
}

class AuthenticationClient: MyCTCASessionClient {
    
    let invalidScope: String = "invalid_scope"
    let invalidGrant: String = "invalid_grant"
    
    let ERROR_RETRIEVING_IDENTITY_USER_DATA: String = "Error retrieving Identity User data"
    let ERROR_PARSING_IDENTITY_USER_DATA: String = "Error parsing Identity User data"
    let ERROR_LOCATING_IDENTITY_USER_DATA: String = "Error locating Identity User data"
    /**
     When a user needs to switch to a proxy, use this method.
     */
    func switchToProxyUser(parameters: LoginParameters,
                           ctcaUniqueId: String,
                           completion: @escaping (AccessToken?, ServerError?, String?) -> Void) {
        
        guard let url: URL = URL(string: parameters.authorizeEndpoint!) else { fatalError() }
        
        var request: URLRequest = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue(parameters.oauthContentType, forHTTPHeaderField: "Content-Type")
        request.addValue(parameters.accept, forHTTPHeaderField: "Accept")
        print("httpBody: \(parameters.getImpersonatePostString(ctcaUniqueId: ctcaUniqueId))")
        request.httpBody = parameters.getImpersonatePostString(ctcaUniqueId: ctcaUniqueId).data(using: .utf8)
        
        let task = session.dataTask(with: request) { (data, response, error) -> Void in
            
            if error != nil {
                print("switchToProxyUser error connection: \(String(describing: error))")
                completion(nil, ServerError(serverError: error!), nil)
                return
            }
            if let theData = data {
                do {
                    let response = try JSONSerialization.jsonObject(with: theData as Data, options: .allowFragments) as! [String: AnyObject]
                    if (AccessToken.validJSON(response)) {
                        
                        let token: AccessToken = AccessToken(accessToken: response["access_token"] as! String,
                                                             expiresIn: response["expires_in"] as! UInt,
                                                             tokenType: response["token_type"] as! String)
                        
                        print("switchToProxyUser accessToken: \(String(describing: token))")
                        completion(token, nil, nil)
                    } else {
                        let responseError: String? = response["error"] as? String
                        if (responseError == self.invalidScope) {
                            var sError = ServerError(errorCode:(AuthServiceError.invalidScope).rawValue)
                            if let msg = response["error_description"] as? String {
                                sError.errorMessage = msg
                            }
                            completion(nil, sError, response["error_description"] as? String )
                        } else if (responseError == self.invalidGrant){
                            var sError = ServerError(errorCode:(AuthServiceError.userAccessDenied).rawValue)
                            if let msg = response["error_description"] as? String {
                                sError.errorMessage = msg
                            }
                            completion(nil, sError, response["error_description"] as? String )
                        } else {

                            AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                            completion(nil, ServerError(errorCode:(AuthServiceError.accessTokenResponseError).rawValue), response["error_description"] as? String)
                        }
                    }
                } catch {
                    AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                    completion(nil, ServerError(serverError:(AuthServiceError.badJSONDataReturned) as! Error), nil)
                }
            } else {
                completion(nil, ServerError(serverError:(AuthServiceError.dataEmptyError) as! Error), nil)
            }
        }
        task.resume()
    }
    
    /**
     This method is basically identical to the loginWithParameters method.
     
     When the Resource-Owner flow is changed to an Authentication Code flow, it will
     probably be necessary to have a standalone refreshToken method.
     */
    func refreshToken (parameters: LoginParameters,
                       completion: @escaping (AccessToken?, ServerError?, RESTResponse) -> Void) {
            
        guard let url: URL = URL(string: parameters.authorizeEndpoint!) else { fatalError() }
        
        var request: URLRequest = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue(parameters.oauthContentType, forHTTPHeaderField: "Content-Type")
        request.addValue(parameters.accept, forHTTPHeaderField: "Accept")
        
        request.httpBody = parameters.loginPostString.data(using: .utf8)
        
        let task = session.dataTask(with: request) { (data, response, error) -> Void in
            
            if error != nil {
                print("loginWithParameters error connection: \(String(describing: error))")
                completion(nil, ServerError(serverError: error!), .FAILED)
                return
            }
            
            if let urlResponse = response as? HTTPURLResponse, !(200 ... 299 ~= urlResponse.statusCode), 302 != urlResponse.statusCode {
                AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(urlResponse.statusCode)")

                completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                return
            }
            
            if let theData = data {
                do {
                    let response = try JSONSerialization.jsonObject(with: theData as Data, options: .allowFragments) as! [String: AnyObject]
                    if (AccessToken.validJSON(response)) {
                        
                        let token: AccessToken = AccessToken(accessToken: response["access_token"] as! String,
                                                             expiresIn: response["expires_in"] as! UInt,
                                                             tokenType: response["token_type"] as! String)
                        completion(token, nil, .SUCCESS)
                    } else {
                        completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                    }
                } catch {

                    AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                    completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                }
            } else {
                completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
            }
        }
        task.resume()
    }

}
