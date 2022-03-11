//
//  CTCAViewControllerProtocol.swift
//  myctca
//
//  Created by Tomack, Barry on 11/27/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation

import UIKit
import CoreTelephony

/**
 "Decorate view controller with some basic common functions
 */
protocol CTCAViewControllerProtocol {
    
}

extension CTCAViewControllerProtocol {
    
    // MARK: - CTCA Info Alert
    
    func ctcaInfoAlert(title:String,
                       message: String,
                       okaction: UIAlertAction? = nil,
                       otheraction:UIAlertAction? = nil) -> UIAlertController {
        
        let alertController = UIAlertController(title: title,
                                                message: message,
                                                preferredStyle:.alert)
        
        if(okaction != nil) {
            alertController.addAction(okaction!)
        } else {
            let okAction = UIAlertAction(title: "OK",
                                         style: .default,
                                         handler: nil)
            alertController.addAction(okAction)
        }
        
        if (otheraction != nil) {
            alertController.addAction(otheraction!)
        }
        
        return alertController
    }
    
    // MARK: - Activity Indicator Block
    
    func showActivityIndicator(view: UIView, message: String? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.showOverlay(view: view, message: message)
        })
    }
    
    func dismissActivityIndicator(completion: (() -> Swift.Void)? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.hideOverlay()
            
            if (completion != nil) {
                completion!()
            }
        })
    }
    
    func fadeOutActivityIndicator(completion: (() -> Swift.Void)? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.fadeOutOverlay()
            
            if (completion != nil) {
                completion!()
            }
        })
    }
    
    // MARK: Localization
    // Localization
    
    func getFormattedStringFromNumber(_ number: Double) -> String{
        let numberFormatter = NumberFormatter()
        numberFormatter.numberStyle = .decimal
        return numberFormatter.string(from: number as NSNumber)!
    }
    
    func getFormattedStringFromDate(_ aDate: Date) -> String{
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .medium
        return dateFormatter.string(from: aDate)
    }
    
    // Mark: Screen Width (Swift 4)
    // Screen width.
    public var screenWidth: CGFloat {
        return UIScreen.main.bounds.width
    }
    
    // Screen height.
    public var screenHeight: CGFloat {
        return UIScreen.main.bounds.height
    }
    
}
