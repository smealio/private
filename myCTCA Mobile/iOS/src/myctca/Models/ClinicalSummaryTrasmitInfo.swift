//
//  ClinicalSummaryTrasmitInfo.swift
//  myctca
//
//  Created by Manjunath K on 8/17/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct ClinicalSummaryTrasmitInfo : Codable {
    var directAddress = ""
    var documentId = [String]()
    var filePass = ""
    
    init() {
    }
}
