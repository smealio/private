//
//  NetworkServices.swift
//  myctca
//
//  Created by Manjunath K on 9/25/20.
//  Copyright © 2020 CTCA. All rights reserved.
//

import Foundation
import Alamofire

final class NetworkService {
    
    static let shared = NetworkService()
    
    /// defined certificates for Certificate pinning
    private let certificates = [
        "myctca.com":
            PinnedCertificatesTrustEvaluator(certificates: [Certificates.myCTCACert],
                                             acceptSelfSignedCertificates: false,
                                             performDefaultValidation: true,
                                             validateHost: true),
        "testaccounts.myctca.com":
            PinnedCertificatesTrustEvaluator(certificates: [Certificates.myCTCACert],
                                             acceptSelfSignedCertificates: false,
                                             performDefaultValidation: true,
                                             validateHost: true),
        "v3testservice.myctca.com":
            PinnedCertificatesTrustEvaluator(certificates: [Certificates.myCTCACert],
                                             acceptSelfSignedCertificates: false,
                                             performDefaultValidation: true,
                                             validateHost: true),
        "accounts.myctca.com":
            PinnedCertificatesTrustEvaluator(certificates: [Certificates.myCTCACert],
                                             acceptSelfSignedCertificates: false,
                                             performDefaultValidation: true,
                                             validateHost: true),
        "service.myctca.com":
            PinnedCertificatesTrustEvaluator(certificates: [Certificates.myCTCACert],
                                             acceptSelfSignedCertificates: false,
                                             performDefaultValidation: true,
                                             validateHost: true)
    ]
    
    private var _session: Session?
    //private var _nosession: Session?
    
    private var almofireSession: Session {
        get {
            if let actSession = _session {
                return actSession
            } else {
                _session = Session()
                return _session!
            }
        }
    }
    private var nonCTCAsession = Session()
//    private var nonCTCAsession: Session {
//        get {
//            if let actSession = _nosession {
//                return actSession
//            } else {
//                _nosession = Session()
//                return _nosession!
//            }
//    }
        
    /// init method for AlamofireNetworking
    ///
    /// - Parameter allHostsMustBeEvaluated: it configures certificate pinning behaviour
    /// if true: Alamofire will only allow communication with hosts defined in evaluators and matching defined Certificates.
    /// if false: Alamofire will check certificates only for hosts defined in evaluators dictionary. Communication with other hosts than defined will not use Certificate pinning
    private init() {
        
        let serverTrustPolicy = ServerTrustManager(
            allHostsMustBeEvaluated: true,
            evaluators: certificates
        )
        
        _session = {
            var configuration = URLSessionConfiguration.af.default
            configuration.timeoutIntervalForRequest = 120

            if TestSetupManager.shared.testMode {
                configuration = URLSessionConfiguration.ephemeral
                configuration.protocolClasses = [CTCAMockURLProtocol.self]
            }
            return Alamofire.Session(configuration: configuration, serverTrustManager: serverTrustPolicy)
        }()
    }

    func executeLoginRequest<T:Decodable>(urlRequest:BaseAPIRouter, decodingType: T.Type, completion: @escaping(Decodable?, ServerError?) -> Void) {
        
        almofireSession.request(urlRequest)
            .validate(statusCode: 200 ... 299)
            .responseDecodable {
                (response: AFDataResponse<T>) in
                
                if let data = response.data {
                    print("response in executeLoginRequest before parsing :\(String(decoding: (data), as: UTF8.self))")
                }
                
                switch response.result {
                case .success(let result):
                    completion(result, nil)
                case .failure(let error):
                    var sError = ServerError(serverError: error)
                    if let underlyingError = error.underlyingError,
                       let urlError = underlyingError as? URLError {
                        sError = ServerError(afError: urlError)
                    } 
                    
                    if let jsonData = response.data {
                        do {
                            let model: AuthModel = try JSONDecoder().decode(AuthModel.self, from: jsonData)
                            completion(model, sError)
                        } catch let error {
                            print("executeLoginRequest : \(error)")
                            completion(nil, sError)
                        }
                    } else {
                        completion(nil, sError)
                    }
                }
            }
    }
    
    func executeRequestForGenerics(urlRequest:BaseAPIRouter, completion: @escaping(Data?, ServerError?) -> Void) {
        print("executeRequestForGenerics : \(urlRequest.path)")
        almofireSession.request(urlRequest)
            .validate(statusCode: 200 ... 299)
            .responseData {
                response in
                
                if let data = response.data {
                    print("response in executeRequestForGenerics before parsing :\(String(decoding: (data), as: UTF8.self))")
                }
                
                switch response.result {
                case .success(let result):
                    completion(result, nil)
                case .failure(let error):
                    var sError = ServerError(serverError: error)
                    if let underlyingError = error.underlyingError,
                       let urlError = underlyingError as? URLError {
                        sError = ServerError(afError: urlError)
                    }
                    print("Error in executeRequestForGenerics : \(response)")
                    completion(nil, sError)
                }
            }
    }
    
