//
//  SendMessageTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 3/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class SendMessageTests: BaseTests {
    
    let sendMessageManager = SendMessageManager()
    let mailData = [
        "EmailAddress" :"emial.232@se.com",
        "Comments" : "Message",
        "Subject" : "Subject",
        "UserName" : "Name",
        "Facility" :"Cancer Treatment Centers of America, Phoenix",
        "PhoneNumber" : "23232432323",
        "AreaOfConcern" : "Scheduling"
    ]
    
    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_Success_sendMessage() {
        
        let ex = expectation(description: "Expecting valid json response")
        
        setMockData(withString: "\"Success\"", for: SendMessageAPIRouter.sendMessage(infoList: mailData).urlRequest!)
        
        sendMessageManager.sendMessage(content: mailData) {
            status in
            ex.fulfill()
            
            XCTAssertTrue(status == .SUCCESS)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_sendMessage() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockData(withString: "\"Failed\"", for: SendMessageAPIRouter.sendMessage(infoList: mailData).urlRequest!)
        
        sendMessageManager.sendMessage(content: mailData) {
            status in
            ex.fulfill()
            
            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_sendMessage() {
        let ex = expectation(description: "Expecting server error")
        setError(for: SendMessageAPIRouter.sendMessage(infoList: mailData).urlRequest!)
        
        sendMessageManager.sendMessage(content: mailData) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.sendMessageManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
}
