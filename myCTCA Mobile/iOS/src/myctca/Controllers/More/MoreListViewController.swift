//
//  MoreListViewController.swift
//  myctca
//
//  Created by Manjunath K on 7/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class MoreListViewController: CTCABaseViewController {

    @IBOutlet weak var tableView: UITableView!
    var doShowAIOnLaunch = false
    
    var itemsList = [MyCTCAListItem]()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        //Register subtitle Cell
        let tableViewLoadingCellNib = UINib(nibName: "CTCASubTitleTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCellNib, forCellReuseIdentifier: "subTitleTableViewCell")
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if doShowAIOnLaunch {
            self.showActivityIndicator(view: view, message: "Loading...")
        }
    }
}

extension MoreListViewController : UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return itemsList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "subTitleTableViewCell") as! CTCASubTitleTableViewCell
        
        cell.setSubTitle(title: itemsList[indexPath.row].title!)
        
        return cell
    }
}

extension MoreListViewController: UITableViewDelegate {

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        itemsList[indexPath.row].action()
    }
    
}
