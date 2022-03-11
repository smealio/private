//
//  WarningMessageManager.swift
//  myctca
//
//  Created by Manjunath K on 2/26/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class WarningMessageManager : BaseManager {
    
    let userDefaultsWarningMesageKey = "WARNING_MESSAGE_PROCESSED"
    
    var messageList = [WarningMessage]()
    
    func fetchWarningMessges() {
        messageList = getMessages()
    }
    
    func checkForAnyWarningMessages() -> WarningMessage? {
        let nowDateValue = Date()
        
        for msg in messageList {
            if (nowDateValue >= msg.startDateTime) && (nowDateValue <= msg.endDateTime) {
                // date is in range
                return msg
            }
        }
        
        return nil
    }
    
    func getMessages() -> [WarningMessage] {
        // CST time in UTC format
        let april8 = "2021-04-08T05:00:01.000-00:00"
        let april10 = "2021-04-10T13:00:01.000-00:00"
        let april10_end = "2021-04-10T21:00:00.000-00:00"
        
        let formatter = DateFormatter()
        
        formatter.timeZone = TimeZone(abbreviation: "UTC")
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"
            
        let message1 = WarningMessage()
        let message2 = WarningMessage()
    
        message1.blockApp = false
        message1.startDateTime = formatter.date(from: april8)!
        message1.endDateTime = formatter.date(from: april10)!
        message1.message = "The myCTCA app will be undergoing planned maintenance Saturday morning, April 10th and will not be available for up to 8 hours. We apologize for the inconvenience."
        message1.title = "Planned Maintenance"

        message2.blockApp = true
        message2.startDateTime = formatter.date(from: april10)!
        message2.endDateTime = formatter.date(from: april10_end)!
        message2.message = "The myCTCA app is currently undergoing planned maintenance. Please try again later. We apologize for the inconvenience."
        message2.title = "Down for Maintenance"
        
        print(message1.startDateTime)
        print(message1.endDateTime)
        print(message2.endDateTime)
        
        return [message1, message2]
    }
}
