//
//  MailViewController.swift
//  myctca
//
//  Created by Tomack, Barry on 11/10/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

protocol MailBoxDelegate : AnyObject {
    func didSendMail()
    func didArchiveMail()
    func refreshMailBox()
}

class MailViewController: CTCABaseViewController, MailBoxDelegate {

    @IBOutlet weak var mailboxSelector: UISegmentedControl!
    @IBOutlet weak var tableView: UITableView!
    
    let cellMailHeight: CGFloat = 65.0
    
    let MAIL_NEW_SEGUE: String = "MailNewSegue"
    let MAIL_DET_SEGUE: String = "MailDetailSegue"
    
    var selectedMail: Mail?
    
    lazy var refreshCtrl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refreshMailData(_:)), for: .valueChanged)
        refreshControl.tintColor = MyCTCAColor.ctcaSecondGreen.color
        refreshControl.attributedTitle = NSAttributedString(string: ActivityIndicatorMsgs.refreshMailText, attributes: [
            NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Italic", size: 14.0)!,
            NSAttributedString.Key.foregroundColor : MyCTCAColor.ctcaGreen.color
            ])
        
        return refreshControl
    }()
    
    var hasViewMailPermission: Bool = false
    var hasSendMailPermission: Bool = false
    
    let fetchMailErrorTitle: String = "Secure Mail Retrieval Error"
    let fetchMailErrorResponse: String = "There seems to be some kind of problem retrieving Care Team data. You can try again later or call the Care Manager directly."
    
    var sentPageLogged = false
    var archivedPageLogged = false
    
    var mailManager = MailManager.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Set Title
        self.title = "Secure Mail"
        
        // Navigation Bar
        self.navigationController?.navigationBar.setValue(true, forKey: "hidesShadow")
        
        // TableView
        self.tableView.tableFooterView = UIView()
        self.tableView.allowsSelection = true
        self.tableView.backgroundColor = UIColor.clear
        
        // Add Refresh Control (pull-to-refresh)
        if #available(iOS 10.0, *) {
            tableView.refreshControl = refreshCtrl
        } else {
            tableView.addSubview(refreshCtrl)
        }
        
        // Set tableview for empty data sets and
        // an empty footer to clear extra cells
        self.tableView.emptyDataSetSource = self
        self.tableView.emptyDataSetDelegate = self
        
        // Segmented Control
        self.mailboxSelector.selectedSegmentIndex = 0
        self.mailboxSelector.addTarget(self, action: #selector(mailSelectorChanged), for: .valueChanged)
        
        AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_MAIL_INBOX_VIEW)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        super.viewWillAppear(animated)
        
        // Check Permissions
        let currentUserId = AppSessionManager.shared.currentUser.currentUserId
        let userProfile = AppSessionManager.shared.currentUser.userProfile!
        
        hasViewMailPermission = userProfile.userCan(UserPermissionType.viewSecureMessages, viewerId: currentUserId)
        hasSendMailPermission = userProfile.userCan(UserPermissionType.sendSecureMessages, viewerId: currentUserId)
        
        if (hasSendMailPermission) {
            addBarButtonItemsToNav()
        }
        
        loadViewData()
    }
    
    override func loadViewData() {
        NetworkStatusManager.shared.registerForReload(view: self)

        if NetworkStatusManager.shared.isNetworkConnectionNotAvailable() {
            return
        }
                
        if (hasViewMailPermission == true) {
            
            var shouldFetchMail: Bool = false
            let mailbox = self.currentBox()
            if (mailbox == .inbox) {
                if (mailManager.newMails.count == 0) {
                    shouldFetchMail = true
                }
            }
            if (mailbox == .sent) {
                if (mailManager.sentMails.count == 0) {
                    shouldFetchMail = true
                }
            }
            if (mailbox == .archive) {
                if (mailManager.archivedMails.count == 0) {
                    shouldFetchMail = true
                }
            }
            
            // Fetch Datas
            if (shouldFetchMail == true) {
                fetchMailData(withActivityIndicator: true, forBox: nil)
            }
        }
    }
    
    func addBarButtonItemsToNav() {
        // Bar Buttons
        let composeNewMailBarButton = UIBarButtonItem(barButtonSystemItem: .compose, target: self, action: #selector(sendNewMail))
        
        self.navigationItem.rightBarButtonItems = [composeNewMailBarButton]
    }
    
    func fetchMailData(withActivityIndicator: Bool, forBox mailbox:MailBox?) {

        var mailBox = mailbox
        if (mailBox == nil) {
            mailBox = self.currentBox()
        }
        
        if (withActivityIndicator == true) {
            var activityText: String = ""
            if (mailBox == .inbox) {
                activityText = ActivityIndicatorMsgs.retriveInboxMailsText
            } else if(mailBox == .sent) {
                activityText = ActivityIndicatorMsgs.retriveSentMailsText
            } else if (mailBox == .archive) {
                activityText = ActivityIndicatorMsgs.retriveArchivedMailsText
            }
            // Show Activity Indicator
            showActivityIndicator(view: self.view, message:activityText)
        }
        
        mailManager.fetchMails(ofType: mailBox!) { [self]
            status in
            
            self.fadeOutActivityIndicator()
            
            if status == .FAILED {
                ErrorManager.shared.showServerError(error: self.mailManager.getLastServerError(), onView: self)
            } else {
                DispatchQueue.main.async {
                    self.refreshCtrl.endRefreshing()
                    self.tableView.reloadData()
                }
            }
        }
    }
    
    func dismissThis(action: UIAlertAction) {
        dismiss(animated: true, completion: nil)
    }

    @objc private func refreshMailData(_ sender: Any) {
        
        fetchMailData(withActivityIndicator: false, forBox:nil);
    }
    
    @objc private func mailSelectorChanged() {
        
        if (hasViewMailPermission == true) {
            let mailBox = self.currentBox()
            
            if (mailBox == .inbox){
                if(mailManager.newMails.count == 0) {
                    fetchMailData(withActivityIndicator: true, forBox:mailBox);
                } else {
                    DispatchQueue.main.async {
                        self.tableView.quickFadeOut(completion: {
                            (finished: Bool) -> Void in
                            self.tableView.reloadData()
                            self.tableView.quickFadeIn()
                        })
                    }
                }
            }
            if (mailBox == .sent){
                
                if !sentPageLogged {
                    sentPageLogged = true
                    AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_MAIL_SENT_VIEW)
                }
                
                if(mailManager.sentMails.count == 0) {
                    fetchMailData(withActivityIndicator: true, forBox: mailBox);
                } else {
                    DispatchQueue.main.async {
                        self.tableView.quickFadeOut(completion: {
                            (finished: Bool) -> Void in
                            self.tableView.reloadData()
                            self.tableView.quickFadeIn()
                        })
                    }
                }
            }
            if (mailBox == .archive){
                if !archivedPageLogged {
                    archivedPageLogged = true
                    AnalyticsManager.shared.trackPageView(CTCAAnalyticsConstants.PAGE_MAIL_ARCHIVED_VIEW)
                }
                
                if(mailManager.archivedMails.count == 0) {
                    fetchMailData(withActivityIndicator: true, forBox: mailBox);
                } else {
                    DispatchQueue.main.async {
                        self.tableView.quickFadeOut(completion: {
                            (finished: Bool) -> Void in
                            self.tableView.reloadData()
                            self.tableView.quickFadeIn()
                        })
                    }
                }
            }
        }
    }
    
    @objc func sendNewMail() {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_NEW_MAIL_TAP)
        performSegue(withIdentifier: MAIL_NEW_SEGUE, sender: self)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func currentBox() -> MailBox {
        var box: MailBox = MailBox.inbox
        if (self.mailboxSelector.selectedSegmentIndex == 1) {
            box = MailBox.sent
        } else if (self.mailboxSelector.selectedSegmentIndex == 2) {
            box = MailBox.archive
        }
        return box
    }
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == MAIL_NEW_SEGUE {
            
            let destinationNavigationController = segue.destination as! UINavigationController
            if let targetController = destinationNavigationController.topViewController as? MailSendNewViewController {
                targetController.mailBoxDelegate = self
            }
        }
        if segue.identifier == MAIL_DET_SEGUE {

            let destinationController = segue.destination as! MailDetailViewController
            print("prepare destination: \(destinationController)")
            destinationController.mail = self.selectedMail
            destinationController.currentBox = self.currentBox()
            destinationController.mailBoxDelegate = self
        }
    }
    
    func didSendMail() {
        // Update Sendbox
        fetchMailData(withActivityIndicator: true, forBox: .sent);
    }
    
    func didArchiveMail() {
        // Update Archivebox
        fetchMailData(withActivityIndicator: true, forBox: .archive);
    }
    
    func refreshMailBox() {
        print("refreshMailBox")
        fetchMailData(withActivityIndicator: false, forBox: nil)
        showActivityIndicator(view: self.view, message: ActivityIndicatorMsgs.updateSecureMails)
    }
    
    func scrollToFirstRow() {
        var mailBox: [Mail] = [Mail]();
        if (self.mailboxSelector.selectedSegmentIndex == 0) {
            mailBox = mailManager.newMails
        } else if (self.mailboxSelector.selectedSegmentIndex == 1) {
            mailBox = mailManager.sentMails
        }  else if (self.mailboxSelector.selectedSegmentIndex == 2) {
            mailBox = mailManager.archivedMails
        }
        if (mailBox.count > 0) {
            let indexPath = IndexPath(row: 0, section: 0)
            self.tableView.scrollToRow(at: indexPath, at: .top, animated: true)
        }
    }
}

