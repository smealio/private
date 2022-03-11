//
//  MedDocsManager1.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 14/07/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

class MedDocsManager : BaseManager {
    
    var clinicalSummaryList = [ClinicalSummary]()
    var carePlan = CarePlan()
    var medDocsOriginal: [MedDocNew] = [MedDocNew]()
    var medDocs: [MedDocNew] = [MedDocNew]()
    var medDoc = MedDocNew()
    var imagingDocs: [ImagingDocNew] = [ImagingDocNew]()
    var imagingDocsOriginal: [ImagingDocNew] = [ImagingDocNew]()
    
    static let shared = MedDocsManager()
    
    func fetchClinicalSummaries(fromDate: String, toDate: String, completion: @escaping(RESTResponse) -> Void) {
        
        let payload = ["startDate":fromDate, "endDate":toDate]
        
        MedDocsStore().fetchClinicalSummaries(route: MedDocsAPIRouter.getClinicalSummaries(valueDict: payload)) { [weak self]
            response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = response {
                    self.clinicalSummaryList = list.sorted { $0.creationTime > $1.creationTime }
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func fetchClinicalSummaryData(id: String, completion: @escaping(String?, RESTResponse) -> Void) {
        let payload = ["DocumentId": id, "DirectAddress": ""]
        
        MedDocsStore().fetchClinicalSummaryData(route: MedDocsAPIRouter.getClinicalSummaryData(valueDict: payload)) { [weak self]
            response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(nil, .FAILED)
            } else {
                if let data = response {
                    completion(data, .SUCCESS)
                } else {
                    completion(nil, .SUCCESS)
                }
            }
        }
    }
    
    func transmitCSDoc(isSecure: Bool, transmitInfo: ClinicalSummaryTrasmitInfo,
                       completion: @escaping(Bool, ServerException?, RESTResponse) -> Void) {
        var payload = [String:String]()
        
        if isSecure {
            payload["barracuda"] = "true"
        } else {
            payload["barracuda"] = "false"
        }
        
        MedDocsStore().transmitCSDoc(route: MedDocsAPIRouter.transmitClinicalSummary(valueDict: payload, request: transmitInfo)) { [weak self]
            
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
    
    func downloadCSDoc(downloadInfo: ClinicalSummaryDownloadInfo,
                       completion: @escaping(String?, RESTResponse) -> Void) {
        
        PDFDownloadStore().downloadReports(route: MedDocsAPIRouter.downloadClinicalSummary(request: downloadInfo), filename: MyCTCAConstants.FileNameConstants.CSZipName) { [weak self]
            newFile, sError, status in
            
            guard let self = self else { return }
            
            if status == .SUCCESS {
                if let fileurl = newFile {
                    if !TestSetupManager.shared.testMode {
                        print("success")
                    }
                    completion(fileurl, status)
                } else {
                    self.serverError = ErrorManager.shared.getDefaultServerError()
                    completion(nil, .FAILED)
                }
            } else {
                self.serverError = sError
                completion(nil, status)
            }
        }
    }
    
    func downloadCarePlan(completion: @escaping(RESTResponse) -> Void) {
        downloadFileName = MyCTCAConstants.FileNameConstants.CarePlanPDFName
        
        downloadReports(router: MedDocsAPIRouter.downloadCarePlan, url: nil) {
            status in
            completion(status)
        }
    }
    
    func fetchCarePlanDetails(completion: @escaping(Bool, RESTResponse) -> Void) {
        MedDocsStore().fetchCarePlanDetails(route: MedDocsAPIRouter.getCarePlanDetails) { [weak self]
            carePlan, noDataString, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(false, .FAILED)
            } else {
                if let plan = carePlan {
                    self.carePlan.documentText = plan
                    completion(true, .SUCCESS)
                } else {
                    completion(false, .SUCCESS)
                }
            }
        }
    }
    
    func fetchMedDocs(type: MedDocType, completion: @escaping(RESTResponse) -> Void)  {
        
        var typeString = ""
        switch (type) {
        case .clinical:
            typeString = "clinical"
        case .radiation:
            typeString = "radiation"
        case .integrative:
            typeString = "integrative"
        default:
            break
        }
        
        MedDocsStore().fetchMedDocs(route: MedDocsAPIRouter.getmedicaldocuments(type: typeString)) { [weak self]
            response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = response {
                    self.medDocs = list.sorted { $0.docAuthoredDate > $1.docAuthoredDate }
                    self.medDocsOriginal = list.sorted { $0.docAuthoredDate > $1.docAuthoredDate
                    }
                    completion(.SUCCESS)
                }
            }
        }
    }
    
    func fetchMedDoc(type: MedDocType, docId: String, completion: @escaping(RESTResponse) -> Void)  {
        
        var typeString = ""
        switch (type) {
        case .clinical:
            typeString = "clinical"
        case .radiation:
            typeString = "radiation"
        case .integrative:
            typeString = "integrative"
        default:
            break
        }
        
        MedDocsStore().fetchMedDoc(route: MedDocsAPIRouter.getmedicaldocument(type: typeString, id: docId)) { [weak self]
            response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let doc = response {
                    self.medDoc = doc
                }
                completion(.SUCCESS)
            }
        }
    }
    
    func downloadMedDocs(type: MedDocType, docId: String, docName:String, completion: @escaping(RESTResponse) -> Void)  {
        downloadFileName = docName
        
        var typeString = ""
        switch (type) {
        case .clinical:
            typeString = "clinical"
        case .radiation:
            typeString = "radiation"
        case .integrative:
            typeString = "integrative"
        default:
            break
        }
        
        var route = MedDocsAPIRouter.none
        
        if type == .imaging {
            route = MedDocsAPIRouter.downloadimagingdocumentreport(id: docId)
        } else {
            route = MedDocsAPIRouter.downloadmedicaldocumentreport(type: typeString, id: docId)
        }

        downloadReports(router: route, url: nil) {
            status in
            completion(status)
        }
    }
    
    func fetchImagingDocs(completion: @escaping(RESTResponse) -> Void)  {
        
        MedDocsStore().fetchImagingDocs(route: MedDocsAPIRouter.getimagingdocuments) { [weak self]
            response, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = response {
                    self.imagingDocs = list.sorted { $0.documentDate > $1.documentDate }
                    self.imagingDocsOriginal = list.sorted { $0.documentDate > $1.documentDate
                    }
                    completion(.SUCCESS)
                }
            }
        }
    }
}
