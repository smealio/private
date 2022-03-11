//
//  LabsManagerTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 1/12/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class LabsManagerTests: BaseTests {
    
    let labsManager = LabsManager()

    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_fetchLabResults() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "labresults", for: LabsAPIRouter.getLabReports.urlRequest!, forBundle: Self.self)

        labsManager.fetchLabResults() {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchLabResults() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: LabsAPIRouter.getLabReports.urlRequest!, forBundle: Self.self)

        labsManager.fetchLabResults() {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchLabResults() {
        let ex = expectation(description: "Expecting server error")
        setError(for: LabsAPIRouter.getLabReports.urlRequest!)

        labsManager.fetchLabResults() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.labsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_downloadLabReport() {
        
        guard let theData = dataFromTestBundleFile(fileName: "labresults",
                                                   withExtension: "json", forBundle: Self.self) else {
            return
        }
        do {
            let labResultList = try JSONDecoder().decode([LabResult].self, from: theData)
//
//            var params = [String:String]()
//            params["performeddate"] = "2019-11-06"
//            params["collectedby"] = "CTCA"
//
//            let url =  LabsAPIRouter.downloadLabReports(valueDict: params).urlRequest!
//            setMockFilePDF(responseFile: "unit-test", for:url, forBundle: Self.self)
//
//
            //This needs to be done like this, becuase the order of the params in url can be anything
            let url1 = URL(string: "https://v3testservice.myctca.com/api/v1/labresults/getlabresultsreport?collectedby=CTCA&performeddate=2019-11-06")
            
            let url2 = URL(string: "https://v3testservice.myctca.com/api/v1/labresults/getlabresultsreport?performeddate=2019-11-06&collectedby=CTCA")
            
            let ex = expectation(description: "Expecting valid pdf file")
            
            guard let mockResponse = dataFromTestBundleFile(fileName: "unit-test",
                                                            withExtension: "pdf", forBundle: Self.self) else {
                return
            }
                                                            
            CTCAMockURLProtocol.testURLs = [url1 : mockResponse, url2 : mockResponse]
            
            labsManager.downloadLabReport(report: labResultList[0]) {
                path, status  in
                ex.fulfill()
                
                XCTAssertTrue(status == .SUCCESS)
                XCTAssertNotNil(path)
            }
            waitForExpectations(timeout: 2.0, handler: nil)
            
        } catch let error {
            print("test_success_downloadLabReport : \(error)")
            XCTFail()
        }
    }
    
    func test_Fail_downloadLabReport() {
        guard let theData = dataFromTestBundleFile(fileName: "labresults",
                                                   withExtension: "json", forBundle: Self.self) else {
            return
        }
        do {
            let labResultList = try JSONDecoder().decode([LabResult].self, from: theData)
            
            var params = [String:String]()
            params["performeddate"] = "2019-11-06"
            params["collectedby"] = "CTCA"
            
            let ex = expectation(description: "Expecting valid pdf file")
            let url =  LabsAPIRouter.downloadLabReports(valueDict: params).urlRequest!
            
            setNilData(urlRequest: url)

            print("Testing = \(url)")
            labsManager.downloadLabReport(report: labResultList[0]) {
                path, status  in
                ex.fulfill()
                
                XCTAssertTrue(status == .FAILED)
                XCTAssertNil(path)
            }
            waitForExpectations(timeout: 2.0, handler: nil)
            
        } catch let error {
            print("test_Fail_downloadLabReport : \(error)")
            XCTFail()
        }
        
    }
    
    func test_ServerError_downloadLabReport()  {
        let ex = expectation(description: "Expecting server error")
        
        guard let theData = dataFromTestBundleFile(fileName: "labresults",
                                                   withExtension: "json", forBundle: Self.self) else {
            return
        }
        do {
            let labResultList = try JSONDecoder().decode([LabResult].self, from: theData)
            
            let url1 = URL(string: "https://v3testservice.myctca.com/api/v1/labresults/getlabresultsreport?collectedby=CTCA&performeddate=2019-11-06")
            
            let url2 = URL(string: "https://v3testservice.myctca.com/api/v1/labresults/getlabresultsreport?performeddate=2019-11-06&collectedby=CTCA")
            
            setError(for:URLRequest(url: url1!))
            setError(for:URLRequest(url: url2!))

            labsManager.downloadLabReport(report: labResultList[0]) {
                path, status  in

                XCTAssertTrue(status == .FAILED)
                XCTAssert(self.labsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
                
                ex.fulfill()
            }
            waitForExpectations(timeout: 2.0, handler: nil)
        
        } catch let error {
            print("test_ServerError_downloadLabReport : \(error)")
            XCTFail()
        }
    }
}

