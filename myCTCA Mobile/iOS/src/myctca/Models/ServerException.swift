//
//  ServerException.swift
//  myctca
//
//  Created by Manjunath K on 7/16/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class ServerExceptionErrorMessage: Codable {
    var errorMessage = ""
    enum CodingKeys: String, CodingKey {
        case errorMessage
    }
}

class ServerExceptionError: Codable {
    var value = ""
    var errors:[ServerExceptionErrorMessage] = [ServerExceptionErrorMessage]()
    
    enum CodingKeys: String, CodingKey {
        case value, errors
    }
}

class ServerException: Codable {
    var exceptionFor = ""
    var exception:ServerExceptionError?
    
    enum CodingKeys: String, CodingKey {
        case exceptionFor = "uniqueId"
        case exception = "creationTime"
    }
}

