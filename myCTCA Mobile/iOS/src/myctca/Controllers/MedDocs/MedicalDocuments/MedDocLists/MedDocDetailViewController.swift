//
//  MedDocDetailViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/15/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit
import WebKit

class MedDocDetailViewController: UIViewController, CTCAViewControllerProtocol, WKUIDelegate, WKNavigationDelegate {

    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var downloadButton: UIButton!
    
    var webView: WKWebView?
    var webViewConstraints: [NSLayoutConstraint] = []
    
    var textView: UITextView?
    var textViewConstraints: [NSLayoutConstraint] = []
    
    var medDocType: MedDocType? 
    var medDoc: MedDocNew?
    var imagingDoc: ImagingDocNew?
    
    let medDocsFetchErrorTitle: String = "Medical Doc Error"
    let medDocsFetchErrorResponse: String = "There seems to be some kind of problem retrieving medical document detail data. You can try again later or call the Care Manager directly."
    
    let medDocsManager = MedDocsManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if (medDocType == .imaging) {
            if (imagingDoc != nil) {
                self.title = imagingDoc!.itemName
            } else {
                self.title = ""
            }
        } else {
            if (medDoc != nil) {
                self.title = medDoc!.docName
            } else {
                self.title = ""
            }
        }
        
        switch medDocType {
        case .clinical:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_CLINICAL_DETAIL_VIEW)
        case .radiation:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_RADIATION_DETAIL_VIEW)
        case .imaging:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_IMAGING_DETAIL_VIEW)
        case .integrative:
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_INTEGRATIVE_DETAIL_VIEW)
        default:
            break
        }
        self.prepareView()
        
        medDocsManager.hostViewController = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if (medDocType == .imaging) {
            self.textView!.isScrollEnabled = true
        }
    }
    
    func prepareView() {
        if (medDocType == .imaging) {
            self.textView = UITextView()
            self.view.addSubview(self.textView!)
            self.textView!.isScrollEnabled = false
            
            setUpTextViewConstraints()
            
            self.textView!.backgroundColor = UIColor.white
            self.textView!.font = UIFont(name: "HelveticaNeue", size: 15)
            
            self.view.sendSubviewToBack(self.textView!)
            
            if (imagingDoc != nil) {
                self.textView!.text = imagingDoc!.notes
                
                self.cancelButton.isHidden = false
                self.downloadButton.isHidden = false
            }
            
        } else {
            self.webView = WKWebView()
            self.view.addSubview(self.webView!)
            
            setUpWebViewConstraints()
            
            self.webView!.uiDelegate = self
            self.webView!.navigationDelegate = self
            
            self.webView!.backgroundColor = UIColor.white
            self.webView!.scrollView.backgroundColor = UIColor.white
            
            self.view.sendSubviewToBack(self.webView!)
            
            loadViewData()
        }
    }
    
    override func loadViewData() {
        if (medDocType != .imaging) {
            NetworkStatusManager.shared.registerForReload(view: self)
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            fetchMedDoc()
        }
    }
    
    func setUpWebViewConstraints() {
        
        self.webView!.translatesAutoresizingMaskIntoConstraints = false
        
        self.webView!.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 0.0).isActive = true
        self.webView!.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: 0.0).isActive = true
        self.webView!.topAnchor.constraint(equalTo: self.view.topAnchor, constant:20.0).isActive = true
        self.webView!.bottomAnchor.constraint(equalTo: downloadButton.topAnchor, constant:-10.0).isActive = true
    }
    
    func setUpTextViewConstraints() {
        
        self.textView!.translatesAutoresizingMaskIntoConstraints = false
        
        self.textView!.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 20.0).isActive = true
        self.textView!.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: -20.0).isActive = true
        self.textView!.topAnchor.constraint(equalTo: self.view.topAnchor, constant:20.0).isActive = true
        self.textView!.bottomAnchor.constraint(equalTo: downloadButton.topAnchor, constant:-10.0).isActive = true
    }
    
    func fetchMedDoc() {
        
        var actMessage = ""
        switch medDocType {
        case .clinical:
            actMessage = ActivityIndicatorMsgs.retriveSClinicalText
        case .radiation:
            actMessage = ActivityIndicatorMsgs.retriveSRadiationText
        case .imaging:
            actMessage = ActivityIndicatorMsgs.retriveSImagingText
        case .integrative:
            actMessage = ActivityIndicatorMsgs.retriveSIntegrativeText
        default:
            break
        }
        
        self.showActivityIndicator(view: self.view, message: actMessage)
        
        medDocsManager.fetchMedDoc(type: medDocType!, docId: medDoc!.docId) {
            status in
            
            self.fadeOutActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
            } else {
                self.medDoc = self.medDocsManager.medDoc;
                
                if let html = self.medDocsManager.medDoc.docText {
                    let newHead = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, user-scalable=yes'><style>body{font-family: Sans-Serif;}p{padding-bottom: 15px;}</style></head><body>"
                    let htmlString = "\(newHead)\(html)</body></html>"
                    print("htmlString: \(htmlString)")
                    self.webView!.loadHTMLString(htmlString, baseURL: nil)
                }
                
                self.cancelButton.isHidden = false
                self.downloadButton.isHidden = false
            }
        }
    }
    
    func dismissThis(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func downloadTapped(_ sender: Any) {
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }

        var actMessage = "", id = "", name = ""
        switch medDocType {
        case .clinical:
            actMessage = ActivityIndicatorMsgs.downloadClinicalText
            id = medDoc!.docId
            name = medDoc!.docName
        case .radiation:
            actMessage = ActivityIndicatorMsgs.downloadRadiationText
            id = medDoc!.docId
            name = medDoc!.docName
        case .imaging:
            actMessage = ActivityIndicatorMsgs.downloadImagingText
            id = imagingDoc!.id
            name = imagingDoc!.itemName
        case .integrative:
            actMessage = ActivityIndicatorMsgs.downloadIntegrativeText
            id = medDoc!.docId
            name = medDoc!.docName
        default:
            break
        }
        
        self.showActivityIndicator(view: self.view, message: actMessage)
        medDocsManager.downloadMedDocs(type: medDocType!, docId: id, docName: name) {
            status in
            
            self.fadeOutActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.medDocsManager.getLastServerError(), onView: self)
            }
        }
    }
    
    @IBAction func cancelTapped(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }

}
