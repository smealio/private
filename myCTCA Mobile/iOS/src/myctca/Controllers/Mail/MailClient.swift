//
//  MailClient.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

enum MailServiceError : Error {
    case dataEmptyError
    case mailResponseError
    case userAccessDenied
    case invalidScope
    case badJSONDataReturned
    case unknownError
}

class MailClient: MyCTCASessionClient {
    
    let expectedServerResponse = "\"Success\""
    
//    // Get Mail
//    func fetchMail(mailbox: MailBox,
//                   parameters: MailParameters,
//                   completion: @escaping ([Mail]?, ServerError?, RESTResponse) -> Void) {
//
//        guard let url: URL = URL(string: parameters.urlForMailbox(mailbox)) else { fatalError() }
//
//        var request: URLRequest = URLRequest(url: url)
//        NetworkServices.serveGETRequest(for: &request) {
//            (data, error) -> Void in
//            if let sError = error {
//                completion(nil, sError, .FAILED)
//                return
//            } else {
//                if let theData = data {
//                    do {
//                        let response = try JSONSerialization.jsonObject(with: theData as Data, options: .allowFragments) as! [[String: AnyObject]]
//                        print("Mail Response: \(response)")
//                        let mailAR: [Mail] = self.parseMailData(response)
//                        completion(mailAR, nil, .SUCCESS)
//                    } catch {
//                        AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
//                        completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
//                    }
//                } else {
//                    completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
//                }
//            }
//        }
//
//    }
//
//    func parseMailData(_ data:[[String: AnyObject]]) -> [Mail]{
//
//        var mailAR: [Mail] = Array()
//
//        for mailData in data {
//            let mail: Mail = Mail(mailData: mailData)
//            mailAR.append(mail)
//        }
//
//        return mailAR
//    }
//
//    // Care Team
//    func  fetchCareTeam(parameters: MailParameters,
//                        completion: @escaping([CareTeam]?, ServerError?, RESTResponse) -> Void) {
//
//        guard let url: URL = URL(string: parameters.careTeamURL) else { fatalError() }
//
//        var request: URLRequest = URLRequest(url: url)
//        NetworkServices.serveGETRequest(for: &request) {
//            (data, error) -> Void in
//            if let sError = error {
//                completion(nil, sError, .FAILED)
//                return
//            } else {
//                if let theData = data {
//                    do {
//                        let response = try JSONSerialization.jsonObject(with: theData as Data, options: .allowFragments) as! [[String: AnyObject]]
//                        print("CareTeam Response: \(response)")
//                        var careTeamAR: [CareTeam] = Array()
//                        for careTeamData in response {
//                            let careTeam: CareTeam = CareTeam(careTeamData: careTeamData)
//                            careTeamAR.append(careTeam)
//                        }
//                        completion(careTeamAR, nil, .SUCCESS)
//
//                    } catch {
//                        AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
//                        completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
//                    }
//                } else {
//                    completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
//                }
//            }
//        }
//    }
    
    // Send Mail
    /**
     {
     "mailMessageId": "",
     "from": "LABTEST, SRMCONE,  (30901096 SRMC)",
     "from": null,
     "selectedTo": null,
     "subject": "FROM PATIENT:    New Test SHM.  Please continue to disregard.",
     "comments": "New Test SHM.  Please keep disregarding.",
     "sent": "2018-01-18T12:26:54.917Z",
     "parentMessageId": null,
     "isRead": false,
     "messageType": 1
     }
     *  */
    func sendNewMail11(parameters: MailParameters,
                     mailInfo: NewMailInfo,
                     completion: @escaping (Bool, ServerError?, RESTResponse) -> Void) {
        
        guard let url: URL = URL(string: parameters.sendMailURL) else { fatalError() }
        
        var request: URLRequest = URLRequest(url: url)
        
        request.httpMethod = "POST"
        request.addValue(parameters.jsonContentType, forHTTPHeaderField: "Content-Type")
        request.addValue(parameters.accept, forHTTPHeaderField: "Accept")
        request.addValue(AppSessionManager.shared.getBearerAccessToken(), forHTTPHeaderField: "Authorization")
        
        let encoder = JSONEncoder()
        let encodedData = try? encoder.encode(mailInfo)
        let jsonString = String(data: encodedData!, encoding: .utf8)
        print("NEW MAIL DATA: \(String(describing: jsonString))")
        request.httpBody = encodedData
        
        let task = session.dataTask(with: request as URLRequest,
                                    completionHandler: { data, response, error in
                                        guard error == nil else {
                                            completion(false, ServerError(serverError: error!), .FAILED)
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
                                        if let returnData = String(data: data, encoding: .utf8) {
                                            print("Send Mail return data: \(returnData)")
                                            if (returnData == self.expectedServerResponse) {
                                                completion(true, nil, .SUCCESS)
                                            } else {
                                                completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                            }
                                        } else {
                                            AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                                            completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                        }
        })
        
        task.resume()
    }
    
    func markAsRead11(parameters: MailParameters,
                    body: [String: Any],
                    completion: @escaping (Bool, ServerError?, RESTResponse) -> Void) {
        
        let markAsReadURL = parameters.markAsReadURL
        
        guard let url: URL = URL(string: markAsReadURL) else { fatalError() }
        
        var request: URLRequest = URLRequest(url: url)
        
        request.httpMethod = "POST"
        request.addValue(AppSessionParameters().oauthContentType, forHTTPHeaderField: "Content-Type")
        request.addValue(AppSessionParameters().accept, forHTTPHeaderField: "Accept")
        request.addValue(AppSessionManager.shared.getBearerAccessToken(), forHTTPHeaderField: "Authorization")
        
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
                                            completion(false, ServerError(serverError: error!), .FAILED)
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
                                        if let returnData = String(data: data, encoding: .utf8) {
                                            print("MarkAsRead return data: \(returnData)")
                                            let returnVal = returnData.replacingOccurrences(of: "\"", with: "")
                                            
                                            //server returns no.of unread messages on succes,
                                            //so if any returned value, can be converted to int, then it is success
                                            if let _ = Int(returnVal) {
                                                completion(true, nil, .SUCCESS)
                                            } else {
                                                completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                            }
                                        } else {
                                            AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                                            completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                        }
        })
        
        task.resume()
    }
    
    func archiveMail11(parameters: MailParameters,
                     body: [String: Any],
                     completion: @escaping (Bool, ServerError?, RESTResponse) -> Void) {
        
        let archivedMailURL = parameters.archiveMailURL
        
        guard let url: URL = URL(string: archivedMailURL) else { fatalError() }
        
        var request: URLRequest = URLRequest(url: url)
        
        request.httpMethod = "POST"
        request.addValue(AppSessionParameters().oauthContentType, forHTTPHeaderField: "Content-Type")
        request.addValue(AppSessionParameters().accept, forHTTPHeaderField: "Accept")
        request.addValue(AppSessionManager.shared.getBearerAccessToken(), forHTTPHeaderField: "Authorization")
        
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
                                            completion(false, ServerError(serverError: error!), .FAILED)
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
                                        if let returnData = String(data: data, encoding: .utf8) {
                                            print("ArchiveMail return data: \(returnData)")
                                            if (returnData == self.expectedServerResponse) {
                                                completion(true, nil, .SUCCESS)
                                            } else {
                                                completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                            }
                                        } else {
                                            AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                                            completion(false, ErrorManager.shared.getDefaultServerError(), .FAILED)
                                        }
        })
        
        task.resume()
    }
}
