using System;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.API.Common;
using CTCA.Telehealth.API.Models.Response;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Application.Services.Appointment;
using CTCA.Telehealth.Application.Services.Meeting;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using Microsoft.AspNet.OData;
using Microsoft.AspNet.OData.Routing;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.API.Controllers
{

    public class MeetingsController : ApiControllerBase
    {
        private readonly ILogger<MeetingsController> _logger;

        /// <summary>
        /// </summary>
        /// <param name="serviceProvider">The service provider.</param>
        /// <param name="logger">The logger.</param>
        public MeetingsController(IServiceProvider serviceProvider, ILogger<MeetingsController> logger, ITelehealthAppointmentRepository repository)
            : base(serviceProvider)
        {
            _logger = logger;
        }

        [HttpGet("api/telehealthMeetings/{id}")]
        [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(TelehealthMeetingsViewModel))]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> Get([FromRoute] string  id, CancellationToken cancellationToken)
        {
            try
            {
                var cmd = new TelehealthMeetingQuery(id);
                var rsp = await Mediator.Send(cmd, cancellationToken);

                return PrepareResponse<TelehealthAppointment, TelehealthMeetingsViewModel>(rsp);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthMeetings/{id}' GET({id})", id);
                return StatusCode(500, ex.Message);
            }
            
        }

    }
}
