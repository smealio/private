//
//  AppInfoStore.swift
//  myctca
//
//  Created by Manjunath K on 2/3/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class AppInfoStore {

    func fetchAppInfo(route:AppInfoAPIRouter, completion:@escaping (AppInfo?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: AppInfo.self) {
            result, error in
            if let appInfo = result as? AppInfo {
                completion(appInfo, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
