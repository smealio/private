//
//  ImportantNumbersTableViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 26/08/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class ImportantNumbersTableViewController: UITableViewController {

    var numberCount = ImportantNumbersStrings.numberTitles.count + 1
    var primaryFacility:Facility?
    var requiredExtraNumberCell = false
    
    //occ related
    var sec1Numbers = [(String, String, String, Bool)]()
    var sec2Numbers = [(String, String, String, Bool)]()
    var sec3Numbers = [(String, String, String, Bool)]()
    
    var medRecNumbers = [String:String]()
    var financialCounsellingNumbers = [String:String]()

    var sec1Header = ""
    var sec2Header = ""
    var sec3Header = ""
    
    private var isOCCFac = false
    private var listOfNumbers = [ImportantNumber]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let primaryFac = AppSessionManager.shared.currentUser.primaryFacility {
            primaryFacility = primaryFac
            
            if let shortDisplayName = primaryFacility?.shortDisplayName, shortDisplayName == "CTCA OCC" {
                isOCCFac = true
            } else if let secondaryPhone = primaryFac.schedulingSecondaryPhone, !secondaryPhone.isEmpty {
                requiredExtraNumberCell = true
            }
        }
        
        if isOCCFac {
            prepareOCCNumbers()
        } else {
            preparePresetNumbers()
            createPhoneNumbers()
        }
    }
    
    func preparePresetNumbers() {
        medRecNumbers["CTCA Atlanta"] = "(770) 400-6100"
        medRecNumbers["CTCA Chicago"] = "(847) 872-6321"
        medRecNumbers["CTCA Phoenix"] = "(623) 207-3080"
        
        financialCounsellingNumbers["CTCA Atlanta"] = "(855) 848-8659"
        financialCounsellingNumbers["CTCA Chicago"] = "(847) 746-6997"
        financialCounsellingNumbers["CTCA Phoenix"] = "(623) 207-3040"
    }
    
    func prepareOCCNumbers() {
        let item1 = ("General Inquiries", "For concerns regarding your care, access to medical records and any general questions you might have.", "(866) 801-7097", false)
        let item2 = ("Technical Support", "For questions related to creating a portal account, accessing the portal, and password resets.", "(800) 234-0482", false)
        let item3 = ("Atlanta Hospital", "600 Celebrate Life Pkwy. \nNewnan, GA 30265", "(770) 400-6000", true)
        let item4 = ("Chicago Hospital", "2520 Elisha Ave.\nZion, IL 60099", "(847) 872-4561", true)
        let item5 = ("Phoenix Hospital", "14200 W. Celebrate Life Way\nGoodyear, AZ 85338", "(847) 872-4561", true)
        sec1Numbers = [item1, item2, item3, item4, item5]
        
        let item6 = ("Downtown Chicago", "", "(312) 535-7863", false)
        let item7 = ("Gurnee", "", "(847) 665-0936", false)
        sec2Numbers = [item6, item7]
        
        let itme8 = ("North Phoenix", "", "(847) 872-4561", false)
        let item9 = ("Scottsdale", "", "(928) 482-4670", false)
        sec3Numbers = [itme8, item9]
        
        sec2Header = "CHICAGO\nOutpatient Care Centers"
        sec3Header = "PHOENIX\nOutpatient Care Centers"
    }
    
    func getPhoneNumberForService(index:Int) -> String {
        
        switch index {
        case 1:
            return primaryFacility?.mainPhone ?? ""
        case 2:
            return formatedTechSupportNo() ?? ""
        case 3:
            return primaryFacility?.careManagementPhone ?? ""
        case 4:
            return primaryFacility?.schedulingPhone ?? ""
        case 5:
            return primaryFacility?.travelAndAccommodationsPhone ?? ""
        case 6:
            if let shortDisplayName = primaryFacility?.shortDisplayName {
                return medRecNumbers[shortDisplayName] ?? ""
            }
            return ""
        case 7:
            if let shortDisplayName = primaryFacility?.shortDisplayName {
                return financialCounsellingNumbers[shortDisplayName] ?? ""
            }
            return ""
        case 8:
            return primaryFacility?.billingPhone ?? ""
        case 9:
            return primaryFacility?.pharmacyPhone ?? ""
        default:
            return ""
        }
    }
    
    
    func formatedTechSupportNo() -> String? {
        //input - 1-800-234-0482
        //output - (800) 234-0482
        
        if var phone = AppInfoManager.shared.appInfo?.techSupportNumber {

            if let range = phone.range(of: "1-", options: .regularExpression) {
                phone.replaceSubrange(range, with: "(")
            }
            if let range = phone.range(of: "-", options: .regularExpression) {
                phone.replaceSubrange(range, with: ") ")
            }
            return phone
        }
        return nil
    }
    
    func createPhoneNumbers() {
        for index in 1...ImportantNumbersStrings.numberTitles.count {
            let phNo = getPhoneNumberForService(index: index)
            
            if !phNo.isEmpty {
                var number = ImportantNumber()
                number.title = ImportantNumbersStrings.numberTitles[index-1]
                number.description = ImportantNumbersStrings.numberDescs[index-1]
                number.phNumber1 = "  " + phNo
                if index == 4 && requiredExtraNumberCell {
                    number.phNumber2 = "  " + (primaryFacility?.schedulingSecondaryPhone ?? "")
                }
                
                listOfNumbers.append(number)
            }
        }
        
        if listOfNumbers.count > 0 {
            DispatchQueue.main.async(execute: {
                self.tableView.reloadData()
            })
        }
    }
    
    //-Tableview delegates
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        if isOCCFac {
            return 3
        }
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if isOCCFac {
            if section == 0 {
                return 6
            }
            return 2
        }
        return listOfNumbers.count + 1
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if isOCCFac {
            if indexPath.section == 0 {
                if indexPath.row == 0 { //facName
                    let cell = tableView.dequeueReusableCell(withIdentifier: "facilityNameTableViewCell", for: indexPath) as! FacilityNameTableViewCell
                    
                    cell.facNameLabel.text = "CTCA LOCATIONS"
                    cell.buttonHtConstant.constant = 0.0
                    cell.facAddressButton.setAttributedTitle(NSAttributedString(string: ""), for: .normal)
                    return cell
                }
                if indexPath.row == 2 || indexPath.row == 1 {
                    let cell = tableView.dequeueReusableCell(withIdentifier: "numberTableViewCell", for: indexPath) as! NumberTableViewCell
                    
                    let item = sec1Numbers[indexPath.row-1]
                    cell.numberTypeLabel.text = item.0
                    cell.numberDescriptionLabel.text = item.1
                    cell.numberButton.setTitle("  " + item.2, for: .normal)
                    return cell
                } else {
                    let cell = tableView.dequeueReusableCell(withIdentifier: "occNumberTableViewCell", for: indexPath) as! OCCNumberTableViewCell
                    
                    let item = sec1Numbers[indexPath.row-1]
                    cell.titleLabel.text = item.0
                    cell.addressButton.setAttributedTitle(NSAttributedString(string: item.1), for: .normal)
                    cell.callButton.setTitle("  " + item.2, for: .normal)
                    cell.displayName = item.0
                    return cell
                }
            } else if indexPath.section == 1 {
                let cell = tableView.dequeueReusableCell(withIdentifier: "occNumberTableViewCell", for: indexPath) as! OCCNumberTableViewCell
                
                let item = sec2Numbers[indexPath.row]
                cell.titleLabel.text = item.0
                cell.buttonHtConstant.constant = 0.0
                cell.addressButton.setAttributedTitle(NSAttributedString(string: ""), for: .normal)
                cell.callButton.setTitle("  " + item.2, for: .normal)
                return cell
            } else {
                let cell = tableView.dequeueReusableCell(withIdentifier: "occNumberTableViewCell", for: indexPath) as! OCCNumberTableViewCell
                
                let item = sec3Numbers[indexPath.row]
                cell.titleLabel.text = item.0
                cell.buttonHtConstant.constant = 0.0
                cell.addressButton.setAttributedTitle(NSAttributedString(string: ""), for: .normal)
                cell.callButton.setTitle("  " + item.2, for: .normal)
                return cell
            }
        } else {
            if indexPath.row == 0 { //facName
                let cell = tableView.dequeueReusableCell(withIdentifier: "facilityNameTableViewCell", for: indexPath) as! FacilityNameTableViewCell
                cell.setUp(primFacility: primaryFacility)
                return cell
            } else {
                if indexPath.row == 4 && requiredExtraNumberCell {
                    if indexPath.row < listOfNumbers.count {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "numberExtraTableViewCell", for: indexPath) as! NumberExtraTableViewCell
                        cell.numberTypeLabel.text = listOfNumbers[indexPath.row-1].title
                        cell.numberDescriptionLabel.text = listOfNumbers[indexPath.row-1].description
                        cell.number1Button.setTitle(listOfNumbers[indexPath.row-1].phNumber1, for: .normal)
                        cell.number2Button.setTitle(listOfNumbers[indexPath.row-1].phNumber2, for: .normal)
                        return cell
                    }
                } else {
                    if indexPath.row <= listOfNumbers.count {
                        let cell = tableView.dequeueReusableCell(withIdentifier: "numberTableViewCell", for: indexPath) as! NumberTableViewCell
                        cell.numberTypeLabel.text = listOfNumbers[indexPath.row-1].title
                        cell.numberDescriptionLabel.text = listOfNumbers[indexPath.row-1].description
                        cell.numberButton.setTitle(listOfNumbers[indexPath.row-1].phNumber1, for: .normal)
                        return cell
                    }
                }
                return UITableViewCell()
            }
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if section == 0 {
            return .zero
        } else {
            return 44.0
        }
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if section == 0 {
            return UIView(frame: .zero)
        } else {
            var title = ""
            if section == 1 {
                title = sec2Header
            } else if section == 2 {
                title = sec3Header
            }
                
            let secHeaderView = UIView.init(frame: CGRect.init(x: 0, y: 0, width: tableView.frame.width, height: 44.0))
            secHeaderView.backgroundColor = MyCTCAColor.ctcaSectionBackground.color
            
            let label = UILabel()
            label.frame = CGRect.init(x: 20, y: 13, width: secHeaderView.frame.width-70, height: 18)
            label.backgroundColor = UIColor.clear
            label.numberOfLines = 1
            label.font = UIFont(name: "HelveticaNeue", size: 13)
            label.minimumScaleFactor = 0.75
            label.adjustsFontSizeToFitWidth = true
            label.textColor = MyCTCAColor.ctcaGrey75.color
            label.lineBreakMode = .byTruncatingTail
            label.text = title
            secHeaderView.addSubview(label)
            
            return secHeaderView
        }
    }
}