// MARK: Table View Data Source

extension MailViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        
        // Currently (1/11/18) only 1 section per mailbox (n)ot divided by date
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        if (self.mailboxSelector.selectedSegmentIndex == 0) {
            return mailManager.newMails.count
        } else if (self.mailboxSelector.selectedSegmentIndex == 1) {
            return mailManager.sentMails.count
        } else if (self.mailboxSelector.selectedSegmentIndex == 2) {
            return mailManager.archivedMails.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        print("cellForRowAt: \(indexPath.row) ::: selectedIndex: \(self.mailboxSelector.selectedSegmentIndex)")
        if (self.mailboxSelector.selectedSegmentIndex == 0) {
            // Inbox
            if (mailManager.newMails.count > 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailTableViewCell") as! MailTableViewCell
                cell.prepareView(mail: mailManager.newMails[indexPath.row])
                return cell
            }
        } else if (self.mailboxSelector.selectedSegmentIndex == 1) {
            // Sent Mail
            if (mailManager.sentMails.count > 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailTableViewCell") as! MailTableViewCell
                cell.prepareView(mail: mailManager.sentMails[indexPath.row])
                return cell
            }
        } else if (self.mailboxSelector.selectedSegmentIndex == 2) {
            // Archive Mail
            if (mailManager.archivedMails.count > 0) {
                let cell = tableView.dequeueReusableCell(withIdentifier: "mailTableViewCell") as! MailTableViewCell
                cell.prepareView(mail: mailManager.archivedMails[indexPath.row])
                return cell
            }
        }
        let cell = UITableViewCell()
        return cell
    }
}

