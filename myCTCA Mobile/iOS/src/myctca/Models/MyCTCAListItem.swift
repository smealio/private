//
//  MyCTCAListItem.swift
//  myctca
//
//  Created by Manjunath K on 1/19/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

struct MyCTCAListItem {
    var title:String?
    var hasDisclouser:Bool?
    var action:(() -> Void) //should be uiaction ios 13 onwards
}
