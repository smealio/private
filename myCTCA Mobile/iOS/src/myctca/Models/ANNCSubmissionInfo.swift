//
//  ANNCSubmissionInfo.swift
//  myctca
//
//  Created by Manjunath K on 9/8/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct ANNCSubmissionInfo : Codable {
    var dateOfService:String?
    var dateSigned:String?
    var facilityName:String?
    var insuranceName:String?
    var mrn:String?
    var patientName:String?
    var patientSignature:String?
    var paymentOption:String?
    var responsibleParty:String?
    
    init() {
    }
}


