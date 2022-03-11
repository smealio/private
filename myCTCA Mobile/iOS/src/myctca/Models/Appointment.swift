//
//  Appointment.swift
//  myctca
//
//  Created by Tomack, Barry on 12/6/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

class Resource : Codable {
    var title:String = ""
    var resourceName:String? = ""
    
    enum CodingKeys: String, CodingKey {
        case title, resourceName
    }
}


class Appointment : Codable {
    var appointmentId:String = ""
    var description:String?
    var location:String?
    var startDateTime:String = ""
    var endDateTime:String = ""
    var resources:String? = ""
    var status:String = ""
    var patientInstructions:String = ""
    var schedulerNotes:String = ""
    var isTeleHealth:Bool = false
    var teleHealthUrl:String = ""
    var telehealthInfoUrl:String = ""
    var telehealthMeetingJoinUrl:String = ""
    var additionalInfo:String? = ""
    var requestDateTime = [String]()
    var meetingId:String = ""
    var resourceList = [Resource]()
    
    //TODO - Refactoring
    //fac info
    var name: String = ""
    var address1: String = ""
    var address2: String?
    var city: String = ""
    var state: String = ""
    var postalCode: String = ""
    var mainPhone: String = ""
    var schedulingPhone :String = ""
    var accommodationsPhone: String = ""
    var transportationPhone: String = ""
    var facilityTimeZone: String = ""
    
    private(set) var _startDate: Date?
    private(set) var _startDateInLocalTZ: Date?
    
    var startDate: Date? {
        get{
            if let date = _startDate {
                return date
            } else {
                if !startDateTime.isEmpty {
                    if let date = DateConvertor.convertToDateFromString(dateString: startDateTime, inputFormat: .usStandardWithTimeForm2) {
                        _startDate = date
                        return _startDate
                    }
                }
                return nil
            }
        }
    }
    
    var startDateInLocalTZ: Date? {
        get{
            if let date = _startDateInLocalTZ {
                return date
            } else {
                if let date = startDate {
                    _startDateInLocalTZ = DateConvertor.convertToLocal(from: facilityTimeZone, date: date)
                    return _startDateInLocalTZ
                }
                return nil
            }
        }
    }
        
    var isUpcoming: Bool {
        get {
            let stDate = getApptTimeInLocalWithExtraTime()
            if (stDate > Date()) {
                return true
            }
            return false
        }
    }
    
    var facility: Facility {
        get{
            if let fac = _facility {
                return fac
            } else {
                _facility = Facility(appointmentData: self)
                return _facility!
            }
        }
    }
    
    private(set) var _facility: Facility?

    enum CodingKeys: String, CodingKey {
        case appointmentId = "appointmentId"
        case description = "description"
        case location = "location"
        case startDateTime = "startDateTime"
        case endDateTime = "endDateTime"
        case resources = "resources"
        case status = "status"
        case patientInstructions = "patientInstructions"
        case schedulerNotes = "schedulerNotes"
        case isTeleHealth = "isTeleHealth"
        case telehealthInfoUrl = "telehealthInfoUrl"
        case teleHealthUrl = "teleHealthUrl"
        case name = "facilityName"
        case address1 = "facilityAddress1"
        case address2 = "facilityAddress2"
        case city = "facilityCity"
        case state = "facilityState"
        case postalCode = "facilityPostalCode"
        case mainPhone = "facilityMainPhone"
        case schedulingPhone = "facilitySchedulingPhone"
        case accommodationsPhone = "facilityAccommodationsPhone"
        case transportationPhone = "facilityTransportationPhone"
        case telehealthMeetingJoinUrl = "telehealthMeetingJoinUrl"
        case facilityTimeZone, additionalInfo, resourceList
    }
    
    
    func getFormattedStartTime() -> String {
        if let date = startDate {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "h:mm a"
            dateFormatter.amSymbol = "AM"
            dateFormatter.pmSymbol = "PM"
            let timeString: String = dateFormatter.string(from: date)
            return timeString
        }
        return ""
    }
    
    func getFormattedStartDate() -> String {
        if let date = startDate {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "EEEE, MMMM dd, yyyy"
            let dateString: String = dateFormatter.string(from: date)
            return dateString
        }
        return ""
    }
    //This one required because appts needs to be sorted as past and upcoming with 30mins extra time
    //#12812 - Azure
    func getApptTimeInLocalWithExtraTime() -> Date {
        
        guard let time = startDateInLocalTZ?.addingTimeInterval(30 * 60) else {
            return Date()
        }
        return time
    }
    
    func getResourceNames() -> String {
        if resourceList.count == 0 {
            return ""
        }
        
        let count = resourceList.count
        var resourceNames = ""
        for index in 0...count-1 {
            let item = resourceList[index]
            let title = item.title.trimmingCharacters(in: .whitespacesAndNewlines)
            if !title.isEmpty {
                resourceNames += title
            }
            if let resourceName = item.resourceName?.trimmingCharacters(in: .whitespacesAndNewlines) {
                if !resourceName.isEmpty {
                    resourceNames += resourceName
                }
                
                if index != count-1 {
                    resourceNames += "\n"
                }
            }
        }
        
        if resourceNames.isEmpty {
            resourceNames = "Not Specified"
        }
        return resourceNames
    }
}

class AppointmentDateTimes: Encodable {
    var date:Date = Date()
    var timePreference:ApptTimePref = .NONE
    
    enum CodingKeys: String, CodingKey {
        case date, timePreference
    }
    
    init(date:Date, timePreference:ApptTimePref) {
        self.date = date
        self.timePreference = timePreference
    }
}

class ApptReqest: Encodable {
    var appointmentId:String = ""
    var appointmentDate:Date = Date()
    var from:String = ""
    var subject:String = ""
    var phoneNumber:String = ""
    var Email:String = ""
    var communicationPreference = CommunicationPref.NONE
    var reason:String = ""
    var additionalNotes:String = ""
    var appointmentDateTimes = [AppointmentDateTimes]()
    
    enum CodingKeys: String, CodingKey {
        case appointmentId, appointmentDate, from
        case subject, phoneNumber, Email
        case reason, additionalNotes, communicationPreference
        case appointmentDateTimes
    }
}
