using System;
using CTCA.Telehealth.Application.Models;
using FluentValidation;

namespace CTCA.Telehealth.API.Models.Validation
{
    public class AppointmentCancelValidator:AbstractValidator<AppointmentCancelRequest>
    {
        public AppointmentCancelValidator()
        {
            RuleFor(appt => appt.AppointmentServiceId).NotEmpty();
            RuleFor(appt => appt.AppointmentServiceSource).NotEmpty();
            RuleFor(appt => appt.PID).NotEmpty();
        }
    }
}
