using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Api;
using CTCA.Telehealth.Microsoft.Models.Email;
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
    public class MicrosoftGraphEmailClient : IMailProvider
    {
        private readonly MicrosoftGraphApiClientSettings _clientsettings;
        private readonly HttpClient _client;
        private readonly ILogger<MicrosoftGraphO365Client> _logger;
        private readonly EmailSettings _emailSettings;

        private readonly MicrosoftAccessToken _accessToken;

        public string ChannelName { get; }

        public MicrosoftGraphEmailClient(IOptions<MicrosoftGraphApiClientSettings> clientSettings, IOptions<EmailSettings> emailSettings, HttpClient httpClient, IClientAccessTokenStrategy accessTokenStrategy, ILogger<MicrosoftGraphO365Client> logger)
        {
            _clientsettings = clientSettings.Value;
            _emailSettings = emailSettings.Value;
            _client = httpClient;
            _logger = logger;

            _accessToken = accessTokenStrategy.AccessToken;

            ChannelName = "O365";
        }


        public async Task<(bool, ServiceError)> SendMailAsync(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var emailRequest = new EmailApiReqest(_clientsettings, _emailSettings, request, _client, _logger, _accessToken, $"/v1.0/users/{_clientsettings.Username}/sendMail");
            var isSent = await emailRequest.GetResponse(cancellationToken);
            return isSent;
        }

        public async Task<(bool, ServiceError)> SendReadinessCheckAsync(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var emailRequest = new ReadinessCheckEmailApiRequest(_clientsettings, _emailSettings, request, _client, _logger, _accessToken, $"/v1.0/users/{_clientsettings.Username}/sendMail");
            var isSent = await emailRequest.GetResponse(cancellationToken);
            return isSent;
        }

    }
}
