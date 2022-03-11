//
//  ANNCFormTableViewController.swift
//  myctca
//
//  Created by Manjunath K on 9/3/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class ANNCFormTableViewController: UITableViewController, UITextFieldDelegate {
    
    let headerHeight: CGFloat = 44.0
    let cellDefaultHeight: CGFloat = 60.0
    let doneToolbarHeight: CGFloat = 50.0
    
    //section 1
    @IBOutlet weak var treatmentFacLbl: UILabel!
    @IBOutlet weak var treatmentFacTF: UITextField!
    @IBOutlet weak var insuranceLbl: UILabel!
    @IBOutlet weak var insuranceTF: UITextField!
    @IBOutlet weak var patientFNameLbl: UILabel!
    @IBOutlet weak var patientFNameTF: UITextField!
    @IBOutlet weak var patientLNameLbl: UILabel!
    @IBOutlet weak var patientLNameTF: UITextField!
    @IBOutlet weak var mrnLbl: UILabel!
    @IBOutlet weak var MRNTF: UITextField!
    @IBOutlet weak var dofLbl: UILabel!
    @IBOutlet weak var dofTF: UITextField!
    
    //Section 2
    @IBOutlet weak var ANNCText1: UILabel!
    @IBOutlet weak var ANNCText2: UILabel!
    
    @IBOutlet weak var costTableMainView: UIView!
    
    @IBOutlet weak var preocedureTypeLbl: InsetLabel!
    @IBOutlet weak var reasonTypeLbl: InsetLabel!
    @IBOutlet weak var cost1Lbl: InsetLabel!
    
    @IBOutlet weak var needToKnwTitle: UILabel!
    @IBOutlet weak var needToKnw1: UILabel!
    @IBOutlet weak var needToKnw2: UILabel!
    @IBOutlet weak var needToKnow3: UILabel!
    @IBOutlet weak var needToKnowNoteLbl: UILabel!
    
    //section 3
    @IBOutlet weak var optionTextLbl: UILabel!
    @IBOutlet weak var option1SelectionButton: UIButton!
    @IBOutlet weak var option1TextLbl: UILabel!
    @IBOutlet weak var optionAdInfoTextLbl: UILabel!
    @IBOutlet weak var option2TextLbl: UILabel!
    @IBOutlet weak var option2SelectionButton: UIButton!
    
    //Section4
    @IBOutlet weak var disclaimerTextLbl: UILabel!
    @IBOutlet weak var responsiblePartyTextLbl: UILabel!
    @IBOutlet weak var responsiblePartyTF: UITextField!
    @IBOutlet weak var relationTextLbl: UILabel!
    @IBOutlet weak var relationTF: UITextField!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var dateTF: UITextField!
    
    var anncFormInfo = ANNCSubmissionInfo()
    var mrnValue = ""
    let option1 = "option1"
    let option2 = "option2"
    
    // Array of Text fields that can be looped through to do stuff like add a Done button to the keyboard
    var textFieldArray:[UITextField] = [UITextField]()
    var textLabelArray:[UILabel] = [UILabel]()
    
    var isFormDataLoaded = false
    let formsManager = FormsManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.backgroundColor = UIColor.white
        
        self.tableView.estimatedRowHeight = cellDefaultHeight
        self.tableView.rowHeight = UITableView.automaticDimension
        
        self.clearsSelectionOnViewWillAppear = false
        
        textFieldArray = [treatmentFacTF, insuranceTF, patientFNameTF, patientLNameTF, MRNTF, dofTF, responsiblePartyTF, relationTF, dateTF]
        textLabelArray = [treatmentFacLbl, insuranceLbl, patientFNameLbl, patientLNameLbl, mrnLbl, dofLbl, responsiblePartyTextLbl, relationTextLbl, dateLbl]
        
        // Add Toolbar to keyboard for text fields
        self.addDoneButtonOnKeyboard()
        
        prepareDisplayView()
        
        loadViewData()
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_ANNC_FORM_VIEW)
    }
    
    func prepareDisplayView() {
        //seciotn 1
        treatmentFacTF.text = AppSessionManager.shared.currentUser.primaryFacility?.displayName
        anncFormInfo.facilityName = AppSessionManager.shared.currentUser.primaryFacility?.facilityCode
        
        patientFNameTF.text = AppSessionManager.shared.currentUser.userProfile?.firstName
        patientLNameTF.text = AppSessionManager.shared.currentUser.userProfile?.lastName
        anncFormInfo.patientName = patientFNameTF.text! + " " + patientLNameTF.text!
        
        //Section 2
        costTableMainView.layer.borderWidth = 1
        costTableMainView.layer.borderColor = UIColor.black.cgColor
        
        ANNCText1.text = ANNCUIConstants.anncStm1
        
        let anncStm2AttrText = NSMutableAttributedString()
            .normal(ANNCUIConstants.anncStm2P1)
            .bold(ANNCUIConstants.teleHealthText)
            .normal(ANNCUIConstants.anncStm2P2)
        ANNCText2.attributedText = anncStm2AttrText
        
        preocedureTypeLbl.text = ANNCUIConstants.procedureType
        reasonTypeLbl.text = ANNCUIConstants.reasonType
        cost1Lbl.text = ANNCUIConstants.costType + "\n" + ANNCUIConstants.cost1 + "\n" + ANNCUIConstants.cost2
        
        needToKnwTitle.text = ANNCUIConstants.needToKnowText
        needToKnw1.text = ANNCUIConstants.needToKnowPt1Text
        needToKnw2.text = ANNCUIConstants.needToKnowPt2Text
        
        let needToKnowAttrText = NSMutableAttributedString()
            .normal(ANNCUIConstants.needToKnowPt3P1Text)
            .bold(ANNCUIConstants.teleHealthText)
            .normal(ANNCUIConstants.needToKnowPt3P2Text)
        needToKnow3.attributedText = needToKnowAttrText
        
        let needToKnowNoteAttrText = NSMutableAttributedString()
            .bold("Note: ")
            .normal(ANNCUIConstants.needToKnowNoteText)
        needToKnowNoteLbl.attributedText = needToKnowNoteAttrText
        
        //section 2
        optionTextLbl.text = ANNCUIConstants.optionsText
        
        let opt1AttrText = NSMutableAttributedString()
            .bold(ANNCUIConstants.option1Part1Text)
            .bold("want")
            .normal(ANNCUIConstants.option1Part2Text)
            .bold("I can appeal")
            .normal(ANNCUIConstants.option1Part3Text)
        
        option1TextLbl.attributedText = opt1AttrText
        
        let opt2AttrText = NSMutableAttributedString()
            .bold(ANNCUIConstants.option2Part1Text)
            .bold("don't want")
            .normal(ANNCUIConstants.option2Part2Text)
            .bold("not")
            .normal(ANNCUIConstants.option2Part3Text)
            .bold(ANNCUIConstants.option2Part4Text)
        
        option2TextLbl.attributedText = opt2AttrText
        
        let addtionalInfoAttrText = NSMutableAttributedString()
            .bold(ANNCUIConstants.additionalInfoTitleText)
            .normal(ANNCUIConstants.additionalInfoP1Text)
        //.italic(ANNCUIConstants.additionalInfoP2Text)
        
        optionAdInfoTextLbl.attributedText = addtionalInfoAttrText
        
        disclaimerTextLbl.text = ANNCUIConstants.signConfText
        
        let date = Date()
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM d, yyyy"
        let todaysDate = formatter.string(from: date)
        dateTF.text = todaysDate
        anncFormInfo.dateSigned = convertDateForPayload(fromDate: todaysDate)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.adjustForKeyboard), name: UIResponder.keyboardWillHideNotification, object: nil)
        tableView.setNeedsDisplay()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func adjustForKeyboard(notification: NSNotification) {
        
        let userInfo = notification.userInfo!
        
        let keyboardScreenEndFrame = (userInfo[UIResponder.keyboardFrameEndUserInfoKey] as! NSValue).cgRectValue
        let keyboardViewEndFrame = view.convert(keyboardScreenEndFrame, from: view.window)
        
        if notification.name == UIResponder.keyboardWillHideNotification {
            tableView.contentInset = UIEdgeInsets.zero
        } else {
            tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: keyboardViewEndFrame.height+doneToolbarHeight, right: 0)
        }
        
        tableView.scrollIndicatorInsets = tableView.contentInset
    }
    
    override func  viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
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
    
    @IBAction func backButtonTapped(_ sender: Any) {
        GenericHelper.shared.showFormLeaveAlert(leaveAction: {
            self.navigationController?.popViewController(animated: true)
        })
    }
    
    override func loadViewData() {
        if !isFormDataLoaded {
            //NetworkStatusManager.shared.registerForReload(view: self)
            
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            getANNCFormData()
        }
    }
    
    func addDoneButtonOnKeyboard() {
        let doneToolbar: UIToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: self.tableView.bounds.width, height: doneToolbarHeight))
        doneToolbar.barStyle       = UIBarStyle.default
        let flexSpace              = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        let done: UIBarButtonItem  = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.doneButtonAction))
        done.tintColor = MyCTCAColor.ctcaGreen.color
        
        var items = [UIBarButtonItem]()
        items.append(flexSpace)
        items.append(done)
        
        doneToolbar.items = items
        doneToolbar.sizeToFit()
        
        for textInput:UITextField in textFieldArray {
            textInput.inputAccessoryView = doneToolbar
            textInput.delegate = self
        }
    }
    
    @objc func doneButtonAction() {
        for textInput:UITextField in textFieldArray {
            textInput.resignFirstResponder()
        }
    }
    
    func convertDateToFormat(fromDate:String) -> String {
        var setDateString:String = ""
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = DatePickerOverlay.DATE_FORMAT
        dateFormatter.timeZone = .current// TimeZone(abbreviation: "GMT+0:00") //Current time zone
        let birthDate = dateFormatter.date(from: fromDate)
        dateFormatter.dateFormat = "MMM d, yyyy"
        setDateString = dateFormatter.string(from: birthDate!)
        
        return setDateString
    }
    
    // Call back for DateTimePickerOverlay
    @objc func setDTPODateTime(_ notification: NSNotification) {
        print("setDTPODateTime object: \(String(describing: notification.object))::: userInfo: \(String(describing: notification.userInfo))")
        var setDateString:String = ""
        if let dateTimeStr = notification.object as? String {
            setDateString = convertDateToFormat(fromDate: dateTimeStr)
        }
        
        let notificationDict = notification.userInfo
        if (notificationDict != nil) {
            let callerField:UIView = notificationDict![DatePickerOverlay.DATE_FIELD_KEY] as! UIView
            if (callerField == dofTF) {
                anncFormInfo.dateOfService = convertDateForPayload(fromDate: setDateString)
                dofTF.text = setDateString
                dofLbl.textColor = MyCTCAColor.formLabel.color
            } else if (callerField == dateTF) {
                anncFormInfo.dateSigned = convertDateForPayload(fromDate: setDateString)
                dateTF.text = setDateString
                dateLbl.textColor = MyCTCAColor.formLabel.color
            }
        }
    }
    
    func convertDateForPayload(fromDate:String) -> String {
        var setDateString:String = ""
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM d, yyyy"
        let birthDate = dateFormatter.date(from: fromDate)
        dateFormatter.dateFormat = "MM/dd/yyyy"
        setDateString = dateFormatter.string(from: birthDate!)
        
        return setDateString
    }
    
    func getANNCFormData() {
        self.showActivityIndicator(view: self.view , message: "Loading..")
        
        formsManager.fetchMRNNumber() {
            mrnVal, status in
            
            self.dismissActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.formsManager.getLastServerError(), onView: self)
            } else {
                if let mrn = mrnVal {
                    DispatchQueue.main.async(execute: {
                        self.mrnValue = mrn
                        self.MRNTF.text = mrn
                    })
                    
                }
            }
        }
    }
    
    func showActivityIndicator(view: UIView, message: String? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.showOverlay(view: self.navigationController!.view, message: message)
        })
    }
    
    func dismissActivityIndicator(completion: (() -> Swift.Void)? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.fadeOutOverlay()
            
            if (completion != nil) {
                completion!()
            }
        })
    }
    
    func formIsValid() -> Bool {
        
        var formIsValid = true
        
        //seciotn 1
        if let treatmentFacTxt: String = treatmentFacTF.text {
            if (treatmentFacTxt != "") {
                treatmentFacLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                treatmentFacLbl.textColor = UIColor.red
            }
        }
        if let firstNameTxt: String = patientFNameTF.text {
            if (firstNameTxt.trimmingCharacters(in: .whitespaces) != "") {
                anncFormInfo.patientName = firstNameTxt.trimmingCharacters(in: .whitespaces)
                patientFNameLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                patientFNameLbl.textColor = UIColor.red
            }
        }
        if let lastNameTxt: String = patientLNameTF.text {
            if (lastNameTxt.trimmingCharacters(in: .whitespaces) != "") {
                if let fName = anncFormInfo.patientName {
                    anncFormInfo.patientName = fName + " " + lastNameTxt.trimmingCharacters(in: .whitespaces)
                }
                patientLNameLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                patientLNameLbl.textColor = UIColor.red
            }
        }
        if let mrnTxt: String = MRNTF.text {
            if (mrnTxt.trimmingCharacters(in: .whitespaces) != "") {
                anncFormInfo.mrn = mrnTxt.trimmingCharacters(in: .whitespaces)
                mrnLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                mrnLbl.textColor = UIColor.red
            }
        }
        if let insuranceTxt: String = insuranceTF.text {
            if (insuranceTxt.trimmingCharacters(in: .whitespaces) != "") {
                anncFormInfo.insuranceName = insuranceTxt.trimmingCharacters(in: .whitespaces)
                insuranceLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                insuranceLbl.textColor = UIColor.red
            }
        }
        if let dobTxt: String = dofTF.text {
            if (dobTxt != "") {
                dofLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                dofLbl.textColor = UIColor.red
            }
        }
        
        //section 3
        if !option1SelectionButton.isSelected && !option2SelectionButton.isSelected {
            formIsValid = false;
            option1SelectionButton.tintColor = UIColor.red
            option2SelectionButton.tintColor = UIColor.red
        }
        
        //section 4
        if let responsiblePartyTxt: String = responsiblePartyTF.text {
            if (responsiblePartyTxt.trimmingCharacters(in: .whitespaces) != "") {
                anncFormInfo.patientSignature = responsiblePartyTxt.trimmingCharacters(in: .whitespaces)
                responsiblePartyTextLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                responsiblePartyTextLbl.textColor = UIColor.red
            }
        }
        if let dobTxt: String = dateTF.text {
            if (dobTxt != "") {
                dateLbl.textColor = MyCTCAColor.formLabel.color
            } else {
                formIsValid = false;
                dateLbl.textColor = UIColor.red
            }
        }
        if let relationTxt: String = relationTF.text {
            if (relationTxt.trimmingCharacters(in: .whitespaces) != "") {
                anncFormInfo.responsibleParty = relationTxt.trimmingCharacters(in: .whitespaces)
            }
        }
        
        return formIsValid
    }
    
    func showSuccessMessage() {
        let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title:ANNCUIConstants.successfulSendTitle,
                                                              message: ANNCUIConstants.successfulSendResponse,
                                                              state: true,
                                                              buttonAction: {
                                                                self.navigationController?.popViewController(animated: true)
                                                              })
            
        GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
    }
    
    func showFailureMessage(message:String) {
        let alertInfo:MyCTCANewAlertInfo = MyCTCANewAlertInfo(title: ANNCUIConstants.unsuccessfulSendTitle,
                                                              message: message,
                                                              state: false,
                                                              buttonAction: nil)
            
        GenericHelper.shared.showNewApptAlert(alertInfo: alertInfo)
    }
    
    
    func applyBorderPropertiesToTF(textField:UITextField) {
        textField.layer.cornerRadius = 5
        textField.layer.masksToBounds = true
        textField.layer.borderWidth = 0.5
    }
    
    // MARK: - Button Actions
    
    @IBAction func option1SelectionTapped(_ sender: Any) {
        //(sender as! UIButton).isSelected = !(sender as AnyObject).isSelected
        
        if option1SelectionButton.isSelected {
            option1SelectionButton.isSelected = false
            option2SelectionButton.isSelected = true
            anncFormInfo.paymentOption = option2
        } else {
            option1SelectionButton.isSelected = true
            option2SelectionButton.isSelected = false
            anncFormInfo.paymentOption = option1
        }
        
        option1SelectionButton.tintColor = MyCTCAColor.ctcaSecondGreen.color
        option2SelectionButton.tintColor = MyCTCAColor.ctcaSecondGreen.color
    }
    
    @IBAction func option2SelectionTapped(_ sender: Any) {
        //(sender as! UIButton).isSelected = !(sender as AnyObject).isSelected
        
        if option2SelectionButton.isSelected {
            option2SelectionButton.isSelected = false
            option1SelectionButton.isSelected = true
            anncFormInfo.paymentOption = option1
        } else {
            option2SelectionButton.isSelected = true
            option1SelectionButton.isSelected = false
            anncFormInfo.paymentOption = option2
        }
        
        option1SelectionButton.tintColor = MyCTCAColor.ctcaSecondGreen.color
        option2SelectionButton.tintColor = MyCTCAColor.ctcaSecondGreen.color
    }
    
    @IBAction func SendANNC(_ sender: Any) {
        
        // Validate Form
        if (formIsValid()) {
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_ANNC_SEND_TAP)
            if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
                return
            }
            
            self.showActivityIndicator(view: self.view, message: "Submitting ANNC Request")
            
            formsManager.submitANNCForm(anncInfo: anncFormInfo) {
                success, expt, status in
                
                self.dismissActivityIndicator()
                
                if status == .FAILED {
                    self.showFailureMessage(message: self.formsManager.getLastServerError().errorMessage)
                    AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ANNC_SUBMIT_FAIL)
                } else {
                    if success {
                        self.showSuccessMessage()
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ANNC_SUBMIT_SUCCESS)
                    } else {
                        var exceptionString = ""
                        if let exptnErr = expt?.exception {
                            
                            if exptnErr.errors.count > 0 {
                                exceptionString = exptnErr.errors[0].errorMessage
                            }
                            
                        } else {
                            exceptionString = ANNCUIConstants.unsuccessfulSendResponse
                        }
                        
                        self.showFailureMessage(message: exceptionString)
                        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ALERT_ANNC_SUBMIT_FAIL)
                    }
                }
            }
        }
        else {
            GenericHelper.shared.showAlert(withtitle: "Form Incomplete", andMessage: ANNCUIConstants.invalidFormResponse, onView: self)
        }
    }
}

