//
//  PeriodSelectionViewControllerNew.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 19/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import JTAppleCalendar

let calenderStartDate = "2010 01 01"
enum DateSelection: Int {
    case START = 1, END, RANGE, NONE
}

class PeriodSelectionViewControllerNew: UIViewController {
    
    @IBOutlet weak var collectionView: JTAppleCalendarView!
    @IBOutlet weak var calenderView: CTCACardView!
    @IBOutlet weak var startDateLabel: UILabel!
    @IBOutlet weak var endDateLabel: UILabel!
    
    private var formatterMonth = DateFormatter()
    private var gregorianCalender = Calendar.current
    
    private var maxSections = 4 //90
    private var curMonth = 0
    private var curYear = 0
    
    @IBOutlet weak var endDateSelectionView: CTCACardView!
    @IBOutlet weak var startDateSelectionView: CTCACardView!
    
    @IBOutlet weak var downloadButton: CTCAGreenButton!
    @IBOutlet weak var startDateButton: SwitchButton!
    @IBOutlet weak var endDateButton: SwitchButton!
    
    private var dateSelection:DateSelection = .START
        
    private var selectedDatesRange = [Date]()
    weak var delegate:PeriodSelectionProtocol?
    
    private var startDateString:String?
    private var endDateString:String?
    
    private var selectedEndDate:Date? {
        get {
            if let date = endDateString {
                return DateConvertor.convertToDateFromString(dateString: date, inputFormat: .baseForm)
            }
            return nil
        }
    }
    
    private var selectedStartDate:Date? {
        get {
            if let date = startDateString {
                return DateConvertor.convertToDateFromString(dateString: date, inputFormat: .baseForm)
            }
            return nil
        }
    }
        
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let tableViewLoadingCellNib = UINib(nibName: "CalenderDateTableViewCell", bundle: nil)
        self.collectionView.register(tableViewLoadingCellNib, forCellWithReuseIdentifier: "CalenderDateTableViewCell")

        collectionView.scrollDirection = .horizontal
        collectionView.scrollingMode   = .stopAtEachCalendarFrame
        collectionView.showsHorizontalScrollIndicator = false
        
        formatterMonth.dateFormat = "MMMM yyyy"
        
        if let calender = NSCalendar(calendarIdentifier: NSCalendar.Identifier.gregorian) {
            gregorianCalender = calender as Calendar
            curMonth = gregorianCalender.component(.month, from: Date())
            curYear = gregorianCalender.component(.year, from: Date())
        }
                
        // Do any additional setup after loading the view.
        setUpUI()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        AppDelegate.AppUtility.lockOrientation(.portrait)
         
        collectionView.allowsMultipleSelection = true
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        AppDelegate.AppUtility.lockOrientation(.all)
    }
    
    @IBAction func downloadTapped(_ sender: Any) {
        if let startDate = startDateLabel.text, let endDate = endDateLabel.text {
            if GenericHelper.shared.isValidateStartAndEndDates(startDate, endDate) {
                
                let okAction = UIAlertAction(title: "Download", style: .default) {
                    [weak self]
                    (action) in
                    guard let self = self else { return }
                    
                    if let vcDel = self.delegate {
                        vcDel.periodSelected(fromDate:startDate, toDate: endDate)
                    }
                    self.dismiss(animated: true, completion: nil)
                }
                
                let cancelAction = UIAlertAction(title: "Cancel", style: .destructive) {
                    (action) in
                }
                
                GenericHelper.shared.showAlert(withtitle: "\"Schedule.pdf\"", andMessage: "will be downloaded to your device.", onView: self, okaction: cancelAction, otheraction: okAction)
            } else {
                GenericHelper.shared.showAlert(withtitle: "myCTCA", andMessage: CommonMsgConstants.invalidDatesMessage, onView: self)
            }
        }
    }
    
    func setButtonFocus(button: UIButton) {
        button.layer.borderColor = UIColor.ctca_blue_focus.cgColor
        button.layer.cornerRadius = 6
        button.layer.borderWidth = 1
    }
    
    func unsetButtonFocus(button: UIButton) {
        button.layer.borderColor = UIColor.clear.cgColor
    }
    
    @IBAction func closeTapped(_ sender: Any) {
        if let vcDel = delegate {
            vcDel.cancelledPeriodSelection()
        }
        dismiss(animated: true, completion: nil)
    }
}

//Calender releated
extension PeriodSelectionViewControllerNew {
    
