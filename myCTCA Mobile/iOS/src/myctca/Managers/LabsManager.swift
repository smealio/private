//
//  LabsManager.swift
//  myctca
//
//  Created by Manjunath K on 12/10/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class LabsManager: BaseManager {
    
    var labResults:[LabResult]?
    var labResultsOriginal:[LabResult]?
    
    static let shared = LabsManager()

    func fetchLabResults(completion: @escaping(RESTResponse) -> Void) {

        LabsStore().fetchLabResuts(route: LabsAPIRouter.getLabReports) {  [weak self]
             result, error, status  in
            
            guard let self = self else { return }
            
            if let sError = error, status == .FAILED {
                self.serverError = sError
                completion(.FAILED)
            } else {
                if let list = result {
                    let sortedList = list.sorted { $0.performedDate! > $1.performedDate! }
                    self.labResults = sortedList
                    self.labResultsOriginal = sortedList
                }
                completion(.SUCCESS)
            }
        }
    }

    func downloadLabReport(report: LabResult, completion: @escaping(String?, RESTResponse) -> Void) {

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")

        let perfDate: String = dateFormatter.string(from: report.performedDate!)

        var params = [String:String]()
        params["performeddate"] = perfDate
        params["collectedby"] = report.collectedBy

        LabsStore().downloadLabReport(route: LabsAPIRouter.downloadLabReports(valueDict: params), filename: MyCTCAConstants.FileNameConstants.LabReportsPDFName) { [weak self]
            newFile, sError, status in
            
            guard let self = self else { return }

            if status == .SUCCESS {
                completion(newFile!, status)
            } else {
                self.serverError = sError
                completion(nil, status)
            }
        }
    }
}

