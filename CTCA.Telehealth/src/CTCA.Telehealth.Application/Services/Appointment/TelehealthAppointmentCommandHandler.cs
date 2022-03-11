using AutoMapper;
using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Application.Common.Exceptions;
using CTCA.Telehealth.Application.Services.Interfaces;
using CTCA.Telehealth.Domain.Interfaces;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Shared;
using MediatR;
using Microsoft.Azure.Cosmos;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Runtime.InteropServices;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Application.Services.Appointment
{
    public class AppointmentCommandHandler : Common.RequestHandler<AppointmentCommand, ObjectResponse<TelehealthAppointment>>
    {
        private readonly IMediator _mediator;
        private readonly IMapper _mapper;
        private readonly IMeetingProvider _meetingProvider;
        private readonly ITelehealthAppointmentRepository _repoProvider;
        private readonly IMailProvider _mailProvider;

        //Leaving this here so that we can rip and repalce when SES goes live
        //private readonly ICalendarProvider _calendarProvider;
        private readonly IServiceProvider _services;

        public AppointmentCommandHandler(IMediator mediator, IMapper mapper, ILogger<AppointmentCommandHandler> logger, IMeetingProvider meetingProvider, IMailProvider mailProvider,
            IServiceProvider services, ITelehealthAppointmentRepository repoProvider
            ) : base(logger)
        {
            _mediator = mediator;
            _mapper = mapper;
            _meetingProvider = meetingProvider;
            _mailProvider = mailProvider;
            //_calendarProvider = calendarProvider;
            _repoProvider = repoProvider;
            _services = services;
        }

        /// <summary>
        /// This method is the work
        /// </summary>
        /// <returns></returns>
        protected override async Task<ObjectResponse<TelehealthAppointment>> HandleRequest(AppointmentCommand request, CancellationToken cancellationToken)
        {

            Logger.LogTrace("Processing request '{requestType}'", request.GetType());
            var sw = Stopwatch.StartNew();
            var serviceErrors = new List<ServiceError>();

            TelehealthAppointment telAppt = new TelehealthAppointment();
            try
            {
                //Set Create Data and Time to Central Standard Time 
                //@@@
                if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
                {
                    request.AppointmentRequest.CreatedDT = TimeZoneInfo.ConvertTimeFromUtc(DateTime.UtcNow, TimeZoneInfo.FindSystemTimeZoneById("Central Standard Time"));
                    request.AppointmentRequest.ModifiedDT = request.AppointmentRequest.CreatedDT;
                }
                else if (RuntimeInformation.IsOSPlatform(OSPlatform.Linux))
                {
                    request.AppointmentRequest.CreatedDT = TimeZoneInfo.ConvertTimeFromUtc(DateTime.UtcNow, TimeZoneInfo.FindSystemTimeZoneById("America/Chicago"));
                    request.AppointmentRequest.ModifiedDT = request.AppointmentRequest.CreatedDT;
                }
                else
                {
                    throw new NotImplementedException("OS Platform is not supported.");
                }
                //Create an online meeting on behalf of a user by using the object ID (OID) in the user token.
                telAppt = await _meetingProvider.ScheduleMeetingAsync(request.AppointmentRequest, cancellationToken);

                // Create an event in the user's default calendar or specified calendar.
                var o365Client = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("O365"));
                var o365Response = await o365Client.ScheduleEvent(telAppt, cancellationToken);
                
                if (string.IsNullOrEmpty(o365Response.Item1.CalendarID))
                {
                    Logger.LogWarning("3001", "There was a problem hitting o365 mailbox, failing over to exchange");

                    Logger.LogTrace($"Running {request.AppointmentRequest.PatientEmail} against EWS");
                    serviceErrors.Add(o365Response.Item2);

                    var ewsClient = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("EWS"));
                    var ewsResponse = await ewsClient.ScheduleEvent(telAppt, cancellationToken);
                    if(string.IsNullOrEmpty(ewsResponse.Item1.CalendarID))
                    {
                        Logger.LogWarning("Telehealth appointment could not be scheduled with O365 or EWS.");
                        serviceErrors.Add(ewsResponse.Item2);
                        return new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.InternalServerError);
                    }
                    else
                    {
                        telAppt = ewsResponse.Item1;
                    }
                }
                else
                {
                    telAppt = o365Response.Item1;
                }
                

                //Add appt. to cosmos repo
                await _repoProvider.AddAsync(telAppt);
                Logger.LogTrace($"Added {telAppt.id} to repository");

                //Send patient appt. email
                var mailResponse = await _mailProvider.SendMailAsync(telAppt, cancellationToken);
                if (!mailResponse.Item1)
                {
                    serviceErrors.Add(mailResponse.Item2);
                    var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.InternalServerError);
                    objectResponse.IsPartialSuccess = true;
                    return objectResponse;
                }
                Logger.LogTrace($"Sent email to {request.AppointmentRequest.PatientEmail}");

                //appt.created successfully
                return new ObjectResponse<TelehealthAppointment>(telAppt);
            }
            catch (EntityAlreadyExistsException ex)
            {
                Logger.LogError($"EntityAlreadyExistsException Faced: {ex.Message}\r{request.AppointmentRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                serviceErrors.Add(new ServiceError() {Code = (int)HttpStatusCode.BadRequest, Message = ex.Message });
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.BadRequest);
                return objectResponse;
            }
            catch (CosmosException ex)
            {
                Logger.LogError($"CosmosException Faced: {ex.Message}\r{request.AppointmentRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                serviceErrors.Add(new ServiceError() 
                {
                    Code = (int)ex.StatusCode,
                    Message = $"Cosmos Error: Message: {ex.Message}; SubStatusCode: {ex.SubStatusCode}; InnerException: {ex.InnerException}"
                });
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.BadRequest);
                return objectResponse;
            }
            catch (WebException ex)
            {
                Logger.LogError($"WebException Faced: {ex.Message}\r{request.AppointmentRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                var statusCode = ex.Response.GetType().GetProperty("StatusCode").GetValue(ex.Response);
                var statusCodeNumber = (int)System.Enum.Parse(typeof(HttpStatusCode), statusCode.ToString(), true);
                serviceErrors.Add(new ServiceError() { Code = statusCodeNumber, Message = $"Error calling EWS. Status: {ex.Status}; Message: {ex.Message}" });
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.NotImplemented);
                return objectResponse;
            }
            catch (NotImplementedException cmdException)
            {
                Logger.LogError($"NotImplementedException Faced: {cmdException.Message}\r{request.AppointmentRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                serviceErrors.Add(new ServiceError()
                {
                    Code = (int)HttpStatusCode.NotImplemented,
                    Message = cmdException.Message
                });
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.NotImplemented);
                return objectResponse;
            }
            catch (Exception ex)
            {
                Logger.LogError($"Exception Faced: {ex.Message}\r{request.AppointmentRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                serviceErrors.Add(new ServiceError()
                {
                    Code = (int)HttpStatusCode.InternalServerError,
                    Message = ex.Message
                });
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.InternalServerError);
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
    

