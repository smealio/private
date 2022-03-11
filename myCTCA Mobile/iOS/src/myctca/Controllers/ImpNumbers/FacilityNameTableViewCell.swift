//
//  FacilityNameTableViewCell.swift
//  myctca
//
//  Created by Manjunath Kambalekar on 26/08/21.
//  Copyright © 2021 CTCA. All rights reserved.
//

import UIKit

class FacilityNameTableViewCell: UITableViewCell {

    @IBOutlet weak var facNameLabel: UILabel!
    @IBOutlet weak var facAddressButton: UIButton!
    @IBOutlet weak var buttonHtConstant: NSLayoutConstraint!
    
    var facility:Facility?
    
    func setUp(primFacility:Facility?) {
        guard let fac = primFacility else {
            return
        }
        facility = fac
        
        facNameLabel.text = fac.shortDisplayName
        let address = "\(fac.address.address1 ?? "")\n\(fac.address.city ?? ""), \(fac.address.state ?? "") \(fac.address.postalCode ?? "")"
        
        let attributes = [NSAttributedString.Key.underlineColor: UIColor(red:0, green:0.3125, blue:0.78125, alpha:1.0),
                          NSAttributedString.Key.underlineStyle: NSUnderlineStyle.single.rawValue] as [NSAttributedString.Key : Any]
        
        facAddressButton.setAttributedTitle(NSAttributedString(string: address, attributes: attributes), for: .normal)
        facAddressButton.titleLabel?.textAlignment = .center
    }
    
    @IBAction func facAddressButtonTapped(_ sender: Any) {
        if let primFac = facility {
            GenericHelper.shared.openFacAddressInMap(facility: primFac)
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
