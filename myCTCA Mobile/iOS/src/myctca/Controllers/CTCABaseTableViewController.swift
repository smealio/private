//
//  CTCABaseTableViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 04/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCABaseTableViewController: UITableViewController, CTCAViewControllerProtocol {

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if #available(iOS 15.0, *) {
            let appearance = UINavigationBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = MyCTCAColor.ctcaNavBarColor.color
            if let navController = self.navigationController {
                navController.navigationBar.standardAppearance = appearance;
                navController.navigationBar.scrollEdgeAppearance = navController.navigationBar.standardAppearance
            }
        }
    }
}
