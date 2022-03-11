//
//  AppInfoAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 12/11/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum AppInfoAPIRouter: BaseAPIRouter {
    case getApplicationInfo
    case openAppStore
    case none
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .openAppStore:
            return .safari([:])
        default:
            return.body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .getApplicationInfo:
            return "/support/getapplicationinfo"
        case .openAppStore:
            return "https://apps.apple.com/us/app/myctca/id1313005971"
        default:
            return ""
        }
    }

    // MARK: - URLRequestConvertible
    func asURLRequest() throws -> URLRequest {
        let url = URL(string: AppSessionParameters().portalDataServer)
        
        var urlRequest = URLRequest(url: url!.appendingPathComponent(path))
        
        // HTTP Method
        urlRequest.httpMethod = method.rawValue
        
        // Common Headers
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.acceptType.rawValue)
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
        
        // Parameters
        urlRequest.httpBody = nil
        return urlRequest
    }
}
