//
//  CTCAMockURLProtocol.swift
//  myctcaTests
//
//  Created by Manjunath K on 12/18/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class CTCAMockURLProtocol: URLProtocol {
    // this dictionary maps URLs to test data
    static var testURLs = [URL?: Data]()
    
    // say we want to handle all types of request
    override class func canInit(with request: URLRequest) -> Bool {
        return true
    }
    
    // ignore this method; just send back what we were given
    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }
    
    override func startLoading() {
        // if we have a valid URL…
        if let url = request.url {
            print("Looking for the stub  \(url.absoluteString)")
            // …and if we have test data for that URL…
            if let data = CTCAMockURLProtocol.testURLs[url] {
                //...and if data is a error string, throw error
                if let errorCode = String(data: data, encoding: .utf8), errorCode == "\(DEFAULT_ERROR_CODE)" {
                    //throw error
                    let error = NSError(domain: "", code:400 , userInfo: nil)
                    self.client?.urlProtocol(self, didFailWithError: error)
                } else {
                    // …else, load it immediately.
                    self.client?.urlProtocol(self, didLoad: data)
                }
            } else {
                let error = NSError(domain: "", code:345 , userInfo: nil)
                self.client?.urlProtocol(self, didFailWithError: error)
            }
        }
        
        // mark that we've finished
        self.client?.urlProtocolDidFinishLoading(self)
    }
    
    // this method is required but doesn't need to do anything
    override func stopLoading() {
    }
}
