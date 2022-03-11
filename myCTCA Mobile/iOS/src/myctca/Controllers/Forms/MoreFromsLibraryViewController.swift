//
//  MoreFromsLibraryViewController.swift
//  myctca
//
//  Created by Manjunath K on 7/29/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

class MoreFromsLibraryViewController: CTCABaseViewController {

    @IBOutlet weak var tableView: UITableView!
    
    let FORMSLIB_ROI_SEGUE: String = "FormsLibROISegue"
    let FORMSLIB_ANNC_SEGUE: String = "FormsLibANNCSegue"
    
    let ROI_TITLE = "Release of Information (ROI)"
    let ANNC_TITLE = "Advance Notice of Non-Coverage (ANNC)"
    
    var displayList = [String]()
    var fromUniversalLink = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        //Register subtitle Cell
        let tableViewLoadingCellNib = UINib(nibName: "CTCASubTitleTableViewCell", bundle: nil)
        self.tableView.register(tableViewLoadingCellNib, forCellReuseIdentifier: "subTitleTableViewCell")
        
        prepareDiaplyList()
    }
    
    func prepareDiaplyList(){
        displayList.append(ROI_TITLE)
        displayList.append(ANNC_TITLE)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if fromUniversalLink {
            showANNCBaseVC()
            fromUniversalLink = false
        }
    }
    
    func showANNCBaseVC() {
        performSegue(withIdentifier: FORMSLIB_ANNC_SEGUE, sender: self)
    }
    
    func showROIBaseVC() {
        performSegue(withIdentifier: FORMSLIB_ROI_SEGUE, sender: self)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == FORMSLIB_ANNC_SEGUE {
            if let controller = segue.destination as? ANNCBaseViewController {
                controller.fromUniversalLink = fromUniversalLink
                fromUniversalLink = false
            }
        }
    }
}

extension MoreFromsLibraryViewController : UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return displayList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "subTitleTableViewCell") as! CTCASubTitleTableViewCell
        
        cell.setSubTitle(title: displayList[indexPath.row])
        
        return cell
    }
}

extension MoreFromsLibraryViewController: UITableViewDelegate {

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        if indexPath.row == 0 {
            showROIBaseVC()
        } else {
            showANNCBaseVC()
        }
    }
    
}
