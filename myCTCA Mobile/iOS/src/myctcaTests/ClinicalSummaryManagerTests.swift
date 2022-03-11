//
//  ClinicalSummaryTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 7/23/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class ClinicalSummaryTests: BaseTests {
    
    let medDocsManager = MedDocsManager()
    
    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_success_fetchClinicalSummaries() {
        let ex = expectation(description: "Expecting valid json response")
        
        let payload = ["startDate":"", "endDate":""]
        setMockFile(responseFile: "ccda", for: MedDocsAPIRouter.getClinicalSummaries(valueDict: payload).urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchClinicalSummaries(fromDate: "", toDate: "") { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(medDocsManager.clinicalSummaryList.count == 7)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_fetchClinicalSummaries_filter() {
        let ex = expectation(description: "Expecting valid json response")
        
        let payload = ["startDate":"Jun 1, 2021", "endDate":"Jul 1, 2021"]
        setMockFile(responseFile: "ccda-june", for: MedDocsAPIRouter.getClinicalSummaries(valueDict: payload).urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchClinicalSummaries(fromDate: "Jun 1, 2021", toDate: "Jul 1, 2021") { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(medDocsManager.clinicalSummaryList.count == 1)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    
    func test_Parse_Fail_fetchClinicalSummaries() {
        let ex = expectation(description: "Expecting invalid json response")
        
        let payload = ["startDate":"", "endDate":""]
        setMockFile(responseFile: "Appointments_Invalid", for: MedDocsAPIRouter.getClinicalSummaries(valueDict: payload).urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchClinicalSummaries(fromDate: "", toDate: "") {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 30.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchClinicalSummaries() {
        let ex = expectation(description: "Expecting empty json response")

        let payload = ["startDate":"", "endDate":""]
        setMockFile(responseFile: "Empty", for: MedDocsAPIRouter.getClinicalSummaries(valueDict: payload).urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchClinicalSummaries(fromDate: "", toDate: "") {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchClinicalSummaries() {
        let ex = expectation(description: "Expecting server error")
        
        let payload = ["startDate":"", "endDate":""]
        setError(for: MedDocsAPIRouter.getClinicalSummaries(valueDict: payload).urlRequest!)
        
        medDocsManager.fetchClinicalSummaries(fromDate: "", toDate: "") {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchClinicalSummaryData() {
        let ex = expectation(description: "Expecting valid json response")

        let payload = ["DocumentId": "123", "DirectAddress": ""]
        setMockFile(responseFile: "success", for: MedDocsAPIRouter.getClinicalSummaryData(valueDict: payload).urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchClinicalSummaryData(id: "123"){
            data, status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertFalse(data == nil)
        }
        waitForExpectations(timeout: 20.0, handler: nil)
    }
    
    func test_Fail_fetchClinicalSummaryData() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        let payload = ["DocumentId": "123", "DirectAddress": ""]
        setMockData(withString: "\"Failed\"", for: MedDocsAPIRouter.getClinicalSummaryData(valueDict: payload).urlRequest!)

        medDocsManager.fetchClinicalSummaryData(id: "123"){
            data, status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
            XCTAssertTrue(data == nil)
        }
        waitForExpectations(timeout: 20.0, handler: nil)
    }
    
    func test_ServerError_fetchClinicalSummaryData() {
        let ex = expectation(description: "Expecting server error")
        
        let payload = ["DocumentId": "123", "DirectAddress": ""]
        setError(for: MedDocsAPIRouter.getClinicalSummaryData(valueDict: payload).urlRequest!)

        medDocsManager.fetchClinicalSummaryData(id: "123"){
            data, status in

            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            XCTAssertTrue(data == nil)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_transmitCSDoc() {
        let ex = expectation(description: "Expecting valid json response")

        var payload = ClinicalSummaryTrasmitInfo()
        payload.directAddress = "mk@gmail.com"
        payload.documentId = ["123"]
        payload.filePass = "aaa"
        
        let payloadDict = ["barracuda": "true"]
        
        setMockData(withString: "\"Success\"", for: MedDocsAPIRouter.transmitClinicalSummary(valueDict: payloadDict, request: payload).urlRequest!)
        
        medDocsManager.transmitCSDoc(isSecure: true, transmitInfo: payload) {
            data, exptn, status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(data == true)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_transmitCSDoc() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        var payload = ClinicalSummaryTrasmitInfo()
        payload.directAddress = "mk@gmail.com"
        payload.documentId = ["123"]
        payload.filePass = "aaa"
        
        let payloadDict = ["barracuda": "true"]
        
        setMockData(withString: "\"Failed\"", for: MedDocsAPIRouter.transmitClinicalSummary(valueDict: payloadDict, request: payload).urlRequest!)
        
        medDocsManager.transmitCSDoc(isSecure: true, transmitInfo: payload) {
            data, exptn, status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
            XCTAssertTrue(data == false)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_transmitCSDoc() {
        let ex = expectation(description: "Expecting server error")
        
        var payload = ClinicalSummaryTrasmitInfo()
        payload.directAddress = "mk@gmail.com"
        payload.documentId = ["123"]
        payload.filePass = "aaa"
        
        let payloadDict = ["barracuda": "true"]
        
        setError(for: MedDocsAPIRouter.transmitClinicalSummary(valueDict: payloadDict, request: payload).urlRequest!)

        medDocsManager.transmitCSDoc(isSecure: true, transmitInfo: payload) {
            data, exptn, status in

            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            XCTAssertTrue(data == false)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
}
