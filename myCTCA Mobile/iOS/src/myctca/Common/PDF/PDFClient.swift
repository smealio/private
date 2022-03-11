//
//  PDFClient.swift
//  myctca
//
//  Created by Manjunath K on 8/26/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class PDFClient : MyCTCASessionClient {
    func downloadPDF(withEndPoint:String, toPath: URL,
                     completion: @escaping (Bool, URL?, ServerError?) -> Void) {
        
        guard let urlString = withEndPoint.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) else {  fatalError()  }
        
        guard let url = URL(string: urlString) else { return }
        
        var request: URLRequest = URLRequest(url: url)
        print("downloadPDF : \(String(describing: request.url?.absoluteString))")
        request.httpMethod = "GET"
        request.addValue(AppSessionManager.shared.getBearerAccessToken(), forHTTPHeaderField: "Authorization")
        
        let task = session.downloadTask(with: request) { localURL, urlResponse, error in
            
            if error == nil {
                if let httpResponse = urlResponse as? HTTPURLResponse, !(200 ... 299 ~= httpResponse.statusCode), 302 != httpResponse.statusCode {
                    completion(false, nil, ErrorManager.shared.getDefaultServerError())
                } else {
                    
                    if let localURL = localURL {
                        if let string = try? String(contentsOf: localURL) {
                            print("downloadPDF: file downloaded at : \(string)")
                        }
                        
                        do {
                            try FileManager.default.copyItem(at: localURL, to: toPath)
                            completion(true, localURL, nil)
                            
                        } catch (let fileWriteError) {
                            print("downloadPDF: Error while wrting to file \(toPath) : \(fileWriteError)")
                            completion(false, nil, ErrorManager.shared.getDefaultServerError())
                        }
                    } else {
                        AnalyticsManager.shared.logException(CTCAAnalyticsConstants.EXCEPTION_REST_API, url.absoluteString, "\(PARSE_ERROR_CODE)")
                        completion(false, nil, ErrorManager.shared.getDefaultServerError())
                    }
                }
            } else {
                completion(false, nil, ServerError(serverError: error!))
            }
        }
        task.resume()
    }
}
