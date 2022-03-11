//
//  CTCAURLSession.swift
//  myctca
//
//  Created by Tomack, Barry on 11/27/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

// Helps for Unit Testing (also dependency injection, should one so choose)
protocol CTCAURLSession {
    func dataTask(with request: URLRequest, completionHandler: @escaping (Data?, URLResponse?, Error?) -> Swift.Void) -> URLSessionDataTask
    
    func downloadTask(with request: URLRequest, completionHandler: @escaping (URL?, URLResponse?, Error?) -> Void) -> URLSessionDownloadTask
}
