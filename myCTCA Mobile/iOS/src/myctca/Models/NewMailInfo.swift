//
//  NewMailInfo.swift
//  myctca
//
//  Created by Manjunath K on 10/7/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class NewMailInfo : Encodable {
    var from: String?
    var comments: String?
    var folderName: String?
    var messageType: String?
    var parentMessageId: String?
    var selectedTo: [CareTeam] = []
    var subject: String?
    var to: [CareTeam] = []
    var sent: String?
}

