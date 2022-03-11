//
//  LoginViewController.swift
//  myCTCA
//
//  Created by Tomack, Barry on 11/8/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit
import LocalAuthentication
import SafariServices

class LoginViewController: UIViewController, UITextFieldDelegate, CTCAViewControllerProtocol, SFSafariViewControllerDelegate {
    
    @IBOutlet weak var scrollView: UIScrollView!
    var activeField: UITextField?
    // Text Fields
    @IBOutlet weak var usernameTF: UITextField!
    @IBOutlet weak var passwordTF: UITextField!
    // Labels
    @IBOutlet weak var usernameLabel: UILabel!
    @IBOutlet weak var passwordLabel: UILabel!
    // Constraints
    @IBOutlet weak var logoSpaceFromTop: NSLayoutConstraint!
    // Buttons
    @IBOutlet weak var signInButton: UIButton!
    @IBOutlet weak var biometricButton: UIButton!
    @IBOutlet weak var forgotPasswordButton: UIButton!
    @IBOutlet weak var createAcctButton: UIButton!
    @IBOutlet weak var contactSupportButton: UIButton!
    
    @IBOutlet weak var showPasswordButton: UIButton!
    var passwordIsVisible: Bool = false
    
    // Biometrics Button Constraints
    @IBOutlet weak var biometricsWidth: NSLayoutConstraint!
    @IBOutlet weak var biometricsHeight: NSLayoutConstraint!
    
    let doneBarHeight:CGFloat = 50
    let shortScreenSpaceFromTop: CGFloat = 15
    let normalSpaceFromTop: CGFloat = 25
    let longScreenSpaceFromTop: CGFloat = 150
    
    lazy var myKeychainManager: KeychainManager  = KeychainManager.shared
    
    var authClient: AuthenticationClient!
    
    // Local Authorization Context
    var laContext: LAContext = LAContext()
    
    var contactSupportComposer: ContactSupportComposer!
    
    // Previous User
    var previousUsername: String?
    
    // For Unit Testing
    var isSigningIn: Bool = false
    var isAuthenticated: Bool = false
    
    let ShowTermsOfUseSegue = "ShowTermsOfUse"
    
    let authenticationManager = AuthenticationManager()

    override func viewDidLoad() {
        super.viewDidLoad()

        self.navigationController?.navigationBar.isHidden = true;
        addDoneButtonOnKeyboard()
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_SIGNIN_VIEW)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        super.viewWillAppear(animated)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
        
