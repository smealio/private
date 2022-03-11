//
//  TelehealthAccessToken.swift
//  myctca
//
//  Created by Manjunath K on 4/20/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class  TelehealthAccessTokenValue : Codable {
    var token:String = ""
    var expiresOn:String = ""
    
    enum CodingKeys: String, CodingKey {
        case token, expiresOn
    }
}

class TelehealthAccessToken : Codable {
    var value:TelehealthAccessTokenValue?
    
    enum CodingKeys: String, CodingKey {
        case value
    }
}
