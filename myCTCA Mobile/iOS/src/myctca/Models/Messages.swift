//
//  Messages.swift
//  myctca
//
//  Created by Manjunath K on 9/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class Messages : Codable {
    var id = ""
    var messageText = ""
    
    enum CodingKeys: String, CodingKey {
        case id
        case messageText
    }
}
