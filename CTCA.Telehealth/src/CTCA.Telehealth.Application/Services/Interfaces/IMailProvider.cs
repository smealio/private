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
    public interface IMailProvider
    {
        Task<(bool, ServiceError)> SendMailAsync(Domain.Models.TelehealthAppointment telehealthAppointment, CancellationToken cancellationToken);
        Task<(bool, ServiceError)> SendReadinessCheckAsync(TelehealthAppointment request, CancellationToken cancellationToken);
    }
}
