//
//  MailDetailViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 1/12/18.
//  Copyright © 2018 CTCA. All rights reserved.
//

import UIKit

class MailDetailViewController: UIViewController, CTCAViewControllerProtocol {

    var mail: Mail?
    var currentBox: MailBox = MailBox.inbox
    
    @IBOutlet weak var replyBarButton: UIBarButtonItem!
    @IBOutlet weak var archiveBarButton: UIBarButtonItem!
    
    let cellDefaultHeight: CGFloat = 90.0
    
    let MAIL_REPLY_SEGUE: String = "MailReplySegue"
    
    @IBOutlet weak var textView: UITextView!
    
    weak var mailBoxDelegate: MailBoxDelegate?
    
    var markedAsRead:Bool = false
    var archived:Bool = false
    
    let successfulArchiveResponse: String = "Mail has been archived."
    let mailManager = MailManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Title
        self.title = "Mail Detail"
        
        if (mail != nil) {
            self.textView.attributedText = buildMessageDetail()
        }
        if (currentBox == MailBox.archive) {
            archiveBarButton.isEnabled = false
        } else {
            archiveBarButton.isEnabled = true
        }
        textView.isEditable = false
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_MAIL_DETAIL_VIEW)
        
        if AppSessionManager.shared.getUserType() == .PROXY {
            replyBarButton.isEnabled = false
            archiveBarButton.isEnabled = false
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if !GenericHelper.shared.hasPermissionTo(feature: .sendSecureMessages) {
            self.navigationItem.rightBarButtonItems?.remove(at: 1)
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        textView.scrollRangeToVisible(NSMakeRange(0, 0))
        if (mail != nil) {
            if (mail!.isRead == false) {
                markAsRead()
            }
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if (markedAsRead == true || archived == true) {
            if (mailBoxDelegate != nil) {
                mailBoxDelegate!.refreshMailBox()
            }
        }
    }
    
    func buildMessageDetail() -> NSAttributedString {

        let messageText = NSMutableAttributedString()
            .bold("Date: ", fontSize: 15.0)
            .normal("\(mail!.getFullDateTimeString()) \n\n", fontSize: 15.0)
            .bold("From: ", fontSize: 15.0)
            .normal("\(mail!.from) \n\n", fontSize: 15.0)
            .bold("To: ", fontSize: 15.0)
            .normal("\(mail!.getCommaSeparatedSelectedTo()) \n\n", fontSize: 15.0)
            .bold("Subject: ", fontSize: 15.0)
            .normal("\(mail!.subject) \n\n", fontSize: 15.0)
            .normal(mail!.comments, fontSize: 15.0)

        return messageText;
    }
    
    func markAsRead() {
        mailManager.setMailRead(msgID: self.mail!.mailId) { [self]
            status in
            if (status == .SUCCESS) {
                self.markedAsRead = true;
            } else {
                ErrorManager.shared.showServerError(error: self.mailManager.getLastServerError(), onView: self)
            }
        }
    }
    
    @IBAction func replyToEmail(_ sender: Any) {
        print("REPLY")
        performSegue(withIdentifier: MAIL_REPLY_SEGUE, sender: nil)
    }
    
    @IBAction func archiveMail(_ sender: Any) {
        guard let msgID = mail?.mailId else {
            return
        }
        
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MAIL_ARCHIVE_TAP)
        
        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
        
        showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.archiveMailText)
        
        mailManager.archiveMail(msgID: msgID) {
            status in
            
            if status == .SUCCESS {
                self.archived = true;
                GenericHelper().showBanner(title: self.successfulArchiveResponse)
                
                if (self.mailBoxDelegate != nil) {
                    self.mailBoxDelegate!.didArchiveMail()
                }
            } else {
                ErrorManager.shared.showServerError(error: self.mailManager.getLastServerError(), onView: self)
            }
            
            self.fadeOutActivityIndicator(completion: nil)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == MAIL_REPLY_SEGUE {
            let destinationNavigationController = segue.destination as! UINavigationController
            if let targetController = destinationNavigationController.topViewController as? MailSendNewViewController {
                targetController.respondingMail = self.mail
                targetController.mailBoxDelegate = self
            }
        }
    }
}

extension MailDetailViewController: MailBoxDelegate {
    func didSendMail() {
        if (self.mailBoxDelegate != nil) {
            self.mailBoxDelegate!.didSendMail()
        }
    }
    func didArchiveMail() {
        if (self.mailBoxDelegate != nil) {
            self.mailBoxDelegate!.didArchiveMail()
        }
    }
    func refreshMailBox() {
        if (mailBoxDelegate != nil) {
            mailBoxDelegate!.refreshMailBox()
        }
    }
}
