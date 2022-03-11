//
//  LabResult.swift
//  myctca
//
//  Created by Tomack, Barry on 1/2/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

class LabSetDetail : Codable, Hashable {
    var id: String = ""
    var performedDateTimeString: String = ""
    var itemName: String = ""
    var abnormalityCodeCalculated: String? = ""
    var abnormalityCodeDescription: String? = ""
    var result: String? = ""
    var normalRange: String? = ""
    var notes: String? = ""
    var displaySequence: Int = 0
    private(set) var _performedDate:Date?
    
    func hash(into hasher: inout Hasher) {
           hasher.combine(id)
       }

    static func ==(lhs: LabSetDetail, rhs: LabSetDetail) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
    
    var performedDate: Date? {
        get {
            if let date = _performedDate {
                return date
            } else {
                if let date = DateConvertor.convertToDateFromString(dateString: performedDateTimeString, inputFormat: .usStandardWithTimeForm1) {
                    _performedDate = date
                    return _performedDate
                } else {
                    return Date()
                }
            }
        }
    }
    
    enum CodingKeys: String, CodingKey {
        case id
        case performedDateTimeString = "performedDateTime"
        case itemName
        case abnormalityCodeCalculated
        case abnormalityCodeDescription
        case result
        case normalRange
        case notes
        case displaySequence
    }
}

class LabSet : Codable, Hashable {
    var orderId: String = ""
    var name: String = ""
    var details: [LabSetDetail]
    
    func hash(into hasher: inout Hasher) {
           hasher.combine(orderId)
       }

    static func ==(lhs: LabSet, rhs: LabSet) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
    
    enum CodingKeys: String, CodingKey {
        case orderId
        case name
        case details = "detail"
    }
}

class LabResult : Codable, Hashable {
    
    var performedDateString: String = ""
    var collectedBy: String = ""
    var labSets: [LabSet]
    private(set) var _performedDate:Date?
    
    func hash(into hasher: inout Hasher) {
           hasher.combine(_performedDate)
       }

    static func ==(lhs: LabResult, rhs: LabResult) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
    
    var performedDate: Date? {
        get {
            if let date = _performedDate {
                return date
            } else {
                if let date = DateConvertor.convertToDateFromString(dateString: performedDateString, inputFormat: .usStandardForm1) {
                    _performedDate = date
                    return _performedDate
                } else {
                    return Date()
                }
            }
        }
    }
    
    enum CodingKeys: String, CodingKey {
        case performedDateString = "performedDate"
        case collectedBy
        case labSets = "summary"
    }
    
    func getLabSetsNames(separatedBy: String) -> String {
        
        var setNames: String = ""
        for labSet in labSets {
            if setNames.isEmpty {
                setNames = labSet.name
            } else {
                setNames += "\(separatedBy)\(labSet.name)"
            }
        }
        return setNames
    }
    
    func getFormattedPerformedDateString() -> String {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEEE, MMMM dd, yyyy"
        let dateString: String = dateFormatter.string(from: performedDate!)
        return dateString
    }
    
    func getPerformedDateStringWithSlashes() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YY"
        let dateString: String = dateFormatter.string(from: performedDate!)
        return dateString
    }
    
    func isLessThan24HoursAgo() -> Bool {
        
        let yesterday: Date = Calendar.current.date(byAdding: .day, value: -1, to: Date())!
        if (performedDate! > yesterday) {
            return true;
        }
        return false
    }
}
