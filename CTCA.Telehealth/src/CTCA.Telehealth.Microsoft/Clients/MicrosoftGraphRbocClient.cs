using System;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Api;
using CTCA.Telehealth.Microsoft.Models;
using CTCA.Telehealth.Microsoft.Models.Calendar;
using CTCA.Telehealth.Microsoft.Security;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;

namespace CTCA.Telehealth.Microsoft.Clients
{
    public class MicrosoftGraphRbocClient : IMeetingProvider
    {
        private readonly MicrosoftGraphApiClientSettings _clientsettings;
        private readonly HttpClient _client;
        private readonly ILogger<MicrosoftGraphRbocClient> _logger;

        private readonly MicrosoftAccessToken _accessToken;

        public MicrosoftGraphRbocClient(IOptions<MicrosoftGraphApiClientSettings> clientSettings, HttpClient httpClient, IAccessTokenStrategy accessTokenStrategy, ILogger<MicrosoftGraphRbocClient> logger)
        {
            _clientsettings = clientSettings.Value;
            _client = httpClient;
            _logger = logger;

            _accessToken = accessTokenStrategy.AccessToken;
        }

       

        public async Task<TelehealthAppointment> ScheduleMeetingAsync(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var meetingRequest = new MeetingApiRequest(_clientsettings,request,_client,_logger,_accessToken, "/v1.0/me/onlineMeetings");
            
            var response = await meetingRequest.GetResponse(cancellationToken);

            return response;
        }

        public async Task<TelehealthAppointment> ScheduleReadinessCheckAsync(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var meetingRequest = new ReadinessCheckMeetingApiRequest(_clientsettings, request, _client, _logger, _accessToken, "/v1.0/me/onlineMeetings");

            var response = await meetingRequest.GetResponse(cancellationToken);

            return response;
        }

        public async Task<bool> CancelMeetingAsync(Domain.Models.TelehealthAppointment request, CancellationToken cancellationToken)
        { throw new NotImplementedException(); }

    }
}
