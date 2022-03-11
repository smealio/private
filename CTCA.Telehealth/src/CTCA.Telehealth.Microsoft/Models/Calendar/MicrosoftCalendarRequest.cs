using System;
using System.Collections.Generic;

namespace CTCA.Telehealth.Microsoft.Models.Calendar
{

    public class Body
    {

        /// <summary>
        /// 
        /// </summary>
        public string contentType { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string content { get; set; }
    }

    public class Start
    {

        /// <summary>
        /// 
        /// </summary>
        public DateTime dateTime { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string timeZone { get; set; }
    }

    public class End
    {

        /// <summary>
        /// 
        /// </summary>
        public DateTime dateTime { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string timeZone { get; set; }
    }

    public class Location
    {

        /// <summary>
        /// 
        /// </summary>
        public string displayName { get; set; }
        public string locationType { get; set; }
        public string uniqueId { get; set; }
        public string  uniqueIdType { get; set; }
    }

    public class EmailAddress
    {

        /// <summary>
        /// 
        /// </summary>
        public string address { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string name { get; set; }
    }

    public class AttendeesItem
    {

        /// <summary>
        /// 
        /// </summary>
        public EmailAddress emailAddress { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string type { get; set; }
    }

    public class MicrosoftCalendarRequest
    {

        /// <summary>
        /// 
        /// </summary>
        public string subject { get; set; }
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
        public List<AttendeesItem> attendees { get; set; }

        public List<Location> locations { get; set; }
    }



}
