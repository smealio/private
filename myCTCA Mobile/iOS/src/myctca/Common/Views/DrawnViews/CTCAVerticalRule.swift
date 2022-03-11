//
//  CTCAVerticalRule.swift
//  myctca
//
//  Created by Tomack, Barry on 11/27/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

@IBDesignable

/**
 Draws a vertical rule at the center of its view container
 */
class CTCAVerticalRule: UIView {
    
    @IBInspectable var lineWidth: CGFloat = 1.0
    @IBInspectable var lineColor: UIColor = UIColor.black
    @IBInspectable var withEndTicks: Bool = false
    
    override func draw(_ rect: CGRect) {
        
        // X Center
        let xCenter = self.frame.size.width/2
        // Drawing code
        let path = UIBezierPath()
        path.move(to: CGPoint(x: xCenter, y: 0.0))
        path.addLine(to: CGPoint(x: xCenter, y: self.frame.size.height))
        
        lineColor.setStroke()
        path.lineWidth = lineWidth + 0.5
        
        if (withEndTicks) {
            drawEndTicks()
        }
        path.stroke()
        super.draw(rect)
    }
    
    func drawEndTicks() {
        // Draw end tick at top end
        let leftPath = UIBezierPath()
        leftPath.move(to: CGPoint(x: 0.0, y: 0.0))
        leftPath.addLine(to: CGPoint(x: self.frame.size.width, y: 0.0))
        
        lineColor.setStroke()
        leftPath.lineWidth = lineWidth + 0.5
        leftPath.stroke()
        
        // Draw end tick at bottom end
        let rightPath = UIBezierPath()
        rightPath.move(to: CGPoint(x: 0.0, y: self.frame.size.height))
        rightPath.addLine(to: CGPoint(x: self.frame.width, y: self.frame.size.height))
        
        lineColor.setStroke()
        rightPath.lineWidth = lineWidth + 0.5
        rightPath.stroke()
    }
}
