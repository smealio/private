//
//  FormsAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 7/21/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum FormsAPIRouter: BaseAPIRouter {
    case checkForExistingANNC
    case submitANNC(request:ANNCSubmissionInfo)
    case getMRNNumber
    case downloadANNC
    case getROIFormInfo
    case submitROI(request:ROI)
    case downloadROI
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        case .submitANNC, .submitROI:
            return .post
        default:
            return .get
        }
    }
    
    var parameters: RequestParams {
        switch self {
        case .checkForExistingANNC, .getMRNNumber:
            return .body([:])
        case .submitANNC(let request):
            return .submitANNCData(request)
        case .getROIFormInfo:
            return .url(["listOptions" : "true"])
        case .submitROI(let request):
            return .submitROIData(request)
        default :
            return .body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .checkForExistingANNC:
            return "/downloads/anncformexists"
        case .getMRNNumber:
            return "/downloads/getmrn"
        case .submitANNC:
            return "/downloads/submitanncform"
        case .downloadANNC:
            return "/downloads/downloadanncform"
        case .getROIFormInfo:
            return "/downloads/getroiform"
        case .submitROI:
            return "/downloads/submitroiform"
        case .downloadROI:
            return "/downloads/getroipdf"
            
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
            
        case .submitANNCData(let model):
            let encoder = JSONEncoder()
            let encodedData = try? encoder.encode(model)
            urlRequest.httpBody = encodedData
            
            let jsonString = String(data: encodedData!, encoding: .utf8)
            print("NEW request DATA: \(String(describing: jsonString))")
            
        case .submitROIData(let model):
            let encoder = JSONEncoder()
            let encodedData = try? encoder.encode(model)
            urlRequest.httpBody = encodedData
            
            let jsonString = String(data: encodedData!, encoding: .utf8)
            print("NEW request DATA: \(String(describing: jsonString))")
        
        default:
            break
        }
        
        return urlRequest
    }
}
