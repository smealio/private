//
//  FormsManager.swift
//  myctca
//
//  Created by Manjunath K on 7/21/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class FormsManager : BaseManager {
    
    static let shared = FormsManager()
    var roiFormInfo = ROIFormInfo()
        
    func checkForExistingANNC(completion: @escaping(Bool?, RESTResponse) -> Void) {
        
        FormsStore().checkForExistingANNC(route: FormsAPIRouter.checkForExistingANNC) { [weak self]
             response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(nil, .FAILED)
            } else {
                if let retVal = response {
                    completion(retVal, .SUCCESS)
                } else {
                    completion(false, .SUCCESS)
                }
            }
        }
    }
    
    func fetchMRNNumber(completion: @escaping(String?, RESTResponse) -> Void) {
        FormsStore().fetchMRNNumber(route: FormsAPIRouter.getMRNNumber) { [weak self]
             response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(nil, .FAILED)
            } else {
                completion(response, .SUCCESS)
            }
        }
    }
    
    func submitANNCForm(anncInfo: ANNCSubmissionInfo, completion: @escaping(Bool, ServerException?, RESTResponse) -> Void) {
        FormsStore().submitForm(route: FormsAPIRouter.submitANNC(request: anncInfo)) { [weak self]
            
            result, exception, error, status in
            
            guard let self = self else { return }
            
            if status == .SUCCESS {
                if !result {
                    completion(false, exception!, .SUCCESS)
                } else {
                    completion(true, nil, .SUCCESS)
                }
            } else {
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(false, nil, .FAILED)
                }
            }
        }
    }
    
    func downloadANNCForm(completion: @escaping(RESTResponse) -> Void) {
        downloadFileName = MyCTCAConstants.FileNameConstants.ANNCPDFName
        downloadReports(router: FormsAPIRouter.downloadANNC, url: nil) {
            status in
            completion(status)
        }
    }
    
    func fetchROIFormInfo(completion: @escaping(RESTResponse) -> Void) {
        FormsStore().fetchROIFormInfo(route: FormsAPIRouter.getROIFormInfo) { [weak self]
            
            result, error, status in
            
            guard let self = self else { return }
            
            if status == .SUCCESS, let info = result {
                self.roiFormInfo = info
                completion(.SUCCESS)
            } else {
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(.FAILED)
                }
            }
        }
    }
    
    func submitROIForm(roiInfo: ROI, completion: @escaping(Bool, ServerException?, RESTResponse) -> Void) {
        FormsStore().submitForm(route: FormsAPIRouter.submitROI(request: roiInfo)) { [weak self]
            
            result, exception, error, status in
            
            guard let self = self else { return }
            
            if status == .SUCCESS {
                if !result {
                    completion(false, exception!, .SUCCESS)
                } else {
                    completion(true, nil, .SUCCESS)
                }
            } else {
                if let sError = error, status == .FAILED {
                    self.serverError = sError
                    completion(false, nil, .FAILED)
                }
            }
        }
    }
    
    func downloadROIForm(completion: @escaping(RESTResponse) -> Void) {
        downloadFileName = MyCTCAConstants.FileNameConstants.ROIPDFName
        downloadReports(router: FormsAPIRouter.downloadROI, url: nil) {
            status in
            completion(status)
        }
    }

}
