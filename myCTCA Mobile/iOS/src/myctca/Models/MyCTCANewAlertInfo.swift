//
//  MyCTCANewAlertInfo.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 02/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

struct MyCTCANewAlertInfo {
    var title:String
    var message:String
    var state:Bool
    var buttonAction:(() -> Void)?
}
