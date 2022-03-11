using System;
using System.Threading;
using System.Threading.Tasks;
using Azure.Communication.Identity;
using CTCA.Telehealth.Application.Services.Interfaces;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.Microsoft.Clients.ACS
{
    public class MicrosoftAcsClient : ICommunicationProvider
    {
        private readonly ILogger<MicrosoftAcsClient> _logger;
        private readonly CommunicationIdentityClient _client;

        public MicrosoftAcsClient(CommunicationIdentityClient client)
        {
            _client = client;
            //_logger = logger;
        }

        public async Task<object> ProvideAccessToken(CancellationToken cancellationToken)
        {
            
            var identityResponse = await _client.CreateUserAsync(cancellationToken);
            var identity = identityResponse.Value;

            //Console.WriteLine($"\nCreated an identity with ID: {identity.Id}");
            //_logger.LogTrace("\nCreated an identity with ID: {identity.Id}");

            // Issue an access token with the "voip" scope for an identity
            var tokenResponse = await _client.GetTokenAsync(identity, scopes: new[] { CommunicationTokenScope.VoIP });
            var token = tokenResponse.Value.Token;
            var expiresOn = tokenResponse.Value.ExpiresOn;

            //Console.WriteLine($"\nIssued an access token with 'voip' scope that expires at {expiresOn}:");
            //Console.WriteLine(token);
            //_logger.LogTrace($"\nIssued an access token with 'voip' scope that expires at {expiresOn}:");

            return tokenResponse;
        }
    }
}
