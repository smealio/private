//
//  StepIndicator.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 23/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import UIKit
import CoreGraphics
import simd

@IBDesignable
class CircleView: UIView {
    public var radius: CGFloat
    let diaYetToPass = 12.0
    let diaPassesd = 8.0
    
    var passedCircleImageView:UIImageView?
    var yetToPassCircleImageView:UIImageView?
    var outerCircleImageView:UIImageView?
    
    init(radius: CGFloat, isFirst:Bool = false) {
        self.radius = radius
        let diameter = radius * 2
        let containingSquare = CGRect(x: 0, y: 0, width: diameter, height: diameter)
        super.init(frame: containingSquare)
        makeCircle(outerFrame: containingSquare, first:isFirst)
        widthAnchor.constraint(equalToConstant: diameter).isActive = true
        heightAnchor.constraint(equalToConstant: diameter).isActive = true
    }
    
    override init(frame: CGRect) {
        print("init circle with frame")
        radius = min(frame.size.height, frame.size.width) / 2
        super.init(frame: frame)
        makeCircle(outerFrame: frame, first:false)
    }
    
    required init?(coder aDecoder: NSCoder) {
        radius = 0
        super.init(coder: aDecoder)
        radius = min(frame.size.height, frame.size.width) / 2
        makeCircle(outerFrame: .zero, first:false)
    }
    
    func makeCircle(outerFrame: CGRect, first:Bool) {
        passedCircleImageView = UIImageView(frame: CGRect(x: 6, y: 6, width: diaPassesd, height: diaPassesd))
        passedCircleImageView?.image = UIImage(named: "purple_dot")
        
        yetToPassCircleImageView = UIImageView(frame: CGRect(x: 4, y: 4, width: diaYetToPass, height: diaYetToPass))
        yetToPassCircleImageView?.image = UIImage(named: "purple_dot_border_small")
        
        outerCircleImageView = UIImageView(frame: frame)
        outerCircleImageView?.image = UIImage(named: "purple_dot_border")

        self.addSubview(outerCircleImageView!)
        self.addSubview(passedCircleImageView!)
        self.addSubview(yetToPassCircleImageView!)
        
        if first {
            setHighlighted()
        } else {
            setYetToPass()
        }
    }
    
    func setYetToPass() {
        outerCircleImageView?.isHidden = true
        yetToPassCircleImageView?.isHidden = false
        passedCircleImageView?.isHidden = true
    }
    
    func setHighlighted() {
        outerCircleImageView?.isHidden = false
        yetToPassCircleImageView?.isHidden = true
        passedCircleImageView?.isHidden = false
    }
    
    func setPassed() {
        outerCircleImageView?.isHidden = true
        yetToPassCircleImageView?.isHidden = true
        passedCircleImageView?.isHidden = false
    }
}

@IBDesignable
class StepIndicator: UIView {
    private let stack = StackView<CircleView>(frame: .zero)
    
    @IBInspectable
    var stepCount: Int {
        didSet {
            if stepCount > oldValue {
                for index in oldValue..<stepCount {
                    stack.addArrangedSubview(createStepView(index))
                }
            } else {
                if let index = highlightIndex, index>=stepCount {
                    highlightIndex = nil
                }
                for index in (stepCount..<oldValue).reversed() {
                    let view = stack[stackedView: index]
                    stack.removeArrangedSubview(view)
                    view.removeFromSuperview()
                }
            }
        }
    }
    
    /// The index of the highlighted step. Nil if no step is currently highlighted
    var highlightIndex: Int? {
        willSet {
            if let oldIndex = highlightIndex {
                dim(circle: stack[stackedView: oldIndex])
            }
            if let index = newValue {
                let container = stack[stackedView: index]
                highlight(circle: container)
            }
        }
    }
    
    /// An alias of `highlightIndex` but without the Optional type for use in Interface Builder.
    /// The index of the highlighted step. `-1` if no step is currently highlighted.
    @IBInspectable
    var cursor: Int {
        get { return highlightIndex ?? -1 }
        set {
            highlightIndex = stack.arrangedSubviews.indices.contains(newValue) ? newValue : nil
        }
    }
    
    @IBInspectable
    var highlightRadius: CGFloat = 15 {
        didSet {
            if let index = highlightIndex {
                highlight(circle: stack[stackedView: index])
            }
        }
    }
    
    @IBInspectable
    var defaultRadius:   CGFloat = 10 {
        didSet {
            var indexRange = Array(stack.arrangedSubviews.indices)
            if let index = highlightIndex {
                indexRange.remove(at: index)
            }
            for index in indexRange {
                dim(circle: stack[stackedView: index])
            }
        }
    }
    
    init(stepCount: Int) {
        self.stepCount = stepCount
        super.init(frame: .zero)
        addSubViews()
    }
    
    override init(frame: CGRect) {
        stepCount = 5
        super.init(frame: frame)
        addSubViews()
    }
    
    required init?(coder aDecoder: NSCoder) {
        stepCount = 5
        super.init(coder: aDecoder)
        addSubViews()
    }

    private func createStepView(_ index:Int) -> CircleView {
        if index == 0 {
            return CircleView(radius: defaultRadius, isFirst: true)
        } else {
            return CircleView(radius: defaultRadius)
        }
    }
    
    private func highlight(circle: CircleView) {
        //circle.radius = self.highlightRadius
        circle.setHighlighted()
    }
    
    private func dim(circle: CircleView) {
        //circle.radius = self.defaultRadius
        //circle.setYetToPasS()
    }
    
    private func addSubViews() {
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.distribution = .equalCentering
        stack.alignment = .center
        addSubview(stack)
        
        let line = UIView(frame: .zero)
        line.translatesAutoresizingMaskIntoConstraints = false
        stack.addSubview(line)
                
        stack.leadingAnchor.constraint(equalToSystemSpacingAfter: leadingAnchor, multiplier: 0).isActive = true
        stack.trailingAnchor.constraint(equalToSystemSpacingAfter: trailingAnchor, multiplier: 0).isActive = true
        stack.topAnchor.constraint(equalToSystemSpacingBelow: topAnchor, multiplier: 0).isActive = true
        stack.bottomAnchor.constraint(equalToSystemSpacingBelow: bottomAnchor, multiplier: 0).isActive = true

        for index in 0..<stepCount {
            let circle = createStepView(index)
            stack.addArrangedSubview(circle)
        }
        
        line.backgroundColor = UIColor.ctca_gray40
        line.heightAnchor.constraint(equalToConstant: 2).isActive = true
        line.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 5).isActive = true
        line.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -5).isActive = true
        line.centerYAnchor.constraint(equalToSystemSpacingBelow: stack.centerYAnchor, multiplier: 0).isActive = true
    }
    
    func moveForward() {
        let current =  stack[stackedView: highlightIndex!]
        current.setPassed()
        highlightIndex! += 1
    }
    
    func moveBack() {
        let current =  stack[stackedView: highlightIndex!]
        current.setYetToPass()
        highlightIndex! -= 1
    }
}

class StackView<Element>: UIStackView {
    var stackedViews: [Element] {
        return arrangedSubviews.map {$0 as! Element}
    }
    
    subscript (stackedView index: Int) -> Element {
        return arrangedSubviews[index] as! Element
    }
}