    func executeRequest<T:Decodable>(urlRequest:BaseAPIRouter, decodingType: T.Type, completion: @escaping(Decodable?, ServerError?) -> Void) {
        print("executeRequest : \(urlRequest.path)")
        almofireSession.request(urlRequest)
            .validate(statusCode: 200 ... 299)
            .responseDecodable {
                (response: AFDataResponse<T>) in
                
                if let data = response.data {
                    print("response in executeRequest before parsing :\(String(decoding: (data), as: UTF8.self))")
                }
                
                switch response.result {
                case .success(let result):
                    completion(result, nil)
                case .failure(let error):
                    var sError = ServerError(serverError: error)
                    if let underlyingError = error.underlyingError,
                       let urlError = underlyingError as? URLError {
                        sError = ServerError(afError: urlError)
                    }
                    print("Error in executeGetRequest : \(response)")
                    completion(nil, sError)
                }
            }
    }
    
    func executeDownlodRequest(urlRequest:BaseAPIRouter, fileName: String, completionHandler:@escaping(String?, ServerError?, RESTResponse)->()){
        
        let destinationPath: DownloadRequest.Destination = { _, _ in
            let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0];
            let fileURL = documentsURL.appendingPathComponent("\(fileName)")
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
        }
        
        almofireSession.download(urlRequest, to: destinationPath)
            .downloadProgress { progress in }
            .validate(statusCode: 200 ... 299)
            .responseData { response in
                print("response: \(response)")
                switch response.result {
                case .success:
                    if response.fileURL != nil, let filePath = response.fileURL?.absoluteString {
                        print("executeDownlodRequest: filePath: \(filePath)")
                        completionHandler(filePath, nil, .SUCCESS)
                    }
                    break
                case .failure(let error):
                    var sError = ServerError(serverError: error)
                    if let underlyingError = error.underlyingError,
                       let urlError = underlyingError as? URLError {
                        sError = ServerError(afError: urlError)
                    }
                    completionHandler(nil, sError, .FAILED)
                    break
                }
                
            }
    }
    
    func executeRequestSync(urlRequest:BaseAPIRouter) -> Data? {
        let url = urlRequest.urlRequest
        let semaphore = DispatchSemaphore(value: 0)
        
        var result: Data?
        
        let task = URLSession.shared.dataTask(with: url!) {(data, response, error) in
            
            if let returnedData = data {
                result = returnedData
            } else {
                result = nil
            }
            
            semaphore.signal()
        }
        
        task.resume()
        semaphore.wait()
        return result
    }
    
    func executeNonCTCARequest<T:Decodable>(urlRequest:BaseAPIRouter, decodingType: T.Type, completion: @escaping(Decodable?, ServerError?) -> Void) {
        print("executeRequest : \(urlRequest.path)")
        let header : HTTPHeaders = ["Ocp-Apim-Subscription-Key" : "7556d0d1508a4deb8d15d0b41d0342fe"]
        
        AF.request("https://apim.ctca-hope.com/test/telehealth/api/accesstoken", method: .post, parameters: nil, headers:header)
            .validate(statusCode: 200 ... 299)
            .responseDecodable {
                (response: AFDataResponse<T>) in
                
                if let data = response.data {
                    print("response in executeNonCTCARequest before parsing :\(String(decoding: (data), as: UTF8.self))")
                }
                
                switch response.result {
                case .success(let result):
                    completion(result, nil)
                case .failure(let error):
                    print("Error in executeGetRequest : \(response)")
                    var sError = ServerError(serverError: error)
                    if let underlyingError = error.underlyingError,
                       let urlError = underlyingError as? URLError {
                        sError = ServerError(afError: urlError)
                    }
                    completion(nil, sError)
                }
            }
    }
    
    func executeDownlodRequest(url:URLRequest, fileName: String, completionHandler:@escaping(String?, ServerError?, RESTResponse)->()){
        
        let destinationPath: DownloadRequest.Destination = { _, _ in
            let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0];
            let fileURL = documentsURL.appendingPathComponent("\(fileName)")
            return (fileURL, [.removePreviousFile, .createIntermediateDirectories])
        }
        
        almofireSession.download(url, to: destinationPath)
            .downloadProgress { progress in }
            .validate(statusCode: 200 ... 299)
            .responseData { response in
                print("response: \(response)")
                switch response.result {
                case .success:
                    if response.fileURL != nil, let filePath = response.fileURL?.absoluteString {
                        print("executeDownlodRequest: filePath: \(filePath)")
                        completionHandler(filePath, nil, .SUCCESS)
                    }
                    break
                case .failure(let error):
                    var sError = ServerError(serverError: error)
                    if let underlyingError = error.underlyingError,
                       let urlError = underlyingError as? URLError {
                        sError = ServerError(afError: urlError)
                    }
                    completionHandler(nil, sError, .FAILED)
                    break
                }
                
            }
    }
    
}