        if !processWarningMessages() {
            prepareView()
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        
        super.viewWillDisappear(animated)
        
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    func addDoneButtonOnKeyboard() {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: 320, height: doneBarHeight))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        self.usernameTF.inputAccessoryView = doneToolbar
        self.passwordTF.inputAccessoryView = doneToolbar
    }
    
    @objc func doneButtonAction() {
        self.usernameTF.resignFirstResponder()
        self.passwordTF.resignFirstResponder()
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        //Need to calculate keyboard exact size due to Apple suggestions
        self.scrollView.isScrollEnabled = true
        let info : NSDictionary = notification.userInfo! as NSDictionary
        let keyboardSize = (info[UIResponder.keyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue.size
        let contentInsets : UIEdgeInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: keyboardSize!.height+doneBarHeight+20.0, right: 0.0)

        self.scrollView.contentInset = contentInsets
        self.scrollView.scrollIndicatorInsets = contentInsets

        var aRect : CGRect = self.view.frame
        aRect.size.height -= keyboardSize!.height
        if activeField != nil
        {
            if (!aRect.contains(activeField!.frame.origin))
            {
                self.scrollView.scrollRectToVisible(activeField!.frame, animated: true)
            }
        }


    }


    @objc func keyboardWillHide(notification: NSNotification) {
        //Once keyboard disappears, restore original positions
        let info : NSDictionary = notification.userInfo! as NSDictionary
        let keyboardSize = (info[UIResponder.keyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue.size
        let contentInsets : UIEdgeInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: -keyboardSize!.height, right: 0.0)
        self.scrollView.contentInset = contentInsets
        self.scrollView.scrollIndicatorInsets = contentInsets
        self.view.endEditing(true)
        self.scrollView.isScrollEnabled = false

    }
    
    func prepareView() {
        
        self.usernameTF.delegate = self
        self.passwordTF.delegate = self
        self.usernameTF.addTarget(self, action: #selector(textDidChange), for: .editingChanged)
        
        checkForPreviousUsername()
        
        DispatchQueue.main.async(execute: {

            self.passwordTF.text = ""
            self.passwordIsVisible = false
            
            //self.usernameTF.text = "tanja.testphoenix@testctca.com"
            //self.usernameTF.text = "phil.test@testctca.com"
            //self.usernameTF.text = "atlantanew.test"
            //self.usernameTF.text = "rose.test@testctca.com"
            //self.usernameTF.text = "Tanja.testphilly@testctca.com"
            //self.usernameTF.text = "Tanja.Test@testctca.com"
            //self.usernameTF.text = "brad.test@testctca.com"
            //self.usernameTF.text = "western.scm@testctca.com"
            //self.usernameTF.text = "annf777@testctca.com"
            //self.usernameTF.text = "drgcare@testctca.com"
            //self.usernameTF.text = "scott.pool@testctca.com"
            //self.usernameTF.text = "eastern.integration@fakectca-hope.com"
            //self.usernameTF.text = "Western.integration@fakectca-hope.com"
            //self.usernameTF.text = "tanja.testchicago@testctca.com"
            //self.passwordTF.text = "CItestPW2020$"
            //self.usernameTF.text = "rosalia.lmz09@gmail.com"
            //self.usernameTF.text = "atlantanew.test@testctca.com"
            //self.passwordTF.text = "myCTCAtest"
            //self.usernameTF.text = "serflexfield.test@testctca.com"
            //self.usernameTF.text = "mrmc.integration@testctca.com"
            
            self.passwordTF.adjustsFontForContentSizeCategory = true
            self.setTogglePasswordButtonImage()
            
            self.signInButton.titleLabel?.text = "\(MyCTCAConstants.FormText.buttonSignIn)"
            self.setBiometricButtonImage()
            
            var hasPreviousUsername: Bool = false
            if(self.previousUsername != nil) {
                hasPreviousUsername = true
            }
            let biometricsEnabled = BiometricManager.shared.isBiometricsEnabled()
            print("biometricsEnabled: \(biometricsEnabled)")
            let localAuthorization = BiometricManager.shared.biometricCapable()
            if (hasPreviousUsername && biometricsEnabled && localAuthorization) {
                self.biometricButton.isHidden = false
                self.launchBiometricAuth(self)
            } else {
                self.biometricButton.isHidden = true
            }
        })
    }
    
    func setTogglePasswordButtonImage() {
        if (self.passwordIsVisible == false) {
            self.showPasswordButton.setImage(#imageLiteral(resourceName: "eyeslash") , for: .normal)
        } else {
            self.showPasswordButton.setImage(#imageLiteral(resourceName: "eye") , for: .normal)
        }
    }
    
    func setBiometricButtonImage() {
        let btnImage = BiometricManager.shared.getBiometricImage(context: laContext)
        self.biometricButton.setImage(btnImage , for: .normal)
        self.biometricButton.contentHorizontalAlignment = .fill
        self.biometricButton.contentVerticalAlignment = .fill
        self.biometricButton.imageView?.contentMode = .scaleAspectFit
    }
    
    func checkForPreviousUsername() {
        
        // Retrieve Last Username from user defaults
        let prefs:UserDefaults = UserDefaults.standard
        
        let username = prefs.string(forKey: MyCTCAConstants.UserPrefs.username)
        if username != nil {
            DispatchQueue.main.async(execute: {
                print("HEY I GOT A PREVIOUS USERNAME")
                self.previousUsername = username!
                self.usernameTF.text = username!
            })
        }
    }

    func authenticateUser() {
        
        isSigningIn = true
        if (authClient == nil) {
            authClient = AuthenticationClient()
        }
        
        // Clear stored data if username is different than previous username
        if (previousUsername != nil) {
            let currUserName: String = usernameTF.text!
            if (previousUsername != currUserName) {
                clearStoredData()
            }
        }
        
        BiometricManager.shared.loginParameters.username = usernameTF.text!
        
        var password: String = ""
        if (passwordTF.text! == "") {
            if (BiometricManager.shared.biometricCapable()) {
                let data = BiometricManager.shared.fetchItems()
                password = data["password"] as! String
            } else {
                password = passwordTF.text!
            }
        } else {
            password = passwordTF.text!
        }
        BiometricManager.shared.loginParameters.password = password
        
        // Show Activity Indicator
        showActivityIndicator(view: self.view, message:"Authenticating...")
        
        // Clear data arrays if username is different from previous username
        if (BiometricManager.shared.loginParameters.username != previousUsername) {
            AppSessionManager.shared.clearAllSessionParameters()
            UserDefaults.standard.set(false, forKey: MyCTCAConstants.UserPrefs.biometricPreferenceSet)
        }
        print("Biometric Parameters: \(BiometricManager.shared.loginParameters)")
        
        GenericHelper.shared.printDate(string: "LoginViewController:Starts authentication at: ")
        
        //REST call 1
        authenticationManager.authenticateUser(username: BiometricManager.shared.loginParameters.username, password: BiometricManager.shared.loginParameters.password) { [self]
            (error: ServerError?, status: RESTResponse) in
        
            if (status == .SUCCESS) {
                _ = self.updateLoginFailAttempts(nil)
                
                self.passwordTF.text = ""
                
                if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                    self.dismissActivityIndicator(completion: nil)
                    return
                }
                
                authenticationManager.fetchIdentityUserInfo() {
                    status in
                    
                    if status == .SUCCESS {

                        UserDefaults.standard.set(true, forKey: MyCTCAConstants.UserPrefs.successfullyLoggedIn)
                        
                        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                            self.dismissActivityIndicator(completion: nil)
                            return
                        }
                        
                        //REST call 3
                        authenticationManager.fetchUserProfile() {
                            status in
                            if status == .SUCCESS {

                                if let facCode = AppSessionManager.shared.currentUser.userProfile?.primaryFacilityCode {
                                    
                                    if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                                        self.dismissActivityIndicator(completion: nil)
                                        return
                                    }
                                    
                                    //REST call 4
                                    self.retrieveFacilityData(accessToken: AppSessionManager.shared.currentUser.accessToken!, facilityCode: facCode, completion: { (success, error) in
                                        if (success) {
                                            AppSessionManager.shared.beginCurrentSession()
                                            _ = AppSessionManager.shared.currentUser.accessToken!.isExpired()
                                            self.validateForAcceptanceOfTermsOfUse()
                                        } else {
                                            let analyticsDict = ["errorCode":("\(authenticationManager.getLastServerError().errorCode)"), "api":"retrieveFacilityData"]
                                            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SIGNIN_FAIL, analyticsDict)
                                            
                                            self.handleAuthenticationError(error: authenticationManager.getLastServerError(), errorDesc: authenticationManager.getLastServerError().errorMessage)
                                        }
                                    })
                                } else {
                                    AppSessionManager.shared.beginCurrentSession()
                                    _ = AppSessionManager.shared.currentUser.accessToken!.isExpired()
                                    self.validateForAcceptanceOfTermsOfUse()                                }
                            } else {
                                let analyticsDict = ["errorCode":("\(authenticationManager.getLastServerError().errorCode)"), "api":"retrieveUserProfileData"]
                                  AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SIGNIN_FAIL, analyticsDict)
                                  
                                self.handleAuthenticationError(error: authenticationManager.getLastServerError(), errorDesc: authenticationManager.getLastServerError().errorMessage)
                            }
                        }
                    } else {
                        let analyticsDict = ["errorCode":("\(authenticationManager.getLastServerError().errorCode)"), "api":"retrieveUserData"]
                          AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SIGNIN_FAIL, analyticsDict)
                                                  
                        self.handleAuthenticationError(error: authenticationManager.getLastServerError(), errorDesc: authenticationManager.getLastServerError().errorMessage)
                    }
                }
            } else {
                let analyticsDict = ["errorCode":("\(error!.errorCode)"), "api":"loginWithParameters"]
                  AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_SIGNIN_FAIL, analyticsDict)
                                          
                self.handleAuthenticationError(error: error!, errorDesc: error!.errorMessage)
            }
        }
    }
    
    func handleAuthenticationError(error: ServerError, errorDesc: String) {
        
        // Disable Touch ID
        BiometricManager.shared.setBiometricPreference(enabled: false)
        BiometricManager.shared.removeStoredParameters()
        
        // Show Error
        self.isSigningIn = false
        
        if !self.updateLoginFailAttempts(error) { //if message is displayed for 3 wrong credential attempts
            if (error.errorCode == AuthServiceError.userIsLocked.rawValue || error.errorCode == AuthServiceError.userAccessDenied.rawValue) {
                if error.errorCode == AuthServiceError.userIsLocked.rawValue {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.LOGIN_LOCKED_ACCOUNT)
                } else {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.LOGIN_INCORRECT_CREDENTIALS)
                }
                self.loginErrorMessageWithPhoneNumber(for: error, desc: errorDesc)
            } else {
                self.dismissActivityIndicator()
                
                if error.errorCode == TIMEOUT_ERROR_CODE {
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.LOGIN_TIMEOUT_ERROR)
                }

                let alert = self.ctcaInfoAlert(title:NSLocalizedString("LoginProblemTitle", comment: "Title appearing on alert pop-up when there is a problem logging in to CTCA Identity services."),
                                               message: self.loginErrorMessage(for: error, desc: errorDesc),
                                               okaction: nil,
                                               otheraction:nil)
                self.present(alert,
                             animated: true,
                             completion: nil)
            }
        }
    }
    
    func clearStoredData() {
        
        AppSessionManager.shared.clearAllSessionParameters()
        BiometricManager.shared.setBiometricPreference(enabled: false)
        BiometricManager.shared.removeStoredParameters()
    }
    
    func loginErrorMessageWithPhoneNumber(for sError: ServerError, desc: String?)  {
        var msg: String = ""
        var techSupportNumber = ""
        
        AppInfoManager.shared.getTechSupportPhoneNumber() {
            number, status in
            
            if status == .SUCCESS {
                techSupportNumber = number ?? ""
            }
            
            self.dismissActivityIndicator()
            
            //We dont need to have separate messages for userIsLocked and accessDenied, as the same
            //format message is displayed.
            let msg1 = NSLocalizedString("LoginErrorUserAccessDeniedAppendP1", comment: " Error message when invalid_grant error received.")
            let msg2 = NSLocalizedString("LoginErrorUserAssitMsg", comment: " Error message when invalid_grant error .")
            
            msg = msg1 + techSupportNumber + msg2
            
            if (desc != nil) {
                msg = "\(String(describing: desc!)) \(msg)"
            }
            
            DispatchQueue.main.async {
                let alert = self.ctcaInfoAlert(title:NSLocalizedString("LoginProblemTitle", comment: "Title appearing on alert pop-up when there is a problem logging in to CTCA Identity services."),
                                               message: msg,
                                               okaction: nil,
                                               otheraction:nil)
                self.present(alert,
                             animated: true,
                             completion: nil)
            }
        }
    }
    
    func loginErrorMessage(for sError: ServerError, desc: String?) -> String {
        let error = sError.errorCode
        var msg: String = ""
        if (error == AuthServiceError.accessTokenResponseError.rawValue) {
            msg = NSLocalizedString("NetworkingDataProblem", comment: "Error message when there is a problem retrieving data from CTCA service.")
        } else {
            msg = NSLocalizedString("NetworkingErrorGeneralMessage", comment: "Error message displayed for general problem with connecting to CTCA Services.")
        }
        return msg
    }
    
    func retrieveFacilityData(accessToken: AccessToken, facilityCode: String, completion: @escaping (Bool, ServerError?) -> Void) {
        
        let facList = AppSessionManager.shared.currentUser.allFacilitesList
        
        if facList.count > 0 {
            completion(true, nil)
            return
        }
        
        authenticationManager.fetchAllFacilites(facCode:facilityCode) {
            status in
            if (status == .SUCCESS) {
                completion(true, nil)
            } else {
                completion(false, ErrorManager.shared.getDefaultServerError())
            }
        }
    }
    
    func storeItems(parameters: LoginParameters, accessToken: AccessToken) {
        
        if (myKeychainManager.keychainWrapper == nil) {
            myKeychainManager.keychainWrapper = KeychainWrapper()
        }
        
        let data: [String: AnyObject] = [KeychainDataKey.password.rawValue: parameters.password as AnyObject,
                                         KeychainDataKey.token.rawValue: accessToken.asJSONString() as AnyObject]
        
        myKeychainManager.save(data: data, account: parameters.username)
        
        //Store username in user preferences
        let prefs:UserDefaults = UserDefaults.standard
        prefs.setValue(parameters.username, forKey: MyCTCAConstants.UserPrefs.username)
    }
    
    func fetchItems(parameters: LoginParameters) -> [String: AnyObject] {
        
        if (myKeychainManager.keychainWrapper == nil) {
            myKeychainManager.keychainWrapper = KeychainWrapper()
        }
        
        let data: [String: AnyObject]? = myKeychainManager.fetch(account: parameters.username)
        
        return data!
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK - SHOW/HIDE PASSWORD
    @IBAction func togglePassword(_ sender: Any) {
        
        self.passwordTF.isSecureTextEntry = self.passwordIsVisible
        self.passwordIsVisible = !self.passwordIsVisible
        setTogglePasswordButtonImage()
        
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_SHOW_PASSWORD_TAP)
    }
    
    // MARK - SIGN IN
    @IBAction func signIn() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_SIGNIN_TAP)
        
        print("LoginViewController signIn")
        // Dismiss Keyboard if visible
        usernameTF.resignFirstResponder()
        passwordTF.resignFirstResponder()
        
        // Make sure username and password are not blank
        if (usernameTF.text == "" || passwordTF.text == "") {
            
            let alert = self.ctcaInfoAlert(title:NSLocalizedString("LoginProblemTitle", comment: "Title displayed in pop up alert box when there is an error attempting to log in."),
                                           message: NSLocalizedString("LoginRequiredFields", comment: "List the two required fields required to submit a login requrest."),
                                           okaction: nil,
                                           otheraction:nil)
            self.present(alert, animated: true, completion: nil)
        } else {
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            authenticateUser()
        }
    }

    // MARK - Forgot Password
    @IBAction func forgotPassword() {
        if let url = AuthenticationAPIRouter.openResetPasswordLink.asUrl() {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_FORGOT_PASSWORD_TAP)
            GenericHelper.shared.launchSafariViewController(withUrl: url, forView: self)
        }
    }
    
    // MARK - Biometric Launch
    @IBAction func launchBiometricAuth(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_TOUCHID_TAP)
        
        // Set the reason string that will appear on the authentication alert.
        var reasonString = "Unlock"
        if BiometricManager.shared.biometricCapable(context: laContext) {
            reasonString = BiometricManager.shared.getBiometricLaunchMessage(context: laContext)!
        }
        if(self.previousUsername != "" && self.previousUsername != nil) {
            reasonString = "\(reasonString), \(self.previousUsername!)"
        }
        
        self.laContext.touchIDAuthenticationAllowableReuseDuration = 1.0;
        if BiometricManager.shared.biometricCapable(context: laContext) {
            
            self.laContext.evaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics,
                                          localizedReason: reasonString,
                                          reply: { (success, error) -> Void in
                                            
                                            DispatchQueue.main.async(execute: {
                                                if success {
                                                    print("LoginViewController Biometrics Success")
                                                    self.authenticateUser()
                                                    self.laContext = LAContext()
                                                }
                                                if error != nil {
                                                    let errorEval = BiometricManager.shared.evaluateErrorFromBiometrics(error: error! as NSError, laContext: self.laContext)
                                                    
                                                    if errorEval.alertNeeded == true {
                                                        var alertTitle = ""
                                                        if #available(iOS 11.0, *) {
                                                            if (self.laContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)) {
                                                                if (self.laContext.biometryType == LABiometryType.faceID) {
                                                                    alertTitle = NSLocalizedString("LoginFaceIdErrorTitle", comment: "Title of error alert pop-up when there is a FaceId error.")
                                                                } else {
                                                                    alertTitle = NSLocalizedString("LoginTouchIdErrorTitle", comment: "Title of error alert pop-up when there is a TouchId error.")
                                                                }
                                                            }
                                                        } else {
                                                            alertTitle = NSLocalizedString("LoginTouchIdErrorTitle", comment: "Title of error alert pop-up when there is a TouchId error.")
                                                        }
                                                        let alertView = UIAlertController(title: alertTitle, message: errorEval.errorMsg, preferredStyle:.alert)
                                                        let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                                                        alertView.addAction(okAction)
                                                        self.present(alertView, animated: true, completion: nil)
                                                    }
                                                }
                                            })
            })
        }
    }
    
    // MARK - Create Account
    @IBAction func createAccount(_ sender: AnyObject) {        
        let createAcctComposer = CreateAccountComposer(self)
        let alertController = createAcctComposer.configuredCreateAccountController(sender)
        self.present(alertController, animated: true, completion:nil)
    }
    
    // MARK - Contact Support
    @IBAction func contactSupport(_ sender: AnyObject) {
        
        if (self.contactSupportComposer == nil) {
            // This conditional was originally put here for unit tests
            self.contactSupportComposer = ContactSupportComposer(self)
        }
        let alertController = self.contactSupportComposer.configuredContactSupportController(sender)
        self.present(alertController, animated: true, completion: nil)
    }
    
    // MARK - Text Field Delegate
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        
        if (usernameTF.text == "") {
            self.usernameTF.becomeFirstResponder()
        } else if (passwordTF.text == "") {
            self.passwordTF.becomeFirstResponder()
        } else {
            textField.resignFirstResponder()
            //            self.login(self.loginButton)
        }
        return true
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        guard let text = textField.text else { return true }
        let newLength = text.count + string.count - range.length
        return newLength <= MyCTCAConstants.Form.textCharLimit
    }
    
    @objc func textDidChange(textField: UITextField) {
        if textField == self.usernameTF {
            if (textField.text?.count == 0) {
                disableBiometrics();
            }
        }
    }
    
    func textFieldShouldClear(_ textField: UITextField) -> Bool {
        if textField == self.usernameTF {
            print("USERNAME CLEARED");
            disableBiometrics();
        }
        return true
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        activeField = textField
        textFieldDoesHaveFocus(textField)
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        activeField = textField
        textFieldDoesNotHaveFocus(textField)
    }
    
    func textFieldDoesHaveFocus(_ textField: UITextField) {
        if (textField == usernameTF) {
            usernameLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        if (textField == passwordTF) {
            passwordLabel.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        textField.layer.cornerRadius = 5
        textField.layer.masksToBounds = true
        textField.layer.borderWidth = 0.5
        textField.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func textFieldDoesNotHaveFocus(_ textField: UITextField) {
        if (textField == usernameTF) {
            usernameLabel.textColor = MyCTCAColor.formLabel.color
        }
        if (textField == passwordTF) {
            passwordLabel.textColor = MyCTCAColor.formLabel.color
        }
        textField.layer.cornerRadius = 5
        textField.layer.masksToBounds = true
        textField.layer.borderWidth = 0.5
        textField.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func disableBiometrics() {
        BiometricManager.shared.setBiometricPreference(enabled: false)
        self.biometricButton.isHidden = true
    }
    
    func processWarningMessages() -> Bool {
        let warningMessageManager = WarningMessageManager()
        
        if let state = GenericHelper.shared.getFromUserDefaults(forKey: warningMessageManager.userDefaultsWarningMesageKey) as? Bool, !state {
        
            warningMessageManager.fetchWarningMessges()
            
            GenericHelper.shared.saveInUserDefaults(object: true, key: warningMessageManager.userDefaultsWarningMesageKey)
            
            if let message = warningMessageManager.checkForAnyWarningMessages() {
                if message.blockApp {
                    DispatchQueue.main.async {
                        let alert = UIAlertController(title: message.title,
                                                                message: message.message,
                                                                preferredStyle:.alert)
                        
                        alert.view.tintColor = MyCTCAColor.ctcaGreen.color
                        self.present(alert,
                                       animated: true,
                                       completion: nil)
                    }
                } else {
                    let okAction = UIAlertAction(title: "OK", style: .default) { (action) in
                        self.prepareView()
                    }
                    
                    GenericHelper.shared.showAlert(withtitle: message.title, andMessage: message.message, onView: self, okaction: okAction)
                }
                return true
            }
        }
        
        return false
    }

}

extension LoginViewController {
    
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard))
        tap.cancelsTouchesInView = false
        self.view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        self.view.endEditing(true)
    }
}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
	return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}

