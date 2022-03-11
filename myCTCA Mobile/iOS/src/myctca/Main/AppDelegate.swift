//
//  AppDelegate.swift
//  myctca
//
//  Created by Tomack, Barry on 11/2/17.
//  Copyright © 2017 CTCA. All rights reserved.
//

import UIKit

//@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    var orientationLock:UIInterfaceOrientationMask = .all
    
    //some random number
    private let blurViewTag = 198698
    
    // Flag for determining if app is first launched or is returning from background
    // Is set to true after application first becomes active - added 11/6/17 for version 0.7.1
    var activatedFromBackground:Bool = false
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        self.activatedFromBackground = false
        
        NetworkStatusManager.shared.startObserver()
        
        AnalyticsManager.shared.startAnalyticsReporting()
        
        GenericHelper.shared.saveInUserDefaults(object: false, key: WarningMessageManager().userDefaultsWarningMesageKey)
        
        if #available(iOS 15.0, *) {
            UITableView.appearance().sectionHeaderTopPadding = 0.0
        }
        
        return true
    }
    
    func popToRoot() {
        print("AppDelegate popToRoot")
        DispatchQueue.main.async(execute: {
            if let rootNavCtrl = self.window?.rootViewController as? UINavigationController {
                print("AppDelegate poptoRoot: \(rootNavCtrl)")
                rootNavCtrl.dismiss(animated: true, completion: {})
                rootNavCtrl.popToRootViewController(animated: true)
            }
        })
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
        
        //Blur snaphshot
        blurPresentedView()
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
                
        var bgTaskId = UIBackgroundTaskIdentifier.invalid
        let taskId = application.beginBackgroundTask {
            application.endBackgroundTask(convertToUIBackgroundTaskIdentifier(bgTaskId.rawValue))
        }
        
        bgTaskId = taskId
        
        if (bgTaskId == UIBackgroundTaskIdentifier.invalid) {
            print("Failed to start background task")
            return
        }
        // Set Time Stamp in UserPrefs for when application entered background
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(5), execute: {
            let prefs:UserDefaults = UserDefaults.standard
            prefs.set(Date(), forKey: MyCTCAConstants.UserPrefs.enterBackground)
            
            application.endBackgroundTask(taskId)
        })
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        
        // Retrieve EnterBackground from UserDefaults if activated from background and not first launch
        if (self.activatedFromBackground) {
            let prefs:UserDefaults = UserDefaults.standard
            if let enterBackgroundTimestamp:Date = prefs.object(forKey: MyCTCAConstants.UserPrefs.enterBackground) as? Date {
                print("AppSessionManager.shared: enterBackgroundTimestamp: \(enterBackgroundTimestamp)")
                prefs.removeObject(forKey: MyCTCAConstants.UserPrefs.enterBackground)
                // Calculate the seconds since going into backgroud (5 second delay)
                let _:Int = Calendar.current.dateComponents([.second], from: enterBackgroundTimestamp, to: Date()).second ?? 0
            }
        } else {
            self.activatedFromBackground = true
        }
        
        unblurPresentedView()
    }
    
    @objc func gotoViewController(path:String) -> Bool {
        if let page = NavigationManager.shared.getViewForUrl(url: path), page != "" {
            NavigationManager.shared.gotoPage(withName: page)
            return true
        }
        return false
    }
    
    func openUniversalLinkInSafari() {
        
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        return true
    }
    
    
    func application(
        _ application: UIApplication,
        continue userActivity: NSUserActivity,
        restorationHandler: @escaping ([UIUserActivityRestoring]?
        ) -> Void) -> Bool {
        
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
              let url = userActivity.webpageURL,
              let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            return false
        }
        
        if gotoViewController(path: components.path) {
            return true
        }
        
        application.open(url)
        
        return false
    }
    
    func needsIdleTimeOut(backgroundInterval: Int) -> Bool {
        
        var needsTimeOut = true
        var idleTimeInt:Int
        if (AppSessionManager.shared.currentUser.sessionIdleTimer != nil) {
            idleTimeInt = Int(UserValues.sessionTimeOutPeriod)
        } else {
            idleTimeInt = Int(UserValues.sessionTimeOutPeriod)
        }
        print("backgroundInterval: \(backgroundInterval)")
        print("idleTimeInt: \(idleTimeInt)")
        
        if(backgroundInterval > idleTimeInt) {
            DispatchQueue.main.async {
                AppSessionManager.shared.endCurrentSession()
            }
        } else {
            needsTimeOut = false
        }
        return needsTimeOut
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        AppSessionManager.shared.endCurrentSession()
    }
    
    /**
     Mark - PUSH Notifications
     */
    
    // Called when APNs has assigned the device a unique token
    //    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
    //        // Convert token to string
    //        let deviceTokenString = deviceToken.reduce("", {$0 + String(format: "%02X", $1)})
    //        // Print it to console
    //        print("APNs device token: \(deviceTokenString)")
    //        // Persist it in your backend in case it's new
    //        UserDefaults.standard.set(deviceTokenString, forKey: MyCTCAConstants.UserPrefs.deviceTokenString)
    //    }
    //
    //    // Called when APNs failed to register the device for push notifications
    //    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
    //        // Print the error to console (you should alert the user that registration failed)
    //        print("APNs registration failed: \(error)")
    //    }
    //
    //    // Push notification received
    //    func application(_ application: UIApplication, didReceiveRemoteNotification data: [AnyHashable : Any]) {
    //        // Print notification payload data
    //        print("Push notification received: \(data)")
    //    }
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        
        return self.orientationLock
    }
    
    struct AppUtility {
        
        static func lockOrientation(_ orientation: UIInterfaceOrientationMask) {
            
            if let delegate = UIApplication.shared.delegate as? AppDelegate {
                
                delegate.orientationLock = orientation
                
            }
            
        }
        
        static func lockOrientation(_ orientation: UIInterfaceOrientationMask, andRotateTo rotateOrientation:UIInterfaceOrientation) {
            
            self.lockOrientation(orientation)
            
            UIDevice.current.setValue(rotateOrientation.rawValue, forKey: "orientation")
            
        }
        
    }
}

// Helper function inserted by Swift 4.2 migrator.
fileprivate func convertToUIBackgroundTaskIdentifier(_ input: Int) -> UIBackgroundTaskIdentifier {
    return UIBackgroundTaskIdentifier(rawValue: input)
}


extension AppDelegate {
    
    func blurPresentedView() {
        //return if bluered view with hardcoded tag is added to main window
        DispatchQueue.main.async(execute: {
            if (self.window?.viewWithTag(self.blurViewTag) != nil){
                return
            }
            
            let snapshot  = self.bluredSnapshot();
            self.window?.addSubview(snapshot!)
        })
    }
    
    //find and remove blured view
    func unblurPresentedView() {
        DispatchQueue.main.async(execute: {
            if let blurView = self.window?.viewWithTag(self.blurViewTag) {
                blurView.removeFromSuperview()
            }
        })
    }
    
    func bluredSnapshot () -> UIView? {
        if let rootVC = self.window?.rootViewController {
            let imageView = UIImageView()
            imageView.frame = rootVC.view.bounds
            let blurEffect = UIBlurEffect(style: .extraLight)
            let blurredEffectView = UIVisualEffectView(effect: blurEffect)
            blurredEffectView.frame = imageView.bounds
            imageView.addSubview(blurredEffectView)
            imageView.tag = self.blurViewTag
            return imageView
        }
        return nil
    }
}


