//
//  ServiceDateTableViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/29/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

protocol ServiceDateSelectDelegate : AnyObject {
    
    func didSelectServiceDate(value:String, tf: UITextField)
}

class ServiceDateTableViewController: UITableViewController {

    weak var delegate: ServiceDateSelectDelegate?
    var callingTextField: UITextField?
    var firstChoiceText: String?
    
    let secondChoiceText: String = "Specific Date"
    let startDateMessage: String = "Enter a date to specify the release of information beginning from a certain date."
    let endDateMessage: String = "Enter a date to specify the release of information up until a certain date."
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = "Select Date"
        // Listen For DatePicker Notification
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.setDTPODateTime(_:)),
                                               name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION), object: nil)
    }
    
    override func  viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        NotificationCenter.default.removeObserver(self,
                                                  name: NSNotification.Name(rawValue: DatePickerOverlay.SELECTED_DATE_TIME_NOTIFICATION),
                                                  object: nil)
    }
    
    // Call back for DateTimePickerOverlay
    @objc func setDTPODateTime(_ notification: NSNotification) {
        print("setDTPODateTime object: \(String(describing: notification.object))::: userInfo: \(String(describing: notification.userInfo))")
        if let dateTimeStr = notification.object as? String {
            print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = DatePickerOverlay.DATE_FORMAT
            dateFormatter.timeZone = .current// TimeZone(abbreviation: "GMT+0:00") //Current time zone
            let serviceDate = dateFormatter.date(from: dateTimeStr)
            dateFormatter.dateFormat = "MMM d, yyyy"
            let serviceDateStr = dateFormatter.string(from: serviceDate!)
            let cell1 = tableView.cellForRow(at: IndexPath(row: 1, section: 0))!
            cell1.textLabel?.text = serviceDateStr
            cell1.textLabel!.textColor = MyCTCAColor.formContent.color
        }
    }

    @IBAction func doneTapped(_ sender: Any) {
        if(delegate != nil) {
            var returnString: String = ""
            let cell0 = tableView.cellForRow(at: IndexPath(row: 0, section: 0))!
            if (cell0.isSelected == true) {
                returnString = (cell0.textLabel?.text)!
            } else {
                let cell1 = tableView.cellForRow(at: IndexPath(row: 1, section: 0))!
                returnString = (cell1.textLabel?.text)!
            }
            self.delegate!.didSelectServiceDate(value: returnString, tf: self.callingTextField!)
        }
        _ = navigationController?.popViewController(animated: true)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.row == 0) {
            let cell0 = tableView.dequeueReusableCell(withIdentifier: "selectDateCell", for: indexPath)
            cell0.accessoryType = .none
            if (firstChoiceText != nil) {
                cell0.textLabel!.text = firstChoiceText
                cell0.textLabel!.textColor = MyCTCAColor.formContent.color
            }
            return cell0
        }
        let cell1 = tableView.dequeueReusableCell(withIdentifier: "dateSelectCell", for: indexPath)
        
        cell1.textLabel!.text = secondChoiceText
        cell1.textLabel!.textColor = MyCTCAColor.formContent.color
        return cell1
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        let cell0 = tableView.cellForRow(at: IndexPath(row: 0, section: 0))!
        let cell1 = tableView.cellForRow(at: IndexPath(row: 1, section: 0))!
        if (indexPath.row == 0) {
            print("cell0.isSelected: \(cell0.isSelected)")
            cell0.accessoryType = .checkmark
            cell0.isSelected = true
            cell0.textLabel!.textColor = MyCTCAColor.formContent.color
            cell1.textLabel!.text = secondChoiceText
            cell1.textLabel!.textColor = MyCTCAColor.formContent.color
        } else {
            cell0.accessoryType = .none
            cell0.isSelected = false
            cell0.textLabel!.textColor = MyCTCAColor.formContent.color
            print("got to show date selector")
            self.view.endEditing(true)
            var specificDateMsg = ""
            if (firstChoiceText == MoreROITableViewController.BEGINNING_OF_TREATMENT) {
                specificDateMsg = startDateMessage
            } else {
                specificDateMsg = endDateMessage
            }
            let bgImage:UIImage = UIImage(named: "check-square-o_white.png")!
            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: specificDateMsg,
                                                 bgImage: bgImage,
                                                 mode: .date)
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
