//
//  AppointmentsStore.swift
//  myctca
//
//  Created by Manjunath K on 11/26/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class AppointmentsStore : BaseStore {

    func fetchAppointments(route:AppointmentsAPIRouter, completion:@escaping ([Appointment]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [Appointment].self) {
            result, error in
            if let apptList = result as? [Appointment] {
                completion(apptList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func requestOrChangeAppointment(route:AppointmentsAPIRouter, completion:@escaping (String?, ServerError?, RESTResponse)  -> Void) {
        
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let data = response {
                do {
                    //create json object from data
                    if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: AnyObject] {
                        print("requestAppointment: \(json)")
                        completion("success", nil, .SUCCESS)
                    }
                } catch let error {
                    print(error)
                }
            } else {
                completion("failed", sError, .FAILED)
            }
        }
    }
    
    func fetchTelehealthAccessToken(route:AppointmentsAPIRouter, completion: @escaping(TelehealthAccessToken?, ServerError?,RESTResponse) -> Void) {
        NetworkService.shared.executeNonCTCARequest(urlRequest: route, decodingType: TelehealthAccessToken.self) {
            result, error in
            if let token = result as? TelehealthAccessToken {
                completion(token, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
