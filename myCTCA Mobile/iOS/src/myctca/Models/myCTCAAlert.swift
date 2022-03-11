//
//  myCTCAAlert.swift
//  myctca
//
//  Created by Manjunath K on 6/9/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

struct myCTCAAlert {
    var image:UIImage?
    var title:String = ""
    var message:String = ""
    var attributeMessage:NSAttributedString?
    
    var leftBtnTitle:String?
    var rightBtnTitle:String?
    
    var leftBtnAction:(()->Void)?
    var rightBtnAction:(()->Void)?
    
    var isDistructive = false
}
