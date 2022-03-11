//
//  MyCTCAUser.swift
//  myctca
//
//  Created by Manjunath K on 3/8/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class MyCTCAUser {
    // The current AccessToken is stored in the AppSessionManager to be accessed
    // from all of the networkorking client classes throughout the app
    var accessToken: AccessToken?
    
    // The Identity User data represents the logged in user and the data associated with their CTCA account
    var iUser: IdentityUser?
    
    // CareTeam of the currently displayed patient. Used when sending new secure mail.
    //TODO: Contigency for more than one Care Team. Not at issue at this time because th current version
    // of SCM (AllScripts) does not handle more than one care team per patient.
    // Proposed a dictionary with key of system id and value of care team object.
    // Not sure how to relate Care Team with specific cancer patient is treated for.
    //
    var careTeams: [CareTeam]?
    
    // UserProfile contains data specific to the user on the patient portal.
    // That includes the user permissions of what they are allowed to view and
    // the proxy user(s) (if any) that indicate other patient data that the user
    // is allowed to view
    var userProfile: MyCTCAUserProfile?
    
    // Data associated with the primary facility that the patient is treated at
    // such as the address and important phone numbers.
    var primaryFacility: Facility?
    
    // This might come in more handy when the ability to change displayed patient data is ready
    var currentUserId: String = ""
    
    // This marks when the current session was created. Since the current session length isn't
    // needed right no, it isn't necessary.
    var createdAt: Date = Date()
    
    //List of all facility names. this is for Appt details and Contact us screens.
    var allFacilitesNamesList = [String:String]()
    var allFacilitesList = [Facility]()
    
    // sessionLength is not being used for anything. It was important for Connect
    var sessionLength: TimeInterval {
        get {
            return Date().timeIntervalSince(createdAt)
        }
    }
    
    var currentUserPref = [UserPreference]()
    
    var sessionIdleTimer: AppSessionIdleTimer?
    
    var sessionState:MyCTCASessionState = .INACTIVE
    
    var userPermissions: [String] = [String]()
    
    var userContacts:MyCTCAUserContacts?
    
    private var usersPreferedContactnumber:String?
    
    func getUsersPreferedContactnumber() -> String {
        if let number = usersPreferedContactnumber {
            return number
        }
        usersPreferedContactnumber = ""
        if let contacts = userContacts?.phoneNumbers, contacts.count > 0 {
            let keys = ["HOME","CELLULAR","ALT","BUISINESS","INTERNATIONAL","OFFICE","TEMPORARY","OTHER","WORK"]
            for key in keys {
                for item in contacts {
                    if item.phoneType == key {
                        usersPreferedContactnumber = item.phone
                        break
                    }
                }
                if !(usersPreferedContactnumber!.isEmpty) {
                    usersPreferedContactnumber = usersPreferedContactnumber?.trimmingCharacters(in: .whitespacesAndNewlines)
                    break
                }
            }
        }
        usersPreferedContactnumber = PhoneNumberFormatter.shared.format(phoneNumber: usersPreferedContactnumber!)
        return usersPreferedContactnumber!
    }
    
    func getUsersPreferedEmailId() -> String {
        if let email = userContacts?.emailAddress {
            return email
        }
        return ""
    }
    
    func cleanup() {
        userContacts = nil
        accessToken = nil
        iUser = nil
        careTeams = nil
        userProfile = nil
        primaryFacility = nil
        allFacilitesNamesList.removeAll()
        allFacilitesList.removeAll()
        usersPreferedContactnumber = nil
    }
}
