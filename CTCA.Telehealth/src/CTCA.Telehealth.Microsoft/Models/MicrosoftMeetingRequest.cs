using System;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Models
{
    public class MicrosoftMeetingRequest
    {
        [JsonProperty("startDateTime")]
        public DateTimeOffset _startDateTime { get; set; }

        [JsonProperty("endDateTime")]
        public DateTimeOffset _endDateTime { get; set; }

        [JsonProperty("subject")]
        public string _subject { get; set; }

       
    }
}
