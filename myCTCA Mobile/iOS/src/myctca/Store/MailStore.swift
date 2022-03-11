//
//  MailStore.swift
//  myctca
//
//  Created by Manjunath K on 12/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class MailStore : BaseStore {
    
    func fetchMails(route:MailAPIRouter, completion:@escaping ([Mail]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [Mail].self) {
            result, error in
            if let mailList = result as? [Mail] {
                completion(mailList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchCareTeams(route:MailAPIRouter, completion:@escaping ([CareTeam]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [CareTeam].self) {
            result, error in
            if let careTeamList = result as? [CareTeam] {
                completion(careTeamList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func setMailRead(route:MailAPIRouter, completion:@escaping (ServerError?, RESTResponse)  -> Void) {
        
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let data = response, let returnData = String(data: data, encoding: .utf8) {
                print("setMailRead return data: \(returnData)")
                let returnVal = returnData.replacingOccurrences(of: "\"", with: "")
                
                //server returns no.of unread messages on succes,
                //so if any returned value, can be converted to int, then it is success
                if let _ = Int(returnVal) {
                    completion(nil, .SUCCESS)
                } else {
                    completion(ErrorManager.shared.getDefaultServerError(), .FAILED)
                }
            } else {
                completion(sError, .FAILED)
            }
        }
    }
    
    func archiveMail(route:MailAPIRouter, completion:@escaping (ServerError?, RESTResponse)  -> Void) {
        
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let data = response, let returnData = String(data: data, encoding: .utf8) {
                
                print("ArchiveMail return data: \(returnData)")
                if (returnData == self.expectedServerResponse) {
                    completion( nil, .SUCCESS)
                } else {
                    completion(ErrorManager.shared.getDefaultServerError(), .FAILED)
                }
            } else {
                completion(sError, .FAILED)
            }
        }
    }
    
    func sendNewMail(route:MailAPIRouter, completion:@escaping (ServerError?, RESTResponse)  -> Void) {
        
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let data = response, let returnData = String(data: data, encoding: .utf8) {
                
                print("sendNewMail return data: \(returnData)")
                if (returnData == self.expectedServerResponse) {
                    completion( nil, .SUCCESS)
                } else {
                    completion(ErrorManager.shared.getDefaultServerError(), .FAILED)
                }
            } else {
                completion(sError, .FAILED)
            }
        }
    }
}
