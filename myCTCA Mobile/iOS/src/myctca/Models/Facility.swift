//
//  Facility.swift
//  myctca
//
//  Created by Tomack, Barry on 12/6/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

struct FacilityAddress : Codable {
    var address1: String? = ""
    var address2: String?
    var city: String? = ""
    var state: String? = ""
    var postalCode: String? = ""
    
    enum CodingKeys: String, CodingKey {
        case city, state, postalCode
        case address1 = "line1"
        case address2 = "line2"
    }
    
}

struct Facility : Codable {
    var facilityCode: String = ""
    var displayName: String = ""
    var address = FacilityAddress()
    var mainPhone: String? = ""
    var schedulingPhone :String? = ""
    var accommodationsPhone: String? = ""
    var transportationPhone: String? = ""
    var himROIPhone: String?
    var shortDisplayName: String? = ""
    var schedulingSecondaryPhone: String? = ""
    var travelAndAccommodationsPhone: String? = ""
    var careManagementPhone: String? = ""
    var billingPhone: String? = ""
    var pharmacyPhone: String? = ""

    enum CodingKeys: String, CodingKey {
        case facilityCode = "name"
        case displayName, address, mainPhone, schedulingPhone
        case accommodationsPhone, transportationPhone, shortDisplayName
        case pharmacyPhone, billingPhone, careManagementPhone
        case travelAndAccommodationsPhone, schedulingSecondaryPhone
        case himROIPhone = "himroiPhone"
    }
    
    init(appointmentData: Appointment) {
        var found = false
        if AppSessionManager.shared.currentUser.allFacilitesList.count > 0 {
            for fac in AppSessionManager.shared.currentUser.allFacilitesList {
                if fac.facilityCode == appointmentData.name {
                    self = fac
                    found = true
                    break
                }
            }
        }
        
        if !found {
            self.facilityCode = appointmentData.name
            
            self.address = FacilityAddress()
            self.address.address1 = appointmentData.address1
            self.address.address2 = appointmentData.address2
            self.address.city = appointmentData.city
            self.address.state = appointmentData.state
            self.address.postalCode = appointmentData.postalCode
            
            self.mainPhone = appointmentData.mainPhone
            self.schedulingPhone = appointmentData.schedulingPhone
            self.accommodationsPhone = appointmentData.accommodationsPhone
            self.transportationPhone = appointmentData.transportationPhone
        }
    }
}
