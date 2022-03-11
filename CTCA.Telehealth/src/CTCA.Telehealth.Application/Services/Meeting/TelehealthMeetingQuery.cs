using System;
using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Domain.Models;
using MediatR;

namespace CTCA.Telehealth.Application.Services.Meeting
{
    public class TelehealthMeetingQuery : IRequest<ObjectResponse<TelehealthAppointment>>
    {
        public string Id { get; }

        public TelehealthMeetingQuery(string id)
        {
            Id = id;
        }
    }
}
