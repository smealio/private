//
//
//  NewApptFormDateViewController
//
//  Created by Manjunath Kambalekar on 24/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

@available(iOS 13.0, *)
class NewApptFormDateViewController: FormsBaseViewController {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var bottomButtonsView: UIStackView!
    @IBOutlet weak var tableViewTopDistConst: NSLayoutConstraint!
    
    private var footerView:UIView?
    private var selectedDates = [NewApptCalenderSelection]()
    private let maxDatesCount = 3
    private let dateCellHeight = 98.0
    private var viewWidth = 0.0
    
    static func getInstance(index:AppointmentsRequestFormPage) -> NewApptFormDateViewController {
        let vc = UIStoryboard(name: "Appointments", bundle: nil).instantiateViewController(identifier: "NewApptFormDateViewController") as! NewApptFormDateViewController
        vc.pageIndex = index
        return vc
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setup()
        let tableViewLoadingCell3Nib = UINib(nibName: "CTCAApptDetailsTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCell3Nib, forCellReuseIdentifier: "CTCAApptDetailsTableViewCell")
        
        viewWidth = view.bounds.width
        
        if AppointmentsManager.shared.requestType == .new {
            GenericHelper.shared.updateTitleWithPrefix(label: titleLabel, title: nil)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        selectedDates.removeAll()
        
        for item in AppointmentsManager.shared.requestAppointment.appointmentDateTimes {
            var dateSel = NewApptCalenderSelection()
            switch item.timePreference {
            case .ALL_DAY:
                dateSel.session1 = true
                dateSel.session2 = true
            case .MORNING:
                dateSel.session1 = true
            case .AFTERNOON:
                dateSel.session2 = true
            default:
                break
            }
            dateSel.date = item.date
            selectedDates.append(dateSel)
        }
        
        if selectedDates.count > 0 {
            tableView.reloadData()
        }
        
        if AppointmentsManager.shared.requestType == .new {
            titleLabel.text = ""
            tableViewTopDistConst.constant = 0.0
        }
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if selectedDates.count > 0 {
            canMoveNext()
        }
    }
    
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        if UIDevice.current.userInterfaceIdiom == .pad {
            if viewWidth != view.bounds.width {
                viewWidth = view.bounds.width
                tableView.reloadData()
            }
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        if pageIndex != .EDITMODE {
            saveDataOnEdit()
        }
    }
    
    func setup() {
        bottomView = bottomButtonsView
    }
    
    func getFooter() -> UIView {
        if footerView == nil {
            footerView = UIView(frame: CGRect(x: 0, y: 0, width: viewWidth, height: 50))
            let addButton = UIButton(frame: CGRect(x: 16, y: 0, width: viewWidth-32, height: 48))

            let attrs = [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue", size: 16.0)!,
                         NSAttributedString.Key.foregroundColor: UIColor.ctca_selection_purple]
            let attrtTitle = NSAttributedString(string: "+  Add Dates/Times", attributes: attrs)
            addButton.setAttributedTitle(attrtTitle, for: .normal)
            addButton.addTarget(self, action: #selector(addDates), for: .touchUpInside)
            addButton.addDashedBorder(color: UIColor.ctca_selection_purple, width: 1)
            footerView?.addSubview(addButton)

            addButton.leadingAnchor.constraint(equalTo: footerView!.leadingAnchor, constant: 16.0).isActive = true
            addButton.trailingAnchor.constraint(equalTo: footerView!.trailingAnchor, constant: 16.0).isActive = true
        }
        return footerView!
    }
    
    @objc func addDates() {
        if let topController = GenericHelper.shared.getTopVC() {
            DispatchQueue.main.async {
                let calenderVC = UIStoryboard(name: "Secondary", bundle: nil).instantiateViewController(withIdentifier: "CalenderViewController") as! CalenderViewController
                calenderVC.delegate = self
                calenderVC.preSelectedDates = self.selectedDates
                topController.present(calenderVC, animated: true, completion: nil)
            }
        }
    }
    
    override func isValid() -> Bool {
        return selectedDates.count > 0
    }
    
    override func saveDataOnEdit() {
        
        AppointmentsManager.shared.requestAppointment.appointmentDateTimes.removeAll()
        
        for item in selectedDates {
            var timePreference = ApptTimePref.NONE
            if item.session2 && item.session1 {
                timePreference = ApptTimePref.ALL_DAY
            } else if item.session1 {
                timePreference = ApptTimePref.MORNING
            } else {
                timePreference = ApptTimePref.AFTERNOON
            }
            let date = AppointmentDateTimes(date: item.date!, timePreference: timePreference)
            AppointmentsManager.shared.requestAppointment.appointmentDateTimes.append(date)
        }
    }
}

@available(iOS 13.0, *)
extension NewApptFormDateViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if AppointmentsManager.shared.requestType != .new {
            return selectedDates.count + 2
        }
        
        return selectedDates.count + 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 0 {
            if AppointmentsManager.shared.requestType != .new {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "CTCAApptDetailsTableViewCell") as? CTCAApptDetailsTableViewCell {
                    cell.configure()
                    return cell
                }
            } else {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "NewApptFormTitlesTableViewCell") as? NewApptFormTitlesTableViewCell {
                    cell.config()
                    return cell
                }
            }
        } else if indexPath.row == 1 && AppointmentsManager.shared.requestType != .new {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "NewApptFormTitlesTableViewCell") as? NewApptFormTitlesTableViewCell {
                cell.config()
                return cell
            }
        } else {
            let dateIndex = (AppointmentsManager.shared.requestType != .new) ? indexPath.row - 2 : indexPath.row - 1
            if let cell = tableView.dequeueReusableCell(withIdentifier: "NewApptFormDateTableViewCell") as? NewApptFormDateTableViewCell, dateIndex < selectedDates.count {
                var data = selectedDates[dateIndex]
                data.index = dateIndex
                cell.prepareCell(data: data)
                cell.editAction = editThisRow
                cell.deleteAction = deleteThisRow
                return cell
            }
        }
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        if AppointmentsManager.shared.requestType == .new && tableView.numberOfRows(inSection: 0) < 4 {
            if UIDevice.current.userInterfaceIdiom == .pad {
                footerView = nil
                return getFooter()
            } else {
                return getFooter()
            }
        } else if AppointmentsManager.shared.requestType != .new && tableView.numberOfRows(inSection: 0) < 5 {
            if UIDevice.current.userInterfaceIdiom == .pad {
                footerView = nil
                return getFooter()
            } else {
                return getFooter()
            }
        }
        return UIView()
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if AppointmentsManager.shared.requestType == .new {
            if indexPath.row > 0 {
                return dateCellHeight
            }
        } else {
            if indexPath.row > 1 {
                return dateCellHeight
            }
        }
        return UITableView.automaticDimension
    }
    
    func deleteThisRow(date:NewApptCalenderSelection) {

        let deleteAction = UIAlertAction(title: "Delete", style: .destructive, handler: { [weak self] action in
            
            guard let self = self else {return}
            
            DispatchQueue.main.async {
                if self.tableView.cellForRow(at: IndexPath(row: date.index, section: 0)) != nil {
                    self.selectedDates.remove(at: date.index)
                    
                    self.tableView.deleteRows(at: [IndexPath(row: date.index, section: 0)], with: .fade)
                    
                    self.tableView.reloadData()
                    
                    if self.selectedDates.count == 0 {
                        self.cannotMoveNext()
                    }
                }
            }
        })
        
        GenericHelper.shared.showDeleteAction(withtitle: "", andMessage: "Do you want to delete this option?",
                                              onView: self, deleteAction: deleteAction, otheraction: nil)
    }
    
    func editThisRow(date:NewApptCalenderSelection) {
        if let topController = GenericHelper.shared.getTopVC() {
            DispatchQueue.main.async {
                let calenderVC = UIStoryboard(name: "Secondary", bundle: nil).instantiateViewController(withIdentifier: "CalenderViewController") as! CalenderViewController
                calenderVC.delegate = self
                calenderVC.dateSlot = date
                calenderVC.preSelectedDates.removeAll()
                    for item in self.selectedDates {
                        if !Calendar.current.isDate(date.date!, inSameDayAs: item.date!) {
                            calenderVC.preSelectedDates.append(item)
                        }
                    }
                topController.present(calenderVC, animated: true, completion: nil)
            }
        }
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 50.0
    }
}

@available(iOS 13.0, *)
extension NewApptFormDateViewController: CalenderViewDelegates {
    func didSelectDate(dateSlot: NewApptCalenderSelection) {
        if dateSlot.index == -1 { //new
            selectedDates.append(dateSlot)
            canMoveNext()
        } else {
            selectedDates.remove(at: dateSlot.index)
            selectedDates.insert(dateSlot, at: dateSlot.index)
        }
        tableView.reloadData()
    }
    
    func didCancel() {
        //cancel
    }
}
