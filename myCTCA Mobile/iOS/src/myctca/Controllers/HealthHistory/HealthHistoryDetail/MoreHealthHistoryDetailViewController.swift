//
//  MoreHealthHistoryDetailViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 2/27/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MoreHealthHistoryDetailViewController: UIViewController {

    @IBOutlet weak var shouldRefillLabel: UILabel!
    
    @IBOutlet weak var scrollView: UIScrollView!
    
    @IBOutlet weak var contentView: UIView!
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var statusLabel: UILabel!
    @IBOutlet weak var startDateLabel: UILabel!
    @IBOutlet weak var endDateLabel: UILabel!
    @IBOutlet weak var instructionLabel: UILabel!
    
    @IBOutlet weak var commentsFieldLabel: UILabel!
    @IBOutlet weak var commentsDataLabel: UILabel!
    
    var refillRequest:UIButton = UIButton(type: .custom)
    
    var prescription: Prescription?
    
    var shouldRefill: Bool = false;
    var showRefillBarButton: Bool = false
    
    let INCLUDE_IN_REFILL_REQUEST: String = "Include in renewal request"
    let PRESCRIPTION_RENEWAL_SEGUE = "IndividualPrescriptionRenewalSegue"

    override func viewDidLoad() {
        super.viewDidLoad()

        prepareView()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func prepareView() {
        
        if (self.prescription != nil) {
            //prescription detail view
            AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_PRESCRIPTION_DETAILS_VIEW)
            nameLabel.text = prescription!.drugName
            statusLabel.text = prescription!.subText
            
            if (prescription!.startDate == "") {
                startDateLabel.text = prescription!.NO_START_DATE
            } else {
                startDateLabel.text = prescription!.startDate
            }
            
            if (prescription!.expireDate == "") {
                endDateLabel.text = prescription!.NO_EXPIRED_DATE
            } else {
                endDateLabel.text = prescription!.expireDate
            }
            
            if (prescription!.instructions == "") {
                instructionLabel.text = prescription!.NO_INSTRUCTIONS
            } else {
                instructionLabel.text = prescription!.instructions
            }
            
            if (prescription!.comments == "") {
                commentsFieldLabel.isHidden = true
                commentsDataLabel.isHidden = true
            } else {
                commentsFieldLabel.isHidden = false
                commentsDataLabel.isHidden = false
                commentsDataLabel.text = prescription!.comments
            }
            self.self.shouldRefillLabel.text = ""

            if (showRefillBarButton == true) {
                addPrescriptionBarButton()
            }
        } else {
            nameLabel.text = ""
            statusLabel.text = ""
            startDateLabel.text = ""
            endDateLabel.text = ""
            instructionLabel.text = ""
            commentsFieldLabel.isHidden = true
            commentsDataLabel.isHidden = true
            
            self.navigationItem.setRightBarButtonItems([], animated: true)
            self.shouldRefillLabel.text = ""
        }
    }
    
    func addPrescriptionBarButton() {
        refillRequest.setImage(UIImage(named: "prescription_renew"), for: .normal)
        refillRequest.tintColor = MyCTCAColor.ctcaGreen.color
        refillRequest.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
        refillRequest.addTarget(self, action: #selector(toggleShouldRefill), for: .touchUpInside)
        let refillRequestItem = UIBarButtonItem(customView: refillRequest)
        self.navigationItem.setRightBarButtonItems([refillRequestItem], animated: true)
    }
    
    @objc func toggleShouldRefill() {
        if !GenericHelper.shared.hasPermissionTo(feature: .requestPrescritionRefill){
            if AppSessionManager.shared.getUserType() == .PATIENT {
                GenericHelper.shared.showAlert(withtitle: CommonMsgConstants.noAccessTitle, andMessage: CommonMsgConstants.noPrescriptionRenewalAccessMessage, onView: self)
            } else {
                GenericHelper.shared.showNoAccessMessage(view: self)
            }
        } else {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_PRESCRIPTION_RENEWAL_GOTO_TAP)
            if let renewable = prescription?.allowRenewal, renewable == true {
                performSegue(withIdentifier: PRESCRIPTION_RENEWAL_SEGUE, sender: self)
            } else {
                GenericHelper.shared.showAlert(withtitle: PrescriptionsMsgConstants.prescriptionMsgTitle, andMessage: PrescriptionsMsgConstants.discontinuedPrescriptionMessage, onView: self)
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let destinationNavigationController = segue.destination as! UINavigationController
        if let targetController = destinationNavigationController.topViewController as? PrescriptionRefillViewController {
            print("\(targetController)")
             if let currentPrescription = prescription {
                var prescriptions = [Prescription]()
                prescriptions.append(currentPrescription)
                
                targetController.prescriptionRefills = prescriptions
            }
        }
    }
    
    func setShouldRefillUIObjects() {
        print("toggleShouldRefill: \(self.shouldRefill)")
        if (self.shouldRefill == true) {
            refillRequest.tintColor = MyCTCAColor.ctcaGreen.color
            shouldRefillLabel.text = INCLUDE_IN_REFILL_REQUEST
        } else {
            refillRequest.tintColor = MyCTCAColor.ctcaGrey50.color
            shouldRefillLabel.text = ""
        }
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