    func setDate() {
        if dateSelection == .END {
            endDateLabel.text = endDateString
        } else {
            startDateLabel.text = startDateString
        }
        
        if let startDate = selectedStartDate,
           let endDate = selectedEndDate {
            print("startDate - \(startDate)")
            print("endDate - \(endDate)")
            if startDate <= endDate {
                //dateSelection = .RANGE
                
                var returnDates: [Date] = []
                var currentDate = startDate
                repeat {
                    if !gregorianCalender.isDateInWeekend(currentDate) {
                        returnDates.append(currentDate)
                    }
                    currentDate = gregorianCalender.startOfDay(for: gregorianCalender.date(
                        byAdding: .day, value: 1, to: currentDate)!)
                } while currentDate <= endDate
                
                selectedDatesRange.removeAll()
                selectedDatesRange = returnDates
                if #available(iOS 13.0, *) {
                    selectDates()
                }
            } else {
                print("setDate - startDate > endDate ")
                selectedDatesRange.removeAll()
                selectedDatesRange.append(startDate)
                selectedDatesRange.append(endDate)
                
                GenericHelper.shared.showAlert(withtitle: "myCTCA", andMessage: CommonMsgConstants.invalidDatesMessage, onView: self)
            }
        } else if let startDate = selectedStartDate {
            selectedDatesRange.removeAll()
            selectedDatesRange.append(startDate)
        } else if let endDate = selectedEndDate {
            selectedDatesRange.removeAll()
            selectedDatesRange.append(endDate)
        }
        
        if #available(iOS 13.0, *) {
            selectDates()
        }
        
        if let startText = startDateLabel.text, let endText = endDateLabel.text,
           !startText.isEmpty, !endText.isEmpty {
            downloadButton.setEnabled()
        } else {
            downloadButton.setDisabled()
        }
    }
    
    @IBAction func endDateSelectionButtonTapped(_ sender: Any) {
        if endDateSelectionView.isSelected {
            endDateSelectionView.setUnFocused()
            dateSelection = .NONE
            unsetButtonFocus(button: endDateButton)
        } else {
            endDateSelectionView.setFocused()
            startDateSelectionView.setUnFocused()
            
            dateSelection = .END
            
            setButtonFocus(button: endDateButton)
            unsetButtonFocus(button: startDateButton)
        }
    }

    @IBAction func startDateSelectionButtonTapped(_ sender: Any) {
        if startDateSelectionView.isSelected {
            startDateSelectionView.setUnFocused()
            dateSelection = .NONE
            unsetButtonFocus(button: startDateButton)
        } else {
            startDateSelectionView.setFocused()
            endDateSelectionView.setUnFocused()
            
            dateSelection = .START
            setButtonFocus(button: startDateButton)
            unsetButtonFocus(button: endDateButton)
        }
    }
        
    func setUpUI() {
        DispatchQueue.main.async { [self] in
            self.startDateSelectionView.setFocused()
            
            startDateLabel.text = ""
            endDateLabel.text = ""
            
            downloadButton.setDisabled()
            setButtonFocus(button: startDateButton)
            
            self.collectionView.scrollToDate(Date()) {
                print("scrollToDate Done")
            }
        }
    }
    
    //calender related
    func configureCell(view: JTAppleCell?, cellState: CellState) {
        guard let cell = view as? CalenderDateTableViewCell  else { return }
        cell.dateLabel.text = cellState.text
        handleCellTextColor(cell: cell, cellState: cellState)
    }
    
    func handleCellTextColor(cell: CalenderDateTableViewCell, cellState: CellState) {
        cell.dateLabel.textColor = UIColor.clear
        
        if cellState.dateBelongsTo == .thisMonth {
            if  NSCalendar(calendarIdentifier: NSCalendar.Identifier.gregorian)!.isDateInWeekend(cellState.date) {
                cell.dateLabel.textColor = UIColor.ctca_gray30
            } else {
                cell.dateLabel.textColor = UIColor.ctca_dark_gray
            }
            
            if selectedDatesRange.count > 0 {
                for item in selectedDatesRange {
                    if gregorianCalender.compare(cellState.date, to: item, toGranularity: .day) == .orderedSame {
                        cell.dateLabel.textColor = UIColor.ctca_white
                    }
                }
            }
        }
    }
}

@available(iOS 13.0, *)
extension PeriodSelectionViewControllerNew: JTAppleCalendarViewDataSource {
    func configureCalendar(_ calendar: JTAppleCalendarView) -> ConfigurationParameters {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy MM dd"
        let startDate = formatter.date(from: calenderStartDate)!
        
        let endDate = gregorianCalender.date(byAdding: .month, value: 36, to: Date()) ?? Date()
        return ConfigurationParameters(startDate: startDate, endDate: endDate,
                                       generateInDates: .forAllMonths,
                                       generateOutDates: .off)
    }
    
    
}

