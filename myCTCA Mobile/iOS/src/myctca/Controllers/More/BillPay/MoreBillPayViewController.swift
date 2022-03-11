//
//  MoreBillPayViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/24/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreBillPayViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_BILL_PAY_VIEW)
    }
    
    @IBAction func launchBillPay(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_BILL_PAY_TAP)
        GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openBillPay.path, forView: self)
    }
}
