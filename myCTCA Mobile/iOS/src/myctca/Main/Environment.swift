//
//  Environment.swift
//  myctca
//
//  Created by Tomack, Barry on 4/18/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import Foundation

enum PlistKey {
    case CTCAHostServer
    case PortalDataServer
    case ServerProtocol
    case AppSecret
    case ClientSecret
    
    func value() -> String {
        switch self {
        case .CTCAHostServer:
            return "ctca_host_server"
        case .PortalDataServer:
            return "portal_data_server"
        case .ServerProtocol:
            return "server_protocol"
        case .AppSecret:
            return "appSecret"
        case .ClientSecret:
            return "clientSecret"
        }
    }
}

struct Environment {

    fileprivate var infoDict: [String: Any]  {
        get {
            if let dict = Bundle.main.infoDictionary {
                return dict
            }else {
                fatalError("Plist file not found")
            }
        }
    }
    public func configuration(_ key: PlistKey) -> String {
        switch key {
        case .CTCAHostServer:
            return infoDict[PlistKey.CTCAHostServer.value()] as! String
        case .PortalDataServer:
            return infoDict[PlistKey.PortalDataServer.value()] as! String
        case .ServerProtocol:
            return infoDict[PlistKey.ServerProtocol.value()] as! String
        case .AppSecret:
            return infoDict[PlistKey.AppSecret.value()] as! String
        case .ClientSecret:
            return infoDict[PlistKey.ClientSecret.value()] as! String
        }
    }
}
