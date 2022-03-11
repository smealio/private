using System;
namespace CTCA.Telehealth.Application.Models
{
    public class AppointmentSearchRequest
    {
        public string PID { get; set; }
        public string ServiceSource { get; set; }
        public string ServiceSourceId { get; set; }
        public DateTimeOffset BeginDateTime { get; set; }
        public DateTimeOffset EndDateTime { get; set; }
    }
}
