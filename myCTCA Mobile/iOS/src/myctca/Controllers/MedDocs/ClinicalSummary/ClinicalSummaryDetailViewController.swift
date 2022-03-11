//
//  ClinicalSummaryDetailViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/14/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit
import WebKit

class ClinicalSummaryDetailViewController: UIViewController, CTCAViewControllerProtocol, WKUIDelegate, WKNavigationDelegate {

    @IBOutlet weak var btnView: UIView!
    var webView: WKWebView = WKWebView()
    // Constraints
    var webViewConstraints: [NSLayoutConstraint] = []
    
    var clinicalSummary: ClinicalSummary?
    let medDocsManager = MedDocsManager.shared

    @IBOutlet weak var cancelButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if (clinicalSummary != nil) {
            self.title = clinicalSummary!.csTitle
        } else {
            self.title = ""
        }
        self.prepareView()
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CS_DETAIL_VIEW)
    }
    
    func prepareView() {
        
         DispatchQueue.main.async {
        // MUST add the WKWebView to the View Hierarchy before you can set the constraints
            self.view.addSubview(self.webView)
        
            self.setUpWebViewConstraints()
            
            self.webView.uiDelegate = self
            self.webView.navigationDelegate = self
            
            self.webView.backgroundColor = UIColor.white
            self.webView.scrollView.backgroundColor = UIColor.white
            
            self.view.sendSubviewToBack(self.webView)
            
            self.cancelButton.layer.borderWidth = 0.5
            self.cancelButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
        }
        
        loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)

        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        fetchClinicalSummary()
    }
    
    func setUpWebViewConstraints() {
        
        self.webView.translatesAutoresizingMaskIntoConstraints = false
        
        self.webView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 0.0).isActive = true
        self.webView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: 0.0).isActive = true
        self.webView.topAnchor.constraint(equalTo: self.view.topAnchor, constant:20.0).isActive = true
        self.webView.bottomAnchor.constraint(equalTo: self.btnView.topAnchor).isActive = true
    }
    
    func fetchClinicalSummary() {
        
        showActivityIndicator(view: self.webView, message: ActivityIndicatorMsgs.retriveSClinicalSummariesText)
        
        medDocsManager.fetchClinicalSummaryData(id: clinicalSummary!.csId) {
            data, status in
            
            self.fadeOutActivityIndicator()
            
            if status == .SUCCESS, let html = data {
                let newHead = "<head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, user-scalable=yes'>"
                let htmlString = html.replacingOccurrences(of: "<head>", with: newHead)
                self.webView.loadHTMLString(htmlString, baseURL: nil)
            } else {
                ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func downloadTapped(_ sender: Any) {
        if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.downloadCCDADocument) {

            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_GOTO_DOWNLOAD_TAP)
            
            if let id = clinicalSummary?.csId {
                 let trasmitViewController = self.storyboard?.instantiateViewController(withIdentifier: "ClinicalSummaryDownloadViewController") as! ClinicalSummaryDownloadViewController
                 
                 trasmitViewController.documentIDs = [id]
                 let backItem = UIBarButtonItem()
                 backItem.title = "Back"
                 navigationItem.backBarButtonItem = backItem
                 self.navigationController?.pushViewController(trasmitViewController, animated: true)
            }
        }  else {
            GenericHelper.shared.showNoAccessMessage(view: nil)
        }
    }
    
    @IBAction func transmitTapped(_ sender: Any) {
        if GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.transmitCCDADocuments) {

            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_GOTO_TRANSMIT_TAP)

            if let id = clinicalSummary?.csId {
                let trasmitViewController = self.storyboard?.instantiateViewController(withIdentifier: "ClinicalSummaryTrasmitViewController") as! ClinicalSummaryTrasmitViewController
                let backItem = UIBarButtonItem()
                backItem.title = "Back"
                navigationItem.backBarButtonItem = backItem
                trasmitViewController.documentIDs = [id]
                self.navigationController?.pushViewController(trasmitViewController, animated: true)
            }
        }   else {
            GenericHelper.shared.showNoAccessMessage(view: nil)
        }
    }
    
    @IBAction func cancelTapped(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_GOTO_CANCEL_TAP)

        self.navigationController?.popViewController(animated: true)
    }
}
