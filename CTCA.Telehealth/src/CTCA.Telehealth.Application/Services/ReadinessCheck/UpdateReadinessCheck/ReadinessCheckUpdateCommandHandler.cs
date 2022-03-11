using AutoMapper;
using CTCA.Telehealth.Application.Common;
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
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Application.Services.ReadinessCheck.UpdateReadinessCheck
{
    public class ReadinessCheckUpdateCommandHandler : Common.RequestHandler<ReadinessCheckUpdateCommand, ObjectResponse<TelehealthAppointment>>
    {
        private readonly IMediator _mediator;
        private readonly IMapper _mapper;
        private readonly IMeetingProvider _meetingProvider;
        private readonly ITelehealthAppointmentRepository _repoProvider;
        private readonly IMailProvider _mailProvider;

        //Leaving this here so that we can rip and repalce when SES goes live
        //private readonly ICalendarProvider _calendarProvider;
        private readonly IServiceProvider _services;
        public ReadinessCheckUpdateCommandHandler(IMediator mediator, IMapper mapper, ILogger<ReadinessCheckUpdateCommandHandler> logger, IMeetingProvider meetingProvider, IMailProvider mailProvider,
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

        protected override async Task<ObjectResponse<TelehealthAppointment>> HandleRequest(ReadinessCheckUpdateCommand request, CancellationToken cancellationToken)
        {
            Logger.LogTrace("Processing request '{requestType}'", request.GetType());
            var sw = Stopwatch.StartNew();
            var serviceErrors = new List<ServiceError>();

            TelehealthAppointment telAppt = new TelehealthAppointment();
            try
            {
                telAppt = (await _repoProvider.SearchAsync(
                p => p.Service.Source.Equals(request.ReadinessCheckRequest.source) &&
                p.PatientId.Equals(request.ReadinessCheckRequest.PatientId) &&
                p.Service.SourceServiceId.Equals(request.ReadinessCheckRequest.Service.SourceServiceId))).FirstOrDefault();


                if (telAppt == null)
                {
                    return new ObjectResponse<TelehealthAppointment>(new TelehealthAppointment(), ApplicationResult.NotFound, new Shared.ServiceError()
                    {
                        Code = (int)HttpStatusCode.NotFound,
                        Message = "No Readiness Check Appointments Found Matching Source ID"
                    });
                }
                //Delete old calendar

                var o365Client = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("O365"));
                var isDeleted = await o365Client.DeleteEventAsync(telAppt, cancellationToken);

                if (!isDeleted.Item1)
                {
                    serviceErrors.Add(isDeleted.Item2);
                    Logger.LogWarning("3001", "There was a problem hitting o365 mailbox, failing over to exchange");

                    var ewsClient = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("EWS"));
                    isDeleted = await ewsClient.DeleteEventAsync(telAppt, cancellationToken);

                    if (!isDeleted.Item1)
                    {
                        serviceErrors.Add(isDeleted.Item2);

                        return new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.InternalServerError);
                    }

                }

                //check for changed provider id
                if (telAppt.Service.SourceProviderId != request.ReadinessCheckRequest.Service.SourceProviderId)
                {
                    telAppt.Service.SourceProviderId = request.ReadinessCheckRequest.Service.SourceProviderId;

                }

                //check for changed date time
                if (telAppt.StartDateTime != request.ReadinessCheckRequest.StartDateTime)
                    telAppt.StartDateTime = request.ReadinessCheckRequest.StartDateTime;


                //check for changed duration
                if (telAppt.AppointmentDuration != request.ReadinessCheckRequest.AppointmentDuration)
                {
                    telAppt.AppointmentDuration = request.ReadinessCheckRequest.AppointmentDuration;
                }

                //update meeting end date time
                telAppt.EndDateTime = request.ReadinessCheckRequest.StartDateTime.AddMinutes(request.ReadinessCheckRequest.AppointmentDuration);

                //schedule appointment
                o365Client = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("O365"));
                var o365Response = await o365Client.ScheduleReadinessCheck(telAppt, cancellationToken);
                if (o365Response.Item1.CalendarID != null)
                {
                    telAppt.CalendarID = o365Response.Item1.CalendarID;
                }
                else
                {
                    Logger.LogWarning("3001", "There was a problem hitting o365 mailbox, failing over to exchange");
                    serviceErrors.Add(o365Response.Item2);
                    var ewsClient = _services.GetServices<ICalendarProvider>().First(s => s.ChannelName.Equals("EWS"));
                    var ewsResponse = await ewsClient.ScheduleReadinessCheck(telAppt, cancellationToken);
                    if (ewsResponse.Item1.CalendarID != null)
                    {
                        telAppt.CalendarID = ewsResponse.Item1.CalendarID;
                    }
                    else
                    {
                        Logger.LogWarning("Readiness Check appointment could not be scheduled with O365 or EWS.");
                        serviceErrors.Add(ewsResponse.Item2);
                        return new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.InternalServerError);
                    }
                }

                //Set Create Data and Time to Central Standard Time 
                //@@@
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

                //update appt. in Cosmos
                await _repoProvider.UpdateAsync(telAppt);

                //Send patient updated appt. email
                var mailResponse = await _mailProvider.SendReadinessCheckAsync(telAppt, cancellationToken);
                if (!mailResponse.Item1)
                {
                    serviceErrors.Add(mailResponse.Item2);
                    var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.InternalServerError);
                    objectResponse.IsPartialSuccess = true;
                    return objectResponse;
                }
                Logger.LogTrace($"Sent email to {request.ReadinessCheckRequest.PatientEmail}");

                //appt. was updated successfully
                return new ObjectResponse<TelehealthAppointment>(telAppt);
            }
            catch (Common.Exceptions.ApplicationException ex)
            {
                Logger.LogError($"ApplicationException Faced: {ex.Message}\r{request.ReadinessCheckRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                serviceErrors.Add(new ServiceError()
                {
                    Code = (int)HttpStatusCode.NotFound,
                    Message = ex.Message
                });
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.NotFound);
                return objectResponse;
            }
            catch (CosmosException ex)
            {
                Logger.LogError($"CosmosException Faced: {ex.Message}\r{request.ReadinessCheckRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
                serviceErrors.Add(new ServiceError()
                {
                    Code = (int)ex.StatusCode,
                    Message = $"Cosmos Error: Message: {ex.Message}; SubStatusCode: {ex.SubStatusCode}; InnerException: {ex.InnerException}"
                });
                var objectResponse = new ObjectResponse<TelehealthAppointment>(telAppt, serviceErrors, ApplicationResult.BadRequest);
                return objectResponse;
            }
            catch (NotImplementedException cmdException)
            {
                Logger.LogError($"NotImplementedException Faced: {cmdException.Message}\r{request.ReadinessCheckRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
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
                Logger.LogError($"Exception Faced: {ex.Message}\r{request.ReadinessCheckRequest.Service.SourceServiceId}", System.Reflection.MethodBase.GetCurrentMethod().Name, DateTime.UtcNow.ToLongTimeString());
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
