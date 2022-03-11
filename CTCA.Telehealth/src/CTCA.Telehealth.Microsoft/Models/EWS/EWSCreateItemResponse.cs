using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace CTCA.Telehealth.Microsoft.EWS
{

	[XmlRoot(ElementName = "ServerVersionInfo", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
	public class ServerVersionInfo
	{
		[XmlAttribute(AttributeName = "MajorVersion")]
		public string MajorVersion { get; set; }
		[XmlAttribute(AttributeName = "MinorVersion")]
		public string MinorVersion { get; set; }
		[XmlAttribute(AttributeName = "MajorBuildNumber")]
		public string MajorBuildNumber { get; set; }
		[XmlAttribute(AttributeName = "MinorBuildNumber")]
		public string MinorBuildNumber { get; set; }
		[XmlAttribute(AttributeName = "Version")]
		public string Version { get; set; }
		[XmlAttribute(AttributeName = "h", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string H { get; set; }
		[XmlAttribute(AttributeName = "xmlns")]
		public string Xmlns { get; set; }
		[XmlAttribute(AttributeName = "xsi", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string Xsi { get; set; }
		[XmlAttribute(AttributeName = "xsd", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string Xsd { get; set; }
	}

	[XmlRoot(ElementName = "Header", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
	public class Header
	{
		[XmlElement(ElementName = "ServerVersionInfo", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
		public ServerVersionInfo ServerVersionInfo { get; set; }
	}

	[XmlRoot(ElementName = "ItemId", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
	public class ItemId
	{
		[XmlAttribute(AttributeName = "Id")]
		public string Id { get; set; }
		[XmlAttribute(AttributeName = "ChangeKey")]
		public string ChangeKey { get; set; }
	}

	[XmlRoot(ElementName = "CalendarItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
	public class CalendarItem
	{
		[XmlElement(ElementName = "ItemId", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
		public ItemId ItemId { get; set; }
	}

	[XmlRoot(ElementName = "Items", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
	public class Items
	{
		[XmlElement(ElementName = "CalendarItem", Namespace = "http://schemas.microsoft.com/exchange/services/2006/types")]
		public CalendarItem CalendarItem { get; set; }
	}

	[XmlRoot(ElementName = "CreateItemResponseMessage", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
	public class CreateItemResponseMessage
	{
		[XmlElement(ElementName = "ResponseCode", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
		public string ResponseCode { get; set; }
		[XmlElement(ElementName = "Items", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
		public Items Items { get; set; }
		[XmlAttribute(AttributeName = "ResponseClass")]
		public string ResponseClass { get; set; }
	}

	[XmlRoot(ElementName = "ResponseMessages", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
	public class ResponseMessages
	{
		[XmlElement(ElementName = "CreateItemResponseMessage", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
		public CreateItemResponseMessage CreateItemResponseMessage { get; set; }
	}

	[XmlRoot(ElementName = "CreateItemResponse", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
	public class CreateItemResponse
	{
		[XmlElement(ElementName = "ResponseMessages", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
		public ResponseMessages ResponseMessages { get; set; }
		[XmlAttribute(AttributeName = "m", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string M { get; set; }
		[XmlAttribute(AttributeName = "t", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string T { get; set; }
	}

	[XmlRoot(ElementName = "Body", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
	public class Body
	{
		[XmlElement(ElementName = "CreateItemResponse", Namespace = "http://schemas.microsoft.com/exchange/services/2006/messages")]
		public CreateItemResponse CreateItemResponse { get; set; }
		[XmlAttribute(AttributeName = "xsi", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string Xsi { get; set; }
		[XmlAttribute(AttributeName = "xsd", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string Xsd { get; set; }
	}

	[XmlRoot(ElementName = "Envelope", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
	public class EwsCreateItemResponse
	{
		[XmlElement(ElementName = "Header", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
		public Header Header { get; set; }
		[XmlElement(ElementName = "Body", Namespace = "http://schemas.xmlsoap.org/soap/envelope/")]
		public Body Body { get; set; }
		[XmlAttribute(AttributeName = "s", Namespace = "http://www.w3.org/2000/xmlns/")]
		public string S { get; set; }
	}
}
