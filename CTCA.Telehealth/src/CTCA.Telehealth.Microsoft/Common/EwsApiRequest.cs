using System;
using System.IO;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using System.Xml;
using CTCA.Telehealth.Microsoft.Clients;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.Microsoft.Common
{
    internal abstract class EwsApiRequest<TResponseType>
    {
        private readonly MicrosoftEwsClientSettings _clientSettings;
        private readonly ILogger _logger;
        private readonly string _requestUri;
        private readonly string _action;
        private readonly IConfiguration _configuration;

        protected EwsApiRequest(
            MicrosoftEwsClientSettings clientSettings,
            ILogger logger,
            IConfiguration configuration
            )
        {
            //need to populate token here somewhere

            //_requestUri = new Uri($"{clientSettings.BaseUrl}{resource}");
            //_clientSettings = clientSettings;
            //_client = client;
            //_logger = logger;
            //_method = method;
            //_accessToken = accessToken;


            _requestUri = "https://10.75.125.69/ews/exchange.asmx";
            _action = "https://10.75.125.69/ews/exchange.asmx";
            _configuration = configuration;
        }

        /// <summary>
        /// Sends the request and returns the response.
        /// </summary>
        /// <returns></returns>
#pragma warning disable CS1998 // Async method lacks 'await' operators and will run synchronously -
        //this warning is inaccurate as the async is built into the handle
        public async Task<WebResponse> SendAsync(CancellationToken cancellationToken)
#pragma warning restore CS1998 // Async method lacks 'await' operators and will run synchronously
        {

            System.Net.ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls | SecurityProtocolType.Tls11 | SecurityProtocolType.Tls12;

            var webRequest = GetRequestMessage();

            var asyncResult = webRequest.BeginGetResponse(null, null);

            // suspend this thread until call is complete. You might want to
            // do something usefull here like update your UI.
            asyncResult.AsyncWaitHandle.WaitOne();

            return webRequest.EndGetResponse(asyncResult);
        }

        private HttpWebRequest GetRequestMessage()
        {
            HttpWebRequest webRequest = (HttpWebRequest)WebRequest.Create(_requestUri);
            var username = _configuration.GetValue<string>("EwsApi:username");
            var password = _configuration.GetValue<string>("EwsApi:password");
            var domain = _configuration.GetValue<string>("EwsApi:domain");
            webRequest.Credentials = new System.Net.NetworkCredential(username, password, domain);

            XmlDocument soapEnvelopeXml = new XmlDocument();
            soapEnvelopeXml.LoadXml(GetPayload().ToString());

            webRequest.Headers.Add("Host", "webmail.ctca-hope.com");
            webRequest.Headers.Add("SOAPAction", _action);
            webRequest.ContentType = "text/xml;charset=\"utf-8\"";
            webRequest.Accept = "text/xml";
            webRequest.Method = "POST";

            InsertSoapEnvelopeIntoWebRequest(soapEnvelopeXml, webRequest);

            return webRequest;
        }

        public static void InsertSoapEnvelopeIntoWebRequest(XmlDocument soapEnvelopeXml, HttpWebRequest webRequest)
        {
            using (Stream stream = webRequest.GetRequestStream())
            {
                soapEnvelopeXml.Save(stream);
            }

        }


        public abstract Task<TResponseType> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false);

        /// <summary>
        /// Gets the payload that will be serialized into the request body.
        /// </summary>
        /// <returns></returns>
        protected abstract object GetPayload();


    }
}
