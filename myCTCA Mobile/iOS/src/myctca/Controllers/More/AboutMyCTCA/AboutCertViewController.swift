//
//  AboutCertViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/19/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit
import WebKit

class AboutCertViewController: UIViewController, CTCAViewControllerProtocol {

    @IBOutlet weak var certTextWebView: WKWebView!
    @IBOutlet weak var certImageView: UIImageView!

    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "Certifications"
        certTextWebView.navigationDelegate = self

        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ABOUT_MYCTCA_CERT_VIEW)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
                
        prepareView()
    }

    private func prepareView() {
        loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        showActivityIndicator(view: self.view, message: "Loading..")
        
        setCertText()
    }
    
    func setCertText() {
        if let url = MoreAPIRouter.getCertificateText.asUrl() {
            let request: URLRequest = URLRequest(url: url)
            self.certTextWebView.load(request)
        }
    }
    
    func setCertImage() {
        guard let imageUrl = MoreAPIRouter.getCertificateImage.asUrl() else { fatalError() }
        
        do {
            let imageData = try Data(contentsOf: imageUrl)
            certImageView.image = UIImage(data: imageData)
        } catch {
            GenericHelper.shared.showAlert(withtitle: "Error", andMessage: "Failed to download the Certificate image", onView: self)
        }
    }
}

extension AboutCertViewController : WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        //to dynamically fit
        certTextWebView.frame.size.height = 1
        certTextWebView.frame.size = certTextWebView.scrollView.contentSize
        certTextWebView.scrollView.isScrollEnabled = false;
        fadeOutActivityIndicator()
        //load image now.
        setCertImage()
    }
    
    func webView(_: WKWebView, didFail: WKNavigation!, withError: Error) {
        GenericHelper.shared.showAlert(withtitle: "Error", andMessage: "Uknowen error occured!!", onView: self)
        fadeOutActivityIndicator()           
    }

    func webView(_: WKWebView, didFailProvisionalNavigation: WKNavigation!, withError: Error) {
        GenericHelper.shared.showAlert(withtitle: "Error", andMessage: "Uknowen error occured!!", onView: self)
        fadeOutActivityIndicator()
    }
}
