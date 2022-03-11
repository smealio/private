using System;
using Newtonsoft.Json;

namespace CTCA.Telehealth.API.Models
{
    public class AppointmentResponseViewModel
    {
        public DateTime AppointmentStartTime { get; set; }

        public int AppointmentDuration { get; set; }

        public string  AppointmentOrganizerEmail {get;set;}

        public string AppointmentProviderId { get; set; }

        public string AppointmentPatientName { get; set; }

        public string AppointmentPatientEmail { get; set; }

        public string AppointmentServiceId { get; set; }

        public string PID { get; set; }

    }
}
