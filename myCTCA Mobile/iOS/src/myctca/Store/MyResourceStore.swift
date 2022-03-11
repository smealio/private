//
//  MyResourceStore.swift
//  myctca
//
//  Created by Manjunath K on 4/14/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class MyResourceStore: BaseStore {
    
    func fetchExternalLinks(route:MoreAPIRouter, completion:@escaping ([ExternalLink]?, ServerError?,  RESTResponse)  -> Void) {
        NetworkService.shared.executeRequest(urlRequest: route, decodingType: [ExternalLink].self) {
            result, error in
            
            if let linkList = result as? [ExternalLink] {
                completion(linkList, nil, .SUCCESS)
            } else {
                completion(nil, error, . FAILED)
            }
        }
    }
}
