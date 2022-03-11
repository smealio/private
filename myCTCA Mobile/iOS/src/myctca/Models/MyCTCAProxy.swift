//
//  MyCTCAProxy.swift
//  myctca
//
//  Created by Tomack, Barry on 2/22/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

struct MyCTCAProxy: Codable {
    var toCTCAUniqueId: String = ""
    var relationshipType: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var emailAddress: String?
    var expiresDateString: String?
    var permissions: [String]?
    var isImpersonating: Bool = false
    
    var fullName: String {
        get {
            return "\(firstName) \(lastName)"
        }
    }
    
    enum CodingKeys: String, CodingKey {
        case toCTCAUniqueId = "toCtcaUniqueId"
        case relationshipType, firstName, lastName, emailAddress, expiresDateString, permissions, isImpersonating
    }
    
    var expiresDate: Date {
        get {
            if let string = expiresDateString {
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
                dateFormatter.locale = Locale(identifier: "en_US_POSIX")
                return dateFormatter.date(from: string)!
            } else {
                return Date()
            }
        }
    }

}

