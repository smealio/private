//
//  ROIFormInfo.swift
//  myctca
//
//  Created by Manjunath K on 8/4/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class ROIFormInfo: Codable {
    
    var facilityList = [String]()
    var facilitiesDetailList = [[String:String]]()
    var deliveryMethods = [String]()
    var authorizationActions = [String]()
    var purposes = [String]()
    var disclosureInformationList = [String]()
    var highlyConfidentialInformationList = [String]()
    
    enum CodingKeys: String, CodingKey {
        case facilityList, facilitiesDetailList = "facilities", deliveryMethods
        case authorizationActions, purposes, disclosureInformationList
        case highlyConfidentialInformationList
    }
}
