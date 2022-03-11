using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace CTCA.Telehealth.Microsoft.Models.EWS.Delete
{


    [XmlRoot(ElementName = "RequestServerVersion", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class RequestServerVersion
    {
        [XmlAttribute(AttributeName = "Version")]
        public string Version { get; set; }
    }

    [XmlRoot(ElementName = "ConnectingSID", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class ConnectingSID
    {
        [XmlElement(ElementName = "SmtpAddress", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public string SmtpAddress { get; set; }
    }

    [XmlRoot(ElementName = "ExchangeImpersonation", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class ExchangeImpersonation
    {
        [XmlElement(ElementName = "ConnectingSID", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public ConnectingSID ConnectingSID { get; set; }
    }

    [XmlRoot(ElementName = "Header", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
    public class Header
    {
        [XmlElement(ElementName = "RequestServerVersion", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public RequestServerVersion RequestServerVersion { get; set; }
        [XmlElement(ElementName = "ExchangeImpersonation", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public ExchangeImpersonation ExchangeImpersonation { get; set; }
    }

    [XmlRoot(ElementName = "ItemId", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class ItemId
    {
        [XmlAttribute(AttributeName = "Id")]
        public string Id { get; set; }
        [XmlAttribute(AttributeName = "ChangeKey")]
        public string ChangeKey { get; set; }
    }

    [XmlRoot(ElementName = "ItemIds", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
    public class ItemIds
    {
        [XmlElement(ElementName = "ItemId", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public ItemId ItemId { get; set; }
    }

    [XmlRoot(ElementName = "DeleteItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
    public class DeleteItem
    {
        [XmlElement(ElementName = "ItemIds", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
        public ItemIds ItemIds { get; set; }
        [XmlAttribute(AttributeName = "DeleteType")]
        public string DeleteType { get; set; }
        [XmlAttribute(AttributeName = "SendMeetingCancellations")]
        public string SendMeetingCancellations { get; set; }
    }

    [XmlRoot(ElementName = "Body", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
    public class Body
    {
        [XmlElement(ElementName = "DeleteItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
        public DeleteItem DeleteItem { get; set; }
    }

    [XmlRoot(ElementName = "Envelope", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
    public class EwsDeleteItem
    {
        [XmlElement(ElementName = "Header", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
        public Header Header { get; set; }
        [XmlElement(ElementName = "Body", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
        public Body Body { get; set; }
        [XmlAttribute(AttributeName = "xsi", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string Xsi { get; set; }
        [XmlAttribute(AttributeName = "m", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string M { get; set; }
        [XmlAttribute(AttributeName = "t", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string T { get; set; }
        [XmlAttribute(AttributeName = "soap", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string Soap { get; set; }

        public EwsDeleteItem() { }

        public EwsDeleteItem(string impersonateId, string calendarId)
        {

            this.M = "http://schemas.microsoft.com/exchange/services/2006/messages";
            this.T = "http://schemas.microsoft.com/exchange/services/2006/types";
            this.Soap = "http://schemas.xmlsoap.org/soap/envelope/";
            this.Header = new Header
            {
                RequestServerVersion = new RequestServerVersion { Version = "Exchange2010" },
                ExchangeImpersonation = new ExchangeImpersonation
                {
                    ConnectingSID = new ConnectingSID
                    {
                        SmtpAddress = impersonateId
                    }
                }
            };
            this.Body = new Body
            {
                DeleteItem = new DeleteItem
                {
                    DeleteType = "HardDelete",
                    SendMeetingCancellations = "SendToNone",
                    ItemIds = new ItemIds
                    {
                        ItemId = new ItemId
                        {
                            Id = calendarId
                        }
                    }
                }
            };

        }
    }

}
