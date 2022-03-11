//
//  UserPreferences.swift
//  myctca
//
//  Created by Manjunath K on 9/10/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct UserPreference: Codable {
    var userId:Int
    var userPreferenceType:String
    var userPreferenceValueString:String
    
    var userPreferenceValue:Bool {
        return userPreferenceValueString == "True" ? true : false
    }
    
    enum CodingKeys: String, CodingKey {
        case userId, userPreferenceType
        case userPreferenceValueString = "userPreferenceValue"
    }
    
    init() {
        userId = 0
        userPreferenceType = ""
        userPreferenceValueString = ""
    }
    
    func getPayloadVariant() -> UserPreferencePayload {
        var payloadVariant = UserPreferencePayload()
        payloadVariant.userId = self.userId
        payloadVariant.userPreferenceType = self.userPreferenceType
        if (self.userPreferenceValueString != "") {
            payloadVariant.userPreferenceValue = self.userPreferenceValueString
        } else {
            payloadVariant.userPreferenceValue = self.userPreferenceValue ? "True" : "False"
        }
        return payloadVariant
    }
}

struct UserPreferencePayload : Encodable {
    var userId = 0
    var userPreferenceType = ""
    var userPreferenceValue = ""
}
