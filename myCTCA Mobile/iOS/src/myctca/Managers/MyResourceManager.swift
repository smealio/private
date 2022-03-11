//
//  MyResourceManager.swift
//  myctca
//
//  Created by Manjunath K on 4/14/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class MyResourceManager: BaseManager {
    
    static let shared = MyResourceManager()
    var externalLinksList:[ExternalLink]?
    
    func fetchExternalLinks(completion: @escaping (RESTResponse) -> Void) {
        MyResourceStore().fetchExternalLinks(route: MoreAPIRouter.getexternallinks) { [weak self]
             result, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = result {
                    self.externalLinksList = list
                }
                completion(.SUCCESS)
            }
        }
    }
}
