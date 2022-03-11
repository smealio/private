//
//  BaseManager.swift
//  myctca
//
//  Created by Manjunath K on 1/4/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class BaseManager {
    
    var serverError:ServerError?
    var downloadFileName = ""
    var hostViewController = UIViewController()

    func downloadReports(router:BaseAPIRouter?, url:String?, pdfDocType: PDFDocType = .other, completion: @escaping(RESTResponse) -> Void) {
        if let route = router {
            
            PDFDownloadStore().downloadReports(route: route, filename: downloadFileName) { [weak self]
                newFile, sError, status in
                
                guard let self = self else { return }

                if status == .SUCCESS {
                    if let fileurl = URL(string: newFile!) {
                        if !TestSetupManager.shared.testMode {
                            self.showPDF(fileURL: fileurl, pdfDocType:pdfDocType)
                        }
                        completion(status)
                    } else {
                        self.serverError = ErrorManager.shared.getDefaultServerError()
                        completion(.FAILED)
                    }
                } else {
                    self.serverError = sError
                    completion(status)
                }
            }
        } else if let fileUrlPath = url {
            
            PDFDownloadStore().downloadReports(urlPath: fileUrlPath, filename: downloadFileName) {
                newFile, sError, status in
                if status == .SUCCESS {
                    if let fileurl = URL(string: newFile!) {
                        if !TestSetupManager.shared.testMode {
                            self.showPDF(fileURL: fileurl, pdfDocType:pdfDocType)
                        }
                        completion(status)
                    } else {
                        self.serverError = ErrorManager.shared.getDefaultServerError()
                        completion(.FAILED)
                    }
                } else {
                    self.serverError = sError
                    completion(status)
                }
            }
        }
    }
    
    func showPDF(fileURL:URL, pdfDocType: PDFDocType) {
        
        DispatchQueue.main.async {
            [weak self] in
            
            guard let self = self else { return }
            
            let pdfViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PDFViewController") as! PDFViewController
            
            pdfViewController.fileURL = fileURL
            self.hostViewController.present(pdfViewController, animated: true, completion: {
                pdfViewController.titleLabel.text = self.downloadFileName
                pdfViewController.pdfDocType = pdfDocType
            })
        }
    }
    
    func getLastServerError() -> ServerError {
        if let error = serverError {
            return error
        }
        return ErrorManager.shared.getDefaultServerError()
    }
}