extension ANNCFormTableViewController : RecordSelectionDelegate {
    func didMakeSelection(value:Any, type: SelectionType) {
        if (type == .treatmentFacility) {
            let val = value as! String

            treatmentFacTF.text = val
            treatmentFacLbl.textColor = MyCTCAColor.formLabel.color
            if let facility = findFacilityInList(val) {
                anncFormInfo.facilityName = facility
            }
        }
    }
    
    func findFacilityInList(_ search: String) -> String? {
        if let key = AppSessionManager.shared.currentUser.allFacilitesNamesList.first(where: { $0.value == search })?.key {
            return key
        }
        
        return nil
    }
}

// MARK: - Perform
extension ANNCFormTableViewController  {
    func mainSectionSelection(row: Int) {
        if row == 0 {
            // Treatment Facility
            let treatmentFacilityViewController: RecordSelectionViewController = self.storyboard?.instantiateViewController(withIdentifier: "RecordSelectionViewController") as! RecordSelectionViewController
            treatmentFacilityViewController.delegate = self
            treatmentFacilityViewController.selectionType = .treatmentFacility
            
            var facilities = [(String,String,String)]()
            
            for item in AppSessionManager.shared.currentUser.allFacilitesNamesList {
                facilities.append((item.value, item.key, item.value))
            }
            
            if let text = treatmentFacTF.text, text != "" {
                treatmentFacilityViewController.selectedOption = text
            }
            
            treatmentFacilityViewController.treatmentFacilities = facilities
            self.navigationController?.pushViewController(treatmentFacilityViewController, animated: true)
            
        } else if row == 5 {
            
            var curDate = Date()
            if let dateTimeStr = dofTF.text, dateTimeStr != "" {
                print("setDTPODateTime dateTimeStr: \(String(describing: dateTimeStr))")
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "MMM d, yyyy"
                dateFormatter.timeZone = .current// TimeZone(abbreviation: "GMT+0:00") //Current time zone
                curDate = dateFormatter.date(from: dateTimeStr)!
            }
            
            let bgImage:UIImage = UIImage(named: "check-square-o_white.png")!
            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: "Select start date of the service",
                                                 bgImage: bgImage,
                                                 mode: .date,
                                                 minDate: Date(),
                                                 currDate: curDate,
                                                 dateForField:dofTF)
        }
    }
    
    func lastSectionSelection(row: Int) {
        if row == 2 {
            let bgImage:UIImage = UIImage(named: "check-square-o_white.png")!
            DatePickerOverlay.shared.showOverlay(view: (self.navigationController?.view)!,
                                                 message: "Select date.",
                                                 bgImage: bgImage,
                                                 mode: .date,
                                                 dateForField:dateTF)
        }
    }
}


