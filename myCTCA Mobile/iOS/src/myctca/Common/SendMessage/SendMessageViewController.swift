//
//  SendMessageViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 11/27/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

class SendMessageViewController: UIViewController {

    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func dismissSendMessage(_ sender: Any) {
        print("SendMessageViewController dismissSendMessage")
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func sendMessage(_ sender: Any) {
        print("SendMessageViewController sendMessage")
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
