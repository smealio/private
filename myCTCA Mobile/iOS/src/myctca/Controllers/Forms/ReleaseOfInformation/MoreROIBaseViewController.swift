//
//  MoreROIBaseViewController.swift
//  myctca
//
//  Created by Manjunath K on 7/30/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit
import PDFKit

class MoreROIBaseViewController: UIViewController, CTCAViewControllerProtocol {
    
    let ROI_FORM_SEGUE: String = "ROIToFormSegue"
    
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var dewnloadOptionLabel: UILabel!
    @IBOutlet weak var submitFormLabel: UILabel!
    @IBOutlet weak var downloadButton: UIButton!
    @IBOutlet weak var submitFormOnlineButton: UIButton!
    @IBOutlet weak var enquiryLabel: UILabel!
    
    let formsManager = FormsManager()
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.navigationBar.isTranslucent = false
        
        prepareView()
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ROI_BASE_VIEW)
        formsManager.hostViewController = self
    }
    
    func prepareView() {
        downloadButton.layer.cornerRadius = 5
        submitFormOnlineButton.layer.cornerRadius = 5
        
        descriptionLabel.text = ROIUIViewConstants.descriptionTitle
        dewnloadOptionLabel.text = ROIUIViewConstants.option1Title
        submitFormLabel.text = ROIUIViewConstants.option2Title
        enquiryLabel.text = ROIUIViewConstants.bottomDetailText
    }
    
    @IBAction func downloadButtonTapped(_ sender: Any) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        self.showActivityIndicator(view: self.view, message: "Downloading ROI form…")
        
        formsManager.downloadROIForm() {
            status in
            
            self.dismissActivityIndicator()
            
            if status == .FAILED {
                //display error
                ErrorManager.shared.showServerError(error: self.formsManager.getLastServerError(), onView: self)
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ROI_DOWNLOAD_FAIL)
            }
        }
    }
    
    @IBAction func submitFormOnlineButtonTapped(_ sender: Any) {
        if GenericHelper.shared.hasPermissionTo(feature: .submitROIForm) {
            performSegue(withIdentifier: ROI_FORM_SEGUE, sender: self)
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ROI_SUBMIT_TAP)
        } else {
            GenericHelper.shared.showNoAccessMessage(view: self)
        }
    }
}


