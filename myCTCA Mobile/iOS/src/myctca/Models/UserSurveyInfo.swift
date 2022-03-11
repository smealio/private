//
//  UserSurveyInfo.swift
//  myctca
//
//  Created by Manjunath K on 7/7/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class UserSurveyInfo: Codable {
    var mrn: String? = ""
    var deviceId: String? = ""
    var nextSurveyDate: String? = ""
    var surveyDue: Bool? = false
    var newPatient: Bool? = false
    var firstTimeSurvey: Bool? = false

    enum CodingKeys: String, CodingKey {
        case mrn, deviceId, nextSurveyDate
        case surveyDue, newPatient, firstTimeSurvey
    }
}

