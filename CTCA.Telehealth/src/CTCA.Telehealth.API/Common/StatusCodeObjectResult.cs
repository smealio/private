using System;
using Microsoft.AspNetCore.Mvc;

namespace CTCA.Telehealth.API
{
    public class StatusCodeObjectResult : ObjectResult
    {
        public StatusCodeObjectResult(int statusCode, object value) : base(value) => StatusCode = statusCode;
                
    }
}
