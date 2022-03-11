using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Domain.Models;
using MediatR;
using System.Linq;
using LinqKit;

namespace CTCA.Telehealth.Application.Services.Appointment.SearchAppointment
{
    public class AppointmentSearchQuery : IRequest<ObjectResponse<List<TelehealthAppointment>>>
    {
        public Expression<Func<TelehealthAppointment, bool>> SearchQuery { get; set; }
        public string ServiceSourceId { get; set; }

        public AppointmentSearchQuery(string pid, string serviceSource, string serviceSourceId, DateTimeOffset beginDateTime, DateTimeOffset endDateTime)
        {
            ServiceSourceId = serviceSourceId;
            var predicateBuilder = PredicateBuilder.New<TelehealthAppointment>();

            if (!string.IsNullOrEmpty(pid))
                predicateBuilder = predicateBuilder.And(t => t.PatientId == pid);

            if (!string.IsNullOrEmpty(serviceSourceId))
                predicateBuilder = predicateBuilder.And(t => t.Service.SourceServiceId.Equals(serviceSourceId));

            if (beginDateTime != DateTimeOffset.MinValue && endDateTime != DateTimeOffset.MinValue)
                predicateBuilder = predicateBuilder.And(t => t.StartDateTime == beginDateTime && t.EndDateTime == endDateTime);

            SearchQuery = predicateBuilder;
        }

        }
}
