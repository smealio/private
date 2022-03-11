//
//  CareGiverHomeFooterView.swift
//  myctca
//
//  Created by Manjunath K on 2/5/21.
//  Copyright © 2021 CTCA. All rights reserved.
//
import UIKit

class CareGiverHomeFooterView: UITableViewHeaderFooterView {
    
    @IBOutlet weak var noPateintsLabel: UILabel!
    @IBOutlet weak var patientNameField: UITextField!
    @IBOutlet weak var showPatientButton: UIButton!
    @IBOutlet weak var signOutButton: UIButton!
    @IBOutlet weak var viewRecordsButton: UIButton!
    
    var patientsAssigned = false
    var parentVC:HomeViewController?
    
    static let reuseIdentifier: String = String(describing: self)

    static var nib: UINib {
        return UINib(nibName: String(describing: self), bundle: nil)
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
    
    @IBAction func signouTappedTapped(_ sender: Any) {
        if let parent = parentVC {
            GenericHelper.shared.doSignOut(onView: parent, sourceView: sender as! UIView)
        } else {
            print("Something went wrong")
        }
    }
    
    func setUp() {
        contentView.backgroundColor = .white
        signOutButton.layer.borderWidth = 0.5
        signOutButton.layer.borderColor = MyCTCAColor.ctcaGreen.color.cgColor
        signOutButton.layer.cornerRadius = 5
        
        viewRecordsButton.layer.cornerRadius = 5
        viewRecordsButton.isHidden = true
        
        if let proxies = AppSessionManager.shared.currentUser.userProfile?.proxies,
           proxies.filter({ $0.relationshipType == "Caregiver"}).count > 0 {
            patientsAssigned = true
        }
        
        if !patientsAssigned {
            noPateintsLabel.text = CareGiverMsgs.noPatientsAssingText
            patientNameField.isHidden = true
            showPatientButton.isHidden = true
        } else if !patientNameField.text!.isEmpty {
            viewRecordsButton.isHidden = false
        }
    }
    
    @IBAction func viewRecordsTapped(_ sender: Any) {
        if let parent = parentVC {
            parent.viewRecords()
        }
    }
}
