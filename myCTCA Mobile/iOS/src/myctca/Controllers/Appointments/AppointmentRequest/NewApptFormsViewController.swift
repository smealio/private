//
//  NewApptFormsViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

@available(iOS 13.0, *)
class NewApptFormsViewController: UIPageViewController, CTCAViewControllerProtocol {
    
    var apptFormsPageViewControllerList = [UIViewController]()
    var didChangePage:((_ pageIndex:AppointmentsRequestFormPage) -> Void)?
    var didClose:(() -> Void)?
    
    var reasonVC: NewApptFormCommentsViewController?
    var datesVC: NewApptFormDateViewController?
    var contactsVC: NewApptFormContactsViewController?
    var commentsVC: NewApptFormCommentsViewController?
    var summaryVC: NewApptSummaryViewController?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setupForNewAppointment() {
        reasonVC = NewApptFormCommentsViewController.getInstance(index: .REASON)
        reasonVC!.delegate = self
        
        datesVC = NewApptFormDateViewController.getInstance(index: .DATES)
        datesVC!.delegate = self

        contactsVC = NewApptFormContactsViewController.getInstance(index: .CONTACTS)
        contactsVC!.delegate = self

        commentsVC = NewApptFormCommentsViewController.getInstance(index: .COMMENTS)
        commentsVC!.delegate = self

        summaryVC = NewApptSummaryViewController.getInstance(index: .SUMMARY)
        summaryVC!.delegate = self
        summaryVC!.showPageInEditMode = showPageInEditMode
        
        apptFormsPageViewControllerList = [
            reasonVC!,
            datesVC!,
            contactsVC!,
            commentsVC!,
            summaryVC!
        ]
        
        dataSource = self
        
        setViewControllers([apptFormsPageViewControllerList[0]], direction: .forward, animated: true, completion: nil)
        
        self.isPagingEnabled = false
    }
    
    func setupForRescheduleAppointment() {
        datesVC = NewApptFormDateViewController.getInstance(index: .RESCHEDULE_DATES)
        datesVC!.delegate = self

        contactsVC = NewApptFormContactsViewController.getInstance(index: .CONTACTS)
        contactsVC!.delegate = self

        commentsVC = NewApptFormCommentsViewController.getInstance(index: .COMMENTS)
        commentsVC!.delegate = self

        summaryVC = NewApptSummaryViewController.getInstance(index: .SUMMARY)
        summaryVC!.delegate = self
        summaryVC!.showPageInEditMode = showPageInEditMode
        
        apptFormsPageViewControllerList = [
            datesVC!,
            contactsVC!,
            commentsVC!,
            summaryVC!
        ]
        
        dataSource = self
        
        setViewControllers([apptFormsPageViewControllerList[0]], direction: .forward, animated: true, completion: nil)
        
        self.isPagingEnabled = false
    }
    
    func setupForCancelAppointment() {
        reasonVC = NewApptFormCommentsViewController.getInstance(index: .REASON)
        reasonVC!.delegate = self
        
        contactsVC = NewApptFormContactsViewController.getInstance(index: .CONTACTS)
        contactsVC!.delegate = self
        
        commentsVC = NewApptFormCommentsViewController.getInstance(index: .COMMENTS)
        commentsVC!.delegate = self
        
        summaryVC = NewApptSummaryViewController.getInstance(index: .SUMMARY)
        summaryVC!.delegate = self
        summaryVC!.showPageInEditMode = showPageInEditMode
        
        apptFormsPageViewControllerList = [
            reasonVC!,
            contactsVC!,
            commentsVC!,
            summaryVC!
        ]
        
        dataSource = self
        
        setViewControllers([apptFormsPageViewControllerList[0]], direction: .forward, animated: true, completion: nil)
        
        self.isPagingEnabled = false
    }
    
    func moveNext() {
        guard let currentViewController = self.viewControllers?.first as? FormsBaseViewController else { return }
        guard let nextViewController = dataSource?.pageViewController( self, viewControllerAfter: currentViewController ) as? FormsBaseViewController else { return }
        
        //check for validity
        if !currentViewController.isValid() {
            return
        }
        
        if let changeAction = didChangePage {
            changeAction(nextViewController.pageIndex)
        }
        
        setViewControllers([nextViewController], direction: .forward, animated: true, completion: nil)
    }

