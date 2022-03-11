//
//  MedDocNew.swift
//  myctca
//
//  Created by Manjunath K on 7/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class MedDocNew : Hashable, Codable {
    
    var docId: String = ""
    var docAuthoredDateString = ""
    var docAuthor: String = ""
    var docAuthorOccupationCode: String = ""
    var docName: String = ""
    var docText: String? = ""
    
    private var _docAuthoredDate: Date?
    var docAuthoredDate: Date {
        get {
            if let date = _docAuthoredDate {
                return date
            } else {
                if !docAuthoredDateString.isEmpty {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "yyyy-MM-dd"
                    dateFormatter.locale = Locale(identifier: "en_US_POSIX")

                    if let primaryDocumentAuthoredDate = dateFormatter.date(from: docAuthoredDateString) {
                        _docAuthoredDate = primaryDocumentAuthoredDate
                    } else {
                        _docAuthoredDate = Date()
                    }
                } else {
                    print("Med doc date not present")
                    _docAuthoredDate = Date()
                }
                return _docAuthoredDate!
            }
        }
    }
    
    func hash(into hasher: inout Hasher) {
           hasher.combine(docId)
       }

    static func ==(lhs: MedDocNew, rhs: MedDocNew) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
    
    func getSlashFormattedAuthoredDate() -> String {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yyyy"
        let dateString: String = dateFormatter.string(from: docAuthoredDate)
        return dateString
    }
    
    enum CodingKeys: String, CodingKey {
        case docId = "documentId",  docAuthor = "documentAuthor"
        case docName = "documentName", docText = "documentText"
        case docAuthoredDateString = "documentAuthoredDate"
        case docAuthorOccupationCode = "documentAuthorOccupationCode"
    }
}
