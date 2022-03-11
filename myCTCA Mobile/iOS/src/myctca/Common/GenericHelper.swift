//
//  GenericHelper.swift
//  myctca
//
//  Created by Manjunath K on 8/4/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import CoreTelephony
import SafariServices
import MapKit

class GenericHelper {
    
    static let shared = GenericHelper()
    
    func getTopVC() -> UIViewController? {
        let keyWindow = UIApplication.shared.windows.filter {$0.isKeyWindow}.first
        
        if var topController = keyWindow?.rootViewController {
            while let presentedViewController = topController.presentedViewController {
                topController = presentedViewController
            }
            return topController
        }
        
        return nil
    }
    
    func showAlert(info:myCTCAAlert) {
        DispatchQueue.main.async(execute: {
            let keyWindow = UIApplication.shared.windows.filter {$0.isKeyWindow}.first

            if var topController = keyWindow?.rootViewController {
                while let presentedViewController = topController.presentedViewController {
                    topController = presentedViewController
                }
                let alertVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MyCTCAAlertViewController") as! MyCTCAAlertViewController
                alertVC.modalPresentationStyle = .overCurrentContext
                topController.present(alertVC, animated: true, completion: nil)
                if alertVC.isViewLoaded {
                    alertVC.setup(alertInfo: info)
                } else {
                    print("GenericHelper.showAlert failed")
                }
            }
        })
    }
    
