//
//  Immunization.swift
//  myctca
//
//  Created by Tomack, Barry on 3/7/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class Immunization: Codable, Hashable {

    var immunizationName: String = ""
    var vaccineName: String = ""
    var performedDate: String = ""
    var performedBy: String = ""
    var Id:Int = 0
    
    enum CodingKeys: String, CodingKey {
        case immunizationName, vaccineName, performedDate, performedBy
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(Id)
    }

    static func == (lhs: Immunization, rhs: Immunization) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
}
