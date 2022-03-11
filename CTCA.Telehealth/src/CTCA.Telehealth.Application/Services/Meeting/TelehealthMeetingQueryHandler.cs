using System;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using AutoMapper;
using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Application.Common.Exceptions;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using MediatR;
using Microsoft.Azure.Cosmos;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.Application.Services.Meeting
{
    public class TelehealthMeetingQueryHandler : Common.RequestHandler<TelehealthMeetingQuery, ObjectResponse<TelehealthAppointment>>
    {
        private readonly IMediator _mediator;
        private readonly IMapper _mapper;
        private readonly ITelehealthAppointmentRepository _repoProvider;

        public TelehealthMeetingQueryHandler(IMediator mediator, IMapper mapper, ILogger<TelehealthMeetingQueryHandler> logger, ITelehealthAppointmentRepository repoProvider) : base(logger)
        {
            _mediator = mediator;
            _mapper = mapper;
            _repoProvider = repoProvider;
        }

        /// <summary>
        /// This method is the work
        /// </summary>
        /// <returns></returns>
        protected override async Task<ObjectResponse<TelehealthAppointment>> HandleRequest(TelehealthMeetingQuery request, CancellationToken cancellationToken)
        {
            Logger.LogTrace("Processing request '{requestType}'", request.GetType());
            var sw = Stopwatch.StartNew();
            TelehealthAppointment telAppt = new TelehealthAppointment();
            try
            {
                telAppt = await _repoProvider.GetByIdAsync(request.Id);

                if (telAppt == null)
                {
                    Logger.LogWarning($"Telehealth appointment not found with Id: {request.Id}.");
                    return new ObjectResponse<TelehealthAppointment>(new TelehealthAppointment(), ApplicationResult.NotFound, new Shared.ServiceError()
                    {
                        Code = (int)HttpStatusCode.NotFound,
                        Message = $"Telehealth appointment not found with Id: {request.Id}."
                    });
                }

                var response = new ObjectResponse<TelehealthAppointment>(telAppt);

                return response;
            }
            catch (CosmosException ex)
            {
                Logger.LogError($"CosmosException Faced: {ex.Message}\r{request.Id}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, ApplicationResult.BadRequest, new Shared.ServiceError()
                {
                    Code = (int)ex.StatusCode,
                    Message = $"Cosmos Error: Message: {ex.Message}; SubStatusCode: {ex.SubStatusCode}; InnerException: {ex.InnerException}"
                });
                return objectResponse;
            }
            catch (Exception ex)
            {
                Logger.LogError($"Exception Faced: {ex.Message}\r{request.Id}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, ApplicationResult.InternalServerError, new Shared.ServiceError()
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
