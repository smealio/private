//
//  NetworkStatusManager.swift
//  myctca
//
//  Created by Manjunath K on 8/27/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation


class NetworkStatusManager {
    
    static let shared = NetworkStatusManager()
    weak var currentViewToReload:UIViewController?
    let reachability = Reachability()!
    
    func startObserver() {
        NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(note:)), name: ReachabilityChangedNotification, object: reachability)
        do{
            try reachability.startNotifier()
        }catch{
            print("could not start reachability notifier")
        }
    }
    
    @objc func reachabilityChanged(note: Notification) {
        
        let reachability = note.object as! Reachability
        
        switch reachability.currentReachabilityStatus {
        case .reachableViaWiFi:
            reloadView()
        case .reachableViaWWAN:
            reloadView()
        case .notReachable:
            print("Network not reachable")
        }
    }
    
    func registerForReload(view:UIViewController) {
        currentViewToReload = view
    }
    
    func isNetworkConnectionNotAvailable() -> Bool {
        //This is needed
        let reachability = Reachability()!
        if reachability.currentReachabilityStatus == .notReachable {
            
            DispatchQueue.main.async(execute: {
                let banner = Banner(title: CommonMsgConstants.noInternetTitle,
                                    subtitle: CommonMsgConstants.noInternetsubtitle,
                                    image: UIImage(named: "warning_triangle")!.withRenderingMode(.alwaysTemplate),
                                    backgroundColor: UIColor.red.withAlphaComponent(0.75))
                banner.dismissesOnTap = true
                banner.position = .top
                banner.springiness = .none
                banner.show(duration: 3.0)
            })
            return true
        }
        return false
    }
    
    fileprivate func reloadView() {
        
        if let view = currentViewToReload {
            DispatchQueue.main.async(execute: {
                let banner = Banner(title: CommonMsgConstants.activeInternetTitle,
                                    subtitle: CommonMsgConstants.activeInternetsubtitle,
                                    image: UIImage(named: "iInfo")!.withRenderingMode(.alwaysTemplate),
                                    backgroundColor: MyCTCAColor.ctcaGreen.color.withAlphaComponent(0.75))
                banner.dismissesOnTap = true
                banner.position = .top
                banner.springiness = .none
                banner.show(duration: 2.0)
                
                view.loadViewData()
            })
            currentViewToReload = nil
        }
        
        
    }
}
