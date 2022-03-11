//
//  ANNCBaseViewController.swift
//  myctca
//
//  Created by Manjunath K on 9/3/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class ANNCBaseViewController: UIViewController, CTCAViewControllerProtocol {
    
    @IBOutlet weak var statement1: UILabel!
    @IBOutlet weak var statement2: UILabel!
    @IBOutlet weak var statement3: UILabel!
    
    @IBOutlet weak var downloadButton: UIButton!
    @IBOutlet weak var submitButton: UIButton!
    var fromUniversalLink = false
    
    let formManager = FormsManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        prepareDisplay()
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ANNC_BASE_VIEW)
        formManager.hostViewController = self
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if fromUniversalLink {
            showANNCForm()
            fromUniversalLink = false
        } else {
            loadViewData()
        }
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        checkForExistingANNC()
    }
    
    func prepareDisplay() {
        statement1.text = ANNCUIConstants.statement1
        statement2.text = ANNCUIConstants.statement2
        statement3.text = ANNCUIConstants.statement3
        
        downloadButton.layer.cornerRadius = 5
        submitButton.layer.cornerRadius = 5
        
        downloadButton.isHidden = true
        statement3.isHidden = true
    }
    
    func checkForExistingANNC() {
        if downloadButton.isHidden == false {
            return
        }
        
        self.showActivityIndicator(view: self.view , message: "Loading..")
        
        formManager.checkForExistingANNC() {
            isFormExist, status in
            
            self.dismissActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.formManager.getLastServerError(), onView: self)
            } else {
                if let isANNCExist = isFormExist, isANNCExist == true {
                    DispatchQueue.main.async(execute: {
                            self.downloadButton.isHidden = false
                            self.statement3.isHidden = false
                        })
                } else {
                    DispatchQueue.main.async(execute: {
                        self.downloadButton.isHidden = true
                        self.statement3.isHidden = true
                    })
                }
            }
        }
    }
    
    @IBAction func submitTapped(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ANNC_SUBMIT_TAP)
        showANNCForm()
    }
    
    func showANNCForm() {
        if GenericHelper.shared.hasPermissionTo(feature: .submitANNCForm) {
            performSegue(withIdentifier: "ShowANNCFormSegue", sender: self)
        } else {
            GenericHelper.shared.showNoAccessMessage(view: self)
        }
    }
    
    @IBAction func downloadTapped(_ sender: Any) {
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        self.showActivityIndicator(view: self.view, message: "Downloading ANNC form…")
        
        formManager.downloadANNCForm() {
            status in
            
            self.dismissActivityIndicator()
            
            if status == .FAILED {
                //display error
                ErrorManager.shared.showServerError(error: self.formManager.getLastServerError(), onView: self)
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ANNC_DOWNLOAD_FAIL)
            }
        }
    }
         
}

extension ANNCBaseViewController {
    
    func showPDF(fileURL:URL) {
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ANNC_FORM_PDF_VIEW)
        DispatchQueue.main.async {
            [weak self] in
            
            guard let self = self else { return }
            
            let pdfViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PDFViewController") as! PDFViewController

            pdfViewController.fileURL = fileURL
            self.present(pdfViewController, animated: true, completion: nil)
            pdfViewController.titleLabel.text = MyCTCAConstants.FileNameConstants.ANNCPDFName
            pdfViewController.pdfDocType = .ANNC
        }
    }
}
