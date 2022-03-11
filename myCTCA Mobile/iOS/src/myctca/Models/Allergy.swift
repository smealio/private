//
//  Allergy.swift
//  myctca
//
//  Created by Tomack, Barry on 3/7/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

class Allergy : Codable, Hashable {
    var _substance: String? = ""
    var _reactionSeverity: String? = ""
    var _status: String? = ""
    var Id:Int = 0
    
    var substance:String {
        get {
            if let val = _substance {
                return val
            } else {
                return ""
            }
        }
    }
    
    var reactionSeverity:String {
        get {
            if let val = _reactionSeverity {
                return val
            } else {
                return ""
            }
        }
    }
    
    var status:String {
        get {
            if let val = _status {
                return val
            } else {
                return ""
            }
        }
    }
    
    enum CodingKeys: String, CodingKey {
        case _substance = "substance"
        case _reactionSeverity = "reactionSeverity"
        case _status = "status"
    }
    
    public required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        self._substance = try container.decodeIfPresent(String.self, forKey: ._substance)
        self._reactionSeverity = try container.decodeIfPresent(String.self, forKey: ._reactionSeverity)
        self._status = try container.decodeIfPresent(String.self, forKey: ._status)
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(Id)
    }

    static func == (lhs: Allergy, rhs: Allergy) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
}
