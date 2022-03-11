////
////  CustomHelpers.swift
////  myctca
////
////  Created by Manjunath K on 12/1/20.
////  Copyright © 2020 CTCA. All rights reserved.
////
//
import Foundation
import Alamofire

enum AppointmentsAPIRouter: BaseAPIRouter {
    case getAppts
    case apptRequest(valueDict:[String:String])
    case cancelAppt(valueDict:[String:String])
    case rescheduleAppt(valueDict:[String:String])
    case downloadAppts(valueDict:[String:String])
    case getTelehealthAccessToken
    case apptNewRequestV2(model: ApptReqest)
    case apptCancelRequestV2(model: ApptReqest)
    case apptRescheduleRequestV2(model: ApptReqest)
    case downloadApptByID(valueDict:[String:String])
    case none
    
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        case .apptRequest, .rescheduleAppt, .getTelehealthAccessToken,
                .apptNewRequestV2, .apptCancelRequestV2, .apptRescheduleRequestV2, .downloadApptByID:
            return .post
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .getAppts:
            return.body([:])
        case .apptRequest(let valueDict):
            //TODO = Refactoring - beter way to do
            return .body(["From":valueDict["From"] as Any, "Subject":valueDict["Subject"] as Any, "AppointmentDate":valueDict["AppointmentDate"] as Any, "PhoneNumber": valueDict["PhoneNumber"] as Any, "Comments":valueDict["Comments"] as Any, "facilityTimeZone":valueDict["facilityTimeZone"] as Any])
        case .cancelAppt(let valueDict):
            return .body(["From":valueDict["From"] as Any, "Subject":valueDict["Subject"] as Any, "AppointmentId":valueDict["AppointmentId"] as Any, "PhoneNumber": valueDict["PhoneNumber"] as Any, "Comments":valueDict["Comments"] as Any])
        case .rescheduleAppt(let valueDict):
            return .body(["From":valueDict["From"] as Any, "Subject":valueDict["Subject"] as Any, "AppointmentId":valueDict["AppointmentId"] as Any, "PhoneNumber": valueDict["PhoneNumber"] as Any, "Comments":valueDict["Comments"] as Any, "facilityTimeZone":valueDict["facilityTimeZone"] as Any])
        case .downloadAppts(let valueDict):
            return .url(["startDate":valueDict["startDate"]! as String, "endDate":valueDict["endDate"]! as String])
        case .apptNewRequestV2(let model), .apptCancelRequestV2(let model), .apptRescheduleRequestV2(let model):
            return .appointmentRequest(model)
        case .downloadApptByID(let valueDict):
            return .body(["appointmentId":valueDict["appointmentId"]! as String])
        default:
            return .body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .getAppts:
            return "/appointments/getappointments"
        case .apptRequest:
            return "/appointments/requestnewappointment"
        case .apptNewRequestV2:
            return "/appointments/requestnewappointmentv2"
        case .cancelAppt:
            return "/appointments/cancelappointment"
        case .rescheduleAppt:
            return "/appointments/rescheduleappointment"
        case .downloadAppts:
            return "/appointments/getappointmentsreport"
        case .getTelehealthAccessToken:
            return "https://apim.ctca-hope.com/test/telehealth/api/accesstoken"
        case .apptRescheduleRequestV2:
            return "/appointments/rescheduleappointmentv2"
        case .apptCancelRequestV2:
            return "/appointments/cancelappointmentv2"
        case .downloadApptByID:
            return "/appointments/getappointmentdetailsbyidreport"
        default:
            return ""
        }
    }
    
    // MARK: - URLRequestConvertible
    func asURLRequest() throws -> URLRequest {
            
        let url = URL(string: AppSessionParameters().portalDataServer)
        
        var urlRequest = URLRequest(url: url!.appendingPathComponent(path))
        
        switch self {
        case .getTelehealthAccessToken:
            urlRequest = URLRequest(url: URL(string: path)!)
            urlRequest.setValue("7556d0d1508a4deb8d15d0b41d0342fe", forHTTPHeaderField: "Ocp-Apim-Subscription-Key")
        default:
            // HTTP Method
            urlRequest.httpMethod = method.rawValue
            
            // Common Headers
            urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.acceptType.rawValue)
            urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
            urlRequest.setValue(AppSessionManager.shared.getBearerToken(), forHTTPHeaderField: HTTPHeaderField.authentication.rawValue)
        }
                    
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
                return URLQueryItem(name: pair.key, value: "\(pair.value)")
            }
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)
            components?.queryItems = queryParams
            urlRequest.url = components?.url
            
        case .appointmentRequest(let model):
            let encoder = JSONEncoder()
            encoder.dateEncodingStrategy = .formatted(.iso8601Full)
            let encodedData = try? encoder.encode(model)
            let jsonString = String(data: encodedData!, encoding: .utf8)
            print("appointmentRequest - \(jsonString)")
            urlRequest.httpBody = encodedData
        default:
            break
        }
        
        return urlRequest
    }
}
