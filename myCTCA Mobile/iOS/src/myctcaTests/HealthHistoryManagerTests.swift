//
//  HealthHistoryManagerTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 1/29/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class HealthHistoryManagerTests: BaseTests {
    
    let healthHistoryManager = HealthHistoryManager()

    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_Success_fetchHelathHistory_vitals() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "vitals", for: HealthHistoryAPIRouter.getVitals.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .vitals){ [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(healthHistoryManager.vitalsInfoList.count == 1)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchHelathHistory_vitals() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: HealthHistoryAPIRouter.getVitals.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .vitals) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchHelathHistory_vitals() {
        let ex = expectation(description: "Expecting server error")
        setError(for: HealthHistoryAPIRouter.getVitals.urlRequest!)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .vitals) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.healthHistoryManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchHelathHistory_allergies() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "allergies", for: HealthHistoryAPIRouter.getAllergies.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .allergies){ [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(healthHistoryManager.allergiesOriginal!.count == 4)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchHelathHistory_allergies() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: HealthHistoryAPIRouter.getAllergies.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .allergies) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchHelathHistory_allergies() {
        let ex = expectation(description: "Expecting server error")
        setError(for: HealthHistoryAPIRouter.getAllergies.urlRequest!)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .allergies) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.healthHistoryManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchHelathHistory_healthissues() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "healthIssues", for: HealthHistoryAPIRouter.getHealthIssues.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .healthIssues){ [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(healthHistoryManager.healthIssues!.count == 3)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchHelathHistory_healthissues() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: HealthHistoryAPIRouter.getHealthIssues.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .healthIssues) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchHelathHistory_healthissues() {
        let ex = expectation(description: "Expecting server error")
        setError(for: HealthHistoryAPIRouter.getHealthIssues.urlRequest!)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .healthIssues) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.healthHistoryManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchHelathHistory_immunization() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "immunizations", for: HealthHistoryAPIRouter.getImmunizations.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .immunizations){ [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(healthHistoryManager.immunizations!.count == 2)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchHelathHistory_immunization() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: HealthHistoryAPIRouter.getImmunizations.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .immunizations) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchHelathHistory_immunization() {
        let ex = expectation(description: "Expecting server error")
        setError(for: HealthHistoryAPIRouter.getImmunizations.urlRequest!)

        healthHistoryManager.fetchHelathHistory(healthHistoryType: .immunizations) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.healthHistoryManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchHelathHistory_prescriptions() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "prescriptions", for: HealthHistoryAPIRouter.getPrescriptions.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchPrescriptions() { [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(healthHistoryManager.prescriptionsOriginal!.count == 9)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchHelathHistory_prescriptions() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: HealthHistoryAPIRouter.getPrescriptions.urlRequest!, forBundle: Self.self)

        healthHistoryManager.fetchPrescriptions() {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchHelathHistory_prescriptions() {
        let ex = expectation(description: "Expecting server error")
        setError(for: HealthHistoryAPIRouter.getPrescriptions.urlRequest!)

        healthHistoryManager.fetchPrescriptions() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.healthHistoryManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    
}
