//
//  ROIException.swift
//  myctca
//
//  Created by Manjunath K on 8/7/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

class FormExceptionErrorMessage {
    var errorMessage = ""
    
    init(formData: [String: AnyObject]) {
        if let msg = formData["errorMessage"] as? String {
            self.errorMessage = msg
        }
    }
}

class FormExceptionError {
    var value = ""
    var errors:[FormExceptionErrorMessage] = [FormExceptionErrorMessage]()
    
    init?(formData: [String: AnyObject]) {
        if let msg = formData["value"] as? String {
            self.value = msg
        }
        
        if let list = formData["errors"] as? [AnyObject] {
            for item in list {
                let model = FormExceptionErrorMessage(formData: item as! [String: AnyObject])
                self.errors.append(model)
                break //just first one
            }
        }
    }
}

class FormException {
    var exceptionFor = ""
    var exception:FormExceptionError?
}

