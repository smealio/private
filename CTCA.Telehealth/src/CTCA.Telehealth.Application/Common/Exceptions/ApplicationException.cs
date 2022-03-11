using System;
using System.Net;

namespace CTCA.Telehealth.Application.Common.Exceptions
{
    public class ApplicationException : Exception
    {
        private readonly HttpStatusCode statusCode;

        public ApplicationException(HttpStatusCode statusCode, string message, Exception ex)
            : base(message, ex)
        {
            this.statusCode = statusCode;
        }

        public ApplicationException(HttpStatusCode statusCode, string message)
            : base(message)
        {
            this.statusCode = statusCode;
        }

        public ApplicationException(HttpStatusCode statusCode)
        {
            this.statusCode = statusCode;
        }

        public HttpStatusCode StatusCode
        {
            get { return this.statusCode; }
        }
    }
}