    func showAlertLarge(info:myCTCAAlert) {
        DispatchQueue.main.async(execute: {
            let keyWindow = UIApplication.shared.windows.filter {$0.isKeyWindow}.first

            if var topController = keyWindow?.rootViewController {
                while let presentedViewController = topController.presentedViewController {
                    topController = presentedViewController
                }
                let alertVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "MyCTCAAlertLargeViewController") as! MyCTCAAlertLargeViewController
                alertVC.modalPresentationStyle = .overCurrentContext
                topController.present(alertVC, animated: true, completion: nil)
                if alertVC.isViewLoaded {
                    alertVC.setup(alertInfo: info)
                } else {
                    print("GenericHelper.showAlert failed")
                }
            }
        })
    }
        
    func showAlert(withtitle:String, andMessage :String, onView:UIViewController,
                   okaction: UIAlertAction? = nil, otheraction:UIAlertAction? = nil) {
        let alert = ctcaInfoAlert(title:withtitle,
                                  message: andMessage,
                                  okaction: okaction,
                                  otheraction:otheraction)
        
        alert.preferredAction = okaction
        DispatchQueue.main.async {
            alert.view.tintColor = MyCTCAColor.ctcaGreen.color
            onView.present(alert,
                           animated: true,
                           completion: nil)
        }
    }
    
    func showNewApptAlert(alertInfo:MyCTCANewAlertInfo?) {
        if let topController = getTopVC(), let info = alertInfo {
            DispatchQueue.main.async {
                let alertVC = UIStoryboard(name: "Secondary", bundle: nil).instantiateViewController(withIdentifier: "MyCTCANewAlertViewController") as! MyCTCANewAlertViewController
                alertVC.modalPresentationStyle = .overCurrentContext
                alertVC.alertInfo = info
                topController.present(alertVC, animated: true, completion: nil)
            }
        }
    }
    
    func ctcaInfoAlert(title:String,
                       message: String,
                       okaction: UIAlertAction? = nil,
                       otheraction:UIAlertAction? = nil) -> UIAlertController {
        
        let alertController = UIAlertController(title: title,
                                                message: message,
                                                preferredStyle:.alert)
        
        if(okaction != nil) {
            alertController.addAction(okaction!)
        } else {
            let okAction = UIAlertAction(title: "OK",
                                         style: .default,
                                         handler: nil)
            alertController.addAction(okAction)
        }
        
        if (otheraction != nil) {
            alertController.addAction(otheraction!)
        }
        
        return alertController
    }
    
    func showDeleteAction(withtitle:String,
                          andMessage :String,
                          onView:UIViewController,
                          deleteAction: UIAlertAction? = nil,
                          otheraction:UIAlertAction? = nil) {
        
        var cancelAction = otheraction

        if cancelAction == nil {
            cancelAction = UIAlertAction(title: "Cancel", style: .default, handler: nil)
        }
        
        let alert = ctcaInfoAlert(title:withtitle,
                                  message: andMessage,
                                  okaction: cancelAction,
                                  otheraction:deleteAction)
        
        DispatchQueue.main.async {
            alert.view.tintColor = MyCTCAColor.ctcaGreen.color
            onView.present(alert,
                           animated: true,
                           completion: nil)
        }
        
    }
    
    func isValidEmail(_ email: String) -> Bool {
        let emailRegEx = "(?:[a-zA-Z0-9!#$%\\&‘*+/=?\\^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%\\&'*+/=?\\^_`{|}" +
            "~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\" +
            "x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-" +
            "z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5" +
            "]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-" +
            "9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21" +
            "-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
        
        let emailTest = NSPredicate(format:"SELF MATCHES[c] %@", emailRegEx)
        return emailTest.evaluate(with: email)
    }
    
    func isValidateStartAndEndDates(_ fromDateString:String, _ toDateString:String) -> Bool {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM d, yyyy"
        
        if let begin:Date = dateFormatter.date(from: fromDateString),
           let end:Date = dateFormatter.date(from: toDateString) {
            if (end < begin) {
                return false
            }
        } else {
            return false
        }
        
        return true
    }
    
    func getNoRecordsMessageWithStyle() -> NSAttributedString {
        let style = NSMutableParagraphStyle()
        style.alignment = NSTextAlignment.center
        
        let attrs = [NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: UIFont.TextStyle.headline),
                     NSAttributedString.Key.foregroundColor: MyCTCAColor.ctcaGrey.color,
                     NSAttributedString.Key.paragraphStyle: style ]
        
        return NSAttributedString(string: CommonMsgConstants.noRecordsFoundMsg, attributes: attrs)
    }
    
    func capableOfCalling() -> Bool {
        var isCapableToCall: Bool = false
        
        if UIApplication.shared.canOpenURL(URL(string: "tel://")!) {
            // Check if iOS Device supports phone calls
            // User will get an alert error when they will try to make a phone call in airplane mode
            if let mobileNetworkCode = CTTelephonyNetworkInfo().subscriberCellularProvider?.mobileNetworkCode, !mobileNetworkCode.isEmpty {
                // iOS Device is capable for making calls
                isCapableToCall = true
            }
        }
        
        return isCapableToCall
    }
    
    func showActivityIndicator(message: String? = nil) {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        if let rootNavCtrl = appDelegate.window?.rootViewController as? UINavigationController {
            DispatchQueue.main.async(execute: {
                LoadingOverlay.shared.showOverlay(view: rootNavCtrl.view, message: message)
            })
        }
    }
    
    func dismissActivityIndicator(completion: (() -> Swift.Void)? = nil) {
        DispatchQueue.main.async(execute: {
            LoadingOverlay.shared.fadeOutOverlay()
            if (completion != nil) {
                completion!()
            }
        })
    }
    
    func launchSafariViewController(withUrl: URL, forView:UIViewController) {
        print("launchSafariViewController : Opening url - \(withUrl.absoluteString)")
        let svc = SFSafariViewController(url:withUrl)
        svc.delegate = forView as? SFSafariViewControllerDelegate
        svc.preferredBarTintColor = MyCTCAColor.tableHeaderGrey.color
        svc.preferredControlTintColor = MyCTCAColor.ctcaGreen.color
        forView.present(svc, animated: true, completion: nil)
    }
    
    func launchSafariViewController(withPath: String, forView:UIViewController) {
        if let url = URL(string: withPath) {
            print("launchSafariViewController : Opening url - \(withPath)")
            
            let svc = SFSafariViewController(url:url)
            svc.delegate = forView as? SFSafariViewControllerDelegate
            svc.preferredBarTintColor = MyCTCAColor.tableHeaderGrey.color
            svc.preferredControlTintColor = MyCTCAColor.ctcaGreen.color
            forView.present(svc, animated: true, completion: nil)
        }
    }
    
    func openInSafari(path:String) {
        if let url = URL(string: path), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url)
        } else {
            showBanner(title: "Cannot open this page")
        }
    }
    
    func printDate(string: String) {
        let date = Date()
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss.SSSS"
        print(string + formatter.string(from: date))
    }
    
    func showBanner(title:String) {
        let bannerImage: UIImage = UIImage(named: "iInfo")!.withRenderingMode(.alwaysTemplate)
        
        let banner = Banner(title: title,
                            subtitle: nil,
                            image: bannerImage,
                            backgroundColor: MyCTCAColor.ctcaGreen.color.withAlphaComponent(0.85))
        banner.dismissesOnTap = true
        banner.position = .top
        banner.springiness = .none
        banner.show(duration: 3.0)
    }
    
    func hasPermissionTo(feature:UserPermissionType) -> Bool {
        // Check Permissions
        let currentUserId = AppSessionManager.shared.currentUser.currentUserId
        let userProfile = AppSessionManager.shared.currentUser.userProfile!
        return userProfile.userCan(feature, viewerId: currentUserId)
    }
    
    private func cleanTelNum(_ telNum: String) -> String {
        return telNum.components(separatedBy: CharacterSet.decimalDigits.inverted).joined()
    }
    
    func tryToCall(telNo:String, parentVC:UIViewController?) {        
        if (capableOfCalling()) {
            var cleanTelNum = self.cleanTelNum(telNo)
            
            if cleanTelNum.count == 10 {
                cleanTelNum = "1" + cleanTelNum //appending coutry code if missing
            }
            
            if let url: NSURL = URL(string: "tel://\(cleanTelNum)") as NSURL?
            {
                UIApplication.shared.open(url as URL, options: convertToUIApplicationOpenExternalURLOptionsKeyDictionary([:]), completionHandler: nil)
            } else {
                if let parent = parentVC {
                    showAlert(withtitle: "Error", andMessage: "Invalid phone number", onView: parent)
                }
            }
            
        } else {
            if let parent = parentVC {
                showAlert(withtitle: "Phone Error", andMessage: "This device doesn't appear capable of making phone calls. To contact Technical Support, please dial \n\(telNo)", onView: parent)
            } else {
                let appDelegate = UIApplication.shared.delegate as! AppDelegate
                if let rootNavCtrl = appDelegate.window?.rootViewController as? UINavigationController {
                    showAlert(withtitle: "Phone Error", andMessage: "This device doesn't appear capable of making phone calls. To contact Technical Support, please dial \n\(telNo)", onView: rootNavCtrl)
                }
            }
        }
    }
    
    // Helper function inserted by Swift 4.2 migrator.
    fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
        return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
    }
    
    func saveInUserDefaults(object: Any, key: String) {
        let app = UserDefaults.standard
        app.set(object, forKey: key)
    }
    
    func getFromUserDefaults(forKey: String) -> Any? {
        let app = UserDefaults.standard
        return app.object(forKey: forKey)
    }
    
    func deleteInUserDefaults(key: String) {
        let app = UserDefaults.standard
        app.removeObject(forKey: key)
    }
    
    func doSignOut(onView:UIViewController, sourceView:UIView) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MORE_SIGNOUT_TAP)
        
        print("doSignOut");
        
        let actionSheet = UIAlertController(title: nil, message: "\(MyCTCAConstants.SettingsText.signOutMessage)", preferredStyle: .actionSheet)
        
        let cancelAction = UIAlertAction(title: "\(MyCTCAConstants.FormText.buttonCancel)", style: .cancel) { (action) in
            print("Cancel AlertController")
        }
        
        let signOutAction = UIAlertAction(title: "\(MyCTCAConstants.FormText.buttonSignOut)", style: .destructive) { (action) in
            AppSessionManager.shared.endCurrentSession()
            AppSessionManager.shared.toLoginScreen()
        }
        
        actionSheet.addAction(signOutAction)
        actionSheet.addAction(cancelAction)
        
        actionSheet.view.tintColor = MyCTCAColor.ctcaGreen.color
        
        // For iPad
        if let popoverController = actionSheet.popoverPresentationController {
            popoverController.sourceView = sourceView
            popoverController.sourceRect = sourceView.bounds
        }
        
        DispatchQueue.main.async(execute: {
            onView.present(actionSheet, animated: true, completion: nil)
        })
    }
    
    func showNoAccessMessage(view:UIViewController?) {
        if let onView = view {
            showAlert(withtitle: CommonMsgConstants.noAccessTitle, andMessage: CommonMsgConstants.noAccessMessage, onView: onView)
        } else {
            let appDelegate = UIApplication.shared.delegate as! AppDelegate
            if let rootNavCtrl = appDelegate.window?.rootViewController as? UINavigationController {
                
                showAlert(withtitle: CommonMsgConstants.noAccessTitle, andMessage: CommonMsgConstants.noAccessMessage, onView: rootNavCtrl)
            }
        }
    }
    
    //- Mark Map related
    
    func openFacAddressInMap(facility:Facility?, locAddress:String? = nil, displayName:String? = nil) {
        var address = ""
        var dispName = ""
        
        if let addr = locAddress, let name = displayName {
            address = addr
            dispName = name
        } else if let fac = facility {
            address = fac.address.address1  ?? ""
            if (fac.address.address2 != nil) {
                address += ", \(String(describing: fac.address.address2 ?? ""))"
            }
            address += ", \(fac.address.city ?? ""), \(fac.address.state ?? "") \(fac.address.postalCode ?? "")"
            dispName = fac.displayName
        } else {
            return
        }
        
        self.coordinates(forAddress: address) {
            (location) in
            guard let location = location else {
                // Handle error here.
                return
            }
            AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_MAP_ADDR_TAP)
            
            self.openMapForPlace(lat: location.latitude, long: location.longitude, displayName:dispName)
        }
    }
    
    func coordinates(forAddress address: String, completion: @escaping (CLLocationCoordinate2D?) -> Void) {
        let geocoder = CLGeocoder()
        geocoder.geocodeAddressString(address) {
            (placemarks, error) in
            guard error == nil else {
                print("Geocoding error: \(error!)")
                completion(nil)
                return
            }
            completion(placemarks?.first?.location?.coordinate)
        }
    }
    
    func openMapForPlace(lat: CLLocationDegrees, long: CLLocationDegrees, displayName:String) {
        
        let latitude: CLLocationDegrees = lat
        let longitude: CLLocationDegrees = long
        
        let regionDistance:CLLocationDistance = 10000
        let coordinates = CLLocationCoordinate2DMake(latitude, longitude)
        let regionSpan = MKCoordinateRegion.init(center: coordinates, latitudinalMeters: regionDistance, longitudinalMeters: regionDistance)
        let options = [
            MKLaunchOptionsMapCenterKey: NSValue(mkCoordinate: regionSpan.center),
            MKLaunchOptionsMapSpanKey: NSValue(mkCoordinateSpan: regionSpan.span)
        ]
        let placemark = MKPlacemark(coordinate: coordinates, addressDictionary: nil)
        let mapItem = MKMapItem(placemark: placemark)
        mapItem.name = displayName
        mapItem.openInMaps(launchOptions: options)
    }
    
    func getImageForColor(color:UIColor) -> UIImage? {
        UIGraphicsBeginImageContext(CGSize(width: 1, height: 1))
        UIGraphicsGetCurrentContext()!.setFillColor(color.cgColor)
        UIGraphicsGetCurrentContext()!.fill(CGRect(x: 0, y: 0, width: 1, height: 1))
        let colorImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return colorImage
    }
    
    func updateTitleWithPrefix(label:UILabel, title:String?, isOptional:Bool = false) {
        var prefix = " (required)"
        if isOptional {
            prefix = " (optional)"
        }
        
        let title = title ?? (label.text ?? "")
        
        if title.contains(prefix) {
            return
        }
        
        let attrsTitle = [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue-Bold", size: 18.0)!,
                          NSAttributedString.Key.foregroundColor: UIColor.ctca_dark_gray]
        let attributedTitle = NSMutableAttributedString(string: title, attributes: attrsTitle)
        
        let attrReq = [NSAttributedString.Key.font:UIFont(name: "HelveticaNeue-Italic", size: 16.0)!,
                       NSAttributedString.Key.foregroundColor: UIColor.ctca_gray130]
        let reqTitle = NSAttributedString(string: prefix, attributes: attrReq)
        
        attributedTitle.append(reqTitle)
        label.attributedText = attributedTitle
    }
    
    func showFormLeaveAlert(leaveAction:@escaping (() -> Void)) {
        DispatchQueue.main.async(execute: {
            let keyWindow = UIApplication.shared.windows.filter {$0.isKeyWindow}.first

            if var topController = keyWindow?.rootViewController {
                while let presentedViewController = topController.presentedViewController {
                    topController = presentedViewController
                }
                let alertVC = UIStoryboard(name: "Secondary", bundle: nil).instantiateViewController(withIdentifier: "MyCTCAFormLeaveMessageViewController") as! MyCTCAFormLeaveMessageViewController
                alertVC.modalPresentationStyle = .overCurrentContext
                alertVC.leaveAction = leaveAction
                topController.present(alertVC, animated: true, completion: nil)
            }
        })
    }
}
