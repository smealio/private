//
//  HealthHistoryStore.swift
//  myctca
//
//  Created by Manjunath K on 1/6/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class HealthHistoryStore : BaseStore {
    func fetchVitals(route:HealthHistoryAPIRouter, completion:@escaping ([VitalsInfo]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [VitalsInfo].self) {
            result, error in
            
            if let vitalsList = result as? [VitalsInfo] {
                completion(vitalsList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchAllergies(route:HealthHistoryAPIRouter, completion:@escaping ([Allergy]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [Allergy].self) {
            result, error in

            if let allergiesList = result as? [Allergy] {
                completion(allergiesList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchHealthIssues(route:HealthHistoryAPIRouter, completion:@escaping ([HealthIssue]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [HealthIssue].self) {
            result, error in

            if let allergiesList = result as? [HealthIssue] {
                completion(allergiesList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchImmunizations(route:HealthHistoryAPIRouter, completion:@escaping ([Immunization]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [Immunization].self) {
            result, error in

            if let allergiesList = result as? [Immunization] {
                completion(allergiesList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchPrescriptions(route:HealthHistoryAPIRouter, completion:@escaping ([Prescription]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [Prescription].self) {
            result, error in

            if let allergiesList = result as? [Prescription] {
                completion(allergiesList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func sendPrescriptionRenewalRequest(route: HealthHistoryAPIRouter, completion: @escaping(ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let res = response, let returnData = String(data: res, encoding: .utf8) {
                
                print("sendPrescriptionRenewalRequest return data: \(returnData)")
                if (returnData == self.expectedServerResponse) {
                    completion( nil, .SUCCESS)
                } else {
                    completion(ErrorManager.shared.getDefaultServerError(), .FAILED)
                }
            } else {
                completion(sError, .FAILED)
            }
        }
    }
}
