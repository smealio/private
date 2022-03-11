//
//  CalenderViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 03/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import JTAppleCalendar

protocol CalenderViewDelegates: AnyObject {
    func didSelectDate(dateSlot:NewApptCalenderSelection)
    func didCancel()
}

@available(iOS 13.0, *)
class CalenderViewController: UIViewController {
    
    @IBOutlet weak var collectionView: JTAppleCalendarView!
    @IBOutlet weak var dateTitke: UILabel!
    @IBOutlet weak var dateDescription: UILabel!
    @IBOutlet weak var calenderView: CTCACardView!
    @IBOutlet weak var timeTitle: UILabel!
    @IBOutlet weak var timeDescription: UILabel!
    
    @IBOutlet weak var timrCardMorningView: CTCACardView!
    @IBOutlet weak var timeCardEveningView: CTCACardView!
    
    @IBOutlet weak var morningSelectionIV: UIImageView!
    @IBOutlet weak var eveningSelectionIV: UIImageView!
    
    @IBOutlet weak var eveningSelButton: UIButton!
    @IBOutlet weak var morningSelButton: UIButton!
    
    @IBOutlet weak var addOptionButton: CTCAGreenButton!
    
    private var closeBarButton = UIBarButtonItem()
    private var datePicker = UIDatePicker()
    private var formatterMonth = DateFormatter()
    private var selectedDateCell:DateCell?
    private var gregorianCalender = Calendar.current
    private var maxSections = 4 //90
    private var curMonth = 0
    private var curYear = 0
    
    var dateSlot:NewApptCalenderSelection = NewApptCalenderSelection(date: nil)
    weak var delegate:CalenderViewDelegates?
    var preSelectedDates = [NewApptCalenderSelection]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        collectionView.scrollDirection = .horizontal
        collectionView.scrollingMode   = .stopAtEachCalendarFrame
        collectionView.showsHorizontalScrollIndicator = false
        
        formatterMonth.dateFormat = "MMMM yyyy"
        
        if let calender = NSCalendar(calendarIdentifier: NSCalendar.Identifier.gregorian) {
            gregorianCalender = calender as Calendar
            curMonth = gregorianCalender.component(.month, from: Date())
            curYear = gregorianCalender.component(.year, from: Date())
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setUpUI()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        AppDelegate.AppUtility.lockOrientation(.portrait)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        AppDelegate.AppUtility.lockOrientation(.all)
    }
    
    func setUpUI() {
        DispatchQueue.main.async { [self] in
            addOptionButton.setDisabled()
            
            //close bar button
            closeBarButton.target = self
            closeBarButton.image = #imageLiteral(resourceName: "close")
            closeBarButton.action = #selector(close)
            closeBarButton.tintColor = .systemGray
            self.navigationItem.setRightBarButtonItems([closeBarButton], animated: true)
            
            addOptionButton.isEnabled = false
            addOptionButton.backgroundColor = UIColor.ctca_button_disabled
            
            calenderView.layer.borderColor = UIColor.ctca_selection_purple.cgColor
            
            //Need to prepopulate
            if dateSlot.index != -1 {
                if dateSlot.session1 {
                    setTimeCardSelected(cardView: timrCardMorningView)
                }
                if dateSlot.session2 {
                    setTimeCardSelected(cardView: timeCardEveningView)
                }
                if let selDate = dateSlot.date {
                    self.collectionView.scrollToDate(selDate) {
                        self.collectionView.selectDates([selDate])
                    }
                }
            }
        }
    }
    
    @IBAction func cardSelButtonTapped(_ sender: Any) {
        if eveningSelButton == (sender as? UIButton) {
            if timeCardEveningView.isSelected {
                setTimeCardUnSelected(cardView: timeCardEveningView)
            } else {
                setTimeCardSelected(cardView: timeCardEveningView)
            }
        } else {
            if timrCardMorningView.isSelected {
                setTimeCardUnSelected(cardView: timrCardMorningView)
            } else {
                setTimeCardSelected(cardView: timrCardMorningView)
            }
        }
    }
    
    func setTimeCardSelected(cardView: CTCACardView) {
        for view in cardView.subviews {
            if let label = view as? UILabel {
                label.textColor = UIColor.ctca_selection_purple
            }
            if let imageView = view as? UIImageView {
                imageView.image = UIImage(named: "round-check")
                imageView.tintColor = UIColor.ctca_selection_purple
            }
        }
        
        cardView.setFocused()
        setAddOptionActive()
    }
    
    func setTimeCardUnSelected(cardView: CTCACardView) {
        for view in cardView.subviews {
            if let label = view as? UILabel {
                label.textColor = UIColor.ctca_dark_gray
            }
            if let imageView = view as? UIImageView {
                imageView.image = UIImage(named: "round-gray")
                imageView.backgroundColor = UIColor.clear
            }
        }
        
        cardView.setUnFocused()
        setAddOptionActive()
    }
    
    @IBAction func addOptionButtonTapped(_ sender: Any) {
        if let del = delegate, let date = selectedDateCell?.date {
            dateSlot.date = date
            dateSlot.session1 = timrCardMorningView.isSelected
            dateSlot.session2 = timeCardEveningView.isSelected
            
            del.didSelectDate(dateSlot: dateSlot)
        }
        close()
    }
    
    @IBAction func closeTapped(_ sender: Any) {
        if let del = delegate {
            del.didCancel()
        }
        close()
    }
    
