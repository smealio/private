//
//  CTCATableView.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 28/10/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class CTCATableView: UITableView {
    func setEmptyView(info:EmptyDataInfo) {
        let emptyView = UIView(frame: CGRect(x: self.center.x, y: self.center.y, width: self.bounds.size.width, height: self.bounds.size.height))
        
        let noDataView = CTCANoDataView()
        noDataView.setup(info:info)
        
        noDataView.translatesAutoresizingMaskIntoConstraints = false
        
        emptyView.addSubview(noDataView)
        
        noDataView.topAnchor.constraint(equalTo: emptyView.topAnchor, constant: 0.0).isActive = true
        noDataView.bottomAnchor.constraint(equalTo: emptyView.bottomAnchor, constant: 0.0).isActive = true
        noDataView.leadingAnchor.constraint(equalTo: emptyView.leadingAnchor, constant: 0.0).isActive = true
        noDataView.trailingAnchor.constraint(equalTo: emptyView.trailingAnchor, constant: 0.0).isActive = true
        
        emptyView.backgroundColor = .blue
        self.backgroundView = emptyView
        self.separatorStyle = .none
    }
    
    func restoreBGView() {
        if self.backgroundView != nil {
            self.backgroundView = nil
            self.separatorStyle = .singleLine
        }
    }
}
