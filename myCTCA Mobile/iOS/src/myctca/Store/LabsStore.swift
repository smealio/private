//
//  LabsStore.swift
//  myctca
//
//  Created by Manjunath K on 12/10/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class LabsStore: BaseStore {

    func fetchLabResuts(route:LabsAPIRouter, completion:@escaping ([LabResult]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [LabResult].self) {
            result, error in
            
            if let labResultList = result as? [LabResult] {
                completion(labResultList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
    
    func downloadLabReport(route:LabsAPIRouter, filename:String, completion:@escaping (String?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeDownlodRequest(urlRequest: route, fileName: filename) {
            downloadedFilePath, sError, status in
            completion(downloadedFilePath, sError, status)
        }
    }
}
