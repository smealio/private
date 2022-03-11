//
//  IdentityUser.swift
//  myctca
//
//  Created by Tomack, Barry on 11/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

/* User object returned from CTCA Identity */

struct IdentityUser: Codable {
    var sub: String
    var firstName: String
    var lastName: String
    var middleName: String?
    var userName: String
    var displayName: String
    var role: String?
    var title: String?
    var colorId: String?
    var imageUploaded: String = "false"
    var lastActivity:Date?
    var pWord: String?
    var imageURL: String?
    
    var fullName: String {
        get {
            var fName: String = "\(self.firstName)"
            if (self.middleName != nil) {
                fName += " \(self.middleName!) "
            }
            fName += " \(self.lastName)"
            return fName
        }
    }
    
    var userId:Int {
        get {
            return Int(sub)!
        }
    }
    
    enum CodingKeys: String, CodingKey {
        case sub
        case colorId = "color_id"
        case firstName = "given_name"
        case lastName = "family_name"
        case userName = "email"
        case displayName = "name"
        case imageUploaded = "image_uploaded"
        case role = "role"
        case middleName = "middle_name"
        case imageURL = "picture"
    }
    
    func description() {
        var printUser: String = ""
        printUser += "UserId: \(String(describing: self.userId))"
        printUser += "Username: \(String(describing: self.userName))"
        printUser += "imageURL: \(String(describing: self.imageURL))"
        printUser += "ImageUploaded: \(String(describing: self.imageUploaded))"
        
        print(printUser)
    }
}
