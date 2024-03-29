﻿using System;
using System.Linq;
using System.Net;
using System.Runtime.InteropServices;
using System.Threading;
using System.Threading.Tasks;
using AutoMapper;
using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using MediatR;
using Microsoft.Azure.Cosmos;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace CTCA.Telehealth.Application.Services.Appointment
{
    public class AppointmentCancelCommandHandler : IRequestHandler<AppointmentCancelCommand, (int, string)>
    {
        private readonly IMediator _mediator;
        private readonly ILogger<AppointmentCancelCommandHandler> _logger;
        private readonly IMapper _mapper;
        private readonly IMeetingProvider _meetingProvider;
        private readonly ITelehealthAppointmentRepository _repoProvider;

        //Leaving this here so that we can rip and repalce when SES goes live
        //private readonly ICalendarProvider _calendarProvider;
        private readonly IServiceProvider _services;

        public AppointmentCancelCommandHandler(IMediator mediator, IMapper mapper, ILogger<AppointmentCancelCommandHandler> logger, IMeetingProvider meetingProvider,
            IServiceProvider services, ITelehealthAppointmentRepository repoProvider
            )
        {
            _mediator = mediator;
            _mapper = mapper;
            _logger = logger;
            _meetingProvider = meetingProvider;
            _services = services;
            //_calendarProvider = calendarProvider;
            _repoProvider = repoProvider;

        }

        /// <summary>
        /// This method is the work
        /// </summary>
        /// <returns></returns>
        public async Task<(int, string)> Handle(AppointmentCancelCommand request, CancellationToken cancellationToken)
        {

            var telAppts = await _repoProvider.SearchAsync(
                p => p.Service.Source.Equals(request.AppointmentCancelRequest.AppointmentServiceSource) &&
                p.PatientId.Equals(request.AppointmentCancelRequest.PID) &&
                p.Service.SourceServiceId.Equals(request.AppointmentCancelRequest.AppointmentServiceId));

            if (telAppts.Count == 0)
            {
                _logger.LogWarning($"Cannot find telehealth appointment with id: {request.AppointmentCancelRequest.AppointmentServiceId}");
                return ((int)HttpStatusCode.NotFound, $"Cannot find telehealth appointment with id: {request.AppointmentCancelRequest.AppointmentServiceId} and patient id: {request.AppointmentCancelRequest.PID}");
            }

            try
            {
                foreach (var telAppt in telAppts)
                {
                    telAppt.isCancelled = true;

                    var o365Client = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("O365"));
                    var isDeleted = await o365Client.DeleteEventAsync(telAppt, cancellationToken);
                    
                    

                    if (!isDeleted.Item1)
                    {
                        _logger.LogWarning("There was a problem hitting o365 mailbox, failing over to exchange");

                        var ewsClient = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("EWS"));
                        isDeleted = await ewsClient.DeleteEventAsync(telAppt, cancellationToken);
                    }
                    

                    if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
                    {
                        telAppt.ModifiedDT = TimeZoneInfo.ConvertTimeFromUtc(DateTime.UtcNow, TimeZoneInfo.FindSystemTimeZoneById("Central Standard Time"));
                    }
                    else if (RuntimeInformation.IsOSPlatform(OSPlatform.Linux))
                    {
                        telAppt.ModifiedDT = TimeZoneInfo.ConvertTimeFromUtc(DateTime.UtcNow, TimeZoneInfo.FindSystemTimeZoneById("America/Chicago"));
                    }
                    else
                    {
                        throw new NotImplementedException("OS Platform is not supported.");
                    }

                    //meeting.delete
                    await _repoProvider.UpdateAsync(telAppt);
                }

                //completed successfully
                var apptTerm = telAppts.Count > 1 ? "appointments were" : "appointment was";
                return (200, $"{telAppts.Count} {apptTerm} cancelled.");
            }
            catch (Common.Exceptions.ApplicationException ex)
            {
                _logger.LogError($"ApplicationException Faced: {ex.Message}\r{request.AppointmentCancelRequest.AppointmentServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                return (404, $"Appointment was not found. Message: {ex.Message}");
            }
            catch (CosmosException ex)
            {
                _logger.LogError($"CosmosException Faced: {ex.Message}\r{request.AppointmentCancelRequest.AppointmentServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                return ((int)ex.StatusCode, $"Cosmos Error: Message: {ex.Message}; SubStatusCode: {ex.SubStatusCode}; InnerException: {ex.InnerException}");
            }
            catch (WebException ex)
            {
                _logger.LogError($"WebException Faced: {ex.Message}\r{request.AppointmentCancelRequest.AppointmentServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                var statusCode = ex.Response.GetType().GetProperty("StatusCode").GetValue(ex.Response);
                var statusCodeNumber = (int)System.Enum.Parse(typeof(HttpStatusCode), statusCode.ToString(), true);
                return (statusCodeNumber, $"Error calling EWS. Status: {ex.Status}; Message: {ex.Message}");
            }
            catch (NotImplementedException ex)
            {
                _logger.LogError($"NotImplementedException Faced: {ex.Message}\r{request.AppointmentCancelRequest.AppointmentServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                return (501, $"Exception Faced: {ex.Message}");
            }
            catch (Exception ex)
            {
                _logger.LogError($"Exception Faced: {ex.Message}\r{request.AppointmentCancelRequest.AppointmentServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                return (500, $"{ex.Message}");
            }
            

            
        }
    }
}

