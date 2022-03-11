//
//  FormsManagerTests.swift
//  myctcaTests
//
//  Created by Manjunath K on 7/23/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import XCTest
@testable import myctca

class FormsManagerTests: BaseTests {
    
    let formsManager = FormsManager()
    
    var aNNCSubmissionInfo:ANNCSubmissionInfo?
    var roiInfo:ROI?
    
    override func setUp() {
        super.setUp()
        
        TestSetupManager.shared.testMode = true
        
        aNNCSubmissionInfo = ANNCSubmissionInfo()
        aNNCSubmissionInfo?.dateOfService = "08\\/12\\/2021"
        aNNCSubmissionInfo?.dateSigned = "08\\/12\\/2021"
        aNNCSubmissionInfo?.facilityName = "WRMC"
        aNNCSubmissionInfo?.insuranceName = "Test"
        aNNCSubmissionInfo?.mrn = "12345"
        aNNCSubmissionInfo?.patientName = "Jennifer"
        aNNCSubmissionInfo?.patientSignature = "Test"
        aNNCSubmissionInfo?.paymentOption = "option2"
        aNNCSubmissionInfo?.responsibleParty = "Test"
        
        roiInfo = ROI()
        roiInfo?.selectedFacility = ""
        roiInfo?.firstName = "Jennifer"
        roiInfo?.lastName = "Jennifer"
        roiInfo?.dateOfBirth = "Jun 14, 1981"
        roiInfo?.selectedDeliveryMethod = ["Mail"]
        roiInfo?.pickupDate = "Jun 14, 2021"
        roiInfo?.selectedAuthorizationAction = ["Release information to:"]
        roiInfo?.facilityOrIndividual = "SERMC"
        roiInfo?.address = "Test"
        roiInfo?.city = "KL"
        roiInfo?.state = "Ohio"
        roiInfo?.zip = "12345"
        roiInfo?.phoneNumber = "122432434"
        roiInfo?.fax = ""
        roiInfo?.emailAddress = "sfdsr@sfdf.com"
        roiInfo?.selectedPurposes = ["Legal"]
        roiInfo?.beginningOfTreatment = false
        roiInfo?.beginDate = "Jun 10, 2021"
        roiInfo?.endOfTreatment = false
        roiInfo?.endDate = "Jun 11, 2021"
        roiInfo?.restrictions = "restrictions"
        roiInfo?.selectedDisclosureInformation = ["Chemotherapy flowsheet"]
        roiInfo?.disclosureInformationOther = "Test"
        roiInfo?.selectedHighlyConfidentialDiscolosureInformation = ["Abuse of an adult with disability", "Child abuse and neglect"]
        roiInfo?.signature = "test"
        roiInfo?.patientRelation = "test"
    }

    func test_success_fetchROIFormInfo() {
        let ex = expectation(description: "Expecting valid json response")
        
        setMockFile(responseFile: "roi_info", for: FormsAPIRouter.getROIFormInfo.urlRequest!, forBundle: Self.self)
        
        formsManager.fetchROIFormInfo() {
            status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssert((self.formsManager.roiFormInfo.facilityList.count == 5))

            ex.fulfill()
        }
        waitForExpectations(timeout: 20.0, handler: nil)
    }
    