extension MailViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat
    {
        return cellMailHeight
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MAIL_DETAIL_TAP)
        
        tableView.deselectRow(at: indexPath, animated: true)
        
        let mailBox = self.currentBox()
        
        if (mailBox == .inbox) {
            self.selectedMail = mailManager.newMails[indexPath.row]
        } else if (mailBox == .sent) {
            self.selectedMail = mailManager.sentMails[indexPath.row]
        } else if (mailBox == .archive) {
            self.selectedMail = mailManager.archivedMails[indexPath.row]
        }
        print("selecteMail: \(String(describing: self.selectedMail?.from))")
        performSegue(withIdentifier: MAIL_DET_SEGUE, sender: nil)
    }
}

extension MailViewController: DZNEmptyDataSetSource, DZNEmptyDataSetDelegate {
    
    func description(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        
        var str = ""
        if (hasViewMailPermission == true) {
            str = CommonMsgConstants.noRecordsFoundMsg
            if (self.mailboxSelector.selectedSegmentIndex == 0) {
                str = "You have not sent any messages."
            } else if (self.mailboxSelector.selectedSegmentIndex == 1) {
                str = "You have not sent any messages."
            } else if (self.mailboxSelector.selectedSegmentIndex == 2) {
                str = "You have no archived messages."
            }
        } else {
            if AppSessionManager.shared.getUserType() == .CARE_GIVER {
                if (self.mailboxSelector.selectedSegmentIndex == 0) {
                    str = "Your inbox is empty."
                } else if (self.mailboxSelector.selectedSegmentIndex == 1) {
                    str = "You have not sent any messages."
                } else if (self.mailboxSelector.selectedSegmentIndex == 2) {
                    str = "You have no archived messages."
                }
            } else {
                if (self.mailboxSelector.selectedSegmentIndex == 0) {
                    str = "You don't have permissions to view mail in this inbox."
                } else if (self.mailboxSelector.selectedSegmentIndex == 1) {
                    str = "You don't have permissions to view mail in this sent box."
                } else if (self.mailboxSelector.selectedSegmentIndex == 2) {
                    str = "You don't have permissions to view mail in this archive box."
                }
            }
        }
        
        let style = NSMutableParagraphStyle()
        style.alignment = NSTextAlignment.center
        
        let attrs = [NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Medium", size: 15.0)!,
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color,
                     NSAttributedString.Key.paragraphStyle: style ]
        
        return NSAttributedString(string: str, attributes: attrs)
    }
    
