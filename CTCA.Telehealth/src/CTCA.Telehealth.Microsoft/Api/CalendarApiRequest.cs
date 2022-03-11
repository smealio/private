using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Reflection;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Common;
using CTCA.Telehealth.Microsoft.Models;
using CTCA.Telehealth.Microsoft.Models.Calendar;
using CTCA.Telehealth.Microsoft.Security;
using CTCA.Telehealth.Shared;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Api
{
    internal class CalendarApiRequest : ApiRequest<(TelehealthAppointment, ServiceError)>
    {

        private readonly TelehealthAppointment _request;
        private readonly ILogger _logger;

        public CalendarApiRequest(MicrosoftGraphApiClientSettings clientSettings, TelehealthAppointment request,
            HttpMessageInvoker client, ILogger logger, MicrosoftAccessToken accessToken, HttpMethod method, string resource)
            : base(clientSettings, method, client, logger, accessToken, resource)
        {
            _logger = logger;
            _request = request;
        }

        public override async Task<(TelehealthAppointment, ServiceError)> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false)
        {
            var response = await SendAsync(cancellationToken);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError($"Error scheduling event with MicrosoftGraphApi. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}");
                return (new TelehealthAppointment(), new ServiceError()
                {
                    Code = (int)response.StatusCode,
                    Message = $"Error scheduling event with MicrosoftGraphApi. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}"
                });
            }

            var responseObj = JsonConvert.DeserializeObject<MicrosoftMeetingResponse>(
                    await response.Content.ReadAsStringAsync());

            _request.CalendarID = responseObj.Id;

            return (_request, new ServiceError());
        }

        protected override object GetPayload()
        {

            var payload = new MicrosoftCalendarRequest
            {
                subject = "CTCA Telehealth Appointment",
                body = new Body
                {
                    contentType = "HTML",
                    content = $"<html><center><p><h3>{_request.PatientName}<br />" +
                    $"<h4>{_request.StartDateTime.ToString("dddd, MMMM dd, yyyy")}<br />" +
                    $"<p>For CTCA Telehealth technical related questions please call 800-234-0577 or email <a href=\"mailto:CTCATelehealthSupport@ctca-hope.com?subject=Virtual Visit Help\" style='text - decoration: underline; color: #f8b370;' title='CTCATelehealthSupport@ctca-hope.com'>CTCATelehealthSupport@ctca-hope.com </a> <br /> with your full name, contact phone number and best time to reach you 7am-6pm CT.</p><br /><br />" +
                    $"{Regex.Replace(HttpUtility.UrlDecode(_request.JoinContent.Replace("data:text/html,", "")), @"\r\n?|\n?|\t", "")}" +
                    $"</center></html>"
                },
                start = new Start
                {
                    dateTime = _request.StartDateTime.DateTime,
                    timeZone = _request.AppointmentTimeZone
                },
                end = new End
                {
                    dateTime = _request.EndDateTime.DateTime,
                    timeZone = _request.AppointmentTimeZone
                },
                locations = new List<Location>()
                {
                    new Location
                    {
                        displayName = "Microsoft Teams",
                        locationType = "default",
                        uniqueId = _request.MeetingId,
                        uniqueIdType = "locationStore"

                    },
                     new Location
                    {
                        displayName = "Microsoft Teams",
                        locationType = "default",
                        uniqueId = "Microsoft Teams Meeting",
                        uniqueIdType = "private"

                    }
                },
                attendees = new List<AttendeesItem>()
                {

                }

            };

            return payload;
        }

        private string GetCalendarBody()
        {
            return CTCA.Telehealth.Shared.Resources.ResourceAccessor.GetPatientEmailTemplate();

        }
    }
}
