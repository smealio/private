//
//  SendMessageAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 3/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum SendMessageAPIRouter: BaseAPIRouter {
    case sendMessage(infoList:[String:String])
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        case .sendMessage:
            return .post
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .sendMessage(let valueDict):
            return .body(valueDict)
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .sendMessage:
            return "/support/sendfeedback"
        }
    }

    func asURLRequest() throws -> URLRequest {
        var baseUrl = ""
        
        switch(self) {
        case .sendMessage:
            baseUrl = AppSessionParameters().portalDataServer
        }
        
        let url = URL(string: baseUrl)
        
        var urlRequest = URLRequest(url: url!.appendingPathComponent(path))
        
        // HTTP Method
        urlRequest.httpMethod = method.rawValue
        
        // Common Headers
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.acceptType.rawValue)
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
        urlRequest.setValue(AppSessionManager.shared.getBearerToken(), forHTTPHeaderField: HTTPHeaderField.authentication.rawValue)
        
        // Parameters
        switch parameters {
        case .url(let params):
            let queryParams = params.map { pair  in
                return URLQueryItem(name: pair.key, value: "\(pair.value)")
            }
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)
            components?.queryItems = queryParams
            urlRequest.url = components?.url
        case .payloadDataArray(let modelList):
            let encoder = JSONEncoder()
            let encodedData = try? encoder.encode(modelList)
            urlRequest.httpBody = encodedData
        case .body(let params):
            if params.count > 0 {
                urlRequest.setValue(ContentType.formEncode.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
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
        default:
            break
        }
        
        return urlRequest
    }
}
