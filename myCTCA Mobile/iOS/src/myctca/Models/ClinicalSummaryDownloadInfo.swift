//
//  ClinicalSummaryDownloadInfo.swift
//  myctca
//
//  Created by Manjunath K on 8/24/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct ClinicalSummaryDownloadInfo : Codable {
    var documentId = [String]()
    var filePass = ""
    
    init(list:[String], password:String) {
        documentId = list
        filePass = password
    }
}

