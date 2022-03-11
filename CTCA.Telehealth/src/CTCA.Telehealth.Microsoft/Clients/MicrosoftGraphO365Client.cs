using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Api;
using CTCA.Telehealth.Microsoft.Security;
using CTCA.Telehealth.Shared;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Microsoft.Clients
{
    public class MicrosoftGraphO365Client : ICalendarProvider
    {
        private readonly MicrosoftGraphApiClientSettings _clientsettings;
        private readonly HttpClient _client;
        private readonly ILogger<MicrosoftGraphO365Client> _logger;

        private readonly MicrosoftAccessToken _accessToken;

        public string ChannelName { get; }

        public MicrosoftGraphO365Client(IOptions<MicrosoftGraphApiClientSettings> clientSettings, HttpClient httpClient, IClientAccessTokenStrategy accessTokenStrategy, ILogger<MicrosoftGraphO365Client> logger)
        {
            _clientsettings = clientSettings.Value;
            _client = httpClient;
            _logger = logger;

            _accessToken = accessTokenStrategy.AccessToken;

            ChannelName = "O365";
        }



        public async Task<(TelehealthAppointment, ServiceError)> ScheduleEvent(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var meetingRequest = new CalendarApiRequest(_clientsettings, request, _client, _logger, _accessToken, HttpMethod.Post, $"/v1.0/users/{request.Service.SourceProviderEmail}/events");
            
            var response = await meetingRequest.GetResponse(cancellationToken);

            return response;
        }

        public async Task<(TelehealthAppointment, ServiceError)> ScheduleReadinessCheck(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var meetingRequest = new ReadinessCheckCalendarApiRequest(_clientsettings, request, _client, _logger, _accessToken, HttpMethod.Post, $"/v1.0/users/{request.Service.SourceProviderEmail}/events");

            var response = await meetingRequest.GetResponse(cancellationToken);

            return response;
        }

        public async Task<(bool, ServiceError)> DeleteEventAsync(TelehealthAppointment request, CancellationToken cancellationToken)
        {
             
            var meetingRequest = new GraphCalendarDeleteApiRequest(_clientsettings, null, _client, _logger, _accessToken, HttpMethod.Delete, $"/v1.0/users/{request.Service.SourceProviderEmail}/events/{request.CalendarID}");

            var response = await meetingRequest.GetResponse(cancellationToken);

            return response;
        }



    }
}
