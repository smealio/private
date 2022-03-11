//
//  ImagingDocNew.swift
//  myctca
//
//  Created by Manjunath K on 7/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class ImagingDocNew : Hashable, Codable {
    
    var id: String = ""
    var itemName: String = ""
    var documentDateString: String = ""
    var notes: String = ""
    
    func hash(into hasher: inout Hasher) {
           hasher.combine(id)
       }

    static func ==(lhs: ImagingDocNew, rhs: ImagingDocNew) -> Bool {
        return ObjectIdentifier(lhs) == ObjectIdentifier(rhs)
    }
    
    private var _documentDate: Date?
    var documentDate: Date {
        get {
            if let date = _documentDate {
                return date
            } else {
                if !documentDateString.isEmpty {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "yyyy-MM-dd"
                    dateFormatter.locale = Locale(identifier: "en_US_POSIX")

                    if let primaryDocumentAuthoredDate = dateFormatter.date(from: documentDateString) {
                        _documentDate = primaryDocumentAuthoredDate
                    } else {
                        _documentDate = Date()
                    }
                } else {
                    print("Imaging doc date not present")
                    _documentDate = Date()
                }
                return _documentDate!
            }
        }
    }
    
    enum CodingKeys: String, CodingKey {
        case id, notes, itemName
        case documentDateString = "documentDate"
    }

    func getSlashFormattedDocDate() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yyyy"
        let dateString: String = dateFormatter.string(from: documentDate)
        return dateString
    }
}
