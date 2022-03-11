using System;
using CTCA.Telehealth.API.Common;

namespace CTCA.Telehealth.API.Models.Response
{
    public class TelehealthAppointmentResponse : ViewModel
    {

        public string AppointmentServiceSource { get; set; }
        public string AppointmentPatientId { get; set; }
        public string AppointmentProviderId { get; set; }
        public string AppointmentServiceId { get; set; }
        public string TelehealthAppointmentId { get; set; }

    }
}
