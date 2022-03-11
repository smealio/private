//
//  NavigationManager.swift
//  myctca
//
//  Created by Manjunath K on 10/23/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

final class NavigationManager {
    static let shared = NavigationManager()
    
    private init() {
    }
    
    func getViewForUrl(url:String) -> String? {
        switch url {
        case "/app/forms-library":
            return MyCTCAConstants.UniverslaLinkPages.anncForm
        case "/mobile-convert-success":
            return MyCTCAConstants.UniverslaLinkPages.loginPage
        default:
            return ""
        }
    }
    
    func gotoPage(withName:String) {
        switch withName {
        case MyCTCAConstants.UniverslaLinkPages.anncForm :
            gotoANNCForm()
        default:
            print("never execute this")
        }
    }
    
    private func gotoANNCForm() {
        //if logged in
        if AppSessionManager.shared.getSessionState() == .ACTIVE {
            DispatchQueue.main.async(execute: {
                let appDelegate = UIApplication.shared.delegate as! AppDelegate
                if let rootNavCtrl = appDelegate.window?.rootViewController as? UINavigationController {
                    print(String(describing: rootNavCtrl.viewControllers))
                    
                    let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
                    
                    if let newViewController = storyBoard.instantiateViewController(withIdentifier: "MyCTCATabBarController") as? MyCTCATabBarController {
                        newViewController.selectedIndex = 4
                        appDelegate.window?.rootViewController = newViewController
                        
                        if let moreNVC = newViewController.viewControllers?[4] as? UINavigationController {
                            if let more = moreNVC.viewControllers[0] as? MoreViewController {
                                more.doFormsLib(isFromUL: true)
                            }
                        }
                    }
                }
            })
        } else {
            
        }
    }
}
