using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Api;
using CTCA.Telehealth.Shared;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Twilio.Clients
{
    public class SendGridClient : IMailProvider
    {
        private readonly TwilioClientSettings _clientsettings;
        private readonly HttpClient _client;
        private readonly ILogger<SendGridClient> _logger;


        public SendGridClient(IOptions<TwilioClientSettings> clientSettings, HttpClient httpClient, ILogger<SendGridClient> logger)
        {
            _clientsettings = clientSettings.Value;
            _client = httpClient;
            _logger = logger;
        }


        public async Task<(bool, ServiceError)> SendMailAsync(TelehealthAppointment request, CancellationToken cancellationToken)
        {
            var emailRequest = new EmailApiReqest(_clientsettings, request, _client, _logger, $"/v1.0/users/{_clientsettings.ApiKey}/sendMail");
            var isSent = await emailRequest.GetResponse(cancellationToken);
            return isSent;
        }

    }
}
