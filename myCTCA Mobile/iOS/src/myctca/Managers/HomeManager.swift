//
//  HomeManager.swift
//  myctca
//
//  Created by Manjunath K on 2/15/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class HomeManager: BaseManager {
    
    static let shared = HomeManager()
    var messages = [Messages]()
    var messagesAll = [Messages]()
    
    func saveUserPrefrences(preference:UserPreferencePayload,
                            completion: @escaping (RESTResponse) -> Void) {
  
        var preferences = [preference]
        
        for item in AppSessionManager.shared.currentUser.currentUserPref {
            if item.userPreferenceType != preference.userPreferenceType {
                preferences.append(item.getPayloadVariant())
            }
        }
        
        HomeStore().saveUserPreferences(route: HomeViewAPIRouter.saveuserpreferences(infoList: preferences)) { [weak self]
            error, status in
            
            guard let self = self else { return }
            
            if status == .FAILED {
                self.serverError = error
            }
            
            completion(status)
        }
    }
    
    func fetchAlertMessages(completion: @escaping (RESTResponse) -> Void) {
        messages.removeAll()
        messagesAll.removeAll()
        
        HomeStore().fetchAlertMessages(route: HomeViewAPIRouter.getAlertMessages) { [weak self]
             result, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = result {
                    self.messagesAll = list
                    
                    if self.messagesAll.count > 0, let firstMessage = self.messagesAll.first {
                        self.messages.append(firstMessage)
                    }
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func fetchUserSurveyInfo(completion:@escaping (String?, RESTResponse) -> Void) {
        HomeStore().fetchUserSurveyInfo(route: AuthenticationAPIRouter.getUserSITLookupInfo) { [weak self]
            sitUrl, error, status in
            
            guard let self = self else { return }
            
            if status == .FAILED {
                self.serverError = error
                completion(nil, status)
            } else {
                if let url = sitUrl {
                    completion(url, status)
                } else {
                    completion(nil, status)
                }
            }
        }
    }
    
}
    
