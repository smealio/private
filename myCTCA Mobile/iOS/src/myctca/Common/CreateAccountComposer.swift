//
//  CreateAccountComposer.swift
//  myctca
//
//  Created by Tomack, Barry on 11/29/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import Foundation
import UIKit
import SafariServices

/**
 Called from the LoginViewController, this class delivers an alert controller
 with links to terms and conditions, privacy polcy, and CTCA Identity
 */
class CreateAccountComposer: NSObject, SFSafariViewControllerDelegate {
    
    var parentVC: UIViewController
    
    init(_ parent: UIViewController) {
        parentVC = parent
        super.init()
    }
    
    // Return a UIAlertController instance
    func configuredCreateAccountController(_ sender: AnyObject) -> UIAlertController {
        
        let alertController = UIAlertController(title: "Create Account", message: "By creating a CTCA ID, you agree to the terms and conditons and the CTCA Privacy Policy.", preferredStyle: .actionSheet)
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel) { (action) in
            
        }
        let continueAction = UIAlertAction(title: "Create CTCA ID", style: .default) { (action) in
            if let url = AuthenticationAPIRouter.openRegistrationLink.asUrl() {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CREATE_ACCOUNT_TAP)
                GenericHelper.shared.launchSafariViewController(withUrl: url, forView: self.parentVC)
            }
        }
        let termsAction = UIAlertAction(title: "Terms and Conditions", style: .default) { (action) in
            if let url = AuthenticationAPIRouter.openTermsOfUseLink.asUrl() {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_TERMS_CONDITIONS_TAP)
                GenericHelper.shared.launchSafariViewController(withUrl: url, forView: self.parentVC)
            }
        }
        let privacyAction = UIAlertAction(title: "Privacy Policy", style: .default) { (action) in
            if let url = AuthenticationAPIRouter.openPrivacyPolicyLink.asUrl() {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_PRIVACY_POLICY_TAP)
                GenericHelper.shared.launchSafariViewController(withUrl: url, forView: self.parentVC)
            }
        }
        
        alertController.addAction(termsAction)
        alertController.addAction(privacyAction)
        alertController.addAction(continueAction)
        alertController.addAction(cancelAction)
        
        alertController.view.tintColor = MyCTCAColor.ctcaGreen.color
        
        // For iPad
        if let popoverController = alertController.popoverPresentationController {
            popoverController.sourceView = sender as? UIView
            popoverController.sourceRect = sender.bounds
        }
        
        return alertController
    }
}
