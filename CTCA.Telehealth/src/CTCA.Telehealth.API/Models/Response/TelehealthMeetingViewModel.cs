using System;
using CTCA.Telehealth.API.Common;

namespace CTCA.Telehealth.API.Models.Response
{
    public class TelehealthMeetingsViewModel : ViewModel
    {
        public string Id { get; set; }

        public string MeetingId { get; set; }

        public string MeetingJoinUrl { get; set; }

        public string MeetingJoinWebUrl { get; set; }

        public DateTimeOffset AppointmentDateTime { get; set; }

        public string AppointmentTimeZone { get; set; }

        public int AppointmentDuration { get; set; }

        public string AppointmentProviderName { get; set; }
    }
}
