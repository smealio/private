//
//  ErrorManager.swift
//  myctca
//
//  Created by Tomack, Barry on 4/10/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

let PARSE_ERROR_CODE = 999
let DEFAULT_ERROR_CODE = 998
let API_ERROR_CODE = 997
let TIMEOUT_ERROR_CODE = 996

enum RESTResponse : Int {
    case FAILED = 0
    case SUCCESS
}

class ErrorManager {
    
    static let shared = ErrorManager()
    var telehealthFallbackUrl = ""
    
    func showServerError(error:ServerError, onView:UIViewController) {
        
        let errMessage = error.errorMessage
        GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.serverErrorTitle, andMessage: errMessage, onView: onView)
        
    }
    
    func getDefaultServerError() -> ServerError {
        return ServerError(customErrorMessage: CommonMsgConstants.serverErrorMessage)
    }
    
    func showDefaultError(onView:UIViewController) {
        let errMessage = ServerError(customErrorMessage: CommonMsgConstants.serverErrorMessage).errorMessage
        GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.serverErrorTitle, andMessage: errMessage, onView: onView)
    }
    
    func showDefaultTelehealthError(onView:UIViewController, appointment:Appointment?) {
        var alertInfo = myCTCAAlert()
        
        alertInfo.title = TelehealthMsgConstants.defaultTelehealthErrorTitle
        alertInfo.message = TelehealthMsgConstants.defaultTelehealthErrorMessage
        
        alertInfo.rightBtnTitle = "Continue"
        alertInfo.rightBtnAction = {
            if !self.telehealthFallbackUrl.isEmpty {
                AnalyticsManager.shared.trackTelehealthEvent(CTCAAnalyticsConstants.ACTION_TELEHEALTH_JOIN_ON_WEB, customInfo: nil, appointment: appointment)
                GenericHelper.shared.openInSafari(path: self.telehealthFallbackUrl)
            }
        }
        
        alertInfo.leftBtnTitle = "Not Now"

        GenericHelper.shared.showAlert(info: alertInfo)
    }
}
