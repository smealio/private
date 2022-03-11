namespace CTCA.Telehealth.Microsoft.Models.Email
{
    using System;
    using System.Collections.Generic;

    using System.Globalization;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    public partial class MicrosoftMailRequest
    {
        [JsonProperty("message")]
        public Message Message { get; set; }

        [JsonProperty("saveToSentItems")]
        //[JsonConverter(typeof(ParseStringConverter))]
        public bool SaveToSentItems { get; set; }
    }

    public partial class Message
    {
        [JsonProperty("subject")]
        public string Subject { get; set; }

        [JsonProperty("body")]
        public Body Body { get; set; }

        [JsonProperty("toRecipients")]
        public List<Recipient> ToRecipients { get; set; }

        //[JsonProperty("attachments")]
        //public List<Attachment> Attachments { get; set; }


    }

    public partial class Attachment
    {
        [JsonProperty("@odata.type")]
        public string OdataType { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("contentType")]
        public string ContentType { get; set; }

        [JsonProperty("contentBytes")]
        public string ContentBytes { get; set; }
    }

    public partial class Body
    {
        [JsonProperty("contentType")]
        public string ContentType { get; set; }

        [JsonProperty("content")]
        public string Content { get; set; }
    }

    public partial class Recipient
    {
        [JsonProperty("emailAddress")]
        public EmailAddress EmailAddress { get; set; }
    }

    public partial class EmailAddress
    {
        [JsonProperty("address")]
        public string Address { get; set; }
    }

 
}
