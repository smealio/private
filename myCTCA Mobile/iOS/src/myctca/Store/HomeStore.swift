//
//  HomeStore.swift
//  myctca
//
//  Created by Manjunath K on 2/15/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class HomeStore: BaseStore {
    func saveUserPreferences(route:HomeViewAPIRouter, completion:@escaping (ServerError?, RESTResponse)  -> Void) {
        
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let data = response, let returnData = String(data: data, encoding: .utf8) {
                
                print("saveUserPreferences return data: \(returnData)")
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
    
    func fetchAlertMessages(route:HomeViewAPIRouter, completion:@escaping ([Messages]?, ServerError?,  RESTResponse)  -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [Messages].self) {
            result, error in
            
            if let msgList = result as? [Messages] {
                completion(msgList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchUserSurveyInfo(route:AuthenticationAPIRouter, completion:@escaping (String?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: String.self) {
            result, error in
            if let sitUrl = result as? String {
                completion(sitUrl, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
