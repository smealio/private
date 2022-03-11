using System;
using System.Net;

namespace CTCA.Telehealth.Application.Common.Exceptions
{
    public class EntityNotFoundException : ApplicationException
    {
      
        public EntityNotFoundException(HttpStatusCode statusCode, string message) : base(statusCode, message) { }
    }
}
