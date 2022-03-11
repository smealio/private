//
//  SendMessageManager.swift
//  myctca
//
//  Created by Manjunath K on 3/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class SendMessageManager: BaseManager {
    
    static let shared = SendMessageManager()
    
    func sendMessage(content:[String:String],
                            completion: @escaping (RESTResponse) -> Void) {
         
        SendMessageStore().sendMessage(route: SendMessageAPIRouter.sendMessage(infoList: content)) { [weak self]
            error, status in
            
            guard let self = self else { return }
            
            if status == .FAILED {
                self.serverError = error
            }
            
            completion(status)
        }
        
    }
}
