//
//  CalenderView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 18/11/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit
import JTAppleCalendar

class CalenderView: UIView {
    
    @IBOutlet var contentView: UIView!
    @IBOutlet weak var collectionView: JTAppleCalendarView!
    @IBOutlet weak var calenderView: CTCACardView!
    
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
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func commonInit() {
        Bundle(for: CTCANoDataView.self).loadNibNamed("CalenderView", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = self.bounds
        
        contentView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        setUpUI()
    }
    
    private func initCollectionView() {
      let nib = UINib(nibName: "DateCell", bundle: nil)
      collectionView.register(nib, forCellWithReuseIdentifier: "dateCell")
    }
    
    func setUpUI() {
        DispatchQueue.main.async { [self] in
            
            collectionView.scrollDirection = .horizontal
            collectionView.scrollingMode   = .stopAtEachCalendarFrame
            collectionView.showsHorizontalScrollIndicator = false
            
            formatterMonth.dateFormat = "MMMM yyyy"
                        
            if let calender = NSCalendar(calendarIdentifier: NSCalendar.Identifier.gregorian) {
                gregorianCalender = calender as Calendar
                curMonth = gregorianCalender.component(.month, from: Date())
                curYear = gregorianCalender.component(.year, from: Date())
            }
                        
            calenderView.layer.borderColor = UIColor.ctca_selection_purple.cgColor
            
            //Need to prepopulate
            if dateSlot.index != -1 {
//                if dateSlot.session1 {
//                    setTimeCardSelected(cardView: timrCardMorningView)
//                }
//                if dateSlot.session2 {
//                    setTimeCardSelected(cardView: timeCardEveningView)
//                }
                if let selDate = dateSlot.date {
                    self.collectionView.scrollToDate(selDate) {
                        self.collectionView.selectDates([selDate])
                    }
                }
            }
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
extension CalenderView: JTAppleCalendarViewDataSource {
    func configureCalendar(_ calendar: JTAppleCalendarView) -> ConfigurationParameters {
        let startDate = Date()
        let endDate = gregorianCalender.date(byAdding: .month, value: 36, to: Date()) ?? Date()
        return ConfigurationParameters(startDate: startDate, endDate: endDate,
                                       generateInDates: .forAllMonths,
                                       generateOutDates: .off)
    }
    
}

@available(iOS 13.0, *)
extension CalenderView: JTAppleCalendarViewDelegate {
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
        }
    }
}

@available(iOS 13.0, *)
extension CalenderView: DateHeaderProtocol {
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
