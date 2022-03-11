//
//  MailManager.swift
//  myctca
//
//  Created by Manjunath K on 12/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class MailManager : BaseManager {

    var newMails = [Mail]()
    var sentMails = [Mail]()
    var archivedMails = [Mail]()
    var careTeams = [CareTeam]()
    
    var requestForType:MailBox?
    static let shared = MailManager()
        
    func fetchMails(ofType:MailBox, completion: @escaping(RESTResponse) -> Void) {
        var router = MailAPIRouter.none
        
        switch ofType {
        case .inbox:
            router = MailAPIRouter.getNewMail
        case .sent:
            router = MailAPIRouter.getSentMail
        case .archive:
            router = MailAPIRouter.getArchivedMail
        }
        
        requestForType = ofType

        MailStore().fetchMails(route: router) { [weak self]
             response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = response {
                    switch self.requestForType! {
                    case .inbox:
                        self.newMails = list
                    case .sent:
                        self.sentMails = list
                    case .archive:
                        self.archivedMails = list
                    }
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func fetchCareTeams(completion: @escaping(RESTResponse) -> Void) {
        MailStore().fetchCareTeams(route: MailAPIRouter.getCareTeam) { [weak self]
             response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = response {
                    self.careTeams = list
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func setMailRead(msgID:String, completion: @escaping(RESTResponse) -> Void) {
        MailStore().setMailRead(route: MailAPIRouter.setMailRead(value: msgID)) { [weak self]
             error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                completion(.SUCCESS)
            }
        }
    }
    
    func archiveMail(msgID:String, completion: @escaping(RESTResponse) -> Void) {
        MailStore().archiveMail(route: MailAPIRouter.archiveMail(value: msgID)) { [weak self]
             error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                completion(.SUCCESS)
            }
        }
    }
    
    func sendNewMail(newMailInfo:NewMailInfo, completion: @escaping(RESTResponse) -> Void) {
        MailStore().sendNewMail(route: MailAPIRouter.sendMail(value: newMailInfo)) { [weak self]
             error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                completion(.SUCCESS)
            }
        }
    }
}
