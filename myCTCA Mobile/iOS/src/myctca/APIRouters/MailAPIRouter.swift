//
//  MailAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 12/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum MailAPIRouter: BaseAPIRouter {
    
    case getNewMail
    case getSentMail
    case getArchivedMail
    case sendMail(value:NewMailInfo)
    case archiveMail(value:String)
    case setMailRead(value:String)
    case getCareTeam
    case none
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        case .getNewMail, .getSentMail, .getArchivedMail, .getCareTeam:
            return .get
        case .sendMail, .setMailRead, .archiveMail:
            return .post
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .getNewMail:
            return .url(["mailFolder": "Inbox"])
        case .getSentMail:
            return .url(["mailFolder": "SentItems"])
        case .getArchivedMail:
            return .url(["mailFolder": "DeletedItems"])
        case .getCareTeam:
            return .body([:])
        case  .sendMail(let model):
            return .mailData(model)
        case .archiveMail(let value), .setMailRead(let value):
            return .body(["mailMessageId":value])
        default:
            return.body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .getNewMail:
            return "/securemessages/getsecuremessages"
        case .getSentMail:
            return "/securemessages/getsecuremessages"
        case .getArchivedMail:
            return "/securemessages/getsecuremessages"
        case .sendMail:
            return "/securemessages/sendsecuremessage"
        case .archiveMail:
            return "/securemessages/archivesecuremessage"
        case .setMailRead:
            return "/securemessages/setsecuremessageread"
        case .getCareTeam:
            return "/securemessages/getcareteams"
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
        case .url(let params):
            let queryParams = params.map { pair  in
                return URLQueryItem(name: pair.key, value: "\(pair.value)")
            }
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)
            components?.queryItems = queryParams
            urlRequest.url = components?.url
            
        case .mailData(let model):
            let encoder = JSONEncoder()
            let encodedData = try? encoder.encode(model)
            urlRequest.httpBody = encodedData
            
            let jsonString = String(data: encodedData!, encoding: .utf8)
            print("NEW MAIL DATA: \(String(describing: jsonString))")
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
