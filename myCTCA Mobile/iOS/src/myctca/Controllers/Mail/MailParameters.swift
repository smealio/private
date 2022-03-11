//
//  MailParameters.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

struct MailParameters {
    let sessionParams = AppSessionParameters()

    let contentType: String = "application/x-www-form-urlencoded"
    let jsonContentType: String = "application/json"
    let accept: String = "application/json"
    
    var inboxURL: String {
        get {
            // https://v3devservice.myctca.com/api/v1/securemessages/getsecuremessages?mailFolder=Inbox
            return "\(sessionParams.portalDataServer)/securemessages/getsecuremessages?mailFolder=Inbox"
        }
    }
    
    var sentURL: String {
        get {
            return "\(sessionParams.portalDataServer)/securemessages/getsecuremessages?mailFolder=SentItems"
        }
    }
    
    var archiveURL: String {
        get {
            return "\(sessionParams.portalDataServer)/securemessages/getsecuremessages?mailFolder=DeletedItems"
        }
    }
    
    var careTeamURL: String {
        get {
            return "\(sessionParams.portalDataServer)/securemessages/getcareteams"
        }
    }
    /*
     Header for sending messages:
     string MailMessageId
     string From
     List<ShmRecipient> To
     List<string> SelectedTo
     string Subject
     string Comments
     DateTime Sent
     string ParentMessageId
     bool IsRead
     ShmMessageTypeEnum MessageType
     */
    var sendMailURL:String {
        // POST
        get {
            return "\(sessionParams.portalDataServer)/securemessages/sendsecuremessage"
        }
    }
    
    var archiveMailURL: String {
        get {
            return "\(sessionParams.portalDataServer)/securemessages/archivesecuremessage"
        }
    }
    
    var markAsReadURL: String {
        get {
            return "\(sessionParams.portalDataServer)/securemessages/setsecuremessageread"
        }
    }

    func urlForMailbox(_ mailBox: MailBox) -> String {
        if (mailBox == MailBox.inbox) {
            return inboxURL
        } else if (mailBox == MailBox.sent) {
            return sentURL
        } else if (mailBox == MailBox.archive) {
            return archiveURL
        }
        return ""
    }
}
