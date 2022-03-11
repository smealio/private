//
//  MedDocsAPIRouter.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 14/07/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum MedDocsAPIRouter: BaseAPIRouter {
    case getClinicalSummaries(valueDict:[String:String])
    case getClinicalSummaryData(valueDict:[String:String])
    case downloadClinicalSummary(request:ClinicalSummaryDownloadInfo)
    case transmitClinicalSummary(valueDict:[String:String], request:ClinicalSummaryTrasmitInfo)
    case getCarePlanDetails
    case downloadCarePlan
    case getmedicaldocuments(type: String)
    case getmedicaldocument(type: String, id: String)
    case downloadmedicaldocumentreport(type: String, id: String)
    case downloadimagingdocumentreport(id: String)
    case getimagingdocuments
    case none
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        case .getClinicalSummaryData, .transmitClinicalSummary, .downloadClinicalSummary:
            return .post
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .getClinicalSummaries(let valueDict):
            if let startDate = valueDict["startDate"],
               let endDate = valueDict["endDate"] {
                if !startDate.isEmpty && !endDate.isEmpty {
                    return .url(["startDate": startDate, "endDate": endDate])
                }
            }
            return .url([:])
        case .getClinicalSummaryData(let valueDict):
            return .body(["DocumentId": valueDict["DocumentId"] as Any, "DirectAddress": valueDict["DirectAddress"] as Any])
        case .transmitClinicalSummary(let valueDict,let request):
            return .csTrasmitData_url(valueDict, request)
        case .downloadClinicalSummary(let request):
            return .csDownloadData(request)
        case .getCarePlanDetails, .downloadCarePlan, .getimagingdocuments:
            return .body([:])
        case .getmedicaldocuments(let type):
            return .url(["docType": type])
        case .getmedicaldocument(let type, let id):
            return .url(["docType": type, "documentid": id])
        case .downloadmedicaldocumentreport(let type, let id):
            return .url(["docType": type, "documentid": id])
        case .downloadimagingdocumentreport(let id):
            return .url(["documentid": id])
        default:
            return .safari([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .getClinicalSummaries:
            return "/documentexchange/getccdadocumentlist"
        case .getClinicalSummaryData:
            return "/documentexchange/getccdadocumentdetail"
        case .transmitClinicalSummary:
            return "/documentexchange/transmitccdadocumentregularemail"
        case .downloadClinicalSummary:
            return "/documentexchange/downloadccdadocument"
        case .getCarePlanDetails:
            return "/medicaldocuments/getcareplan"
        case .downloadCarePlan:
            return "/medicaldocuments/getcareplanreport"
        case .getmedicaldocuments:
            return "/medicaldocuments/getmedicaldocuments"
        case .getmedicaldocument:
            return "/medicaldocuments/getmedicaldocument"
        case .downloadmedicaldocumentreport:
            return "/medicaldocuments/getmedicaldocumentreport"
        case .downloadimagingdocumentreport:
            return "/medicaldocuments/getimagingdocumentreport"
        case .getimagingdocuments:
            return "/medicaldocuments/getimagingdocuments"
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
            
            
        case .csTrasmitData_url(let params, let model):
            let queryParams = params.map { pair  in
                return URLQueryItem(name: pair.key, value: String(describing: pair.value))
            }
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)
            components?.queryItems = queryParams
            urlRequest.url = components?.url
            
            let encoder = JSONEncoder()
            let encodedData = try? encoder.encode(model)
            urlRequest.httpBody = encodedData
            
            let jsonString = String(data: encodedData!, encoding: .utf8)
            print("NEW request DATA: \(String(describing: jsonString))")
            
        case .csDownloadData(let model):
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
