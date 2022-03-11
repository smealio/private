//
//  ClinicalSummaryTransmitActionTableViewCell.swift
//  myctca
//
//  Created by Manjunath K on 8/17/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import UIKit

protocol ClinicalSummaryTransmitActionProtocol : AnyObject {
    func didPerformAction(state:Bool);
}

class ClinicalSummaryTransmitActionTableViewCell: UITableViewCell {

    @IBOutlet weak var trasmitButton: UIButton!
    @IBOutlet weak var closeButton: UIButton!
    
    weak var delegate:ClinicalSummaryTransmitActionProtocol?

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func transmitTapped(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_TRANSMIT_TAP)
        if let del = delegate {
            del.didPerformAction(state: true)
        }
    }
    
    @IBAction func closeTapped(_ sender: Any) {
        AnalyticsManager.shared.trackEvent(CTCAAnalyticsConstants.ACTION_CS_TRANSMIT_CANCEL_TAP)

        if let del = delegate {
            del.didPerformAction(state: false)
        }

    }
}
