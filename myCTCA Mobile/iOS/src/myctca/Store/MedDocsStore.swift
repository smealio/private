//
//  MedDocsStore.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 14/07/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class MedDocsStore : BaseStore {

    func fetchClinicalSummaries(route:MedDocsAPIRouter, completion:@escaping ([ClinicalSummary]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [ClinicalSummary].self) {
            result, error in
            if let csList = result as? [ClinicalSummary] {
                completion(csList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchClinicalSummaryData(route:MedDocsAPIRouter, completion:@escaping (String?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            response, sError in
            
            if let data = response {
                do {
                    //create json object from data
                    if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: AnyObject] {
                        print("fetchClinicalSummaryData: \(json)")
                    
                        if let utf8Str: String = (json["content"] as? String), let html = self.fromBase64(str: utf8Str) {
                            completion(html, nil, .SUCCESS)
                        } else {
                            completion(nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                        }
                    }
                } catch let error {
                    print(error)
                }
            } else {
                completion("Failed to fetch details", ErrorManager.shared.getDefaultServerError(), .FAILED)
            }
        }
    }
    
    func fromBase64(str:String) -> String? {
        guard let data = Data(base64Encoded: str) else { return nil }
        return String(data: data, encoding: .utf8)
    }
    
    func transmitCSDoc(route:MedDocsAPIRouter, completion:@escaping (Bool, ServerException?, ServerError?, RESTResponse) -> Void)  {
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            
            data, sError in

            if let error = sError  {
                completion(false, nil, error, .FAILED)
                return
            }
            
            guard let data = data else {
                completion(false, nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                return
            }
            if let returnData = String(data: data, encoding: .utf8) {
                if (returnData == self.expectedServerResponse) {
                    completion(true, nil, nil, .SUCCESS)
                } else {
                    do {
                        let response = try JSONSerialization.jsonObject(with: data as Data, options: .allowFragments) as! [String: AnyObject]
                        for item in response {
                            let exceptions: ServerException = ServerException()
                            exceptions.exceptionFor = item.key
                            if let exception = try JSONSerialization.jsonObject(with: item.value as! Data, options: .allowFragments) as? ServerExceptionError {
                                exceptions.exception = exception
                            }
                            completion(false, exceptions, nil, .SUCCESS)
                        }
                    }
                    catch {
                        completion(false, nil, nil, .FAILED)
                    }
                }
            } else {
                completion(false, nil, nil, .FAILED)
            }
        }
    }
    
    func fetchCarePlanDetails(route:MedDocsAPIRouter, completion: @escaping(
                                String? , Bool?, ServerError?, RESTResponse) -> Void) {
        
        NetworkService.shared.executeRequestForGenerics(urlRequest: route) {
            data, sError in

            if let error = sError  {
                completion(nil, nil, error, .FAILED)
                return
            }

            guard let data = data else {
                completion(nil, nil, ErrorManager.shared.getDefaultServerError(), .FAILED)
                return
            }

            if let returnData = String(data: data, encoding: .utf8) {
                if (returnData == self.noCarePlansResponse) {
                    completion(nil, false, nil, .SUCCESS)
                } else {
                    do {
                        if let response = try JSONSerialization.jsonObject(with: data as Data, options: .allowFragments) as? [String:Any], let plan = response["documentText"] as? String  {
                            completion(plan , true, nil, .SUCCESS)
                        } else {
                            completion(nil, nil, nil, .FAILED)
                        }
                    }
                    catch {
                        completion(nil, nil, nil, .FAILED)
                    }
                }
            } else {
                completion(nil, nil, nil, .FAILED)
            }
        }
    }
    
    func fetchMedDocs(route:MedDocsAPIRouter, completion:@escaping ([MedDocNew]?, ServerError?, RESTResponse) -> Void) {

        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [MedDocNew].self) {
            result, error in
            if let list = result as? [MedDocNew] {
                completion(list, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchMedDoc(route:MedDocsAPIRouter, completion:@escaping (MedDocNew?, ServerError?, RESTResponse) -> Void) {

        NetworkService.shared.executeRequest(urlRequest: route, decodingType: MedDocNew.self) {
            result, error in
            if let doc = result as? MedDocNew {
                completion(doc, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchImagingDocs(route:MedDocsAPIRouter, completion:@escaping ([ImagingDocNew]?, ServerError?, RESTResponse) -> Void) {
        
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [ImagingDocNew].self) {
            result, error in
            if let list = result as? [ImagingDocNew] {
                completion(list, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
