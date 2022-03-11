//
//  FormsBaseViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 28/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

protocol FormsBasePageViewProtocol : AnyObject {
    func doMoveNext()
    func doMoveBack()
    func doSubmit()
    func doSave()
}

class FormsBaseViewController: UIViewController, CTCAViewControllerProtocol {
    
    private var backButton:CTCAGrayButton?
    private var nextButton:CTCAGreenButton?
    
    weak var delegate:FormsBasePageViewProtocol?
    var bottomView:UIStackView = UIStackView(frame: .zero)
    
    var pageIndex:AppointmentsRequestFormPage = .NONE
    
    private var closeBarButton:UIBarButtonItem?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        showButtons()
    }
    
    func isValid() -> Bool {
        return false
    }
    
    func showButtons() {
        var both = false
        if pageIndex == .COMMENTS || pageIndex == .CONTACTS || pageIndex == .DATES {
            both = true
        }
        
        nextButton?.removeFromSuperview()
        backButton?.removeFromSuperview()
        
        if let nextButton = nextButton {
            bottomView.removeArrangedSubview(nextButton)
        }
        if let backButton = backButton {
            bottomView.removeArrangedSubview(backButton)
        }
        
        nextButton = nil
        backButton = nil
        
        if both {
            addBackButton()
            addNextButton()
        } else {
            addOneButton()
        }
        
        view.layoutIfNeeded()
    }
    
    func addNextButton() {
        nextButton = CTCAGreenButton(frame: .zero)
        nextButton?.setTitle("Next  >", for: .normal)
        nextButton?.addTarget(self, action: #selector(moveToNext), for: .touchUpInside)
        nextButton?.setDisabled()
        
        bottomView.addArrangedSubview(nextButton!)
    }
    
    func addBackButton() {
        backButton = CTCAGrayButton(frame: .zero)
        backButton?.setTitle("<  Back", for: .normal)
        backButton?.addTarget(self, action: #selector(moveToPrevious), for: .touchUpInside)
        
        bottomView.addArrangedSubview(backButton!)

    }
    
    func addOneButton() {
        nextButton = CTCAGreenButton(frame: .zero)
        if pageIndex == .SUMMARY {
            nextButton?.setTitle("Submit Request", for: .normal)
            nextButton?.addTarget(self, action: #selector(complete), for: .touchUpInside)
        } else if pageIndex == .EDITMODE ||
                    pageIndex == .EDITMODE_REASON ||
                    pageIndex == .EDITMODE_COMMENTS {
            nextButton?.setTitle("Save Changes", for: .normal)
            nextButton?.addTarget(self, action: #selector(save), for: .touchUpInside)
            addCloseButton()
        } else {
            nextButton?.setTitle("Next  >", for: .normal)
            nextButton?.addTarget(self, action: #selector(moveToNext), for: .touchUpInside)
        }
        
        nextButton?.setDisabled()
        bottomView.addArrangedSubview(nextButton!)
    }
    
    @objc func moveToNext() {
        if let del = delegate, isValid() {
            del.doMoveNext()
        }
    }
    
    @objc func moveToPrevious() {
        if let del = delegate {
            del.doMoveBack()
        }
    }
    
    @objc func complete() {
        if let del = delegate {
            del.doSubmit()
        }
    }
    
    @objc func save(){
        saveDataOnEdit()
        if let del = delegate {
            del.doSave()
        }
        dismissThis()
    }
    
    func canMoveNext() {
        nextButton?.setEnabled()
    }
    
    func cannotMoveNext() {
        nextButton?.setDisabled()
    }
    
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard))
        tap.cancelsTouchesInView = false
        self.view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        self.view.endEditing(true)
    }
    
    @objc func dismissThis() {
        dismissKeyboard()
        self.dismiss(animated: true, completion: nil)
    }
    
    func addCloseButton() {
        if closeBarButton == nil {
            let closeButton = UIButton(type: .system)
            closeButton.setImage(UIImage(named: "close"), for: .normal)
            closeButton.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
            closeButton.addTarget(self, action: #selector(dismissThis), for: .touchUpInside)
            closeButton.tintColor = UIColor.ctca_gray130
            closeBarButton = UIBarButtonItem(customView: closeButton)
        }
        
        if let navController = self.navigationController {
            navController.children.first?.navigationItem.rightBarButtonItem = closeBarButton
        }
    }
    
    func saveDataOnEdit() {
        //To be overriden
    }
}

