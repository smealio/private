//
//  BaseTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 12/18/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import XCTest
@testable import myctca

class BaseTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
    }
    
    override func tearDown() {
        super.tearDown()
    }
    
    func dataFromTestBundleFile<T:BaseTests>(fileName: String, withExtension fileExtension: String, forBundle:T.Type) -> Data? {
        let testBundle = Bundle(for: T.self)
        if let resourceUrl = testBundle.url(forResource: fileName, withExtension: fileExtension) {
            do {
                let data = try Data(contentsOf: resourceUrl)
                return data
            } catch {
                XCTFail("Error reading data from resource file \(fileName).\(fileExtension)")
                return nil
            }
        }
        
        return nil
    }
    
    func setMockFile<T:BaseTests>(responseFile: String, for urlRequest:URLRequest, forBundle:T.Type) {
        guard let mockResponse = dataFromTestBundleFile(fileName: responseFile,
                                                        withExtension: "json", forBundle: T.self) else {
            return
        }
                                                        
        CTCAMockURLProtocol.testURLs = [urlRequest.url! : mockResponse]
    }
    
    func setMockFilePDF<T:BaseTests>(responseFile: String, for urlRequest:URLRequest, forBundle:T.Type) {
        guard let mockResponse = dataFromTestBundleFile(fileName: responseFile,
                                                        withExtension: "pdf", forBundle: T.self) else {
            return
        }
                                                        
        print("Mock set for \(urlRequest.url!)")
        CTCAMockURLProtocol.testURLs = [urlRequest.url! : mockResponse]
    }
    
    func setNilData(urlRequest:URLRequest) {
        CTCAMockURLProtocol.testURLs = [urlRequest.url! : Data()]
    }
    
    func setMockData(withString:String, for urlRequest:URLRequest) {
        print("Mock data \"\(withString)\" set for \(urlRequest.url!)")
        
        let mockdata = Data(withString.utf8)
        
        CTCAMockURLProtocol.testURLs = [urlRequest.url! : mockdata]
    }
    
    func setError(for urlRequest:URLRequest) {
        print("Mock error for \(urlRequest.url!)")
        setMockData(withString: "\(DEFAULT_ERROR_CODE)", for: urlRequest)
    }
}
    
