//
//  myCTCAColor.swift
//  myctca
//
//  Created by Tomack, Barry on 11/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

enum MyCTCAColor {
    
    case ctcaGreen, ctcaGrey, ctcaGrey75, ctcaGrey50, ctcaGrey25, ctcaSectionBackground, ctcaSecondGreen, superLightGrey, connectionsViewLightGrey, connectionsViewMidGrey, tableHeaderGrey, overlayBackground, formLabel, formContent, formLines, darkRedWarning, blueText, placeHolder, calanderOverlayBackground, highlighterColor, thBGColor, alertRedColor, ctcaNavBarColor
    
    var color: UIColor {
        switch self {
        case .ctcaGreen:
            //return UIColor(red: 0x03 / 255.0, green: 0x56 / 255.0, blue: 0x42 / 255.0, alpha: 1.0)
            return UIColor(red: 41/255, green: 104/255, blue: 83/255, alpha: 1.0)
        case .ctcaGrey:
            return UIColor(red: 0x56 / 255.0, green: 0x5A / 255.0, blue: 0x5C / 255.0, alpha: 1.0)
        case .ctcaGrey75:
            return UIColor(red: 0x56 / 255.0, green: 0x5A / 255.0, blue: 0x5C / 255.0, alpha: 0.75)
        case .ctcaGrey50:
            return UIColor(red: 0x56 / 255.0, green: 0x5A / 255.0, blue: 0x5C / 255.0, alpha: 0.5)
        case .ctcaGrey25:
            return UIColor(red: 0x56 / 255.0, green: 0x5A / 255.0, blue: 0x5C / 255.0, alpha: 0.25)
        case .ctcaSectionBackground:
            return UIColor(red: 239/255.0, green: 239/255.0, blue: 245/255.0, alpha: 1.0)
        case .ctcaSecondGreen:
//            return UIColor(red: 0x0C / 255.0, green: 0x7C / 255.0, blue: 0x3B / 255.0, alpha: 1.0)
            return UIColor(red: 0x03 / 255.0, green: 0x56 / 255.0, blue: 0x42 / 255.0, alpha: 1.0)
        case .superLightGrey:
            return UIColor(red: 0xE6 / 255.0, green: 0xE6 / 255.0, blue: 0xE9 / 255.0, alpha: 1.0)
        case .connectionsViewLightGrey:
            return UIColor(red: 0xF3 / 255.0, green: 0xF5 / 255.0, blue: 0xF3 / 255.0, alpha: 1.0)
        case .connectionsViewMidGrey:
            return UIColor(red: 0xA3 / 255.0, green: 0xAF / 255.0, blue: 0xA3 / 255.0, alpha: 1.0)
        case .tableHeaderGrey:
            return UIColor(red: 0xF3 / 255.0, green: 0xF5 / 255.0, blue: 0xF3 / 255.0, alpha: 1.0)
        case .overlayBackground:
            return UIColor(red: 0xFF / 255.0, green: 0xFF / 255.0, blue: 0xFF / 255.0, alpha: 0.9)
        case .calanderOverlayBackground:
            return UIColor(red: 0xFF / 255.0, green: 0xFF / 255.0, blue: 0xFF / 255.0, alpha: 0.95)
        case .formLabel:
            return UIColor(red: 0x6C / 255.0, green: 0x76 / 255.0, blue: 0x6C / 255.0, alpha: 1.0)
        case .formContent:
            return UIColor(red: 0x21 / 255.0, green: 0x21 / 255.0, blue: 0x21 / 255.0, alpha: 1.0)
        case .formLines:
            return UIColor(red: 0xD6 / 255.0, green: 0xD5 / 255.0, blue: 0xD9 / 255.0, alpha: 1.0)
        case .darkRedWarning:
            return UIColor(red: 0x72 / 255.0, green: 0x1C / 255.0, blue: 0x24 / 255.0, alpha: 1.0)
        case .blueText:
            return UIColor(red: 0x00 / 255.0, green: 0x7F / 255.0, blue: 0xFA / 255.0, alpha: 1.0)
        case .placeHolder:
            return UIColor(red: 0.78, green: 0.78, blue: 0.80, alpha: 1.0)
        case .highlighterColor:
            return UIColor(red: 0.21, green: 0.49, blue: 0.71, alpha: 1.0)
        case .thBGColor:
            return UIColor(red: 0.192, green: 0.192, blue: 0.192, alpha: 1.0)
            //RGB(49,49,49)
        case .alertRedColor:
            //rgba(240, 63, 63, 1)
            return UIColor(red: 0.9418, green: 0.247,  blue: 0.247, alpha:1.0)
        case .ctcaNavBarColor:
            return UIColor(red: 0.969, green: 0.969, blue: 0.969, alpha: 1.0)
        }
    }
}

enum ThemeName: String {
    case modern = "Modern"
    case dark = "Dark"
    case contrast = "Contrast"
}

struct Theme {
    
    var primaryColor: UIColor!
    var secondaryColor: UIColor!
    var highlightColor: UIColor!
    
    init(theme name: ThemeName ) {
        switch name {
        case .modern:
            primaryColor = MyCTCAColor.connectionsViewLightGrey.color
            secondaryColor = MyCTCAColor.connectionsViewMidGrey.color
            highlightColor = MyCTCAColor.connectionsViewMidGrey.color
        case .dark:
            break
        case .contrast:
            break
        }
    }
}

extension UIColor {
    convenience init(hexString: String) {
        
        var cString:String = hexString.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        
        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }
        
        var hex:UInt32 = 0
        Scanner(string: cString).scanHexInt32(&hex)
        
        let red   = CGFloat((hex >> 16) & 0xff) / 255.0
        let green = CGFloat((hex >> 8) & 0xff) / 255.0
        let blue  = CGFloat((hex) & 0xff) / 255.0
        
        self.init(red: red, green: green, blue: blue, alpha: 1.0)
    }
    
    func toHexString() -> String {
        var r:CGFloat = 0
        var g:CGFloat = 0
        var b:CGFloat = 0
        var a:CGFloat = 0
        
        getRed(&r, green: &g, blue: &b, alpha: &a)
        
        let rgb:Int = (Int)(r*255)<<16 | (Int)(g*255)<<8 | (Int)(b*255)<<0
        
        return  String(format:"#%06x", rgb)
    }
}
