/* 
 Licensed under the Apache License, Version 2.0

 http://www.apache.org/licenses/LICENSE-2.0
 */
using System;
using System.Xml.Serialization;
using System.Collections.Generic;
namespace CTCA.Telehealth.Microsoft.EWS
{

    [XmlRoot(ElementName = "RequestServerVersion", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class RequestServerVersion
    {
        [XmlAttribute(AttributeName = "Version")]
        public string Version { get; set; }
    }

    [XmlRoot(ElementName = "Period", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class Period
    {
        [XmlAttribute(AttributeName = "Bias")]
        public string Bias { get; set; }
        [XmlAttribute(AttributeName = "Name")]
        public string Name { get; set; }
        [XmlAttribute(AttributeName = "Id")]
        public string Id { get; set; }
    }

    [XmlRoot(ElementName = "Periods", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class Periods
    {
        [XmlElement(ElementName = "Period", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Period Period { get; set; }
    }

    [XmlRoot(ElementName = "To", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class To
    {
        [XmlAttribute(AttributeName = "Kind")]
        public string Kind { get; set; }
        [XmlText]
        public string Text { get; set; }
    }

    [XmlRoot(ElementName = "Transition", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class Transition
    {
        [XmlElement(ElementName = "To", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public To To { get; set; }
    }

    [XmlRoot(ElementName = "TransitionsGroup", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class TransitionsGroup
    {
        [XmlElement(ElementName = "Transition", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Transition Transition { get; set; }
        [XmlAttribute(AttributeName = "Id")]
        public string Id { get; set; }
    }

    [XmlRoot(ElementName = "TransitionsGroups", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class TransitionsGroups
    {
        [XmlElement(ElementName = "TransitionsGroup", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public TransitionsGroup TransitionsGroup { get; set; }
    }

    [XmlRoot(ElementName = "Transitions", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class Transitions
    {
        [XmlElement(ElementName = "Transition", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Transition Transition { get; set; }
    }

    [XmlRoot(ElementName = "TimeZoneDefinition", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class TimeZoneDefinition
    {
        [XmlElement(ElementName = "Periods", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Periods Periods { get; set; }
        [XmlElement(ElementName = "TransitionsGroups", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public TransitionsGroups TransitionsGroups { get; set; }
        [XmlElement(ElementName = "Transitions", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Transitions Transitions { get; set; }
        [XmlAttribute(AttributeName = "Name")]
        public string Name { get; set; }
        [XmlAttribute(AttributeName = "Id")]
        public string Id { get; set; }
    }

    [XmlRoot(ElementName = "TimeZoneContext", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class TimeZoneContext
    {
        [XmlElement(ElementName = "TimeZoneDefinition", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public TimeZoneDefinition TimeZoneDefinition { get; set; }
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
        [XmlElement(ElementName = "TimeZoneContext", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public TimeZoneContext TimeZoneContext { get; set; }
        [XmlElement(ElementName = "ExchangeImpersonation", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public ExchangeImpersonation ExchangeImpersonation { get; set; }
    }

    [XmlRoot(ElementName = "Body", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class Body
    {
        [XmlAttribute(AttributeName = "BodyType")]
        public string BodyType { get; set; }
        [XmlText]
        public string Text { get; set; }
    }

    [XmlRoot(ElementName = "Mailbox", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class Mailbox
    {
        [XmlElement(ElementName = "EmailAddress", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public string EmailAddress { get; set; }
    }

    [XmlRoot(ElementName = "Attendee", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class Attendee
    {
        [XmlElement(ElementName = "Mailbox", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Mailbox Mailbox { get; set; }
    }

    [XmlRoot(ElementName = "RequiredAttendees", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class RequiredAttendees
    {
        [XmlElement(ElementName = "Attendee", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Attendee Attendee { get; set; }
    }

    [XmlRoot(ElementName = "CalendarItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
    public class CalendarItem
    {
        [XmlElement(ElementName = "Subject", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public string Subject { get; set; }
        [XmlElement(ElementName = "Body", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public Body Body { get; set; }
        [XmlElement(ElementName = "ReminderDueBy", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public string ReminderDueBy { get; set; }
        [XmlElement(ElementName = "Start", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public string Start { get; set; }
        [XmlElement(ElementName = "End", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public string End { get; set; }
        [XmlElement(ElementName = "Location", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public string Location { get; set; }
        [XmlElement(ElementName = "RequiredAttendees", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public RequiredAttendees RequiredAttendees { get; set; }
    }

    [XmlRoot(ElementName = "Items", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
    public class Items
    {
        [XmlElement(ElementName = "CalendarItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
        public CalendarItem CalendarItem { get; set; }
    }

    [XmlRoot(ElementName = "CreateItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
    public class CreateItem
    {
        [XmlElement(ElementName = "Items", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
        public Items Items { get; set; }
        [XmlAttribute(AttributeName = "SendMeetingInvitations")]
        public string SendMeetingInvitations { get; set; }
    }

    [XmlRoot(ElementName = "Body", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
    public class Body2
    {
        [XmlElement(ElementName = "CreateItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
        public CreateItem CreateItem { get; set; }
    }

    [XmlRoot(ElementName = "Envelope", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
    public class EWSCreateItem
    {
        [XmlElement(ElementName = "Header", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
        public Header Header { get; set; }
        [XmlElement(ElementName = "Body", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
        public Body2 Body2 { get; set; }
        [XmlAttribute(AttributeName = "xsi", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string Xsi { get; set; }
        [XmlAttribute(AttributeName = "m", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string M { get; set; }
        [XmlAttribute(AttributeName = "t", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string T { get; set; }
        [XmlAttribute(AttributeName = "soap", Namespace = "http://www.w3.org/2000/xmlns/")]
        public string Soap { get; set; }

        public EWSCreateItem() { }
        public EWSCreateItem(string impersonateId, string subject, string body, string start, string end, string location)
        {
            this.M = "http://schemas.microsoft.com/exchange/services/2006/messages";
            this.T = "http://schemas.microsoft.com/exchange/services/2006/types";
            this.Soap = "http://schemas.xmlsoap.org/soap/envelope/";
            this.Header = new Header
            {
                RequestServerVersion = new RequestServerVersion { Version = "Exchange2010" },
                TimeZoneContext = new TimeZoneContext
                {
                    TimeZoneDefinition = new TimeZoneDefinition
                    {
                        Name = "(UTC-07:00) Arizona",
                        Id = "US Mountain Standard Time",
                        Periods = new Periods
                        {
                            Period = new Period
                            {
                                Bias = "P0DT7H0M0.0S",
                                Name = "Standard",
                                Id = "Std"
                            }
                        },
                        TransitionsGroups = new TransitionsGroups
                        {
                            TransitionsGroup = new TransitionsGroup
                            {
                                Id = "0",
                                Transition = new Transition
                                {
                                    To = new To
                                    {
                                        Kind = "Period",
                                        Text = "Std"
                                    }
                                }
                            }
                        },
                        Transitions = new Transitions
                        {
                            Transition = new Transition
                            {
                                To = new To
                                {
                                    Kind = "Group",
                                    Text = "0"
                                }
                            }
                        }
                    }
                },
                ExchangeImpersonation = new ExchangeImpersonation
                {
                    ConnectingSID = new ConnectingSID
                    {
                        SmtpAddress = impersonateId
                    }
                }
            };
            this.Body2 = new Body2
            {
                CreateItem = new CreateItem
                {
                    SendMeetingInvitations = "SendToNone",
                    Items = new Items
                    {
                        CalendarItem = new CalendarItem
                        {
                            Subject = subject,
                            Body = new Body
                            {
                                BodyType = "HTML",
                                Text = body
                            },
                            Start = start,
                            End = end,
                            Location = location
                        }
                    }
                }
            };
        }
    }

}
