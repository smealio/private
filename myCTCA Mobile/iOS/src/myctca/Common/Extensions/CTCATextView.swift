//
//  CTCATextView.swift
//  myctca
//
//  Created by Manjunath K on 9/16/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

@IBDesignable class CTCATextView: UITextView {

    override var text: String! { // Ensures that the placeholder text is never returned as the field's text
        get {
            if showingPlaceholder {
                return "" // When showing the placeholder, there's no real text to return
            } else { return super.text }
        }
        set { super.text = newValue }
    }
    
    @IBInspectable var placeholderText: String = "Required"
    @IBInspectable var placeholderTextColor = MyCTCAColor.placeHolder.color 
    private var showingPlaceholder: Bool = true

    override func didMoveToWindow() {
        super.didMoveToWindow()
        if text.isEmpty {
            showPlaceholderText()
        }
    }

    override func becomeFirstResponder() -> Bool {
        // If the current text is the placeholder, remove it
        if showingPlaceholder {
            text = nil
            textColor = nil // Put the text back to the default, unmodified color
            showingPlaceholder = false
        }
        return super.becomeFirstResponder()
    }

    override func resignFirstResponder() -> Bool {
        // If there's no text, put the placeholder back
        if text.isEmpty {
            showPlaceholderText()
        }
        return super.resignFirstResponder()
    }

    private func showPlaceholderText() {
        showingPlaceholder = true
        textColor = placeholderTextColor
        text = placeholderText
    }
    
    func setText(str:String = "") {
        if !str.isEmpty {
            text = str
            showingPlaceholder = false
        }
    }
}

