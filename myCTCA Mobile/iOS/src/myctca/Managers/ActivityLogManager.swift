//
//  ActivityLogManager.swift
//  myctca
//
//  Created by Manjunath K on 2/9/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class ActivityLogManager: BaseManager {
    
    var filterDate = ""
    var filterMessage = ""
    var filterUsername = ""
    var applyFilter = false
    let activityLogPageSize = 20
    
    let activityLogStore = ActivityLogStore()
    
    func fetchActivityLogs(page:Int,
                           completion: @escaping([Date:[ActivityLog]]?, RESTResponse) -> Void) {
        var filters = ""
        if applyFilter {
            filters = prepareFilterString()
        }
        
        let skip = page * activityLogPageSize
        let payload = ["take":String(activityLogPageSize), "skip":String(skip), "filter":filters]
        
        activityLogStore.fetchActivityLogs(route: MoreAPIRouter.getActivityLogs(valueDict: payload)) { [weak self]
            list, error, status in
            
            guard let self = self else { return }
            
            if status == .FAILED {
                self.serverError = error
                completion(nil, status)
            } else {
                if let acList = list {
                    var dict = [Date:[ActivityLog]]()
                    for item in acList {
                        var cal = Calendar(identifier: .gregorian)
                        cal.timeZone = .current
                        let dateKey = cal.startOfDay(for: item.timeStamp)
                        
                        if dict[dateKey] != nil {
                            dict[dateKey]?.append(item)
                        } else {
                            dict[dateKey] = [item]
                        }
                    }
                    
                    completion(dict, status)
                } else {
                    completion([Date:[ActivityLog]](), .FAILED)
                }
            }
        }
    }
    
     func prepareFilterString() -> String {
         var filter = [String]()
         var returnString = ""
         var dateFilter = ""
         var messageFilter = ""
         var usernameFilter = ""

         if filterDate != "" {
             
             let formatter = DateFormatter()
             formatter.calendar = Calendar(identifier: .iso8601)
             formatter.locale = Locale(identifier: "en_US_POSIX")
             formatter.timeZone = .current
             formatter.dateFormat = "EEE, MMM dd, yyyy"
             
             if let date = formatter.date(from: filterDate)
             {
                 guard var dateNew = Calendar.current.date(from: Calendar.current.dateComponents([.year, .month, .day], from: date)) else {
                     fatalError("Failed to strip time from Date object")
                 }
                 
                 //create from and to dates
                 let dateFormatter = DateFormatter()
                 //dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"
                 dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
                 let fromDate = dateFormatter.string(from: dateNew)
                 
                 dateNew.addTimeInterval(60*60*24)
                 let toDate = dateFormatter.string(from: dateNew)
                 
                 filter.append("[\"timestamp\"")
                 filter.append("\">=\"")
                 filter.append("\"\(fromDate)\"]")
                 filter.append("\"and\"")
                 filter.append("[\"timestamp\"")
                 filter.append("\"<\"")
                 filter.append("\"\(toDate)\"]")
                 
                 if filter.count > 0 {
                     dateFilter = "[" + filter.map { String($0) }.joined(separator: ",") + "]"
                 }
             }
         }

         if filterUsername.count > 0 {
             filter.removeAll()
             filter.append("[\"userName\"")
             filter.append("\"contains\"")
             filter.append("\"\(filterUsername)\"]")
             if filter.count > 0 {
                  usernameFilter = filter.map { String($0) }.joined(separator: ",")
             }
         }
         
         if filterMessage.count > 0 {
             filter.removeAll()
             filter.append("[\"formattedMessage\"")
             filter.append("\"contains\"")
             filter.append("\"\(filterMessage)\"]")
             if filter.count > 0 {
                  messageFilter = filter.map { String($0) }.joined(separator: ",")
             }
         }

         returnString = ""
         
         var filterArray = [String]()
         
         if dateFilter.count > 0 {
             filterArray.append(dateFilter)
         }
         
         if usernameFilter.count > 0 {
             if filterArray.count > 0 {
                 filterArray.append("\"and\"")
             }
             filterArray.append(usernameFilter)
         }
         
         if messageFilter.count > 0 {
             if filterArray.count > 0 {
                 filterArray.append("\"and\"")
               }
             filterArray.append(messageFilter)
         }
         
         if filterArray.count > 0 {
             returnString = "[" + filterArray.map { String($0) }.joined(separator: ", ") + "]"
         }
         
         return returnString
     }

}
