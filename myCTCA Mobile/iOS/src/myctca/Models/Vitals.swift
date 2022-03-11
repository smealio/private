//
//  Vitals.swift
//  myctca
//
//  Created by Tomack, Barry on 2/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

class VitalsInfo: Codable {
    var enteredDateString: String = ""
    var details = [Vitals]()
    
    enum CodingKeys: String, CodingKey {
        case details
        case enteredDateString = "enteredDate"
    }
}

class Vitals: Codable {
    var observationItem: String? = ""
    var value: String = ""
    var displaySequence: Int? = 0

    var enteredDate = Date()

    enum CodingKeys: String, CodingKey {
        case observationItem, value, displaySequence
    }
}
