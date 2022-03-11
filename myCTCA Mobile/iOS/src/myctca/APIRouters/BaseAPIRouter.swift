//
//  BaseAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 12/9/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

protocol BaseAPIRouter: URLRequestConvertible {
    var method: HTTPMethod { get }
    var path: String { get }
    var parameters: RequestParams { get }
}

enum HTTPHeaderField: String {
    case authentication = "Authorization"
    case contentType = "Content-Type"
    case acceptType = "Accept"
    case acceptEncoding = "Accept-Encoding"
}

enum ContentType: String {
    case json = "Application/json"
    case formEncode = "application/x-www-form-urlencoded"
}

enum RequestParams {
    case body(_:Parameters)
    case url(_:Parameters)
    case safari(_:Parameters)
    case mailData(NewMailInfo)
    case refillData(PrescriptionRefillRequest)
    case payloadDataArray([UserPreferencePayload])
    case csTrasmitData_url([String:String], ClinicalSummaryTrasmitInfo)
    case csDownloadData(ClinicalSummaryDownloadInfo)
    case submitANNCData(ANNCSubmissionInfo)
    case submitROIData(ROI)
    case appointmentRequest(ApptReqest)
}
