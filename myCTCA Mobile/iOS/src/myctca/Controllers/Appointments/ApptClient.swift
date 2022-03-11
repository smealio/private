//
//  ApptClient.swift
//  myctca
//
//  Created by Tomack, Barry on 12/6/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

enum ApptServiceError : Error {
    case dataEmptyError
    case apptResponseError
    case userAccessDenied
    case invalidScope
    case badJSONDataReturned
    case unknownError
}

/**
 Networking client to handle all GETs and POSTs to server for Appointment section
 */

class ApptClient: MyCTCASessionClient{

    // Get Appointments
    func fetchAppointments(parameters: ApptParameters,
                           completion: @escaping ([Appointment]?, ServerError?, RESTResponse) -> Void) {
        
        guard let url: URL = URL(string: parameters.getApptsURL) else { fatalError() }
        
        var request: URLRequest = URLRequest(url: url)
        NetworkServices.serveGETRequest(for: &request) {
            (data, error) -> Void in
            if let sError = error {
                completion(nil, sError, .FAILED)
                return
            } else {
                if let theData = data {
                    do {
                        let response = try JSONSerialization.jsonObject(with: theData as Data, options: .allowFragments) as! [[String: AnyObject]]
                        //print("Appt Response: \(response)")
                        let appointments: [Appointment] = self.parseApptData(response)
                        completion(appointments, nil, .SUCCESS)
                    } catch {
                        
                        AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                        completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                    }
                } else {
                    completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                }
            }
        }
    }
    
    func parseApptData(_ data:[[String: AnyObject]]) -> [Appointment]{
        
        var appointments: [Appointment] = Array()
        
        for appointmentData in data {
            let appointment: Appointment = Appointment(appointmentData: appointmentData)
            appointments.append(appointment)
        }
        
        return appointments
    }
    
    // New Appointment Request
    func requestAppointment(requestType: ApptRequestType,
                            parameters: ApptParameters,
                            body: [String: Any],
                            completion: @escaping (Bool, ServerError?, RESTResponse) -> Void) {
        
        var apptURL = parameters.newApptRequestURL
        if (requestType == ApptRequestType.reschedule) {
            apptURL = parameters.rescheduleApptRequestURL
        } else if (requestType == ApptRequestType.cancel) {
            apptURL = parameters.cancelApptRequestURL
        }
        
        guard let url: URL = URL(string: apptURL) else { fatalError() }
        
        var request: URLRequest = URLRequest(url: url)
        
        request.httpMethod = "POST"
        request.addValue(parameters.contentType, forHTTPHeaderField: "Content-Type")
        request.addValue(parameters.accept, forHTTPHeaderField: "Accept")
        request.addValue(AppSessionManager.shared.getBearerAccessToken(), forHTTPHeaderField: "Authorization")

        print("Data for body: \(body)")
        let parameterArray = body.map { (arg) -> String in
            
            let (key, value) = arg
            let valStr = value as! String
            let percentVal = String(describing: valStr.addingPercentEncoding(withAllowedCharacters: .alphanumerics)!)
            return "\(key)=\(percentVal)"
        }
        let HTTPBody = parameterArray.joined(separator: "&").data(using: .utf8)
        request.httpBody = HTTPBody
        
        let task = session.dataTask(with: request as URLRequest,
                                    completionHandler: { data, response, error in
                                        guard error == nil else {
                                            completion(false,ServerError(serverError: error!), .FAILED)
                                            return
                                        }
                                        if let urlResponse = response as? HTTPURLResponse, !(200 ... 299 ~= urlResponse.statusCode), 302 != urlResponse.statusCode {
                                            AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(urlResponse.statusCode)")
                                            
                                            completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                            return
                                        }
                                        
                                        guard let data = data else {
                                            completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                            return
                                        }
                                        
                                        do {
                                            //create json object from data
                                            if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: AnyObject] {
                                                print("requestAppointment: \(json)")
                                                completion(true, nil, .SUCCESS)
                                            }
                                            
                                        } catch let error {
                                            
                                            AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                                            print(error.localizedDescription)
                                            completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                        }
        })
        
        task.resume()
    }
}
