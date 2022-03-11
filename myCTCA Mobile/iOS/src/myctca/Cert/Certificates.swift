//
//  Certificates.swift
//  myctca
//
//  Created by Manjunath K on 12/1/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

struct Certificates {
    static let myCTCACert: SecCertificate = Certificates.certificate(filename: MyCTCAConstants.FileNameConstants.SSLCertificateName)
  
    private static func certificate(filename: String) -> SecCertificate {
        
        let filePath = Bundle.main.path(forResource: filename, ofType: "cer")!
        let data = try! Data(contentsOf: URL(fileURLWithPath: filePath))
        let certificate = SecCertificateCreateWithData(nil, data as CFData)!
        
        return certificate
  }
}
