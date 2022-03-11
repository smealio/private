//
//  ExternalLink.swift
//  myctca
//
//  Created by Manjunath K on 4/14/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class ExternalLinkItem: Codable {
    var title: String = ""
    var url: String = ""
    var isDownload: Bool = false

    enum CodingKeys: String, CodingKey {
        case title, url, isDownload
    }
}

class ExternalLink: Codable {
    var title: String = ""
    var url: String = ""
    var isDownload: Bool = false
    var items:[ExternalLinkItem]?
    
    enum CodingKeys: String, CodingKey {
        case title, url, isDownload
        case items = "children"
    }
}
