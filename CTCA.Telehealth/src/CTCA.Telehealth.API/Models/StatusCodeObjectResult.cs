using System;
using Microsoft.AspNetCore.Mvc;

namespace CTCA.Telehealth.API.Models
{
    public class StatusCodeObjectResult : ObjectResult
    {
        public StatusCodeObjectResult(int statusCode, object value) : base(value) => StatusCode = statusCode;

    }
}
