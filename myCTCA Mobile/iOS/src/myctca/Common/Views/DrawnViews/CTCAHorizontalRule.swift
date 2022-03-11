//
//  CTCAHorizontalRule.swift
//  SymptomInventory
//
//  Created by Tomack, Barry on 3/1/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

@IBDesignable

/**
 Draws a horizontal rule at the boottome of this view container
 */
class CTCAHorizontalRule: UIView {

    @IBInspectable var lineWidth: CGFloat = 1.0
    @IBInspectable var lineColor: UIColor = UIColor.black
    @IBInspectable var withEndTicks: Bool = false
    
    override func draw(_ rect: CGRect) {
        
        // Drawing code
        let path = UIBezierPath()
        path.move(to: CGPoint(x: 0.0, y: self.frame.size.height))
        path.addLine(to: CGPoint(x: self.frame.width, y: self.frame.size.height))
        
        lineColor.setStroke()
        path.lineWidth = lineWidth + 0.5
        
        if (withEndTicks) {
            drawEndTicks()
        }
        path.stroke()
        super.draw(rect)
    }
    
    func drawEndTicks() {
        // Draw end tick at left end
        let leftPath = UIBezierPath()
        leftPath.move(to: CGPoint(x: 0.0, y: 0.0))
        leftPath.addLine(to: CGPoint(x: 0.0, y: self.frame.size.height))
        
        lineColor.setStroke()
        leftPath.lineWidth = lineWidth + 0.5
        leftPath.stroke()
        
        // Draw end tick at right end
        let rightPath = UIBezierPath()
        rightPath.move(to: CGPoint(x: self.frame.width, y: 0.0))
        rightPath.addLine(to: CGPoint(x: self.frame.width, y: self.frame.size.height))
        
        lineColor.setStroke()
        rightPath.lineWidth = lineWidth + 0.5
        rightPath.stroke()
    }
}
