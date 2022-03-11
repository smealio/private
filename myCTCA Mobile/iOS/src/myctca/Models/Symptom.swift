//
//  Symptom.swift
//  myctca
//
//  Created by Manjunath K on 3/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class Symptom : Codable {
    var _performedDateString:String = ""
    var displayPerformedDate:String = ""
    var itemName:String = ""
    var observationValue:String? = ""
    var rangeValue:String? = ""
    var abnormalityCode:String = ""
    var formatedTextEncoded:String = ""
    var abnormalityCodeDescription:String = ""
    
    enum CodingKeys: String, CodingKey {
        case _performedDateString = "performedDate"
        case displayPerformedDate
        case itemName
        case observationValue
        case rangeValue
        case abnormalityCode
        case formatedTextEncoded
        case abnormalityCodeDescription
    }
    
    private(set) var _performedDate:Date?
    
    var performedDate: Date? {
        get {
            if let date = _performedDate {
                return date
            } else {
                if let date = DateConvertor.convertToDateFromString(dateString: _performedDateString, inputFormat: .usStandardForm1) {
                    _performedDate = date
                    return _performedDate
                } else {
                    return Date()
                }
            }
        }
    }
}
