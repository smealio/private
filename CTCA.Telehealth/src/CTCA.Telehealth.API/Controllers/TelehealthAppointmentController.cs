using CTCA.Telehealth.API.Common;
using CTCA.Telehealth.API.Models.Request;
using CTCA.Telehealth.API.Models.Response;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Application.Services.Appointment;
using CTCA.Telehealth.Application.Services.Appointment.SearchAppointment;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.API.Controllers
{
    public class TelehealthAppointmentController : ApiControllerBase
    {
        private readonly ILogger<TelehealthAppointmentController> _logger;

        /// <summary>
        /// </summary>
        /// <param name="serviceProvider">The service provider.</param>
        /// <param name="logger">The logger.</param>
        public TelehealthAppointmentController(IServiceProvider serviceProvider, ILogger<TelehealthAppointmentController> logger, ITelehealthAppointmentRepository repository)
            : base(serviceProvider)
        {
            _logger = logger;
        }

        [HttpPost("api/telehealthappointment")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(StatusCodes.Status501NotImplemented)]
        public async Task<IActionResult> Post([FromBody] TelehealthAppointmentRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var telApt = Mapper.Map<TelehealthAppointment>(request);

                var cmd = new AppointmentCommand(telApt);
                var rsp = await Mediator.Send(cmd, cancellationToken);

                return PrepareResponse<TelehealthAppointment, TelehealthAppointmentResponse>(rsp);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthappointment' POST({request})", request);
                return StatusCode(500, ex.Message);
            }
            
        }

        [HttpPut("api/telehealthappointment")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(StatusCodes.Status501NotImplemented)]
        public async Task<IActionResult> Put([FromBody] TelehealthAppointmentRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var telApt = Mapper.Map<TelehealthAppointment>(request);

                var cmd = new AppointmentUpdateCommand(telApt);
                var rsp = await Mediator.Send(cmd, cancellationToken);

                return PrepareResponse<TelehealthAppointment, TelehealthAppointmentResponse>(rsp);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthappointment' PUT({request})", request);
                return StatusCode(500, ex.Message);
            }
        }

        [HttpPost("api/telehealthAppointment/cancel")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(StatusCodes.Status501NotImplemented)]
        public async Task<IActionResult> Put([FromBody] AppointmentCancelRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var cmd = new AppointmentCancelCommand(request);
                var rsp = await Mediator.Send(cmd, cancellationToken);
                return StatusCode(rsp.Item1, rsp.Item2);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthAppointment/cancel' PUT({request})", request);
                return StatusCode(500, ex.Message);
            }
        }

        

        [HttpPost("api/telehealthAppointment/search")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> Post([FromBody] AppointmentSearchRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var cmd = new AppointmentSearchQuery(request.PID, request.ServiceSource, request.ServiceSourceId, request.BeginDateTime, request.EndDateTime);
                var rsp = await Mediator.Send(cmd, cancellationToken);

                if (rsp.Errors.Count > 0 && rsp.Errors.First() != null)
                {
                    return StatusCode(rsp.Errors.First().Code, rsp);
                }
                else
                {
                    return Ok(rsp);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthAppointment/search' POST({request})", request);
                return StatusCode(500, ex.Message);
            }
        }


    }
}
