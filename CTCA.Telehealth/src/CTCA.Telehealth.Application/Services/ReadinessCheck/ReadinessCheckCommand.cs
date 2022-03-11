using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Domain.Models;
using MediatR;

namespace CTCA.Telehealth.Application.Services.ReadinessCheck
{
    public class ReadinessCheckCommand : IRequest<ObjectResponse<TelehealthAppointment>>
    {
        public TelehealthAppointment ReadinessCheckRequest { get; }
        public ReadinessCheckCommand(TelehealthAppointment readinessCheckRequest)
        {
            ReadinessCheckRequest = readinessCheckRequest;
        }
    }
}
