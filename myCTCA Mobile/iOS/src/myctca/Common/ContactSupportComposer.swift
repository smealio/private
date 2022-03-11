//
//  ContactSupportComposer.swift
//  myCTCA
//
//  Created by Tomack, Barry on 1/27/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit
import CoreTelephony
import SafariServices

/**
 Called from the LoginViewController or MoreContactUsViewController, this class
 delivers an alert controller with links to call or send a message.
 */
class ContactSupportComposer: NSObject {

    var parentVC: UIViewController
    
    var contactSupportMessage: String {
        get {
            var msgString = "Contact Technical Support"
            
            if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
                msgString += "\nVersion \(version)"
            }
            
            return msgString
        }
    }
    
    init(_ parent: UIViewController) {
        parentVC = parent
        super.init()
    }
    
    // Return a UIAlertController instance
    func configuredContactSupportController(_ sender: AnyObject) -> UIAlertController {
        
        let alertController = UIAlertController(title: nil, message: contactSupportMessage, preferredStyle: .actionSheet)
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel) { (action) in
            print("Cancel AlertController")
        }
        let techSupportAction = UIAlertAction(title: "Call", style: .default) { (action) in
            print("Call")
            self.showTechSupportInfo(sender)
        }
        let sendMessageAction = UIAlertAction(title: "Send Message", style: .default) { (action) in
            print("Send Message sender: \(sender)")
            self.sendMessage(sender)
        }
        
        alertController.addAction(techSupportAction)
        alertController.addAction(sendMessageAction)
        alertController.addAction(cancelAction)
        
        alertController.view.tintColor = MyCTCAColor.ctcaGreen.color
        
        // For iPad
        if let popoverController = alertController.popoverPresentationController {
            popoverController.sourceView = sender as? UIView
            popoverController.sourceRect = sender.bounds
        }
        
        return alertController
    }
    
    func showTechSupportInfo(_ sender: AnyObject) {
        
        if let appInfo = AppInfoManager.shared.appInfo {
            self.showTechSupportInfoWith(number: appInfo.techSupportNumber, sender: sender)
        } else {
        
            GenericHelper.shared.showActivityIndicator(message: "Loading...")
            
            AppInfoManager.shared.getTechSupportPhoneNumber() {
                number, status in
                
                GenericHelper.shared.dismissActivityIndicator()
                
                if status == .FAILED {
                    self.showTechSupportInfoWith(number: "", sender: sender)
                } else {
                    self.showTechSupportInfoWith(number: number, sender: sender)
                }
            }
        }
    }
    
    func showTechSupportInfoWith(number:String?, sender: AnyObject) {
            
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_LOGIN_CALL_TAP)
        
        let alertController = UIAlertController(title: "", message: "", preferredStyle: .actionSheet)
        
        let cancelAction = UIAlertAction(title: "Close", style: .cancel) { (action) in
            print("Close Tech Support AlertController")
        }

        if (GenericHelper().capableOfCalling()) {
            let callAction = UIAlertAction(title: "Call Tech Support", style: .default) { (action) in
                if let url = URL(string: "tel://\(number!)") , UIApplication.shared.canOpenURL(url) {
                    if #available(iOS 10.0, *) {
                        UIApplication.shared.open(url, options: convertToUIApplicationOpenExternalURLOptionsKeyDictionary([:]), completionHandler: nil)
                    } else {
                        // Fallback on earlier versions
                        _ = UIApplication.shared.openURL(url)
                    }
                }
            }
            alertController.addAction(callAction)
        }
        alertController.addAction(cancelAction)
        
        alertController.view.tintColor = MyCTCAColor.ctcaGreen.color
        
        let attributedTitle = NSAttributedString(string: "Technical Support", attributes: [
            NSAttributedString.Key.font : UIFont.boldSystemFont(ofSize: 15),
            NSAttributedString.Key.foregroundColor : MyCTCAColor.ctcaGreen.color
            ])
        alertController.setValue(attributedTitle, forKey: "attributedTitle")
        
        let attributedMessage = NSAttributedString(string: "If you need technical support with any aspect of myCTCA, please call \(number!).", attributes: [
            NSAttributedString.Key.font : UIFont.systemFont(ofSize: 15),
            NSAttributedString.Key.foregroundColor : MyCTCAColor.formContent.color
            ])
        alertController.setValue(attributedMessage, forKey: "attributedMessage")
        
        // For iPad
        if let popoverController = alertController.popoverPresentationController {
            popoverController.sourceView = sender as? UIView
            popoverController.sourceRect = sender.bounds
        }
        
        self.parentVC.present(alertController, animated: true, completion: nil)
    }
    
    /**
     Call SendMessageViewController as modal
    */    
    func sendMessage(_ sender: AnyObject) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_LOGIN_SEND_MESSAGE_GOTO_TAP)
        
        print("ContactSupportComposer sendMessage")
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let sendMessageVC = storyboard.instantiateViewController(withIdentifier: "SendMessageController") as! UINavigationController
        
        self.parentVC.present(sendMessageVC, animated: true, completion: nil)
    }
}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
	return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}
