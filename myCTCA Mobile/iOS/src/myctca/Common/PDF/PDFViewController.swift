//
//  PDFViewController.swift
//  myctca
//
//  Created by Manjunath K on 8/7/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit
import PDFKit

enum PDFDocType: Int {
    case multipleAppointments = 1
    case labResults
    case ROI
    case ANNC
    case other
    case singlAppointment
}

class PDFViewController: CTCABaseViewController {
    
    var fileURL:URL?
    @IBOutlet weak var pdfDisplayView: UIView!
    @IBOutlet weak var topView: UIView!
    @IBOutlet weak var bottomView: UIView!
    @IBOutlet weak var uploadBtn: UIButton!
    @IBOutlet weak var titleLabel: UILabel!
    
    var pdfDocType:PDFDocType = .other
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        showPDF()
        
        topView.backgroundColor = self.presentedViewController?.navigationController?.navigationBar.backgroundColor
        bottomView.backgroundColor = self.presentedViewController?.navigationController?.navigationBar.backgroundColor
        
        uploadBtn.tintColor = MyCTCAColor.ctcaSecondGreen.color

    }
    
    func showPDF() {
        if let pdfURL = fileURL {
            DispatchQueue.main.async {
                
                // Add PDFView to view controller.
                if #available(iOS 11.0, *) {
                    let pdfView = PDFView(frame: self.pdfDisplayView.bounds)
                    
                    pdfView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
                    self.pdfDisplayView.addSubview(pdfView)
                    
                    // Fit content in PDFView.
                    pdfView.autoScales = true
                    
                    // Load Sample.pdf file from app bundle.
                    pdfView.document = PDFDocument(url: pdfURL)

                } else {
                    GenericHelper.shared.showAlert(withtitle: "ROI Form", andMessage: "You cannot open file in this version of the iOS.", onView:self)
                }
                
            }
        }
    }
    
    @IBAction func sctionButtonTapped(_ sender: Any) {
        if let pdfURL = fileURL {
            
            DispatchQueue.main.async {
                
                // Add PDFView to view controller.
                if #available(iOS 11.0, *) {
                    
                    let activityController = UIActivityViewController(activityItems: [pdfURL], applicationActivities: nil)
                    
                    //For ipad
                    if let popoverController = activityController.popoverPresentationController {
                        popoverController.sourceRect = CGRect(x: UIScreen.main.bounds.width / 2, y: UIScreen.main.bounds.height / 2, width: 0, height: 0)
                        popoverController.sourceView = self.view
                        popoverController.permittedArrowDirections = UIPopoverArrowDirection(rawValue: 0)
                    }
                    
                    self.present(activityController, animated: true, completion: nil)
                    
                    activityController.completionWithItemsHandler = { activity, success, items, error in
                        if !success{
                            return
                        } else if let userActivity = activity {
                            self.logEventForActivity(userActivity)
                        }
                    }
                } else {
                    GenericHelper.shared.showAlert(withtitle: "ROI Form", andMessage: "You cannot open file in this version of the iOS.", onView:self)
                }
                
            }
        }
    }
    @IBAction func cancelTapped(_ sender: Any) {
        if pdfDocType == .singlAppointment {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_PDF_CLOSE_SHARE_TAP)
        }
        dismiss(animated: true, completion: nil)
    }
    
    func logEventForActivity(_ activity:UIActivity.ActivityType) {
        switch activity {
        case .print:
            switch pdfDocType {
            case .multipleAppointments:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_PDF_PRINT_TAP)
            case .labResults:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_LAB_RESULTS_PDF_PRINT_TAP)
            case .ROI:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ROI_PDF_PRINT_TAP)
            case .ANNC:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ANNC_PDF_PRINT_TAP)
            case .singlAppointment:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENT_PDF_PRINT_TAP)
            default:
                break
            }
            
        case .copyToPasteboard:
            switch pdfDocType {
            case .multipleAppointments:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENTS_PDF_COPY_TAP)
            case .labResults:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_LAB_RESULTS_PDF_COPY_TAP)
            case .ROI:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ROI_PDF_COPY_TAP)
            case .ANNC:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ANNC_PDF_COPY_TAP)
            case .singlAppointment:
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENT_PDF_COPY_TAP)
            default:
                break
            }
        case .mail:
            if pdfDocType == .singlAppointment {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENT_PDF_MAIL_TAP)
            }
        case .airDrop:
            if pdfDocType == .singlAppointment {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENT_PDF_AIRDROP_TAP)
            }
        default:
            if activity.rawValue == "com.apple.DocumentManagerUICore.SaveToFiles" {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENT_PDF_SAVE_TAP)
            } else {
                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_APPOINTMENT_PDF_OTHER_ACTION_TAP, ["action_performed" : activity.rawValue])
            }
        }
    }
}
