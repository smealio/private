//
//  AppointmentsManagerTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 12/14/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class AppointmentsManagerTests: BaseTests {
    
    let appointmentsManager = AppointmentsManager().shared

    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
    }
    
    //MARK: - fetchAppointments() test

    func test_success_fetchAppointments() {
        let ex = expectation(description: "Expecting valid json response")
        setMockFile(responseFile: "Appointments", for: AppointmentsAPIRouter.getAppts.urlRequest!, forBundle: Self.self)
        
        appointmentsManager.fetchAppointments() { [self]
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(appointmentsManager.appointments?.count == 2)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    
    func test_Parse_Fail_fetchAppointments() {
        let ex = expectation(description: "Expecting invalid json response")
        setMockFile(responseFile: "Appointments_Invalid", for: AppointmentsAPIRouter.getAppts.urlRequest!, forBundle: Self.self)

        appointmentsManager.fetchAppointments() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 30.0, handler: nil)
        XCTAssertTrue(appointmentsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchAppointments() {
        let ex = expectation(description: "Expecting empty json response")
        setMockFile(responseFile: "Empty", for: AppointmentsAPIRouter.getAppts.urlRequest!, forBundle: Self.self)

        appointmentsManager.fetchAppointments() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(appointmentsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchAppointments() {
        let ex = expectation(description: "Expecting server error")
        setError(for: AppointmentsAPIRouter.getAppts.urlRequest!)

        appointmentsManager.fetchAppointments() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.appointmentsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
        
    func test_success_requestOrChangeAppointment() {
        let ex = expectation(description: "Expecting valid json response")

        let payload = ["From": "Tanja Test",
                       "AppointmentId": "10516",
                       "Subject": "Appointment cancellation request",
                       "PhoneNumber": "1234567890",
                       "Comments": "Test"]
                
        setMockFile(responseFile: "CancelAppointments", for: AppointmentsAPIRouter.cancelAppt(valueDict: payload).urlRequest!, forBundle: Self.self)
        
        appointmentsManager.requestOrChangeAppointment(requestType: .cancel, params: payload) {
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_requestOrChangeAppointment() {
        let ex = expectation(description: "Expecting invalid json response")

        let payload = ["From": "Tanja Test",
                       "AppointmentId": "",
                       "Subject": "Appointment cancellation request",
                       "PhoneNumber": "1234567890",
                       "Comments": "Test"]
        
        setNilData(urlRequest: AppointmentsAPIRouter.cancelAppt(valueDict: payload).urlRequest!)
        
        appointmentsManager.requestOrChangeAppointment(requestType: .cancel, params: payload) {
            status in
            
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
            
        }
        waitForExpectations(timeout: 60.0, handler: nil)
       XCTAssertTrue(self.appointmentsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_requestOrChangeAppointment() {
        let ex = expectation(description: "Expecting server error")
        
        let payload = ["From": "Tanja Test",
                       "AppointmentId": "",
                       "Subject": "Appointment cancellation request",
                       "PhoneNumber": "1234567890",
                       "Comments": "Test"]
    
        setError(for: AppointmentsAPIRouter.cancelAppt(valueDict: payload).urlRequest!)

        appointmentsManager.requestOrChangeAppointment(requestType: .cancel, params: payload) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.appointmentsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_downloadAppointments() {
        let ex = expectation(description: "Expecting valid pdf response")

        let payload = ["startDate": "Dec 1, 2020",
                       "endDate": "Dec 2, 2020"]
                
        setMockFilePDF(responseFile: "unit-test", for: AppointmentsAPIRouter.downloadAppts(valueDict: payload).urlRequest!, forBundle: Self.self)
        
        appointmentsManager.downloadAppointments(params: payload) {
            status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_downloadAppointments() {
        let ex = expectation(description: "Expecting error")

        let payload = ["startDate": "INVALID",
                       "endDate": "INVALID"]

        setNilData(urlRequest: AppointmentsAPIRouter.downloadAppts(valueDict: payload).urlRequest!)
        
        appointmentsManager.downloadAppointments(params: payload) {
            status in
            ex.fulfill()
            XCTAssertTrue(status == .FAILED)

        }
        waitForExpectations(timeout: 60.0, handler: nil)
    }
    
    func test_ServerError_downloadAppointments() {
        let ex = expectation(description: "Expecting server error")
        let payload = ["startDate": "INVALID",
                       "endDate": "INVALID"]
        
        setError(for: AppointmentsAPIRouter.downloadAppts(valueDict: payload).urlRequest!)

        appointmentsManager.downloadAppointments(params: payload) {
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.appointmentsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
}
