//
//  NewApptFormContactsViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

@available(iOS 13.0, *)
class NewApptFormContactsViewController: FormsBaseViewController {
    
    @IBOutlet weak var scrollView: UIScrollView!
    var emailMeView: ContactPrefView?
    var callMeView: ContactPrefView?
    @IBOutlet weak var contactsStackView: UIStackView!
    @IBOutlet weak var bottomButtonsView: UIStackView!
    @IBOutlet weak var titleLabel: UILabel!
    
    static func getInstance(index:AppointmentsRequestFormPage) -> NewApptFormContactsViewController {
      let vc = UIStoryboard(name: "Appointments", bundle: nil).instantiateViewController(identifier: "NewApptFormContactsViewController") as! NewApptFormContactsViewController
        vc.pageIndex = index
      return vc
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        GenericHelper.shared.updateTitleWithPrefix(label: titleLabel, title: nil)
        setup()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if AppointmentsManager.shared.requestAppointment.communicationPreference == .EMAIL {
            emailMeView?.setContactSelected()
        } else if AppointmentsManager.shared.requestAppointment.communicationPreference == .CALL {
            callMeView?.setContactSelected()
        } else {
            emailMeView?.setUnSelected()
            callMeView?.setUnSelected()
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        if pageIndex == .EDITMODE {
            return
        }
        
        saveDataOnEdit()
        
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    func setup() {
        bottomView = bottomButtonsView
        
        callMeView = ContactPrefView()
        emailMeView = ContactPrefView()
        
        contactsStackView.addArrangedSubview(callMeView!)
        contactsStackView.addArrangedSubview(emailMeView!)
        
        callMeView?.didStateChange = didStateChange
        emailMeView?.didStateChange = didStateChange
        
        callMeView?.textFieldType = .PHONE
        emailMeView?.textFieldType = .EMAIL
        
        callMeView?.configCell(title: "Call me", subTitle: AppSessionManager.shared.currentUser.getUsersPreferedContactnumber())
        emailMeView?.configCell(title: "Email me", subTitle: AppSessionManager.shared.currentUser.getUsersPreferedEmailId())
        
        callMeView?.subTitleLabel.text = AppointmentsManager.shared.requestAppointment.phoneNumber
        emailMeView?.subTitleLabel.text = AppointmentsManager.shared.requestAppointment.Email
        
        scrollView.alwaysBounceHorizontal = false
        scrollView.isScrollEnabled = false
        
        hideKeyboardWhenTappedAround()
    }
    
    override func isValid() -> Bool {
        return callMeView!.isSelected || emailMeView!.isSelected
    }
    
    func didStateChange(contactView:ContactPrefView) {
        if callMeView!.isSelected || emailMeView!.isSelected {
            canMoveNext()
        } else {
            cannotMoveNext()
        }
        
        //if unselected
        if !contactView.isSelected {
            AppointmentsManager.shared.requestAppointment.communicationPreference = .NONE
        } else {
            //togglemif needed
            if contactView.isSelected && contactView == callMeView
                && emailMeView!.isSelected {
                emailMeView?.setUnSelected()
            } else if contactView.isSelected && contactView == emailMeView
                        && callMeView!.isSelected {
                callMeView?.setUnSelected()
            }
        }
    }
    
    override func saveDataOnEdit() {
        if callMeView!.isSelected {
            AppointmentsManager.shared.requestAppointment.communicationPreference = .CALL
            AppointmentsManager.shared.requestAppointment.phoneNumber = callMeView?.subTitleLabel.text ?? ""
            AppointmentsManager.shared.requestAppointment.Email = emailMeView?.subTitleLabel.text  ?? ""
        } else if emailMeView!.isSelected {
            AppointmentsManager.shared.requestAppointment.communicationPreference = .EMAIL
            AppointmentsManager.shared.requestAppointment.phoneNumber = callMeView?.subTitleLabel.text ?? ""
            AppointmentsManager.shared.requestAppointment.Email = emailMeView?.subTitleLabel.text  ?? ""
        }
    }
    
    
    @objc func keyboardWillShow(notification: NSNotification) {
        let doneBarHeight:CGFloat = 50
        guard let keyboardValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue else { return }

        let keyboardScreenEndFrame = keyboardValue.cgRectValue
        let keyboardViewEndFrame = view.convert(keyboardScreenEndFrame, from: view.window)

        let contentInsets = UIEdgeInsets(top: 0, left: 0, bottom: keyboardViewEndFrame.height+doneBarHeight+20.0, right: 0)
                
        self.scrollView.contentInset = contentInsets
        self.scrollView.scrollIndicatorInsets = contentInsets

        var aRect : CGRect = self.view.frame
        aRect.size.height -= keyboardViewEndFrame.height
        let textViewBottomPoint = CGPoint(x: contactsStackView!.frame.origin.x , y: contactsStackView!.frame.origin.y+contactsStackView.frame.height)
        if (!aRect.contains(textViewBottomPoint))
        {
            let newFrame = contactsStackView!.frame
            self.scrollView.scrollRectToVisible(newFrame, animated: true)
        }
    }

    @objc func keyboardWillHide(notification: NSNotification) {
        self.scrollView.scrollRectToVisible(titleLabel.frame, animated: true)
        self.view.endEditing(true)
        self.scrollView.isScrollEnabled = false
    }
}
