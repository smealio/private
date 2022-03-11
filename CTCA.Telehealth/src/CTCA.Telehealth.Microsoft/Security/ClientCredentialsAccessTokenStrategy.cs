using System;
using System.Collections.Generic;
using System.Net.Http;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Security
{
    public class ClientCredentialsAccessTokenStrategy : IClientAccessTokenStrategy
    {
        private readonly MicrosoftGraphApiClientSettings _clientSettings;
        private readonly HttpClient _httpClient;
        private readonly ILogger<ClientCredentialsAccessTokenStrategy> _logger;

        public MicrosoftAccessToken AccessToken { get; set; }


        public ClientCredentialsAccessTokenStrategy(IOptions<MicrosoftGraphApiClientSettings> clientSettings, IHttpClientFactory httpClientFactory, ILogger<ClientCredentialsAccessTokenStrategy> logger)
        {
            _clientSettings = clientSettings.Value;
            _httpClient = httpClientFactory.CreateClient();
            _logger = logger;

            SetAccessTokenAsync();
        }


        public void ClearAccessToken()
        {
            AccessToken = null;
        }

        public void RefreshAccessTokenAsync()
        {
            ClearAccessToken();
            SetAccessTokenAsync();
        }

        public void SetAccessTokenAsync()
        {
            GetAccessToken();
        }

        public void GetAccessToken()
        {
            var formData = new Dictionary<string, string>
            {
                
                ["grant_type"] = "client_credentials",
                ["client_id"] = _clientSettings.ClientId,
                ["client_secret"] = _clientSettings.ClientSecret,
                ["scope"] = _clientSettings.Scope
            };

            var url = _clientSettings.AuthUrl;
            var content = new FormUrlEncodedContent(formData);
            var request = new HttpRequestMessage(HttpMethod.Post, url) { Content = content };

            var response = _httpClient.SendAsync(request);
            var responseBody = response.Result.Content.ReadAsStringAsync().Result;

            if (!response.Result.IsSuccessStatusCode)
                throw new HttpRequestException(responseBody);

            var accessToken = JsonConvert.DeserializeObject<MicrosoftAccessToken>(responseBody);
            AccessToken = accessToken;

        }
    }
}

