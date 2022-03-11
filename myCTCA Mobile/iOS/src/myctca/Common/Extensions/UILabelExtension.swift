//
//  UILabelExtension.swift
//  myctca
//
//  Created by Manjunath K on 11/24/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

extension UILabel {
    
    func setTitleWithHighlight(title:String, text:String) {
        if text != "" {
            let allOccurances = getAllRanges(inString: title, substring: text, options: String.CompareOptions.caseInsensitive)
            if allOccurances.count > 0 {
                let normalNameString = NSMutableAttributedString.init(string: title)
                for range in allOccurances {
                    let nsRange = NSRange(range, in: title)
                    if nsRange.location != NSNotFound {
                        let attributedSubString = NSAttributedString.init(string: NSString(string: title).substring(with: nsRange), attributes: [NSAttributedString.Key.backgroundColor : MyCTCAColor.highlighterColor.color, NSAttributedString.Key.foregroundColor : .white])
                        normalNameString.replaceCharacters(in: nsRange, with: attributedSubString)
                    }
                }
                self.attributedText = normalNameString
            } else {
                let attributedTitle = NSAttributedString(string: title, attributes: nil)
                self.attributedText = attributedTitle
            }
        } else {
            let attributedTitle = NSAttributedString(string: title, attributes: nil)
            self.attributedText = attributedTitle
        }
    }
    
    func getAllRanges(inString:String, substring: String, options: String.CompareOptions = [], locale: Locale? = nil) -> [Range<String.Index>] {
        var ranges: [Range<String.Index>] = []
        while let range = inString.range(of: substring, options: options, range: (ranges.last?.upperBound ?? inString.startIndex)..<inString.endIndex, locale: locale) {
            ranges.append(range)
        }
        return ranges
    }
}
