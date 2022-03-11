//
//  ROI.swift
//  myctca
//
//  Created by Tomack, Barry on 2/5/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

/**
 This object was declared as a struct to make it smaller and copyable instead of passing by reference.
    1. Encapsulates a few relatively simple data values.
    2. Reasonable to expect that the encapsulated values will be copied rather than referenced
    3. Properties stored by the struct are themselves value types
    4. The struct does not need to inherit properties or behavior from another existing type.
 Struct instances are allocated on stack, and class instances are allocated on heap so sructs are faster.
 
 Codable means that it can be directly encoded (for JSON, in this case)
 */

struct ROI : Codable {
    
    var selectedFacility: String?
    var firstName: String?
    var lastName: String?
    var dateOfBirth: String?
    var selectedDeliveryMethod: [String] = [String]()
    var pickupDate: String?
    var selectedAuthorizationAction = [String]()
    var facilityOrIndividual: String?
    var address: String?
    var city: String?
    var state: String?
    var zip: String?
    var phoneNumber: String?
    var fax: String?
    var emailAddress: String?
    var selectedPurposes: [String] = [String]()
    var beginningOfTreatment: Bool = false
    var beginDate: String?
    var endOfTreatment: Bool = false
    var endDate: String?
    var restrictions: String?
    var selectedDisclosureInformation: [String] = [String]()
    var disclosureInformationOther: String?
    var selectedHighlyConfidentialDiscolosureInformation: [String] = [String]()
    var signature: String?
    var patientRelation: String?
    
    init() { }
    
}

