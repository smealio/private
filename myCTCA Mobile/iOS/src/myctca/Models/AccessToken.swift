//
//  AccessToken.swift
//  myCTCA
//
//  Created by Tomack, Barry on 11/3/16.
//  Copyright © 2016 CTCA. All rights reserved.
//

import Foundation

struct AccessToken {
    
    static let tokenDateFormat = "MM/dd/yy h:mm a Z"
    
    var tokenType: String
    var expiresIn: UInt
    var accessToken: String
    var createdOn: Date
    
    init(accessToken: String, expiresIn: UInt, tokenType:String) {
        
        self.accessToken = accessToken
        self.expiresIn = expiresIn
        self.tokenType = tokenType
        
        self.createdOn = Date()
    }
    
    init(model: AuthModel) {
        if let accessToken = model.accessToken {
            self.accessToken = accessToken
        } else {
            self.accessToken = ""
        }
        if let expiresIn = model.expiresIn {
            self.expiresIn = UInt(expiresIn)
        } else {
            self.expiresIn = 0
        }
        if let tokenType = model.tokenType {
            self.tokenType = tokenType
        } else {
            self.tokenType = ""
        }
        
        self.createdOn = Date()
    }
    
    init(model: AuthModelProxy) {
        if let accessToken = model.accessToken {
            self.accessToken = accessToken
        } else {
            self.accessToken = ""
        }
        if let expiresIn = model.expiresIn, let expires = UInt(expiresIn) {
            self.expiresIn = expires
        } else {
            self.expiresIn = 0
        }
        if let tokenType = model.tokenType {
            self.tokenType = tokenType
        } else {
            self.tokenType = ""
        }
        
        self.createdOn = Date()
    }
    
    func isExpired() -> Bool {
        
        let elapsed:Double = Date().timeIntervalSince(createdOn)
        
        if (Int(elapsed) > expiresIn) {
            return false
        }
        
        return true
    }
    
    /**
     If token is within 5 minutes of expiring
     Didn't end up using this method but might come in handy - BT 4/25/18
     */
    func willExpireSoon() -> Bool {
        let elapsed:Double = Date().timeIntervalSince(createdOn)
        
        if (Int(elapsed) > expiresIn - 300) {
            return true
        }
        
        return false
    }
    
    func isEmpty() -> Bool {
        
        return accessToken.isEmpty
    }
    
    /**
     Might consider making this struct Codable but it small enough where it doesn't "have to" be- BT 4/25/18
     */
    func asJSONString() -> String {
        
        var accessTokenDict = [String: String]();
        
        accessTokenDict["accessToken"] = accessToken
        accessTokenDict["tokenType"] = tokenType
        accessTokenDict["expiresIn"] = String(expiresIn)
        
        let dateformatter = DateFormatter()
        dateformatter.dateFormat = AccessToken.tokenDateFormat
        let created = dateformatter.string(from: createdOn)
        accessTokenDict["createdOn"] = created
        
        var jsonData: Data = Data()
        
        do {
            jsonData = try JSONSerialization.data(withJSONObject: accessTokenDict, options: .prettyPrinted)
        } catch {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, ["exception": error.localizedDescription, "method": "AccessToken:asJSONString"])
            print(error.localizedDescription)
        }

        let jsonString: String = String(data: jsonData, encoding: String.Encoding.utf8)!
        
        return jsonString
    }
    
    static func fromTokenString(_ tokenString: String) -> AccessToken {
        
        let data = tokenString.data(using: String.Encoding.utf8, allowLossyConversion: false)
        
        var json = [String: Any]()
        if let jsonData = data {
            // Will return an object or nil if JSON decoding fails
            do {
                json = try JSONSerialization.jsonObject(with: jsonData, options: .allowFragments) as! [String:Any]
            } catch {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.EXCEPTION_SYSTEM_THROWN, ["exception": error.localizedDescription, "method": "AccessToken:fromTokenString"])
                print("Error converting tokenString to AccessToken: \(error)")
            }
        }
        
        let aToken = json["accessToken"] as! String
        let expiresString = json["expiresIn"] as! String
        let expiresInt = UInt(expiresString)
        let tType = json["tokenType"] as! String
        
        var accessToken = AccessToken(accessToken: aToken, expiresIn: expiresInt!, tokenType: tType)
        
        let createdString = json["createdOn"] as! String
        
        let dateformatter = DateFormatter()
        dateformatter.dateFormat = AccessToken.tokenDateFormat
        accessToken.createdOn = dateformatter.date(from: createdString)!
        
        return accessToken
    }
    
    static func validJSON(_ json: [String:AnyObject]) -> Bool {
        
        guard let _ = json["access_token"] as? String else {
            return false
        }
        guard let _ = json["expires_in"] as? String else {
            return true
        }
        guard let _ = json["token_type"] as? String else {
            return false
        }
        
        return true
    }
    
}