@available(iOS 13.0, *)
extension PeriodSelectionViewControllerNew: JTAppleCalendarViewDelegate {
    func calendar(_ calendar: JTAppleCalendarView, cellForItemAt date: Date, cellState: CellState, indexPath: IndexPath) -> JTAppleCell {
        let cell = calendar.dequeueReusableJTAppleCell(withReuseIdentifier: "CalenderDateTableViewCell", for: indexPath) as! CalenderDateTableViewCell
        
        cell.noBGView.layer.cornerRadius = 6.0
        cell.noBGView.layer.borderWidth = 1
        
        cell.noBGView.backgroundColor = .clear
        cell.noBGView.layer.borderColor = UIColor.clear.cgColor
        cell.date = cellState.date
        
        if selectedDatesRange.count > 0 {
            for item in selectedDatesRange {
                if gregorianCalender.compare(cellState.date, to: item, toGranularity: .day) == .orderedSame &&
                    cellState.dateBelongsTo == .thisMonth {
                    cell.noBGView.backgroundColor = UIColor.ctca_selection_purple
                    cell.noBGView.layer.borderColor = UIColor.clear.cgColor
                }
            }
        }
        
        self.calendar(calendar, willDisplay: cell, forItemAt: date, cellState: cellState, indexPath: indexPath)
        return cell
    }
    
    func calendar(_ calendar: JTAppleCalendarView, willDisplay cell: JTAppleCell, forItemAt date: Date, cellState: CellState, indexPath: IndexPath) {
        configureCell(view: cell, cellState: cellState)
    }
    
    func calendar(_ calendar: JTAppleCalendarView, headerViewForDateRange range: (start: Date, end: Date), at indexPath: IndexPath) -> JTAppleCollectionReusableView {
        let header = calendar.dequeueReusableJTAppleSupplementaryView(withReuseIdentifier: "DateHeader", for: indexPath) as! DateHeader
        header.delegate = self
        header.monthTitleLabel.text = formatterMonth.string(from: range.start)
        if indexPath.section == 0 {
            header.leftButton.isEnabled = false
            header.leftButton.tintColor = UIColor.ctca_dark_gray
        } else if indexPath.section == maxSections - 1 {
            header.rightButton.isEnabled = false
            header.rightButton.tintColor = UIColor.ctca_dark_gray
        } else {
            header.keepAllActive()
        }
        return header
    }
    
    func calendarSizeForMonths(_ calendar: JTAppleCalendarView?) -> MonthSize? {
        return MonthSize(defaultSize: 70)
    }
    
    func calendar(_ calendar: JTAppleCalendarView, didSelectDate date: Date, cell: JTAppleCell?, cellState: CellState) {
        if dateSelection == .NONE {
            return
        }
        
        guard let selectedCell = cell as? CalenderDateTableViewCell  else {
            return
        }
        
        if dateSelection == .START {
            //if not re-selected
            if selectedCell.date == selectedStartDate {
                startDateString = nil
                
                if selectedCell.date != selectedEndDate {
                    selectedCell.noBGView.backgroundColor = .clear
                    selectedCell.noBGView.layer.borderColor = UIColor.clear.cgColor
                    selectedCell.dateLabel.textColor = UIColor.ctca_dark_gray
                }
            } else {
                selectedCell.noBGView.backgroundColor = UIColor.ctca_selection_purple
                selectedCell.noBGView.layer.borderColor = UIColor.clear.cgColor
                selectedCell.dateLabel.textColor = UIColor.ctca_white
                startDateString = DateConvertor.convertToStringFromDate(date: selectedCell.date, outputFormat: .baseForm)
            }
            
            if selectedDatesRange.count > 0 {
                selectedDatesRange.removeAll()
                deselectDates()
            }
            
            setDate()
        } else if dateSelection == .END {
            //if not re-selected
            if selectedCell.date == selectedEndDate {
                endDateString = nil
                
                if selectedCell.date != selectedStartDate {
                    selectedCell.noBGView.backgroundColor = .clear
                    selectedCell.noBGView.layer.borderColor = UIColor.clear.cgColor
                    selectedCell.dateLabel.textColor = UIColor.ctca_dark_gray
                }
            } else {
                selectedCell.noBGView.backgroundColor = UIColor.ctca_selection_purple
                selectedCell.noBGView.layer.borderColor = UIColor.clear.cgColor
                selectedCell.dateLabel.textColor = UIColor.ctca_white
                endDateString = DateConvertor.convertToStringFromDate(date: selectedCell.date, outputFormat: .baseForm)
            }
            
            if selectedDatesRange.count > 0 {
                selectedDatesRange.removeAll()
                deselectDates()
            }
            
            setDate()
        }
    }
    
    func deselectDates() {
        collectionView.reloadData()
    }
    
    func selectDates() {
        collectionView.reloadData()
    }
}

@available(iOS 13.0, *)
extension PeriodSelectionViewControllerNew: DateHeaderProtocol {
    func didTappedChange(_ index: Int) {
        
        if index == -1 {
            if curMonth == 1 {
                curYear = curYear - 1
                curMonth = 12
            } else {
                curMonth = curMonth - 1
            }
        } else {
            if curMonth == 12 {
                curYear = curYear + 1
                curMonth = 1
            } else {
                curMonth = curMonth + 1
            }
        }
        
        let newDateString = "\(curMonth)/01/\(curYear)"
        if let nextDate = DateConvertor.convertToDateFromString(dateString: newDateString, inputFormat: .usStandardForm2) {
            self.collectionView.scrollToDate(nextDate)
        }
    }
}
