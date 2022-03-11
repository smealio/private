using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Domain.Models;
using LinqKit;
using MediatR;
using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Text;

namespace CTCA.Telehealth.Application.Services.ReadinessCheck.SearchReadinessCheck
{
    public class ReadinessCheckSearchQuery : IRequest<ObjectResponse<List<TelehealthAppointment>>>
    {
        public Expression<Func<TelehealthAppointment, bool>> SearchQuery { get; set; }
        public string ServiceSourceId { get; set; }

        public ReadinessCheckSearchQuery(string pid, string serviceSource, string serviceSourceId, DateTimeOffset beginDateTime, DateTimeOffset endDateTime)
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
