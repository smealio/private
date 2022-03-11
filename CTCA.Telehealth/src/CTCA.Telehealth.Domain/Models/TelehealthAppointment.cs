using CTCA.Telehealth.Domain.Models;
using Newtonsoft.Json;
using System;

namespace CTCA.Telehealth.Domain.Models
{
    public class TelehealthAppointment : Entity
    {
       
        public string source { get; set; }
        [JsonProperty("telehealth-meeting-id")]
        public string MeetingId { get; set; }
        [JsonProperty("telehealth-meeting-join-url")]
        public string JoinUrl { get; set; }
        [JsonProperty("telehealth-meeting-join-web-url")]
        public string JoinWebUrl { get; set; }
        [JsonProperty("telehealth-appointment-starttime")]
        public DateTimeOffset StartDateTime { get; set; }
        [JsonProperty("telehealth-appointment-time-zone")]
        public string AppointmentTimeZone { get; set; }
        [JsonProperty("telehealth-appointment-endtime")]
        public DateTimeOffset EndDateTime { get; set; }
        public int AppointmentDuration { get; set; }
        [JsonProperty("telehealth-appointment-patient-name")]
        public string PatientName { get; set; }
        [JsonProperty("telehealth-appointment-patient-email")]
        public string PatientEmail { get; set; }
        [JsonProperty("telehealth-appointment-patient-id")]
        public string PatientId { get; set; }
        public string ProviderEmail { get; set; } //Partition Key in Cosmos
        public string ProviderName { get; set; }
        [JsonProperty("telehealth-meeting-join-content")]
        public string JoinContent { get; set; }
        [JsonProperty("telehealth-audio-info")]
        public AudioInformation audioInfo { get; set; }
        [JsonProperty("telehealth-calendar-source")]
        public string CalendarSource { get; set; }
        [JsonProperty("telehealth-calendar-id")]
        public string CalendarID { get; set; }
        [JsonProperty("telehealth-is-complete")]
        public bool isComplete { get; set; }
        [JsonProperty("telehealth-is-cancelled")]
        public bool isCancelled { get; set; }
        [JsonProperty("telehealth-service")]
        public Service Service { get; set; }

        public bool isReminderSent { get; set; }


    }
    public class AudioInformation
    {
        [JsonProperty("telehealth-audio-info-phone-number")]
        public string PhoneNumber { get; set; }
        [JsonProperty("telehealth-audio-info-conference-id")]
        public string ConferenceId { get; set; }
        [JsonProperty("telehealth-audio-info-dial-url")]
        public string DialInUrl { get; set; }



    }

    public class Service
    {
        [JsonProperty("telehealth-service-source")]
        public string Source { get; set; }
        [JsonProperty("telehealth-service-source-serviceid")]
        public string SourceServiceId { get; set; }
        [JsonProperty("telehealth-service-providerid")]
        public string SourceProviderId { get; set; }
        [JsonProperty("telehealth-source-provider-email")]
        public string SourceProviderEmail { get; set; }
    }


}
