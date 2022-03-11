//
//  NewApptRequestViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

@available(iOS 13.0, *)
class NewApptRequestViewController: CTCABaseViewController {
    
    @IBOutlet weak var closeBarButton: UIBarButtonItem!
    @IBOutlet weak var stepSliderView: StepIndicator!
    @IBOutlet weak var apptsFormPageViewController: UIView!
    
    private var newApptFormsViewController: NewApptFormsViewController!
    private var lastPageIndex:AppointmentsRequestFormPage = .NONE
    var requestType:ApptRequestType = .new
    
    override func viewDidLoad() {
        super.viewDidLoad()
        stepSliderView.cursor = 0
        self.isModalInPresentation = true
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {

        if let desinationViewController = segue.destination as? NewApptFormsViewController {
            newApptFormsViewController = desinationViewController
            
            switch requestType {
            case .reschedule:
                stepSliderView.stepCount = 4
                newApptFormsViewController.setupForRescheduleAppointment()
            case .cancel:
                stepSliderView.stepCount = 4
                newApptFormsViewController.setupForCancelAppointment()
            case .new:
                stepSliderView.stepCount = 5
                newApptFormsViewController.setupForNewAppointment()
            }
            
            newApptFormsViewController.didChangePage = didChangePage
            newApptFormsViewController.didClose = dismisThis
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        AppDelegate.AppUtility.lockOrientation(.portrait)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        AppDelegate.AppUtility.lockOrientation(.all)
    }
    
    func didChangePage(index:AppointmentsRequestFormPage) {
        let newPageIndex = index.rawValue
        if lastPageIndex.rawValue < newPageIndex {
            self.stepSliderView.moveForward()
        } else if lastPageIndex.rawValue > newPageIndex {
            self.stepSliderView.moveBack()
        }
        self.lastPageIndex = index
    }
    
    @IBAction func closeSubmissionTapped(_ sender: Any) {
        GenericHelper.shared.showFormLeaveAlert(leaveAction: dismisThis)
    }
    
    @objc func dismisThis() {
        self.dismiss(animated: true, completion: nil)
    }
    
}
