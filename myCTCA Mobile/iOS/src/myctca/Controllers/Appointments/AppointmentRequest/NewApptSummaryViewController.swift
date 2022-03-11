//
//  NewApptSummaryViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 27/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import JTAppleCalendar

@available(iOS 13.0, *)
class NewApptSummaryViewController: FormsBaseViewController {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var bottomButtonsView: UIStackView!
    
    private var rowCount = 4
    var showPageInEditMode:((AppointmentsRequestFormPage) -> Void)?
    
    static func getInstance(index:AppointmentsRequestFormPage) -> NewApptSummaryViewController {
        let vc = UIStoryboard(name: "Appointments", bundle: nil).instantiateViewController(identifier: "NewApptSummaryViewController") as! NewApptSummaryViewController
        vc.pageIndex = index
        return vc
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Register subtitle Cell
        let tableViewLoadingCellNib = UINib(nibName: "CTCACommentsTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCellNib, forCellReuseIdentifier: "CTCACommentsTableViewCell")
        
        let tableViewLoadingCell2Nib = UINib(nibName: "CTCAContactsTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCell2Nib, forCellReuseIdentifier: "CTCAContactsTableViewCell")
        
        let tableViewLoadingCell3Nib = UINib(nibName: "CTCAApptDetailsTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCell3Nib, forCellReuseIdentifier: "CTCAApptDetailsTableViewCell")
        
        setup()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        reloadInfo()
        canMoveNext()
    }
    
    func setup() {
        bottomView = bottomButtonsView
    }
    
    override func isValid() -> Bool {
        return true
    }
    
    func reloadInfo() {
        DispatchQueue.main.async {
            self.tableView.beginUpdates()
            self.tableView.reloadData()
            self.tableView.endUpdates()
        }
    }
}

@available(iOS 13.0, *)
extension NewApptSummaryViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return rowCount
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 0 {
            if AppointmentsManager.shared.requestType == .new {
                let cell = tableView.dequeueReusableCell(withIdentifier: "CTCACommentsTableViewCell") as! CTCACommentsTableViewCell
                cell.configureCell(title: "REASON FOR APPOINTMENT", subTitle: AppointmentsManager.shared.requestAppointment.reason)
                cell.showEditPage = showPageInEditMode
                cell.index = .REASON
                return cell
            } else {
                let cell = tableView.dequeueReusableCell(withIdentifier: "CTCAApptDetailsTableViewCell") as! CTCAApptDetailsTableViewCell
                cell.configure()
                return cell
            }
        } else if indexPath.row == 1 {
            if AppointmentsManager.shared.requestType == .cancel {
                let cell = tableView.dequeueReusableCell(withIdentifier: "CTCACommentsTableViewCell", for: indexPath) as! CTCACommentsTableViewCell
                //let cell = tableView.dequeueReusableCell(withIdentifier: "CTCACommentsTableViewCell")
                cell.configureCell(title: "REASON FOR CANCELLATION", subTitle: AppointmentsManager.shared.requestAppointment.reason)
                cell.showEditPage = showPageInEditMode
                if AppointmentsManager.shared.requestType == .cancel {
                    cell.index = .REASON
                } else {
                    cell.index = .COMMENTS
                }
                return cell

            } else {
                if let  cell = tableView.dequeueReusableCell(withIdentifier: "CTCAContactsTableViewCell", for: indexPath) as? CTCAContactsTableViewCell {
                    cell.configureDateCell()
                    cell.showEditPage = showPageInEditMode
                    if AppointmentsManager.shared.requestType == .reschedule  {
                        cell.index = .RESCHEDULE_DATES
                    } else {
                        cell.index = .DATES
                    }
                    cell.layoutIfNeeded()
                    return cell
                } else {
                    print("Empty cell")
                    return UITableViewCell()
                }
            }
        } else if indexPath.row == 2 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CTCACommentsTableViewCell") as! CTCACommentsTableViewCell
            cell.configContactCell()
            cell.showEditPage = showPageInEditMode
            cell.index = .CONTACTS
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CTCACommentsTableViewCell") as! CTCACommentsTableViewCell
            let text = AppointmentsManager.shared.requestAppointment.additionalNotes.isEmpty ? "No Comments" : AppointmentsManager.shared.requestAppointment.additionalNotes
            cell.configureCell(title: "ADDITIONAL COMMENTS", subTitle: text)
            cell.showEditPage = showPageInEditMode
            if AppointmentsManager.shared.requestType == .cancel {
                cell.index = .CANCEL_COMMENTS
            } else {
                cell.index = .COMMENTS
            }
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
//        if indexPath.row == 1 {
//            if AppointmentsManager.shared.requestType != .cancel {
//                let datesList = AppointmentsManager.shared.requestAppointment.appointmentDateTimes
//                
//                switch datesList.count {
//                case 1:
//                    return 122.0
//                case 2:
//                    return 195.0
//                case 3:
//                    return 270.0
//                default:
//                    return UITableView.automaticDimension
//                }
//            }
//        }
        return UITableView.automaticDimension
    }
}
