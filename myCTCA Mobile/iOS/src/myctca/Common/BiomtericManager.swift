//
//  BiometricManager.swift
//  myCTCA
//
//  Created by Tomack, Barry on 9/13/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit
import LocalAuthentication

/**
 Class to manage Touch Id and Face ID
 */

class BiometricManager {
    
    static let shared = BiometricManager()
    
    let TOUCH_ID: String = "Touch ID"
    let FACE_ID: String = "Face ID"
    
    let ABOUT_APPLE_TOUCH_ID = "https://support.apple.com/en-us/HT204587"
    let ABOUT_APPLE_FACE_ID  = "https://support.apple.com/en-us/HT208108"
    
    var loginParameters:LoginParameters = LoginParameters()
    
    lazy var myKeychainManager: KeychainManager  = KeychainManager.shared
    
    //This prevents others from using the default '()' initializer for this class.
    private init() {
        disableBiometricsByDefault()
    }
    
    // Is device Biometrically capable?
    func biometricCapable (context: LAContext? = nil) -> Bool {
        
        let laContext: LAContext = (context == nil) ? LAContext() : context!
        return laContext.canEvaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics, error: nil)
    }
    
    // Are biometrics enabled for app?
    func isBiometricsEnabled() -> Bool {
        return UserDefaults.standard.bool(forKey: MyCTCAConstants.UserPrefs.enableBiometrics)
    }
    
    func disableBiometricsByDefault() {
        
        // EnableBiometrics should be false by default
        // If the user has not set their biometric preference (biometricPreferenceSet == false ...also the default),
        // then EnableBiometrics should be set to false in the UserDefault setting (to be used later)
        let biometricsPreferenceSet = UserDefaults.standard.bool(forKey: MyCTCAConstants.UserPrefs.biometricPreferenceSet)
        print("disableBiometricsByDefault biometricPreferenceSet:\(biometricsPreferenceSet)")
        if (!biometricsPreferenceSet)
        {
            UserDefaults.standard.set(false, forKey: MyCTCAConstants.UserPrefs.enableBiometrics)
        }
    }
    
    func setBiometricPreference(enabled: Bool) {
        UserDefaults.standard.set(true, forKey: MyCTCAConstants.UserPrefs.biometricPreferenceSet)
        UserDefaults.standard.set(enabled, forKey: MyCTCAConstants.UserPrefs.enableBiometrics)
    }
    
    func storeLoginParameters() {
        print("BiometricManager storeLoginParameters: \(String(describing: loginParameters.password))")
        if (myKeychainManager.keychainWrapper == nil) {
            myKeychainManager.keychainWrapper = KeychainWrapper()
        }
        
        let data: [String: AnyObject] = [KeychainDataKey.password.rawValue: loginParameters.password as AnyObject,
                                         KeychainDataKey.token.rawValue: AppSessionManager.shared.currentUser.accessToken!.asJSONString() as AnyObject]
        
        myKeychainManager.save(data: data, account: loginParameters.username)
        
        //Store username in user preferences
        let prefs:UserDefaults = UserDefaults.standard
        prefs.setValue(loginParameters.username, forKey: MyCTCAConstants.UserPrefs.username)
        
    }
    
    func removeStoredParameters() {
        print("BiometricManager removeStoredParameters: \(String(describing: loginParameters.password))")
        if (myKeychainManager.keychainWrapper == nil) {
            myKeychainManager.keychainWrapper = KeychainWrapper()
        }
        myKeychainManager.delete(account: loginParameters.username)
    }
    
    func fetchItems() -> [String: Any] {
        
        if (myKeychainManager.keychainWrapper == nil) {
            myKeychainManager.keychainWrapper = KeychainWrapper()
        }
        var data: [String:Any] = ["password": ""];
        if let fetchedData: [String: Any] = myKeychainManager.fetch(account: loginParameters.username) {
            data = fetchedData;
        }
        return data
    }
    
    func getBiometricImage(context: LAContext?) -> UIImage? {
        
        if let biometricName:String = self.getBiometricName(context: context) {
            if (biometricName == TOUCH_ID) {
                return UIImage(named: "touchId")
            }
            if (biometricName == FACE_ID) {
                return UIImage(named: "faceId")
            }
        }
        return nil
    }
    
    func getBiometricName(context: LAContext?) -> String? {
        if #available(iOS 11.0, *) {
            let laContext: LAContext = (context == nil) ? LAContext() : context!
            print("getBiometricName LACONTEXT: \(laContext) ::: biometryType")
            if (laContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)) {
                if (laContext.biometryType == LABiometryType.faceID) {
                    return FACE_ID
                } else if (laContext.biometryType == LABiometryType.touchID) {
                    return TOUCH_ID
                }
            }
            return "nil"
        } else {
            return TOUCH_ID
        }
    }
    
    func getBiometricEnablePrompt(context: LAContext?) -> String? {
        var prompt: String = ""
        
        if let biometricName:String = self.getBiometricName(context: context) {
            print("getBiometricEnablePrompt biometricName: \(biometricName)")
            prompt = "Would you like to enable \(biometricName) for your myCTCA account?"
        }
        
        return prompt
    }
    
    func getBiometricEnabledConfirmation(context: LAContext?) -> (title: String, msg: String) {
        
        var confirmTuple = (title: "", msg: "")
        
        if let biometricName:String = self.getBiometricName(context: context) {
            let confirmTitle = "\(biometricName) Enabled"
            let confirmMessage = "You should be able to use \(biometricName) the next time you sign in to myCTCA."
            
            confirmTuple = (title: confirmTitle, msg: confirmMessage)
        }
        
        return confirmTuple
    }
    
    func getBiometricDisabledConfirmation(context: LAContext?) -> (title: String, msg: String) {
        
        var confirmTuple = (title: "", msg: "")
        
        if let biometricName:String = self.getBiometricName(context: context) {
            let confirmTitle = "\(biometricName) Disabled"
            let confirmMessage = "You can enable \(biometricName) at any time by visiting the \(biometricName) section under HELP AND SETTINGS in the More section."
            
            confirmTuple = (title: confirmTitle, msg: confirmMessage)
        }
        
        return confirmTuple
    }
    
    func getBiometricLaunchMessage(context: LAContext?) -> String? {
        var prompt: String = ""
        
        if let biometricName:String = self.getBiometricName(context: context) {
            print("getBiometricEnablePrompt biometricName: \(biometricName)")
            prompt = "Sign in with \(biometricName)"
        }
        
        return prompt
    }
    
    func getAppleBiometricURL(context: LAContext?) -> String {
        
        var urlString: String = ABOUT_APPLE_TOUCH_ID
        
        if let biometricName:String = self.getBiometricName(context: context) {
            if (biometricName == FACE_ID) {
                urlString = ABOUT_APPLE_FACE_ID
            }
        }
        
        return urlString
    }
    
    func evaluateErrorFromBiometrics(error: NSError, laContext: LAContext) -> (errorMsg: String, alertNeeded: Bool) {
        
        var message: String = ""
        var showAlert: Bool = false
        print("Biometry errorcode : \(error.code)")
        switch(error.code) {
        case LAError.authenticationFailed.rawValue:
            if #available(iOS 11.0, *) {
                if (laContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)) {
                    if (laContext.biometryType == LABiometryType.faceID) {
                        message = NSLocalizedString("LoginFaceIDErrorAuthenticationFailed", comment:"Error message displayed when authentication attempt through Touch ID fails.")
                    } else {
                        message = NSLocalizedString("LoginTouchIDErrorAuthenticationFailed", comment:"Error message displayed when authentication attempt through Touch ID fails.")
                    }
                }
            } else {
                message = NSLocalizedString("LoginTouchIDErrorAuthenticationFailed", comment:"Error message displayed when authentication attempt through Touch ID fails.")
            }
            showAlert = true
            break;
        case LAError.userCancel.rawValue:
            if #available(iOS 11.0, *) {
                if (laContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)) {
                    if (laContext.biometryType == LABiometryType.faceID) {
                        message = NSLocalizedString("LoginFaceIDErrorUserCancelled", comment:"Message displayed when user cancels sign in attempt.")
                    } else {
                        message = NSLocalizedString("LoginTouchIDErrorUserCancelled", comment: "Message displayed when user cancels sign in attempt.")
                    }
                }
            } else {
                message = NSLocalizedString("LoginTouchIDErrorUserCancelled", comment: "Message displayed when user cancels sign in attempt.")
            }
            showAlert = false
            break;
        case LAError.userFallback.rawValue:
            if #available(iOS 11.0, *) {
                if (laContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)) {
                    if (laContext.biometryType == LABiometryType.faceID) {
                        message = NSLocalizedString("LoginFaceIdErrorUserFallback", comment: "Message displayed when the user intentionally doesn’t use the TouchID authentication")
                    } else {
                        message = NSLocalizedString("LoginTouchIdErrorUserFallback", comment: "Message displayed when the user intentionally doesn’t use the TouchID authentication")
                    }
                }
            } else {
                message = NSLocalizedString("LoginTouchIdErrorUserFallback", comment: "Message displayed when the user intentionally doesn’t use the TouchID authentication")
            }
            showAlert = true
            break;
        case LAError.systemCancel.rawValue:
            if #available(iOS 11.0, *) {
                if (laContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)) {
                    if (laContext.biometryType == LABiometryType.faceID) {
                        message = NSLocalizedString("LoginFaceIDErrorSystemCancelled", comment: "Message displayed when system stops authentication (another app becomes active).")
                    } else {
                        message = NSLocalizedString("LoginTouchIDErrorSystemCancelled", comment: "Message displayed when system stops authentication (another app becomes active).")
                    }
                }
            } else {
                message = NSLocalizedString("LoginTouchIDErrorSystemCancelled", comment: "Message displayed when system stops authentication (another app becomes active).")
            }
            showAlert = true
            break;
        case LAError.biometryNotAvailable.rawValue:
            break
        default:
            showAlert = true
            message = NSLocalizedString("LoginTouchIdErrorUnkown", comment: "Error message displayed when an unkown error is encountered.")
            break;
        }
        
        return (errorMsg: message, alertNeeded: showAlert)
    }
}

