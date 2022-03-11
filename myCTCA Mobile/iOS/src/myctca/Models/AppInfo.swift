//
//  AppInfo.swift
//  myctca
//
//  Created by Manjunath K on 2/3/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class AppVersions : Codable {
    var id:String = ""
    var platform:String = ""
    var versionNumber:String = ""
    var mandatory:Bool = true
    
    enum CodingKeys: String, CodingKey {
        case id, platform
        case versionNumber, mandatory
    }
}

class AppInfo : Codable {
    var techSupportNumber:String = ""
    var applicationVersions:[AppVersions]
    
    enum CodingKeys: String, CodingKey {
        case techSupportNumber, applicationVersions
    }
}
