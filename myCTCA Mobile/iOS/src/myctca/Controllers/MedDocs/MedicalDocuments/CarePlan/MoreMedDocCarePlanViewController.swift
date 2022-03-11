//
//  MoreMedDocCarePlanViewController
//  myctca
//
//  Created by Tomack, Barry on 1/25/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit
import WebKit

class MoreMedDocCarePlanViewController: UIViewController, CTCAViewControllerProtocol, WKUIDelegate, WKNavigationDelegate {
    
    @IBOutlet weak var downloadButton: UIButton!
    @IBOutlet weak var cancelBtn: UIButton!
    
    //tableView is just to show no records message
    @IBOutlet weak var tableView: UITableView!
    
    var webView: WKWebView = WKWebView()
    // Constraints
    var webViewConstraints: [NSLayoutConstraint] = []
    let medDocsManager = MedDocsManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "Care Plan"
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CARE_PLAN_VIEW)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        // an empty footer to clear extra cells
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        
        self.prepareView()
        
        self.loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        fetchCarePlan()
    }
    
    private func prepareView() {
        
        // MUST add the WKWebView to the View Hierarchy before you can set the constraints
        self.view.addSubview(webView)
        
        setUpWebViewConstraints()
        
        self.webView.uiDelegate = self
        self.webView.navigationDelegate = self
        
        self.webView.backgroundColor = UIColor.white
        self.webView.scrollView.backgroundColor = UIColor.white

        self.webView.isHidden = true
    }
    
    func setUpWebViewConstraints() {
        
        self.webView.translatesAutoresizingMaskIntoConstraints = false
        
        self.webView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 20.0).isActive = true
        self.webView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: 0.0).isActive = true
        self.webView.topAnchor.constraint(equalTo: self.view.topAnchor, constant:20.0).isActive = true
        self.webView.bottomAnchor.constraint(equalTo: downloadButton.topAnchor, constant:-10.0).isActive = true
    }
    
    func fetchCarePlan() {
        
        showActivityIndicator(view: self.view, message: "Retrieving Care Plan…")
        
        medDocsManager.fetchCarePlanDetails() {
            dataStatus, status in
            
            self.fadeOutActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
            } else {
                if dataStatus {
                    DispatchQueue.main.async(execute: {
                        self.tableView.isHidden = true
                        self.webView.isHidden = false
                        let htmlString = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'><style>* {font-family: sans-serif; padding-right: 5px}</style></head><body>\(self.medDocsManager.carePlan.documentText)</body></html>"
                        self.webView.loadHTMLString(htmlString, baseURL: nil)
                        self.fadeOutActivityIndicator()

                        self.downloadButton.isHidden = false
                        self.cancelBtn.isHidden = false
                    })
                }
            }
        }
    }
    
    func dismissThis(action: UIAlertAction) {
        _ = self.navigationController?.popViewController(animated: true)
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {

        switch navigationAction.navigationType {
        case .linkActivated:
            if let anchorName = navigationAction.request.url?.absoluteString {
                let anchorAR = anchorName.components(separatedBy: "%23") // # character
                let anchor = anchorAR[1].replacingOccurrences(of: "%20", with: " ")
                let jsString: String = "var element_to_scroll_to = document.getElementById('\(anchor)');element_to_scroll_to.scrollIntoView();"
                webView.evaluateJavaScript(jsString, completionHandler: nil)
            }
        default:
            print("DEFAULT")
            break
        }
        decisionHandler(.allow)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func downloadTapped(_ sender: Any) {
        downloadReports()
    }
    
    @IBAction func cacelButtonTapped(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    func downloadReports() {
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.downloadCarePlanText)

        medDocsManager.hostViewController = self
        
        medDocsManager.downloadCarePlan() {
            
            status in
            
            self.fadeOutActivityIndicator()

            if status == .FAILED {
                //download failed error
                ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
            }
        }
    }
}

extension MoreMedDocCarePlanViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let str = "myCTCA Medical Documents: Care Plan"
        
        let attrs = [NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: UIFont.TextStyle.headline),
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color]
        return NSAttributedString(string: str, attributes: attrs)
    }
    
    func description(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        return GenericHelper.shared.getNoRecordsMessageWithStyle()
    }
    
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        if UIDevice.current.orientation.isLandscape {
            return UIImage(named: "myctca_logo_128")
        } else {
            return UIImage(named: "myctca_logo_256")
        }
    }
    
    override func didRotate(from fromInterfaceOrientation: UIInterfaceOrientation) {
        self.tableView.reloadData()
    }
}

