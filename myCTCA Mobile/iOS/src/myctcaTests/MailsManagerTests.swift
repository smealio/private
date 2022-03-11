//
//  MailsManagerTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 1/22/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class MailsManagerTests: BaseTests {
    
    let mailManager = MailManager()

    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    func test_Success_fetchMails_Inbox() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "inboxmail", for: MailAPIRouter.getNewMail.urlRequest!, forBundle: Self.self)

        mailManager.fetchMails(ofType: .inbox) { [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(mailManager.newMails.count == 6)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchMails_Inbox() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: MailAPIRouter.getNewMail.urlRequest!, forBundle: Self.self)

        mailManager.fetchMails(ofType: .inbox) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchMails_Inbox() {
        let ex = expectation(description: "Expecting server error")
        setError(for: MailAPIRouter.getNewMail.urlRequest!)

        mailManager.fetchMails(ofType: .inbox) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.mailManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchMails_SentMail() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "inboxmail", for: MailAPIRouter.getSentMail.urlRequest!, forBundle: Self.self)

        mailManager.fetchMails(ofType: .sent) { [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(mailManager.sentMails.count == 6)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchMails_SentMail() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: MailAPIRouter.getSentMail.urlRequest!, forBundle: Self.self)

        mailManager.fetchMails(ofType: .sent) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchMails_SentMail() {
        let ex = expectation(description: "Expecting server error")
        setError(for: MailAPIRouter.getNewMail.urlRequest!)

        mailManager.fetchMails(ofType: .sent) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.mailManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchMails_Archived() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "inboxmail", for: MailAPIRouter.getArchivedMail.urlRequest!, forBundle: Self.self)

        mailManager.fetchMails(ofType: .archive) { [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(mailManager.archivedMails.count == 6)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchMails_Archived() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: MailAPIRouter.getArchivedMail.urlRequest!, forBundle: Self.self)

        mailManager.fetchMails(ofType: .archive) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchMails_Archived() {
        let ex = expectation(description: "Expecting server error")
        setError(for: MailAPIRouter.getNewMail.urlRequest!)

        mailManager.fetchMails(ofType: .archive) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.mailManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchCareTeams() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "careteams", for: MailAPIRouter.getCareTeam.urlRequest!, forBundle: Self.self)

        mailManager.fetchCareTeams(){ [self]
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(mailManager.careTeams.count == 4)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchCareTeams() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: MailAPIRouter.getCareTeam.urlRequest!, forBundle: Self.self)

        mailManager.fetchCareTeams() {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchCareTeams() {
        let ex = expectation(description: "Expecting server error")
        setError(for: MailAPIRouter.getCareTeam.urlRequest!)

        mailManager.fetchCareTeams() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.mailManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_setMailRead() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "setread", for: MailAPIRouter.setMailRead(value: "123").urlRequest!, forBundle: Self.self)

        mailManager.setMailRead(msgID: "123"){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_setMailRead() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockFile(responseFile: "Empty", for: MailAPIRouter.setMailRead(value: "123").urlRequest!, forBundle: Self.self)

        mailManager.setMailRead(msgID: "123"){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_setMailRead() {
        let ex = expectation(description: "Expecting server error")
        setError(for: MailAPIRouter.setMailRead(value: "123").urlRequest!)

        mailManager.setMailRead(msgID: "123") {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.mailManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_archiveMail() {
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockFile(responseFile: "success", for: MailAPIRouter.archiveMail(value: "123").urlRequest!, forBundle: Self.self)
        
        setMockData(withString: "\"Success\"", for: MailAPIRouter.archiveMail(value: "123").urlRequest!)

        mailManager.archiveMail(msgID: "123"){
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_archiveMail() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockData(withString: "\"Failed\"", for: MailAPIRouter.archiveMail(value: "123").urlRequest!)

        mailManager.archiveMail(msgID: "123") {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_archiveMail() {
        let ex = expectation(description: "Expecting server error")
        setError(for: MailAPIRouter.archiveMail(value: "123").urlRequest!)

        mailManager.archiveMail(msgID: "123") {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.mailManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_sendNewMail() {
        let mailInfo = NewMailInfo()
        
        mailInfo.comments = "Test"
        mailInfo.from = "Test"
        mailInfo.messageType = "1"
        
        mailInfo.subject = "Test"
        mailInfo.sent = "02023-01-20T23:12:12"
        
        var careTeam = CareTeam()
        careTeam.name = "Test"
        careTeam.systemId = "1111"
        careTeam.userName = "1111"
        
        mailInfo.to = [careTeam]
        mailInfo.selectedTo = [careTeam]
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockData(withString: "\"Success\"", for: MailAPIRouter.sendMail(value: mailInfo).urlRequest!)

        mailManager.sendNewMail(newMailInfo: mailInfo) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_sendNewMail() {
        let mailInfo = NewMailInfo()
        
        mailInfo.comments = "Test"
        mailInfo.from = "Test"
        mailInfo.messageType = "1"
        mailInfo.subject = "Test"
        mailInfo.sent = "02023-01-20T23:12:12"
        
        var careTeam = CareTeam()
        careTeam.name = "Test"
        careTeam.systemId = "1111"
        careTeam.userName = "1111"
        
        mailInfo.selectedTo = [careTeam]
        
        let ex = expectation(description: "Expecting valid json response")
  
        setMockData(withString: "\"Failed\"", for: MailAPIRouter.sendMail(value: mailInfo).urlRequest!)

        mailManager.sendNewMail(newMailInfo: mailInfo) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_sendNewMail() {
        
        let mailInfo = NewMailInfo()
        
        mailInfo.comments = "Test"
        mailInfo.from = "Test"
        mailInfo.messageType = "1"
        mailInfo.subject = "Test"
        mailInfo.sent = "02023-01-20T23:12:12"
        
        let ex = expectation(description: "Expecting server error")
        setError(for: MailAPIRouter.sendMail(value: mailInfo).urlRequest!)

        mailManager.sendNewMail(newMailInfo: mailInfo) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.mailManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }

}
