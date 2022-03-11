//
//  TermsOfUseViewController.swift
//  myctca
//
//  Created by Manjunath K on 9/10/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit
import WebKit

protocol TermsOfUseProtocol : AnyObject {
    func didAcceptTermsOfUse(status:Bool)
}

class TermsOfUseViewController: UIViewController, CTCAViewControllerProtocol {

    @IBOutlet weak var submitButton: UIButton!
    @IBOutlet weak var declineTermsOfUseButton: UIButton!
    @IBOutlet weak var acceptTermsOfUseButton: UIButton!
    @IBOutlet weak var webView: WKWebView!
    
    weak var delegate: TermsOfUseProtocol?
    let homeManager = HomeManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        webView.navigationDelegate = self
        loadViewData()
        
        declineTermsOfUseButton.isSelected = true
        submitButton.layer.cornerRadius = 5
        self.modalPresentationStyle = .fullScreen
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_TERMS_OF_USE_VIEW)
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        showActivityIndicator(view: self.view, message: "Loading..")
        
        if let url = AuthenticationAPIRouter.openTermsOfUseLink.asUrl() {
            let request: URLRequest = URLRequest(url: url)
            self.webView.load(request)
        }
    }
    
    func moveBackToLoginPage(withStatus:Bool) {

        if let del = self.delegate {
            del.didAcceptTermsOfUse(status: withStatus)
        }
        self.dismiss(animated: true, completion: nil)
        
        //self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func submitButtonTapped(_ sender: Any) {
        
        if !acceptTermsOfUseButton.isSelected {
            moveBackToLoginPage(withStatus:false)
            return
        }
        
        if let userId = AppSessionManager.shared.currentUser.iUser?.userId {
        
            var userInfo = UserPreference()
            userInfo.userId = userId
            userInfo.userPreferenceType = "AcceptedTermsOfUse"
            userInfo.userPreferenceValueString = "True"
                        
            showActivityIndicator(view: self.view, message: "Submitting...")
            
            homeManager.saveUserPrefrences(preference: userInfo.getPayloadVariant()) {
                status in
                
                self.fadeOutActivityIndicator()
                
                if status == .SUCCESS {
                    self.moveBackToLoginPage(withStatus: true)
                } else {
                    ErrorManager.shared.showServerError(error: self.homeManager.getLastServerError(), onView: self)
                }
            }
        }
    }
    
    @IBAction func acceptButtonTapped(_ sender: Any) {
        acceptTermsOfUseButton.isSelected = !acceptTermsOfUseButton.isSelected
        declineTermsOfUseButton.isSelected = !acceptTermsOfUseButton.isSelected
        
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ACCEPT_TERMS_OF_USE_TAP)
    }
    
    @IBAction func declineButtonTapped(_ sender: Any) {
        declineTermsOfUseButton.isSelected = !declineTermsOfUseButton.isSelected
        acceptTermsOfUseButton.isSelected = !declineTermsOfUseButton.isSelected
        
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_DECLINE_TERMS_OF_USE_TAP)
    }
}

extension TermsOfUseViewController : WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        fadeOutActivityIndicator()
    }
    
    func webView(_: WKWebView, didFail: WKNavigation!, withError: Error) {
        GenericHelper.shared.showAlert(withtitle: "Error", andMessage: "Uknowen error occured!!", onView: self)
        
        self.moveBackToLoginPage(withStatus: false)
        
    }

    func webView(_: WKWebView, didFailProvisionalNavigation: WKNavigation!, withError: Error) {
        GenericHelper.shared.showAlert(withtitle: "Error", andMessage: "Uknowen error occured!!", onView: self)
        
        self.moveBackToLoginPage(withStatus: false)
        
    }

}
