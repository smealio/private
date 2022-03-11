//
//  MedDocsTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 7/27/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class MedDocsTests: BaseTests {
    
    let medDocsManager = MedDocsManager()
    
    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_Success_fetchCarePlanDetails() {
        let ex = expectation(description: "Expecting valid json response")

        setMockFile(responseFile: "careplan", for: MedDocsAPIRouter.getCarePlanDetails.urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchCarePlanDetails(){
            data, status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(data == true)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_no_data_fetchCarePlanDetails() {
        
        let ex = expectation(description: "Expecting json with no data response")
        
        setMockFile(responseFile: "nocareplans", for: MedDocsAPIRouter.getCarePlanDetails.urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchCarePlanDetails(){
            data, status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(data == false)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchCarePlanDetails() {
        let ex = expectation(description: "Expecting server error")
        
        setError(for: MedDocsAPIRouter.getCarePlanDetails.urlRequest!)

        medDocsManager.fetchCarePlanDetails(){
            data, status in

            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_fetchMedDocs() {
        let ex = expectation(description: "Expecting valid json response")
        
        setMockFile(responseFile: "meddocs", for: MedDocsAPIRouter.getmedicaldocuments(type: "clinical").urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchMedDocs(type: .clinical) { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(medDocsManager.medDocs.count == 9)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Parse_Fail_fetchMedDocs() {
        let ex = expectation(description: "Expecting invalid json response")
        
        setMockFile(responseFile: "Appointments_Invalid", for: MedDocsAPIRouter.getmedicaldocuments(type: "clinical").urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchMedDocs(type: .clinical) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchMedDocs() {
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: MedDocsAPIRouter.getmedicaldocuments(type: "clinical").urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchMedDocs(type: .clinical) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchMedDocs() {
        let ex = expectation(description: "Expecting server error")
        
        setError(for: MedDocsAPIRouter.getmedicaldocuments(type: "clinical").urlRequest!)
        
        medDocsManager.fetchMedDocs(type: .clinical) { [self]
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_fetchMedDoc() {
        let ex = expectation(description: "Expecting valid json response")
        
        setMockFile(responseFile: "meddocdetails", for: MedDocsAPIRouter.getmedicaldocument(type: "clinical", id: "256100001").urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchMedDoc(type: .clinical, docId: "256100001") {
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 20.0, handler: nil)
    }
    
    func test_Parse_Fail_fetchMedDoc() {
        let ex = expectation(description: "Expecting invalid json response")
        
        setMockFile(responseFile: "Appointments_Invalid", for: MedDocsAPIRouter.getmedicaldocument(type: "clinical", id: "256100001").urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchMedDoc(type: .clinical, docId: "256100001") {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchMedDoc() {
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: MedDocsAPIRouter.getmedicaldocument(type: "clinical", id: "256100001").urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchMedDoc(type: .clinical, docId: "256100001") { 
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchMedDoc() {
        let ex = expectation(description: "Expecting server error")
        
        setError(for: MedDocsAPIRouter.getmedicaldocument(type: "clinical", id: "256100001").urlRequest!)
        
        medDocsManager.fetchMedDoc(type: .clinical, docId: "256100001") { [self]
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_fetchImagingDocs() {
        let ex = expectation(description: "Expecting valid json response")
        
        setMockFile(responseFile: "imaging", for: MedDocsAPIRouter.getimagingdocuments.urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchImagingDocs() { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(medDocsManager.imagingDocs.count == 1)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Parse_Fail_fetchImagingDocs() {
        let ex = expectation(description: "Expecting invalid json response")
        
        setMockFile(responseFile: "Appointments_Invalid", for: MedDocsAPIRouter.getimagingdocuments.urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchImagingDocs() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchImagingDocs() {
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: MedDocsAPIRouter.getimagingdocuments.urlRequest!, forBundle: Self.self)
        
        medDocsManager.fetchImagingDocs() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
        XCTAssertTrue(medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchImagingDocs() {
        let ex = expectation(description: "Expecting server error")
        
        setError(for: MedDocsAPIRouter.getimagingdocuments.urlRequest!)
        
        medDocsManager.fetchImagingDocs() { [self]
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.medDocsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
}
