using System;
using System.Net;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Api;
using CTCA.Telehealth.Microsoft.Models.EWS.Create;
using Microsoft.Exchange.WebServices.Data;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;

namespace CTCA.Telehealth.Microsoft.Clients
{
    public class MicrosoftEwsClient : ICalendarProvider
    {
        private readonly MicrosoftEwsClientSettings _clientsettings;
        private readonly HttpClient _client;
        private readonly ILogger<MicrosoftGraphO365Client> _logger;
        private readonly IConfiguration _configuration;

        public string ChannelName { get; }

        public MicrosoftEwsClient(IOptions<MicrosoftEwsClientSettings> clientSettings, HttpClient httpClient, ILogger<MicrosoftGraphO365Client> logger, IConfiguration configuration)
        {
            _clientsettings = clientSettings.Value;
            _client = httpClient;
            _logger = logger;
            _configuration = configuration;
            ChannelName = "EWS";
        }
             

        public async Task<(TelehealthAppointment, Shared.ServiceError)> ScheduleEvent(TelehealthAppointment telehealthAppointment, CancellationToken cancellationToken)
        {
            var newEvent = new EwsCalendarCreateApiRequest(_clientsettings, telehealthAppointment, _logger, _configuration);

            var ewsResponse = await newEvent.GetResponse(cancellationToken);

            telehealthAppointment.CalendarID = ewsResponse.Item1;

            return (telehealthAppointment, ewsResponse.Item2);
            
        }

        public async Task<(bool, CTCA.Telehealth.Shared.ServiceError)> DeleteEventAsync(TelehealthAppointment telehealthAppointment, CancellationToken cancellationToken)
        {
            var newEvent = new EwsCalendarDeleteApiRequest(_clientsettings, telehealthAppointment, _logger, _configuration);

            var response = await newEvent.GetResponse(cancellationToken);
            return response;
        }

        public async Task<(TelehealthAppointment, Shared.ServiceError)> ScheduleReadinessCheck(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var newEvent = new EwsCalendarCreateApiRequest(_clientsettings, request, _logger, _configuration);

            var ewsResponse = await newEvent.GetResponse(cancellationToken);

            request.CalendarID = ewsResponse.Item1;

            return (request, ewsResponse.Item2);
        }
    }
}
