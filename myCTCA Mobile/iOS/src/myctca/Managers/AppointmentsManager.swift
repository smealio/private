//
//  AppointmentsManager.swift
//  myctca
//
//  Created by Manjunath K on 11/26/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class AppointmentsManager : BaseManager {

    var appointments:[Appointment] = [Appointment]()
    
    var nextAppt = [String: [Appointment]]()
    var nextSections = [String]()
    
    var upcomingAppt = [String: [Appointment]]()
    var upcomingSections = [String]()
    
    var pastAppt = [String: [Appointment]]()
    var pastSections = [String]()
    
    var requestAppointment = ApptReqest()
    var requestType = ApptRequestType.new
    
    static let shared = AppointmentsManager()
        
    func fetchAppointments(completion: @escaping(RESTResponse) -> Void) {

        AppointmentsStore().fetchAppointments(route: AppointmentsAPIRouter.getAppts) { [weak self]
             response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = response, list.count > 0 {
                    self.appointments = list
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func buildUpcomingAndPastData() {
        
        if self.appointments.count > 0 {
            self.upcomingSections.removeAll()
            self.upcomingAppt.removeAll()
            self.pastSections.removeAll()
            self.pastAppt.removeAll()
            self.nextSections.removeAll()
            self.nextAppt.removeAll()
            
            let now = Date().timeIntervalSince1970
            let allUpcomingAppt = appointments.filter() { ($0.getApptTimeInLocalWithExtraTime().timeIntervalSince1970) >= now }
            var allPastAppt = appointments.filter() { ($0.getApptTimeInLocalWithExtraTime().timeIntervalSince1970) < now }
            
            if (allUpcomingAppt.count > 0) {
                for appointment in allUpcomingAppt {
                    
                    guard let startDate = appointment.startDate else {
                        continue
                    }

                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "EEEE, MMMM dd"
                    let apptDate: String = dateFormatter.string(from: startDate)
                    
                    if nextSections.count == 0 {
                        if isNextappt(appointment: appointment) {
                            self.nextSections.append(apptDate)
                            self.nextAppt[apptDate] = [Appointment]()
                            self.nextAppt[apptDate]!.append(appointment)
                            continue
                        }
                    } else if (self.nextSections.contains(apptDate)) {
                        self.nextAppt[apptDate]!.append(appointment)
                        continue
                    }
                    
                    if (!self.upcomingSections.contains(apptDate)) {
                        // Add current Date to sections array
                        self.upcomingSections.append(apptDate)
                        self.upcomingAppt[apptDate] = [Appointment]()
                    }
  
                    self.upcomingAppt[apptDate]!.append(appointment)
                }
            }
            if (allPastAppt.count > 0) {
                // Reverse sort past appointments
                allPastAppt = allPastAppt.sorted { $0.startDate! > $1.startDate! }
                for appointment in allPastAppt {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "EEEE, MMMM dd"
                    
                    guard let startDate = appointment.startDate else {
                        continue
                    }
                    
                    let apptDate: String = dateFormatter.string(from: startDate)
                    if (!self.pastSections.contains(apptDate)) {
                        self.pastSections.append(apptDate)
                        self.pastAppt[apptDate] = [Appointment]()
                    }

                    self.pastAppt[apptDate]!.append(appointment)
                }
            }
        } else {
            nextAppt.removeAll()
            nextSections.removeAll()
            upcomingAppt.removeAll()
            upcomingSections.removeAll()
            pastAppt.removeAll()
            pastSections.removeAll()
        }
    }
    
    func isNextappt(appointment:Appointment) -> Bool {
        if let startDateInLocalTZ = appointment.startDateInLocalTZ {
            
            let currentTime = Date()
            let diffComponents = Calendar.current.dateComponents([.day], from: currentTime, to: startDateInLocalTZ)
            
            if let days = diffComponents.day, days < 7 {
                return true
            }
        }
        return false
    }

    func requestOrChangeAppointment(requestType:ApptRequestType, params:[String:String], completion: @escaping(RESTResponse) -> Void) {

        var request = AppointmentsAPIRouter.none
        
        switch(requestType) {
        case .new:
            request = AppointmentsAPIRouter.apptRequest(valueDict: params)
        case .cancel:
            request = AppointmentsAPIRouter.cancelAppt(valueDict: params)
        case .reschedule:
            request = AppointmentsAPIRouter.rescheduleAppt(valueDict: params)
        }
        
        AppointmentsStore().requestOrChangeAppointment(route: request) { [weak self]
             response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(status)
            } else {
                completion(status)
            }
        }
    }
    
    func downloadAppointments(params:[String:String], completion: @escaping(RESTResponse) -> Void) {
        downloadFileName = MyCTCAConstants.FileNameConstants.ApptReportsPDFName
        downloadReports(router: AppointmentsAPIRouter.downloadAppts(valueDict: params), url: nil) {
            status in
            completion(status)
        }
    }
    
    func fetchTelehealthAccessToken(completion: @escaping(String?, RESTResponse) -> Void) {
        AppointmentsStore().fetchTelehealthAccessToken(route: AppointmentsAPIRouter.getTelehealthAccessToken) { [weak self]
             token, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(nil, .FAILED)
            } else {
                if let tokenString = token?.value?.token {
                    completion(tokenString, .SUCCESS)
                } else {
                    completion(nil, .FAILED)
                }
            }
        }
    }
    
    func submitAppointmentRequest(completion: @escaping(RESTResponse) -> Void) {
        var request = AppointmentsAPIRouter.none

        switch(requestType) {
        case .new:
            request = AppointmentsAPIRouter.apptNewRequestV2(model: requestAppointment)
        case .cancel:
            request = AppointmentsAPIRouter.apptCancelRequestV2(model: requestAppointment)
        case .reschedule:
            request = AppointmentsAPIRouter.apptRescheduleRequestV2(model: requestAppointment)
        }
        
        AppointmentsStore().requestOrChangeAppointment(route: request) {
             response, error, status  in
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(status)
            } else {
                completion(status)
            }
        }
    }
    
    func prepareAppointmentRequestFor(requestType: ApptRequestType, appointment:Appointment?) {
        self.requestType = requestType
        requestAppointment = ApptReqest()
        if let name = AppSessionManager.shared.currentUser.userProfile?.fullName {
            requestAppointment.from = name
        }
        requestAppointment.phoneNumber = AppSessionManager.shared.currentUser.getUsersPreferedContactnumber()
        requestAppointment.Email = AppSessionManager.shared.currentUser.getUsersPreferedEmailId()
                
        switch requestType {
        case .reschedule:
            AppointmentsManager.shared.requestAppointment.subject = AppointmentMsgConstants.rescheduleSubject
            if let id = appointment?.appointmentId {
                AppointmentsManager.shared.requestAppointment.appointmentId = id
            }
        case .cancel:
            AppointmentsManager.shared.requestAppointment.subject = AppointmentMsgConstants.cancelSubject
            if let id = appointment?.appointmentId {
                AppointmentsManager.shared.requestAppointment.appointmentId = id
            }
        case .new:
            AppointmentsManager.shared.requestAppointment.subject = AppointmentMsgConstants.newSubject
        }
    }
    
    func getAppointmentDetailsForId(appointmentId:String) -> Appointment? {
        for item in appointments { //can search only for upcoming appointments
            if appointmentId == item.appointmentId {
                return item
            }
        }
        return nil
    }
    
    func downloadAppointment(id:String, completion: @escaping(RESTResponse) -> Void) {
        downloadFileName = MyCTCAConstants.FileNameConstants.SingleApptReportsPDFName
        downloadReports(router: AppointmentsAPIRouter.downloadApptByID(valueDict: ["appointmentId" : id]), url: nil, pdfDocType: .singlAppointment) {
            status in
            completion(status)
        }
    }
    
    func canRescheduleAppt(startDateInLocalTZ:Date?) -> Bool {
        if let startDate = startDateInLocalTZ {
            let currentTime = Date()
            let diffComponents = Calendar.current.dateComponents([.hour], from: currentTime, to: startDate)
            if let hr = diffComponents.hour, hr >= 24 {
                return true
            }
        }
        return false
    }
}
