//
//  PatientReportedManager.swift
//  myctca
//
//  Created by Manjunath K on 3/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class PatientReportedManager: BaseManager {
    
    static let shared = PatientReportedManager()

    var symptomsList = [Symptom]()
    var symptomsDocs =  [Date:[Symptom]]()
    var symptomsDates = [Date]()
    
    func fetchSymptomInventory(completion: @escaping (RESTResponse) -> Void) {
        PatientReportedStore().fetchSymptomInventory(route: MoreAPIRouter.getSymptomInventory) { [weak self]
             result, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = result {
                    self.symptomsList = list
                    self.reArrangeSymptomInventory()
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func reArrangeSymptomInventory() {
        let list = symptomsList
        for item in list {
            if let date = item.performedDate {
                if symptomsDates.contains(date) {
                    symptomsDocs[date]?.append(item)
                } else {
                    symptomsDocs[date] = [item]
                    symptomsDates.append(date)
                }
            }
        }
    }
}
