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
    internal class GraphCalendarDeleteApiRequest : ApiRequest<(bool, ServiceError)>
    {

        private readonly TelehealthAppointment _request;
        private readonly ILogger _logger;

        public GraphCalendarDeleteApiRequest(MicrosoftGraphApiClientSettings clientSettings, TelehealthAppointment request,
            HttpMessageInvoker client, ILogger logger, MicrosoftAccessToken accessToken, HttpMethod method, string resource)
            : base(clientSettings, method, client, logger, accessToken, resource)
        {
            _request = request;
            _logger = logger;
        }

        public override async Task<(bool, ServiceError)> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false)
        {
            var response = await SendAsync(cancellationToken);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError($"Error deleting event with MicrosoftGraphApi. Status Code: {response.StatusCode}; Reason Phrase: {response.ReasonPhrase}");
                return (false, new ServiceError() {Code = (int)response.StatusCode,  Message = $"Error deleting event with MicrosoftGraphApi. Status Code: {(int)response.StatusCode}; Reason Phrase: {response.ReasonPhrase}" });
            }

            return (true, new ServiceError());
        }

        protected override object GetPayload()
        {

            var payload = "";

            return payload;
        }


    }
}
