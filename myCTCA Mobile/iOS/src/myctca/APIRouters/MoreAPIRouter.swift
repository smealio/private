//
//  MoreAPIRouter.swift
//  myctca
//
//  Created by Manjunath K on 1/19/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation
import Alamofire

enum MoreAPIRouter: BaseAPIRouter {
    
    case downloadUserGuide
    case openFAQ
    case downloadYourHealth
    case downloadNutritionBasics
    case downloadYourMenu
    case downloadSymtmMagmt
    case downloadEatingChallenge
    case openCTCANews
    case openReferAFriend
    case openCovid19
    case openPatientInfo
    case openExtFAQ
    case openContacts
    case openTogether
    case openClinicianBios
    case openNewsletter
    case openFindInfo
    case openClinicalTrials
    case openBlogs
    case openHospital
    case openHospitalActvty
    case openSideEffects
    case openRecipes
    case openVHelpU
    case openMember
    case openJoinNow
    case openAmericanCancerSociety
    case openAmericanLungAssociation
    case openCancerSupportCommunities
    case openBillPay
    case getCertificateText
    case getCertificateImage
    case getActivityLogs(valueDict:[String:String])
    case downloadSITDoc
    case getSymptomInventory
    case getexternallinks
        
    // MARK: - HTTPMethod
    var method: HTTPMethod {
        switch self {
        default:
            return .get
        }
    }
    
    // MARK: - Parameters
    var parameters: RequestParams {
        switch self {
        case .openFAQ, .getCertificateImage, .getCertificateText:
            return .safari([:])
        case .getActivityLogs(let valueDict):
            return .url(valueDict)
        default:
            return .body([:])
        }
    }
    
    // MARK: - Path
    var path: String {
        switch self {
        case .downloadUserGuide:
            return "/support/getiosuserguideasync"
        case .openFAQ:
            return "/support/getportalfaqsasync"
        case .downloadYourHealth:
            return "nutrition/getyourhealthyourfutureasync"
        case .downloadNutritionBasics:
            return "/nutrition/getnutritionbasicsasync"
        case .downloadYourMenu:
            return "/nutrition/getenhanceyourmenuasync"
        case .downloadSymtmMagmt:
            return "/nutrition/getsymptomcontrolasync"
        case .downloadEatingChallenge:
            return "/nutrition/geteatingischallengeasync"
        case .openCTCANews:
            return "https://www.cancercenter.com/community"
        case .openReferAFriend:
            return "https://www.cancerfighter.com/give-support/refer-a-friend"
        case .openCovid19:
            return "https://www.cancercenter.com/covid19"
        case .openPatientInfo:
            return "https://www.cancercenter.com/covid19/patient-information"
        case .openExtFAQ:
            return "https://www.cancercenter.com/covid19/frequently-asked-questions"
        case .openContacts:
            return "https://www.cancercenter.com/covid19/contacts"
        case .openTogether:
            return "https://www.cancercenter.com/together"
        case .openClinicianBios:
            return "https://www.cancercenter.com/physician-directory"
        case .openNewsletter:
            return "https://www.cancercenter.com/"
        case .openFindInfo:
            return "https://www.cancercenter.com/cancer-types"
        case .openClinicalTrials:
            return "https://www.cancercenter.com/clinical-trials"
        case .openBlogs:
            return "https://www.cancercenter.com/community/blog"
        case .openHospital:
            return "https://www.cancercenter.com/locations/phoenix"
        case .openHospitalActvty:
            return "https://www.cancercenter.com/become-a-patient/patient-experience/amenities"
        case .openSideEffects:
            return "https://www.cancercenter.com/integrative-care"
        case .openRecipes:
            return "https://www.cancercenter.com/community/recipes"
        case .openVHelpU:
            return "https://www.cancerfighter.com/how-we-help-you"
        case .openMember:
            return "https://www.cancerfighter.com/get-support/why-become-a-member"
        case .openJoinNow:
            return "https://ctca.force.com/cancerfighters/s/self-registration"
        case .openAmericanCancerSociety:
            return "https://www.cancer.org"
        case .openAmericanLungAssociation:
            return "https://www.lung.org"
        case .openCancerSupportCommunities:
            return "https://www.cancersupportcommunity.org"
        case .openBillPay:
            return "https://www.patientportal.me/CTCA/Patient/login.html"
        case .getCertificateText:
            return "/support/getsitecertificationasync"
        case .getCertificateImage:
            return "/support/getsitecertificationimageasync"
        case .getActivityLogs:
            return "/userprofile/getuseractivitylogs"
        case .downloadSITDoc:
            return "/medicaldocuments/getsitpdf"
        case .getSymptomInventory:
            return "/medicaldocuments/getpatientreporteddocuments"
        case .getexternallinks:
            return "/support/getexternallinkurls"
        }
    }

    // MARK: - URLRequestConvertible
    func asURLRequest() throws -> URLRequest {
        let url = URL(string: AppSessionParameters().portalDataServer)
        
        var urlRequest = URLRequest(url: url!.appendingPathComponent(path))
        
        // HTTP Method
        urlRequest.httpMethod = method.rawValue
        
        // Common Headers
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.acceptType.rawValue)
        urlRequest.setValue(ContentType.json.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
        urlRequest.setValue(AppSessionManager.shared.getBearerToken(), forHTTPHeaderField: HTTPHeaderField.authentication.rawValue)
        
        // Parameters
        switch parameters {
        case .body(let params):
            if params.count > 0 {
                urlRequest.setValue(ContentType.formEncode.rawValue, forHTTPHeaderField: HTTPHeaderField.contentType.rawValue)
                let parameterArray = params.map { (arg) -> String in
                    
                    let (key, value) = arg
                    let valStr = value as! String
                    let percentVal = String(describing: valStr.addingPercentEncoding(withAllowedCharacters: .alphanumerics)!)
                    return "\(key)=\(percentVal)"
                }
                let HTTPBody = parameterArray.joined(separator: "&").data(using: .utf8)
                urlRequest.httpBody = HTTPBody
            } else {
                urlRequest.httpBody = nil
            }
            
        case .url(let params):
            let queryParams = params.map { pair  in
                return URLQueryItem(name: pair.key, value: String(describing: pair.value))
            }
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)
            components?.queryItems = queryParams
            urlRequest.url = components?.url
            
        default:
            break
        }
        
        return urlRequest
    }
    
    func asUrl() -> URL? {
        var baseurl = ""
        switch self {
        default:
            baseurl = AppSessionParameters().portalDataServer
        }
        
        var url = URL(string: baseurl)
        
        switch parameters {
        case .safari(let params):
            var components = URLComponents(string:url!.appendingPathComponent(path).absoluteString)

            if params.count > 0 {
                let queryParams = params.map { pair  in
                    return URLQueryItem(name: pair.key, value: String(describing: pair.value))
                }
                components?.queryItems = queryParams
            }
            
            url = components?.url
        default:
            break
        }
    
        return url
    }
}

