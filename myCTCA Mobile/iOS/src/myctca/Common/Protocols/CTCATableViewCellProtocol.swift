//
//  CTCATableViewCellProtocol.swift
//  myctca
//
//  Created by Tomack, Barry on 12/13/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation
import UIKit

@objc protocol CTCATableViewCellProtocol {

    func getData() -> String
    @objc optional func isValid() -> Bool
}

