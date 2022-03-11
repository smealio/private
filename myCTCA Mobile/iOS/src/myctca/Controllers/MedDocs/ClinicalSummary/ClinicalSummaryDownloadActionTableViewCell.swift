//
//  ClinicalSummaryDownloadActionTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 8/17/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

protocol ClinicalSummaryDownloadActionProtocol : AnyObject {
    func didPerformAction(state:Bool);
}

class ClinicalSummaryDownloadActionTableViewCell: UITableViewCell {

    @IBOutlet weak var trasmitButton: UIButton!
    @IBOutlet weak var closeButton: UIButton!
    
    weak var delegate:ClinicalSummaryDownloadActionProtocol?

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func transmitTapped(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_DOWNLOAD_TAP)
        if let del = delegate {
            del.didPerformAction(state: true)
        }
    }
    
    @IBAction func closeTapped(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_DOWNLOAD_CANCEL_TAP)

        if let del = delegate {
            del.didPerformAction(state: false)
        }

    }
}
