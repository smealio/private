using CTCA.Telehealth.API.Common;
using CTCA.Telehealth.API.Models.Request;
using CTCA.Telehealth.API.Models.Response;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Application.Services.ReadinessCheck;
using CTCA.Telehealth.Application.Services.ReadinessCheck.CancelReadinessCheck;
using CTCA.Telehealth.Application.Services.ReadinessCheck.SearchReadinessCheck;
using CTCA.Telehealth.Application.Services.ReadinessCheck.UpdateReadinessCheck;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.API.Controllers
{
    public class ReadinessCheckController : ApiControllerBase
    {
        private readonly ILogger<ReadinessCheckController> _logger;

        public ReadinessCheckController(IServiceProvider serviceProvider, ILogger<ReadinessCheckController> logger, ITelehealthAppointmentRepository repository)
            : base(serviceProvider)
        {
            _logger = logger;
        }

        [HttpPost("api/telehealthreadinesscheck")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(StatusCodes.Status501NotImplemented)]
        public async Task<IActionResult> ReadinessCheck([FromBody] TelehealthAppointmentRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var telApt = Mapper.Map<TelehealthAppointment>(request);

                var cmd = new ReadinessCheckCommand(telApt);
                var rsp = await Mediator.Send(cmd, cancellationToken);

                return PrepareResponse<TelehealthAppointment, TelehealthAppointmentResponse>(rsp);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthreadinesscheck' ReadinessCheck({request})", request);
                return StatusCode(500, ex.Message);
            }

        }

        [HttpPut("api/telehealthreadinesscheck")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(StatusCodes.Status501NotImplemented)]
        public async Task<IActionResult> UpdateReadinessCheck([FromBody] TelehealthAppointmentRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var telApt = Mapper.Map<TelehealthAppointment>(request);

                var cmd = new ReadinessCheckUpdateCommand(telApt);
                var rsp = await Mediator.Send(cmd, cancellationToken);

                return PrepareResponse<TelehealthAppointment, TelehealthAppointmentResponse>(rsp);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthreadinesscheck' UpdateReadinessCheck({request})", request);
                return StatusCode(500, ex.Message);
            }
        }

        [HttpPost("api/telehealthreadinesscheck/cancel")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        [ProducesResponseType(StatusCodes.Status501NotImplemented)]
        public async Task<IActionResult> CancelReadinessCheck([FromBody] AppointmentCancelRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var cmd = new ReadinessCheckCancelCommand(request);
                var rsp = await Mediator.Send(cmd, cancellationToken);
                return StatusCode(rsp.Item1, rsp.Item2);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unhandled error in 'api/telehealthreadinesscheck/cancel' CancelReadinessCheck({request})", request);
                return StatusCode(500, ex.Message);
            }
        }



        [HttpPost("api/telehealthreadinesscheck/search")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> SearchReadinessCheck([FromBody] AppointmentSearchRequest request, CancellationToken cancellationToken)
        {
            try
            {
                var cmd = new ReadinessCheckSearchQuery(request.PID, request.ServiceSource, request.ServiceSourceId, request.BeginDateTime, request.EndDateTime);
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
                _logger.LogError(ex, "Unhandled error in 'api/telehealthreadinesscheck/search' SearchReadinessCheck({request})", request);
                return StatusCode(500, ex.Message);
            }
        }
    }
}