    @objc func close() {
        dismiss(animated: true, completion: nil)
    }
    
    private func setAddOptionActive() {
        if (timeCardEveningView.isSelected || timrCardMorningView.isSelected) &&
            selectedDateCell != nil {
            addOptionButton.setEnabled()
        } else {
            addOptionButton.setDisabled()
        }
    }
    
    //calender related
    func configureCell(view: JTAppleCell?, cellState: CellState) {
        guard let cell = view as? DateCell  else { return }
        cell.dateLabel.text = cellState.text
        handleCellTextColor(cell: cell, cellState: cellState)
    }
    
    func handleCellTextColor(cell: DateCell, cellState: CellState) {
        cell.dateLabel.textColor = UIColor.clear
        
        if cellState.dateBelongsTo == .thisMonth {
            if  NSCalendar(calendarIdentifier: NSCalendar.Identifier.gregorian)!.isDateInWeekend(cellState.date) {
                cell.dateLabel.textColor = UIColor.ctca_gray30
            } else {
                for item in preSelectedDates where item.date != nil {
                    if gregorianCalender.compare(item.date!, to: cellState.date, toGranularity: .day) == .orderedSame {
                        cell.dateLabel.textColor = UIColor.ctca_dark_gray
                        return
                    }
                }
                if gregorianCalender.isDateInTomorrow(cellState.date) {
                    cell.dateLabel.textColor = UIColor.ctca_gray30
                } else {
                    let comparison = gregorianCalender.compare(cellState.date, to: Date(), toGranularity: .day)
                    
                    if comparison == ComparisonResult.orderedDescending {
                        cell.dateLabel.textColor = UIColor.ctca_dark_gray
                    } else {
                        cell.dateLabel.textColor = UIColor.ctca_gray30
                    }
                }
            }
        }
    }
}

@available(iOS 13.0, *)
extension CalenderViewController: JTAppleCalendarViewDataSource {
    func configureCalendar(_ calendar: JTAppleCalendarView) -> ConfigurationParameters {
        let startDate = Date()
        let endDate = gregorianCalender.date(byAdding: .month, value: 36, to: Date()) ?? Date()
        return ConfigurationParameters(startDate: startDate, endDate: endDate,
                                       generateInDates: .forAllMonths,
                                       generateOutDates: .off)
    }
    
}

@available(iOS 13.0, *)
extension CalenderViewController: JTAppleCalendarViewDelegate {
    func calendar(_ calendar: JTAppleCalendarView, cellForItemAt date: Date, cellState: CellState, indexPath: IndexPath) -> JTAppleCell {
        let cell = calendar.dequeueReusableJTAppleCell(withReuseIdentifier: "dateCell", for: indexPath) as! DateCell
        
        cell.noBGView.layer.cornerRadius = 6.0
        cell.noBGView.layer.borderWidth = 1
        
        for item in preSelectedDates where item.date != nil {
            if gregorianCalender.compare(item.date!, to: cellState.date, toGranularity: .day) == .orderedSame {
                cell.noBGView.backgroundColor = .white
                cell.noBGView.layer.borderColor = UIColor.ctca_gray40.cgColor
                cell.date = cellState.date
                cell.addShadow()
                self.calendar(calendar, willDisplay: cell, forItemAt: date, cellState: cellState, indexPath: indexPath)
                return cell
            }
        }
        
        cell.noBGView.backgroundColor = .clear
        cell.noBGView.layer.borderColor = UIColor.clear.cgColor
        cell.date = cellState.date
        
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
        if cellState.dateBelongsTo == .thisMonth || cellState.dateBelongsTo == .followingMonthWithinBoundary{
            if  gregorianCalender.isDateInWeekend(cellState.date) || gregorianCalender.isDateInTomorrow(cellState.date) ||
                    gregorianCalender.compare(cellState.date, to: Date(), toGranularity: .day) != ComparisonResult.orderedDescending
            {
                return
            }
            
            guard let selectedCell = cell as? DateCell  else {
                return
            }
            
            
            for item in preSelectedDates where item.date != nil {
                if gregorianCalender.compare(item.date!, to: cellState.date, toGranularity: .day) == .orderedSame {
                    return
                }
            }

            if let previousDate = selectedDateCell, let date = previousDate.date {
                if Calendar.current.isDate(date, inSameDayAs: Date()) {
                    previousDate.noBGView.layer.borderColor = UIColor.ctca_gray_border.cgColor
                    previousDate.noBGView.backgroundColor = UIColor.ctca_gray10
                } else {
                    previousDate.noBGView.backgroundColor = .clear
                    previousDate.noBGView.layer.borderColor = UIColor.clear.cgColor
                }
                
                previousDate.dateLabel.textColor = UIColor.ctca_dark_gray
            }
            
            //if not re-selected
            if selectedCell == selectedDateCell {
                selectedDateCell = nil
            } else {
                selectedCell.noBGView.backgroundColor = UIColor.ctca_selection_purple
                selectedCell.noBGView.layer.borderColor = UIColor.clear.cgColor
                selectedCell.dateLabel.textColor = UIColor.ctca_white
                
                selectedDateCell = selectedCell
            }
            
            setAddOptionActive()
        }
    }
}

@available(iOS 13.0, *)
extension CalenderViewController: DateHeaderProtocol {
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
