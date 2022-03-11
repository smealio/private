using System;
namespace CTCA.Telehealth.API.Models.Request
{
    public class AppointmentCancelRequest
    {
        public string AppointmentServiceSource { get; set; }

        public string AppointmentServiceId { get; set; }

        public string PID { get; set; }

    }
}
