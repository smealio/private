//
//  PrescriptionRefillRequest.swift
//  myctca
//
//  Created by Tomack, Barry on 3/29/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

struct PrescriptionRefillRequest : Encodable {

    var selectedPrescriptions: [String] = [String]()
    var patientPhone: String?
    var pharmacyName: String?
    var pharmacyPhone: String?
    var comments: String?
    var to:[CareTeam] = []
    
    init() { }
}

