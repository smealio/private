using CTCA.Telehealth.Application.Models;
using CTCA.Telehealth.Domain.Models;
using CTCA.Telehealth.Shared;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace CTCA.Telehealth.Application.Services.Interfaces
{
    public interface ICalendarProvider
    {
        string ChannelName { get; }

        Task<(Domain.Models.TelehealthAppointment, ServiceError)> ScheduleEvent(Domain.Models.TelehealthAppointment request, CancellationToken cancellationToken);

        Task<(TelehealthAppointment, ServiceError)> ScheduleReadinessCheck(TelehealthAppointment request, CancellationToken cancellationToken);
        Task<(bool, ServiceError)> DeleteEventAsync(Domain.Models.TelehealthAppointment request, CancellationToken cancellationToken);
    }
}
