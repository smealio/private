//
//  CTCADownloadProtocol.swift
//  myctca
//
//  Created by Manjunath K on 11/19/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

protocol CTCADownloadProtocol : CTCAViewControllerProtocol {
}

extension CTCADownloadProtocol {
    
    func downloadReports(forView:UIViewController, downloadUrl:String?, filename:String?) {
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        if let withUrl = downloadUrl, let file = filename {
            
            let tempDirectoryURL = NSURL.fileURL(withPath: NSTemporaryDirectory(), isDirectory: true)
            let targetURL = tempDirectoryURL.appendingPathComponent("\(file)")
            
            if FileManager.default.fileExists(atPath: (targetURL.path)) {
                //delete path
                do {
                    try FileManager.default.removeItem(at: targetURL)
                } catch {
                    print("downloadButtonTapped: \(error)")
                    return
                }
            }
            
            self.showActivityIndicator(view: forView.view, message: "Downloading document…")
            
            let pdfClient = PDFClient()
            pdfClient.downloadPDF(withEndPoint:withUrl, toPath: targetURL,
                                  completion: { (status: Bool, localPath: URL?, error: ServerError?) in
                                    
                                    self.dismissActivityIndicator()
                                    
                                    if !status {
                                        //download failed error
                                        ErrorManager.shared.showServerError(error: error!, onView: forView)
                                        
                                    } else  {
                                        self.showPDF(forView:forView, fileURL: targetURL, fileName:file)
                                    }
                                  })
        } else {
            GenericHelper.shared.showAlert(withtitle: "Error", andMessage: "Download cannot be initiated", onView: forView)
        }
    }
    
    func showPDF(forView:UIViewController, fileURL:URL, fileName:String) {
        
        DispatchQueue.main.async {
            let pdfViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PDFViewController") as! PDFViewController
            
            pdfViewController.fileURL = fileURL
            forView.present(pdfViewController, animated: true, completion: nil)
            pdfViewController.titleLabel.text = fileName
            pdfViewController.pdfDocType = .other
        }
    }
}
