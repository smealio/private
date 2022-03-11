//
//  AboutBiometricsViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/17/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit
import SafariServices

class AboutBiometricsViewController: UIViewController, UITextViewDelegate, SFSafariViewControllerDelegate {

    @IBOutlet weak var biometricIV: UIImageView!
    @IBOutlet weak var biometricTV: UITextView!
    @IBOutlet weak var biometricPromptLabel: UILabel!
    @IBOutlet weak var biometricSwitch: UISwitch!
    
    @IBOutlet weak var biometricControlView: UIView!

    
    // TouchID
    let ABOUT_TOUCH_ID_TXT: String = "You can choose to sign in to myCTCA™ simply by using your fingerprint. Be sure that only your fingerprint is stored on this device. Once Touch ID™ is enabled, anyone with a stored fingerprint on this device could have access to your data.\n\nYou can turn this feature on or off at anytime here in the myCTCA™ More section.\n\nTo learn more about Touch ID™ and security, please visit Apple's web site.";
    let ABOUT_TOUCH_ID_LINK_TXT: String = "About Touch ID™";
    
    // Face ID
    let ABOUT_FACE_ID_TXT: String = "You can choose to sign in to myCTCA™ simply by using Face ID™. Be sure that only your face is stored on this device. Once Face ID™ is enabled, anyone with a stored face on this device could have access to your data.\n\nYou can turn this feature on or off at anytime here in the myCTCA™ More section.\n\nTo learn more about FaceID™ and security, please visit Apple's web site.";
    let ABOUT_FACE_ID_LINK_TXT: String = "About Face ID™";
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        super.viewWillAppear(animated)
        prepareView()
    }
    
    func prepareView() {
        
        self.title = BiometricManager.shared.getBiometricName(context: nil)
        
        self.biometricTV.delegate = self
        
        self.biometricTV.attributedText = self.getTextDescription()
        self.biometricIV.image = BiometricManager.shared.getBiometricImage(context: nil)
        
        let enableBiometrics = BiometricManager.shared.isBiometricsEnabled()
        
        self.biometricSwitch.setOn(enableBiometrics, animated: false)
        
//        if (self.navigationController != nil) {
//            self.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .stop, target: self, action: #selector(self.closeView(_:)))
//        }
    }
    
    func getTextDescription() -> NSMutableAttributedString  {
        
        var attributedString = NSMutableAttributedString(string: "")
        var biometricTxt = ""
        var biometricLinkTxt = ""
        if let biometricName = BiometricManager.shared.getBiometricName(context: nil) {
            
            if (biometricName == BiometricManager.shared.TOUCH_ID) {
                biometricTxt = "\(ABOUT_TOUCH_ID_TXT)\n\n\(ABOUT_TOUCH_ID_LINK_TXT)"
                biometricLinkTxt = ABOUT_TOUCH_ID_LINK_TXT
            } else {
                biometricTxt = "\(ABOUT_FACE_ID_TXT)\n\n\(ABOUT_FACE_ID_LINK_TXT)"
                biometricLinkTxt = ABOUT_FACE_ID_LINK_TXT
            }
            
            let allTextRange: NSRange = NSRange(location:0, length: biometricTxt.count)
            let linkTextRange: NSRange = (biometricTxt as NSString).range(of: biometricLinkTxt)
            print("linkTextRange: \(linkTextRange)")
            
            attributedString = NSMutableAttributedString(string: biometricTxt)
            attributedString.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "Helvetica Neue", size: 14.0)!, range: allTextRange)
            
            let titleParagraphStyle = NSMutableParagraphStyle()
            titleParagraphStyle.alignment = .left
            
            attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value: titleParagraphStyle, range: allTextRange)
            
            if let nsURL = NSURL.init(string: BiometricManager.shared.getAppleBiometricURL(context: nil)) {
                attributedString.addAttribute(NSAttributedString.Key.link, value: nsURL, range: linkTextRange)
            }
        }
        return attributedString
    }

    // UITextViewDelegate
    @available(iOS, deprecated: 10.0)
    func textView(_ textView: UITextView, shouldInteractWith url: URL, in characterRange: NSRange) -> Bool {
        
        let svc = SFSafariViewController(url: url)
        svc.delegate = self
        svc.preferredBarTintColor = MyCTCAColor.tableHeaderGrey.color
        svc.preferredControlTintColor = MyCTCAColor.ctcaGreen.color
        self.present(svc, animated: true, completion: nil)
        
        return false
    }
    
    @available(iOS 10.0, *)
    func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange, interaction: UITextItemInteraction) -> Bool {
        
        let svc = SFSafariViewController(url: URL)
        svc.delegate = self
        svc.preferredBarTintColor = MyCTCAColor.tableHeaderGrey.color
        svc.preferredControlTintColor = MyCTCAColor.ctcaGreen.color
        self.present(svc, animated: true, completion: nil)
        
        return false
    }
    
    func goToURL(URL: URL) {
        
        if #available(iOS 10.0, *) {
            UIApplication.shared.open(URL, options: convertToUIApplicationOpenExternalURLOptionsKeyDictionary([:]), completionHandler: nil)
        } else {
            // Fallback on earlier versions
            _ = UIApplication.shared.openURL(URL)
        }
    }
    
    @IBAction func switchValueChanged(sender: UISwitch) {
        BiometricManager.shared.setBiometricPreference(enabled: sender.isOn)
        if (sender.isOn == true) {
            BiometricManager.shared.storeLoginParameters()
        } else {
            BiometricManager.shared.removeStoredParameters()
        }
    }
    
    @IBAction func closeView(_ sender: Any) {
        print("CLOSE ABOUT BIOMETRICS VIEW")
        view.endEditing(true)
        dismiss(animated: true, completion: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
	return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}
