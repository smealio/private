//
//  MenuOverlay.swift
//  myctca
//
//  Created by Manjunath K on 11/20/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

final class MenuOverlay {
    
    var overlayView: UIView = UIView()
    var contentView: UIView = UIView()
    static let shared: MenuOverlay = MenuOverlay()
    
    private init() {
    }
    
    public func showOverlay(view: UIView, buttons : inout [UIButton], origin:CGFloat) {
        
        //add main overlay
        let mainWidth = view.frame.size.width
        let mainHeight = view.frame.size.height
        let size = (mainWidth > mainHeight) ? mainWidth : mainHeight
        
        self.overlayView.frame = CGRect(x:view.frame.origin.x, y: view.frame.origin.y, width: size, height: size);
        self.overlayView.center = view.center
        self.overlayView.alpha = 1
        self.overlayView.clipsToBounds = true
        
        let btnHeight = 30.0
        let width = 210.0
        let height = (Double(buttons.count+1) * (btnHeight+10.0) ) + (Double(buttons.count+1) - 1.0)
        let x = Double(view.frame.size.width) - (width + 15.0)
        let y = Double(origin)
        
        overlayView.addSubview(contentView)
        
        self.contentView.frame = CGRect(x: x, y: y, width: width, height: height);
        self.contentView.center = view.center
        self.contentView.backgroundColor = .white
        self.contentView.clipsToBounds = true
        self.contentView.layer.cornerRadius = 5
        self.contentView.layer.masksToBounds = true
        self.contentView.layer.borderWidth = 0.5
        self.contentView.layer.borderColor = MyCTCAColor.formLabel.color.cgColor
        
        self.contentView.translatesAutoresizingMaskIntoConstraints = false
        self.contentView.topAnchor.constraint(equalTo: overlayView.topAnchor, constant: origin).isActive = true
        self.contentView.trailingAnchor.constraint(equalTo: overlayView.trailingAnchor, constant: 15.0).isActive = true
        self.contentView.heightAnchor.constraint(equalToConstant: CGFloat(height)).isActive = true
        self.contentView.widthAnchor.constraint(equalToConstant: CGFloat(width)).isActive = true
        
        let canceldBtn = UIButton()
        canceldBtn.addTarget(self, action: #selector(cancelTapped), for: .touchUpInside)
        canceldBtn.setTitle("Cancel", for: .normal)
        buttons.append(canceldBtn)
        
        var buttonIndex = 0.0
        var offsetY = 0.0
        for btn in buttons {
            btn.frame = CGRect(x: 10.0 , y: offsetY + 5.0, width: 160.0, height: btnHeight)
            buttonIndex = buttonIndex + 1.0
            btn.setTitleColor(.black, for: .normal)
            btn.contentHorizontalAlignment = .left
            btn.titleLabel?.font =  UIFont(name: "HelveticaNeue-Regular", size: 15)
            contentView.addSubview(btn)
            
            let image = UIImageView(frame: CGRect(x: 170.0, y: offsetY + 10.0, width: btnHeight-5.0, height: btnHeight-5.0))
            image.image = btn.imageView?.image
            image.contentMode = .center
            contentView.addSubview(image)
            
            image.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: 5.0).isActive = true
            image.heightAnchor.constraint(equalToConstant: CGFloat(btnHeight-5.0)).isActive = true
            image.widthAnchor.constraint(equalToConstant: CGFloat(btnHeight-5.0)).isActive = true

            btn.setImage(nil, for: .normal)
            
            if Int(buttonIndex) != buttons.count {
                offsetY = (buttonIndex * btnHeight) + (buttonIndex * 10.0)
                
                let separatorView = UIView(frame: CGRect(x: 0.0 , y: offsetY, width: width, height: 1.0))
                separatorView.backgroundColor = MyCTCAColor.formLabel.color
                separatorView.alpha = 0.5
                contentView.addSubview(separatorView)
                
                offsetY = offsetY + 1.0
            } else {
                btn.setTitleColor(.red, for: .normal)
            }
        }
        
        view.addSubview(self.overlayView)
        self.overlayView.translatesAutoresizingMaskIntoConstraints = false
        self.overlayView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        self.overlayView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        self.overlayView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        self.overlayView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        
        self.overlayView.quickFadeIn()
    }
    
    @objc private func cancelTapped() {
        fadeOutOverlay()
    }
    
    public func hideOverlay() {
        overlayView.removeFromSuperview()
        contentView.removeFromSuperview()
    }
    
    public func fadeOutOverlay() {
        self.overlayView.quickFadeOut(completion: { (finished: Bool) -> Void in
            self.hideOverlay()
        })
    }
    
}
