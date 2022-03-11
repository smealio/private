//
//  ActivityLogStore.swift
//  myctca
//
//  Created by Manjunath K on 2/9/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class ActivityLogStore {
    func fetchActivityLogs(route:MoreAPIRouter, completion:@escaping([ActivityLog]?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: ActivityLogResponse.self) {
            result, error in
            if let acResponse = result as? ActivityLogResponse {
                completion(acResponse.data, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
