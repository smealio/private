//
//  Mail.swift
//  myctca
//
//  Created by Tomack, Barry on 1/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

class Mail : Codable {
    var mailId: String = ""
    var from: String = ""
    var to: String? = ""
    var selectedTo: [CareTeam] = []
    var subject: String = ""
    var comments: String = ""
    var sentDateTimeString: String = ""
    var parentMessageId: String? = ""
    var isRead: Bool = false
    var messageType: Int = 1
    var folderName: String = ""
    
    private(set) var _sentDate:Date?
    
    enum CodingKeys: String, CodingKey {
        case from, to, selectedTo, subject
        case comments, parentMessageId, isRead
        case messageType, folderName
        case sentDateTimeString = "sent"
        case mailId = "mailMessageId"
    }
    
//    override func encode(to encoder: Encoder) throws {
//        var container = encoder.container(keyedBy: CodingKeys.self)
//        try container.encode(mailId, forKey: .mailId)
//        try container.encode(from, forKey: .from)
//        try container.encode(to, forKey: .to)
//        try container.encode(selectedTo, forKey: .selectedTo)
//        try container.encode(subject, forKey: .subject)
//        try container.encode(comments, forKey: .comments)
//        try container.encode(parentMessageId, forKey: .parentMessageId)
//        try container.encode(isRead, forKey: .isRead)
//        try container.encode(messageType, forKey: .messageType)
//        try container.encode(folderName, forKey: .folderName)
//        try container.encode(sent, forKey: .sentDateTimeString)
//    }
//
    var sent: Date {
        get {
            if let date = _sentDate {
                return date
            } else {
                if sentDateTimeString.count > 0 {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                    dateFormatter.locale = Locale(identifier: "en_US_POSIX")
                    if let sentDate = dateFormatter.date(from: self.sentDateTimeString) {
                        self._sentDate = sentDate
                    } else {
                        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
                        if let sentDate = dateFormatter.date(from: self.sentDateTimeString) {
                            self._sentDate = sentDate
                        }
                    }
                }
                
                if let date = _sentDate {
                    return date
                } else {
                    return Date()
                }
            }
        }
    }

    func getMonthDaySentString() -> String {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd"
        let dateString: String = dateFormatter.string(from: sent)
        return dateString
    }
    
    func getFullDateTimeString() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd, YYYY @ HH:mm a"
        let dateString: String = dateFormatter.string(from: sent)
        return dateString
    }
    
    func getCommaSeparatedSelectedTo() -> String {
        var toWithCommas: String = ""
        
        for member in selectedTo {
            if (toWithCommas == "") {
                toWithCommas += "\(member.name)"
            } else {
                toWithCommas += ", \(member.name)"
            }
        }
        
        return toWithCommas
    }
    
    func getISO8601SentString() -> String {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyy-MM-dd'T'HH:mm:ss"
        let dateString: String = dateFormatter.string(from: sent)
        return dateString
    }
}
