//
//  PDFDownloadStore.swift
//  myctca
//
//  Created by Manjunath K on 1/8/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class PDFDownloadStore : BaseStore {
    func downloadReports(route: BaseAPIRouter, filename:String, completion:@escaping (String?, ServerError?, RESTResponse) -> Void) {
        NetworkService.shared.executeDownlodRequest(urlRequest: route, fileName: filename) {
            downloadedFilePath, sError, status in
            completion(downloadedFilePath, sError, status)
        }
    }
    
    func downloadReports(urlPath: String, filename:String, completion:@escaping (String?, ServerError?, RESTResponse) -> Void) {
        let url = URL(string: AppSessionParameters().portalDataServer)
        
        var urlRequest = URLRequest(url: url!.appendingPathComponent(urlPath))
        
        // HTTP Method
        urlRequest.httpMethod = HTTPMethod.get.rawValue
        
        // Common Headers
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.acceptType.rawValue)
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
        urlRequest.setValue(AppSessionManager.shared.getBearerToken(), forHTTPHeaderField: HTTPHeaderField.authentication.rawValue)
        urlRequest.httpBody = nil
        
        NetworkService.shared.executeDownlodRequest(url: urlRequest, fileName: filename) {
            downloadedFilePath, sError, status in
            completion(downloadedFilePath, sError, status)
        }
    }
}
