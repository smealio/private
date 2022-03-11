using AutoMapper;
using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Application.Services.Appointment.SearchAppointment;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using MediatR;
using Microsoft.Azure.Cosmos;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Application.Services.ReadinessCheck.SearchReadinessCheck
{
    public class ReadinessCheckSearchQueryHandler : Common.RequestHandler<ReadinessCheckSearchQuery, ObjectResponse<List<TelehealthAppointment>>>
    {
        private readonly IMediator _mediator;
        private readonly IMapper _mapper;
        private readonly ITelehealthAppointmentRepository _repoProvider;
        public ReadinessCheckSearchQueryHandler(IMediator mediator, IMapper mapper, ILogger<ReadinessCheckSearchQueryHandler> logger, ITelehealthAppointmentRepository repoProvider) : base(logger)
        {
            _mediator = mediator;
            _mapper = mapper;
            _repoProvider = repoProvider;
        }

        protected override async Task<ObjectResponse<List<TelehealthAppointment>>> HandleRequest(ReadinessCheckSearchQuery request, CancellationToken cancellationToken)
        {
            var telAppt = new List<TelehealthAppointment>();
            Logger.LogTrace("Processing request '{requestType}'", request.GetType());
            var sw = Stopwatch.StartNew();
            try
            {
                telAppt = await _repoProvider.SearchAsync(request.SearchQuery);

                if (telAppt.Count == 0)
                {
                    Logger.LogWarning($"Zero Readiness Check appointments were found using ServiceSourceId: {request.ServiceSourceId}.");
                    return new ObjectResponse<List<TelehealthAppointment>>(telAppt, ApplicationResult.NotFound, new Shared.ServiceError()
                    {
                        Code = 404,
                        Message = $"Readiness Check Appointment Not Found"
                    });
                }

                return new ObjectResponse<List<TelehealthAppointment>>(telAppt);
            }
            catch (CosmosException ex)
            {
                Logger.LogError($"CosmosException Faced: {ex.Message}\r{request.ServiceSourceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                var objectResponse = new ObjectResponse<List<TelehealthAppointment>>(telAppt, ApplicationResult.BadRequest, new Shared.ServiceError()
                {
                    Code = (int)ex.StatusCode,
                    Message = $"Cosmos Error: Message: {ex.Message}; SubStatusCode: {ex.SubStatusCode}; InnerException: {ex.InnerException}"
                });
                return objectResponse;
            }
            catch (Exception ex)
            {
                Logger.LogError($"Exception Faced: {ex.Message}\r{request.ServiceSourceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                var objectResponse = new ObjectResponse<List<TelehealthAppointment>>(telAppt, ApplicationResult.InternalServerError, new Shared.ServiceError()
                {
                    Code = (int)HttpStatusCode.InternalServerError,
                    Message = ex.Message
                });
                return objectResponse;
            }
            finally
            {
                sw.Stop();
                Logger.LogTrace("Completed {requestType} in {elapsed}", request.GetType(), sw.Elapsed);
            }
        }
    }
}
