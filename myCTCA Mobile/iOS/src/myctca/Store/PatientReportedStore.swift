//
//  PatientReportedStore.swift
//  myctca
//
//  Created by Manjunath K on 3/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class PatientReportedStore: BaseStore {
    
    func fetchSymptomInventory(route:MoreAPIRouter, completion:@escaping ([Symptom]?, ServerError?,  RESTResponse)  -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [Symptom].self) {
            result, error in
            
            if let symptomList = result as? [Symptom] {
                completion(symptomList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