    func test_Parse_Fail_fetchROIFormInfo() {
        let ex = expectation(description: "Expecting invalid json response")
        
        setMockFile(responseFile: "Appointments_Invalid", for: FormsAPIRouter.getROIFormInfo.urlRequest!, forBundle: Self.self)
        
        formsManager.fetchROIFormInfo() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
        XCTAssertTrue(formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_NilData_Fail_fetchROIFormInfo() {
        let ex = expectation(description: "Expecting empty json response")

        setMockFile(responseFile: "Empty", for: FormsAPIRouter.getROIFormInfo.urlRequest!, forBundle: Self.self)
        
        formsManager.fetchROIFormInfo() {
            status in
            
            XCTAssertTrue(status == .FAILED)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 60.0, handler: nil)
        XCTAssertTrue(formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
    }
    
    func test_ServerError_fetchROIFormInfo() {
        let ex = expectation(description: "Expecting server error")
        
        setError(for: FormsAPIRouter.getROIFormInfo.urlRequest!)
        
        formsManager.fetchROIFormInfo() { [self]
            status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_checkForExistingANNC() {
        
        let ex = expectation(description: "Expecting valid json response")
          
        setMockData(withString: "\"Success\"", for: FormsAPIRouter.checkForExistingANNC.urlRequest!)

        formsManager.checkForExistingANNC(){
            result, status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(result == true)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_checkForExistingANNC() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockData(withString: "\"Failed\"", for: FormsAPIRouter.checkForExistingANNC.urlRequest!)

        formsManager.checkForExistingANNC() {
            result, status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
            XCTAssertTrue(result == false)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_checkForExistingANNC() {
        let ex = expectation(description: "Expecting server error")
        setError(for: FormsAPIRouter.checkForExistingANNC.urlRequest!)

        formsManager.checkForExistingANNC() {
            result, status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            XCTAssertTrue(result == false)

            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Success_fetchMRNNumber() {
        
        let ex = expectation(description: "Expecting valid json response")
          
        setMockData(withString: "12345", for: FormsAPIRouter.checkForExistingANNC.urlRequest!)

        formsManager.fetchMRNNumber(){
            result, status in
            ex.fulfill()

            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(result == "12345")

        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_fetchMRNNumber() {
        
        let ex = expectation(description: "Expecting empty json response")
        
        setMockData(withString: "", for: FormsAPIRouter.checkForExistingANNC.urlRequest!)

        formsManager.fetchMRNNumber() {
            result, status in
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_fetchMRNNumber() {
        let ex = expectation(description: "Expecting server error")
        setError(for: FormsAPIRouter.checkForExistingANNC.urlRequest!)

        formsManager.fetchMRNNumber() {
            result, status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)

            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_submitANNCForm() {
        let ex = expectation(description: "Expecting valid json response")

        setMockFile(responseFile: "CancelAppointments", for: FormsAPIRouter.submitANNC(request: aNNCSubmissionInfo!).urlRequest!, forBundle: Self.self)
        
        formsManager.submitANNCForm(anncInfo: aNNCSubmissionInfo!) {
            
            result, error, status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(result == true)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_submitANNCForm() {
        let ex = expectation(description: "Expecting invalid json response")
        
        setNilData(urlRequest: FormsAPIRouter.submitANNC(request: aNNCSubmissionInfo!).urlRequest!)
        
        formsManager.submitANNCForm(anncInfo: aNNCSubmissionInfo!) {
            
            result, error, status in
            
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
            XCTAssertTrue(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_submitANNCForm() {
        let ex = expectation(description: "Expecting server error")
    
        setError(for: FormsAPIRouter.submitANNC(request: aNNCSubmissionInfo!).urlRequest!)
        
        formsManager.submitANNCForm(anncInfo: aNNCSubmissionInfo!) {
            
            result, error, status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_success_submitROIForm() {
        let ex = expectation(description: "Expecting valid json response")

        setMockFile(responseFile: "CancelAppointments", for: FormsAPIRouter.submitROI(request: roiInfo!).urlRequest!, forBundle: Self.self)
        
        formsManager.submitROIForm(roiInfo: roiInfo!) {
            
            result, error, status in
            
            XCTAssertTrue(status == .SUCCESS)
            XCTAssertTrue(result == true)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_Fail_submitROIForm() {
        let ex = expectation(description: "Expecting invalid json response")
        
        setNilData(urlRequest: FormsAPIRouter.submitROI(request: roiInfo!).urlRequest!)
        
        formsManager.submitROIForm(roiInfo: roiInfo!) {
            
            result, error, status in
            
            ex.fulfill()

            XCTAssertTrue(status == .FAILED)
            XCTAssertTrue(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
    
    func test_ServerError_submitROIForm() {
        let ex = expectation(description: "Expecting server error")
    
        setError(for: FormsAPIRouter.submitROI(request: roiInfo!).urlRequest!)
        
        formsManager.submitROIForm(roiInfo: roiInfo!) {
            
            result, error, status in
            
            XCTAssertTrue(status == .FAILED)
            XCTAssert(self.formsManager.serverError?.errorCode == DEFAULT_ERROR_CODE)
            
            ex.fulfill()
        }
        waitForExpectations(timeout: 2.0, handler: nil)
    }
}
