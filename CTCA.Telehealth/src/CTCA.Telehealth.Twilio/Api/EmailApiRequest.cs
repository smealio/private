using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Common;
using CTCA.Telehealth.Shared;
using CTCA.Telehealth.Twilio;
using CTCA.Telehealth.Twilio.Models;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Api
{
    internal class EmailApiReqest : ApiRequest<(bool, ServiceError)>
    {

        private readonly TelehealthAppointment _request;
        private readonly ILogger _logger;

        public EmailApiReqest(TwilioClientSettings clientSettings, TelehealthAppointment request,
            HttpMessageInvoker client, ILogger logger, string resource)
            : base(clientSettings, HttpMethod.Post, client, logger, resource)
        {
            _request = request;
            _logger = logger;
        }

        public override async Task<(bool, ServiceError)> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false)
        {
            var response = await SendAsync(cancellationToken);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError($"Error sending email using Twilio. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}");
                return (false, new ServiceError() 
                { 
                    Code = (int)response.StatusCode,
                    Message = $"Error sending email using Twilio. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}"
                });
            }

            return (true, new ServiceError());
        }

        protected override object GetPayload()
        {

            var payload = new SendGridSendEmailPayload
            {
            };

            return payload;
        }

        private static string MapTimeZone(string timezone) {
            switch (timezone.ToUpper()) {
                case "CENTRAL STANDARD TIME":
                    return "CST";
                case "EASTERN STANDARD TIME":
                    return "EST";
                case "PACIFIC STANDARD TIME":
                    return "PST";
                case "MOUNTAIN STANDARD TIME":
                    return "MST";
                default:
                    return timezone;

            }
        }

        private string GetEmailBody()
        {
            return CTCA.Telehealth.Shared.Resources.ResourceAccessor.GetPatientEmailTemplate();

        }
    }
}
