//
//  HealthHistoryAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 1/6/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum HealthHistoryAPIRouter: BaseAPIRouter {
    case getVitals
    case getHealthIssues
    case getImmunizations
    case getAllergies
    case getPrescriptions
    case sendPrescriptions(request: PrescriptionRefillRequest)
    case downloadVitals
    case downloadHealthIssues
    case downloadImmunizations
    case downloadAllergies
    case downloadPrescriptions
    case none
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch(self) {
        case .sendPrescriptions:
            return .post
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch (self) {
        case .sendPrescriptions(let request):
            return .refillData(request)
        default:
            return.body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .getVitals:
            return "/healthhistory/getvitals"
            
        case .getHealthIssues:
            return "/healthhistory/gethealthissues"
            
        case .getImmunizations:
            return "/healthhistory/getimmunizations"
            
        case .getAllergies:
            return "/healthhistory/getallergies"
            
        case .getPrescriptions:
            return "/healthhistory/getprescriptions"
            
        case .sendPrescriptions:
            return "/securemessages/sendprescriptionrenewalrequest"
            
        case .downloadVitals:
            return "/healthhistory/getvitalsreport"
            
        case .downloadHealthIssues:
            return "/healthhistory/gethealthissuesreport"
            
        case .downloadImmunizations:
            return "/healthhistory/getimmunizationsreport"
            
        case .downloadAllergies:
            return "/healthhistory/getallergiesreport"
            
        case .downloadPrescriptions:
            return "/healthhistory/getprescriptionsreport"
            
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
        urlRequest.setValue(AppSessionManager.shared.getBearerAccessToken(), forHTTPHeaderField: HTTPHeaderField.authentication.rawValue)
        
        // Parameters
        switch parameters {
        case .url(let params):
            let queryParams = params.map { pair  in
                return URLQueryItem(name: pair.key, value: "\(pair.value)")
            }
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)
            components?.queryItems = queryParams
            urlRequest.url = components?.url
            
        case .refillData(let model):
            let encoder = JSONEncoder()
            let encodedData = try? encoder.encode(model)
            urlRequest.httpBody = encodedData
            
            let jsonString = String(data: encodedData!, encoding: .utf8)
            print("NEW request DATA: \(String(describing: jsonString))")
            
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
