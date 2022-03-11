//
//  PatientHomeFooterView.swift
//  myctca
//
//  Created by Manjunath K on 1/27/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class PatientHomeFooterView: UITableViewHeaderFooterView {
    @IBOutlet weak var shareBtn: UIButton!
    @IBOutlet weak var messageBtn: UIButton!
    @IBOutlet weak var appointmentsBtn: UIButton!
    @IBOutlet weak var callBtn: UIButton!
    @IBOutlet weak var signOutButton: UIButton!
    @IBOutlet weak var callLabel: UILabel!
    
    @IBOutlet weak var quickLinkLabel: UILabel!
    @IBOutlet weak var requestApptView: UIView!
    @IBOutlet weak var shareRecordsView: UIView!
    @IBOutlet weak var messageCareteamView: UIView!
    @IBOutlet weak var callSupportView: UIView!
    @IBOutlet weak var patientSelectView: UIView!
    
    @IBOutlet weak var patientNameField: UITextField!
    @IBOutlet weak var showPatientButton: UIButton!
    @IBOutlet weak var viewRecordsButton: UIButton!
    @IBOutlet weak var noPateintsLabel: UILabel!

    weak var parentVC:HomeViewController?
    var patientsAssigned = false
    
    static let reuseIdentifier: String = String(describing: self)

    @IBOutlet weak var patientSelectHtConst: NSLayoutConstraint!
    
    static var nib: UINib {
        return UINib(nibName: String(describing: self), bundle: nil)
    }
    
    func setUp(_ quickLinkViewHt:CGFloat) {
        contentView.backgroundColor = .white
        
        if !GenericHelper.shared.hasPermissionTo(feature: .viewQuickLinks) {
            requestApptView.isHidden = true
            callSupportView.isHidden = true
            messageCareteamView.isHidden = true
            shareRecordsView.isHidden = true
            quickLinkLabel.isHidden = true

            patientSelectHtConst.constant = 0.0
        } else {
            patientSelectHtConst.constant = quickLinkViewHt
            
            if AppSessionManager.shared.getUserType() != .PATIENT {
                callLabel.text = "CALL TECHNICAL SUPPORT"
            } else {
                callLabel.text = "IMPORTANT PHONE NUMBERS"
            }
        }
        
        contentView.backgroundColor = .white
        viewRecordsButton.layer.cornerRadius = 5
        
        if let proxies = AppSessionManager.shared.currentUser.userProfile?.proxies,
           proxies.filter({ $0.relationshipType == "Caregiver"}).count > 0 {
            patientsAssigned = true
        }
        
        viewRecordsButton.isHidden = true
        noPateintsLabel.isHidden = true
        signOutButton.isHidden = true
        
        switch (AppSessionManager.shared.getUserType()) {
        case .CARE_GIVER:
            signOutButton.isHidden = false
            signOutButton.layer.borderWidth = 0.5
            signOutButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
            signOutButton.layer.cornerRadius = 5
            
            if !patientsAssigned {
                noPateintsLabel.isHidden = false
                noPateintsLabel.text = CareGiverMsgs.noPatientsAssingText
                patientNameField.isHidden = true
                showPatientButton.isHidden = true
            } else if !patientNameField.text!.isEmpty {
                viewRecordsButton.isHidden = false
            }
        case .PATIENT:
            if !patientsAssigned {
                patientSelectView.isHidden = true
            } else {
                patientSelectView.isHidden = false
                if !patientNameField.text!.isEmpty {
                    viewRecordsButton.isHidden = false
                }
            }
        case .PROXY:
            patientSelectView.isHidden = true
        default:
            return
        }
    }

    @IBAction func callBtnTapped(_ sender: Any) {
        if AppSessionManager.shared.getUserType() == .PATIENT {
            if let parent = parentVC {
                let listViewController = UIStoryboard(name: "Secondary", bundle: nil).instantiateViewController(withIdentifier: "ImportantNumbersTableViewController") as! ImportantNumbersTableViewController
                listViewController.title = "Important Numbers"
                parent.show(listViewController, sender: nil)
            }
        } else {
            let telNo = AppInfoManager.shared.appInfo?.techSupportNumber ?? ""
            
            if let parent = parentVC {
                GenericHelper.shared.tryToCall(telNo: telNo, parentVC: parent)
            }
        }
    }
    
    @IBAction func appointmentsBtnTapped(_ sender: Any) {
        if !GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.requestAppointments) {
            GenericHelper.shared.showNoAccessMessage(view: nil)
            return
        }
        
        if let parent = parentVC {
            DispatchQueue.main.async {
                AppointmentsManager.shared.prepareAppointmentRequestFor(requestType: .new, appointment: nil)
                
                let storyboard = UIStoryboard(name: "Appointments", bundle: nil)
                if #available(iOS 13.0, *) {
                    let appointmentReqVC = storyboard.instantiateViewController(withIdentifier: "NewApptRequestViewController") as! NewApptRequestViewController
                    
                    appointmentReqVC.title = "Request Appointment"
                    let navController = UINavigationController(rootViewController: appointmentReqVC)
                    parent.present(navController, animated: true, completion: nil)
                }
            }
        }
    }
    
    @IBAction func messageBtnTapped(_ sender: Any) {
        if let parent = parentVC {
            
            if !GenericHelper.shared.hasPermissionTo(feature: .sendSecureMessages){
                if AppSessionManager.shared.getUserType() == .PATIENT {
                    GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.noAccessTitle, andMessage: CommonMsgConstants.noMsgAccessMessage, onView: parent)
                } else {
                    GenericHelper.shared.showNoAccessMessage(view: parent)
                }
                return
            }

            DispatchQueue.main.async {
                let messageVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MailSendNewViewController") as! MailSendNewViewController
                messageVC.respondingMail = nil
                messageVC.mailBoxDelegate = nil
                let navController = UINavigationController(rootViewController: messageVC)
                navController.navigationBar.tintColor = MyCTCAColor.ctcaGreen.color
                parent.present(navController, animated: true, completion: nil)
            }
        }
    }
    
    @IBAction func shareBtnTapped(_ sender: Any) {
        if let parent = parentVC, let url  = AuthenticationAPIRouter.openShareRecords.asUrl() {
            GenericHelper.shared.launchSafariViewController(withUrl: url, forView: parent)
        }
    }
    
    
    @IBAction func showPatientsTapped(_ sender: Any) {
        guard let parent = parentVC else {
            return
        }
        
        let viewablePatientsViewController: RecordSelectionViewController = parent.storyboard?.instantiateViewController(withIdentifier: "RecordSelectionViewController") as! RecordSelectionViewController
        viewablePatientsViewController.delegate = parent
        viewablePatientsViewController.selectionType = .patients
        
        //Get patient list
        if let viewablePatients = AppSessionManager.shared.currentUser.userProfile?.viewablePatients() {
            viewablePatientsViewController.patientsList = viewablePatients
        }

        if let text = patientNameField.text, text != "" {
            viewablePatientsViewController.selectedOption = text
        }
        
        parent.navigationController?.pushViewController(viewablePatientsViewController, animated: true)
    }
    
    @IBAction func viewRecordsTapped(_ sender: Any) {
        if let parent = parentVC {
            parent.viewRecords()
        }
    }
    
    @IBAction func signouTappedTapped(_ sender: Any) {
        if let parent = parentVC {
            GenericHelper.shared.doSignOut(onView: parent, sourceView: sender as! UIView)
        } else {
            print("Something went wrong")
        }
    }
}
