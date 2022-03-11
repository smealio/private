//
//  MoreChangePatientViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 4/12/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreChangePatientViewController: UIViewController, CTCAViewControllerProtocol {

    @IBOutlet weak var tableView: UITableView!

    var viewablePatients: [String] = [String]()
    
    let cellDefaultHeight: CGFloat = 44.0
    let headerHeight: CGFloat = 75.0
    
    override func viewDidLoad() {
        super.viewDidLoad()

//        if (AppSessionManager.shared.currentUser.userProfile != nil) {
//            viewablePatients = (AppSessionManager.shared.currentUser.userProfile?.viewablePatients())!
//        }
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        self.tableView.sectionHeaderHeight = UITableView.automaticDimension;
        self.tableView.estimatedSectionHeaderHeight = headerHeight;
        
        self.title = "Change Patient"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func changeDisplayedPatientData(_ indexPath: IndexPath) {
        
        let loginParameters: LoginParameters = BiometricManager.shared.loginParameters
        let ctcaUniqueId: String = (AppSessionManager.shared.currentUser.userProfile?.getCTCAUniqueIdFromFullName(viewablePatients[indexPath.row]))!
        
        let authClient = AuthenticationClient()
        self.showActivityIndicator(view: self.view, message: "Switching to new patient…")
        authClient.switchToProxyUser(parameters: loginParameters, ctcaUniqueId: ctcaUniqueId, completion: { (accessToken: AccessToken?, error: ServerError?, errorDesc: String?) in
            
            if (error == nil) {
                print("Got New Token: \(String(describing: accessToken!))")
//                self.retrieveNewUserProfileData(accessToken: accessToken!, completion: { (success, error) in
//                    if (success) {
//                        AppSessionManager.shared.beginCurrentSession()
//                        _ = accessToken!.isExpired()
//                        DispatchQueue.main.async(execute: {
//                            self.dismissActivityIndicator(completion: {
//                                print("All Done")
//                                self.tabBarController!.selectedIndex = 0
//                            })
//                        })
//                    } else {
//                        //TODO: ERROR HANDLING
//                    }
//                })
            } else {
                
            }
        })
    }
    
//    func retrieveNewUserProfileData(accessToken: AccessToken, completion: @escaping (Bool, ServerError?) -> Void) {
//        print("retrieveUserProfile +++++++++++++++++++++++++++++++")
//        let authClient = AuthenticationClient()
//
//        let loginParameters = LoginParameters()
//
//        authClient.retrieveUserProfile(accessToken:accessToken, parameters: loginParameters,
//                                       completion: { (userProfile: MyCTCAUserProfile?, error: ServerError?, status: RESTResponse) in
//
//                                        if (status == .SUCCESS) {
//                                            if userProfile == nil {
//                                                //TODO: ERROR HANDLING
//                                                completion(false, error)
//                                            } else {
//                                                AppSessionManager.shared.userProfile = userProfile!
//                                                AppSessionManager.shared.currentUserId = userProfile!.epiId
//                                                completion(true, nil)
//                                            }
//                                        } else {
//                                            // Show Error
//                                            ErrorManager.shared.showServerError(error: error!, onView: self)
//                                            completion(false, error)
//                                        }
//        })
//    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

// MARK: Table View Data Source

extension MoreChangePatientViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return viewablePatients.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "patientNameTableCell") as! MoreChangePatientNameTableViewCell
        if viewablePatients.count > indexPath.row {
            let name:String = viewablePatients[indexPath.row]
            var enabled:Bool = true
            if (name == AppSessionManager.shared.currentUser.userProfile?.fullName) {
                cell.selectionStyle = .none;
                cell.isUserInteractionEnabled = false
                enabled = false
            }
            cell.prepareView(name, enabled: enabled)
        }
        return cell
    }
}

extension MoreChangePatientViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "identityTableCell") as! MoreChangePatientIdentityTableViewCell
        cell.prepareView((AppSessionManager.shared.currentUser.userProfile?.fullName)!)
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return cellDefaultHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        changeDisplayedPatientData(indexPath)
    }
}
