//
//  AuthenticationManagerTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 1/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class AuthenticationManagerTests: BaseTests {
    
    let authenticationManager = AuthenticationManager()

    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_Success_authenticateUser() {
        
        let ex = expectation(description: "Expecting valid json response")
        
        let payload = ["username": "test", "password": "test", "client_id": "test", "client_secret": Environment().configuration(PlistKey.ClientSecret), "grant_type": "test", "scope": "test"]
        
        setMockFile(responseFile: "accesstoken", for: AuthenticationAPIRouter.login(valueDict: payload).urlRequest!, forBundle: Self.self)

        authenticationManager.authenticateUser(username: "test", password: "test") {
            error, status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail1_authenticateUser() {
        
        let ex = expectation(description: "Expecting valid json response with error")
        
        let payload = ["username": "test", "password": "test", "client_id": "test", "client_secret": Environment().configuration(PlistKey.ClientSecret), "grant_type": "test", "scope": "test"]
        
        setMockFile(responseFile: "accesstoken_error", for: AuthenticationAPIRouter.login(valueDict: payload).urlRequest!, forBundle: Self.self)

        authenticationManager.authenticateUser(username: "test", password: "test") {
            error, status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_authenticateUser(){
        let ex = expectation(description: "Expecting server error")
        
        let payload = ["username": "test", "password": "test", "client_id": "test", "client_secret": Environment().configuration(PlistKey.ClientSecret), "grant_type": "test", "scope": "test"]
        
        setError(for: AuthenticationAPIRouter.login(valueDict: payload).urlRequest!)
        
        authenticationManager.authenticateUser(username: "test", password: "test") {
            error, status in

            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchIdentityUserInfo() {
        
        let ex = expectation(description: "Expecting valid json response")

        setMockFile(responseFile: "identityuser", for: AuthenticationAPIRouter.getUserInfo.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchIdentityUserInfo(){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchIdentityUserInfo() {
        
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: AuthenticationAPIRouter.getUserInfo.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchIdentityUserInfo() {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchIdentityUserInfo() {
        let ex = expectation(description: "Expecting server error")
        setError(for: AuthenticationAPIRouter.getUserInfo.urlRequest!)

        authenticationManager.fetchIdentityUserInfo() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.authenticationManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchUserProfile() {
        
        let ex = expectation(description: "Expecting valid json response")

        setMockFile(responseFile: "userprofile", for: AuthenticationAPIRouter.getUserProfile.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchUserProfile(){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchUserProfile() {
        
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: AuthenticationAPIRouter.getUserProfile.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchUserProfile(){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchUserProfile() {
        let ex = expectation(description: "Expecting server error")
        setError(for: AuthenticationAPIRouter.getUserProfile.urlRequest!)

        authenticationManager.fetchUserProfile() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.authenticationManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchAllFacilites() {
        
        let ex = expectation(description: "Expecting valid json response")

        setMockFile(responseFile: "allfacilities", for: AuthenticationAPIRouter.getAllFacilities.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchAllFacilites(){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchAllFacilites() {
        
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: AuthenticationAPIRouter.getAllFacilities.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchAllFacilites(){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchAllFacilites() {
        let ex = expectation(description: "Expecting server error")
        setError(for: AuthenticationAPIRouter.getAllFacilities.urlRequest!)

        authenticationManager.fetchAllFacilites() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.authenticationManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchUserPreferences() {
        
        let ex = expectation(description: "Expecting valid json response")

        setMockFile(responseFile: "userpreferences", for: AuthenticationAPIRouter.getUserPreferences.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchUserPreferences(){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchUserPreferences() {
        
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: AuthenticationAPIRouter.getUserPreferences.urlRequest!, forBundle: Self.self)

        authenticationManager.fetchUserPreferences(){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchUserPreferences() {
        let ex = expectation(description: "Expecting server error")
        setError(for: AuthenticationAPIRouter.getUserPreferences.urlRequest!)

        authenticationManager.fetchUserPreferences() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.authenticationManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_switchToProxyUser() {
        
        let ex = expectation(description: "Expecting valid json response")
        let params = ["proxyUserId": "7135", "IsImpersonating": "true"]

        setMockFile(responseFile: "userprofile", for: AuthenticationAPIRouter.switchToProxy(valueDict:params).urlRequest!, forBundle: Self.self)

        authenticationManager.switchToProxyUser(proxyUserId: "7135", toPatient: true) {
            error, status  in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            //XCTAssertTrue(self.authenticationManager.impersonatedPatientProfile != nil)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_switchToProxyUser() {
        
        let ex = expectation(description: "Expecting empty json response")
        let params = ["proxyUserId": "7135", "IsImpersonating": "true"]

        setMockFile(responseFile: "Empty", for: AuthenticationAPIRouter.switchToProxy(valueDict:params).urlRequest!, forBundle: Self.self)

        authenticationManager.switchToProxyUser(proxyUserId: "7135", toPatient: true) {
            error, status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_fetchUserContactInfo() {
        let ex = expectation(description: "Expecting valid json response")
        
        setMockFile(responseFile: "contactinfo", for: AuthenticationAPIRouter.getUserContactInfo.urlRequest!, forBundle: Self.self)
        
        authenticationManager.fetchUserContactInfo() {
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssert((self.formsManager.userContacts != nil))

            ex.fulfill()
        }
        waitForExpectations(timeout: 20.0, handler: nil)
    }
    
    func test_Parse_Fail_fetchUserContactInfo() {
        let ex = expectation(description: "Expecting invalid json response")
        
        setMockFile(responseFile: "Appointments_Invalid", for: AuthenticationAPIRouter.getUserContactInfo.urlRequest!, forBundle: Self.self)
        
        authenticationManager.fetchUserContactInfo() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
        XCTAssertTrue(formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchUserContactInfo() {
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: FormsAPIRouter.AuthenticationAPIRouter.urlRequest!, forBundle: Self.self)
        
        authenticationManager.fetchUserContactInfo() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchUserContactInfo() {
        let ex = expectation(description: "Expecting server error")
        
        setError(for: AuthenticationAPIRouter.getUserContactInfo.urlRequest!)
        
        authenticationManager.fetchUserContactInfo() { [self]
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
}
