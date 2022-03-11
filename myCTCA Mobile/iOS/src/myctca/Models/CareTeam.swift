//
//  CareTeam.swift
//  myctca
//
//  Created by Tomack, Barry on 1/15/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

struct CareTeam: Codable {
    
    var systemId: String = ""
    var name: String = ""
    var userName: String? = ""
    var environmentId: String? = ""
    
    enum CodingKeys: String, CodingKey {
        case systemId, name
        case userName, environmentId
    }
}
