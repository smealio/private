//
//  LabsAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 12/10/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum LabsAPIRouter: BaseAPIRouter {
    case getLabReports
    case downloadLabReports(valueDict:[String:String])
    case none
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        case .getLabReports:
            return .get
        case .downloadLabReports:
            return .get
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .getLabReports:
            return.body([:])
        case .downloadLabReports(let valueDict):
            return .url(["performeddate":valueDict["performeddate"]! as String, "collectedby":valueDict["collectedby"]! as String])
        default:
            return.body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .getLabReports:
            return "/labresults/getlabresultsgrouped"
        case .downloadLabReports:
            return "/labresults/getlabresultsreport"
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
        urlRequest.setValue(AppSessionManager.shared.getBearerToken(), forHTTPHeaderField: HTTPHeaderField.authentication.rawValue)
        
        // Parameters
        switch parameters {
        case .body(let params):
            if params.count > 0 {
                urlRequest.setValue(ContentType.formEncode.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
                //urlRequest.httpBody = try JSONSerialization.data(withJSONObject: params, options: [])
                let parameterArray = params.map { (arg) -> String in
                    
                    let (key, value) = arg
                    let valStr = value as! String
                    let percentVal = String(describing: valStr.addingPercentEncoding(withAllowedCharacters: .alphanumerics)!)
                    return "\(key)=\(percentVal)"
                }
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
}
