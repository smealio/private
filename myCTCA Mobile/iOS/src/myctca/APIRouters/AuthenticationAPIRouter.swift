//
//  AuthenticationAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 12/16/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum AuthenticationAPIRouter: BaseAPIRouter {
    
    case login(valueDict:[String:String])
    case getUserInfo
    case getUserProfile
    case getAllFacilities
    case getUserPreferences
    case openUserProfile
    case openRegistrationLink
    case openResetPasswordLink
    case openExprdPasswordLink
    case openTermsOfUseLink
    case openPrivacyPolicyLink
    case openShareRecords
    case openPrivacyPracticeLink
    case openCovertAccntLink(userId: String)
    case switchToProxy(valueDict:[String:String])
    case getUserSITLookupInfo
    case getUserContactInfo
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        case .login, .switchToProxy:
            return .post
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .login(let valueDict):
            return .body(["username":valueDict["username"]! as String, "password":valueDict["password"]! as String, "grant_type":valueDict["grant_type"]! as String, "client_id":valueDict["client_id"]! as String, "client_secret":valueDict["client_secret"]! as String,
                        "scope":valueDict["scope"]! as String])
        case .getUserInfo:
            return .url(["schema": "openid"])
        case .getUserProfile:
            return .url(["isNative": "true"])
        case .openRegistrationLink, .openResetPasswordLink, .openExprdPasswordLink:
            return .safari(["isNative": "true"])
        case .openUserProfile, .openTermsOfUseLink, .openPrivacyPolicyLink, .openPrivacyPracticeLink:
            return .safari([:])
        case .openShareRecords:
            return .safari(["active" : "userdetails-securityprivacy"])
        case .openCovertAccntLink(let userId):
            return .safari(["CtcaUniqueId" : userId])
        case .switchToProxy(let valueDict):
            return .body(["ToCtcaUniqueId":valueDict["proxyUserId"]! as String, "IsImpersonating":valueDict["IsImpersonating"]! as String])
        default:
            return .body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .login:
            return "/connect/token"
        case .getUserInfo:
            return "/connect/userinfo"
        case .getUserProfile:
            return "/userprofile/getuserprofile"
        case .getAllFacilities:
            return "/support/getfacilityinfoall"
        case .getUserPreferences:
            return "/userprofile/getuserpreferences"
        case .openUserProfile:
            return "/user/detail"
        case .openRegistrationLink:
            return "/user/registration"
        case .openResetPasswordLink:
            return "/user/passwordReset"
        case .openExprdPasswordLink:
            return "/user/passwordExpired"
        case .openTermsOfUseLink:
            return "/support/gettermsofuseasync"
        case .openPrivacyPolicyLink:
            return "https://www.cancercenter.com/privacy-policy"
        case .openShareRecords:
            return "user/detail"
        case .openPrivacyPracticeLink:
            return "/support/getprivacypolicyasync"
        case .openCovertAccntLink:
            return "/user/registration/mobilepatientverification"
        case .switchToProxy:
            return "/userprofile/impersonatemobileuser"
        case .getUserSITLookupInfo:
            return "/userprofile/getsitsurveyurlasync"
        case .getUserContactInfo:
            return "/contactinfo/getcontactinfo"
        }
    }
    
    // MARK: - URLRequestConvertible
    func asURLRequest() throws -> URLRequest {
        var baseURL = ""
        var contentType = ""
        var token:String?
        
        switch self {
        case .login:
            baseURL = AppSessionParameters().ctcaHostServer
            contentType = ContentType.formEncode.rawValue
        case .getUserInfo:
            baseURL = AppSessionParameters().ctcaHostServer
            contentType = ContentType.json.rawValue
            token = AppSessionManager.shared.getBearerToken()
        case .getAllFacilities:
            baseURL = AppSessionParameters().portalDataServer
            contentType = ContentType.json.rawValue
        case .switchToProxy:
            baseURL = AppSessionParameters().portalDataServer
            contentType = ContentType.json.rawValue
            if AppSessionManager.shared.getUserType() == .PROXY {
                token = AppSessionManager.shared.getBearerTokenForOriginalUser()
            } else {
                token = AppSessionManager.shared.getBearerToken()
            }
        default:
            baseURL = AppSessionParameters().portalDataServer
            contentType = ContentType.json.rawValue
            token = AppSessionManager.shared.getBearerToken()
        }
        
        let url = URL(string: baseURL)
        
        var urlRequest = URLRequest(url: url!.appendingPathComponent(path))
        urlRequest.setValue(contentType, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
        if let toeknValue = token {
            urlRequest.setValue(toeknValue, forHTTPHeaderField: HTTPHeaderField.authentication.rawValue)
        }

        // HTTP Method
        urlRequest.httpMethod = method.rawValue
        
        // Common Headers
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.acceptType.rawValue)
        
        // Parameters
        switch parameters {
        case .body(let params):
            if params.count > 0 {
                urlRequest.setValue(ContentType.formEncode.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
                let parameterArray = params.map { (arg) -> String in
                    
                    let (key, value) = arg
                    let valStr = value as! String
                    let percentVal = String(describing: valStr.addingPercentEncoding(withAllowedCharacters: .alphanumerics)!)
                    return "\(key)=\(percentVal)"
                }
                print(".Body : \(parameterArray)")
                let HTTPBody = parameterArray.joined(separator: "&").data(using: .utf8)
                urlRequest.httpBody = HTTPBody
            } else {
                urlRequest.httpBody = nil
            }
            
        case .url(let params):
            let queryParams = params.map { pair  in
                return URLQueryItem(name: pair.key, value: String(describing: pair.value))
            }
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)
            components?.queryItems = queryParams
            urlRequest.url = components?.url
        default:
            break
        }
        
        return urlRequest
    }
    
    func asUrl() -> URL? {
        var baseurl = ""
        switch self {
        case .openPrivacyPolicyLink:
            return URL(string: path)
        case .openTermsOfUseLink, .openPrivacyPracticeLink:
            baseurl = AppSessionParameters().portalDataServer
        default:
            baseurl = AppSessionParameters().ctcaHostServer
        }
        
        var url = URL(string: baseurl)
        
        switch parameters {
        case .safari(let params):
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)

            if params.count > 0 {
                let queryParams = params.map { pair  in
                    return URLQueryItem(name: pair.key, value: String(describing: pair.value))
                }
                components?.queryItems = queryParams
            }
            
            url = components?.url
        default:
            break
        }
    
        return url
    }
}