    func movePrevious() {
       guard let currentViewController = self.viewControllers?.first as? FormsBaseViewController else { return }
        guard let previousViewController = dataSource?.pageViewController( self, viewControllerBefore: currentViewController ) as? FormsBaseViewController else { return }
        
        if let changeAction = didChangePage {
            changeAction(previousViewController.pageIndex)
        }
        
       setViewControllers([previousViewController], direction: .reverse, animated: true, completion: nil)
    }
    
    func showPageInEditMode(pageType:AppointmentsRequestFormPage) {
        guard let topController = GenericHelper.shared.getTopVC() else {
            return
        }
        
        var vc:UIViewController?
        
        switch pageType {
        case .REASON:
            let pageVC = reasonVC
            pageVC?.pageIndex = .EDITMODE_REASON
            vc = pageVC
        case .DATES, .RESCHEDULE_DATES:
            let pageVC = datesVC
            pageVC?.pageIndex = .EDITMODE
            vc = pageVC
        case .CONTACTS:
            let pageVC = contactsVC
            pageVC?.pageIndex = .EDITMODE
            vc = pageVC
        case .COMMENTS, .CANCEL_COMMENTS:
            let pageVC = commentsVC
            pageVC?.pageIndex = .EDITMODE_COMMENTS
            vc = pageVC
        default:
            break
        }
        
        guard let pageVC = vc else {
            return
        }
        
        let navController = UINavigationController(rootViewController: pageVC)
        topController.present(navController, animated: true, completion: nil)
    }
    
    
}

@available(iOS 13.0, *)
extension NewApptFormsViewController: UIPageViewControllerDataSource {
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
        
        let indexOfCurrentPageViewController = apptFormsPageViewControllerList.firstIndex(of: viewController)!
        
        if indexOfCurrentPageViewController == 0 {
            return nil // To show there is no previous page
        } else {
            // Previous UIViewController instance
            return apptFormsPageViewControllerList[indexOfCurrentPageViewController - 1]
        }
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
        
        let indexOfCurrentPageViewController = apptFormsPageViewControllerList.firstIndex(of: viewController)!
        
        if indexOfCurrentPageViewController == apptFormsPageViewControllerList.count - 1 {
            return nil // To show there is no next page
        } else {
            // Next UIViewController instance
            return apptFormsPageViewControllerList[indexOfCurrentPageViewController + 1]
        }
    }
    
    func dismissThis() {
        if let action = didClose {
            action()
        }
    }
    
}

@available(iOS 13.0, *)
extension NewApptFormsViewController: FormsBasePageViewProtocol {
    func doMoveBack() {
        movePrevious()
    }
    
    func doMoveNext() {
        moveNext()
    }
    
    func doSubmit() {
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        self.showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.sendApptRequest)
        
        AppointmentsManager.shared.submitAppointmentRequest() {
            result in
            
            self.fadeOutActivityIndicator(completion: nil)
            switch(result) {
            
            case .FAILED:
                let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: CommonMsgConstants.requestFailedErrorTitle, message: CommonMsgConstants.submitFailMessage, state: false, buttonAction: nil)
                
                GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)

                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_REQUEST_FAIL)
                
            case .SUCCESS:
                var message = ""
                switch AppointmentsManager.shared.requestType {
                case .new:
                    message = AppointmentMsgConstants.successfulRequestResponse
                case .reschedule:
                    message = AppointmentMsgConstants.successfulRescheduleResponse
                case .cancel:
                    message = AppointmentMsgConstants.successfulCancellationResponse
                }
                let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: AppointmentMsgConstants.successfulRequestTitle, message: message, state: true, buttonAction: self.dismissThis)
                
                GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)

                AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_APPOINTMENTS_REQUEST_SUCCESS)
            }
        }
    }
    
    func doSave() {
        if let summaryVC = summaryVC {
            summaryVC.reloadInfo()
        }
    }
}