// MARK: - Table view delegates
extension ANNCFormTableViewController  {
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 1 || indexPath.section == 2 {
            return UITableView.automaticDimension
        } else if indexPath.section == 3 && indexPath.row == 0 {
            return UITableView.automaticDimension
        }  else if indexPath.section == 3 && indexPath.row == 2 {
            return UITableView.automaticDimension
        } else {
            return cellDefaultHeight
        }
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let cell = MoreROISectionCell()
        
        var sectionTitle = ""
        switch (section) {
        case 0:
            sectionTitle = "PATIENT INFORMATION"
        case 1:
            sectionTitle = "ADVANCE NOTICE OF NON-COVERAGE"
        case 2:
            sectionTitle = "OPTIONS"
        case 3:
            sectionTitle = "AUTHORIZATION"
        default:
            sectionTitle = ""
        }
        cell.prepareView(sectionTitle)
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: false)
        print("didSelectRowAt: \(indexPath)")
        
        if indexPath.section == 0 {
            mainSectionSelection(row: indexPath.row)
        } else if indexPath.section == 3 {
            lastSectionSelection(row: indexPath.row)
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return headerHeight
    }
}

extension ANNCFormTableViewController  {
    // MARK: TextField Delegate
    func textFieldDidBeginEditing(_ textField: UITextField) {
        textFieldDoesHaveFocus(textField)
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        textFieldDoesNotHaveFocus(textField)
    }
    
