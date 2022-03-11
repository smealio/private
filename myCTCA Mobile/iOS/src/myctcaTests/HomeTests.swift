//
//  HomeTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 3/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class HomeTests: BaseTests {
    
    let homeManager = HomeManager()
    
    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
        
    func test_success_fetchAlertMessages() {
        let ex = expectation(description: "Expecting valid json response")
        setMockFile(responseFile: "alertmessages", for: HomeViewAPIRouter.getAlertMessages.urlRequest!, forBundle: Self.self)
        
        homeManager.fetchAlertMessages() { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(homeManager.messagesAll.count == 4)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    
    func test_Parse_Fail_fetchAlertMessages() {
        let ex = expectation(description: "Expecting invalid json response")
        setMockFile(responseFile: "Appointments_Invalid", for: HomeViewAPIRouter.getAlertMessages.urlRequest!, forBundle: Self.self)
        
        homeManager.fetchAlertMessages() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 30.0, handler: nil)
        XCTAssertTrue(homeManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchAlertMessages() {
        let ex = expectation(description: "Expecting empty json response")
        setMockFile(responseFile: "Empty", for: HomeViewAPIRouter.getAlertMessages.urlRequest!, forBundle: Self.self)
        
        homeManager.fetchAlertMessages() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(homeManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchAlertMessages() {
        let ex = expectation(description: "Expecting server error")
        setError(for: HomeViewAPIRouter.getAlertMessages.urlRequest!)
        
        homeManager.fetchAlertMessages() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.homeManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
 
    func test_success_saveUserPrefrences() {
        let ex = expectation(description: "Expecting valid json response")
        
        var payload = UserPreferencePayload()
        payload.userId = 7136
        payload.userPreferenceType = "AcceptedTermsOfUse"
        payload.userPreferenceValue = "True"
        
        let preferences = [payload]
        
        setMockFile(responseFile: "userpreferences", for: HomeViewAPIRouter.saveuserpreferences(infoList: preferences).urlRequest!, forBundle: Self.self)
        
        homeManager.saveUserPrefrences(preference: payload) { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    
    func test_Parse_Fail_saveUserPrefrences() {
        let ex = expectation(description: "Expecting invalid json response")
        
        var payload = UserPreferencePayload()
        payload.userId = 7136
        payload.userPreferenceType = "AcceptedTermsOfUse"
        payload.userPreferenceValue = "True"
        
        let preferences = [payload]
        
        setMockFile(responseFile: "Appointments_Invalid", for: HomeViewAPIRouter.saveuserpreferences(infoList: preferences).urlRequest!, forBundle: Self.self)
        
        homeManager.saveUserPrefrences(preference: payload) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 30.0, handler: nil)
        XCTAssertTrue(homeManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_saveUserPrefrences() {
        let ex = expectation(description: "Expecting empty json response")
        
        var payload = UserPreferencePayload()
        payload.userId = 7136
        payload.userPreferenceType = "AcceptedTermsOfUse"
        payload.userPreferenceValue = "True"
        
        let preferences = [payload]
        
        setMockFile(responseFile: "Empty", for: HomeViewAPIRouter.saveuserpreferences(infoList: preferences).urlRequest!, forBundle: Self.self)
        
        homeManager.saveUserPrefrences(preference: payload) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(homeManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_saveUserPrefrences() {
        let ex = expectation(description: "Expecting server error")
        
        var payload = UserPreferencePayload()
        payload.userId = 7136
        payload.userPreferenceType = "AcceptedTermsOfUse"
        payload.userPreferenceValue = "True"
        
        let preferences = [payload]
        
        setError(for: HomeViewAPIRouter.saveuserpreferences(infoList: preferences).urlRequest!)
        
        homeManager.saveUserPrefrences(preference: payload) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.homeManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
}
