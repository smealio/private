//
//  DateConvertor.swift
//  myctca
//
//  Created by Manjunath K on 10/20/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

extension DateFormatter {
    static let iso8601Full: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = DateFormat.iso8601Full.rawValue
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter
    }()
    
    static let yyyyMMdd: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = DateFormat.usStandardForm1.rawValue
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter
    }()
}

extension Date {
    func get(_ components: Calendar.Component..., calendar: Calendar = Calendar.current) -> DateComponents {
        return calendar.dateComponents(Set(components), from: self)
    }

    func get(_ component: Calendar.Component, calendar: Calendar = Calendar.current) -> Int {
        return calendar.component(component, from: self)
    }
}

enum DateFormat:String {
    case baseForm = "MMM d, yyyy"
    case dayAndBaseForm = "EEE, MMM d, yyyy"
    case appointmentsForm = "EEE, MMM dd, yyyy, h:mm a"
    case activityLogsForm = "YYYY-MM-DD HH:mm:ss.SSS"
    case usStandardWithTimeForm1 = "YYYY-MM-DDTHH:mm:ss"
    case usStandardForm1 = "yyyy-MM-dd"
    case usStandardForm2 = "MM/dd/yyyy"
    case usStandardWithTimeForm2 = "yyyy-MM-dd'T'HH:mm:ss"
    case justTimeForm = "hh:mm a"
    case fullMonthForm = "MMMM dd, yyyy"
    case onlyWeeekDayForm = "EEEE"
    case none = ""
    case iso8601Full = "yyyy-MM-ddTHH:mm:ss.SSSXXXXX"
}

class DateConvertor {
    static func convertToStringFromDate(date:Date?, outputFormat:DateFormat) -> String {
        if let inputDate = date, outputFormat.rawValue != "" && outputFormat.rawValue != "" {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = outputFormat.rawValue
            dateFormatter.timeZone = .current
            let outDateStr = dateFormatter.string(from: inputDate)
            return outDateStr
        }
        return ""
    }
    
    static func convertToDateFromString(dateString:String, inputFormat:DateFormat) -> Date? {
        if inputFormat.rawValue != "" && dateString != "" {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = inputFormat.rawValue
            dateFormatter.timeZone = .current
            if let serviceDate = dateFormatter.date(from: dateString) {
                return serviceDate
            } else {
                return nil
            }
        }
        return nil
    }
    
    static func convert(from initTimeZone: TimeZone, to targetTimeZone: TimeZone, date:Date) -> Date {
        let delta = TimeInterval(targetTimeZone.secondsFromGMT(for: date) - initTimeZone.secondsFromGMT(for: date))
        return date.addingTimeInterval(delta)
    }
    
    static func convertToLocal(from initTimeZoneAbbr: String, date:Date) -> Date {
        
        if let fromTimeZone = TimeZone(abbreviation: initTimeZoneAbbr) {
            let delta = TimeInterval(TimeZone.current.secondsFromGMT(for: date) - fromTimeZone.secondsFromGMT(for: date))
            return date.addingTimeInterval(delta)
        }
        
        return date
    }
        
    func convertToLocalTime(fromTimeZone timeZoneAbbreviation: String, date:Date) -> Date? {
        if let timeZone = TimeZone(abbreviation: timeZoneAbbreviation) {
            let targetOffset = TimeInterval(timeZone.secondsFromGMT(for: date))
            let localOffeset = TimeInterval(TimeZone.autoupdatingCurrent.secondsFromGMT(for: date))

            return date.addingTimeInterval(targetOffset - localOffeset)
        }

        return nil
    }
    
    func getCalederDayForDate(date:Date) -> String {
        let components = date.get(.day, .month, .year)
        //if let day = components.day, let month = components.month, let year = components.year {
        if let day = components.day {
            //print("day: \(day), month: \(month), year: \(year)")
            return "\(day)"
        }
        return ""
    }
}
