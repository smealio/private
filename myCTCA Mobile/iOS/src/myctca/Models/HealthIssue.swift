//
//  HealthIssue.swift
//  myctca
//
//  Created by Tomack, Barry on 3/8/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class HealthIssue: Codable, Hashable {
    
    var shortName: String = ""
    var name: String = ""
    var status: String = ""
    var enteredDateStr: String = ""
    var enteredDate: Date?
    var Id:Int = 0
    
    static var IdCounter:Int = 0
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(shortName)
    }
    
    static func == (lhs: HealthIssue, rhs: HealthIssue) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
    
    enum CodingKeys: String, CodingKey {
        case shortName, name, status
        case enteredDateStr = "enteredDate"
    }
    
    func getFormattedSlashedEnteredDate() -> String {
        
        var dateString = ""
        
        if let date = DateConvertor.convertToDateFromString(dateString: enteredDateStr, inputFormat: .usStandardForm1) {
            dateString = DateConvertor.convertToStringFromDate(date: date, outputFormat: .usStandardForm2)
        }
        return dateString
    }
}
