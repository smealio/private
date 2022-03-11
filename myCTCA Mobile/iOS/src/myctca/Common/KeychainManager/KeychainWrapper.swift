//
//  KeychainWrapper.swift
//  CTCAConnect
//
//  Created by Tomack, Barry on 11/7/16.
//  Copyright © 2016 CTCA. All rights reserved.
//

import Foundation

protocol KeychainAccess {
    
    func createData(account:String, data:[String: AnyObject]) throws
    
    func readData(account:String) throws -> [String: AnyObject]?
    
    func updateData(account:String, data:[String: AnyObject]) throws
    
    func deleteData(account:String) throws
}

struct KeychainWrapper: KeychainAccess {
    
    // MARK: Generic CRUD for Keychain
    func createData(account:String, data:[String: AnyObject]) throws {
        var query: [String: AnyObject] = KeychainWrapper.keychainQuery(account: account)

        do {
            let encodedData = try NSKeyedArchiver.archivedData(withRootObject: data, requiringSecureCoding: false)
            
            query[kSecValueData as String] = encodedData as AnyObject
            query[kSecAttrAccessible as String] = kSecAttrAccessibleWhenUnlocked
            
        } catch let error {
            throw error
        }
        //let encodedData = NSKeyedArchiver.archivedData(withRootObject: data)

        let resultCode:OSStatus = SecItemAdd(query as CFDictionary, nil)
        
        if let err = KeychainWrapper.mapErrorCode(result: resultCode) {
            throw err
        } else {
            print("KeychainWrapper: Item added successfully")
        }
    }
    
    func readData(account:String) throws -> [String: AnyObject]?  {
        
        var query: [String: AnyObject] = KeychainWrapper.keychainQuery(account: account)
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        //query[kSecReturnAttributes as String] = kCFBooleanTrue
        query[kSecReturnData as String] = kCFBooleanTrue
        
        var result: AnyObject?
        let status = withUnsafeMutablePointer(to: &result) {
            SecItemCopyMatching(query as CFDictionary, UnsafeMutablePointer($0))
        }
        
        if let err = KeychainWrapper.mapErrorCode(result: status) {
            throw err
        }
        
        guard let resultVal = result as? Data, let data = NSKeyedUnarchiver.unarchiveObject (with: resultVal as Data) else {
            print("KeychainWrapper: Error parsing keychain result: \(status)")
            return nil
        }
        return data as? [String: AnyObject]
    }
    
    func updateData(account:String, data:[String: AnyObject]) throws {
        var query: [String: AnyObject] = KeychainWrapper.keychainQuery(account: account)
        var updateAttributes = [String:AnyObject]()
        do {
            let encodedData = try NSKeyedArchiver.archivedData(withRootObject: data, requiringSecureCoding: false)
            
            query[kSecValueData as String] = encodedData as AnyObject
            query[kSecAttrAccessible as String] = kSecAttrAccessibleWhenUnlocked
            
            
            updateAttributes = [
                kSecValueData as String: encodedData as AnyObject
            ]
            
        } catch let error {
            throw error
        }
        
        
        if SecItemCopyMatching(query as CFDictionary, nil) == noErr {
            let updateStatus = SecItemUpdate(query as CFDictionary, updateAttributes as CFDictionary)
            if let err = KeychainWrapper.mapErrorCode(result: updateStatus) {
                throw err
            } else {
                print("KeychainWrapper: Successfully updated data")
            }
        }
    }
    
    func deleteData(account:String) throws {
        
        let query: [String: AnyObject] = KeychainWrapper.keychainQuery(account: account)
        
        let status = SecItemDelete(query as CFDictionary)
        
        if let err = KeychainWrapper.mapErrorCode(result: status) {
            throw err
        } else {
            print("KeychainWrapper: Successfully deleted data")
        }
    }
    
    // MARK: Convenience
    private static func keychainQuery(account: String? = nil) -> [String : AnyObject] {
        
        var query = [String : AnyObject]()
        query[kSecClass as String] = kSecClassGenericPassword
        query[kSecAttrService as String] = KeychainConfiguration.serviceName as AnyObject?
        
        if let acct = account {
            query[kSecAttrAccount as String] = acct as AnyObject?
        }
        
        if let accessGroup = KeychainConfiguration.accessGroup {
            query[kSecAttrAccessGroup as String] = accessGroup as AnyObject?
        }
        print("KeychainWrapper keychainQuery query: \(query)")
        return query
    }
    
    static func mapErrorCode(result:OSStatus) -> KeychainError? {
        
        switch result {
        case 0:
            return nil
        case -4:
            return .operationUnimplemented
        case -50:
            return .invalidParam
        case -108:
            return .memoryAllocationFailure
        case -25291:
            return .trustResultsUnavailable
        case -25293:
            return .authFailed
        case -25299:
            return .duplicateItem
        case -25300:
            return .itemNotFound
        case -25308:
            return .serverInteractionNotAllowed
        case -26275:
            return .decodeError
        default:
            return .unknown(result.hashValue)
        }
    }
}

enum KeychainError:Error {
    case invalidInput                       // If the value cannot be encoded as NSData
    case operationUnimplemented             // -4       | errSecUnimplemented
    case invalidParam                       // -50      | errSecParam
    case memoryAllocationFailure            // -108     | errSecAllocate
    case trustResultsUnavailable            // -25291   | errSecNotAvailable
    case authFailed                         // -25293   | errSecAuthFailed
    case duplicateItem                      // -25299   | errSecDuplicateItem
    case itemNotFound                       // -25300   | errSecItemNotFound
    case serverInteractionNotAllowed        // -25308   | errSecInteractionNotAllowed
    case decodeError                        // - 26275  | errSecDecode
    case unknown(Int)                       // Another error code not defined
}

