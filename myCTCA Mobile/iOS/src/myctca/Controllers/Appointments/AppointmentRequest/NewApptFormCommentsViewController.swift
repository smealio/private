//
//
//  NewApptFormCommentsViewController_old.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

@available(iOS 13.0, *)
class NewApptFormCommentsViewController: FormsBaseViewController {
    
    @IBOutlet weak var bottomButtonsView: UIStackView!
    @IBOutlet weak var apptDetailsTableView: UITableView!
    @IBOutlet weak var tableViewTopConstraint: NSLayoutConstraint!
    
    var commentText = ""
    weak var commentsCell:NewApptFormCommentTableViewCell?
    
    static func getInstance(index:AppointmentsRequestFormPage) -> NewApptFormCommentsViewController {
        let vc = UIStoryboard(name: "Appointments", bundle: nil).instantiateViewController(identifier: "NewApptFormCommentsViewController") as! NewApptFormCommentsViewController
        vc.pageIndex = index
        return vc
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setup()
        
        let tableViewLoadingCell3Nib = UINib(nibName: "CTCAApptDetailsTableViewCell", bundle: nil)
        self.apptDetailsTableView.register(tableViewLoadingCell3Nib, forCellReuseIdentifier: "CTCAApptDetailsTableViewCell")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
        
        apptDetailsTableView.reloadData()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        var textDisplay = ""
        if pageIndex == .EDITMODE_REASON || pageIndex == .REASON {
            textDisplay = AppointmentsManager.shared.requestAppointment.reason
        } else if pageIndex == .EDITMODE_COMMENTS || pageIndex == .COMMENTS || pageIndex == .CANCEL_COMMENTS {
            textDisplay = AppointmentsManager.shared.requestAppointment.additionalNotes
        }
        
        if textDisplay.isEmpty && (pageIndex == .REASON || pageIndex == .EDITMODE_REASON) {
            cannotMoveNext()
            return
        }
        
        canMoveNext()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        if pageIndex == .REASON {
            AppointmentsManager.shared.requestAppointment.reason = commentText
        } else if pageIndex == .COMMENTS || pageIndex == .CANCEL_COMMENTS {
            AppointmentsManager.shared.requestAppointment.additionalNotes = commentText
        }
        
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    func setup() {
        bottomView = bottomButtonsView
        hideKeyboardWhenTappedAround()
        
        if !(pageIndex == .REASON && AppointmentsManager.shared.requestType == .cancel){
            tableViewTopConstraint.constant = 0.0
        }
    }
    
    override func isValid() -> Bool {
        if let cell = commentsCell {
            return cell.isValidText()
        }
        return false
    }
    
    func dismissKeyBoard() {
        self.view.resignFirstResponder()
    }
    
    override func saveDataOnEdit() {
        if pageIndex == .EDITMODE_REASON {
            AppointmentsManager.shared.requestAppointment.reason = commentText
        } else if pageIndex == .EDITMODE_COMMENTS {
            AppointmentsManager.shared.requestAppointment.additionalNotes = commentText
        }
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {

        guard let keyboardValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue else { return }

        let keyboardScreenEndFrame = keyboardValue.cgRectValue
        let keyboardViewEndFrame = view.convert(keyboardScreenEndFrame, from: view.window)

        if let commentsCell = commentsCell {
            let fromTop = commentsCell.frame.origin.y + commentsCell.frame.height + 20 //should be visible
            
            let doneBarHeight:CGFloat = 50
            let keypadTop = keyboardViewEndFrame.minY - doneBarHeight
            let overlap = fromTop - keypadTop
            
            if overlap > 0 {
                apptDetailsTableView.setContentOffset(CGPoint(x: 0, y: overlap), animated: true)
            }
        }
    }
    
    @objc func keyboardWillHide(notification: NSNotification) {
        apptDetailsTableView.setContentOffset(CGPoint(x: 0, y: 0), animated: true)
        self.view.endEditing(true)
    }
    
    func setComment(text: String) {
        commentText = text.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if commentText.isEmpty && (pageIndex == .REASON || pageIndex == .EDITMODE_REASON) {
            cannotMoveNext()
        } else {
            canMoveNext()
        }
    }
}

@available(iOS 13.0, *)
extension NewApptFormCommentsViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (AppointmentsManager.shared.requestType == .cancel && (pageIndex == .REASON || pageIndex == .EDITMODE_REASON)) {
            return 2
        }
        
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 0 {
            if (AppointmentsManager.shared.requestType == .cancel && (pageIndex == .REASON || pageIndex == .EDITMODE_REASON)) {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "CTCAApptDetailsTableViewCell") as? CTCAApptDetailsTableViewCell {
                    cell.configure()
                    return cell
                }
            } else {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "commentsTableViewCell") as? NewApptFormCommentTableViewCell {
                    cell.configure(index: pageIndex)
                    cell.parent = self
                    cell.setComments = setComment
                    if self.commentsCell == nil {
                        self.commentsCell = cell
                    }
                    return cell
                }
            }
        } else if indexPath.row == 1 {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "commentsTableViewCell") as? NewApptFormCommentTableViewCell {
                cell.configure(index: pageIndex)
                cell.parent = self
                cell.setComments = setComment
                if self.commentsCell == nil {
                    self.commentsCell = cell
                }
                return cell
            }
        }
        return UITableViewCell()
    }
}

