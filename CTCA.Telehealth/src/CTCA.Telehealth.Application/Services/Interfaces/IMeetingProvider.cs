using System;
using System.Threading;
using System.Threading.Tasks;
using CTCA.Telehealth.Domain.Models;

namespace CTCA.Telehealth.Application.Services.Interfaces
{
    public interface IMeetingProvider
    {
        Task<Domain.Models.TelehealthAppointment> ScheduleMeetingAsync(TelehealthAppointment request, CancellationToken cancellationToken);

        Task<TelehealthAppointment> ScheduleReadinessCheckAsync(TelehealthAppointment request, CancellationToken cancellationToken);

        Task<bool> CancelMeetingAsync(TelehealthAppointment request, CancellationToken cancellationToken);
    }
}
