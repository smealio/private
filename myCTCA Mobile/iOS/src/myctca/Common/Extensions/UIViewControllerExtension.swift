//
//  UIViewControllerExtension.swift
//  myctca
//
//  Created by Manjunath K on 8/27/20.
//  Copyright © 2020 CTCA. All rights reserved.
//


import Foundation
import UIKit

extension UIViewController {
    
    @objc func loadViewData() {
        //Default implementaion
    }

}

extension UIPageViewController {
     var isPagingEnabled: Bool {
        get {
           var isEnabled: Bool = true
           for view in view.subviews {
               if let subView = view as? UIScrollView {
                   isEnabled = subView.isScrollEnabled
               }
           }
           return isEnabled
       }
       set {
           for view in view.subviews {
               if let subView = view as? UIScrollView {
                   subView.isScrollEnabled = newValue
               }
           }
       }
   }
}
