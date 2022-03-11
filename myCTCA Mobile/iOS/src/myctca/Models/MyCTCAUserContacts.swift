//
//  MyCTCAUserContacts.swift
//  myctca
//
//  Created by Manjunath K on 8/12/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct MyCTCAUserPhoneNumbers: Codable {
    var systemId:String
    var phone:String
    var phoneType:String
    var primary:Bool
    
    enum CodingKeys: String, CodingKey {
        case systemId, phone
        case phoneType, primary
    }
}

class MyCTCAUserContacts: Codable {
    var contactId = ""
    var firstName = ""
    var lastName = ""
    var dateOfBirthString:String?
    var primaryFacility = ""
    var emailAddress = ""
    var epiId = ""
    var phoneNumbers:[MyCTCAUserPhoneNumbers]?

    enum CodingKeys: String, CodingKey {
        case contactId, firstName, lastName
        case primaryFacility, emailAddress, epiId
        case dateOfBirthString = "dateOfBirth"
        case phoneNumbers
    }
    
    private var _dateOfBirth:Date?
    var dateOfBirth:Date? {
        get {
            if let date = _dateOfBirth {
                return date
            } else {
                if let dob = dateOfBirthString, !dob.isEmpty {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
                    dateFormatter.locale = Locale(identifier: "en_US_POSIX")

                    if let date = dateFormatter.date(from: dob) {
                        _dateOfBirth = date
                    } else {
                        _dateOfBirth = Date()
                    }
                } else {
                    print("_dateOfBirth date not present")
                    _dateOfBirth = Date()
                }
                return _dateOfBirth!
            }
        }
    }
}
