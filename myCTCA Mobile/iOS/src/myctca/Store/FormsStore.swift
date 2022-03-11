//
//  FormsStore.swift
//  myctca
//
//  Created by Manjunath K on 7/21/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class FormsStore : BaseStore {

    func checkForExistingANNC(route:FormsAPIRouter, completion:@escaping (Bool?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: Bool.self) {
            result, error in
            if let retVal = result as? Bool {
                completion(retVal, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func fetchMRNNumber(route:FormsAPIRouter, completion:@escaping (String?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: String.self) {
            result, error in
            if let retVal = result as? String {
                completion(retVal, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func submitForm(route: FormsAPIRouter, completion:@escaping (Bool, ServerException?, ServerError?, RESTResponse) -> Void)  {
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
    
    func fetchROIFormInfo(route:FormsAPIRouter, completion:@escaping (ROIFormInfo?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: ROIFormInfo.self) {
            result, error in
            if let info = result as? ROIFormInfo {
                completion(info, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
