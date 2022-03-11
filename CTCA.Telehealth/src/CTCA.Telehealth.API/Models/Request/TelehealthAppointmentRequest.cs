using System;
namespace CTCA.Telehealth.API.Models.Request
{
    public class TelehealthAppointmentRequest
    {
        public DateTimeOffset AppointmentStartTime { get; set; }

        public string AppointmentTimeZone { get; set; }

        public int AppointmentDuration { get; set; }

        public string AppointmentOrganizerEmail { get; set; }

        public string AppointmentProviderId { get; set; }

        public string AppointmentProviderName { get; set; }

        public string AppointmentPatientName { get; set; }

        public string AppointmentPatientEmail { get; set; }

        public string AppointmentServiceSource { get; set; }

        public string AppointmentServiceId { get; set; }

        public string PID { get; set; }
    }
}
