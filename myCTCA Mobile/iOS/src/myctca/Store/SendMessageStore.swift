//
//  SendMessageStore.swift
//  myctca
//
//  Created by Manjunath K on 3/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class SendMessageStore: BaseStore {
    func sendMessage(route:SendMessageAPIRouter, completion:@escaping (ServerError?,  RESTResponse)  -> Void) {
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let data = response, let returnData = String(data: data, encoding: .utf8) {
                
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
