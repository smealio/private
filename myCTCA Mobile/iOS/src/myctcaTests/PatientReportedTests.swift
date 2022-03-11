//
//  PatientReportedTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 3/30/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class PatientReportedTests: BaseTests {
    
    let patientReportedManager = PatientReportedManager()

    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_success_fetchSymptomInventory() {
        let ex = expectation(description: "Expecting valid json response")
        setMockFile(responseFile: "patientreporteddocuments", for: MoreAPIRouter.getSymptomInventory.urlRequest!, forBundle: Self.self)
        
        patientReportedManager.fetchSymptomInventory() { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(patientReportedManager.symptomsList.count == 3)
            XCTAssertTrue(patientReportedManager.symptomsDocs.count == 2)
            XCTAssertTrue(patientReportedManager.symptomsDates.count == 2)

            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    
    func test_Parse_Fail_fetchSymptomInventory() {
        let ex = expectation(description: "Expecting invalid json response")
        setMockFile(responseFile: "Appointments_Invalid", for: MoreAPIRouter.getSymptomInventory.urlRequest!, forBundle: Self.self)
        
        patientReportedManager.fetchSymptomInventory() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 30.0, handler: nil)
        XCTAssertTrue(patientReportedManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchSymptomInventory() {
        let ex = expectation(description: "Expecting empty json response")
        setMockFile(responseFile: "Empty", for: MoreAPIRouter.getSymptomInventory.urlRequest!, forBundle: Self.self)
        
        patientReportedManager.fetchSymptomInventory() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(patientReportedManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchSymptomInventory() {
        let ex = expectation(description: "Expecting server error")
        setError(for: MoreAPIRouter.getSymptomInventory.urlRequest!)
        
        patientReportedManager.fetchSymptomInventory() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.patientReportedManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
}
