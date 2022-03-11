//
//  RadialGradientLayer.swift
//  myctca
//
//  Created by Manjunath K on 6/7/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

final class RadialGradientLayer: CALayer {

    var centerColor: CGColor?
    var edgeColor: CGColor?

    init(centerColor: UIColor, edgeColor: UIColor) {
        super.init()

        self.centerColor = centerColor.cgColor
        self.edgeColor = edgeColor.cgColor
                
        needsDisplayOnBoundsChange = true
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func draw(in ctx: CGContext) {
        ctx.saveGState()
        let gradient = CGGradient(colorsSpace: CGColorSpaceCreateDeviceRGB(),
                                  colors: [centerColor, edgeColor] as CFArray,
                                  locations: [0.0, 1.0])

        let gradCenter = CGPoint(x: bounds.midX, y: bounds.midY)
        let gradRadius = min(bounds.width/2, bounds.height/2)

        ctx.drawRadialGradient(gradient!,
                               startCenter: gradCenter,
                               startRadius: 0.0,
                               endCenter: gradCenter,
                               endRadius: gradRadius,
                               options: .drawsAfterEndLocation)
    }
}
