//
//  AuthModel.swift
//  myctca
//
//  Created by Manjunath K on 12/16/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct AuthModelProxy : Codable {
   
    var tokenType:String?
    var idToken:String?
    var expiresIn:String?
    var accessToken:String?
    var error:String?
    var errorMsg:String?
    
    enum CodingKeys: String, CodingKey {
        case tokenType = "token_type"
        case idToken = "id_token"
        case expiresIn = "expires_in"
        case accessToken = "access_token"
        case error = "error"
        case errorMsg = "error_description"
    }
    
}

struct AuthModel : Codable {
   
    var tokenType:String?
    var idToken:String?
    var expiresIn:Int?
    var accessToken:String?
    var error:String?
    var errorMsg:String?
    
    enum CodingKeys: String, CodingKey {
        case tokenType = "token_type"
        case idToken = "id_token"
        case expiresIn = "expires_in"
        case accessToken = "access_token"
        case error = "error"
        case errorMsg = "error_description"
    }
    
}
