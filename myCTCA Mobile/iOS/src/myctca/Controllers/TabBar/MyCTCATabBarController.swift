//
//  MyCTCATabBarController.swift
//  myctca
//
//  Created by Tomack, Barry on 3/13/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MyCTCATabBarController: UITabBarController, UITabBarControllerDelegate {
    
    var currentNavController: UINavigationController?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        delegate = self
        self.currentNavController = selectedViewController as? UINavigationController
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.currentNavController = selectedViewController as? UINavigationController
        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // UITabBarControllerDelegate
    func tabBarController(_ tabBarController: UITabBarController, didSelect viewController: UIViewController) {

        let fromNavController: UINavigationController = currentNavController!
        let toNavController: UINavigationController = viewController as! UINavigationController
        
        if (fromNavController != toNavController) {
            let fromView = fromNavController.visibleViewController?.view
            let toView = toNavController.visibleViewController?.view
            
            UIView.transition(from: fromView!, to: toView!, duration: 0.3, options: [.transitionCrossDissolve], completion: nil)
        }
        
        self.currentNavController = toNavController
    }
    
    func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
        
        let fromNavController: UINavigationController = currentNavController!
        let toNavController: UINavigationController = viewController as! UINavigationController
        
        var hasPermissions = false
        
        if let destinationVC = toNavController.viewControllers.first {
            if destinationVC.isKind(of: ApptViewController.self) {
                hasPermissions = GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.viewApppointments)
            } else if destinationVC.isKind(of: MailViewController.self) {
                hasPermissions = GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.viewSecureMessages)
            } else if destinationVC.isKind(of: LabsViewController.self) {
                hasPermissions = GenericHelper.shared.hasPermissionTo(feature: UserPermissionType.viewLabResults)
            } else {
                hasPermissions = true
            }
        }
        
        if !hasPermissions {
            GenericHelper.shared.showNoAccessMessage(view: fromNavController)
        }
        
        return hasPermissions
    }
    
    func resetMainVCs() {
        if let navViewControllers = self.viewControllers {
            for navVC in navViewControllers {
                if let toNavController = navVC as? UINavigationController {
                    let viewControllers = toNavController.viewControllers
                    for vc in viewControllers {
                        if vc.isKind(of: MoreViewController.self) {
                            let moreVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MoreViewController") as! MoreViewController
                            toNavController.setViewControllers([moreVC], animated: true)
                            break
                        }
                    }
                }
            }
        }
    }
}
