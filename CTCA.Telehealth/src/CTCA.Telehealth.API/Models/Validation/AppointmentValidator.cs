using System;
using CTCA.Telehealth.API.Models.Request;
using CTCA.Telehealth.Application.Models;
using FluentValidation;

namespace CTCA.Telehealth.API.Models.Validation
{
    public class AppointmentValidator : AbstractValidator<TelehealthAppointmentRequest>
    {
        public AppointmentValidator()
        {
            RuleFor(appt => appt.AppointmentServiceSource).NotEmpty();
            RuleFor(appt => appt.AppointmentServiceId).NotEmpty();
            RuleFor(appt => appt.AppointmentProviderId).NotEmpty();
            RuleFor(appt => appt.PID).NotEmpty();
            RuleFor(appt => appt.AppointmentProviderName).NotEmpty();
            RuleFor(appt => appt.AppointmentPatientName).NotEmpty();
            RuleFor(appt => appt.AppointmentDuration).GreaterThan(0);

            RuleFor(appt => appt.AppointmentTimeZone).NotEmpty();

            RuleFor(appt => appt.AppointmentStartTime).NotEmpty();

            RuleFor(appt => appt.AppointmentPatientEmail).EmailAddress().WithMessage(appt => $"Invalid Patient Email Received: {appt.AppointmentPatientEmail}");
            RuleFor(appt => appt.AppointmentOrganizerEmail).EmailAddress().WithMessage(appt => $"Invalid Provider Email Received: {appt.AppointmentOrganizerEmail}");



        }
    }
}
