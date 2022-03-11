//
//  THTestViewController.swift
//  myctca
//
//  Created by Manjunath K on 5/6/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class THTestViewController: UIViewController {

    var telehealthDetail:TelehealthDetail?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func joinButtonTapped(_ sender: Any) {
        let url = "https://teams.microsoft.com/l/meetup-join/19%3ameeting_ODhmZmYxZjgtNzU4Zi00NmRhLWE3NGUtOGQ0NjU1YzJkMjBh%40thread.v2/0?context=%7b%22Tid%22%3a%229b45049c-b0db-452f-9f33-75b120b51c52%22%2c%22Oid%22%3a%22f4515aa5-bd0c-4015-ab08-efd101fa4d0b%22%7d"
        
        self.joinTelehealthMeeting(url: url)
    }
    
    func joinTelehealthMeeting(url: String) {
                        
        AppointmentsManager.shared.fetchTelehealthAccessToken() {
            token, status in
                            
            if status == .SUCCESS {
                self.showTelehealthView(token: token!)
            } else {
                //fallback error
                ErrorManager.shared.showDefaultTelehealthError(onView:self, appointment: nil)
            }
        }
    }
    
    func showTelehealthView(token:String) {
//        DispatchQueue.main.async {
//            let telehealthVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "TelehealthViewController") as! TelehealthViewController
//            telehealthVC.accessToken = AppointmentsManager.shared.telehealthAccessToken
//            telehealthVC.meetingUrl = AppointmentsManager.shared.meetingUrl
//            self.show(telehealthVC, sender: nil)
//        }
//
        DispatchQueue.main.async {
            let preTelehealthCallVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "PreCallViewController") as! PreCallViewController
            
            var telehealthDetail = TelehealthDetail(withValue: Appointment())
            telehealthDetail.accessToken = token
            
            preTelehealthCallVC.telehealthDetail = telehealthDetail
            
            self.show(preTelehealthCallVC, sender: nil)
        }
    }
}
