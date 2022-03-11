//
//  ApptContactsViewController.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 16/09/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class ApptContactsViewController: UIViewController {
    
    @IBOutlet var callMeView:ContactPrefView!
    @IBOutlet var emailMeView:ContactPrefView!
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setup()
        
        //Register subtitle Cell
        let tableViewLoadingCellNib = UINib(nibName: "CTCACommentsTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCellNib, forCellReuseIdentifier: "CTCACommentsTableViewCell")
        
        let tableViewLoadingCell2Nib = UINib(nibName: "CTCAContactsTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCell2Nib, forCellReuseIdentifier: "CTCAContactsTableViewCell")
    }
    
    func setup() {
        callMeView.configCell(title: "Call me", subTitle: "(232) 232-2323")
        //emailMeView.configCell(title: "Email me", subTitle: "xyz@abc.com")
    }
}

extension ApptContactsViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return UITableViewCell()
    }
    
    
}
