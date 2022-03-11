using System;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Twilio;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Common
{
    internal abstract class ApiRequest<TResponseType>
    {
        private readonly TwilioClientSettings _clientSettings;
        private readonly HttpMessageInvoker _client;
        private readonly ILogger _logger;
        private readonly HttpMethod _method;
        private readonly Uri _requestUri;

        protected ApiRequest(
            TwilioClientSettings clientSettings,
            HttpMethod method,
            HttpMessageInvoker client,
            ILogger logger,
            string resource
            )
        {
            //need to populate token here somewhere

            _requestUri = new Uri($"{clientSettings.BaseUrl}{resource}");
            _clientSettings = clientSettings;
            _client = client;
            _logger = logger;
            _method = method;
        }

        /// <summary>
        /// Sends the request and returns the response.
        /// </summary>
        /// <returns></returns>
        public async Task<HttpResponseMessage> SendAsync(CancellationToken cancellationToken)
        {
            var request = GetRequestMessage();
            return await _client.SendAsync(request, cancellationToken);
        }

        public abstract Task<TResponseType> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false);

        /// <summary>
        /// Gets the payload that will be serialized into the request body.
        /// </summary>
        /// <returns></returns>
        protected abstract object GetPayload();

        private HttpRequestMessage GetRequestMessage()
        {
            var payload = GetPayload();
            var json = JsonConvert.SerializeObject(payload, Formatting.None);

            var content = new StringContent(json, Encoding.UTF8, "application/json");
            var httpRequest = new HttpRequestMessage(_method, _requestUri)
            {
                Content = content
            };

            httpRequest.Headers.Authorization = new AuthenticationHeaderValue("Bearer ", _clientSettings.ApiKey);

            return httpRequest;
        }

        public async Task HandleErrorResponse(HttpResponseMessage response)
        {
            if (response == null) return;
            const string message = "Operation failure. ";
            var content = await response.Content.ReadAsStringAsync();
            var errorResponse = JsonConvert.DeserializeObject<ErrorResponse>(content);
            var errorMessages = new StringBuilder();

            if (errorResponse != null && errorResponse.Message.Any())
            {
                errorMessages.AppendLine(string.Join(", ", errorResponse.Message));
                errorMessages.Append(string.Join(", ", errorResponse?.Trace.Select(t => t.Message)));
            }
            else
            {
                errorMessages.Append(content);
            }

            _logger.LogTrace(message + "statusCode: {statusCode}, message: {errorMessages}", errorResponse?.Code, errorMessages);

            // TODO: Convert the codes correctly, this below automatically makes it a 502

            throw new ApplicationException($"{message} {errorMessages}");
        }

    }
}
