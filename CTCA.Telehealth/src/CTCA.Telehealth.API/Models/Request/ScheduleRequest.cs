using System;
namespace CTCA.Telehealth.API.Models.Request
{
    public class ScheduleRequest
    {
        public DateTimeOffset StartDateTime { get; set; }
        public DateTimeOffset EndDateTIme { get; set; }
        public HospitalEntity Entity { get; set; }
    }

    public class HospitalEntity
    {
        string id { get; set; }
    }
}
