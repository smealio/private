//
//  ActivityLog.swift
//  myctca
//
//  Created by Manjunath K on 2/9/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class ActivityLogResponse: Codable {
    var data: [ActivityLog]

    enum CodingKeys: String, CodingKey {
        case data
    }
}


class ActivityLog: Codable {
    var userName: String = ""
    var formattedMessage: String = ""
    private var _timeStampString: String?
    private var _timeStamp:Date?
    
    enum CodingKeys: String, CodingKey {
        case userName, formattedMessage
        case _timeStampString = "formattedTimestamp"
    }
    
    var timeStr: String {
        get {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "h:mm aa"
            dateFormatter.locale = Locale(identifier: "en_US_POSIX")
            dateFormatter.timeZone = .current
            let time = dateFormatter.string(from: self.timeStamp)
            return time
        }
    }
    
    var timeStamp:Date {
        get {
            if let date = _timeStamp {
                return date
            } else {
//                if let tStamp = _timeStampString {
//                    let formatter = DateFormatter()
//                    formatter.calendar = Calendar(identifier: .iso8601)
//                    formatter.locale = Locale(identifier: "en_US_POSIX")
//                    formatter.timeZone = .current//TimeZone(secondsFromGMT: 0)
//                    //formatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
//                    formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"
//
//                    if let date = formatter.date(from: tStamp) {
//                        _timeStamp = date
//                    } else {
//                        _timeStamp = Date()
//                    }
//                    return _timeStamp!
//                } else {
//                    _timeStamp = Date()
//                    return _timeStamp!
//                }
                //"formattedTimestamp"
                if let tStamp = _timeStampString {
                    let formatter = DateFormatter()
                    formatter.calendar = Calendar(identifier: .iso8601)
                    //formatter.locale = Locale(identifier: "en_US_POSIX")
                    formatter.timeZone = .current //TimeZone(secondsFromGMT: 0)
                    formatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSSXXXXX"
                    //formatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSS"
        
                    if let date = formatter.date(from: tStamp) {
                        self._timeStamp = date
                    } else {
                        self._timeStamp = Date()
                    }
                    return _timeStamp!
                } else {
                    _timeStamp = Date()
                    return _timeStamp!
                }
            }
        }
    }
}
