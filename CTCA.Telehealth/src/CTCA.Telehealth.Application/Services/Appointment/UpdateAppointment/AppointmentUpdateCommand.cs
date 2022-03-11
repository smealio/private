using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Domain.Models;
using MediatR;

namespace CTCA.Telehealth.Application.Services.Appointment
{
    public class AppointmentUpdateCommand : IRequest<ObjectResponse<TelehealthAppointment>>
    {
        public TelehealthAppointment AppointmentRequest { get; }
        public AppointmentUpdateCommand(TelehealthAppointment appointmentRequest)
        {
            AppointmentRequest = appointmentRequest;
        }
    }
}
