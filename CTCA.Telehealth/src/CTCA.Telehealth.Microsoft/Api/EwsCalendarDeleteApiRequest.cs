using System;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Microsoft.Clients;
using CTCA.Telehealth.Microsoft.Common;
using CTCA.Telehealth.Microsoft.Models.EWS.Create;
using CTCA.Telehealth.Microsoft.Models.EWS.Create.Response;
using CTCA.Telehealth.Microsoft.Models.EWS.Delete;
using CTCA.Telehealth.Shared;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.Microsoft.Api
{
    internal class EwsCalendarDeleteApiRequest : EwsApiRequest<(bool, ServiceError)>
    {

        private readonly TelehealthAppointment _request;
        private readonly ILogger _logger;

        public EwsCalendarDeleteApiRequest(MicrosoftEwsClientSettings clientSettings, TelehealthAppointment request,
           ILogger logger, IConfiguration configuration)
            : base(clientSettings, logger, configuration)
        {
            _request = request;
            _logger = logger;
        }

        public override async Task<(bool, ServiceError)> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false)
        {
            try
            {
                var response = await SendAsync(cancellationToken);

                using (StreamReader rd = new StreamReader(response.GetResponseStream()))
                {
                    var responseObj = Serialization.Deserialize<EwsCreateItemResponse>(rd.ReadToEnd());

                    //return responseObj.Body.CreateItemResponse.ResponseMessages.CreateItemResponseMessage.Items.CalendarItem.ItemId.Id;
                }

                return (true, new ServiceError());
            }
            catch (Exception ex)
            {
                _logger.LogError($"Exception Faced: Error deleting event with EWS.  {ex.Message}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                return (false, new ServiceError() { Code = 500, Message = $"Exception Faced: Error deleting event with EWS.  {ex.Message}" });
            }
            
        }



        protected override object GetPayload()
        {
            var dEvent = new EwsDeleteItem(_request.ProviderEmail, _request.CalendarID);

            return Serialization.SerializeObject<EwsDeleteItem>(dEvent);
        }


    }
}
