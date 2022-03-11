//
//  NSMutableStringExtension.swift
//  myctca
//
//  Created by Manjunath K on 7/19/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

extension NSMutableAttributedString {
    @discardableResult func bold(_ text: String, fontSize:CGFloat = 12.0) -> NSMutableAttributedString {
        let attrs: [NSAttributedString.Key: Any] = [.font: UIFont(name: "HelveticaNeue-Bold", size: fontSize)!]
        let boldString = NSMutableAttributedString(string:text, attributes: attrs)
        append(boldString)
        
        return self
    }
    
    @discardableResult func normal(_ text: String, fontSize:CGFloat = 12.0) -> NSMutableAttributedString {
        let attrs: [NSAttributedString.Key: Any] = [.font: UIFont(name: "HelveticaNeue", size: fontSize)!]
        let normal = NSMutableAttributedString(string:text, attributes: attrs)
        append(normal)
        
        return self
    }
    
    @discardableResult func italic(_ text: String, fontSize:CGFloat = 12.0) -> NSMutableAttributedString {
        let attrs: [NSAttributedString.Key: Any] = [.font: UIFont(name: "HelveticaNeue-Italic", size: fontSize)!]
        let itlicString = NSMutableAttributedString(string:text, attributes: attrs)
        append(itlicString)
        
        return self
    }
    
}
