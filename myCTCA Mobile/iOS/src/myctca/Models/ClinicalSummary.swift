//
//  ClinicalSummary.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 14/07/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class ClinicalSummary: Codable {
    var csId: String = ""
    var creationTimeString = ""
    var csTitle: String = ""
    var csAuthor: String = ""
    var csFacility: String = ""
    
    private var _creationTime: Date?
    var creationTime: Date {
        get {
            if let date = _creationTime {
                return date
            } else {
                if !creationTimeString.isEmpty {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                    dateFormatter.locale = Locale(identifier: "en_US_POSIX")

                    if let creationTime = dateFormatter.date(from: creationTimeString) {
                        _creationTime = creationTime
                    } else {
                        _creationTime = Date()
                    }
                } else {
                    print("CSD data not present")
                    _creationTime = Date()
                }
                return _creationTime!
            }
        }
    }
    
    enum CodingKeys: String, CodingKey {
        case csId = "uniqueId"
        case creationTimeString = "creationTime"
        case csTitle = "title"
        case csAuthor = "authors"
        case csFacility = "facilityName"
    }

    func getSlashFormattedCreationDate() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yy"
        let dateString: String = dateFormatter.string(from: creationTime)
        return dateString
    }
}
