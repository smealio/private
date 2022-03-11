//
//  HealthHistoryManager.swift
//  myctca
//
//  Created by Manjunath K on 1/6/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class HealthHistoryManager : BaseManager {
    
    var vitalsInfoList = [VitalsInfo]()
    var vitalsDocsOriginal = [Date:[Vitals]]()
    var vitalsDocs =  [Date:[Vitals]]()
    var vitalsDates = [Date]()
    var vitalsDatesOriginal = [Date]()
    
    var allergies: [Allergy]?
    var allergiesOriginal: [Allergy]?
    
    var immunizations: [Immunization]?
    var immunizationsOriginal: [Immunization]?
    
    var healthIssues: [HealthIssue]?
    var healthIssuesOriginal: [HealthIssue]?
    
    var prescriptions: [Prescription]?
    var prescriptionsOriginal: [Prescription]?
    
    let healthHistoryStore = HealthHistoryStore()
    
    func fetchHelathHistory(healthHistoryType: HealthHistoryType, completion: @escaping(RESTResponse) -> Void) {
        switch healthHistoryType {
        case .vitals:
            healthHistoryStore.fetchVitals(route: HealthHistoryAPIRouter.getVitals) { [weak self]
                response, error, status  in
                
                guard let self = self else { return }
                
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(.FAILED)
                } else {
                    if let list = response {
                        self.vitalsInfoList = list
                        self.reArrangeVitals()
                    }
                    completion(.SUCCESS)
                }
            }
        case .allergies:
            healthHistoryStore.fetchAllergies(route: HealthHistoryAPIRouter.getAllergies) { [weak self]
                response, error, status  in
                
                guard let self = self else { return }
                
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(.FAILED)
                } else {
                    if let list = response, list.count > 0  {
                        
                        for i in 0...list.count-1 {
                            list[i].Id = i+1
                        }
                        self.allergiesOriginal = list
                        self.allergies = list
                    }
                    completion(.SUCCESS)
                }
            }
        case .healthIssues:
            healthHistoryStore.fetchHealthIssues(route: HealthHistoryAPIRouter.getHealthIssues) { [weak self]
                response, error, status  in
                
                guard let self = self else { return }
                
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(.FAILED)
                } else {
                    if let list = response, list.count > 0  {
                        
                        for i in 0...list.count-1 {
                            list[i].Id = i+1
                        }
                        
                        self.healthIssuesOriginal = list
                        self.healthIssues = list
                    }
                    completion(.SUCCESS)
                }
            }
        case .immunizations:
            healthHistoryStore.fetchImmunizations(route: HealthHistoryAPIRouter.getImmunizations) { [weak self]
                response, error, status  in
                
                guard let self = self else { return }
                
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(.FAILED)
                } else {
                    if let list = response, list.count > 0 {
                        
                        for i in 0...list.count-1 {
                            list[i].Id = i+1
                        }
                        
                        self.immunizationsOriginal = list
                        self.immunizations = list
                    }
                    completion(.SUCCESS)
                }
            }
        default:
            break
        }
    }
    
    func fetchPrescriptions(completion: @escaping(RESTResponse) -> Void) {
        healthHistoryStore.fetchPrescriptions(route: HealthHistoryAPIRouter.getPrescriptions) { [weak self]
            response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = response {
                    self.prescriptions = Array(list).sorted {  $0.Id > $1.Id }
                    self.prescriptionsOriginal = self.prescriptions
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func sendPrescriptionRenewalRequest(request: PrescriptionRefillRequest, completion: @escaping(RESTResponse) -> Void) {
        healthHistoryStore.sendPrescriptionRenewalRequest(route: HealthHistoryAPIRouter.sendPrescriptions(request: request)) { [weak self]
            error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                completion(.SUCCESS)
            }
        }
    }
    
    func downloadHealthHistory(healthHistoryType: HealthHistoryType, completion: @escaping(RESTResponse) -> Void) {
        
        var router = HealthHistoryAPIRouter.none
        downloadFileName = ""
        
        switch healthHistoryType {
        case .vitals:
            downloadFileName = MyCTCAConstants.FileNameConstants.VitalsPDFName
            router = HealthHistoryAPIRouter.downloadVitals
        case .prescriptions:
            downloadFileName = MyCTCAConstants.FileNameConstants.PrescriptionsPDFName
            router = HealthHistoryAPIRouter.downloadPrescriptions
        case .allergies:
            downloadFileName = MyCTCAConstants.FileNameConstants.AllergiesPDFName
            router = HealthHistoryAPIRouter.downloadAllergies
        case .immunizations:
            downloadFileName = MyCTCAConstants.FileNameConstants.ImmunizationPDFName
            router = HealthHistoryAPIRouter.downloadImmunizations
        case .healthIssues:
            downloadFileName = MyCTCAConstants.FileNameConstants.HealthIssuesPDFName
            router = HealthHistoryAPIRouter.downloadHealthIssues
        }
        
        downloadReports(router: router, url: nil) {
            status in
            completion(status)
        }
    }
    
    func reArrangeVitals() {
        let list = vitalsInfoList
        for item in list {
            if let date = DateConvertor.convertToDateFromString(dateString: item.enteredDateString, inputFormat: .usStandardWithTimeForm2) {
                let count = item.details.count
                for i in 0...count-1 {
                    item.details[i].enteredDate = date
                }
                vitalsDocsOriginal[date] = item.details
                vitalsDatesOriginal.append(date)
            }
        }
        
        vitalsDocs = vitalsDocsOriginal
        vitalsDates = vitalsDatesOriginal
    }
    
    //MARK - Filter funcs
    
    func filterVitalsList(filterText: String) {
        var vitalsDictOriginal = vitalsDocsOriginal
        let vitalsList = Array(vitalsDictOriginal.values).joined()
        let filterVitalsList = vitalsList.filter{$0.observationItem!.localizedCaseInsensitiveContains(filterText)}
        
        //Reorganize by date
        vitalsDictOriginal.removeAll()
        for item in filterVitalsList {
            if vitalsDictOriginal[item.enteredDate] != nil {
                vitalsDictOriginal[item.enteredDate]!.append(item)
            } else {
                vitalsDictOriginal[item.enteredDate] = [item]
            }
        }
        
        self.vitalsDocs = vitalsDictOriginal
        self.vitalsDates = Array(vitalsDictOriginal.keys).sorted()
    }
    
    func filterAllergiesList(filterText: String) {
        if let list = self.allergiesOriginal {
            var filterSubstance = list.filter{$0.substance.localizedCaseInsensitiveContains(filterText)}
            let filterReaction = list.filter{$0.reactionSeverity.localizedCaseInsensitiveContains(filterText)}
            let filterStatus = list.filter{$0.status.localizedCaseInsensitiveContains(filterText)}

            filterSubstance.append(contentsOf: filterReaction)
            filterSubstance.append(contentsOf: filterStatus)
            
            let objectSet = Set(filterSubstance.map { $0 })
            self.allergies = Array(objectSet).sorted {  $0.Id < $1.Id }
        }
    }
    
    func filterImmunizationsList(filterText: String) {
        if let list = self.immunizationsOriginal {
            var filterImmune = list.filter{$0.immunizationName.localizedCaseInsensitiveContains(filterText)}
            let filterVaccine = list.filter{$0.vaccineName.localizedCaseInsensitiveContains(filterText)}
            let filterPerformedBy = list.filter{$0.performedBy.localizedCaseInsensitiveContains(filterText)}

            filterImmune.append(contentsOf: filterVaccine)
            filterImmune.append(contentsOf: filterPerformedBy)
            
            let objectSet = Set(filterImmune.map { $0 })
            self.immunizations = Array(objectSet).sorted {  $0.Id < $1.Id }
        }
    }
    
    func filterHelathIssuesList(filterText: String) {
        if let list = self.healthIssuesOriginal {
            var filterShortName = list.filter{$0.shortName.localizedCaseInsensitiveContains(filterText)}
            let filterName = list.filter{$0.name.localizedCaseInsensitiveContains(filterText)}
            let filterStatus = list.filter{$0.status.localizedCaseInsensitiveContains(filterText)}

            filterShortName.append(contentsOf: filterName)
            filterShortName.append(contentsOf: filterStatus)
            
            let objectSet = Set(filterShortName.map { $0 })
            self.healthIssues = Array(objectSet).sorted {  $0.Id < $1.Id }
        }
    }
    
    func filterPrescriptionsList(filterText: String) {
        if let list = self.prescriptionsOriginal {
            var filterMedication = list.filter{$0.drugName.localizedCaseInsensitiveContains(filterText)}
            let filterStatus = list.filter{$0.statusType.localizedCaseInsensitiveContains(filterText)}
            let filterType = list.filter{$0.prescriptionType.localizedCaseInsensitiveContains(filterText)}

            filterMedication.append(contentsOf: filterStatus)
            filterMedication.append(contentsOf: filterType)
            
            let objectSet = Set(filterMedication.map { $0 })
            self.prescriptions = Array(objectSet).sorted {  $0.Id > $1.Id }
        }
    }
    
}
