//
//  CarePlan.swift
//  myctca
//
//  Created by Manjunath K on 7/27/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class CarePlan: Codable {
    
    var systemId: String = ""
    var primaryDocumentAuthoredDateStr: String = ""
    var primaryDocumentAuthor: String = ""
    var primaryDocumentAuthorOccupationCode = ""
    var documentName: String = ""
    var documentStatus: String = ""
    var documentText: String = ""
        
    private var _primaryDocumentAuthoredDate: Date?
    var primaryDocumentAuthoredDate: Date {
        get {
            if let date = _primaryDocumentAuthoredDate {
                return date
            } else {
                if !primaryDocumentAuthoredDateStr.isEmpty {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                    dateFormatter.locale = Locale(identifier: "en_US_POSIX")

                    if let primaryDocumentAuthoredDate = dateFormatter.date(from: primaryDocumentAuthoredDateStr) {
                        _primaryDocumentAuthoredDate = primaryDocumentAuthoredDate
                    } else {
                        _primaryDocumentAuthoredDate = Date()
                    }
                } else {
                    print("CSD data not present")
                    _primaryDocumentAuthoredDate = Date()
                }
                return _primaryDocumentAuthoredDate!
            }
        }
    }

    enum CodingKeys: String, CodingKey {
        case systemId//, patientId, visitId
        case primaryDocumentAuthoredDateStr = "primaryDocumentAuthoredDate"
        case primaryDocumentAuthorOccupationCode, documentName
        case documentStatus, documentText
        case primaryDocumentAuthor
    }
}

