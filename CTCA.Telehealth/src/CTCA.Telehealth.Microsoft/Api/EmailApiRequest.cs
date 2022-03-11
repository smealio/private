using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Common;
using CTCA.Telehealth.Microsoft.Models.Email;
using CTCA.Telehealth.Microsoft.Security;
using CTCA.Telehealth.Shared;
using CTCA.Telehealth.Shared.Resources;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Api
{
    internal class EmailApiReqest : ApiRequest<(bool, ServiceError)>
    {

        private readonly TelehealthAppointment _request;
        private readonly EmailSettings _emailSettings;
        private readonly ILogger _logger;

        public EmailApiReqest(MicrosoftGraphApiClientSettings clientSettings, EmailSettings emailSettings, TelehealthAppointment request,
            HttpMessageInvoker client, ILogger logger, MicrosoftAccessToken accessToken, string resource)
            : base(clientSettings, HttpMethod.Post, client, logger, accessToken, resource)
        {
            _request = request;
            _emailSettings = emailSettings;
            _logger = logger;
        }

        public override async Task<(bool, ServiceError)> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false)
        {
            var response = await SendAsync(cancellationToken);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError($"Error sending email using MicrosoftGraphApi. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}");
                return (false, new ServiceError() 
                { 
                    Code = (int)response.StatusCode,
                    Message = $"Error sending email using MicrosoftGraphApi. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}"
                });
            }

            return (true, new ServiceError());
        }

        protected override object GetPayload()
        {

            var payload = new MicrosoftMailRequest
            {
                Message = new Message
                {
                    Subject = "Your Upcoming Telehealth Video Appointment with CTCA",
                    Body = new Body
                    {
                        ContentType = "HTML",
                        Content = GetEmailBody().Replace("[PATIENT]", _request.PatientName)
                    .Replace("[DATETIME]", $"{_request.StartDateTime.DateTime.ToString("dddd, MMMM dd, yyyy")} at {_request.StartDateTime.DateTime.ToString("hh:mm")} {_request.StartDateTime.DateTime.ToString("tt")} {MapTimeZone(_request.AppointmentTimeZone)}")
                    .Replace("[PHONE]", _request.audioInfo.PhoneNumber)
                    .Replace("[MEETING ID]", _request.audioInfo.ConferenceId)
                    .Replace("[MEETING_URL]", _emailSettings.EmailMeetingUrl+WebUtility.UrlEncode(_request.id))
                    .Replace("[AUDIO_URL]", _request.audioInfo.DialInUrl)
                    .Replace("[PROVIDERNAME]",_request.ProviderName)
                    },

                    ToRecipients = new List<Recipient>()
                    {
                        new Recipient
                        {
                            EmailAddress= new EmailAddress{
                               Address = _request.PatientEmail
                            }
                    }

                    }
                },

                SaveToSentItems = false

            };


            return payload;
        }

        private static string MapTimeZone(string timezone)
        {
            switch (timezone.ToUpper())
            {
                case "CENTRAL STANDARD TIME":
                    return "CT";
                case "EASTERN STANDARD TIME":
                    return "ET";
                case "PACIFIC STANDARD TIME":
                    return "PT";
                case "MOUNTAIN STANDARD TIME":
                    return "MT";
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
