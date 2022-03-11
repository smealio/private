using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace CTCA.Telehealth.Microsoft.Models.Calendar
{
    public class ResponseStatus
    {

        /// <summary>
        /// 
        /// </summary>
        public string response { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string time { get; set; }
    }

     


    public class LocationsItem
    {

        /// <summary>
        /// 
        /// </summary>
        public string displayName { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string locationType { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string uniqueId { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string uniqueIdType { get; set; }
    }

    public class Status
    {

        /// <summary>
        /// 
        /// </summary>
        public string response { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string time { get; set; }
    }

  

   

    public class Organizer
    {

        /// <summary>
        /// 
        /// </summary>
        public EmailAddress emailAddress { get; set; }
    }

    public class MicrosoftCalendarResponse
    {

        /// <summary>
        /// 
        /// </summary>
        [JsonProperty("@odata.context")]
        public string context{ get; set; }
        /// <summary>
        /// 
        /// </summary>
        [JsonProperty("@odata.etag")]
        public string etag { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string id { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string createdDateTime { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string lastModifiedDateTime { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string changeKey { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public List<string> categories { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string originalStartTimeZone { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string originalEndTimeZone { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string iCalUId { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public int reminderMinutesBeforeStart { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string isReminderOn { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string hasAttachments { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string subject { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string bodyPreview { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string importance { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string sensitivity { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string isAllDay { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string isCancelled { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string isOrganizer { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string responseRequested { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string seriesMasterId { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string showAs { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string type { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string webLink { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string onlineMeetingUrl { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string recurrence { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public ResponseStatus responseStatus { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public Body body { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public Start start { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public End end { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public Location location { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public List<LocationsItem> locations { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public List<AttendeesItem> attendees { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public Organizer organizer { get; set; }
    }


}
