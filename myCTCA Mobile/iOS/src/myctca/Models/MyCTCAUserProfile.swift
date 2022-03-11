//
//  myCTCAUserProfile.swift
//  myctca
//
//  Created by Tomack, Barry on 11/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

struct MyCTCAUserProfile: Codable {
    var ctcaId: String = ""
    var isSuperUser:Bool
    var epiId: String? = ""
    var firstName: String = ""
    var lastName: String = ""
    var emailAddress: String = ""
    var primaryFacilityCode: String? = ""
    var roles: [String] = [String]()
    var userPermissions: [String] = [String]()
    var proxies: [MyCTCAProxy]
    
    enum CodingKeys: String, CodingKey {
        case ctcaId, isSuperUser, proxies, emailAddress, lastName, epiId, firstName, userPermissions, roles
        case primaryFacilityCode = "primaryFacility"
    }
    
    var userType: UserType {
        get {
            if roles.contains(MyCTCAConstants.UserTypeStrings.portalCaregiver) {
                return .CARE_GIVER
            } else {
                return .PATIENT
            }
        }
    }

    var fullName: String {
        get {
            return "\(firstName) \(lastName)"
        }
    }
    
    func userCan(_ permission: UserPermissionType, viewerId: String) -> Bool {
        
        if (epiId == viewerId) {
            for userPermission in userPermissions {
                if (userPermission == permission.rawValue) {
                    return true
                }
            }
        } else if viewerId.isEmpty { //For care giver, to access My Resources
            for userPermission in userPermissions {
                if (userPermission == permission.rawValue) {
                    return true
                }
            }
        } else {  //Need to test after patient assigned
            for proxy in proxies {
                if let permissions = proxy.permissions , proxy.toCTCAUniqueId == viewerId {
                    for userPermission in permissions {
                        if (userPermission == permission.rawValue) {
                            return true
                        }
                    }
                }
            }
        }
        
        return false
    }
    
    func viewablePatients() -> [(String,String)] {
        
        var patients = [(String,String)]()
        
        for proxy in proxies {
            if proxy.relationshipType != "Self" {
                patients.append((proxy.fullName, proxy.toCTCAUniqueId))
            }
        }
        
        return patients
    }
    
    func getCTCAUniqueIdFromFullName(_ fullName: String) -> String? {
        let nameAR = fullName.split(separator: " ")
        let firstName = nameAR[0]
        let lastName = nameAR[1]
        for proxy in proxies {
            if (proxy.firstName == firstName && proxy.lastName == lastName) {
                return proxy.toCTCAUniqueId
            }
        }
        return nil
    }
    
    
}

