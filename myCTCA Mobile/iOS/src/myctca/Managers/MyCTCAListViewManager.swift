//
//  MyCTCAListViewManager.swift
//  myctca
//
//  Created by Manjunath K on 1/19/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import Foundation

class MyCTCAListViewManager: BaseManager {
        
    func showMyResources(parentVC:UIViewController) {

        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "My Resources"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "User Guide"
        item1.action = {
            self.showMyResourcePDF(router: .downloadUserGuide, filename: MyCTCAConstants.FileNameConstants.UserGuidePDFName, parentVC: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "Frequently Asked Questions (FAQs)"
        item2.action = {
            if let url = MoreAPIRouter.openFAQ.asUrl() {
                GenericHelper.shared.launchSafariViewController(withUrl: url, forView: listViewController)
            }
        }
        
        var item3 = MyCTCAListItem(action: {})
        item3.title = "Nutrition Education"
        item3.action = {
            self.showNutritionEducation(parentVC: listViewController)
        }
        
        var item4 = MyCTCAListItem(action: {})
        item4.title = "External Links"
        item4.action = {
            self.showExternalLinks(parentVC: listViewController)
        }

        listViewController.itemsList = [item1, item2, item3, item4]

        parentVC.show(listViewController, sender: nil)
    }
    
    func showNutritionEducation(parentVC:UIViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "Nutrition Education"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "Your Health, Your Future document"
        
        item1.action = {
            self.showMyResourcePDF(router: .downloadYourHealth, filename: MyCTCAConstants.FileNameConstants.YourHealthPDFName, parentVC: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "Nutrition Basics"
        
        item2.action = {
            self.showMyResourcePDF(router: .downloadNutritionBasics, filename: MyCTCAConstants.FileNameConstants.NutritionBasicsPDFName, parentVC: listViewController)
        }
        
        var item3 = MyCTCAListItem(action: {})
        item3.title = "Enhancing Your Menu"
        
        item3.action = {
            self.showMyResourcePDF(router: .downloadYourMenu, filename: MyCTCAConstants.FileNameConstants.YourMenuPDFName, parentVC: listViewController)
        }
        
        var item4 = MyCTCAListItem(action: {})
        item4.title = "Symptom Management"
        
        item4.action = {
            self.showMyResourcePDF(router: .downloadSymtmMagmt, filename: MyCTCAConstants.FileNameConstants.SymptomManagementPDFName, parentVC: listViewController)
        }
        
        var item5 = MyCTCAListItem(action: {})
        item5.title = "When Eating is a Challenge"
        
        item5.action = {
            self.showMyResourcePDF(router: .downloadEatingChallenge, filename: MyCTCAConstants.FileNameConstants.EatingChallengePDFName, parentVC: listViewController)
        }
        
        listViewController.itemsList = [item1, item2, item3, item4, item5]
        parentVC.show(listViewController, sender: nil)
    }
    
    func showMyResourcePDF(router:MoreAPIRouter , filename:String, parentVC:UIViewController) {
        GenericHelper.shared.showActivityIndicator(message: "Downloading...")

        self.hostViewController = parentVC
        self.downloadFileName = filename
        self.downloadReports(router: router, url: nil) {
            status in
            GenericHelper.shared.dismissActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.serverError!, onView: parentVC)
            }
        }
    }
    
    func showMyResourcePDFForCustomURL(urlPath:String , filename:String, parentVC:UIViewController) {
        GenericHelper.shared.showActivityIndicator(message: "Downloading...")

        self.hostViewController = parentVC
        self.downloadFileName = filename
        self.downloadReports(router: nil, url: urlPath) {
            status in
            GenericHelper.shared.dismissActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.serverError!, onView: parentVC)
            }
        }
    }
    
    func showListView(withTitle:String, withItems:[ExternalLinkItem], onView:MoreListViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = withTitle
                
        for item in withItems {
            var listItem = MyCTCAListItem(action: {})
            listItem.title = item.title
            
            if item.isDownload {
                listItem.action = {
                    self.showMyResourcePDFForCustomURL(urlPath: item.url, filename: item.title, parentVC:listViewController)
                }
            } else {
                listItem.action = {
                    GenericHelper.shared.launchSafariViewController(withPath: item.url, forView: listViewController)
                }
            }
        
            listViewController.itemsList.append(listItem)
        }
        
        onView.show(listViewController, sender: nil)
    }
    
