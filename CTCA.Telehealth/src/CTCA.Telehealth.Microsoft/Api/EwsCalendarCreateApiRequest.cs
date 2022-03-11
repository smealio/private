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
using CTCA.Telehealth.Shared;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.Microsoft.Api
{
    internal class EwsCalendarCreateApiRequest : EwsApiRequest<(string, ServiceError)>
    {

        private readonly TelehealthAppointment _request;
        private readonly ILogger _logger;

        public EwsCalendarCreateApiRequest(MicrosoftEwsClientSettings clientSettings, TelehealthAppointment request,
           ILogger logger, IConfiguration configuration)
            : base(clientSettings, logger, configuration)
        {
            _request = request;
            _logger = logger;
        }

        public override async Task<(string, ServiceError)> GetResponse(CancellationToken cancellationToken, bool throwOnFailure = false)
        {
            try
            {
                var response = await SendAsync(cancellationToken);

                using (StreamReader rd = new StreamReader(response.GetResponseStream()))
                {
                    var responseObj = Serialization.Deserialize<EwsCreateItemResponse>(rd.ReadToEnd());

                    return (responseObj.Body.CreateItemResponse.ResponseMessages.CreateItemResponseMessage.Items.CalendarItem.ItemId.Id, new ServiceError());
                }
            }
            catch (Exception ex)
            {
                _logger.LogError($"Exception Faced: Error creating event with EWS.  {ex.Message}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                return (string.Empty, new ServiceError() 
                {
                    Code = 500,
                    Message = $"Exception Faced: Error creating event with EWS.  {ex.Message}"
                });
            }
            

        }



        protected override object GetPayload()
        {
            var mEvent = new EwsCreateItem(
                _request.ProviderEmail,
                "CTCA Telehealth Appointment",
                $"<html><center><p><h3>{_request.PatientName}<br />" +
                    $"<h4>{_request.StartDateTime.ToString("dddd, MMMM dd, yyyy")}<br /><br />" +
                    $"{Regex.Replace(HttpUtility.UrlDecode(_request.JoinContent.Replace("data:text/html,", "")), @"\r\n?|\n?|\t", "")}" +
                    $"</center></html>",
                _request.StartDateTime.ToString("yyyy-MM-ddTHH:mm:ss"),
                _request.EndDateTime.ToString("yyyy-MM-ddTHH:mm:ss"),
                "Microsoft Teams Virtual Visit",
                _request.AppointmentTimeZone);

            return Serialization.SerializeObject<EwsCreateItem>(mEvent);
        }


    }
}
