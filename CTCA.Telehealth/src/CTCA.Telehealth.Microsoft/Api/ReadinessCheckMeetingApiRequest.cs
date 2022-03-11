using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Common;
using CTCA.Telehealth.Microsoft.Models;
using CTCA.Telehealth.Microsoft.Security;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Api
{
    internal class ReadinessCheckMeetingApiRequest : ApiRequest<TelehealthAppointment>
    {

        private readonly TelehealthAppointment _request;
        private readonly ILogger _logger;

        public ReadinessCheckMeetingApiRequest(MicrosoftGraphApiClientSettings clientSettings, TelehealthAppointment request,
            HttpMessageInvoker client, ILogger logger, MicrosoftAccessToken accessToken, string resource)
            : base(clientSettings, HttpMethod.Post, client, logger, accessToken, resource)
        {
            _request = request;
            _logger = logger;
        }

        public override async Task<TelehealthAppointment> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false)
        {
            var response = await SendAsync(cancellationToken);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError($"Error creating online meeting with MicrosoftGraphApi. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}");
                return new TelehealthAppointment();
            }

            var responseObj = JsonConvert.DeserializeObject<MicrosoftMeetingResponse>(
                    await response.Content.ReadAsStringAsync());


            _request.MeetingId = responseObj.Id;
            _request.JoinUrl = responseObj.JoinUrl.OriginalString;
            _request.JoinWebUrl = responseObj.JoinWebUrl.OriginalString;
            _request.JoinContent = responseObj.JoinInformation.Content;
            _request.audioInfo = new AudioInformation
            {
                ConferenceId = responseObj.AudioConferencing.ConferenceId.ToString(),
                DialInUrl = responseObj.AudioConferencing.DialinUrl.OriginalString,
                PhoneNumber = responseObj.AudioConferencing.TollNumber.ToString()
            };
               
            return _request;
        }

        protected override object GetPayload()
        {

            var payload = new MicrosoftMeetingRequest
            {
                _startDateTime = _request.StartDateTime,
                _endDateTime = _request.StartDateTime.AddMinutes(_request.AppointmentDuration),
                _subject = "Readiness Check Appointment"
            };

            return payload;
        }
    }
}