    func image(forEmptyDataSet scrollView: UIScrollView) -> UIImage? {
        if UIDevice.current.orientation.isLandscape {
            return UIImage(named: "myctca_logo_128")
        } else {
            return UIImage(named: "myctca_logo_256")
        }
    }
    
    override func didRotate(from fromInterfaceOrientation: UIInterfaceOrientation) {
        if (self.mailboxSelector.selectedSegmentIndex == 0) {
            if mailManager.newMails.count == 0 {
                self.tableView.reloadData()
            }
        } else if (self.mailboxSelector.selectedSegmentIndex == 1) {
            if mailManager.sentMails.count == 0 {
                self.tableView.reloadData()
            }
        } else if (self.mailboxSelector.selectedSegmentIndex == 2) {
            if mailManager.archivedMails.count == 0 {
                self.tableView.reloadData()
            }
        }
    }
    
    func buttonTitle(forEmptyDataSet scrollView: UIScrollView, for state: UIControl.State) -> NSAttributedString? {
        if GenericHelper.shared.hasPermissionTo(feature: .sendSecureMessages) &&
            (self.mailboxSelector.selectedSegmentIndex == 0 ||
            self.mailboxSelector.selectedSegmentIndex == 1 ) {
            let style = NSMutableParagraphStyle()
            style.alignment = NSTextAlignment.center
            
            let attrs = [NSAttributedString.Key.font : UIFont(name: "HelveticaNeue-Medium", size: 15.0)!,
                         NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGreen.color,
                         NSAttributedString.Key.underlineStyle: NSUnderlineStyle.single.rawValue,
                         NSAttributedString.Key.paragraphStyle: style ] as [NSAttributedString.Key : Any]
            return NSAttributedString(string: "Write first message", attributes: attrs)
        }
        return NSAttributedString(string: "", attributes: nil)
    }
    
    func emptyDataSet(_ scrollView: UIScrollView, didTap button: UIButton) {
        if GenericHelper.shared.hasPermissionTo(feature: .sendSecureMessages) {
            print("Write first message")
            performSegue(withIdentifier: MAIL_NEW_SEGUE, sender: self)
        }
    }
}
