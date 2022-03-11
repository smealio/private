//
//  Prescription.swift
//  myctca
//
//  Created by Tomack, Barry on 2/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

class Prescription : Hashable, Codable {

    var prescriptionId: String = ""
    var drugName: String = ""
    var instructions: String = ""
    var startDate: String = ""
    var expireDate: String = ""
    var prescriptionType: String = ""
    var statusType: String = ""
    var _comments: String? = ""
    var allowRenewal: Bool = true
        
    let CTCA_PRESCRIBED: String = "CTCA-Prescribed"
    let SELF_REPORTED: String = "Self-Reported"
    let NO_START_DATE: String = "Not provided"
    let NO_EXPIRED_DATE: String = "Not provided"
    let NO_INSTRUCTIONS: String = "No specific intructions provided."

    enum CodingKeys: String, CodingKey {
        case prescriptionId, drugName, instructions
        case prescriptionType, statusType, allowRenewal
        case expireDate, startDate
        case _comments = "comments"
    }
    
    var comments:String {
        get {
            if let val = _comments {
                return val
            } else {
                return ""
            }
        }
    }
    
    func hash(into hasher: inout Hasher) {
           hasher.combine(prescriptionId)
       }

    static func == (lhs: Prescription, rhs: Prescription) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
    
    var subText: String {
        get {
            var subStr: String = ""
            
            if (statusType != "") {
                subStr = "\(statusType)"
            }
            if (prescriptionType != "RX" && statusType != "") {
                subStr += ", \(prescriptionType)"
            } else {
                subStr += "\(prescriptionType)"
            }
            
            return subStr
        }
    }
    
    var Id:Int {
        get {
            if let val = Int(prescriptionId) {
                return val
            }
            return 0
        }
    }
}
