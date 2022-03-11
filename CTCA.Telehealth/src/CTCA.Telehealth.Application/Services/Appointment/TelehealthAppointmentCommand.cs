using CTCA.Telehealth.Application.Common;
using CTCA.Telehealth.Domain.Models;
using MediatR;

namespace CTCA.Telehealth.Application.Services.Appointment
{
    public class AppointmentCommand : IRequest<ObjectResponse<TelehealthAppointment>>
    {
        public TelehealthAppointment AppointmentRequest { get; }
        public AppointmentCommand(TelehealthAppointment appointmentRequest)
        {
            AppointmentRequest = appointmentRequest;
        }
    }
}