    func textFieldDoesHaveFocus(_ textField: UITextField) {
        
        if let label: UILabel = matchLabelToTextField(textField) {
            label.textColor = MyCTCAColor.ctcaSecondGreen.color
        }
        
        applyBorderPropertiesToTF(textField: textField)
        textField.layer.borderColor = MyCTCAColor.ctcaSecondGreen.color.cgColor
    }
    
    func textFieldDoesNotHaveFocus(_ textField: UITextField) {
        
        if let label: UILabel = matchLabelToTextField(textField) {
            label.textColor = MyCTCAColor.formLabel.color
        }
        
        applyBorderPropertiesToTF(textField: textField)
        textField.layer.borderColor = MyCTCAColor.formLines.color.cgColor
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if let phoneTextField = textField as? PhoneNumberTextField {
            return phoneTextField.isMaxLengthReached(range, string)
        } else {
            return true
        }
    }
    
    func matchLabelToTextField(_ textField: UITextField) -> UILabel? {
        var label: UILabel? = nil
        
        switch textField {
        case treatmentFacTF:
            label = treatmentFacLbl
        case insuranceTF:
            label = insuranceLbl
        case patientFNameTF:
            label = patientFNameLbl
        case patientLNameTF:
            label = patientLNameLbl
        case MRNTF:
            label = mrnLbl
        case dofTF:
            label = dofLbl
        case dateTF:
            label = dateLbl
        case relationTF:
            label = relationTextLbl
        case responsiblePartyTF:
            label = responsiblePartyTextLbl
        default:
            label = nil
        }
        
        return label
    }
}
