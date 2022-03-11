using System;
namespace CTCA.Telehealth.Application.Common.Exceptions
{
    public class EmailNotFoundException : ApplicationException
    {
        public EmailNotFoundException(string message)
             : base(System.Net.HttpStatusCode.NotFound, message)
        {
        }

        public EmailNotFoundException(string message, Exception innerException)
            : base(System.Net.HttpStatusCode.NotFound, message, innerException)
        {
        }
    }
}