    func showExternalLinks(parentVC:MoreListViewController) {
        
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "External Links"
                
        MyResourceManager.shared.fetchExternalLinks() {
            status in
            
            listViewController.fadeOutActivityIndicator()
            listViewController.doShowAIOnLaunch = false
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error:  MyResourceManager.shared.getLastServerError(), onView: parentVC)
            } else {
                if let linksList = MyResourceManager.shared.externalLinksList {
                    for item in linksList {
                        var listItem = MyCTCAListItem(action: {})
                        listItem.title = item.title
                        
                        //if not childrens
                        if let count = item.items?.count, count == 0 {
                            if item.isDownload {
                                self.showMyResourcePDFForCustomURL(urlPath: item.url, filename: item.title, parentVC:listViewController)
                            } else {
                                listItem.action = {
                                    GenericHelper.shared.launchSafariViewController(withPath: item.url, forView: listViewController)
                                }
                            }
                        } else {
                            if let list = item.items {
                                listItem.action = {
                                    self.showListView(withTitle: item.title, withItems: list, onView: listViewController)
                                }
                            }
                        }
                        
                        listViewController.itemsList.append(listItem)
                    }
                    
                    listViewController.tableView.reloadData()
                }
            }
        }
        
        listViewController.doShowAIOnLaunch = true
        parentVC.show(listViewController, sender: nil)
    }
    
    func showCoronavirusInformation(parentVC:UIViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "Coronavirus Information"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "COVID-19"
        item1.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openCovid19.path, forView: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "Patient Information"
        item2.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openPatientInfo.path, forView: listViewController)
        }
        
        var item3 = MyCTCAListItem(action: {})
        item3.title = "FAQ(Frequently Asked Questions)"
        item3.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openExtFAQ.path, forView: listViewController)
        }
        
        var item4 = MyCTCAListItem(action: {})
        item4.title = "Contacts"
        item4.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openContacts.path, forView: listViewController)
        }
        
        var item5 = MyCTCAListItem(action: {})
        item5.title = "Together"
        item5.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openTogether.path, forView: listViewController)
        }
        
        listViewController.itemsList = [item1, item2, item3, item4, item5]
        parentVC.show(listViewController, sender: nil)
    }
    
    func showCancerCenterCom(parentVC:UIViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "CancerCenter.com"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "Clinician Bios"
        item1.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openClinicianBios.path, forView: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "Sign Up For Newsletter"
        item2.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openNewsletter.path, forView: listViewController)
        }
        
        var item3 = MyCTCAListItem(action: {})
        item3.title = "Find Info About My Cancer"
        item3.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openFindInfo.path, forView: listViewController)
        }
        
        var item4 = MyCTCAListItem(action: {})
        item4.title = "Clinical Trials"
        item4.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openClinicalTrials.path, forView: listViewController)
        }
        
        var item5 = MyCTCAListItem(action: {})
        item5.title = "Blogs"
        item5.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openBlogs.path, forView: listViewController)
        }
        
        listViewController.itemsList = [item1, item2, item3, item4, item5]
        parentVC.show(listViewController, sender: nil)
    }
    
    func showCTCANews(parentVC:UIViewController) {
        GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openCTCANews.path, forView: parentVC)
    }
    
    func showResources(parentVC:UIViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "Resources"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "Your Hospital"
        item1.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openHospital.path, forView: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "Your Hospital Activities"
        item2.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openHospitalActvty.path, forView: listViewController)
        }
        
        var item3 = MyCTCAListItem(action: {})
        item3.title = "Managing Side Effects"
        item3.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openSideEffects.path, forView: listViewController)
        }
        
        var item4 = MyCTCAListItem(action: {})
        item4.title = "Recipes"
        item4.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openRecipes.path, forView: listViewController)
        }
        
        listViewController.itemsList = [item1, item2, item3, item4]
        parentVC.show(listViewController, sender: nil)

    }
    
    func showReferAFriend(parentVC:UIViewController) {
        GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openReferAFriend.path, forView: parentVC)
    }
    
    func showCancerFighters(parentVC:UIViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "Cancer Fighters"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "How We Help You"
        item1.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openVHelpU.path, forView: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "Why Become A Member"
        item2.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openMember.path, forView: listViewController)
        }
        
        var item3 = MyCTCAListItem(action: {})
        item3.title = "Join Now"
        item3.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openJoinNow.path, forView: listViewController)
        }
        
        listViewController.itemsList = [item1, item2, item3]
        parentVC.show(listViewController, sender: nil)
    }
    
    func showCancerResources(parentVC:UIViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "Cancer Fighters"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "American Cancer Society"
        item1.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openAmericanCancerSociety.path, forView: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "American Lung Association"
        item2.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openAmericanLungAssociation.path, forView: listViewController)
        }
        
        var item3 = MyCTCAListItem(action: {})
        item3.title = "Cancer Support Communities"
        item3.action = {
            GenericHelper.shared.launchSafariViewController(withPath: MoreAPIRouter.openCancerSupportCommunities.path, forView: listViewController)
        }
        
        listViewController.itemsList = [item1, item2, item3]
        parentVC.show(listViewController, sender: nil)
    }
    
    func showPatientReported(parentVC:UIViewController) {
        let listViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreListViewController") as! MoreListViewController
        listViewController.title = "Patient Reported"
        
        var item1 = MyCTCAListItem(action: {})
        item1.title = "SIT Document"
        item1.action = {
            self.showMyResourcePDF(router: .downloadSITDoc, filename: MyCTCAConstants.FileNameConstants.SITDocPDFName, parentVC: listViewController)
        }
        
        var item2 = MyCTCAListItem(action: {})
        item2.title = "Symptom Inventory"
        item2.action = {
            self.showSymptomInventory(parentVC: listViewController)
        }
        
        listViewController.itemsList = [item1, item2]

        parentVC.show(listViewController, sender: nil)
    }
    
    func showSymptomInventory(parentVC:UIViewController) {
        let siViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SymptomInventoryViewController") as! SymptomInventoryViewController
        siViewController.title = "Symptom Inventory"
        parentVC.show(siViewController, sender: nil)
    }
    
}