extension LoginViewController {
    
    func signOutUser() {
        AppSessionManager.shared.endCurrentSession()
    }
    
    func showTermsAndConditionPage() {
        DispatchQueue.main.async(execute: {
            [weak self] in
            
            guard let self = self else { return }
            
            self.isSigningIn = false
            self.dismissActivityIndicator(completion: {
                let termsOfUseViewController: TermsOfUseViewController = self.storyboard?.instantiateViewController(withIdentifier: "TermsOfUseViewController") as! TermsOfUseViewController
                termsOfUseViewController.delegate = self
                self.present(termsOfUseViewController, animated: true, completion: nil)
            })
        })
    }
    
    func showMainPage() {
        DispatchQueue.main.async(execute: {
            self.isSigningIn = false
            self.dismissActivityIndicator(completion: {
                self.performSegue(withIdentifier: "Login_Main", sender: self)
            })
        })
    }
    
    func validateForAcceptanceOfTermsOfUse() {
        // Show Activity Indicator
        showActivityIndicator(view: self.view, message:"Authenticating...")
                
        authenticationManager.fetchUserPreferences() {
            status in
            
            self.dismissActivityIndicator()
        
            if status == .SUCCESS {
                if AppSessionManager.shared.currentUser.currentUserPref.count > 0 {
                    let prefList = AppSessionManager.shared.currentUser.currentUserPref
                    var found = false
                    for item in prefList {
                        if item.userPreferenceType == "AcceptedTermsOfUse" {
                            if item.userPreferenceValue {
                                GenericHelper.shared.printDate(string: "LoginViewController:Ends authentication at: ")
                                self.showMainPage()
                            } else {
                                self.showTermsAndConditionPage()
                            }
                            found = true
                            break
                        }
                    }
                    if !found {
                        self.signOutUser()
                    }
                    
                    //This is to get user contact details
                    self.fetchUserContacts()
                } else {
                   self.signOutUser()
                }
            } else {
                // Show Error
                if let sError = self.authenticationManager.serverError {
                    let okAction = UIAlertAction(title: "OK", style: .default) { (action) in
                        _ = self.signOutUser
                    }

                    GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.serverErrorTitle, andMessage: sError.errorMessage, onView: self, okaction: okAction)

                  } else {
                    self.signOutUser()
                }
            }
        }
    }
    
    func fetchUserContacts() {
        
        authenticationManager.fetchUserContactInfo() {
            status in
            
            if status == .FAILED {
                print("fetchUserContactInfo failed")
            }
        }
    }
    
    func updateLoginFailAttempts(_ forError:ServerError?) -> Bool {
        if let err = forError, err.errorCode == AuthServiceError.userAccessDenied.rawValue { //failed to login
            
            var attempts = UserDefaults.standard.integer(forKey: MyCTCAConstants.UserPrefs.loginFailureAttempts)
            attempts = 1 + attempts
            UserDefaults.standard.set(attempts, forKey: MyCTCAConstants.UserPrefs.loginFailureAttempts)
            
            if attempts == 3 {
                self.dismissActivityIndicator()

                //show alert warning
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.accountLockWarningTitle, andMessage: CommonMsgConstants.accountLockWarningMessageText, onView: self)
                return true
            }
        } else { //reset on success
            let attempts = 0
            UserDefaults.standard.set(attempts, forKey: MyCTCAConstants.UserPrefs.loginFailureAttempts)
        }
        
        return false
    }
}


extension LoginViewController : TermsOfUseProtocol {
    func didAcceptTermsOfUse(status: Bool) {
        if status {
            self.showMainPage()
        } else {
            self.signOutUser()
        }
    }
}
