//
//  UIImageViewExtension.swift
//  myctca
//
//  Created by Manjunath K on 11/24/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation

extension UIImage {
    func colored(in color: UIColor) -> UIImage {
        let renderer = UIGraphicsImageRenderer(size: size)
        return renderer.image { context in
            color.set()
            self.withRenderingMode(.alwaysTemplate).draw(in: CGRect(origin: .zero, size: size))
        }
    }
}
